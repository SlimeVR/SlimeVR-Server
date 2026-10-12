import { Localized, useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { CheckBox, CheckboxInternal } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { defaultConfig, useConfig } from '@/hooks/config';
import { ArrowRightLeftIcon } from '@/components/commons/icon/ArrowIcons';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { SystemFileInput } from '@/components/commons/SystemFileInput';
import { useElectron } from '@/hooks/electron';
import { useLocaleConfig } from '@/i18n/config';
import { handleResetSounds } from '@/sounds/sounds';
import {
  ErrorReportingConsent,
  ResetDetail,
  ResetLifecycle,
  ResetType,
} from 'solarxr-protocol';
import { useErrorReporting } from '@/hooks/error-reporting';

interface BehaviorSettingsForm {
  feedbackSound: boolean;
  feedbackSoundVolume: number;
  connectedTrackersWarning: boolean;
  useTray: boolean;
  discordPresence: boolean;
  bvhDirectory: string | null;
  controllerNav: boolean;
}

export function BehaviorSettings() {
  const electron = useElectron();
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { config, setConfig } = useConfig();
  const errorReporting = useErrorReporting();
  const { control, watch, handleSubmit } = useForm<BehaviorSettingsForm>({
    defaultValues: {
      feedbackSound: config?.feedbackSound ?? defaultConfig.feedbackSound,
      feedbackSoundVolume:
        config?.feedbackSoundVolume ?? defaultConfig.feedbackSoundVolume,
      connectedTrackersWarning:
        config?.connectedTrackersWarning ??
        defaultConfig.connectedTrackersWarning,
      useTray: config?.useTray ?? defaultConfig.useTray ?? false,
      discordPresence: config?.discordPresence ?? defaultConfig.discordPresence,
      bvhDirectory: config?.bvhDirectory ?? defaultConfig.bvhDirectory,
      controllerNav: config?.controllerNav ?? defaultConfig.controllerNav,
    },
  });

  const percentageFormat = Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 0,
  });

  const onSubmit = (values: BehaviorSettingsForm) => {
    if (config?.feedbackSoundVolume != values.feedbackSoundVolume) {
      handleResetSounds(values.feedbackSoundVolume, {
        resetType: ResetType.FULL,
        lifecycle: ResetLifecycle.DONE,
        bodyParts: [],
        detailType: ResetDetail.NONE,
        detail: null,
      });
    }

    setConfig({
      feedbackSound: values.feedbackSound,
      feedbackSoundVolume: values.feedbackSoundVolume,
      connectedTrackersWarning: values.connectedTrackersWarning,
      useTray: values.useTray,
      discordPresence: values.discordPresence,
      bvhDirectory: values.bvhDirectory,
      controllerNav: values.controllerNav,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, [onSubmit]);

  return (
    <form className="flex flex-col gap-2 w-full">
      <SettingsPagePaneLayout icon={<ArrowRightLeftIcon />}>
        <>
          <Typography variant="main-title">
            {l10n.getString('settings-interface-behavior')}
          </Typography>
          <div className="pt-2">
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
                name="feedbackSound"
                label={l10n.getString(
                  'settings-general-interface-feedback_sound-label'
                )}
              />
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <NumberSelector
                control={control}
                name="feedbackSoundVolume"
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
                name="connectedTrackersWarning"
                label={l10n.getString(
                  'settings-general-interface-connected_trackers_warning-label'
                )}
              />
            </div>

            {electron.isElectron && (
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
                    name="useTray"
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
                name="discordPresence"
                label={l10n.getString(
                  'settings-general-interface-discord_presence-label'
                )}
              />
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-interface-behavior-controller_nav')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography>
                {l10n.getString(
                  'settings-interface-behavior-controller_nav-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="controllerNav"
                label={l10n.getString(
                  'settings-interface-behavior-controller_nav-label'
                )}
              />
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-interface-behavior-error_tracking')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Localized
                id={'settings-interface-behavior-error_tracking-description_v2'}
                elems={{
                  b: <b />,
                }}
              >
                <Typography whitespace="whitespace-pre-line" />
              </Localized>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckboxInternal
                variant="toggle"
                outlined
                name="errorReporting"
                label={l10n.getString(
                  'settings-interface-behavior-error_tracking-label'
                )}
                disabled={errorReporting.consent === null}
                checked={
                  errorReporting.consent === ErrorReportingConsent.ALLOWED
                }
                onChange={(event) =>
                  errorReporting.answer(
                    event.currentTarget.checked
                      ? ErrorReportingConsent.ALLOWED
                      : ErrorReportingConsent.DENIED
                  )
                }
              />
            </div>

            {electron.isElectron && (
              <>
                <Typography variant="section-title">
                  {l10n.getString('settings-interface-behavior-bvh_directory')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Localized
                    id={'settings-interface-behavior-bvh_directory-description'}
                  >
                    <Typography />
                  </Localized>
                </div>
                <div className="grid gap-3 pb-5">
                  <SystemFileInput
                    name="bvhDirectory"
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
    </form>
  );
}
