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
netsh advfirewall firewall delete rule name="SlimeVR UDP 35903 incoming"
netsh advfirewall firewall delete rule name="SlimeVR UDP 35903 outgoing"

rem Rotational data default port
netsh advfirewall firewall delete rule name="SlimeVR UDP 6969 incoming"
netsh advfirewall firewall delete rule name="SlimeVR UDP 6969 outgoing"

rem WebSocket server default port
netsh advfirewall firewall delete rule name="SlimeVR TCP 21110 incoming"
netsh advfirewall firewall delete rule name="SlimeVR TCP 21110 outgoing"

rem OpenJDK Platform Binary access
netsh advfirewall firewall delete rule name="SlimeVR OpenJDK Platform incoming"
netsh advfirewall firewall delete rule name="SlimeVR OpenJDK Platform outgoing"

echo Done!
pause