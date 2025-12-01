@echo off
rem Run Pacman with console to show errors (use this to debug why jar doesn't open)
cd /d "%~dp0"
if not exist pacman.jar (
  echo pacman.jar not found in %CD%
  pause
  exit /b 1
)
java -jar pacman.jar
if ERRORLEVEL 1 (
  echo.
  echo Program exited with error %ERRORLEVEL%.
)
pause
