import { useLocalization } from '@fluent/react';
import { useState } from 'react';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { BugIcon } from '@/components/commons/icon/BugIcon';
import { Button } from '@/components/commons/Button';
import { SettingsResetModal } from '@/components/settings/SettingsResetModal';

import { open } from '@tauri-apps/plugin-shell';
import { error } from '@/utils/logging';
import { appConfigDir } from '@tauri-apps/api/path';
import { defaultConfig as defaultGUIConfig, useConfig } from '@/hooks/config';
import { defaultValues as defaultDevConfig } from '@/components/widgets/DeveloperModeWidget';
import { RpcMessage, SettingsResetRequestT } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';

function guiDefaults() {
  // Destructure the properties to exclude "lang"
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { lang, ...guiDefaults } = defaultGUIConfig;

  // Include "devSettings" which has all the properties of "defaultDevConfig"
  // @ts-expect-error "devSettings" is not in the "guiDefaults" object but we want to include it (from "defaultDevConfig")
  guiDefaults.devSettings = defaultDevConfig;

  return guiDefaults;
}

export function AdvancedSettings() {
  const { l10n } = useLocalization();
  const { setConfig } = useConfig();

  const [skipWarningGui, setSkipWarningGui] = useState(false);
  const [skipWarningServer, setSkipWarningServer] = useState(false);
  const [skipWarningAll, setSkipWarningAll] = useState(false);
  const { sendRPCPacket } = useWebsocketAPI();

  const openConfigFolder = async () => {
    try {
      const configPath = await appConfigDir();
      await open('file://' + configPath);
    } catch (err) {
      error('Failed to open config folder:', err);
    }
  };

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<BugIcon></BugIcon>} id="advanced">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-utils-advanced')}
            </Typography>

            <div className="grid gap-4 mobile:gap-6">
              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography bold>
                    {l10n.getString('settings-utils-advanced-reset-gui')}
                  </Typography>
                  <div className="flex flex-col pt-1">
                    <Typography color="secondary">
                      {l10n.getString(
                        'settings-utils-advanced-reset-gui-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col">
                  <Button
                    variant="secondary"
                    onClick={() => setSkipWarningGui(true)}
                  >
                    {l10n.getString('settings-utils-advanced-reset-gui-label')}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      setConfig(guiDefaults());
                      setSkipWarningGui(false);
                    }}
                    onClose={() => setSkipWarningGui(false)}
                    isOpen={skipWarningGui}
                    variant="gui"
                  ></SettingsResetModal>
                </div>
              </div>

              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography bold>
                    {l10n.getString('settings-utils-advanced-reset-server')}
                  </Typography>
                  <div className="flex flex-col pt-1">
                    <Typography color="secondary">
                      {l10n.getString(
                        'settings-utils-advanced-reset-server-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col">
                  <Button
                    variant="secondary"
                    onClick={() => setSkipWarningServer(true)}
                  >
                    {l10n.getString(
                      'settings-utils-advanced-reset-server-label'
                    )}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      sendRPCPacket(
                        RpcMessage.SettingsResetRequest,
                        new SettingsResetRequestT()
                      );
                      setSkipWarningServer(false);
                    }}
                    onClose={() => setSkipWarningServer(false)}
                    isOpen={skipWarningServer}
                    variant="server"
                  ></SettingsResetModal>
                </div>
              </div>

              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography bold>
                    {l10n.getString('settings-utils-advanced-reset-all')}
                  </Typography>
                  <div className="flex flex-col pt-1">
                    <Typography color="secondary">
                      {l10n.getString(
                        'settings-utils-advanced-reset-all-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col">
                  <Button
                    variant="secondary"
                    onClick={() => setSkipWarningAll(true)}
                  >
                    {l10n.getString('settings-utils-advanced-reset-all-label')}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      sendRPCPacket(
                        RpcMessage.SettingsResetRequest,
                        new SettingsResetRequestT()
                      );
                      setConfig(guiDefaults());
                      setSkipWarningAll(false);
                    }}
                    onClose={() => setSkipWarningAll(false)}
                    isOpen={skipWarningAll}
                    variant="all"
                  ></SettingsResetModal>
                </div>
              </div>

              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography bold>
                    {l10n.getString('settings-utils-advanced-open_data')}
                  </Typography>
                  <div className="flex flex-col pt-1">
                    <Typography color="secondary">
                      {l10n.getString(
                        'settings-utils-advanced-open_data-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col">
                  <Button variant="secondary" onClick={openConfigFolder}>
                    {l10n.getString('settings-utils-advanced-open_data-label')}
                  </Button>
                </div>
              </div>
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
