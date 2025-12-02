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
    <tr className="group border-b border-background-60">
      <td className="px-6 py-4 flex gap-2 fill-status-success items-center">
        {valid ? (
          <CheckIcon size={20} />
        ) : (
          <WarningIcon width={20} className="text-status-warning" />
        )}
        <Localized id={name}>
          <Typography>{name}</Typography>
        </Localized>
      </td>
      <td className="px-6 py-4 text-end items-center">{recommendedValue}</td>
      <td
        className={classNames(
          'px-6 py-4 text-end items-center',
          !valid && !muted && 'text-status-warning'
        )}
      >
        {value}
      </td>
      <td
        className={classNames('px-6 py-4 text-end items-end justify-end flex')}
      >
        <Localized id={muted ? 'vrc_config-unmute-btn' : 'vrc_config-mute-btn'}>
          <Button variant="secondary" className="min-w-24" onClick={mute} />
        </Localized>
      </td>
    </tr>
  );
}

function Table({ children }: { children: ReactNode }) {
  return (
    <table className="min-w-full divide-y divide-background-50">
      <thead>
        <tr>
          <th scope="col" className="px-6 py-3 text-start">
            <Localized id={'vrc_config-setting_name'}>
              <Typography />
            </Localized>
          </th>

          <th scope="col" className="px-6 py-3 text-end">
            <Localized id={'vrc_config-recommended_value'}>
              <Typography />
            </Localized>
          </th>
          <th scope="col" className="px-6 py-3 text-end">
            <Localized id={'vrc_config-current_value'}>
              <Typography />
            </Localized>
          </th>
          <th scope="col" className="px-6 py-3 text-end">
            <Localized id={'vrc_config-mute'}>
              <Typography />
            </Localized>
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
    <div className="flex flex-col p-4 w-full">
      <div className="flex flex-col max-w-lg mobile:w-full gap-3">
        <Localized id={'vrc_config-page-title'}>
          <Typography variant="main-title" />
        </Localized>
        <Localized id={'vrc_config-page-desc'}>
          <Typography variant="standard" />
        </Localized>
      </div>
      <div className="w-full mt-4 gap-2 flex flex-col">
        <div className="-m-2 overflow-x-auto">
          <div className="p-2 min-w-full inline-block align-middle">
            <div className="overflow-hidden flex flex-col gap-4">
              <div className="flex flex-col gap-2">
                <Localized id="vrc_config-page-big_menu">
                  <Typography variant="section-title" />
                </Localized>
                <Localized id="vrc_config-page-big_menu-desc">
                  <Typography />
                </Localized>
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
                        id={onOffKey(
                          state.recommended.shoulderTrackingDisabled
                        )}
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
                        id={onOffKey(
                          state.recommended.shoulderWidthCompensation
                        )}
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
                      <Localized
                        id={onOffKey(state.state.calibrationVisuals)}
                      />
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
                        id={
                          trackerModelTranslationMap[state.state.trackerModel]
                        }
                      />
                    }
                  />
                </Table>
              </div>
              <div className="flex flex-col gap-2">
                <Localized id="vrc_config-page-wrist_menu">
                  <Typography variant="section-title" />
                </Localized>
                <Localized id="vrc_config-page-wrist_menu-desc">
                  <Typography />
                </Localized>
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
        </div>
      </div>
      <div className="flex flex-col max-w-lg mobile:w-full gap-2 mt-4">
        <Localized id={'vrc_config-page-help'}>
          <Typography variant="section-title" />
        </Localized>
        <Localized
          id={'vrc_config-page-help-desc'}
          elems={{
            a: <A href="https://docs.slimevr.dev/tools/vrchat-config.html" />,
          }}
        >
          <Typography />
        </Localized>
      </div>
    </div>
  );
}
