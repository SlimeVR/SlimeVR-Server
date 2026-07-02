import { useLocalization, Localized } from '@fluent/react';
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
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPageLayout';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';

type HIDForm = {
  hidSettings: {
    trackersOverHID: boolean;
  };
};

const defaultValues: HIDForm = {
  hidSettings: { trackersOverHID: false },
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
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { control, watch, handleSubmit, getValues, reset } = useForm<HIDForm>({
    defaultValues,
    mode: 'onChange',
    reValidateMode: 'onChange',
  });

  const onSubmit = (values: HIDForm) => {
    const settingsReq = new ChangeHIDSettingsRequestT();
    settingsReq.trackersOverHid = values.hidSettings.trackersOverHID;
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
    const formData: DefaultValues<HIDForm> = {};
    formData.hidSettings = {
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
    <SettingsPagePaneLayout icon={<WrenchIcon />} id="mechanics-hid">
      <>
        <div className="flex flex-col pt-5 pb-3">
          <Typography variant="section-title">
            {l10n.getString(
              'settings-general-tracker_mechanics-trackers_over_usb'
            )}
          </Typography>
          <Localized
            id="settings-general-tracker_mechanics-trackers_over_usb-description"
            elems={{ b: <b /> }}
          >
            <Typography />
          </Localized>
        </div>
        <CheckBox
          variant="toggle"
          outlined
          control={control}
          name="hidSettings.trackersOverHID"
          label={l10n.getString(
            'settings-general-tracker_mechanics-trackers_over_usb-enabled-label'
          )}
        />
      </>
    </SettingsPagePaneLayout>
  );
}
