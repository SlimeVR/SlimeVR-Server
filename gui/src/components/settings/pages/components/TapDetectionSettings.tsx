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
import { Typography } from '@/components/commons/Typography';
import { CheckBox } from '@/components/commons/Checkbox';
import { Dropdown } from '@/components/commons/Dropdown';
import { ASSIGNMENT_MODES } from '@/components/onboarding/BodyAssignment';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { useLocaleConfig } from '@/i18n/config';

type TapDetectionForm = {
  mountingResetEnabled: boolean;
  yawResetEnabled: boolean;
  fullResetEnabled: boolean;
  yawResetDelay: number;
  fullResetDelay: number;
  mountingResetDelay: number;
  yawResetTaps: number;
  fullResetTaps: number;
  mountingResetTaps: number;
  numberTrackersOverThreshold: number;
  yawResetTracker: string;
  mountingResetTracker: string;
  fullResetTracker: string;
};

const defaultValues: TapDetectionForm = {
  mountingResetEnabled: false,
  yawResetEnabled: false,
  fullResetEnabled: false,
  yawResetDelay: 0.2,
  fullResetDelay: 1.0,
  mountingResetDelay: 1.0,
  yawResetTaps: 2,
  fullResetTaps: 3,
  mountingResetTaps: 3,
  numberTrackersOverThreshold: 1,
  yawResetTracker: String(BodyPart.CHEST),
  mountingResetTracker: String(BodyPart.RIGHT_UPPER_LEG),
  fullResetTracker: String(BodyPart.LEFT_UPPER_LEG),
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
    .filter((v) => ASSIGNMENT_MODES['full-body'].includes(v as BodyPart))
    .map((value) => ({
      value: String(value),
      label: l10n.getString(`body_part-${BodyPart[value]}`),
    }));

  const { control, watch, handleSubmit, getValues, reset } =
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
    settingsReq.mountingResetTracker = Number(
      values.mountingResetTracker
    );
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
          settings.yawResetEnabled || defaultValues.yawResetEnabled,
      fullResetEnabled:
          settings.fullResetEnabled ||
          defaultValues.fullResetEnabled,
      mountingResetEnabled:
          settings.mountingResetEnabled ||
          defaultValues.mountingResetEnabled,
      yawResetDelay:
          settings.yawResetDelay || defaultValues.yawResetDelay,
      fullResetDelay:
          settings.fullResetDelay || defaultValues.fullResetDelay,
      mountingResetDelay:
          settings.mountingResetDelay ||
          defaultValues.mountingResetDelay,
      yawResetTaps:
          settings.yawResetTaps || defaultValues.yawResetTaps,
      fullResetTaps:
          settings.fullResetTaps || defaultValues.fullResetTaps,
      mountingResetTaps:
          settings.mountingResetTaps ||
          defaultValues.mountingResetTaps,
      yawResetTracker: String(
          settings.yawResetTracker || defaultValues.yawResetTracker
      ),
      fullResetTracker: String(
          settings.fullResetTracker || defaultValues.fullResetTracker
      ),
      mountingResetTracker: String(
          settings.mountingResetTracker ||
          defaultValues.mountingResetTracker
      ),
      numberTrackersOverThreshold:
          settings.numberTrackersOverThreshold ||
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

  return (
    <>
      <div className="flex flex-col pb-2 gap-1">
        <Typography variant="section-title">
          {l10n.getString('settings-general-gesture_control-subtitle')}
        </Typography>
        <Typography>
          {l10n.getString('settings-general-gesture_control-description')}
        </Typography>
      </div>
      <div>
        <div className="grid sm:grid-cols-3 gap-5 pb-2">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="yawResetEnabled"
            label={l10n.getString(
              'settings-general-gesture_control-yawResetEnabled'
            )}
          />
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="fullResetEnabled"
            label={l10n.getString(
              'settings-general-gesture_control-fullResetEnabled'
            )}
          />
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="mountingResetEnabled"
            label={l10n.getString(
              'settings-general-gesture_control-mountingResetEnabled'
            )}
          />
        </div>
        <div className="grid sm:grid-cols-3 gap-5 pb-2">
          <div>
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-gesture_control-yawResetTracker'
              )}
            </Typography>
            <Dropdown
              display="block"
              control={control}
              placeholder={''}
              name="yawResetTracker"
              items={bodyParts}
            />
          </div>
          <div>
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-gesture_control-mountingResetTracker'
              )}
            </Typography>
            <Dropdown
              display="block"
              control={control}
              placeholder={''}
              name="mountingResetTracker"
              items={bodyParts}
            />
          </div>
          <div>
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-gesture_control-fullResetTracker'
              )}
            </Typography>
            <Dropdown
              display="block"
              control={control}
              placeholder={''}
              name="fullResetTracker"
              items={bodyParts}
            />
          </div>
        </div>
      </div>
      <div className="grid sm:grid-cols-3 gap-5 pb-2">
        <NumberSelector
          control={control}
          name="yawResetDelay"
          label={l10n.getString(
            'settings-general-gesture_control-yawResetDelay'
          )}
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0.2}
          max={3.0}
          step={0.2}
        />
        <NumberSelector
          control={control}
          name="fullResetDelay"
          label={l10n.getString(
            'settings-general-gesture_control-fullResetDelay'
          )}
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0.2}
          max={3.0}
          step={0.2}
        />
        <NumberSelector
          control={control}
          name="mountingResetDelay"
          label={l10n.getString(
            'settings-general-gesture_control-mountingResetDelay'
          )}
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0.2}
          max={3.0}
          step={0.2}
        />
      </div>
      <div className="grid sm:grid-cols-3 gap-5">
        <NumberSelector
          control={control}
          name="yawResetTaps"
          label={l10n.getString(
            'settings-general-gesture_control-yawResetTaps'
          )}
          valueLabelFormat={(value) =>
            l10n.getString('settings-general-gesture_control-taps', {
              amount: Math.round(value),
            })
          }
          min={2}
          max={10}
          step={1}
        />
        <NumberSelector
          control={control}
          name="fullResetTaps"
          label={l10n.getString(
            'settings-general-gesture_control-fullResetTaps'
          )}
          valueLabelFormat={(value) =>
            l10n.getString('settings-general-gesture_control-taps', {
              amount: Math.round(value),
            })
          }
          min={2}
          max={10}
          step={1}
        />
        <NumberSelector
          control={control}
          name="mountingResetTaps"
          label={l10n.getString(
            'settings-general-gesture_control-mountingResetTaps'
          )}
          valueLabelFormat={(value) =>
            l10n.getString('settings-general-gesture_control-taps', {
              amount: Math.round(value),
            })
          }
          min={2}
          max={10}
          step={1}
        />
      </div>

      <div className="grid sm:grid-cols-1 gap-1 pt-3">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-gesture_control-numberTrackersOverThreshold'
          )}
        </Typography>
        <Typography>
          {l10n.getString(
            'settings-general-gesture_control-numberTrackersOverThreshold-description'
          )}
        </Typography>

        <div>
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
        </div>
      </div>
    </>
  );
}
