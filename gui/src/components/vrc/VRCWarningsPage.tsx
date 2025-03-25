import { Typography } from '@/components/commons/Typography';
import { ReactNode, useState } from 'react';
import {} from 'solarxr-protocol';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';
import {
  spineModeTranslationMap,
  trackerModelTranslationMap,
  useVRCConfig,
} from '@/hooks/vrc-config';
import { Localized, useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useLocaleConfig } from '@/i18n/config';

export function SettingRow({
  name,
  valid,
  value,
  recommendedValue,
  details,
}: {
  valid: boolean;
  name: string;
  recommendedValue: ReactNode;
  value: ReactNode;
  details: ReactNode;
}) {
  const [open, setOpen] = useState(false);

  const toggle = () => {
    console.log('open');
    setOpen((open) => !open);
  };

  return (
    <>
      <tr
        className="group border-b border-background-60 cursor-pointer"
        onClick={toggle}
      >
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
        <td className="text-end fill-background-40 group-hover:fill-background-30">
          <ArrowDownIcon size={30}></ArrowDownIcon>
        </td>
      </tr>
      <tr>
        <td colSpan={4}>
          <div
            className="bg-background-50 grid w-full rounded-b-md overflow-clip"
            style={{
              gridTemplateRows: open ? '1fr' : '0fr',
              transition: 'grid-template-rows 0.2s ease-in-out',
            }}
          >
            <div className="overflow-hidden">
              <div className="p-2">{details}</div>
            </div>
          </div>
        </td>
      </tr>
    </>
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
      <div className="flex flex-col max-w-lg gap-3">
        <Typography variant="main-title">
          VRChat configuration warnings
        </Typography>
        <Typography variant="standard" color="secondary">
          This page shows the state of your VRChat settings and shows what
          settings are incompatible with SlimeVR. It is highly recommended that
          you fix any warnings showing up here for the best user experience with
          SlimeVR
        </Typography>
      </div>
      <div className="w-full mt-4 gap-2 flex flex-col">
        <div className="-m-2 overflow-x-auto">
          <div className="p-2 min-w-full inline-block align-middle">
            <div className="overflow-hidden">
              <table className="min-w-full divide-y divide-background-50">
                <thead>
                  <tr>
                    <th scope="col" className="px-6 py-3 text-start">
                      <Localized id={'vrc_config-setting_name'}>
                        <Typography></Typography>
                      </Localized>
                    </th>

                    <th scope="col" className="px-6 py-3 text-end">
                      <Localized id={'vrc_config-recommended_value'}>
                        <Typography></Typography>
                      </Localized>
                    </th>
                    <th scope="col" className="px-6 py-3 text-end">
                      <Localized id={'vrc_config-current_value'}>
                        <Typography></Typography>
                      </Localized>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <SettingRow
                    details={<>hI</>}
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
                    details={<>hI</>}
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
                    details={<>hI</>}
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
                    details={<>hI</>}
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
                  <SettingRow
                    details={<>hI</>}
                    name="vrc_config-calibration_range"
                    recommendedValue={meterFormat.format(
                      state.recommended.calibrationRange
                    )}
                    value={meterFormat.format(state.state.calibrationRange)}
                    valid={state.validity.calibrationRangeOk}
                  ></SettingRow>
                  <SettingRow
                    details={<>hI</>}
                    name="vrc_config-user_height"
                    recommendedValue={meterFormat.format(
                      state.recommended.userHeight
                    )}
                    value={meterFormat.format(state.state.userHeight)}
                    valid={state.validity.userHeightOk}
                  ></SettingRow>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
