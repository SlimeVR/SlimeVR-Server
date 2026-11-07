#!/bin/bash

# Upgrades run the old package remove script after the new install, keep the files then
# (deb passes "upgrade", rpm passes the remaining install count)
if [[ "$1" == "upgrade" || "$1" == "1" ]]; then
    exit 0
fi

echo "Removing SlimeVR udev rules..."
rm -f "/lib/udev/rules.d/69-slimevr-devices.rules"
rm -f "/usr/lib/udev/rules.d/69-slimevr-devices.rules"

if command -v udevadm >/dev/null 2>&1; then
    udevadm control --reload-rules
fi

# Keep a definition that is still in use, a zone or rule pointing at a missing one breaks the firewall reload
if grep -rqs 'service name="slimevr"' /etc/firewalld/zones/; then
    echo "SlimeVR firewalld service still enabled, keeping /usr/lib/firewalld/services/slimevr.xml" >&2
else
    rm -f "/usr/lib/firewalld/services/slimevr.xml"
fi

if grep -qs "SlimeVR" /etc/ufw/user.rules /etc/ufw/user6.rules; then
    echo "SlimeVR ufw rule still present, keeping /etc/ufw/applications.d/slimevr" >&2
else
    rm -f "/etc/ufw/applications.d/slimevr"
fi
