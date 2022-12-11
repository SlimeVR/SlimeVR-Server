import { useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useLocation } from 'react-router-dom';
import {
  ChangeSettingsRequestT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  OSCRouterSettingsT,
  OSCSettingsT,
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
  const { t } = useTranslation();
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
            {t('settings.osc.router.title')}
          </Typography>
          <div className="flex flex-col pt-2 pb-4">
            <Typography color="secondary">
              {t('settings.osc.router.description.p0')}
            </Typography>
            <Typography color="secondary">
              {t('settings.osc.router.description.p1')}
            </Typography>
          </div>
          <Typography bold>{t('settings.osc.router.enable.title')}</Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.router.enable.description')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="router.oscSettings.enabled"
              label={t('settings.osc.router.enable.label')}
            />
          </div>
          <Typography bold>{t('settings.osc.router.network.title')}</Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.router.network.description.p0')}
            </Typography>
            <Typography color="secondary">
              {t('settings.osc.router.network.description.p1')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <Input
              type="number"
              {...register('router.oscSettings.portIn', { required: true })}
              placeholder={t('settings.osc.router.network.port-in.placeholder')}
              label={t('settings.osc.router.network.port-in.label')}
            ></Input>
            <Input
              type="number"
              {...register('router.oscSettings.portOut', {
                required: true,
              })}
              placeholder={t(
                'settings.osc.router.network.port-out.placeholder'
              )}
              label={t('settings.osc.router.network.port-out.label')}
            ></Input>
          </div>
          <Typography bold>
            {t('settings.osc.router.network.address.title')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.router.network.address.description')}
            </Typography>
          </div>
          <div className="grid gap-3 pb-5">
            <Input
              type="text"
              {...register('router.oscSettings.address', {
                required: true,
                pattern:
                  /^(?!0)(?!.*\.$)((1?\d?\d|25[0-5]|2[0-4]\d)(\.|$)){4}$/i,
              })}
              placeholder={t('settings.osc.router.network.address.placeholder')}
            ></Input>
          </div>
        </>
      </SettingsPageLayout>
    </form>
  );
}
