# PAIMANA Insight — SIH 26103

Complete prototype based on the uploaded SIH 26103 PAIMANA Insight specification.

## Live Demo

Frontend: https://paimana-frontend-9c9r.onrender.com

## Stack

- Frontend: HTML5/CSS3/JavaScript + Chart.js
- Backend: Java 21 + Spring Boot 3.5.16 + Spring Data JPA/Hibernate + Spring Security/JWT
- Database: PostgreSQL
- ML: Python 3.11 + FastAPI + pandas + scikit-learn
- AI: Spring AI + OpenAI-compatible model + tool calling
- Reporting: PDFBox
- No handwritten SQL queries; persistence uses JPA repository methods.

The specification calls for project data ingestion, predictive ML, evidence, historical comparison, what-if analysis, Spring AI tools, dashboarding and PDF reporting. This package provides a runnable prototype with demo data. The uploaded specification does not include the official PAIMANA dataset or a production trained model, so the ML service uses a small reproducible demo training set.

## Run

1. Start PostgreSQL and ML:

   `docker compose up -d postgres ml`

2. Or run the ML service manually:

   `cd ml-service`

   `python -m venv .venv`

   Windows:

   `.venv\Scripts\activate`

   `pip install -r requirements.txt`

   `uvicorn app:app --reload --port 8001`

3. Start backend:

   `cd backend`

   `mvn spring-boot:run`

4. Start frontend:

   `cd frontend`

   `python -m http.server 5500`

   Open:

   http://localhost:5500

## Services

Backend:

http://localhost:8080

ML:

http://localhost:8001

Frontend:

https://paimana-frontend-9c9r.onrender.com

## Demo Users

- admin@example.com / admin123
- officer@example.com / officer123
- monitor@example.com / monitor123

## AI Configuration

Set `OPENAI_API_KEY` to enable the Spring AI assistant.

The backend can still start without it; the AI endpoint reports that configuration is missing.

## Main Endpoints

POST `/api/auth/login`

GET `/api/projects`

GET `/api/projects/{code}`

POST `/api/projects`

PUT `/api/projects/{id}`

DELETE `/api/projects/{id}`

POST `/api/projects/{code}/risk`

GET `/api/projects/{code}/comparisons`

POST `/api/projects/{code}/what-if`

POST `/api/ai/chat`

GET `/api/reports/projects/{code}`
