package com.paimana.insight.service;

import com.paimana.insight.model.Project;
import com.paimana.insight.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
public class AiService {

    private final ProjectRepository projects;
    private final MlClient mlClient;

    public AiService(
            ProjectRepository projects,
            MlClient mlClient
    ) {
        this.projects = projects;
        this.mlClient = mlClient;
    }


    // =====================================================
    // MAIN CHAT METHOD
    // =====================================================

    public String chat(String question) {

        if (question == null || question.isBlank()) {

            return "Please enter a question about a PAIMANA project.";
        }

        String q =
                question
                        .toLowerCase(Locale.ROOT)
                        .trim();


        // ---------------------------------------------
        // Find project code
        // ---------------------------------------------

        String projectCode =
                findProjectCode(q);


        // ---------------------------------------------
        // If project code found
        // ---------------------------------------------

        if (projectCode != null) {

            Project project =
                    projects
                            .findByProjectCode(projectCode)
                            .orElse(null);

            if (project == null) {

                return "Project "
                        + projectCode
                        + " was not found in the database.";
            }

            return generateProjectAnswer(project);
        }


        // ---------------------------------------------
        // List all projects
        // ---------------------------------------------

        if (q.contains("list projects")
                || q.contains("all projects")
                || q.equals("projects")
                || q.contains("project list")) {

            return generateProjectList();
        }


        // ---------------------------------------------
        // Help
        // ---------------------------------------------

        if (q.contains("help")
                || q.contains("what can you ask")
                || q.contains("what can i ask")
                || q.contains("what can you do")) {

            return generateHelp();
        }


        // ---------------------------------------------
        // Default response
        // ---------------------------------------------

        return """
                PAIMANA Insight AI Assistant

                I can provide information about projects
                stored in the PAIMANA database.

                Try:

                • Tell me about PAI-001
                • Give complete details of PAI-002
                • Explain PAI-003
                • What is the risk of PAI-001?
                • What is the progress of PAI-002?
                • What is the cost of PAI-003?
                • List all projects
                """;
    }


    // =====================================================
    // FIND PROJECT CODE
    // =====================================================

    private String findProjectCode(
            String question
    ) {

        String q =
                question.toUpperCase(Locale.ROOT);

        if (q.contains("PAI-001")) {

            return "PAI-001";
        }

        if (q.contains("PAI-002")) {

            return "PAI-002";
        }

        if (q.contains("PAI-003")) {

            return "PAI-003";
        }

        return null;
    }


    // =====================================================
    // GENERATE COMPLETE PROJECT ANSWER
    // =====================================================

