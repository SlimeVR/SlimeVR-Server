import { Localized, useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeSettingsRequestT,
  OSCRouterSettingsT,
  OSCSettingsT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { RouterIcon } from '@/components/commons/icon/RouterIcon';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { yupResolver } from '@hookform/resolvers/yup';
import { object } from 'yup';
import {
  OSCSettings,
  useOscSettingsValidator,
} from '@/hooks/osc-setting-validator';

interface OSCRouterSettingsForm {
  router: {
    oscSettings: OSCSettings;
  };
}

const defaultValues = {
  router: {
    oscSettings: {
      enabled: false,
      portIn: 9002,
      portOut: 9000,
      address: '127.0.0.1',
    },
  },
};

export function OSCRouterSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { oscValidator } = useOscSettingsValidator();

  const { reset, control, watch, handleSubmit } =
    useForm<OSCRouterSettingsForm>({
      defaultValues: defaultValues,
      reValidateMode: 'onChange',
      mode: 'onChange',
      resolver: yupResolver(
        object({
          router: object({
            oscSettings: oscValidator,
          }),
        })
      ),
    });

  const onSubmit = (values: OSCRouterSettingsForm) => {
    const settings = new ChangeSettingsRequestT();

    if (values.router) {
      const router = new OSCRouterSettingsT();

      router.oscSettings = Object.assign(
        new OSCSettingsT(),
        values.router.oscSettings
      );

      settings.oscRouter = router;
    }
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    const formData: OSCRouterSettingsForm = defaultValues;
    if (settings.oscRouter) {
      if (settings.oscRouter.oscSettings) {
        formData.router.oscSettings.enabled =
          settings.oscRouter.oscSettings.enabled;
        if (settings.oscRouter.oscSettings.portIn)
          formData.router.oscSettings.portIn =
            settings.oscRouter.oscSettings.portIn;
        if (settings.oscRouter.oscSettings.portOut)
          formData.router.oscSettings.portOut =
            settings.oscRouter.oscSettings.portOut;
        if (settings.oscRouter.oscSettings.address)
          formData.router.oscSettings.address =
            settings.oscRouter.oscSettings.address.toString();
      }
    }

    reset(formData);
  });

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<RouterIcon />} id="router">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-osc-router')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              <>
                {l10n
                  .getString('settings-osc-router-description')
                  .split('\n')
                  .map((line, i) => (
                    <Typography key={i}>{line}</Typography>
                  ))}
              </>
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-router-enable')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-router-enable-description')}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="router.oscSettings.enabled"
                label={l10n.getString('settings-osc-router-enable-label')}
              />
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-router-network')}
            </Typography>
            <div className="flex flex-col pb-2">
              <>
                {l10n
                  .getString('settings-osc-router-network-description')
                  .split('\n')
                  .map((line, i) => (
                    <Typography key={i}>{line}</Typography>
                  ))}
              </>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <Localized
                id="settings-osc-router-network-port_in"
                attrs={{ placeholder: true, label: true }}
              >
                <Input
                  type="number"
                  control={control}
                  name="router.oscSettings.portIn"
                  placeholder="9002"
                  label=""
                />
              </Localized>
              <Localized
                id="settings-osc-router-network-port_out"
                attrs={{ placeholder: true, label: true }}
              >
                <Input
                  type="number"
                  control={control}
                  name="router.oscSettings.portOut"
                  placeholder="9000"
                  label=""
                />
              </Localized>
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-router-network-address')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString(
                  'settings-osc-router-network-address-description'
                )}
              </Typography>
            </div>
            <div className="grid gap-3 pb-5">
              <Input
                type="text"
                control={control}
                name="router.oscSettings.address"
                placeholder={l10n.getString(
                  'settings-osc-router-network-address-placeholder'
                )}
                label=""
              />
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
