@echo off
setlocal enableextensions
cd /d "%~dp0"

WHERE java >nul 2>&1 && (
    java -Xmx512M -jar slimevr.jar
) || (
    echo Java was not found in your system.
    echo.
    echo Either use SlimeVR Installer to install the server by following this link:
    echo https://github.com/SlimeVR/SlimeVR-Installer/releases/latest/download/slimevr_web_installer.exe
    echo.
    echo Or download Java 11 by following this link:
    echo https://adoptium.net/releases.html?variant=openjdk11^&jvmVariant=hotspot
    pause
)