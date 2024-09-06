import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Button } from '@/components/commons/Button';
import { SettingsResetModal } from '../SettingsResetModal';

import { defaultConfig as defaultGUIConfig, useConfig } from '@/hooks/config';
import { defaultValues as defaultDevConfig } from '@/components/widgets/DeveloperModeWidget';
import { RpcMessage, SettingsResetRequestT } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';

interface InterfaceSettingsForm {
  appearance: {
    devmode: boolean;
    theme: string;
    textSize: number;
    fonts: string;
  };
  notifications: {
    watchNewDevices: boolean;
    feedbackSound: boolean;
    feedbackSoundVolume: number;
    connectedTrackersWarning: boolean;
    useTray: boolean;
    discordPresence: boolean;
  };
}

function guiDefaults() {
  // Destructure the properties to exclude "lang" and "doneOnboarding"
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { lang, doneOnboarding, ...guiDefaults } = defaultGUIConfig;

  // Include "devSettings" which has all the properties of "defaultDevConfig"
  // @ts-expect-error "devSettings" is not in the "guiDefaults" object but we want to include it (from "defaultDevConfig")
  guiDefaults.devSettings = defaultDevConfig;

  return guiDefaults;
}

export function AdvancedSettings() {
  const { l10n } = useLocalization();
  const { setConfig } = useConfig();
  const { watch, handleSubmit } = useForm<InterfaceSettingsForm>({
    defaultValues: {},
  });

  const [skipWarningGui, setSkipWarningGui] = useState(false);
  const [skipWarningServer, setSkipWarningServer] = useState(false);
  const [skipWarningAll, setSkipWarningAll] = useState(false);
  const { sendRPCPacket } = useWebsocketAPI();

  const onSubmit = (values: InterfaceSettingsForm) => {
    setConfig({
      debug: values.appearance.devmode,
      watchNewDevices: values.notifications.watchNewDevices,
      feedbackSound: values.notifications.feedbackSound,
      feedbackSoundVolume: values.notifications.feedbackSoundVolume,
      theme: values.appearance.theme,
      fonts: values.appearance.fonts.split(','),
      textSize: values.appearance.textSize,
      connectedTrackersWarning: values.notifications.connectedTrackersWarning,
      useTray: values.notifications.useTray,
      discordPresence: values.notifications.discordPresence,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  return (
    <SettingsPageLayout>
      <form
        className="flex flex-col gap-2 w-full"
        style={
          {
            '--font-size': '12rem',
            '--font-size-standard': '12rem',
            '--font-size-vr': '16rem',
            '--font-size-title': '25rem',
          } as React.CSSProperties
        }
      >
        <SettingsPagePaneLayout icon={<WrenchIcon></WrenchIcon>} id="advanced">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-utils-advanced')}
            </Typography>

            <div className="grid grid-cols-2 gap-2 mobile:grid-cols-1">
              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-reset-gui')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-reset-gui-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
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
                  ></SettingsResetModal>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-reset-server')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-reset-server-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
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
                  ></SettingsResetModal>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-reset-all')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-reset-all-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
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
                  ></SettingsResetModal>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-open_config')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-open_config-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
                  <Button variant="secondary" onClick={() => {}}>
                    {l10n.getString(
                      'settings-utils-advanced-open_config-label'
                    )}
                  </Button>
                  {/* TODO: open config folder */}
                </div>
              </div>
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}

