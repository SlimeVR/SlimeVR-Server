import { useState, useEffect } from 'react';
import { Button } from '@/components/commons/Button';
import { BaseModal } from '@/components/commons/BaseModal';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { useElectron } from '@/hooks/electron';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { RpcMessage, InstalledInfoResponseT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { error } from '@/utils/logging';

export function UdevRulesModal() {
  const { config, setConfig } = useConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const electron = useElectron();
  const [udevContent, setUdevContent] = useState('');
  const [isUdevInstalledResponse, setIsUdevInstalledResponse] = useState(false);
  const [showUdevWarning, setShowUdevWarning] = useState(false);
  const [dontShowThisSession, setDontShowThisSession] = useState(false);
  const [dontShowAgain, setDontShowAgain] = useState(false);
  const [exeDir, setExeDir] = useState('');


  const handleUdevContent = async () => {
    if (electron.isElectron) {
      const dir = await electron.api.getInstallDir()
      setExeDir(dir);
      const rulesDir = `${exeDir}/69-slimevr-devices.rules`;
      setUdevContent(`cat ${rulesDir} | sudo tee /etc/udev/rules.d/69-slimevr-devices.rules pn>/dev/null`);
    }
  }

  useEffect(() => {
    handleUdevContent()
  }, [exeDir]);

  useEffect(() => {
    if (!config) throw 'Invalid state!';
    if (electron.isElectron) {
      const isLinux = electron.data().os.type === 'linux';
      const udevMissing = !isUdevInstalledResponse;
      const notHiddenGlobally = !config.dontShowUdevModal;
      const notHiddenThisSession = !dontShowThisSession;
      const shouldShow = isLinux && udevMissing && notHiddenGlobally && notHiddenThisSession;
      if (shouldShow) {
        setShowUdevWarning(true);
      } else {
        setShowUdevWarning(false);
      }
    }
  }, [config, isUdevInstalledResponse, dontShowThisSession]);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.InstalledInfoRequest,
      new InstalledInfoResponseT()
    );
  }, []);

  useRPCPacket(
    RpcMessage.InstalledInfoResponse,
    ({ isUdevInstalled }: InstalledInfoResponseT) => {
      setIsUdevInstalledResponse(isUdevInstalled);
    }
  );

  const handleModalCose = () => {
    if (!config) throw 'Invalid State!';
    setConfig({dontShowUdevModal: dontShowAgain});
    setDontShowThisSession(true);
  }

  const copyToClipboard = () => {
    navigator.clipboard.writeText(udevContent);
  };


  return (
    <BaseModal isOpen={showUdevWarning} appendClasses={'w-full max-w-2xl'}>
      <div className="flex w-full h-full flex-col gap-4">
        <div className="flex flex-col gap-3">
          <div className="flex flex-col gap-2">
            <Typography variant="main-title" id="install-info_udev-rules_modal_title"/>
            <Typography id="install-info_udev-rules_warning"/>
          </div>
          <div className="relative w-full max-w-2xl">
            <div className="absolute right-2 top-2">
              <Button variant="secondary" onClick={copyToClipboard}>Copy</Button>
            </div>
            <div className="bg-background-80 rounded-lg overflow-auto p-2 w-full  h-[300px]">
              <pre className="text-wrap">{udevContent}</pre>
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
            onChange={(e) => setDontShowAgain(e.currentTarget.checked)}
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
