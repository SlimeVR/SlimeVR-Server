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
  const { app } = require('electron')
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const electron = useElectron();
  const [udevContent, setUdevContent] = useState('');
  const [udevInstalledResponse, setUdevInstalledResponse] = useState(true);
  const [showUdevWarning, setShowUdevWarning] = useState(false);
  const [dontShowThisSession, setDontShowThisSession] = useState(false)

  useEffect(() => {
    if (electron.isElectron) {
      const workDir = `${app.getPath('exe')}/69-slimevr-devices.rules`;
      console.log(workDir);
      setUdevContent(`cat ${workDir} | sudo tee /etc/udev/rules.d/69-slimevr-devices.rules >/dev.null`);
    }
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

  const copyToClipboard = () => {

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
              <Button variant="secondary" onClick={copyToClipboard}>Copy</Button>
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
