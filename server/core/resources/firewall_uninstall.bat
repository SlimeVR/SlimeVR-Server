@echo off
echo Uninstalling firewall rules...

rem Discovery defauly port
netsh advfirewall firewall delete rule name="SlimeVR UDP 35903 incoming"
netsh advfirewall firewall delete rule name="SlimeVR UDP 35903 outgoing"

rem Rotational data default port
netsh advfirewall firewall delete rule name="SlimeVR UDP 6969 incoming"
netsh advfirewall firewall delete rule name="SlimeVR UDP 6969 outgoing"

rem WebSocket server default port
netsh advfirewall firewall delete rule name="SlimeVR TCP 21110 incoming"
netsh advfirewall firewall delete rule name="SlimeVR TCP 21110 outgoing"

rem OTA server default port
netsh advfirewall firewall delete rule name="SlimeVR UDP 10001 incoming"
netsh advfirewall firewall delete rule name="SlimeVR UDP 10001 outgoing"


echo Done!
pause
