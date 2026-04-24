@echo off
:: Driver installation doesn't work w/o admin, slimes will be sad.
net session >nul 2>&1
if %errorlevel% == 0 (
    echo Running with administrative privileges! - Needed for firewall modification!
) else (
    echo Requesting administrative privileges - Needed for Driver installation!
    :: Temp script to request admin... Works and doesn't leave a mess.
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    echo UAC.ShellExecute "%~s0", "", "%~dp0", "runas", 1 >> "%temp%\getadmin.vbs"
    "%temp%\getadmin.vbs"
    exit /B
)

pnputil /add-driver *.inf /install /subdirs
