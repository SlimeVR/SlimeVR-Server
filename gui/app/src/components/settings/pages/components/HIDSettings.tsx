import { SettingDef } from '@/components/settings/settings-definitions';
import { Localized } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeHIDSettingsRequestT,
  HIDSettingsRequestT,
  HIDSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { settingReset } from '@/components/settings/SettingReset';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { SettingRow } from '@/components/settings/SettingRow';

type HIDForm = {
  trackersOverHID: boolean;
};

function toForm(settings: HIDSettingsResponseT): HIDForm {
  return { trackersOverHID: settings.trackersOverHid };
}

const hidSettingsAtom = atom<HIDSettingsResponseT | null>(null);
const hidSettingsValueAtom = selectAtom(
  hidSettingsAtom,
  (settings) => settings,
  isEqual
);

export function HIDSettings({ setting }: { setting: SettingDef }) {
  const setSettings = useSetAtom(hidSettingsAtom);
  const settings = useAtomValue(hidSettingsValueAtom);
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const defaults = useSettingsDefaults().hid;

  const { control, watch, handleSubmit, reset, setValue } = useForm<HIDForm>({
    defaultValues: toForm(defaults),
    mode: 'onChange',
    reValidateMode: 'onChange',
  });

  const onSubmit = (values: HIDForm) => {
    const settingsReq = new ChangeHIDSettingsRequestT();
    settingsReq.trackersOverHid = values.trackersOverHID;
    sendRPCPacket(RpcMessage.ChangeHIDSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(RpcMessage.HIDSettingsRequest, new HIDSettingsRequestT());
  }, []);

  useEffect(() => {
    if (!settings) return;
    reset(toForm(settings));
  }, [settings]);

  useRPCPacket(
    RpcMessage.HIDSettingsResponse,
    (settings: HIDSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const resetTrackersOverHid = settingReset<HIDForm, 'trackersOverHID'>(
    defaults.trackersOverHid,
    watch('trackersOverHID'),
    (next) => {
      setValue('trackersOverHID', next);
      handleSubmit(onSubmit)();
    }
  );

  return (
    <SettingRow
      setting={setting}
      description={null}
      details={
        <Localized
          id="settings-general-tracker_mechanics-trackers_over_usb-description"
          elems={{ b: <b /> }}
        >
          <Typography />
        </Localized>
      }
      controlId="trackers-over-usb"
      {...resetTrackersOverHid}
      control={
        <CheckBox
          variant="toggle"
          control={control}
          name="trackersOverHID"
          id="trackers-over-usb"
        />
      }
    />
  );
}
