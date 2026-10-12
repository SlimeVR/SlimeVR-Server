import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  BodyPart,
  ChangeTapDetectionSettingsRequestT,
  RpcMessage,
  TapDetectionSettingsRequestT,
  TapDetectionSettingsResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Dropdown } from '@/components/commons/Dropdown';
import { TAP_DETECTION_BODY_PARTS } from '@/hooks/tracker-picker';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { useLocaleConfig } from '@/i18n/config';
import classNames from 'classnames';
import { Typography } from '@/components/commons/Typography';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import {
  FullResetIcon,
  MountingResetIcon,
  YawResetIcon,
} from '@/components/commons/icon/ResetIcon';
import {
  SettingRow,
  SettingsGroup,
  SettingsGroupLabel,
} from '@/components/settings/SettingRow';

type TapDetectionForm = {
  yawResetEnabled: boolean;
  fullResetEnabled: boolean;
  mountingResetEnabled: boolean;
  yawResetDelay: number;
  fullResetDelay: number;
  mountingResetDelay: number;
  yawResetTaps: number;
  fullResetTaps: number;
  mountingResetTaps: number;
  yawResetTracker: string;
  fullResetTracker: string;
  mountingResetTracker: string;
  numberTrackersOverThreshold: number;
};

const defaultValues: TapDetectionForm = {
  yawResetEnabled: false,
  fullResetEnabled: false,
  mountingResetEnabled: false,
  yawResetDelay: 0.2,
  fullResetDelay: 0.2,
  mountingResetDelay: 0.2,
  yawResetTaps: 2,
  fullResetTaps: 2,
  mountingResetTaps: 2,
  yawResetTracker: String(BodyPart.UPPER_CHEST),
  fullResetTracker: String(BodyPart.LEFT_UPPER_LEG),
  mountingResetTracker: String(BodyPart.RIGHT_UPPER_LEG),
  numberTrackersOverThreshold: 1,
};

const tapDetectionSettingsAtom = atom(new TapDetectionSettingsResponseT());
const tapDetectionSettingsValueAtom = selectAtom(
  tapDetectionSettingsAtom,
  (settings) => settings,
  isEqual
);

