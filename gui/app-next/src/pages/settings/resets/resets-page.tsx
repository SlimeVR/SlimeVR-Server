import { ArmsMountingResetMode, MountingMethod } from 'solarxr-protocol';
import { settingRows } from '@/domain/settings/setting-field';
import { formats } from '@/lib/format';
import { Localized } from '@/ui/localized';
import { NumberField } from '@/ui/number-field';
import { type ChoiceOption, RadioGroup } from '@/ui/radio-group';
import { SettingRow } from '@/ui/setting-row';
import { SettingsAdvanced, SettingsGroup } from '@/ui/settings-group';
import { Toggle } from '@/ui/toggle';
import { SettingsPane } from '../settings-pane';
import { resetsSettingsPage } from './resets-def';
import { resetsDefaults, resetsSettings } from './resets-settings';
import { TapDetection } from './tap-detection';

const { main, resetBehaviour } = resetsSettingsPage.groups;

const mountingMethodOptions: ChoiceOption<MountingMethod>[] = [
  {
    value: MountingMethod.STEP,
    label: 'mounting_method-step',
    description: 'mounting_method-step-description',
  },
  {
    value: MountingMethod.POSE,
    label: 'mounting_method-pose',
    description: 'mounting_method-pose-description',
  },
  {
    value: MountingMethod.MANUAL,
    label: 'mounting_method-manual',
    description: 'mounting_method-manual-description',
  },
];

const armsModeOptions: ChoiceOption<ArmsMountingResetMode>[] = [
  {
    value: ArmsMountingResetMode.BACK,
    label: 'settings-general-fk_settings-arms_mounting_reset_mode-back',
    description:
      'settings-general-fk_settings-arms_mounting_reset_mode-back-description',
  },
  {
    value: ArmsMountingResetMode.FORWARD,
    label: 'settings-general-fk_settings-arms_mounting_reset_mode-forward',
    description:
      'settings-general-fk_settings-arms_mounting_reset_mode-forward-description',
  },
  {
    value: ArmsMountingResetMode.SIDE,
    label: 'settings-general-fk_settings-arms_mounting_reset_mode-t_pose',
    description:
      'settings-general-fk_settings-arms_mounting_reset_mode-t_pose-description',
  },
];

const resetsRow = settingRows(resetsSettings, resetsDefaults);

export function ResetsPage() {
  const method = resetsRow(main.rows.mountingMethod);
  const armsMode = resetsRow(main.rows.armsMountingResetMode);
  const mountingFeet = resetsRow(main.rows.resetMountingFeet);
  const smoothTime = resetsRow(resetBehaviour.rows.yawResetSmoothTime);
  const hmdAttitude = resetsRow(resetBehaviour.rows.resetHmdAttitude);
  const saveMounting = resetsRow(resetBehaviour.rows.saveMountingReset);

  return (
    <SettingsPane page={resetsSettingsPage}>
      <SettingsGroup group={main}>
        <SettingRow
          bind={method}
          controlPlacement="below"
          childrenDisabled={method.field.value !== MountingMethod.POSE}
          control={(f) => (
            <RadioGroup
              {...f}
              options={mountingMethodOptions}
              class="grid gap-sm @lg:grid-cols-3"
            />
          )}
        >
          <SettingRow
            bind={armsMode}
            controlPlacement="below"
            control={(f) => (
              <RadioGroup
                {...f}
                options={armsModeOptions}
                class="grid gap-sm @lg:grid-cols-3"
              />
            )}
          />
          <SettingRow
            bind={mountingFeet}
            pressable
            control={(f) => <Toggle {...f} />}
          />
        </SettingRow>
      </SettingsGroup>

      <TapDetection />

      <SettingsAdvanced>
        <SettingsGroup group={resetBehaviour}>
          <SettingRow
            bind={smoothTime}
            control={(f) => (
              <NumberField
                {...f}
                min={0}
                max={0.5}
                step={0.05}
                format={formats.seconds}
              />
            )}
          />
          <SettingRow
            bind={hmdAttitude}
            pressable
            control={(f) => <Toggle {...f} />}
          />
          <SettingRow
            bind={saveMounting}
            pressable
            details={
              <p>
                <Localized
                  id="settings-general-tracker_mechanics-save_mounting_reset-description"
                  elems={{ b: (text) => <b>{text}</b> }}
                />
              </p>
            }
            control={(f) => <Toggle {...f} />}
          />
        </SettingsGroup>
      </SettingsAdvanced>
    </SettingsPane>
  );
}
