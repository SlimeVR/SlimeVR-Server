#!/bin/bash
echo "Removing SlimeVR udev rules..."
rm -f "/lib/udev/rules.d/69-slimevr-devices.rules"
rm -f "/usr/lib/udev/rules.d/69-slimevr-devices.rules"

if command -v udevadm >/dev/null 2>&1; then
    udevadm control --reload-rules
fi
