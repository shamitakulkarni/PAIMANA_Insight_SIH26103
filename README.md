# PAIMANA Insight — SIH 26103

## ML-Powered Infrastructure Project Risk Analysis & Decision Support

PAIMANA Insight is a web-based project monitoring and decision-support prototype designed for infrastructure projects.

The system goes beyond basic project monitoring by combining:

- Project data monitoring
- Cost and schedule analysis
- Machine Learning based risk prediction
- Explainable risk evidence
- Historical project comparison
- What-if scenario analysis
- AI-assisted project insights
- Interactive dashboard
- PDF-based project reporting

The main objective is to help project authorities identify potential **cost overruns and delays at an early stage** and understand the factors contributing to project risk.

---

## Live Demo

### Frontend

https://paimana-frontend-9c9r.onrender.com

The frontend communicates with the deployed Spring Boot backend through REST APIs.

---

# Key Features

### 1. Project Monitoring

The system stores and displays important project information such as:

- Project name
- Project code
- Ministry
- Sector
- State
- Original project cost
- Revised project cost
- Expenditure
- Original completion date
- Revised completion date
- Physical progress
- Project status

---

### 2. Risk Prediction

The system uses a Python-based Machine Learning service to analyse project information and estimate project risks.

The current prototype focuses mainly on:

- Cost overrun risk
- Delay risk

The ML service is implemented using:

- Python
- FastAPI
- pandas
- scikit-learn

---

### 3. Explainable Risk Analysis

Instead of showing only a risk result, PAIMANA Insight provides supporting evidence and project factors that help explain why a project may be considered risky.

This makes the prediction easier for project officials to understand.

---

### 4. Historical Project Comparison

The system compares the selected project with historical or similar projects.

This helps identify:

- Similar project patterns
- Previous cost behaviour
- Previous schedule behaviour
- Relevant historical evidence

---

### 5. What-If Analysis

The user can test hypothetical changes in project conditions.

For example:

- What if project cost increases?
- What if physical progress decreases?
- What if the completion date changes?

The system analyses the changed scenario and provides updated risk insights.

---

### 6. AI Assistant

The system includes an AI-assisted interface using Spring AI and an OpenAI-compatible model.

The AI assistant can help users understand project information and risk results using natural-language questions.

Example questions:

- "Why is this project at risk?"
- "What are the major risk factors?"
- "Compare this project with similar projects."
- "What happens if the project cost increases?"

---

### 7. Interactive Dashboard

The dashboard provides a centralized view of project performance and risk information.

It can display:

- Project overview
- Cost information
- Expenditure
- Physical progress
- Project status
- Risk results
- Historical comparisons
- What-if results
- AI-generated insights
- Charts and visual indicators

---

# Technology Stack

| Layer | Technology |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Visualization | Chart.js |
| Backend | Java 21, Spring Boot |
| API | REST API |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| ML Service | Python, FastAPI |
| ML Libraries | pandas, scikit-learn |
| AI | Spring AI + OpenAI-compatible model |
| Reporting | PDFBox |
| Deployment | Render |
| Version Control | Git + GitHub |

---

# System Architecture

```text
                    USER / OFFICER
                         |
                         v
                +-------------------+
                |     FRONTEND      |
                | HTML/CSS/JS       |
                | Chart.js           |
                +---------+---------+
                          |
                          | REST API
                          v
                +-------------------+
                |   SPRING BOOT     |
                |     BACKEND       |
                | REST API + JWT    |
                +----+---------+----+
                     |         |
                     |         |
                     v         v
              +----------+  +-------------+
              |PostgreSQL|  | ML SERVICE  |
              | Database |  | FastAPI     |
              +----------+  | scikit-learn|
                            +------+------+
                                   |
                                   v
                           +---------------+
                           | Risk Analysis |
                           +-------+-------+
                                   |
                    +--------------+--------------+
                    |              |              |
                    v              v              v
              Risk Evidence   Historical      What-If
                              Comparison      Analysis
                    |              |              |
                    +--------------+--------------+
                                   |
                                   v
                           +---------------+
                           | AI Assistant  |
                           |  Spring AI    |
                           +-------+-------+
                                   |
                                   v
                           +---------------+
                           |   Dashboard   |
                           | Decision      |
                           | Support       |
                           +---------------+
