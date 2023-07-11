import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { CheckBox } from '../../commons/Checkbox';
import { Typography } from '../../commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '../SettingsPageLayout';
import { useConfig } from '../../../hooks/config';
import { ThemeSelector } from '../../commons/ThemeSelector';
import { SquaresIcon } from '../../commons/icon/SquaresIcon';
import { NumberSelector } from '../../commons/NumberSelector';
import { useLocaleConfig } from '../../../i18n/config';
import { LangSelector } from '../../commons/LangSelector';
import { BellIcon } from '../../commons/icon/BellIcon';

interface InterfaceSettingsForm {
  appearance: {
    devmode: boolean;
    theme: string;
    textSize: number;
    dyslexiaFont: boolean;
  };
  notifications: {
    watchNewDevices: boolean;
    feedbackSound: boolean;
    feedbackSoundVolume: number;
  };
}

const defaultValues: InterfaceSettingsForm = {
  appearance: {
    devmode: false,
    theme: 'slime',
    textSize: 12,
    dyslexiaFont: false,
  },
  notifications: {
    watchNewDevices: true,
    feedbackSound: true,
    feedbackSoundVolume: 0.5,
  }
};

export function InterfaceSettings() {
  const { currentLocales } = useLocaleConfig();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { reset, control, watch, handleSubmit } =
    useForm<InterfaceSettingsForm>({
      defaultValues: defaultValues,
    });

  const onSubmit = (values: InterfaceSettingsForm) => {
    setConfig({
      debug: values.appearance.devmode,
      watchNewDevices: values.notifications.watchNewDevices,
      feedbackSound: values.notifications.feedbackSound,
      feedbackSoundVolume: values.notifications.feedbackSoundVolume,
      theme: values.appearance.theme,
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
      <form className="flex flex-col gap-2 w-full">
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
        <SettingsPagePaneLayout
          icon={<BellIcon></BellIcon>}
          id="notifications"
        >
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
      </form>
    </SettingsPageLayout>
  );
}
