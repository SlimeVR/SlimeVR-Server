@echo off
:: Firewall modification doesn't work w/o admin, slimes will be sad.
net session >nul 2>&1
if %errorlevel% == 0 (
    echo Running with administrative privileges! - Needed for firewall modification!
) else (
    echo Requesting administrative privileges - Needed for firewall modification! 
    :: Temp script to request admin... Works and doesn't leave a mess.
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    echo UAC.ShellExecute "%~s0", "", "%~dp0", "runas", 1 >> "%temp%\getadmin.vbs"
    "%temp%\getadmin.vbs"
    exit /B
)

echo Installing firewall rules...

rem Rotational data default port
call :AddRule "SlimeVR UDP 6969 incoming" "dir=in action=allow protocol=UDP localport=6969 enable=yes"
call :AddRule "SlimeVR UDP 6969 outgoing" "dir=out action=allow protocol=UDP localport=6969 enable=yes"
rem WebSocket server default port
call :AddRule "SlimeVR TCP 21110 incoming" "dir=in action=allow protocol=TCP localport=21110 enable=yes"
call :AddRule "SlimeVR TCP 21110 outgoing" "dir=out action=allow protocol=TCP localport=21110 enable=yes"
rem OpenJDK Platform Binary access
call :AddRule "SlimeVR OpenJDK Platform incoming" "dir=in action=allow program=%~dp0jre\bin\java.exe enable=yes"
call :AddRule "SlimeVR OpenJDK Platform outgoing" "dir=out action=allow program=%~dp0jre\bin\java.exe enable=yes"

echo Done!
pause
exit /B

:: Check and don't make duplicate rules :3
:AddRule
setlocal
set "ruleName=%~1"
set "ruleParams=%~2"

netsh advfirewall firewall show rule name="%ruleName%" >nul 2>&1
if %errorlevel% neq 0 (
    echo Adding rule: %ruleName%
    netsh advfirewall firewall add rule name="%ruleName%" %ruleParams%
) else (
    echo Rule already exists: %ruleName%
)

endlocal
exit /B