    private String generateProjectAnswer(
            Project p
    ) {

        double originalCost =
                number(p.getOriginalCost());

        double revisedCost =
                number(p.getRevisedCost());

        double expenditure =
                number(p.getExpenditure());

        double progress =
                number(p.getPhysicalProgress());


        // ---------------------------------------------
        // Cost increase
        // ---------------------------------------------

        double costIncrease = 0;

        if (originalCost > 0) {

            costIncrease =
                    ((revisedCost - originalCost)
                            / originalCost)
                            * 100;
        }


        // ---------------------------------------------
        // Expenditure percentage
        // ---------------------------------------------

        double expenditurePercentage = 0;

        if (originalCost > 0) {

            expenditurePercentage =
                    (expenditure / originalCost)
                            * 100;
        }


        // ---------------------------------------------
        // Difference between expenditure and progress
        // ---------------------------------------------

        double progressGap =
                expenditurePercentage - progress;


        // ---------------------------------------------
        // Schedule change
        // ---------------------------------------------

        long delayMonths =
                calculateDelayMonths(
                        p.getOriginalEndDate(),
                        p.getRevisedEndDate()
                );


        // ---------------------------------------------
        // ML prediction
        // ---------------------------------------------

        MlClient.Prediction prediction =
                mlClient.predict(p);


        // ---------------------------------------------
        // Build response
        // ---------------------------------------------

        StringBuilder answer =
                new StringBuilder();


        answer.append(
                "PAIMANA INSIGHT - PROJECT ANALYSIS\n"
        );

        answer.append(
                "====================================\n\n"
        );


        // =============================================
        // PROJECT INFORMATION
        // =============================================

        answer.append(
                "PROJECT INFORMATION\n"
        );

        answer.append(
                "-------------------\n"
        );

        answer.append(
                "Project Code: "
        );

        answer.append(
                p.getProjectCode()
        );

        answer.append("\n");


        answer.append(
                "Project Name: "
        );

        answer.append(
                p.getProjectName()
        );

        answer.append("\n");


        answer.append(
                "Ministry: "
        );

        answer.append(
                p.getMinistry()
        );

        answer.append("\n");


        answer.append(
                "Sector: "
        );

        answer.append(
                p.getSector()
        );

        answer.append("\n");


        answer.append(
                "State: "
        );

        answer.append(
                p.getState()
        );

        answer.append("\n");


        answer.append(
                "Data Source: "
        );

        answer.append(
                p.getDataSource()
        );

        answer.append("\n");


        answer.append(
                "Status: "
        );

        answer.append(
                p.getStatus()
        );

        answer.append("\n\n");


        // =============================================
        // FINANCIAL INFORMATION
        // =============================================

        answer.append(
                "FINANCIAL INFORMATION\n"
        );

        answer.append(
                "---------------------\n"
        );


        answer.append(
                "Original Cost: ₹"
        );

        answer.append(
                format(originalCost)
        );

        answer.append(
                " crore\n"
        );


        answer.append(
                "Revised Cost: ₹"
        );

        answer.append(
                format(revisedCost)
        );

        answer.append(
                " crore\n"
        );


        answer.append(
                "Expenditure: ₹"
        );

        answer.append(
                format(expenditure)
        );

        answer.append(
                " crore\n"
        );


        answer.append(
                "Cost Increase: "
        );

        answer.append(
                format(costIncrease)
        );

        answer.append(
                "%\n"
        );


        answer.append(
                "Expenditure against Original Cost: "
        );

        answer.append(
                format(expenditurePercentage)
        );

        answer.append(
                "%\n\n"
        );


        // =============================================
        // PROGRESS INFORMATION
        // =============================================

        answer.append(
                "PROGRESS INFORMATION\n"
        );

        answer.append(
                "--------------------\n"
        );


        answer.append(
                "Physical Progress: "
        );

        answer.append(
                format(progress)
        );

        answer.append(
                "%\n"
        );


        answer.append(
                "Expenditure - Progress Gap: "
        );

        answer.append(
                format(progressGap)
        );

        answer.append(
                " percentage points\n\n"
        );


        // =============================================
        // SCHEDULE
        // =============================================

        answer.append(
                "SCHEDULE INFORMATION\n"
        );

        answer.append(
                "--------------------\n"
        );


        answer.append(
                "Original End Date: "
        );

        answer.append(
                p.getOriginalEndDate()
        );

        answer.append("\n");


        answer.append(
                "Revised End Date: "
        );

        answer.append(
                p.getRevisedEndDate()
        );

        answer.append("\n");


        answer.append(
                "Schedule Change: "
        );

        answer.append(
                delayMonths
        );

        answer.append(
                " months approximately\n\n"
        );


        // =============================================
        // MACHINE LEARNING RISK
        // =============================================

        answer.append(
                "ML RISK ASSESSMENT\n"
        );

        answer.append(
                "------------------\n"
        );


        answer.append(
                "Cost Risk: "
        );

        answer.append(
                prediction.costRisk()
        );

        answer.append("\n");


        answer.append(
                "Delay Risk: "
        );

        answer.append(
                prediction.delayRisk()
        );

        answer.append("\n");


        answer.append(
                "Overall Risk: "
        );

        answer.append(
                prediction.overallRisk()
        );

        answer.append("\n");


        answer.append(
                "Model: "
        );

        answer.append(
                prediction.modelName()
        );

        answer.append("\n");


        answer.append(
                "Model Version: "
        );

        answer.append(
                prediction.modelVersion()
        );

        answer.append("\n\n");


        // =============================================
        // OBSERVATIONS
        // =============================================

        answer.append(
                "KEY OBSERVATIONS\n"
        );

        answer.append(
                "----------------\n"
        );


        if (costIncrease > 10) {

            answer.append(
                    "• Revised cost is more than 10% "
                            + "above the original cost.\n"
            );

        } else {

            answer.append(
                    "• Revised cost is within 10% "
                            + "of the original cost.\n"
            );
        }


        if (progress < 50) {

            answer.append(
                    "• Physical progress is below 50%.\n"
            );

        } else {

            answer.append(
                    "• Physical progress is 50% or above.\n"
            );
        }


        if (progressGap > 10) {

            answer.append(
                    "• Expenditure percentage is more "
                            + "than 10 percentage points above "
                            + "physical progress.\n"
            );

        } else {

            answer.append(
                    "• Expenditure and physical progress "
                            + "are relatively close.\n"
            );
        }


        if (delayMonths > 0) {

            answer.append(
                    "• The revised completion date is "
                            + "later than the original date.\n"
            );

        } else {

            answer.append(
                    "• No schedule extension is recorded.\n"
            );
        }


        answer.append("\n");


        answer.append(
                "This response is generated from the "
                        + "project information stored in "
                        + "PAIMANA Insight and the configured "
                        + "ML risk service."
        );


        return answer.toString();
    }


