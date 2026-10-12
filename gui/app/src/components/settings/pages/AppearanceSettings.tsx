import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { defaultConfig, useConfig } from '@/hooks/config';
import { ThemeSelector } from '@/components/commons/ThemeSelector';
import { SquaresIcon } from '@/components/commons/icon/SquaresIcon';
import { useLocaleConfig } from '@/i18n/config';
import { LangSelector } from '@/components/commons/LangSelector';
import { Button } from '@/components/commons/Button';
import { Range } from '@/components/commons/Range';
import { Dropdown } from '@/components/commons/Dropdown';
import { useElectron } from '@/hooks/electron';

interface AppearanceSettingsForm {
  theme: string;
  textSize: number;
  fonts: string;
}

export function AppearanceSettings() {
  const electron = useElectron();
  const { inContext, inContextFailed, setInContext } = useLocaleConfig();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit } = useForm<AppearanceSettingsForm>({
    defaultValues: {
      theme: config?.theme ?? defaultConfig.theme,
      textSize: config?.textSize ?? defaultConfig.textSize,
      fonts: config?.fonts.join(',') ?? defaultConfig.fonts.join(','),
    },
  });

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

  return (
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
      <SettingsPagePaneLayout icon={<SquaresIcon />}>
        <>
          <Typography variant="main-title">
            {l10n.getString('settings-interface-appearance')}
          </Typography>

          <div className="pb-4">
            <Typography variant="section-title">
              {l10n.getString('settings-general-interface-theme')}
            </Typography>
            <div className="flex flex-wrap gap-3 pt-2">
              <ThemeSelector
                control={control}
                name="theme"
                value={'slime'}
                colors="!bg-slime"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'slime-green'}
                colors="!bg-slime-green"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'slime-yellow'}
                colors="!bg-slime-yellow"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'slime-orange'}
                colors="!bg-slime-orange"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'slime-red'}
                colors="!bg-slime-red"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'dark'}
                colors="!bg-dark"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'light'}
                colors="!bg-light"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'trans'}
                colors="!bg-trans-flag"
              />
              <ThemeSelector
                control={control}
                name="theme"
                value={'asexual'}
                colors="!bg-asexual-flag"
              />
              <ThemeSelector
                control={control}
                name="theme"
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
              {l10n.getString('settings-interface-appearance-font-description')}
            </Typography>
          </div>
          <div className="grid sm:grid-cols-2 pb-4">
            <Dropdown
              control={control}
              name="fonts"
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
              name="textSize"
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

          {electron.isElectron && (
            <>
              <Typography
                variant="section-title"
                id="settings-general-interface-crowdin_in_context"
              />
              <div className="flex flex-col pt-1 pb-2">
                <Typography
                  id={
                    inContext
                      ? 'settings-general-interface-crowdin_in_context-description-active'
                      : 'settings-general-interface-crowdin_in_context-description'
                  }
                />
                {inContextFailed && (
                  <Typography
                    color="text-status-critical"
                    id="settings-general-interface-crowdin_in_context-failed"
                  />
                )}
              </div>
              <div className="grid sm:grid-cols-2 pb-4">
                <Button
                  variant={inContext ? 'tertiary' : 'secondary'}
                  id={
                    inContext
                      ? 'settings-general-interface-crowdin_in_context-close'
                      : 'settings-general-interface-crowdin_in_context-enable'
                  }
                  onClick={() => setInContext(!inContext)}
                />
              </div>
            </>
          )}
        </>
      </SettingsPagePaneLayout>
    </form>
  );
}
