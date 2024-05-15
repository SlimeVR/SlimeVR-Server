@echo off
:: Firewall modification doesn't work w/o admin, slimes will be sad.
net session >nul 2>&1
if %errorlevel% == 0 (
    echo Running with administrative privileges! - Needed for firewall modification!
) else (
    echo Requesting administrative privileges - Needed for firewall modification! 
    :: Temp script to request admin... Works and doesn't leave a mess.
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    echo UAC.ShellExecute "%~s0", "", "", "runas", 1 >> "%temp%\getadmin.vbs"
    "%temp%\getadmin.vbs"
    exit /B
)

echo Uninstalling firewall rules...

rem Discovery default port
call :DeleteRule "SlimeVR UDP 35903 incoming"
call :DeleteRule "SlimeVR UDP 35903 outgoing"
rem Rotational data default port
call :DeleteRule "SlimeVR UDP 6969 incoming"
call :DeleteRule "SlimeVR UDP 6969 outgoing"
rem WebSocket server default port
call :DeleteRule "SlimeVR TCP 21110 incoming"
call :DeleteRule "SlimeVR TCP 21110 outgoing"
rem OpenJDK Platform Binary access
call :DeleteRule "SlimeVR OpenJDK Platform incoming"
call :DeleteRule "SlimeVR OpenJDK Platform outgoing"

echo Done!
pause
exit /B

:: Delete rules if they exist :3
:DeleteRule
setlocal
set "ruleName=%~1"

netsh advfirewall firewall show rule name="%ruleName%" >nul 2>&1
if %errorlevel% == 0 (
    echo Deleting rule: %ruleName%
    netsh advfirewall firewall delete rule name="%ruleName%"
) else (
    echo Rule does not exist: %ruleName%
)

endlocal
exit /B
