const API = (window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1" || window.location.protocol === "file:")
    ? "http://localhost:8081/api"
    : "https://paimana-backend-8o7u.onrender.com/api";

let projectsData = [];
let riskChart = null;
let currentProjectCode = null;

// =====================================================
// AUTO RESTORE SESSION ON PAGE LOAD
// =====================================================
window.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    const name = localStorage.getItem("name");
    const role = localStorage.getItem("role");

    if (token && name) {
        document.getElementById("login").classList.add("hide");
        document.getElementById("app").classList.remove("hide");
        document.getElementById("who").textContent = `${name} · ${role || "USER"}`;
        load();
    }
});

// =====================================================
// LOGIN
// =====================================================
async function loginUser() {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const msg = document.getElementById("msg");

    if (!email || !password) {
        msg.textContent = "Please enter email and password.";
        return;
    }

    msg.textContent = "Signing in...";

    try {
        const response = await fetch(API + "/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || "Invalid email or password");
        }

        // Save session
        localStorage.setItem("token", data.token);
        localStorage.setItem("name", data.name);
        localStorage.setItem("email", data.email);
        localStorage.setItem("role", data.role);

        // Switch to Dashboard
        document.getElementById("login").classList.add("hide");
        document.getElementById("app").classList.remove("hide");
        document.getElementById("who").textContent = `${data.name} · ${data.role}`;
        msg.textContent = "";

        // Load projects & risks
        await load();

    } catch (error) {
        console.error("LOGIN ERROR:", error);
        msg.textContent = "Login error: " + error.message;
    }
}

