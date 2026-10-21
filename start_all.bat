@echo off
echo ====================================================
echo Starting PAIMANA Insight Services...
echo ====================================================

REM 1. Start PostgreSQL
echo [1/4] Starting PostgreSQL Database (Port 5433)...
if exist "%~dp0local_pgdata\postmaster.pid" del /f /q "%~dp0local_pgdata\postmaster.pid"
start "PAIMANA - PostgreSQL" /min "C:\Program Files\PostgreSQL\17\bin\postgres.exe" -D "%~dp0local_pgdata"

REM 2. Start ML Service
echo [2/4] Starting FastAPI ML Engine (Port 8001)...
start "PAIMANA - ML Service" /min "%~dp0ml-service\.venv\Scripts\uvicorn.exe" app:app --host 127.0.0.1 --port 8001 --app-dir "%~dp0ml-service"

REM Wait for DB & ML Engine to initialize
ping -n 5 127.0.0.1 >nul

REM 3. Start Spring Boot Backend
echo [3/4] Starting Spring Boot Backend (Port 8081)...
start "PAIMANA - Backend" /min java -jar "%~dp0backend\target\paimana-insight-1.0.0.jar" --server.port=8081

REM 4. Start Frontend Web Server
echo [4/4] Starting Frontend Server (Port 5500)...
start "PAIMANA - Frontend" /min python -m http.server 5500 --directory "%~dp0frontend"

echo ====================================================
echo All services are running!
echo Frontend UI:    http://localhost:5500
echo Backend API:    http://localhost:8081/api
echo ML Engine:      http://localhost:8001/health
echo ====================================================
ping -n 3 127.0.0.1 >nul
start http://localhost:5500
