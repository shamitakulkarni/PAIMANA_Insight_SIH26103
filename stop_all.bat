
@echo off
echo Stopping PAIMANA Insight processes...

REM Stop Python web server on port 5500
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":5500 "') do (
    taskkill /f /pid %%a 2>nul
)

REM Stop Spring Boot backend on port 8081
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081 "') do (
    taskkill /f /pid %%a 2>nul
)

REM Stop FastAPI ML service on port 8001
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8001 "') do (
    taskkill /f /pid %%a 2>nul
)

REM Stop PostgreSQL on port 5433
"C:\Program Files\PostgreSQL\17\bin\pg_ctl.exe" stop -D "%~dp0local_pgdata" -m fast 2>nul

echo All services stopped.
