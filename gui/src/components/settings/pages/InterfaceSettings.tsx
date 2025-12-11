import { Localized, useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { defaultConfig, useConfig } from '@/hooks/config';
import { ThemeSelector } from '@/components/commons/ThemeSelector';
import { SquaresIcon } from '@/components/commons/icon/SquaresIcon';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { useLocaleConfig } from '@/i18n/config';
import { LangSelector } from '@/components/commons/LangSelector';
import { BellIcon } from '@/components/commons/icon/BellIcon';
import { Range } from '@/components/commons/Range';
import { Dropdown } from '@/components/commons/Dropdown';
import { ArrowRightLeftIcon } from '@/components/commons/icon/ArrowIcons';
import { isTrayAvailable } from '@/utils/tauri';
import { isTauri } from '@tauri-apps/api/core';
import { TauriFileInput } from '@/components/commons/TauriFileInput';
import { DeveloperModeWidget } from '@/components/widgets/DeveloperModeWidget';

interface InterfaceSettingsForm {
  appearance: {
    theme: string;
    textSize: number;
    fonts: string;
    decorations: boolean;
  };
  behavior: {
    devmode: boolean;
    useTray: boolean;
    discordPresence: boolean;
    errorTracking: boolean;
    bvhDirectory: string | null;
  };
  notifications: {
    watchNewDevices: boolean;
    feedbackSound: boolean;
    feedbackSoundVolume: number;
    connectedTrackersWarning: boolean;
  };
}

export function InterfaceSettings() {
  const { currentLocales } = useLocaleConfig();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit } = useForm<InterfaceSettingsForm>({
    defaultValues: {
      appearance: {
        theme: config?.theme ?? defaultConfig.theme,
        textSize: config?.textSize ?? defaultConfig.textSize,
        fonts: config?.fonts.join(',') ?? defaultConfig.fonts.join(','),
        decorations: config?.decorations ?? defaultConfig.decorations,
      },
      notifications: {
        watchNewDevices:
          config?.watchNewDevices ?? defaultConfig.watchNewDevices,
        feedbackSound: config?.feedbackSound ?? defaultConfig.feedbackSound,
        feedbackSoundVolume:
          config?.feedbackSoundVolume ?? defaultConfig.feedbackSoundVolume,
        connectedTrackersWarning:
          config?.connectedTrackersWarning ??
          defaultConfig.connectedTrackersWarning,
      },
      behavior: {
        devmode: config?.debug ?? defaultConfig.debug,
        useTray: config?.useTray ?? defaultConfig.useTray ?? false,
        discordPresence:
          config?.discordPresence ?? defaultConfig.discordPresence,
        errorTracking: config?.errorTracking ?? false,
        bvhDirectory: config?.bvhDirectory ?? defaultConfig.bvhDirectory,
      },
    },
  });

  const fontOptions = [
    {
      label: l10n.getString('settings-interface-appearance-font-slime_font'),
      value: 'poppins',
      fontName: 'poppins, Noto Sans CJK',
    },
    {
      label: 'OpenDyslexic',
      value: 'OpenDyslexic',
      fontName: 'OpenDyslexic',
    },
    { label: 'Lexend', value: 'Lexend', fontName: 'Lexend' },
    { label: 'Ubuntu', value: 'Ubuntu', fontName: 'Ubuntu' },
    {
      label: 'Noto Sans (CJK)',
      value: 'Noto Sans CJK',
      fontName: 'Noto Sans CJK',
    },
    {
      label: l10n.getString('settings-interface-appearance-font-os_font'),
      value: 'ui-sans-serif',
    },
  ];

  const onSubmit = (values: InterfaceSettingsForm) => {
    setConfig({
      watchNewDevices: values.notifications.watchNewDevices,
      feedbackSound: values.notifications.feedbackSound,
      feedbackSoundVolume: values.notifications.feedbackSoundVolume,
      connectedTrackersWarning: values.notifications.connectedTrackersWarning,

      theme: values.appearance.theme,
      fonts: values.appearance.fonts.split(','),
      textSize: values.appearance.textSize,
      decorations: values.appearance.decorations,

      useTray: values.behavior.useTray,
      discordPresence: values.behavior.discordPresence,
      debug: values.behavior.devmode,
      errorTracking: values.behavior.errorTracking,
      bvhDirectory: values.behavior.bvhDirectory,
    });
  };

  const percentageFormat = Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 0,
  });

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  return (
    <SettingsPageLayout>
      <form
        className="flex flex-col gap-2 w-full"
        // Don't resize the font size for this page because you have access to font resizing on it and we don't want to break the layout just in case
        style={
          {
            '--font-size': '12rem',
            '--font-size-standard': '12rem',
            '--font-size-vr': '16rem',
            '--font-size-title': '25rem',
          } as React.CSSProperties
        }
      >
        <SettingsPagePaneLayout icon={<BellIcon />} id="notifications">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-interface-notifications')}
            </Typography>

            <div className="pt-2">
              <Typography variant="section-title">
                {l10n.getString('settings-general-interface-serial_detection')}
              </Typography>
            </div>

            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-general-interface-serial_detection-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="notifications.watchNewDevices"
                label={l10n.getString(
                  'settings-general-interface-serial_detection-label'
                )}
              />
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-general-interface-feedback_sound')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-general-interface-feedback_sound-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="notifications.feedbackSound"
                label={l10n.getString(
                  'settings-general-interface-feedback_sound-label'
                )}
              />
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <NumberSelector
                control={control}
                name="notifications.feedbackSoundVolume"
                label={l10n.getString(
                  'settings-general-interface-feedback_sound-volume'
                )}
                valueLabelFormat={(value) => percentageFormat.format(value)}
                min={0.1}
                max={1.0}
                step={0.1}
              />
            </div>

            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-interface-connected_trackers_warning'
              )}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-general-interface-connected_trackers_warning-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="notifications.connectedTrackersWarning"
                label={l10n.getString(
                  'settings-general-interface-connected_trackers_warning-label'
                )}
              />
            </div>
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout icon={<ArrowRightLeftIcon />} id="behavior">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-interface-behavior')}
            </Typography>
            <div className="pt-2">
              {isTrayAvailable && (
                <>
                  <Typography variant="section-title">
                    {l10n.getString('settings-general-interface-use_tray')}
                  </Typography>
                  <div className="flex flex-col pt-1 pb-2">
                    <Typography>
                      {l10n.getString(
                        'settings-general-interface-use_tray-description'
                      )}
                    </Typography>
                  </div>
                  <div className="grid sm:grid-cols-2 pb-4">
                    <CheckBox
                      variant="toggle"
                      control={control}
                      outlined
                      name="behavior.useTray"
                      label={l10n.getString(
                        'settings-general-interface-use_tray-label'
                      )}
                    />
                  </div>
                </>
              )}

              <Typography variant="section-title">
                {l10n.getString('settings-general-interface-discord_presence')}
              </Typography>
              <div className="flex flex-col pt-1 pb-2">
                <Typography>
                  {l10n.getString(
                    'settings-general-interface-discord_presence-description'
                  )}
                </Typography>
              </div>
              <div className="grid sm:grid-cols-2 pb-4">
                <CheckBox
                  variant="toggle"
                  control={control}
                  outlined
                  name="behavior.discordPresence"
                  label={l10n.getString(
                    'settings-general-interface-discord_presence-label'
                  )}
                />
              </div>

              <Typography variant="section-title">
                {l10n.getString('settings-general-interface-dev_mode')}
              </Typography>
              <div className="flex flex-col pt-1 pb-2">
                <Typography>
                  {l10n.getString(
                    'settings-general-interface-dev_mode-description'
                  )}
                </Typography>
              </div>
              <div className="grid grid-cols-1 gap-2 pb-4 w-full">
                <div className="">
                  <CheckBox
                    variant="toggle"
                    control={control}
                    outlined
                    name="behavior.devmode"
                    label={l10n.getString(
                      'settings-general-interface-dev_mode-label'
                    )}
                  />
                </div>
                {config?.debug && <DeveloperModeWidget />}
              </div>

              <Typography variant="section-title">
                {l10n.getString('settings-interface-behavior-error_tracking')}
              </Typography>
              <div className="flex flex-col pt-1 pb-2">
                <Localized
                  id={
                    'settings-interface-behavior-error_tracking-description_v2'
                  }
                  elems={{
                    b: <b />,
                  }}
                >
                  <Typography whitespace="whitespace-pre-line" />
                </Localized>
              </div>
              <div className="grid sm:grid-cols-2 pb-4">
                <CheckBox
                  variant="toggle"
                  control={control}
                  outlined
                  name="behavior.errorTracking"
                  label={l10n.getString(
                    'settings-interface-behavior-error_tracking-label'
                  )}
                />
              </div>

              {isTauri() && (
                <>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'settings-interface-behavior-bvh_directory'
                    )}
                  </Typography>
                  <div className="flex flex-col pt-1 pb-2">
                    <Localized
                      id={
                        'settings-interface-behavior-bvh_directory-description'
                      }
                    >
                      <Typography />
                    </Localized>
                  </div>
                  <div className="grid gap-3 pb-5">
                    <TauriFileInput
                      name="behavior.bvhDirectory"
                      rules={{
                        required: false,
                      }}
                      control={control}
                      label="settings-interface-behavior-bvh_directory-label"
                      directory
                    />
                  </div>
                </>
              )}
            </div>
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout icon={<SquaresIcon />} id="appearance">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-interface-appearance')}
            </Typography>
            <div className="pt-2">
              <Typography variant="section-title">
                {l10n.getString('settings-interface-appearance-decorations')}
              </Typography>
            </div>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-interface-appearance-decorations-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="appearance.decorations"
                label={l10n.getString(
                  'settings-interface-appearance-decorations-label'
                )}
              />
            </div>

            <div className="pb-4">
              <Typography variant="section-title">
                {l10n.getString('settings-general-interface-theme')}
              </Typography>
              <div className="flex flex-wrap gap-3 pt-2">
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime'}
                  colors="!bg-slime"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-green'}
                  colors="!bg-slime-green"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-yellow'}
                  colors="!bg-slime-yellow"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-orange'}
                  colors="!bg-slime-orange"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-red'}
                  colors="!bg-slime-red"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'dark'}
                  colors="!bg-dark"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'light'}
                  colors="!bg-light"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'trans'}
                  colors="!bg-trans-flag"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'asexual'}
                  colors="!bg-asexual-flag"
                />
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'snep'}
                  colors="!bg-snep"
                />
              </div>
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-interface-appearance-font')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-interface-appearance-font-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <Dropdown
                control={control}
                name="appearance.fonts"
                placeholder={l10n.getString(
                  'settings-interface-appearance-font-placeholder'
                )}
                /* Supports multiple items by separating them with a comma */
                items={fontOptions.map((option) => ({
                  label: (
                    <span style={{ fontFamily: option.fontName }}>
                      {option.label}
                    </span>
                  ),
                  value: option.value,
                }))}
                alignment="left"
              />
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-interface-appearance-font_size')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-interface-appearance-font_size-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <Range
                control={control}
                name="appearance.textSize"
                min={10}
                max={15}
                step={1}
                values={[
                  { value: 10, label: '10pt' },
                  { value: 11, label: '11pt' },
                  { value: 12, label: '12pt', defaultValue: true },
                  { value: 13, label: '13pt' },
                  { value: 14, label: '14pt' },
                  { value: 15, label: '15pt' },
                ]}
              />
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-general-interface-lang')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString('settings-general-interface-lang-description')}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <LangSelector alignment="left" />
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
