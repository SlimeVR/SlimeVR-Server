import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { useLocation } from 'react-router-dom';
import {
  ChangeSettingsRequestT,
  OSCRouterSettingsT,
  OSCSettingsT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { CheckBox } from '../../commons/Checkbox';
import { RouterIcon } from '../../commons/icon/RouterIcon';
import { Input } from '../../commons/Input';
import { Typography } from '../../commons/Typography';
import { SettingsPageLayout } from '../SettingsPageLayout';

interface OSCRouterSettingsForm {
  router: {
    oscSettings: {
      enabled: boolean;
      portIn: number;
      portOut: number;
      address: string;
    };
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
  const { state } = useLocation();
  const pageRef = useRef<HTMLFormElement | null>(null);

  const { reset, control, watch, handleSubmit, register } =
    useForm<OSCRouterSettingsForm>({
      defaultValues: defaultValues,
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

  // Handle scrolling to selected page
  useEffect(() => {
    const typedState: { scrollTo: string } = state as any;
    if (!pageRef.current || !typedState || !typedState.scrollTo) {
      return;
    }
    const elem = pageRef.current.querySelector(`#${typedState.scrollTo}`);
    if (elem) {
      elem.scrollIntoView({ behavior: 'smooth' });
    }
  }, [state]);

  return (
    <form className="flex flex-col gap-2 w-full" ref={pageRef}>
      <SettingsPageLayout icon={<RouterIcon></RouterIcon>} id="router">
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
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </>
          </div>
          <Typography bold>
            {l10n.getString('settings-osc-router-enable')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
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
          <Typography bold>
            {l10n.getString('settings-osc-router-network')}
          </Typography>
          <div className="flex flex-col pb-2">
            <>
              {l10n
                .getString('settings-osc-router-network-description')
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
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
                rules={{ required: true }}
                name="router.oscSettings.portIn"
                placeholder="9002"
                label="Port In"
              ></Input>
            </Localized>
            <Localized
              id="settings-osc-router-network-port_out"
              attrs={{ placeholder: true, label: true }}
            >
              <Input
                type="number"
                control={control}
                rules={{ required: true }}
                name="router.oscSettings.portOut"
                placeholder="9000"
                label="Port Out"
              ></Input>
            </Localized>
          </div>
          <Typography bold>
            {l10n.getString('settings-osc-router-network-address')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {l10n.getString(
                'settings-osc-router-network-address-description'
              )}
            </Typography>
          </div>
          <div className="grid gap-3 pb-5">
            <Input
              type="text"
              control={control}
              rules={{
                required: true,
                pattern:
                  /^(?!0)(?!.*\.$)((1?\d?\d|25[0-5]|2[0-4]\d)(\.|$)){4}$/i,
              }}
              name="router.oscSettings.address"
              placeholder={l10n.getString(
                'settings-osc-router-network-address-placeholder'
              )}
              label="Address"
            ></Input>
          </div>
        </>
      </SettingsPageLayout>
    </form>
  );
}
