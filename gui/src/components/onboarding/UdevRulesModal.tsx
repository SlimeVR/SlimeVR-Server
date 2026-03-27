import { useState, useEffect } from 'react';
import { Button } from '@/components/commons/Button';
import { BaseModal } from '@/components/commons/BaseModal';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { useElectron } from '@/hooks/electron';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { RpcMessage, InstalledInfoResponseT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { useLocalization } from '@fluent/react';
import { useAppContext } from '@/hooks/app';

export function UdevRulesModal() {
  const { config, setConfig } = useConfig();
  const electron = useElectron();
  const [udevContent, setUdevContent] = useState('');
  const [showUdevWarning, setShowUdevWarning] = useState(false);
  const [dontShowThisSession, setDontShowThisSession] = useState(false);
  const [dontShowAgain, setDontShowAgain] = useState(false);
  const { l10n } = useLocalization();
  const { installInfo } = useAppContext();

  const handleUdevContent = async () => {
    if (electron.isElectron) {
      const dir = await electron.api.getInstallDir();
      const rulesPath = `${dir}/69-slimevr-devices.rules`;
      setUdevContent(
        `cat ${rulesPath} | sudo sh -c 'tee /etc/udev/rules.d/69-slimevr-devices.rules >/dev/null && udevadm control --reload-rules && udevadm trigger'`
      );
    }
  };

  useEffect(() => {
    handleUdevContent();
  }, []);

  useEffect(() => {
    if (!config) throw 'Invalid state!';
    if (electron.isElectron) {
      const isLinux = electron.data().os.type === 'linux';
      const udevMissing = !installInfo?.isUdevInstalled;
      const notHiddenGlobally = !config.dontShowUdevModal;
      const notHiddenThisSession = !dontShowThisSession;
      const shouldShow =
        isLinux && udevMissing && notHiddenGlobally && notHiddenThisSession;
      setShowUdevWarning(shouldShow);
    }
  }, [config, dontShowThisSession]);


  const handleModalClose = () => {
    if (!config) throw 'Invalid State!';
    setConfig({ dontShowUdevModal: dontShowAgain });
    setDontShowThisSession(true);
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(udevContent);
  };

  return (
    <BaseModal isOpen={showUdevWarning} appendClasses='w-full max-w-2xl'>
      <div className="flex w-full h-full flex-col gap-4">
        <div className="flex flex-col gap-3">
          <div className="flex flex-col gap-2">
            <Typography
              variant="main-title"
              id="install-info_udev-rules_modal_title"
            />
            <Typography id="install-info_udev-rules_warning" />
          </div>
          <div className="relative w-full max-w-2xl">
            <div className="absolute right-2 top-2">
              <Button variant="secondary" onClick={copyToClipboard}>
                Copy
              </Button>
            </div>
            <div className="bg-background-80 rounded-lg overflow-auto p-2 w-full  h-[300px]">
              <pre className="text-wrap">{udevContent}</pre>
            </div>
          </div>
        </div>
        <div className="flex justify-between gap-2">
          <CheckboxInternal
            label={l10n.getString(
              'install-info_udev-rules_modal-dont-show-again_checkbox'
            )}
            name="dismiss-udev-rules-checkbox"
            onChange={(e) => setDontShowAgain(e.currentTarget.checked)}
          />
          <Button
            variant="primary"
            onClick={handleModalClose}
            id="install-info_udev-rules_modal_button"
          />
        </div>
      </div>
    </BaseModal>
  );
}
