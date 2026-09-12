import { Typography } from '@/components/commons/Typography';
import { ReactNode } from 'react';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import {
  avatarMeasurementTypeTranslationMap,
  spineModeTranslationMap,
  trackerModelTranslationMap,
  useVRCConfig,
  VRCConfigStateSupported,
} from '@/hooks/vrc-config';
import { Localized, useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useLocaleConfig } from '@/i18n/config';
import { A } from '@/components/commons/A';
import { Button } from '@/components/commons/Button';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';

function SettingRow({
  name,
  valid,
  value,
  recommendedValue,
  muted,
  mute,
}: {
  name: string;
  recommendedValue: ReactNode;
  value: ReactNode;
  valid: boolean;
  muted: boolean;
  mute: () => void;
}) {
  return (
    <tr className="group border-b border-background-60 last-of-type:border-b-transparent">
      <td className="px-6 py-4 flex gap-2 fill-status-success items-center">
        {valid ? (
          <CheckIcon size={20} />
        ) : (
          <WarningIcon width={20} className="text-status-warning" />
        )}
        <Typography id={name} />
      </td>
      <td className="px-6 py-4 text-end items-center">{recommendedValue}</td>
      <td
        className={classNames(
          'px-6 py-4 text-end items-center',
          !valid && !muted && 'text-status-warning'
        )}
      >
        <Typography
          color={!valid && !muted ? 'text-status-warning' : undefined}
        >
          {value}
        </Typography>
      </td>
      <td
        className={classNames('px-6 py-4 text-end items-end justify-end flex')}
      >
        <Button
          variant="secondary"
          className="min-w-24"
          onClick={mute}
          id={muted ? 'vrc_config-unmute-btn' : 'vrc_config-mute-btn'}
        />
      </td>
    </tr>
  );
}

function Table({ children }: { children: ReactNode }) {
  return (
    <table className="divide-y divide-background-50 bg-background-80 rounded-lg">
      <thead>
        <tr>
          <th scope="col" className="px-6 py-4 text-start">
            <Typography
              id={'vrc_config-setting_name'}
              bold
              variant="section-title"
            />
          </th>

          <th scope="col" className="px-6 py-4 text-end">
            <Typography
              id={'vrc_config-recommended_value'}
              bold
              variant="section-title"
            />
          </th>
          <th scope="col" className="px-6 py-4 text-end">
            <Typography
              id={'vrc_config-current_value'}
              bold
              variant="section-title"
            />
          </th>
          <th scope="col" className="px-6 py-4 text-end">
            <Typography id={'vrc_config-mute'} bold variant="section-title" />
          </th>
        </tr>
      </thead>
      <tbody>{children}</tbody>
    </table>
  );
}

const onOffKey = (value: boolean) =>
  value ? 'vrc_config-on' : 'vrc_config-off';

