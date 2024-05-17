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

rem Discovery default port (In/Out)
call :AddRule "SlimeVR UDP 35903" "dir=in,out action=allow protocol=UDP localport=35903 enable=yes"
rem Rotational data default port (In/Out)
call :AddRule "SlimeVR UDP 6969" "dir=in,out action=allow protocol=UDP localport=6969 enable=yes"
rem WebSocket server default port (In/Out)
call :AddRule "SlimeVR TCP 21110" "dir=in,out action=allow protocol=TCP localport=21110 enable=yes"
rem OpenJDK Platform Binary access (In/Out)
call :AddRule "SlimeVR OpenJDK Platform" "dir=in,out action=allow program=%~dp0jre\bin\java.exe enable=yes"

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
