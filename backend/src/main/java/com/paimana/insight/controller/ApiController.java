package com.paimana.insight.controller;

import com.paimana.insight.dto.DTOs.Chat;
import com.paimana.insight.dto.DTOs.Login;
import com.paimana.insight.dto.DTOs.LoginOut;
import com.paimana.insight.dto.DTOs.Signup;
import com.paimana.insight.model.HistoricalComparison;
import com.paimana.insight.model.Project;
import com.paimana.insight.dto.DTOs.Risk;
import com.paimana.insight.repository.HistoricalComparisonRepository;
import com.paimana.insight.repository.ProjectRepository;
import com.paimana.insight.repository.UserRepository;
import com.paimana.insight.service.AiService;
import com.paimana.insight.service.AuthService;
import com.paimana.insight.service.RiskService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final UserRepository users;
    private final ProjectRepository projects;
    private final AuthService auth;
    private final RiskService risk;
    private final AiService ai;
    private final HistoricalComparisonRepository comps;

    public ApiController(
            UserRepository u,
            ProjectRepository p,
            AuthService a,
            RiskService r,
            AiService x,
            HistoricalComparisonRepository c
    ) {
        users = u;
        projects = p;
        auth = a;
        risk = r;
        ai = x;
        comps = c;
    }

    // =========================
    // AUTHENTICATION
    // =========================

    @PostMapping("/auth/login")
    public LoginOut login(@RequestBody Login x) {
        return auth.login(x);
    }

    @PostMapping("/auth/signup")
    public LoginOut signup(@RequestBody Signup x) {
        return auth.signup(x);
    }

    // =========================
    // PROJECTS
    // =========================

    @GetMapping("/projects")
    public List<Project> projects() {
        return projects.findAll();
    }

    @GetMapping("/projects/{code}")
    public Project project(@PathVariable String code) {
        return projects.findByProjectCode(code)
                .orElseThrow();
    }

    @PostMapping("/projects")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public Project add(@RequestBody Project p) {
        return projects.save(p);
    }

    @PutMapping("/projects/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public Project update(
            @PathVariable Long id,
            @RequestBody Project p
    ) {
        p.setProjectCode(p.getProjectCode());
        return projects.save(p);
    }

    @DeleteMapping("/projects/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        projects.deleteById(id);
    }

    // =========================
    // RISK ANALYSIS
    // =========================

    @PostMapping("/projects/{code}/risk")
    public Risk risk(@PathVariable String code) {
        return risk.analyze(code);
    }

    @GetMapping("/projects/{code}/comparisons")
    public List<HistoricalComparison> comparisons(
            @PathVariable String code
    ) {
        return risk.comparisons(code);
    }

    // =========================
    // AI ASSISTANT
    // =========================

    @PostMapping("/ai/chat")
    public Map<String, String> ai(
            @RequestBody Chat x
    ) {

        try {

            if (x == null ||
                    x.message() == null ||
                    x.message().isBlank()) {

                return Map.of(
                        "answer",
                        "Please enter a question."
                );
            }

            return Map.of(
                    "answer",
                    ai.chat(x.message())
            );

        } catch (Exception e) {

            return Map.of(
                    "answer",
                    "AI Assistant error: " +
                            e.getMessage()
            );
        }
    }
}