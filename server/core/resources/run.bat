@echo off
setlocal enableextensions
cd /d "%~dp0"

where java >nul 2>&1
if %errorlevel% EQU 0 (
    java -Xmx512M -jar slimevr.jar
) else (
    echo Java was not found in your system.
    echo.
    echo Either use SlimeVR Installer to install the server by following this link:
    echo https://github.com/SlimeVR/SlimeVR-Installer/releases/latest/download/slimevr_web_installer.exe
    echo.
    echo Or download Java 17 by following this link:
    echo https://adoptium.net/temurin/releases/?version=17
)
if %errorlevel% NEQ 0 (
    pause
)
