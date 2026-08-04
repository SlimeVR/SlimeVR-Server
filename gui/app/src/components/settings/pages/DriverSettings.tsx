import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import { Button } from '@/components/commons/Button';
import {
  ChangeDriverSettingsRequestT,
  DriverSettingsRequestT,
  DriverSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';

type DriverForm = {
  sendDerivedVelocity: boolean;
};

const defaultValues: DriverForm = {
  sendDerivedVelocity: false,
};

export function DriverSettings() {
  const [settings, setSettings] = useState(new DriverSettingsResponseT());
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { control, watch, handleSubmit, getValues, reset } =
    useForm<DriverForm>({
      defaultValues,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: DriverForm) => {
    const settingsReq = new ChangeDriverSettingsRequestT();
    settingsReq.sendDerivedVelocity = values.sendDerivedVelocity;
    sendRPCPacket(RpcMessage.ChangeDriverSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.DriverSettingsRequest,
      new DriverSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    const formData: DefaultValues<DriverForm> = {
      sendDerivedVelocity: settings.sendDerivedVelocity,
    };
    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.DriverSettingsResponse,
    (res: DriverSettingsResponseT) => setSettings(res)
  );

  return (
    <SettingsPageLayout>
      <SettingsPagePaneLayout icon={<SteamIcon size={24} />} id="driver">
        <>
          <Typography variant="main-title" id="settings-driver" />
          <div className="flex flex-col pt-1 pb-4">
            <Typography id="settings-driver-description" />
          </div>

          <Typography variant="section-title" id="settings-driver-bones" />
          <div className="flex flex-col gap-2 pt-1 pb-4">
            <Typography id="settings-driver-bones-description" />
            <div className="w-fit">
              <Button
                variant="secondary"
                to="/settings/routing"
                id="settings-driver-bones-link"
              />
            </div>
          </div>

          <Typography
            variant="section-title"
            id="settings-general-fk_settings-velocity_settings"
          />
          <div className="pt-1 pb-2">
            <Typography id="settings-general-fk_settings-velocity_settings-description" />
          </div>
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="sendDerivedVelocity"
            label={l10n.getString(
              'settings-general-fk_settings-velocity_settings-send_derived_velocity'
            )}
          />
        </>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
