import { useLocalization } from '@fluent/react';
import { ReactNode, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import {
  BodyPart,
  ChangeTrackerActionSettingsRequestT,
  RpcMessage,
  TrackerActionMethod,
  TrackerActionSettingsRequestT,
  TrackerActionSettingsResponseT,
  TrackerActionSettingsT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import {
  SettingResetButton,
  settingReset,
} from '@/components/settings/SettingReset';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Dropdown } from '@/components/commons/Dropdown';
import { TAP_DETECTION_BODY_PARTS } from '@/hooks/tracker-picker';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { useLocaleConfig } from '@/i18n/config';
import classNames from 'classnames';
import { Typography } from '@/components/commons/Typography';
import { CheckBox } from '@/components/commons/Checkbox';
import {
  FullResetIcon,
  MountingResetIcon,
  YawResetIcon,
} from '@/components/commons/icon/ResetIcon';
import {
  SettingRow,
  SettingsGroup,
  SettingsGroupLabel,
  useFocusSetting,
} from '@/components/settings/SettingRow';
import {
  SettingDef,
  SettingsGroupDef,
} from '@/components/settings/settings-definitions';

type TrackerActionForm = {
  yawResetEnabled: boolean;
  yawResetBodyPart: string;
  yawResetMethod: string;
  yawResetTaps: number;
  yawResetDelay: number;
  fullResetEnabled: boolean;
  fullResetBodyPart: string;
  fullResetMethod: string;
  fullResetTaps: number;
  fullResetDelay: number;
  mountingResetEnabled: boolean;
  mountingResetBodyPart: string;
  mountingResetMethod: string;
  mountingResetTaps: number;
  mountingResetDelay: number;
  tapsTrackersOverThreshold: number;
};

// The server always sends every field, the fallbacks only satisfy the optional types
function toForm(settings: TrackerActionSettingsResponseT): TrackerActionForm {
  return {
    yawResetEnabled: settings.yawResetAction?.enabled ?? false,
    yawResetBodyPart: String(
      settings.yawResetAction?.bodyPart ?? BodyPart.NONE
    ),
    yawResetMethod: String(
      settings.yawResetAction?.method ?? TrackerActionMethod.TAP
    ),
    yawResetTaps: settings.yawResetAction?.taps ?? 2,
    yawResetDelay: settings.yawResetAction?.delay ?? 0,
    fullResetEnabled: settings.fullResetAction?.enabled ?? false,
    fullResetBodyPart: String(
      settings.fullResetAction?.bodyPart ?? BodyPart.NONE
    ),
    fullResetMethod: String(
      settings.fullResetAction?.method ?? TrackerActionMethod.TAP
    ),
    fullResetTaps: settings.fullResetAction?.taps ?? 2,
    fullResetDelay: settings.fullResetAction?.delay ?? 0,
    mountingResetEnabled: settings.mountingResetAction?.enabled ?? false,
    mountingResetBodyPart: String(
      settings.mountingResetAction?.bodyPart ?? BodyPart.NONE
    ),
    mountingResetMethod: String(
      settings.mountingResetAction?.method ?? TrackerActionMethod.TAP
    ),
    mountingResetTaps: settings.mountingResetAction?.taps ?? 2,
    mountingResetDelay: settings.mountingResetAction?.delay ?? 0,
    tapsTrackersOverThreshold: settings.tapsTrackersOverThreshold ?? 0,
  };
}

const trackerActionSettingsAtom = atom<TrackerActionSettingsResponseT | null>(
  null
);
const trackerActionSettingsValueAtom = selectAtom(
  trackerActionSettingsAtom,
  (settings) => settings,
  isEqual
);

function FocusableRow({
  setting,
  className,
  children,
}: {
  setting: SettingDef;
  className: string;
  children: ReactNode;
}) {
  const { ref, highlighted } = useFocusSetting<HTMLTableRowElement>(
    setting.key
  );
  return (
    <tr
      ref={ref}
      className={classNames(
        className,
        highlighted && 'bg-accent-background-40/30'
      )}
    >
      {children}
    </tr>
  );
}

export function TrackerActionSettings({
  resets,
  threshold,
}: {
  resets: SettingsGroupDef<'yawReset' | 'fullReset' | 'mountingReset'>;
  threshold: SettingsGroupDef<'tapsTrackersOverThreshold'>;
}) {
  const setSettings = useSetAtom(trackerActionSettingsAtom);
  const settings = useAtomValue(trackerActionSettingsValueAtom);
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const defaults = useSettingsDefaults().trackerAction;

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

  const TrackerActionMethods: { value: string; label: string }[] =
    Object.values(TrackerActionMethod)
      .filter((v): v is TrackerActionMethod => typeof v === 'number')
      .map((value) => ({
        value: String(value),
        label: l10n.getString(
          `settings-general-gesture_control-${TrackerActionMethod[value]}`
        ),
      }));

  const { control, watch, handleSubmit, reset, setValue } =
    useForm<TrackerActionForm>({
      defaultValues: toForm(defaults),
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: TrackerActionForm) => {
    const settingsReq = new ChangeTrackerActionSettingsRequestT();
    settingsReq.yawResetAction = new TrackerActionSettingsT(
      values.yawResetEnabled,
      values.yawResetDelay,
      Number(values.yawResetBodyPart),
      Number(values.yawResetMethod),
      values.yawResetTaps
    );
    settingsReq.fullResetAction = new TrackerActionSettingsT(
      values.fullResetEnabled,
      values.fullResetDelay,
      Number(values.fullResetBodyPart),
      Number(values.fullResetMethod),
      values.fullResetTaps
    );
    settingsReq.mountingResetAction = new TrackerActionSettingsT(
      values.mountingResetEnabled,
      values.mountingResetDelay,
      Number(values.mountingResetBodyPart),
      Number(values.mountingResetMethod),
      values.mountingResetTaps
    );
    settingsReq.tapsTrackersOverThreshold = values.tapsTrackersOverThreshold;

    sendRPCPacket(RpcMessage.ChangeTrackerActionSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.TrackerActionSettingsRequest,
      new TrackerActionSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    if (!settings) return;
    reset(toForm(settings));
  }, [settings]);

  useRPCPacket(
    RpcMessage.TrackerActionSettingsResponse,
    (settings: TrackerActionSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const resetThreshold = settingReset<
    TrackerActionForm,
    'tapsTrackersOverThreshold'
  >(
    defaults.tapsTrackersOverThreshold ?? undefined,
    watch('tapsTrackersOverThreshold'),
    (next) => {
      setValue('tapsTrackersOverThreshold', next);
      handleSubmit(onSubmit)();
    }
  );

  const defaultForm = toForm(defaults);
  const resetKindFields = (id: (typeof resetKinds)[number]['id']) =>
    [
      `${id}Enabled`,
      `${id}BodyPart`,
      `${id}Method`,
      `${id}Taps`,
      `${id}Delay`,
    ] as const;
  const kindReset = (id: (typeof resetKinds)[number]['id']) => {
    const fields = resetKindFields(id);
    return {
      modified: fields.some((field) => watch(field) !== defaultForm[field]),
      onReset: () => {
        fields.forEach((field) => setValue(field, defaultForm[field]));
        handleSubmit(onSubmit)();
      },
    };
  };

  const resetKinds = [
    {
      id: 'yawReset',
      enabled: watch('yawResetEnabled'),
      icon: <YawResetIcon width={20} />,
      method: watch('yawResetMethod'),
    },
    {
      id: 'fullReset',
      enabled: watch('fullResetEnabled'),
      icon: <FullResetIcon width={20} />,
      method: watch('fullResetMethod'),
    },
    {
      id: 'mountingReset',
      enabled: watch('mountingResetEnabled'),
      icon: <MountingResetIcon width={20} />,
      method: watch('mountingResetMethod'),
    },
  ] as const;

  const column = (id: string) => (
    <Typography
      variant="section-title"
      id={`settings-general-gesture_control-column-${id}`}
    />
  );

  const mobileCaption = (id: string) => (
    <div className="lg:hidden">
      <Typography bold id={`settings-general-gesture_control-column-${id}`} />
    </div>
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
        <table className="w-full text-left border-collapse block lg:table">
          <thead className="hidden lg:table-header-group">
            <tr className="border-b border-background-60 uppercase">
              <th className="py-3 pl-4 pr-2 w-0" />
              <th className="py-3 pr-4 w-64 whitespace-nowrap">
                {column('action')}
              </th>
              <th className="py-3 px-4">{column('tracker')}</th>
              <th className="py-3 px-4">{column('method')}</th>
              <th className="py-3 px-4 w-56">{column('taps')}</th>
              <th className="py-3 pl-4 pr-4 w-56">{column('delay')}</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-background-60 block lg:table-row-group">
            {resetKinds.map(({ id, enabled, icon, method }) => (
              <FocusableRow
                key={id}
                setting={resets.rows[id]}
                className="grid grid-cols-[1.75rem_minmax(0,1fr)_minmax(0,1fr)] lg:table-row gap-x-2 gap-y-3 lg:gap-0 items-center pl-1 pr-3 py-3 xs:pl-2 xs:pr-4 xs:py-4 lg:p-0"
              >
                <td className="contents lg:table-cell lg:py-3 lg:pl-4 lg:pr-2 align-middle">
                  <div className="contents lg:flex lg:items-center lg:gap-2">
                    <div className="col-start-1 row-start-1 flex justify-center">
                      <SettingResetButton {...kindReset(id)} />
                    </div>
                    <div className="col-start-3 row-start-1 justify-self-end flex">
                      <CheckBox
                        variant="toggle"
                        control={control}
                        name={`${id}Enabled`}
                        id={`tap-${id}`}
                      />
                    </div>
                  </div>
                </td>
                <td className="col-start-2 col-span-2 row-start-1 min-w-0 pr-12 block lg:table-cell lg:py-3 lg:pr-2 align-middle">
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
                <td className="col-start-2 col-span-2 row-start-2 smol:col-start-2 smol:col-span-1 min-w-0 block lg:table-cell lg:py-3 lg:px-2 align-middle">
                  <fieldset
                    disabled={!enabled}
                    className={classNames(
                      'flex flex-col gap-1.5',
                      !enabled && 'opacity-50'
                    )}
                  >
                    {mobileCaption('tracker')}
                    <Dropdown
                      display="block"
                      control={control}
                      placeholder={''}
                      name={`${id}BodyPart` as keyof TrackerActionForm}
                      items={bodyParts}
                    />
                  </fieldset>
                </td>
                <td className="col-start-2 col-span-2 row-start-2 smol:col-start-3 smol:col-span-1 smol:row-start-2 min-w-0 block lg:table-cell lg:py-3 lg:px-2 align-middle">
                  <fieldset
                    disabled={!enabled}
                    className={classNames(
                      'flex flex-col gap-1.5',
                      !enabled && 'opacity-50'
                    )}
                  >
                    {mobileCaption('method')}
                    <Dropdown
                      display="block"
                      control={control}
                      placeholder={''}
                      name={`${id}Method` as keyof TrackerActionForm}
                      items={TrackerActionMethods}
                    />
                  </fieldset>
                </td>
                <td className="col-start-2 col-span-2 row-start-3 smol:col-start-2 smol:col-span-1 min-w-0 block lg:table-cell lg:py-3 lg:px-2 align-middle">
                  <fieldset
                    disabled={!enabled || Number(method) == TrackerActionMethod.BUTTON}
                    className={classNames(
                      'flex flex-col gap-1.5',
                      (!enabled || Number(method) == TrackerActionMethod.BUTTON) && 'opacity-50'
                    )}
                  >
                    {mobileCaption('taps')}
                    <NumberSelector
                      control={control}
                      name={`${id}Taps` as keyof TrackerActionForm}
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
                  </fieldset>
                </td>
                <td className="col-start-2 col-span-2 row-start-3 smol:col-start-3 smol:col-span-1 smol:row-start-3 min-w-0 block lg:table-cell lg:py-3 lg:px-2 align-middle">
                  <fieldset
                    disabled={!enabled}
                    className={classNames(
                      'flex flex-col gap-1.5',
                      !enabled && 'opacity-50'
                    )}
                  >
                    {mobileCaption('delay')}
                    <NumberSelector
                      control={control}
                      name={`${id}Delay` as keyof TrackerActionForm}
                      valueLabelFormat={(value) => secondsFormat.format(value)}
                      min={0.2}
                      max={3.0}
                      step={0.2}
                    />
                  </fieldset>
                </td>
              </FocusableRow>
            ))}
          </tbody>
        </table>
      </div>

      <SettingsGroup group={threshold}>
        <SettingRow
          setting={threshold.rows.tapsTrackersOverThreshold}
          controlWidth="wide"
          {...resetThreshold}
          control={
            <NumberSelector
              control={control}
              name="tapsTrackersOverThreshold"
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
