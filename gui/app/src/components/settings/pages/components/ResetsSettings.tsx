import { useLocalization, Localized } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useResetsSettings } from '@/hooks/resets-settings';
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox } from '@/components/commons/Checkbox';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { MountingMethodRadio } from '@/components/mounting/MountingMethodRadio';
import { ArmsMountingResetMode, MountingMethod } from 'solarxr-protocol';

type ResetsSettingsForm = {
  resetMountingFeet: boolean;
  armsMountingResetMode: ArmsMountingResetMode;
  yawResetSmoothTime: number;
  saveMountingReset: boolean;
  resetReliableReferenceAttitude: boolean;
};

const defaultValues: ResetsSettingsForm = {
  resetMountingFeet: false,
  armsMountingResetMode: ArmsMountingResetMode.BACK,
  yawResetSmoothTime: 0.0,
  saveMountingReset: false,
  resetReliableReferenceAttitude: false,
};

export function ResetsSettings() {
  const { resetsSettings: settings, setResetsSettings } = useResetsSettings();
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const isPoseMounting = settings?.mountingMethod === MountingMethod.POSE;

  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const { control, watch, handleSubmit, getValues, reset } =
    useForm<ResetsSettingsForm>({
      defaultValues,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: ResetsSettingsForm) => {
    setResetsSettings({
      resetMountingFeet: values.resetMountingFeet,
      armsMountingResetMode: values.armsMountingResetMode,
      yawResetSmoothTime: values.yawResetSmoothTime,
      saveMountingReset: values.saveMountingReset,
      resetReliableReferenceAttitude: values.resetReliableReferenceAttitude,
    });
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    if (!settings) return;
    reset({ ...getValues(), ...settings });
  }, [settings]);

  return (
    <>
      <div className="flex flex-col pt-5 gap-1">
        <Typography
          variant="section-title"
          id="settings-general-mounting_method"
        />
        <Typography id="settings-general-mounting_method-description" />
        <MountingMethodRadio />
      </div>

      {isPoseMounting && (
        <>
          <div className="flex flex-col pt-5 pb-2 gap-1">
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-fk_settings-arms_mounting_reset_mode'
              )}
            </Typography>

            <Typography>
              {l10n.getString(
                'settings-general-fk_settings-arms_mounting_reset_mode-description'
              )}
            </Typography>

            <div className="grid flex-col gap-2">
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
          </div>

          <div className="flex flex-col gap-1 pt-5">
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1'
              )}
            </Typography>

            <Typography>
              {l10n.getString(
                'settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1'
              )}
            </Typography>

            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="resetMountingFeet"
              label={l10n.getString(
                'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1'
              )}
            />
          </div>
        </>
      )}

      <div className="flex flex-col pt-5 gap-1">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-tracker_mechanics-yaw-reset-smooth-time'
          )}
        </Typography>

        <Typography>
          {l10n.getString(
            'settings-general-tracker_mechanics-yaw-reset-smooth-time-description'
          )}
        </Typography>

        <NumberSelector
          control={control}
          name="yawResetSmoothTime"
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0.0}
          max={0.5}
          step={0.05}
        />
      </div>

      <div className="flex flex-col pt-5 gap-1">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude'
          )}
        </Typography>

        <Typography>
          {l10n.getString(
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude-description'
          )}
        </Typography>

        <CheckBox
          variant="toggle"
          outlined
          control={control}
          name="resetReliableReferenceAttitude"
          label={l10n.getString(
            'settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude'
          )}
        />
      </div>

      <div className="flex flex-col pt-5 gap-1">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-tracker_mechanics-save_mounting_reset'
          )}
        </Typography>

        <Localized
          id="settings-general-tracker_mechanics-save_mounting_reset-description"
          elems={{ b: <b /> }}
        >
          <Typography />
        </Localized>

        <CheckBox
          variant="toggle"
          outlined
          control={control}
          name="saveMountingReset"
          label={l10n.getString(
            'settings-general-tracker_mechanics-save_mounting_reset-enabled-label'
          )}
        />
      </div>
    </>
  );
}
