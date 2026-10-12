import { Localized } from '@fluent/react';
import { useEffect } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  ChangeHIDSettingsRequestT,
  HIDSettingsRequestT,
  HIDSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { SettingRow } from '@/components/settings/SettingRow';

type HIDForm = {
  trackersOverHID: boolean;
};

const defaultValues: HIDForm = {
  trackersOverHID: false,
};

const hidSettingsAtom = atom(new HIDSettingsResponseT());
const hidSettingsValueAtom = selectAtom(
  hidSettingsAtom,
  (settings) => settings,
  isEqual
);

export function HIDSettings() {
  const setSettings = useSetAtom(hidSettingsAtom);
  const settings = useAtomValue(hidSettingsValueAtom);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { control, watch, handleSubmit, getValues, reset } = useForm<HIDForm>({
    defaultValues,
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

  useEffect(() => {
    sendRPCPacket(RpcMessage.HIDSettingsRequest, new HIDSettingsRequestT());
  }, []);

  useEffect(() => {
    const formData: DefaultValues<HIDForm> = {
      trackersOverHID: settings.trackersOverHid,
    };
    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.HIDSettingsResponse,
    (settings: HIDSettingsResponseT) => {
      setSettings(settings);
    }
  );

  return (
    <SettingRow
      title="settings-general-tracker_mechanics-trackers_over_usb"
      details={
        <Localized
          id="settings-general-tracker_mechanics-trackers_over_usb-description"
          elems={{ b: <b /> }}
        >
          <Typography />
        </Localized>
      }
      controlId="trackers-over-usb"
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
