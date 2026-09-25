const API ="https://paimana-backend-8o7u.onrender.com/api";


// =====================================================
// LOGIN
// =====================================================

async function loginUser() {

    const email =
        document.getElementById("email").value.trim();

    const password =
        document.getElementById("password").value;

    const msg =
        document.getElementById("msg");


    if (!email || !password) {

        msg.textContent =
            "Please enter email and password.";

        return;
    }


    msg.textContent =
        "Signing in...";


    try {

        const response = await fetch(
            API + "/auth/login",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    email: email,
                    password: password
                })
            }
        );


        console.log(
            "LOGIN STATUS:",
            response.status
        );


        const data =
            await response.json();


        console.log(
            "LOGIN RESPONSE:",
            data
        );


        if (!response.ok) {

            throw new Error(
                data.message ||
                "Invalid email or password"
            );
        }


        // Save login data

        localStorage.setItem(
            "token",
            data.token
        );

        localStorage.setItem(
            "name",
            data.name
        );

        localStorage.setItem(
            "email",
            data.email
        );

        localStorage.setItem(
            "role",
            data.role
        );


        // Hide login

        document
            .getElementById("login")
            .classList.add("hide");


        // Show dashboard

        document
            .getElementById("app")
            .classList.remove("hide");


        // Show user

        document
            .getElementById("who")
            .textContent =
            data.name +
            " · " +
            data.role;


        msg.textContent = "";


        // Load projects

        await load();

    }
    catch (error) {

        console.error(
            "LOGIN ERROR:",
            error
        );


        msg.textContent =
            "Login error: " +
            error.message;
    }
}



// =====================================================
// LOAD PROJECTS
// =====================================================

async function load() {

    console.log(
        "===== LOADING PROJECTS ====="
    );


    try {

        const response =
            await fetch(
                API + "/projects"
            );


        console.log(
            "PROJECT STATUS:",
            response.status
        );


        if (!response.ok) {

            throw new Error(
                "HTTP " +
                response.status
            );
        }


        const projects =
            await response.json();


        console.log(
            "PROJECTS:",
            projects
        );


        // Total projects

        document
            .getElementById("count")
            .textContent =
            projects.length;


        // Clear table

        const rows =
            document.getElementById("rows");

        rows.innerHTML = "";


        // Display projects

        projects.forEach(
            function(project) {

                const row =
                    document.createElement("tr");


                row.innerHTML = `

                    <td>
                        ${project.projectCode}
                    </td>

                    <td>
                        ${project.projectName}
                    </td>

                    <td>
                        ${project.ministry}
                    </td>

                    <td>
                        ${project.state}
                    </td>

                    <td>
                        ${project.physicalProgress}%
                    </td>

                    <td>
                        Not analyzed
                    </td>

                    <td>

                        <button
                            onclick="
                                investigate(
                                    '${project.projectCode}'
                                )
                            "
                        >
                            Analyze
                        </button>

                    </td>

                `;


                rows.appendChild(row);

            }
        );


        console.log(
            "===== PROJECTS LOADED ====="
        );

    }
    catch (error) {

        console.error(
            "PROJECT LOAD ERROR:",
            error
        );


        document
            .getElementById("count")
            .textContent =
            "Error";


        document
            .getElementById("rows")
            .innerHTML = `

                <tr>

                    <td colspan="7">

                        Error loading projects:
                        ${error.message}

                    </td>

                </tr>

            `;
    }
}



// =====================================================
// RISK INVESTIGATION
// =====================================================

async function investigate(code) {

    const risk =
        document.getElementById("risk");


    risk.textContent =
        "Analyzing " +
        code +
        "...";


    try {

        const response =
            await fetch(
                API +
                "/projects/" +
                encodeURIComponent(code) +
                "/risk",
                {
                    method: "POST"
                }
            );


        if (!response.ok) {

            throw new Error(
                "HTTP " +
                response.status
            );
        }


        const data =
            await response.json();


        risk.innerHTML = `

            <h4>
                ${code}
            </h4>

            <p>
                <b>Cost Risk:</b>
                ${data.costRisk || "-"}
            </p>

            <p>
                <b>Delay Risk:</b>
                ${data.delayRisk || "-"}
            </p>

            <p>
                <b>Overall Risk:</b>
                ${data.overallRisk || "-"}
            </p>

        `;

    }
    catch (error) {

        risk.textContent =
            "Risk error: " +
            error.message;
    }
}



// =====================================================
// AI ASSISTANT
// =====================================================

async function ask() {

    const q =
        document.getElementById("q");

    const answer =
        document.getElementById("answer");


    const message =
        q.value.trim();


    if (!message) {

        answer.textContent =
            "Please enter a question.";

        return;
    }


    answer.textContent =
        "Thinking...";


    try {

        const response =
            await fetch(
                API + "/ai/chat",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        message: message
                    })
                }
            );


        if (!response.ok) {

            throw new Error(
                "HTTP " +
                response.status
            );
        }


        const data =
            await response.json();


        answer.textContent =
            data.answer ||
            "No answer received.";

    }
    catch (error) {

        answer.textContent =
            "AI error: " +
            error.message;
    }
}



// =====================================================
// LOGOUT
// =====================================================

function logout() {

    localStorage.clear();


    document
        .getElementById("app")
        .classList.add("hide");


    document
        .getElementById("login")
        .classList.remove("hide");


    document
        .getElementById("email")
        .value =
        "admin@example.com";


    document
        .getElementById("password")
        .value =
        "admin123";


    document
        .getElementById("msg")
        .textContent =
        "";
}
