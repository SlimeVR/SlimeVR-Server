@echo off
echo Installing firewall rules...

rem Discovery default port
netsh advfirewall firewall add rule name="SlimeVR UDP 35903 incoming" dir=in action=allow protocol=UDP localport=35903
netsh advfirewall firewall add rule name="SlimeVR UDP 35903 outgoing" dir=out action=allow protocol=UDP localport=35903

rem Rotational data default port
netsh advfirewall firewall add rule name="SlimeVR UDP 6969 incoming" dir=in action=allow protocol=UDP localport=6969
netsh advfirewall firewall add rule name="SlimeVR UDP 6969 outgoing" dir=out action=allow protocol=UDP localport=6969

rem WebSocket server default port
netsh advfirewall firewall add rule name="SlimeVR TCP 21110 incoming" dir=in action=allow protocol=TCP localport=21110
netsh advfirewall firewall add rule name="SlimeVR TCP 21110 outgoing" dir=out action=allow protocol=TCP localport=21110

echo Done!
pause