// =====================================================
// LOAD PROJECTS & ANALYZE RISKS
// =====================================================
async function load() {
    console.log("===== LOADING PROJECTS & RISKS =====");
    const refreshBtn = document.getElementById("refreshBtn");
    if (refreshBtn) {
        refreshBtn.disabled = true;
        refreshBtn.textContent = "Refreshing...";
    }

    try {
        const token = localStorage.getItem("token");
        const headers = token ? { "Authorization": `Bearer ${token}` } : {};

        const response = await fetch(API + "/projects", { headers });

        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        const projects = await response.json();
        projectsData = projects;

        // Total projects count
        document.getElementById("count").textContent = projects.length;

        // Clear table and show loading state
        const rows = document.getElementById("rows");
        rows.innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; color: #6b7280; padding: 20px;">
                    Loading and analyzing project risks...
                </td>
            </tr>
        `;

        // Fetch risk analysis for all projects in parallel
        let highCount = 0;
        let medCount = 0;
        let lowCount = 0;

        await Promise.all(projects.map(async (project) => {
            try {
                const riskRes = await fetch(
                    API + "/projects/" + encodeURIComponent(project.projectCode) + "/risk",
                    {
                        method: "POST",
                        headers: {
                            ...headers,
                            "Content-Type": "application/json"
                        }
                    }
                );

                if (riskRes.ok) {
                    const riskData = await riskRes.json();
                    project.risk = riskData;
                    project.overallRisk = riskData.overallRisk;
                }
            } catch (err) {
                console.warn(`Failed to fetch risk for ${project.projectCode}:`, err);
            }

            const riskTier = (project.overallRisk || "").toUpperCase();
            if (riskTier === "HIGH") highCount++;
            else if (riskTier === "MEDIUM") medCount++;
            else if (riskTier === "LOW") lowCount++;
        }));

        // Update statistics cards
        document.getElementById("high").textContent = highCount;
        document.getElementById("med").textContent = medCount;
        document.getElementById("low").textContent = lowCount;

        // Render project rows
        rows.innerHTML = "";

        if (projects.length === 0) {
            rows.innerHTML = `
                <tr>
                    <td colspan="9" style="text-align: center; color: #6b7280; padding: 20px;">
                        No projects found.
                    </td>
                </tr>
            `;
            return;
        }

        projects.forEach(project => {
            const riskTier = (project.overallRisk || "").toUpperCase();
            let badgeClass = "badge-none";
            if (riskTier === "HIGH") badgeClass = "badge-high";
            else if (riskTier === "MEDIUM") badgeClass = "badge-med";
            else if (riskTier === "LOW") badgeClass = "badge-low";

            const row = document.createElement("tr");
            row.id = `row-${project.projectCode}`;
            row.innerHTML = `
                <td><strong>${project.projectCode}</strong></td>
                <td>${project.projectName}</td>
                <td>${project.ministry || "-"}</td>
                <td>${project.state || "-"}</td>
                <td>${project.originalEndDate || "-"}</td>
                <td>${project.revisedEndDate || "-"}</td>
                <td>${project.physicalProgress != null ? project.physicalProgress + "%" : "-"}</td>
                <td>
                    <span class="badge ${badgeClass}" id="badge-${project.projectCode}">
                        ${project.overallRisk || "Not analyzed"}
                    </span>
                </td>
                <td>
                    <button onclick="investigate('${project.projectCode}')">
                        Analyze
                    </button>
                </td>
            `;
            rows.appendChild(row);
        });

        // Refresh the selected project investigation & chart (or first project)
        const targetCode = currentProjectCode && projects.some(p => p.projectCode === currentProjectCode)
            ? currentProjectCode
            : (projects.length > 0 ? projects[0].projectCode : null);

        if (targetCode) {
            await investigate(targetCode);
        }

        console.log("===== PROJECTS & RISKS LOADED SUCCESSFULLY =====");

    } catch (error) {
        console.error("PROJECT LOAD ERROR:", error);
        document.getElementById("count").textContent = "Error";
        document.getElementById("rows").innerHTML = `
            <tr>
                <td colspan="9" style="color: #d32f2f; text-align: center; padding: 20px;">
                    Error loading projects: ${error.message}
                </td>
            </tr>
        `;
    } finally {
        if (refreshBtn) {
            refreshBtn.disabled = false;
            refreshBtn.textContent = "Refresh";
        }
    }
}

// =====================================================
// RISK INVESTIGATION
// =====================================================
async function investigate(code) {
    currentProjectCode = code;
    const riskBox = document.getElementById("risk");
    riskBox.innerHTML = `<p>Analyzing <strong>${code}</strong>...</p>`;

    // Highlight row in table
    document.querySelectorAll("#rows tr").forEach(tr => tr.style.backgroundColor = "");
    const activeRow = document.getElementById(`row-${code}`);
    if (activeRow) {
        activeRow.style.backgroundColor = "#e0f2fe";
    }

    try {
        const token = localStorage.getItem("token");
        const headers = token ? { "Authorization": `Bearer ${token}` } : {};

        const response = await fetch(
            API + "/projects/" + encodeURIComponent(code) + "/risk",
            {
                method: "POST",
                headers
            }
        );

        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        const data = await response.json();

        // Update badge in table if changed
        const badge = document.getElementById(`badge-${code}`);
        if (badge) {
            const riskTier = (data.overallRisk || "").toUpperCase();
            let badgeClass = "badge-none";
            if (riskTier === "HIGH") badgeClass = "badge-high";
            else if (riskTier === "MEDIUM") badgeClass = "badge-med";
            else if (riskTier === "LOW") badgeClass = "badge-low";

            badge.className = `badge ${badgeClass}`;
            badge.textContent = data.overallRisk || "Not analyzed";
        }

        // Build evidence HTML
        let evidenceHtml = "";
        if (data.evidence && data.evidence.length > 0) {
            evidenceHtml = `
                <div style="margin-top: 15px;">
                    <strong>Supporting Evidence:</strong>
                    <ul class="risk-evidence-list">
                        ${data.evidence.map(e => `
                            <li class="risk-evidence-item">
                                <strong>[${e.factorType || "FACTOR"}]:</strong> ${e.description} 
                                <em>(Weight: ${e.evidenceWeight != null ? (e.evidenceWeight * 100).toFixed(0) + "%" : "-"})</em>
                            </li>
                        `).join("")}
                    </ul>
                </div>
            `;
        }

        const proj = projectsData.find(p => p.projectCode === code) || {};
        const dateInfo = (proj.originalEndDate || proj.revisedEndDate) ? `
            <div style="font-size: 13px; color: #4b5563; margin-bottom: 8px;">
                <strong>Target Date:</strong> ${proj.originalEndDate || "-"} &nbsp;|&nbsp; 
                <strong>Revised Date:</strong> ${proj.revisedEndDate || "-"}
            </div>
        ` : "";

        // Render card
        riskBox.innerHTML = `
            <h4>${code} - Risk Investigation</h4>
            ${dateInfo}
            <div style="display: flex; gap: 15px; margin: 10px 0; flex-wrap: wrap;">
                <div><strong>Overall Risk:</strong> <span class="badge ${getBadgeClass(data.overallRisk)}">${data.overallRisk || "-"}</span></div>
                <div><strong>Cost Risk:</strong> <span class="badge ${getBadgeClass(data.costRisk)}">${data.costRisk || "-"}</span></div>
                <div><strong>Delay Risk:</strong> <span class="badge ${getBadgeClass(data.delayRisk)}">${data.delayRisk || "-"}</span></div>
            </div>
            <p style="font-size: 13px; color: #6b7280; margin: 5px 0;">
                <strong>Engine:</strong> ${data.modelName || "Ensemble ML"} (v${data.modelVersion || "1.0"})
            </p>
            ${evidenceHtml}
        `;

        // Render Chart if Chart.js is loaded
        renderRiskChart(data);

    } catch (error) {
        console.error("INVESTIGATION ERROR:", error);
        riskBox.innerHTML = `<p style="color: #d32f2f;">Risk error: ${error.message}</p>`;
    }
}

function getBadgeClass(risk) {
    const r = (risk || "").toUpperCase();
    if (r === "HIGH") return "badge-high";
    if (r === "MEDIUM") return "badge-med";
    if (r === "LOW") return "badge-low";
    return "badge-none";
}

function renderRiskChart(riskData) {
    if (typeof Chart === "undefined") return;
    const canvas = document.getElementById("chart");
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (riskChart) {
        riskChart.destroy();
        riskChart = null;
    }

    const tierToScore = (tier) => {
        const t = (tier || "").toUpperCase();
        if (t === "HIGH") return 85;
        if (t === "MEDIUM") return 50;
        if (t === "LOW") return 20;
        return 0;
    };

    const costScore = tierToScore(riskData.costRisk);
    const delayScore = tierToScore(riskData.delayRisk);
    const overallScore = tierToScore(riskData.overallRisk);
    const codeLabel = riskData.projectCode || currentProjectCode || "Project";

    riskChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: ["Cost Risk", "Delay Risk", "Overall Risk"],
            datasets: [{
                label: `${codeLabel} Risk Index (0-100)`,
                data: [costScore, delayScore, overallScore],
                backgroundColor: [
                    costScore >= 70 ? "#ef4444" : costScore >= 40 ? "#f59e0b" : "#10b981",
                    delayScore >= 70 ? "#ef4444" : delayScore >= 40 ? "#f59e0b" : "#10b981",
                    overallScore >= 70 ? "#ef4444" : overallScore >= 40 ? "#f59e0b" : "#10b981"
                ],
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            animation: {
                duration: 600,
                easing: "easeOutQuart"
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: (val) => val + "%"
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: "top",
                    labels: {
                        boxWidth: 16,
                        font: {
                            weight: "bold"
                        }
                    }
                }
            }
        }
    });
}

// =====================================================
// AI ASSISTANT
// =====================================================
async function ask() {
    const q = document.getElementById("q");
    const answer = document.getElementById("answer");
    const message = q.value.trim();

    if (!message) {
        answer.textContent = "Please enter a question.";
        return;
    }

    answer.textContent = "Thinking...";

    try {
        const token = localStorage.getItem("token");
        const headers = {
            "Content-Type": "application/json",
            ...(token ? { "Authorization": `Bearer ${token}` } : {})
        };

        const response = await fetch(API + "/ai/chat", {
            method: "POST",
            headers,
            body: JSON.stringify({ message })
        });

        if (!response.ok) {
            throw new Error("HTTP " + response.status);
        }

        const data = await response.json();
        answer.textContent = data.answer || "No answer received.";

    } catch (error) {
        answer.textContent = "AI error: " + error.message;
    }
}

// =====================================================
// LOGOUT
// =====================================================
function logout() {
    localStorage.clear();
    document.getElementById("app").classList.add("hide");
    document.getElementById("login").classList.remove("hide");
    document.getElementById("email").value = "admin@example.com";
    document.getElementById("password").value = "admin123";
    document.getElementById("msg").textContent = "";
}
