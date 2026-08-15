import { useLocalization, Localized } from '@fluent/react';
import { useEffect, useState } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  ChangeTimeoutSettingsRequestT,
  TimeoutSettingsRequestT,
  TimeoutSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Typography } from '@/components/commons/Typography';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { useLocaleConfig } from '@/i18n/config';

type TimeoutForm = {
  duration: number;
};

const defaultValues: TimeoutForm = {
  duration: 30.0,
};

export function TimeoutSettings() {
  const [ settings, setSettings ] = useState(new TimeoutSettingsResponseT());
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();

  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const { control, watch, handleSubmit, getValues, reset } = useForm<TimeoutForm>({
    defaultValues,
    mode: 'onChange',
    reValidateMode: 'onChange',
  });

  const onSubmit = (values: TimeoutForm) => {
    const settingsReq = new ChangeTimeoutSettingsRequestT();
    settingsReq.duration = values.duration;
    sendRPCPacket(RpcMessage.ChangeTimeoutSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.TimeoutSettingsRequest, new TimeoutSettingsRequestT());
  }, []);

  useEffect(() => {
    const formData: DefaultValues<TimeoutForm> = {
      duration: settings.duration,
    };
    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.TimeoutSettingsResponse,
    (settings: TimeoutSettingsRequestT) => {
      setSettings(settings);
    }
  );

  return (
    <>
      <div className="flex flex-col pb-2 pt-5">
        <Typography variant="section-title">
          {l10n.getString(
            'settings-general-tracker_mechanics-timeout_duration'
          )}
        </Typography>
        <Localized
          id="settings-general-tracker_mechanics-timeout_duration-description"
          elems={{ b: <b /> }}
        >
          <Typography />
        </Localized>
      </div>
      <NumberSelector
          control={control}
          name="duration"
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0.0}
          max={Infinity}
          step={0.5}
        />
    </>
  );
}
