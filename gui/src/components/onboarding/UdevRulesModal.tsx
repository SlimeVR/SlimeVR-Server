import { useState, useEffect, useRef } from 'react';
import { Button } from '@/components/commons/Button';
import { BaseModal } from '@/components/commons/BaseModal';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { useElectron } from '@/hooks/electron';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { RpcMessage, InstalledInfoResponseT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { Localized } from '@fluent/react';

export function UdevRulesModal() {
  const { config } = useConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const electron = useElectron();
  const [udevContent, setUdevContent] = useState('');
  const [udevInstalledResponse, setUdevInstalledResponse] = useState(true);
  const [showUdevWarning, setShowUdevWarning] = useState(false);
  const [dontShowThisSession, setDontShowThisSession] = useState(false)

  useEffect(() => {
    setUdevContent(`sudo echo '
# Copyright 2025 Eiren Rain and SlimeVR Contributors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

## QinHeng
# CH340
SUBSYSTEMS=="usb", ATTRS{idVendor}=="1A86", ATTRS{idProduct}=="7522", MODE="0660", TAG+="uaccess"
SUBSYSTEMS=="usb", ATTRS{idVendor}=="1A86", ATTRS{idProduct}=="7523", MODE="0660", TAG+="uaccess"
# CH341
SUBSYSTEMS=="usb", ATTRS{idVendor}=="1A86", ATTRS{idProduct}=="5523", MODE="0660", TAG+="uaccess"
# CH343
SUBSYSTEMS=="usb", ATTRS{idVendor}=="1A86", ATTRS{idProduct}=="55D3", MODE="0660", TAG+="uaccess"
# CH9102x
SUBSYSTEMS=="usb", ATTRS{idVendor}=="1A86", ATTRS{idProduct}=="55D4", MODE="0660", TAG+="uaccess"

## Silabs
# CP210x
SUBSYSTEMS=="usb", ATTRS{idVendor}=="10C4", ATTRS{idProduct}=="EA60", MODE="0660", TAG+="uaccess"

## Espressif
# ESP32-S3 / ESP32-C3 / ESP32-C5 / ESP32-C6 / ESP32-C61 / ESP32-H2 / ESP32-P4
SUBSYSTEMS=="usb", ATTRS{idVendor}=="303A", ATTRS{idProduct}=="1001", MODE="0660", TAG+="uaccess"
# ESP32-S2
SUBSYSTEMS=="usb", ATTRS{idVendor}=="303A", ATTRS{idProduct}=="0002", MODE="0660", TAG+="uaccess"

## FTDI
# FT232BM/L/Q, FT245BM/L/Q
# FT232RL/Q, FT245RL/Q
# VNC1L with VDPS Firmware
# VNC2 with FT232Slave
SUBSYSTEMS=="usb", ATTRS{idVendor}=="0403", ATTRS{idProduct}=="6001", MODE="0660", TAG+="uaccess"

## SlimeVR
# smol slime dongle
SUBSYSTEM=="usb", ATTR{idVendor}=="1209", ATTR{idProduct}=="7690", MODE="0660", TAG+="uaccess"
KERNEL=="hidraw*", SUBSYSTEM=="hidraw", ATTRS{idVendor}=="1209", ATTRS{idProduct}=="7690", MODE="0660", TAG+="uaccess"
' > /etc/udev/rules.d/69-slimevr.rules
`);
  }, []);

  useEffect(() => {
    if (!config) throw "Invalid state!"
    if (electron.isElectron) {
      console.log(electron.data().os.type === 'linux' && !udevInstalledResponse)
      if (electron.data().os.type === 'linux' && !udevInstalledResponse && !config.dontShowUdevModal && !dontShowThisSession) {
        setShowUdevWarning(true);
      } else {
        setShowUdevWarning(false);
      }
    }
  }, [udevInstalledResponse]);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.InstalledInfoRequest,
      new InstalledInfoResponseT()
    );
  }, []);

  useRPCPacket(
    RpcMessage.InstalledInfoResponse,
    ({ isUdevInstalled }: InstalledInfoResponseT) => {
      console.log(`Is udev installed ${isUdevInstalled}`)
      setUdevInstalledResponse(isUdevInstalled);
    }
  );

  const handleModalCose = () => {
    setShowUdevWarning(false)
    setDontShowThisSession(true)
  }

  const setConfig = (checked:boolean) => {
    if (!config) throw "invalid state!"
    config.dontShowUdevModal = checked
  }

  return (
    <BaseModal isOpen={showUdevWarning} appendClasses={'w-full max-w-2xl'}>
      <div className="flex w-full h-full flex-col gap-4">
        <div className="flex flex-col gap-3">
          <div className="flex flex-col gap-2">
            <Typography variant="main-title" id="install-info_udev-rules_modal_title"/>
            <Typography id="install-info_udev-rules_warning"/>
          </div>
          <div className="relative w-full max-w-2xl">
            <div className="absolute right-6 top-4">
              <Button variant="secondary">Copy</Button>
            </div>
            <div className="bg-background-80 rounded-lg overflow-auto p-2  h-[300px]">
              <pre>{udevContent}</pre>
            </div>
          </div>
        </div>
        <div className="flex justify-between gap-2">
          <CheckboxInternal
            label="Don't show this again"
            outlined={false}
            name={'dismiss-udev-rules-checkbox'}
            loading={false}
            disabled={false}
            onChange={(e) => setConfig(e.currentTarget.checked)}
          />
          <Button
            variant="primary"
            onClick={handleModalCose}
            id="install-info_udev-rules_modal_button"
          />
        </div>
      </div>
    </BaseModal>
  );
}