export function TapDetectionSettings() {
  const setSettings = useSetAtom(tapDetectionSettingsAtom);
  const settings = useAtomValue(tapDetectionSettingsValueAtom);
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const bodyParts: { value: string; label: string }[] = Object.values(BodyPart)
    .filter((v): v is BodyPart => typeof v === 'number')
    .filter((v) => TAP_DETECTION_BODY_PARTS.includes(v as BodyPart))
    .map((value) => ({
      value: String(value),
      label: l10n.getString(`body_part-${BodyPart[value]}`),
    }));

  const { control, watch, handleSubmit, getValues, reset, setValue } =
    useForm<TapDetectionForm>({
      defaultValues,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: TapDetectionForm) => {
    const settingsReq = new ChangeTapDetectionSettingsRequestT();
    settingsReq.fullResetDelay = values.fullResetDelay;
    settingsReq.fullResetEnabled = values.fullResetEnabled;
    settingsReq.fullResetTaps = values.fullResetTaps;
    settingsReq.yawResetDelay = values.yawResetDelay;
    settingsReq.yawResetEnabled = values.yawResetEnabled;
    settingsReq.yawResetTaps = values.yawResetTaps;
    settingsReq.yawResetTracker = Number(values.yawResetTracker);
    settingsReq.mountingResetTracker = Number(values.mountingResetTracker);
    settingsReq.fullResetTracker = Number(values.fullResetTracker);
    settingsReq.mountingResetEnabled = values.mountingResetEnabled;
    settingsReq.mountingResetDelay = values.mountingResetDelay;
    settingsReq.mountingResetTaps = values.mountingResetTaps;
    settingsReq.numberTrackersOverThreshold =
      values.numberTrackersOverThreshold;

    sendRPCPacket(RpcMessage.ChangeTapDetectionSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.TapDetectionSettingsRequest,
      new TapDetectionSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    const formData: DefaultValues<TapDetectionForm> = {
      yawResetEnabled:
        settings.yawResetEnabled ?? defaultValues.yawResetEnabled,
      fullResetEnabled:
        settings.fullResetEnabled ?? defaultValues.fullResetEnabled,
      mountingResetEnabled:
        settings.mountingResetEnabled ?? defaultValues.mountingResetEnabled,
      yawResetDelay: settings.yawResetDelay ?? defaultValues.yawResetDelay,
      fullResetDelay: settings.fullResetDelay ?? defaultValues.fullResetDelay,
      mountingResetDelay:
        settings.mountingResetDelay ?? defaultValues.mountingResetDelay,
      yawResetTaps: settings.yawResetTaps ?? defaultValues.yawResetTaps,
      fullResetTaps: settings.fullResetTaps ?? defaultValues.fullResetTaps,
      mountingResetTaps:
        settings.mountingResetTaps ?? defaultValues.mountingResetTaps,
      yawResetTracker: String(
        settings.yawResetTracker ?? defaultValues.yawResetTracker
      ),
      fullResetTracker: String(
        settings.fullResetTracker ?? defaultValues.fullResetTracker
      ),
      mountingResetTracker: String(
        settings.mountingResetTracker ?? defaultValues.mountingResetTracker
      ),
      numberTrackersOverThreshold:
        settings.numberTrackersOverThreshold ??
        defaultValues.numberTrackersOverThreshold,
    };
    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.TapDetectionSettingsResponse,
    (settings: TapDetectionSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const resetKinds = [
    {
      id: 'yawReset',
      enabled: watch('yawResetEnabled'),
      icon: <YawResetIcon width={20} />,
    },
    {
      id: 'fullReset',
      enabled: watch('fullResetEnabled'),
      icon: <FullResetIcon width={20} />,
    },
    {
      id: 'mountingReset',
      enabled: watch('mountingResetEnabled'),
      icon: <MountingResetIcon width={20} />,
    },
  ] as const;

  const column = (id: string) => (
    <Typography
      variant="section-title"
      id={`settings-general-gesture_control-column-${id}`}
    />
  );

  return (
    <div className="flex flex-col gap-2 min-w-0 mt-8">
      <div className="px-3 xs:px-4">
        <SettingsGroupLabel id="settings-general-gesture_control-subtitle" />
      </div>
      <div className="px-3 xs:px-4 max-w-[75ch]">
        <Typography id="settings-general-gesture_control-description" />
      </div>
      <div className="rounded-2xl bg-background-80 overflow-hidden">
        <table className="w-full text-left border-collapse block md:table">
          <thead className="hidden md:table-header-group">
            <tr className="border-b border-background-60 uppercase">
              <th className="py-3 pl-4 pr-2 w-0" />
              <th className="py-3 pr-4 w-64 whitespace-nowrap">
                {column('reset')}
              </th>
              <th className="py-3 px-4">{column('tracker')}</th>
              <th className="py-3 px-4 w-56">{column('taps')}</th>
              <th className="py-3 pl-4 pr-4 w-56">{column('delay')}</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-background-60 block md:table-row-group">
            {resetKinds.map(({ id, enabled, icon }) => (
              <tr
                key={id}
                className="grid grid-cols-[auto,1fr] md:table-row p-4 md:p-0 gap-x-3 gap-y-3 md:gap-0 items-center border-b border-background-60 md:border-b-0"
              >
                <td className="py-2 md:py-3 pl-0 md:pl-4 pr-0 md:pr-2 block md:table-cell align-middle">
                  <CheckboxInternal
                    variant="toggle"
                    name={`${id}Enabled`}
                    id={`tap-${id}`}
                    checked={enabled}
                    onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                      setValue(
                        `${id}Enabled` as keyof TapDetectionForm,
                        event.currentTarget.checked,
                        { shouldDirty: true }
                      )
                    }
                  />
                </td>
                <td className="py-2 md:py-3 pl-0 md:pr-4 block md:table-cell align-middle">
                  <label
                    htmlFor={`tap-${id}`}
                    className="flex items-center gap-2.5 cursor-pointer whitespace-nowrap"
                  >
                    <div className="w-6 h-6 flex items-center justify-center fill-background-10 flex-shrink-0">
                      {icon}
                    </div>
                    <Typography
                      bold
                      whitespace="whitespace-nowrap"
                      id={`settings-general-gesture_control-${id}-name`}
                    />
                  </label>
                </td>
                <fieldset
                  disabled={!enabled}
                  className={classNames(
                    'contents',
                    !enabled && '[&_td]:opacity-50'
                  )}
                >
                  <td className="col-span-2 py-2 md:py-3 px-0 md:px-4 block md:table-cell align-middle">
                    <span className="md:hidden">{column('tracker')}</span>
                    <Dropdown
                      display="block"
                      control={control}
                      placeholder={''}
                      name={`${id}Tracker` as keyof TapDetectionForm}
                      items={bodyParts}
                    />
                  </td>
                  <td className="col-span-2 py-2 md:py-3 px-0 md:px-4 block md:table-cell align-middle">
                    <span className="md:hidden">{column('taps')}</span>
                    <NumberSelector
                      control={control}
                      name={`${id}Taps` as keyof TapDetectionForm}
                      valueLabelFormat={(value) =>
                        l10n.getString(
                          'settings-general-gesture_control-taps',
                          { amount: Math.round(value) }
                        )
                      }
                      min={2}
                      max={10}
                      step={1}
                    />
                  </td>
                  <td className="col-span-2 py-2 md:py-3 px-0 md:px-4 block md:table-cell align-middle">
                    <span className="md:hidden">{column('delay')}</span>
                    <NumberSelector
                      control={control}
                      name={`${id}Delay` as keyof TapDetectionForm}
                      valueLabelFormat={(value) => secondsFormat.format(value)}
                      min={0.2}
                      max={3.0}
                      step={0.2}
                    />
                  </td>
                </fieldset>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <SettingsGroup>
        <SettingRow
          title="settings-general-gesture_control-numberTrackersOverThreshold"
          description="settings-general-gesture_control-numberTrackersOverThreshold-description"
          controlWidth="wide"
          control={
            <NumberSelector
              control={control}
              name="numberTrackersOverThreshold"
              valueLabelFormat={(value) =>
                l10n.getString('settings-general-gesture_control-trackers', {
                  amount: Math.round(value),
                })
              }
              min={1}
              max={20}
              step={1}
            />
          }
        />
      </SettingsGroup>
    </div>
  );
}
