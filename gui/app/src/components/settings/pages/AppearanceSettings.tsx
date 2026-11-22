import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { SettingRow, SettingsGroup } from '@/components/settings/SettingRow';
import { settingReset } from '@/components/settings/SettingReset';
import { defaultConfig, useConfig } from '@/hooks/config';
import { ThemeSelector } from '@/components/commons/ThemeSelector';
import { SquaresIcon } from '@/components/commons/icon/SquaresIcon';
import { useLocaleConfig } from '@/i18n/config';
import { LangSelector } from '@/components/commons/LangSelector';
import { Button } from '@/components/commons/Button';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Dropdown } from '@/components/commons/Dropdown';
import { useElectron } from '@/hooks/electron';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

interface AppearanceSettingsForm {
  theme: string;
  textSize: number;
  fonts: string;
}

export const appearanceSettingsPage = defineSettingsPage({
  path: 'appearance',
  title: 'settings-interface-appearance',
  groups: {
    main: {
      rows: {
        theme: {
          title: 'settings-general-interface-theme',
          description: 'settings-general-interface-theme-description',
        },
      },
    },
    text: {
      title: 'settings-interface-appearance-text_group',
      rows: {
        font: {
          title: 'settings-interface-appearance-font',
          description: 'settings-interface-appearance-font-description',
        },
        fontSize: {
          title: 'settings-interface-appearance-font_size',
          description: 'settings-interface-appearance-font_size-description',
        },
      },
    },
    language: {
      title: 'settings-interface-appearance-language_group',
      rows: {
        lang: {
          title: 'settings-general-interface-lang',
          description: 'settings-general-interface-lang-description',
        },
        crowdinInContext: {
          title: 'settings-general-interface-crowdin_in_context',
          description:
            'settings-general-interface-crowdin_in_context-description',
          available: ({ isElectron }) => isElectron,
        },
      },
    },
  },
});

const { groups } = appearanceSettingsPage;

export function AppearanceSettings() {
  const electron = useElectron();
  const { inContext, inContextFailed, setInContext } = useLocaleConfig();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit, setValue } =
    useForm<AppearanceSettingsForm>({
      defaultValues: {
        theme: config?.theme ?? defaultConfig.theme,
        textSize: config?.textSize ?? defaultConfig.textSize,
        fonts: config?.fonts.join(',') ?? defaultConfig.fonts.join(','),
      },
    });

  const resetTheme = settingReset<AppearanceSettingsForm, 'theme'>(
    defaultConfig.theme,
    watch('theme'),
    (next) => setValue('theme', next)
  );
  const resetFonts = settingReset<AppearanceSettingsForm, 'fonts'>(
    defaultConfig.fonts.join(','),
    watch('fonts'),
    (next) => setValue('fonts', next)
  );
  const resetTextSize = settingReset<AppearanceSettingsForm, 'textSize'>(
    defaultConfig.textSize,
    watch('textSize'),
    (next) => setValue('textSize', next)
  );

  const fontOptions = [
    {
      label: l10n.getString('settings-interface-appearance-font-slime_font'),
      value: 'poppins',
      fontName: 'poppins, Noto Sans',
    },
    {
      label: 'OpenDyslexic',
      value: 'OpenDyslexic',
      fontName: 'OpenDyslexic',
    },
    { label: 'Lexend', value: 'Lexend', fontName: 'Lexend' },
    { label: 'Ubuntu', value: 'Ubuntu', fontName: 'Ubuntu' },
    {
      label: 'Noto Sans',
      value: 'Noto Sans',
      fontName: 'Noto Sans',
    },
    {
      label: l10n.getString('settings-interface-appearance-font-os_font'),
      value: 'ui-sans-serif',
    },
  ];

  const onSubmit = (values: AppearanceSettingsForm) => {
    setConfig({
      theme: values.theme,
      fonts: values.fonts.split(','),
      textSize: values.textSize,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, [onSubmit]);

  const themes = [
    { value: 'slime', colors: '!bg-slime' },
    { value: 'space', colors: '!bg-space' },
    { value: 'midnight', colors: '!bg-midnight' },
    { value: 'light', colors: '!bg-light' },
    { value: 'fire', colors: '!bg-fire' },
    { value: 'citrus', colors: '!bg-citrus' },
    { value: 'trans', colors: '!bg-trans-flag' },
    { value: 'asexual', colors: '!bg-asexual-flag' },
    { value: 'snep', colors: '!bg-snep' },
    { value: 'fox', colors: '!bg-fox' },
  ];

  return (
    <form
      className="w-full"
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
      <SettingsPagePaneLayout
        icon={<SquaresIcon />}
        title={appearanceSettingsPage.title}
      >
        <div className="flex flex-col">
          <SettingsGroup group={groups.main}>
            <SettingRow
              setting={groups.main.rows.theme}
              {...resetTheme}
              below={
                <div className="flex flex-wrap gap-3">
                  {themes.map(({ value, colors }) => (
                    <ThemeSelector
                      key={value}
                      control={control}
                      name="theme"
                      value={value}
                      colors={colors}
                    />
                  ))}
                </div>
              }
            />
          </SettingsGroup>

          <SettingsGroup group={groups.text}>
            <SettingRow
              setting={groups.text.rows.font}
              controlWidth="wide"
              {...resetFonts}
              control={
                <Dropdown
                  control={control}
                  name="fonts"
                  placeholder={l10n.getString(
                    'settings-interface-appearance-font-placeholder'
                  )}
                  /* Supports multiple items by separating them with a comma */
                  display="block"
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
              }
            />
            <SettingRow
              setting={groups.text.rows.fontSize}
              controlWidth="wide"
              {...resetTextSize}
              control={
                <NumberSelector
                  control={control}
                  name="textSize"
                  valueLabelFormat={(value) => `${Math.round(value)}pt`}
                  min={10}
                  max={15}
                  step={1}
                />
              }
            />
          </SettingsGroup>

          <SettingsGroup group={groups.language}>
            <SettingRow
              setting={groups.language.rows.lang}
              controlWidth="wide"
              control={<LangSelector alignment="left" display="block" />}
            />
            {electron.isElectron && (
              <SettingRow
                setting={groups.language.rows.crowdinInContext}
                description={
                  inContext
                    ? 'settings-general-interface-crowdin_in_context-description-active'
                    : undefined
                }
                details={
                  inContextFailed && (
                    <Typography
                      color="text-status-critical"
                      id="settings-general-interface-crowdin_in_context-failed"
                    />
                  )
                }
                control={
                  <Button
                    variant={inContext ? 'tertiary' : 'secondary'}
                    className="whitespace-nowrap"
                    id={
                      inContext
                        ? 'settings-general-interface-crowdin_in_context-close'
                        : 'settings-general-interface-crowdin_in_context-enable'
                    }
                    onClick={() => setInContext(!inContext)}
                  />
                }
              />
            )}
          </SettingsGroup>
        </div>
      </SettingsPagePaneLayout>
    </form>
  );
}
