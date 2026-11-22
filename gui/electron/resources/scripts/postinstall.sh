#!/bin/bash

install_udev_rules() {
    local SRC="/opt/SlimeVR/69-slimevr-devices.rules"
    local DESTDIRS=("/lib" "/usr/lib")

    if [[ ! -f "$SRC" ]]; then
        echo "SlimeVR udev rules not found, serial console and dongles may not work" >&2
        return
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
            return
        fi
    done

    echo "Couldn't copy SlimeVR udev rules, serial console and dongles may not work" >&2
}

# Only defines the services, opening the ports is left to the user.
install_firewall_services() {
    local SRC="/opt/SlimeVR/firewall"

    if [[ -d "/usr/lib/firewalld/services" && -f "$SRC/slimevr.xml" ]]; then
        echo "Installing SlimeVR firewalld service, enable it with: firewall-cmd --permanent --add-service=slimevr && firewall-cmd --reload"
        install -Dm644 "$SRC/slimevr.xml" "/usr/lib/firewalld/services/slimevr.xml"
    fi

    if [[ -d "/etc/ufw/applications.d" && -f "$SRC/slimevr-ufw" ]]; then
        echo "Installing SlimeVR ufw profile, enable it with: ufw allow SlimeVR"
        install -Dm644 "$SRC/slimevr-ufw" "/etc/ufw/applications.d/slimevr"
    fi
}

install_udev_rules
install_firewall_services
exit 0