    // =====================================================
    // PROJECT LIST
    // =====================================================

    private String generateProjectList() {

        List<Project> list =
                projects.findAll();


        if (list.isEmpty()) {

            return "No projects are available.";
        }


        StringBuilder answer =
                new StringBuilder();


        answer.append(
                "PAIMANA INSIGHT PROJECTS\n"
        );

        answer.append(
                "========================\n\n"
        );


        for (Project p : list) {

            answer.append(
                    p.getProjectCode()
            );

            answer.append(
                    " - "
            );

            answer.append(
                    p.getProjectName()
            );

            answer.append("\n");


            answer.append(
                    "Ministry: "
            );

            answer.append(
                    p.getMinistry()
            );

            answer.append("\n");


            answer.append(
                    "Sector: "
            );

            answer.append(
                    p.getSector()
            );

            answer.append("\n");


            answer.append(
                    "State: "
            );

            answer.append(
                    p.getState()
            );

            answer.append("\n");


            answer.append(
                    "Progress: "
            );

            answer.append(
                    format(
                            number(
                                    p.getPhysicalProgress()
                            )
                    )
            );

            answer.append(
                    "%\n"
            );


            answer.append(
                    "Status: "
            );

            answer.append(
                    p.getStatus()
            );

            answer.append(
                    "\n\n"
            );
        }


        return answer.toString();
    }


    // =====================================================
    // HELP
    // =====================================================

    private String generateHelp() {

        return """
                PAIMANA INSIGHT AI ASSISTANT

                You can ask me about PAIMANA projects.

                Examples:

                • Tell me about PAI-001
                • Give complete details of PAI-002
                • Explain PAI-003
                • What is the cost of PAI-001?
                • What is the progress of PAI-002?
                • What is the risk of PAI-001?
                • Explain the schedule of PAI-003
                • List all projects

                Project information includes:

                • Project name
                • Ministry
                • Sector
                • State
                • Data source
                • Original cost
                • Revised cost
                • Expenditure
                • Physical progress
                • Original end date
                • Revised end date
                • Project status
                • ML cost risk
                • ML delay risk
                • Overall ML risk
                • Model information
                """;
    }


    // =====================================================
    // NUMBER HELPER
    // =====================================================

    private double number(Number value) {

        if (value == null) {

            return 0.0;
        }

        return value.doubleValue();
    }


    // =====================================================
    // FORMAT HELPER
    // =====================================================

    private String format(double value) {

        return String.format(
                Locale.US,
                "%.2f",
                value
        );
    }


    // =====================================================
    // DELAY CALCULATION
    // =====================================================

    private long calculateDelayMonths(
            LocalDate originalDate,
            LocalDate revisedDate
    ) {

        if (originalDate == null
                || revisedDate == null) {

            return 0;
        }


        long days =
                ChronoUnit.DAYS.between(
                        originalDate,
                        revisedDate
                );


        return Math.round(
                days / 30.44
        );
    }
}