import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { FieldPath, useForm } from 'react-hook-form';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { BugIcon } from '@/components/commons/icon/BugIcon';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { SettingsResetModal } from '@/components/settings/SettingsResetModal';

import { error } from '@/utils/logging';
import {
  defaultDevSettings,
  defaultConfig as defaultGUIConfig,
  DeveloperModeConfig,
  useConfig,
} from '@/hooks/config';
import { RpcMessage, SettingsResetRequestT } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useElectron } from '@/hooks/electron';

interface DeveloperModeForm {
  developer: DeveloperModeConfig & {
    enabled: boolean;
  };
}

const devToggles = {
  highContrast: 'high_contrast',
  preciseRotation: 'precise_rotation',
  fastDataFeed: 'fast_data_feed',
  rawSlimeRotation: 'raw_slime_rotation',
};

function guiDefaults() {
  // Destructure the properties to exclude "lang"

  const { lang, ...guiDefaults } = defaultGUIConfig;

  guiDefaults.devSettings = defaultDevSettings;

  return guiDefaults;
}

export function AdvancedSettings() {
  const electron = useElectron();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();

  const [showWarningGUI, setShowWarningGUI] = useState(false);
  const [showWarningServer, setShowWarningServer] = useState(false);
  const [showWarningAll, setShowWarningAll] = useState(false);
  const { sendRPCPacket } = useWebsocketAPI();

  const { control, watch, handleSubmit } = useForm<DeveloperModeForm>({
    defaultValues: {
      developer: {
        enabled: config?.debug ?? defaultGUIConfig.debug,
        highContrast:
          config?.devSettings?.highContrast ??
          defaultGUIConfig.devSettings.highContrast,
        preciseRotation:
          config?.devSettings?.preciseRotation ??
          defaultGUIConfig.devSettings.preciseRotation,
        fastDataFeed:
          config?.devSettings?.fastDataFeed ??
          defaultGUIConfig.devSettings.fastDataFeed,
        rawSlimeRotation:
          config?.devSettings?.rawSlimeRotation ??
          defaultGUIConfig.devSettings.rawSlimeRotation,
      },
    },
  });

  const onSubmit = (values: DeveloperModeForm) => {
    setConfig({
      debug: values.developer.enabled,
      devSettings: values.developer,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, [onSubmit]);

  const openConfigFolder = async () => {
    if (!electron.isElectron) throw 'invalid state - no electron';
    try {
      await electron.api.openConfigFolder();
    } catch (err) {
      error('Failed to open config folder:', err);
    }
  };

  const openLogsFolder = async () => {
    if (!electron.isElectron) throw 'invalid state - no electron';
    try {
      await electron.api.openLogsFolder();
    } catch (err) {
      error('Failed to open config folder:', err);
    }
  };

  return (
    <form className="flex flex-col gap-2 w-full">
      <SettingsPagePaneLayout icon={<BugIcon />}>
        <>
          <Typography variant="main-title">
            {l10n.getString('settings-utils-advanced')}
          </Typography>

          <div className="grid gap-6 pt-4">
            <div className="flex flex-col gap-2">
              <div className="flex flex-col">
                <Typography variant="section-title">
                  {l10n.getString('settings-general-interface-dev_mode')}
                </Typography>
                <Typography>
                  {l10n.getString(
                    'settings-general-interface-dev_mode-description'
                  )}
                </Typography>
              </div>
              <div className="grid">
                <CheckBox
                  variant="toggle"
                  control={control}
                  outlined
                  name="developer.enabled"
                  label={l10n.getString(
                    'settings-general-interface-dev_mode-label'
                  )}
                />
              </div>
              {!!config?.debug && (
                <div className="grid sm:grid-cols-2 gap-2 p-4 rounded-2xl bg-background-80">
                  {Object.entries(devToggles).map(([name, label]) => (
                    <CheckBox
                      key={name}
                      control={control}
                      variant="toggle"
                      outlined
                      name={`developer.${name}` as FieldPath<DeveloperModeForm>}
                      label={l10n.getString(`widget-developer_mode-${label}`)}
                    />
                  ))}
                </div>
              )}
            </div>

            {electron.isElectron && (
              <>
                <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                  <div>
                    <Typography variant="section-title">
                      {l10n.getString('settings-utils-advanced-open_data-v1')}
                    </Typography>
                    <div className="flex flex-col">
                      <Typography>
                        {l10n.getString(
                          'settings-utils-advanced-open_data-description-v1'
                        )}
                      </Typography>
                    </div>
                  </div>
                  <div className="flex flex-col nsm:pt-2">
                    <Button variant="secondary" onClick={openConfigFolder}>
                      {l10n.getString(
                        'settings-utils-advanced-open_data-label'
                      )}
                    </Button>
                  </div>
                </div>
                <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                  <div>
                    <Typography variant="section-title">
                      {l10n.getString('settings-utils-advanced-open_logs')}
                    </Typography>
                    <div className="flex flex-col">
                      <Typography>
                        {l10n.getString(
                          'settings-utils-advanced-open_logs-description'
                        )}
                      </Typography>
                    </div>
                  </div>
                  <div className="flex flex-col nsm:pt-2">
                    <Button variant="secondary" onClick={openLogsFolder}>
                      {l10n.getString(
                        'settings-utils-advanced-open_logs-label'
                      )}
                    </Button>
                  </div>
                </div>
              </>
            )}

            <div className="flex flex-col gap-4 p-4 rounded-2xl border border-status-critical">
              <div className="flex flex-col">
                <Typography
                  variant="section-title"
                  color="text-status-critical"
                  id="settings-utils-advanced-danger_zone"
                />
                <Typography
                  color="text-status-critical"
                  id="settings-utils-advanced-danger_zone-description"
                />
              </div>

              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString('settings-utils-advanced-reset-gui')}
                  </Typography>
                  <div className="flex flex-col">
                    <Typography>
                      {l10n.getString(
                        'settings-utils-advanced-reset-gui-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col nsm:pt-2">
                  <Button
                    variant="secondary"
                    onClick={() => setShowWarningGUI(true)}
                  >
                    {l10n.getString('settings-utils-advanced-reset-gui-label')}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      setConfig(guiDefaults());
                      setShowWarningGUI(false);
                    }}
                    onClose={() => setShowWarningGUI(false)}
                    isOpen={showWarningGUI}
                    variant="gui"
                  />
                </div>
              </div>

              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString('settings-utils-advanced-reset-server')}
                  </Typography>
                  <div className="flex flex-col">
                    <Typography>
                      {l10n.getString(
                        'settings-utils-advanced-reset-server-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col nsm:pt-2">
                  <Button
                    variant="secondary"
                    onClick={() => setShowWarningServer(true)}
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
                      setShowWarningServer(false);
                    }}
                    onClose={() => setShowWarningServer(false)}
                    isOpen={showWarningServer}
                    variant="server"
                  />
                </div>
              </div>

              <div className="sm:grid sm:grid-cols-[1.75fr,_1fr] items-center">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString('settings-utils-advanced-reset-all')}
                  </Typography>
                  <div className="flex flex-col">
                    <Typography>
                      {l10n.getString(
                        'settings-utils-advanced-reset-all-description'
                      )}
                    </Typography>
                  </div>
                </div>
                <div className="flex flex-col nsm:pt-2">
                  <Button
                    variant="secondary"
                    onClick={() => setShowWarningAll(true)}
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
                      setShowWarningAll(false);
                    }}
                    onClose={() => setShowWarningAll(false)}
                    isOpen={showWarningAll}
                    variant="all"
                  />
                </div>
              </div>
            </div>
          </div>
        </>
      </SettingsPagePaneLayout>
    </form>
  );
}
