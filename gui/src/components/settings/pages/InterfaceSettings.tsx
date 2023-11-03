import { useLocalization } from '@fluent/react';
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
  };
}

export function InterfaceSettings() {
  const { currentLocales } = useLocaleConfig();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit } = useForm<InterfaceSettingsForm>({
    defaultValues: {
      appearance: {
        devmode: config?.debug ?? defaultConfig.debug,
        theme: config?.theme ?? defaultConfig.theme,
        textSize: config?.textSize ?? defaultConfig.textSize,
        fonts: config?.fonts.join(',') ?? defaultConfig.fonts.join(','),
      },
      notifications: {
        watchNewDevices:
          config?.watchNewDevices ?? defaultConfig.watchNewDevices,
        feedbackSound: config?.feedbackSound ?? defaultConfig.feedbackSound,
        feedbackSoundVolume:
          config?.feedbackSoundVolume ?? defaultConfig.feedbackSoundVolume,
      },
    },
  });

  const onSubmit = (values: InterfaceSettingsForm) => {
    setConfig({
      debug: values.appearance.devmode,
      watchNewDevices: values.notifications.watchNewDevices,
      feedbackSound: values.notifications.feedbackSound,
      feedbackSoundVolume: values.notifications.feedbackSoundVolume,
      theme: values.appearance.theme,
      fonts: values.appearance.fonts.split(','),
      textSize: values.appearance.textSize,
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
        style={
          {
            '--font-size': '12rem',
            '--font-size-standard': '12rem',
            '--font-size-vr': '16rem',
            '--font-size-title': '25rem',
          } as React.CSSProperties
        }
      >
        <SettingsPagePaneLayout icon={<BellIcon></BellIcon>} id="notifications">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-interface-notifications')}
            </Typography>

            <Typography bold>
              {l10n.getString('settings-general-interface-serial_detection')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
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

            <Typography bold>
              {l10n.getString('settings-general-interface-feedback_sound')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
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
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout
          icon={<SquaresIcon></SquaresIcon>}
          id="appearance"
        >
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-interface-appearance')}
            </Typography>

            <Typography bold>
              {l10n.getString('settings-general-interface-dev_mode')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-interface-dev_mode-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="appearance.devmode"
                label={l10n.getString(
                  'settings-general-interface-dev_mode-label'
                )}
              />
            </div>

            <div className="pb-4">
              <Typography bold>
                {l10n.getString('settings-general-interface-theme')}
              </Typography>
              <div className="flex flex-wrap gap-3 pt-2">
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime'}
                  colors="!bg-slime"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-green'}
                  colors="!bg-slime-green"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-yellow'}
                  colors="!bg-slime-yellow"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-orange'}
                  colors="!bg-slime-orange"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'slime-red'}
                  colors="!bg-slime-red"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'dark'}
                  colors="!bg-dark"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'light'}
                  colors="!bg-light"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="appearance.theme"
                  value={'trans'}
                  colors="!bg-trans-flag"
                ></ThemeSelector>
              </div>
            </div>

            <Typography bold>
              {l10n.getString('settings-interface-appearance-font')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
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
                items={[
                  {
                    label: l10n.getString(
                      'settings-interface-appearance-font-slime_font'
                    ),
                    value: 'poppins',
                    fontName: 'poppins',
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
                    label: l10n.getString(
                      'settings-interface-appearance-font-os_font'
                    ),
                    value: 'ui-sans-serif',
                  },
                ]}
                alignment="left"
              />
            </div>

            <Typography bold>
              {l10n.getString('settings-interface-appearance-font_size')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
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

            <Typography bold>
              {l10n.getString('settings-general-interface-lang')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
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
