import { useEffect } from 'react';
import { FieldPath, FieldPathValue, useForm } from 'react-hook-form';
import { CheckBox, CheckboxInternal } from '@/components/commons/Checkbox';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { SettingRow, SettingsGroup } from '@/components/settings/SettingRow';
import { settingReset } from '@/components/settings/SettingReset';
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
import { ErrorReportingDetails } from '@/components/ErrorReportingDetails';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

interface BehaviorSettingsForm {
  feedbackSound: boolean;
  feedbackSoundVolume: number;
  connectedTrackersWarning: boolean;
  useTray: boolean;
  discordPresence: boolean;
  bvhDirectory: string | null;
  controllerNav: boolean;
}

export const behaviorSettingsPage = defineSettingsPage({
  path: 'behavior',
  title: 'settings-interface-behavior',
  groups: {
    notifications: {
      title: 'settings-interface-notifications',
      rows: {
        feedbackSound: {
          title: 'settings-general-interface-feedback_sound',
          description: 'settings-general-interface-feedback_sound-description',
        },
        feedbackSoundVolume: {
          title: 'settings-general-interface-feedback_sound-volume',
          description:
            'settings-general-interface-feedback_sound-volume-description',
        },
        connectedTrackersWarning: {
          title: 'settings-general-interface-connected_trackers_warning',
          description:
            'settings-general-interface-connected_trackers_warning-description',
        },
      },
    },
    desktop: {
      title: 'settings-interface-behavior-desktop_group',
      rows: {
        useTray: {
          title: 'settings-general-interface-use_tray',
          description: 'settings-general-interface-use_tray-description',
          available: ({ isElectron }) => isElectron,
        },
        discordPresence: {
          title: 'settings-general-interface-discord_presence',
          description:
            'settings-general-interface-discord_presence-description',
        },
        bvhDirectory: {
          title: 'settings-interface-behavior-bvh_directory',
          description: 'settings-interface-behavior-bvh_directory-description',
          available: ({ isElectron }) => isElectron,
        },
      },
    },
    input: {
      title: 'settings-interface-behavior-input_group',
      rows: {
        controllerNav: {
          title: 'settings-interface-behavior-controller_nav',
          description: 'settings-interface-behavior-controller_nav-description',
        },
      },
    },
    privacy: {
      title: 'settings-interface-behavior-privacy_group',
      rows: {
        errorTracking: {
          title: 'settings-interface-behavior-error_tracking-label',
          description: 'settings-interface-behavior-error_tracking-summary',
        },
      },
    },
  },
});

export function BehaviorSettings() {
  const electron = useElectron();
  const { currentLocales } = useLocaleConfig();
  const { config, setConfig } = useConfig();
  const errorReporting = useErrorReporting();
  const { control, watch, handleSubmit, setValue } =
    useForm<BehaviorSettingsForm>({
      defaultValues: {
        feedbackSound: config?.feedbackSound ?? defaultConfig.feedbackSound,
        feedbackSoundVolume:
          config?.feedbackSoundVolume ?? defaultConfig.feedbackSoundVolume,
        connectedTrackersWarning:
          config?.connectedTrackersWarning ??
          defaultConfig.connectedTrackersWarning,
        useTray: config?.useTray ?? defaultConfig.useTray ?? false,
        discordPresence:
          config?.discordPresence ?? defaultConfig.discordPresence,
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

  const resetTo = <P extends FieldPath<BehaviorSettingsForm>>(name: P) =>
    settingReset<BehaviorSettingsForm, P>(
      (name === 'useTray'
        ? (defaultConfig.useTray ?? false)
        : defaultConfig[name]) as FieldPathValue<BehaviorSettingsForm, P>,
      watch(name),
      (next) => setValue(name, next)
    );

  const toggle = (name: keyof BehaviorSettingsForm) => ({
    ...resetTo(name),
    controlId: `behavior-${name}`,
    control: (
      <CheckBox
        variant="toggle"
        control={control}
        name={name}
        id={`behavior-${name}`}
      />
    ),
  });

  const { notifications, desktop, input, privacy } =
    behaviorSettingsPage.groups;

  return (
    <form className="w-full">
      <SettingsPagePaneLayout
        icon={<ArrowRightLeftIcon />}
        title={behaviorSettingsPage.title}
      >
        <div className="flex flex-col">
          <SettingsGroup group={notifications}>
            <SettingRow
              setting={notifications.rows.feedbackSound}
              childrenDisabled={!watch('feedbackSound')}
              {...toggle('feedbackSound')}
            >
              <SettingRow
                setting={notifications.rows.feedbackSoundVolume}
                controlWidth="wide"
                {...resetTo('feedbackSoundVolume')}
                control={
                  <NumberSelector
                    control={control}
                    name="feedbackSoundVolume"
                    valueLabelFormat={(value) => percentageFormat.format(value)}
                    min={0.1}
                    max={1.0}
                    step={0.1}
                  />
                }
              />
            </SettingRow>
            <SettingRow
              setting={notifications.rows.connectedTrackersWarning}
              {...toggle('connectedTrackersWarning')}
            />
          </SettingsGroup>

          <SettingsGroup group={desktop}>
            {electron.isElectron && (
              <SettingRow
                setting={desktop.rows.useTray}
                {...toggle('useTray')}
              />
            )}
            <SettingRow
              setting={desktop.rows.discordPresence}
              {...toggle('discordPresence')}
            />
            {electron.isElectron && (
              <SettingRow
                setting={desktop.rows.bvhDirectory}
                {...resetTo('bvhDirectory')}
                below={
                  <SystemFileInput
                    name="bvhDirectory"
                    rules={{ required: false }}
                    control={control}
                    label="settings-interface-behavior-bvh_directory-label"
                    directory
                    compact
                  />
                }
              />
            )}
          </SettingsGroup>

          <SettingsGroup group={input}>
            <SettingRow
              setting={input.rows.controllerNav}
              {...toggle('controllerNav')}
            />
          </SettingsGroup>

          <SettingsGroup group={privacy}>
            <SettingRow
              setting={privacy.rows.errorTracking}
              below={<ErrorReportingDetails />}
              controlId="behavior-error-reporting"
              control={
                <CheckboxInternal
                  variant="toggle"
                  name="errorReporting"
                  id="behavior-error-reporting"
                  disabled={errorReporting.setting === null}
                  checked={
                    errorReporting.setting === ErrorReportingConsent.ALLOWED
                  }
                  onChange={(event) =>
                    errorReporting.answer(
                      event.currentTarget.checked
                        ? ErrorReportingConsent.ALLOWED
                        : ErrorReportingConsent.DENIED
                    )
                  }
                />
              }
            />
          </SettingsGroup>
        </div>
      </SettingsPagePaneLayout>
    </form>
  );
}
