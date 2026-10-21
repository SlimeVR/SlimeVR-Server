import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { FieldPath, useForm } from 'react-hook-form';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { SettingRow, SettingsGroup } from '@/components/settings/SettingRow';
import { settingReset } from '@/components/settings/SettingReset';
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
import { defineSettingsPage } from '@/components/settings/settings-definitions';

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

export const advancedSettingsPage = defineSettingsPage({
  path: 'advanced',
  title: 'settings-utils-advanced',
  groups: {
    developer: {
      title: 'settings-utils-advanced-developer_group',
      rows: {
        devMode: {
          title: 'settings-general-interface-dev_mode',
          description: 'settings-general-interface-dev_mode-description',
        },
      },
    },
    folders: {
      title: 'settings-utils-advanced-folders_group',
      rows: {
        openData: {
          title: 'settings-utils-advanced-open_data-v1',
          description: 'settings-utils-advanced-open_data-description-v1',
          available: ({ isElectron }) => isElectron,
        },
        openLogs: {
          title: 'settings-utils-advanced-open_logs',
          description: 'settings-utils-advanced-open_logs-description',
          available: ({ isElectron }) => isElectron,
        },
      },
    },
    dangerZone: {
      rows: {
        gui: {
          title: 'settings-utils-advanced-reset-gui',
          description: 'settings-utils-advanced-reset-gui-description',
        },
        server: {
          title: 'settings-utils-advanced-reset-server',
          description: 'settings-utils-advanced-reset-server-description',
        },
        all: {
          title: 'settings-utils-advanced-reset-all',
          description: 'settings-utils-advanced-reset-all-description',
        },
      },
    },
  },
});

const { groups } = advancedSettingsPage;

export function AdvancedSettings() {
  const electron = useElectron();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();

  const [showWarningGUI, setShowWarningGUI] = useState(false);
  const [showWarningServer, setShowWarningServer] = useState(false);
  const [showWarningAll, setShowWarningAll] = useState(false);
  const { sendRPCPacket } = useWebsocketAPI();

  const { control, watch, handleSubmit, setValue } = useForm<DeveloperModeForm>(
    {
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
    }
  );

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

  const resetDevMode = settingReset<DeveloperModeForm, 'developer.enabled'>(
    defaultGUIConfig.debug,
    watch('developer.enabled'),
    (next) => setValue('developer.enabled', next)
  );

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

  const resets = [
    {
      id: 'gui',
      setting: groups.dangerZone.rows.gui,
      label: 'settings-utils-advanced-reset-gui-label',
      open: showWarningGUI,
      setOpen: setShowWarningGUI,
      accept: () => {
        setConfig(guiDefaults());
        setShowWarningGUI(false);
      },
    },
    {
      id: 'server',
      setting: groups.dangerZone.rows.server,
      label: 'settings-utils-advanced-reset-server-label',
      open: showWarningServer,
      setOpen: setShowWarningServer,
      accept: () => {
        sendRPCPacket(
          RpcMessage.SettingsResetRequest,
          new SettingsResetRequestT()
        );
        setShowWarningServer(false);
      },
    },
    {
      id: 'all',
      setting: groups.dangerZone.rows.all,
      label: 'settings-utils-advanced-reset-all-label',
      open: showWarningAll,
      setOpen: setShowWarningAll,
      accept: () => {
        sendRPCPacket(
          RpcMessage.SettingsResetRequest,
          new SettingsResetRequestT()
        );
        setConfig(guiDefaults());
        setShowWarningAll(false);
      },
    },
  ] as const;

  return (
    <form className="w-full">
      <SettingsPagePaneLayout
        icon={<BugIcon />}
        title={advancedSettingsPage.title}
      >
        <div className="flex flex-col">
          <SettingsGroup group={groups.developer}>
            <SettingRow
              setting={groups.developer.rows.devMode}
              controlId="advanced-dev-mode"
              {...resetDevMode}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="developer.enabled"
                  id="advanced-dev-mode"
                />
              }
              childrenDisabled={!config?.debug}
            >
              <div className="grid sm:grid-cols-2 gap-2 py-1">
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
            </SettingRow>
          </SettingsGroup>

          {electron.isElectron && (
            <SettingsGroup group={groups.folders}>
              <SettingRow
                setting={groups.folders.rows.openData}
                control={
                  <Button
                    variant="secondary"
                    className="whitespace-nowrap"
                    onClick={openConfigFolder}
                    id="settings-utils-advanced-open_data-label"
                  />
                }
              />
              <SettingRow
                setting={groups.folders.rows.openLogs}
                control={
                  <Button
                    variant="secondary"
                    className="whitespace-nowrap"
                    onClick={openLogsFolder}
                    id="settings-utils-advanced-open_logs-label"
                  />
                }
              />
            </SettingsGroup>
          )}

          <fieldset className="flex flex-col mt-10 rounded-lg border border-status-critical px-2 xs:px-3 pt-4 pb-5 min-w-0">
            <legend className="uppercase px-2 ml-2">
              <Typography
                variant="section-title"
                color="text-status-critical"
                id="settings-utils-advanced-danger_zone"
              />
            </legend>
            <div className="px-3 xs:px-4 pb-2 max-w-[75ch]">
              <Typography id="settings-utils-advanced-danger_zone-description" />
            </div>
            <SettingsGroup group={groups.dangerZone}>
              {resets.map(({ id, setting, label, open, setOpen, accept }) => (
                <SettingRow
                  key={id}
                  setting={setting}
                  control={
                    <>
                      <Button
                        variant="danger"
                        className="whitespace-nowrap"
                        onClick={() => setOpen(true)}
                        id={label}
                      />
                      <SettingsResetModal
                        accept={accept}
                        onClose={() => setOpen(false)}
                        isOpen={open}
                        variant={id}
                      />
                    </>
                  }
                />
              ))}
            </SettingsGroup>
          </fieldset>
        </div>
      </SettingsPagePaneLayout>
    </form>
  );
}
