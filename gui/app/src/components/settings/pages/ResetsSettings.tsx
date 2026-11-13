import { useLocalization, Localized } from '@fluent/react';
import { useEffect } from 'react';
import { FieldPath, FieldPathValue, useForm } from 'react-hook-form';
import { useResetsSettings } from '@/hooks/resets-settings';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox } from '@/components/commons/Checkbox';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { MountingMethodRadio } from '@/components/mounting/MountingMethodRadio';
import {
  ArmsMountingResetMode,
  MountingMethod,
  ResetsSettingsResponseT,
} from 'solarxr-protocol';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { FullResetIcon } from '@/components/commons/icon/ResetIcon';
import { settingReset } from '@/components/settings/SettingReset';
import { TapDetectionSettings } from './components/TapDetectionSettings';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

type ResetsSettingsForm = {
  resetMountingFeet: boolean;
  armsMountingResetMode: ArmsMountingResetMode;
  yawResetSmoothTime: number;
  saveMountingReset: boolean;
  resetHmdAttitude: boolean;
};

function toForm(settings: ResetsSettingsResponseT): ResetsSettingsForm {
  return {
    resetMountingFeet: settings.resetMountingFeet,
    armsMountingResetMode: settings.armsMountingResetMode,
    yawResetSmoothTime: settings.yawResetSmoothTime,
    saveMountingReset: settings.saveMountingReset,
    resetHmdAttitude: settings.resetHmdAttitude,
  };
}

export const resetsSettingsPage = defineSettingsPage({
  path: 'resets',
  title: 'settings-sidebar-resets',
  groups: {
    main: {
      rows: {
        mountingMethod: {
          title: 'settings-general-mounting_method',
          description: 'settings-general-mounting_method-description',
        },
        armsMountingResetMode: {
          title: 'settings-general-fk_settings-arms_mounting_reset_mode',
          description:
            'settings-general-fk_settings-arms_mounting_reset_mode-description',
        },
        resetMountingFeet: {
          title: 'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1',
          description:
            'settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1',
        },
      },
    },
    resetBehaviour: {
      title: 'settings-general-fk_settings-reset_behaviour_group',
      rows: {
        yawResetSmoothTime: {
          title: 'settings-general-tracker_mechanics-yaw-reset-smooth-time',
          description:
            'settings-general-tracker_mechanics-yaw-reset-smooth-time-description',
        },
        resetHmdAttitude: {
          title:
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude',
          description:
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude-description',
        },
        saveMountingReset: {
          title: 'settings-general-tracker_mechanics-save_mounting_reset',
        },
      },
    },
    tapResets: {
      title: 'settings-general-gesture_control-subtitle',
      description: 'settings-general-gesture_control-description',
      rows: {
        yawReset: {
          title: 'settings-general-gesture_control-yawResetEnabled',
        },
        fullReset: {
          title: 'settings-general-gesture_control-fullResetEnabled',
        },
        mountingReset: {
          title: 'settings-general-gesture_control-mountingResetEnabled',
        },
      },
    },
    tapThreshold: {
      rows: {
        numberTrackersOverThreshold: {
          title: 'settings-general-gesture_control-numberTrackersOverThreshold',
          description:
            'settings-general-gesture_control-numberTrackersOverThreshold-description',
        },
      },
    },
  },
});

const { groups } = resetsSettingsPage;

