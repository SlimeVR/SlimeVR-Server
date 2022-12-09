import { useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useLocation } from 'react-router-dom';
import {
  ChangeSettingsRequestT,
  OSCTrackersSettingT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  VRCOSCSettingsT
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { CheckBox } from '../../commons/Checkbox';
import { VRCIcon } from '../../commons/icon/VRCIcon';
import { Input } from '../../commons/Input';
import { Typography } from '../../commons/Typography';
import { SettingsPageLayout } from '../SettingsPageLayout';

interface OSCSettingsForm {
  vrchat: {
    enabled: boolean;
    portIn: number;
    portOut: number;
    address: string;
    trackers: {
      head: boolean;
      chest: boolean;
      elbows: boolean;
      feet: boolean;
      knees: boolean;
      hands: boolean;
      waist: boolean;
    };
  };
}

const defaultValues = {
  vrchat: {
    enabled: false,
    portIn: 9001,
    portOut: 9000,
    address: '127.0.0.1',
    trackers: {
      head: false,
      chest: false,
      elbows: false,
      feet: false,
      knees: false,
      hands: false,
      waist: false,
    },
  },
};

export function OSCSettings() {
  const { t } = useTranslation();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { state } = useLocation();
  const pageRef = useRef<HTMLFormElement | null>(null);

  const { reset, control, watch, handleSubmit, register } =
    useForm<OSCSettingsForm>({
      defaultValues: defaultValues,
    });

  const onSubmit = (values: OSCSettingsForm) => {
    const settings = new ChangeSettingsRequestT();

    if (values.vrchat) {
      const vrcOsc = new VRCOSCSettingsT();
      vrcOsc.enabled = values.vrchat.enabled;
      vrcOsc.portIn = values.vrchat.portIn;
      vrcOsc.portOut = values.vrchat.portOut;
      vrcOsc.address = values.vrchat.address;
      vrcOsc.trackers = Object.assign(
        new OSCTrackersSettingT(),
        values.vrchat.trackers
      );

      settings.vrcOsc = vrcOsc;
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
    const formData: OSCSettingsForm = defaultValues;
    if (settings.vrcOsc) {
      if (settings.vrcOsc.enabled)
        formData.vrchat.enabled = settings.vrcOsc.enabled;
      if (settings.vrcOsc.portIn)
        formData.vrchat.portIn = settings.vrcOsc.portIn;
      if (settings.vrcOsc.portOut)
        formData.vrchat.portOut = settings.vrcOsc.portOut;
      if (settings.vrcOsc.trackers)
        formData.vrchat.trackers = settings.vrcOsc.trackers;
      if (settings.vrcOsc.address)
        formData.vrchat.address = settings.vrcOsc.address.toString();
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
      <SettingsPageLayout icon={<VRCIcon></VRCIcon>} id="vrchat">
        <>
          <Typography variant="main-title">VRChat</Typography>
          <div className="flex flex-col pt-2 pb-4">
            <Typography color="secondary">
              {t('settings.osc.vrchat.description.p0')}
            </Typography>
            <Typography color="secondary">
              {t('settings.osc.vrchat.description.p1')}
            </Typography>
          </div>
          <Typography bold>{t('settings.osc.vrchat.enable.title')}</Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.vrchat.enable.description')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vrchat.enabled"
              label={t('settings.osc.vrchat.enable.label')}
            />
          </div>
          <Typography bold>{t('settings.osc.vrchat.network.title')}</Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.vrchat.network.description')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <Input
              type="number"
              {...register('vrchat.portIn', { required: true })}
              placeholder={t('settings.osc.vrchat.network.port-in.placeholder')}
              label={t('settings.osc.vrchat.network.port-in.label')}
            ></Input>
            <Input
              type="number"
              {...register('vrchat.portOut', { required: true })}
              placeholder={t(
                'settings.osc.vrchat.network.port-out.placeholder'
              )}
              label={t('settings.osc.vrchat.network.port-out.label')}
            ></Input>
          </div>
          <Typography bold>
            {t('settings.osc.vrchat.network.address.title')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.vrchat.network.address.description')}
            </Typography>
          </div>
          <div className="grid gap-3 pb-5">
            <Input
              type="text"
              {...register('vrchat.address', {
                required: true,
                pattern:
                  /^(?!0)(?!.*\.$)((1?\d?\d|25[0-5]|2[0-4]\d)(\.|$)){4}$/i,
              })}
              placeholder={t('settings.osc.vrchat.network.address.placeholder')}
            ></Input>
          </div>
          <Typography bold>
            {t('settings.osc.vrchat.network.trackers.title')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {t('settings.osc.vrchat.network.trackers.description')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vrchat.trackers.chest"
              label={t('settings.osc.vrchat.network.trackers.chest')}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vrchat.trackers.waist"
              label={t('settings.osc.vrchat.network.trackers.waist')}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vrchat.trackers.knees"
              label={t('settings.osc.vrchat.network.trackers.knees')}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vrchat.trackers.feet"
              label={t('settings.osc.vrchat.network.trackers.feet')}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vrchat.trackers.elbows"
              label={t('settings.osc.vrchat.network.trackers.elbows')}
            />
          </div>
        </>
      </SettingsPageLayout>
    </form>
  );
}