export function VRCWarningsPage() {
  const { l10n } = useLocalization();
  const { state, toggleMutedSettings } = useVRCConfig();
  const { currentLocales } = useLocaleConfig();

  const meterFormat = Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'meter',
    maximumFractionDigits: 2,
  });

  if (!state || !state.isSupported) {
    return <></>;
  }

  const settingRowProps = (key: keyof VRCConfigStateSupported['validity']) => ({
    mute: () => toggleMutedSettings(key),
    muted: state.muted.includes(key),
    valid: state.validity[key] == true,
  });

  return (
    <SettingsPageLayout>
      <SettingsPagePaneLayout
        icon={<WarningIcon />}
        id="vrc-warnings"
        className="w-full min-w-fit"
      >
        <div className="flex flex-col gap-3">
          <Typography variant="main-title" id={'vrc_config-page-title'} />
          <Typography variant="standard" id={'vrc_config-page-desc'} />
        </div>
        <div className="w-full mt-4 gap-2 flex flex-col">
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-2">
              <Typography
                variant="section-title"
                id="vrc_config-page-big_menu"
              />
              <Typography id="vrc_config-page-big_menu-desc" />
              <Table>
                <SettingRow
                  {...settingRowProps('userHeightOk')}
                  name="vrc_config-user_height"
                  recommendedValue={meterFormat.format(
                    state.recommended.userHeight
                  )}
                  value={meterFormat.format(state.state.userHeight)}
                />
                <SettingRow
                  {...settingRowProps('legacyModeOk')}
                  name="vrc_config-legacy_mode"
                  recommendedValue={
                    <Localized id={onOffKey(state.recommended.legacyMode)} />
                  }
                  value={<Localized id={onOffKey(state.state.legacyMode)} />}
                />
                <SettingRow
                  {...settingRowProps('shoulderTrackingOk')}
                  name="vrc_config-disable_shoulder_tracking"
                  recommendedValue={
                    <Localized
                      id={onOffKey(state.recommended.shoulderTrackingDisabled)}
                    />
                  }
                  value={
                    <Localized
                      id={onOffKey(state.state.shoulderTrackingDisabled)}
                    />
                  }
                />
                <SettingRow
                  {...settingRowProps('shoulderWidthCompensationOk')}
                  name="vrc_config-shoulder_width_compensation"
                  recommendedValue={
                    <Localized
                      id={onOffKey(state.recommended.shoulderWidthCompensation)}
                    />
                  }
                  value={
                    <Localized
                      id={onOffKey(state.state.shoulderWidthCompensation)}
                    />
                  }
                />
                <SettingRow
                  {...settingRowProps('calibrationVisualsOk')}
                  name="vrc_config-calibration_visuals"
                  recommendedValue={
                    <Localized
                      id={onOffKey(state.recommended.calibrationVisuals)}
                    />
                  }
                  value={
                    <Localized id={onOffKey(state.state.calibrationVisuals)} />
                  }
                />
                <SettingRow
                  {...settingRowProps('calibrationRangeOk')}
                  name="vrc_config-calibration_range"
                  recommendedValue={meterFormat.format(
                    state.recommended.calibrationRange
                  )}
                  value={meterFormat.format(state.state.calibrationRange)}
                />
                <SettingRow
                  {...settingRowProps('trackerModelOk')}
                  name="vrc_config-tracker_model"
                  recommendedValue={
                    <Localized
                      id={
                        trackerModelTranslationMap[
                          state.recommended.trackerModel
                        ]
                      }
                    />
                  }
                  value={
                    <Localized
                      id={trackerModelTranslationMap[state.state.trackerModel]}
                    />
                  }
                />
              </Table>
            </div>
            <div className="flex flex-col gap-2">
              <Typography
                id="vrc_config-page-wrist_menu"
                variant="section-title"
              />
              <Typography id="vrc_config-page-wrist_menu-desc" />
              <Table>
                <SettingRow
                  {...settingRowProps('spineModeOk')}
                  name="vrc_config-spine_mode"
                  recommendedValue={state.recommended.spineMode
                    .map((mode) =>
                      l10n.getString(spineModeTranslationMap[mode])
                    )
                    .join(', ')}
                  value={
                    <Localized
                      id={spineModeTranslationMap[state.state.spineMode]}
                    />
                  }
                />

                <SettingRow
                  {...settingRowProps('avatarMeasurementTypeOk')}
                  name="vrc_config-avatar_measurement_type"
                  recommendedValue={
                    <Localized
                      id={
                        avatarMeasurementTypeTranslationMap[
                          state.recommended.avatarMeasurementType
                        ]
                      }
                    />
                  }
                  value={
                    <Localized
                      id={
                        avatarMeasurementTypeTranslationMap[
                          state.state.avatarMeasurementType
                        ]
                      }
                    />
                  }
                />
              </Table>
            </div>
          </div>
        </div>
        <div className="flex flex-col gap-2 mt-4">
          <Typography variant="section-title" id={'vrc_config-page-help'} />
          <Typography
            id={'vrc_config-page-help-desc'}
            elems={{
              a: <A href="https://docs.slimevr.dev/tools/vrchat-config.html" />,
            }}
          />
        </div>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
