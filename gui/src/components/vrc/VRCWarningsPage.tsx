import { Typography } from '@/components/commons/Typography';
import { ReactNode } from 'react';
import {} from 'solarxr-protocol';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import {
  avatarMeasurementTypeTranslationMap,
  spineModeTranslationMap,
  trackerModelTranslationMap,
  useVRCConfig,
} from '@/hooks/vrc-config';
import { Localized, useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useLocaleConfig } from '@/i18n/config';
import { A } from '@/components/commons/A';

export function SettingRow({
  name,
  valid,
  value,
  recommendedValue,
}: {
  valid: boolean;
  name: string;
  recommendedValue: ReactNode;
  value: ReactNode;
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
          !valid && 'text-status-warning'
        )}
      >
        {value}
      </td>
    </tr>
  );
}

const onOffKey = (value: boolean) =>
  value ? 'vrc_config-on' : 'vrc_config-off';

export function VRCWarningsPage() {
  const { l10n } = useLocalization();
  const { state } = useVRCConfig();
  const { currentLocales } = useLocaleConfig();

  const meterFormat = Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'meter',
    maximumFractionDigits: 2,
  });

  if (!state || !state.isSupported) {
    return <></>;
  }

  return (
    <div className="flex flex-col p-4 w-full">
      <div className="flex flex-col max-w-lg mobile:w-full gap-3">
        <Localized id={'vrc_config-page-title'}>
          <Typography variant="main-title" />
        </Localized>
        <Localized id={'vrc_config-page-desc'}>
          <Typography variant="standard" color="secondary" />
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
                  <Typography color="secondary" />
                </Localized>
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
                    </tr>
                  </thead>
                  <tbody>
                    <SettingRow
                      name="vrc_config-user_height"
                      recommendedValue={meterFormat.format(
                        state.recommended.userHeight
                      )}
                      value={meterFormat.format(state.state.userHeight)}
                      valid={state.validity.userHeightOk}
                    ></SettingRow>
                    <SettingRow
                      name="vrc_config-legacy_mode"
                      recommendedValue={
                        <Localized
                          id={onOffKey(state.recommended.legacyMode)}
                        ></Localized>
                      }
                      value={
                        <Localized
                          id={onOffKey(state.state.legacyMode)}
                        ></Localized>
                      }
                      valid={state.validity.legacyModeOk}
                    ></SettingRow>
                    <SettingRow
                      name="vrc_config-disable_shoulder_tracking"
                      recommendedValue={
                        <Localized
                          id={onOffKey(
                            state.recommended.shoulderTrackingDisabled
                          )}
                        ></Localized>
                      }
                      value={
                        <Localized
                          id={onOffKey(state.state.shoulderTrackingDisabled)}
                        ></Localized>
                      }
                      valid={state.validity.shoulderTrackingOk}
                    ></SettingRow>
                    <SettingRow
                      name="vrc_config-shoulder_width_compensation"
                      recommendedValue={
                        <Localized
                          id={onOffKey(
                            state.recommended.shoulderWidthCompensation
                          )}
                        ></Localized>
                      }
                      value={
                        <Localized
                          id={onOffKey(state.state.shoulderWidthCompensation)}
                        ></Localized>
                      }
                      valid={state.validity.shoulderWidthCompensationOk}
                    ></SettingRow>
                    <SettingRow
                      name="vrc_config-calibration_visuals"
                      recommendedValue={
                        <Localized
                          id={onOffKey(state.recommended.calibrationVisuals)}
                        ></Localized>
                      }
                      value={
                        <Localized
                          id={onOffKey(state.state.calibrationVisuals)}
                        ></Localized>
                      }
                      valid={state.validity.calibrationVisualsOk}
                    ></SettingRow>
                    <SettingRow
                      name="vrc_config-calibration_range"
                      recommendedValue={meterFormat.format(
                        state.recommended.calibrationRange
                      )}
                      value={meterFormat.format(state.state.calibrationRange)}
                      valid={state.validity.calibrationRangeOk}
                    ></SettingRow>
                    <SettingRow
                      name="vrc_config-tracker_model"
                      recommendedValue={
                        <Localized
                          id={
                            trackerModelTranslationMap[
                              state.recommended.trackerModel
                            ]
                          }
                        ></Localized>
                      }
                      value={
                        <Localized
                          id={
                            trackerModelTranslationMap[state.state.trackerModel]
                          }
                        ></Localized>
                      }
                      valid={state.validity.trackerModelOk}
                    ></SettingRow>
                  </tbody>
                </table>
              </div>
              <div className="flex flex-col gap-2">
                <Localized id="vrc_config-page-wrist_menu">
                  <Typography variant="section-title" />
                </Localized>
                <Localized id="vrc_config-page-wrist_menu-desc">
                  <Typography color="secondary" />
                </Localized>
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
                    </tr>
                  </thead>
                  <tbody>
                    <SettingRow
                      name="vrc_config-spine_mode"
                      recommendedValue={state.recommended.spineMode
                        .map((mode) =>
                          l10n.getString(spineModeTranslationMap[mode])
                        )
                        .join(', ')}
                      value={
                        <Localized
                          id={spineModeTranslationMap[state.state.spineMode]}
                        ></Localized>
                      }
                      valid={state.validity.spineModeOk}
                    ></SettingRow>

                    <SettingRow
                      name="vrc_config-avatar_measurement_type"
                      recommendedValue={
                        <Localized
                          id={
                            avatarMeasurementTypeTranslationMap[
                              state.recommended.avatarMeasurementType
                            ]
                          }
                        ></Localized>
                      }
                      value={
                        <Localized
                          id={
                            avatarMeasurementTypeTranslationMap[
                              state.state.avatarMeasurementType
                            ]
                          }
                        ></Localized>
                      }
                      valid={state.validity.avatarMeasurementTypeOk}
                    ></SettingRow>
                  </tbody>
                </table>
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
            a: <A href="https://docs.slimevr.dev/tools/vrchat-config.html"></A>,
          }}
        >
          <Typography color="secondary" />
        </Localized>
      </div>
    </div>
  );
}
