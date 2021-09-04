@echo off
echo Installing firewall rules...

rem Rotational data default port
netsh advfirewall firewall add rule name="UDP 6969 incoming" dir=in action=allow protocol=UDP localport=6969
netsh advfirewall firewall add rule name="UDP 6969 outgoing" dir=out action=allow protocol=UDP localport=6969

rem Info server allowing automatic discovery
netsh advfirewall firewall add rule name="UDP 35903 incoming" dir=in action=allow protocol=UDP localport=35903
netsh advfirewall firewall add rule name="UDP 35903 outgoing" dir=out action=allow protocol=UDP localport=35903

echo Done!
pause