export function ResetsSettings() {
  const { resetsSettings: settings, setResetsSettings } = useResetsSettings();
  const defaults = useSettingsDefaults().resets;
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const isPoseMounting = settings?.mountingMethod === MountingMethod.POSE;

  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const { control, watch, handleSubmit, reset, setValue } =
    useForm<ResetsSettingsForm>({
      defaultValues: toForm(defaults),
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: ResetsSettingsForm) => {
    setResetsSettings({
      resetMountingFeet: values.resetMountingFeet,
      armsMountingResetMode: values.armsMountingResetMode,
      yawResetSmoothTime: values.yawResetSmoothTime,
      saveMountingReset: values.saveMountingReset,
      resetHmdAttitude: values.resetHmdAttitude,
    });
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  const resetTo = <P extends FieldPath<ResetsSettingsForm>>(
    name: P,
    value: FieldPathValue<ResetsSettingsForm, P> | undefined
  ) =>
    settingReset<ResetsSettingsForm, P>(value, watch(name), (next) => {
      setValue(name, next);
      handleSubmit(onSubmit)();
    });

  const resetMountingMethod = {
    reset: {
      modified:
        settings !== null &&
        settings.mountingMethod !== defaults.mountingMethod,
      onReset: () =>
        setResetsSettings({ mountingMethod: defaults.mountingMethod }),
    },
  };

  useEffect(() => {
    if (!settings) return;
    reset(toForm(settings));
  }, [settings]);

  return (
    <SettingsPagePaneLayout
      icon={<FullResetIcon width={20} />}
      title={resetsSettingsPage.title}
    >
      <div className="flex flex-col">
        <SettingsGroup group={groups.main}>
          <SettingRow
            setting={groups.main.rows.mountingMethod}
            below={<MountingMethodRadio />}
            {...resetMountingMethod}
            childrenDisabled={!isPoseMounting}
          >
            <>
              <SettingRow
                setting={groups.main.rows.armsMountingResetMode}
                {...resetTo(
                  'armsMountingResetMode',
                  defaults.armsMountingResetMode
                )}
                below={
                  <div className="grid md:grid-cols-3 gap-3">
                    <Radio
                      control={control}
                      name="armsMountingResetMode"
                      label={l10n.getString(
                        'settings-general-fk_settings-arms_mounting_reset_mode-back'
                      )}
                      description={l10n.getString(
                        'settings-general-fk_settings-arms_mounting_reset_mode-back-description'
                      )}
                      value={ArmsMountingResetMode.BACK}
                    />
                    <Radio
                      control={control}
                      name="armsMountingResetMode"
                      label={l10n.getString(
                        'settings-general-fk_settings-arms_mounting_reset_mode-forward'
                      )}
                      description={l10n.getString(
                        'settings-general-fk_settings-arms_mounting_reset_mode-forward-description'
                      )}
                      value={ArmsMountingResetMode.FORWARD}
                    />
                    <Radio
                      control={control}
                      name="armsMountingResetMode"
                      label={l10n.getString(
                        'settings-general-fk_settings-arms_mounting_reset_mode-t_pose'
                      )}
                      description={l10n.getString(
                        'settings-general-fk_settings-arms_mounting_reset_mode-t_pose-description'
                      )}
                      value={ArmsMountingResetMode.SIDE}
                    />
                  </div>
                }
              />
              <SettingRow
                setting={groups.main.rows.resetMountingFeet}
                controlId="resets-mounting-feet"
                {...resetTo('resetMountingFeet', defaults.resetMountingFeet)}
                control={
                  <CheckBox
                    variant="toggle"
                    control={control}
                    name="resetMountingFeet"
                    id="resets-mounting-feet"
                  />
                }
              />
            </>
          </SettingRow>
        </SettingsGroup>

        <TapDetectionSettings
          resets={groups.tapResets}
          threshold={groups.tapThreshold}
        />

        <SettingsAdvanced>
          <SettingsGroup group={groups.resetBehaviour}>
            <SettingRow
              setting={groups.resetBehaviour.rows.yawResetSmoothTime}
              controlWidth="wide"
              {...resetTo('yawResetSmoothTime', defaults.yawResetSmoothTime)}
              control={
                <NumberSelector
                  control={control}
                  name="yawResetSmoothTime"
                  valueLabelFormat={(value) => secondsFormat.format(value)}
                  min={0.0}
                  max={0.5}
                  step={0.05}
                />
              }
            />
            <SettingRow
              setting={groups.resetBehaviour.rows.resetHmdAttitude}
              controlId="resets-hmd-attitude"
              {...resetTo('resetHmdAttitude', defaults.resetHmdAttitude)}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="resetHmdAttitude"
                  id="resets-hmd-attitude"
                />
              }
            />
            <SettingRow
              setting={groups.resetBehaviour.rows.saveMountingReset}
              details={
                <Localized
                  id="settings-general-tracker_mechanics-save_mounting_reset-description"
                  elems={{ b: <b /> }}
                >
                  <Typography />
                </Localized>
              }
              controlId="resets-save-mounting"
              {...resetTo('saveMountingReset', defaults.saveMountingReset)}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="saveMountingReset"
                  id="resets-save-mounting"
                />
              }
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
