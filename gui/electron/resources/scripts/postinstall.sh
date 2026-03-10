#!/bin/bash

SRC="/opt/SlimeVR/69-slimevr-devices.rules"
DESTS=("/lib/udev/rules.d" "/usr/lib/udev/rules.d")

if [ -f "$SRC" ]; then
    echo "Configuring SlimeVR udev rules..."

    for DIR in "${DESTS[@]}"; do
        if [ -d "$DIR" ]; then
            echo "Copying rules to $DIR"
            cp "$SRC" "$DIR/69-slimevr-devices.rules"
            chmod 644 "$DIR/69-slimevr-devices.rules"
            FOUND_DIR=true
        fi
    done

    if [ "$FOUND_DIR" = true ]; then
        if command -v udevadm >/dev/null 2>&1; then
            udevadm control --reload-rules
            udevadm trigger
        fi
    else
        echo "Warning: No udev rules directory found. Hardware may require manual configuration."
    fi
fi
