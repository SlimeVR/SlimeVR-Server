#!/bin/bash

SRC="/opt/SlimeVR/69-slimevr-devices.rules"
DESTDIRS=("/lib" "/usr/lib")

if [[ ! -f "$SRC" ]]; then
    echo "SlimeVR udev rules not found, serial console and dongles may not work" >&2
    exit 0
fi

echo "Configuring SlimeVR udev rules..."

for DIR in "${DESTDIRS[@]}"; do
    if [[ -d "$DIR" && ! -h "$DIR" ]]; then
        echo "Copying rules to $DIR"
        install -Dm644 "$SRC" "$DIR/udev/rules.d/69-slimevr-devices.rules"

        if command -v udevadm >/dev/null 2>&1; then
            udevadm control --reload-rules
            udevadm trigger
        fi
        exit 0
    fi
done

echo "Couldn't copy SlimeVR udev rules, serial console and dongles may not work" >&2
