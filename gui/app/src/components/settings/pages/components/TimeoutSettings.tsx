import { SettingDef } from '@/components/settings/settings-definitions';
import { Localized } from '@fluent/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeTimeoutSettingsRequestT,
  TimeoutSettingsRequestT,
  TimeoutSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { settingReset } from '@/components/settings/SettingReset';
import { Typography } from '@/components/commons/Typography';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { useLocaleConfig } from '@/i18n/config';
import { SettingRow } from '@/components/settings/SettingRow';

type TimeoutForm = {
  delay: number;
};

function toForm(settings: TimeoutSettingsResponseT): TimeoutForm {
  return { delay: settings.delay };
}

export function TimeoutSettings({ setting }: { setting: SettingDef }) {
  const [settings, setSettings] = useState<TimeoutSettingsResponseT | null>(
    null
  );
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();
  const defaults = useSettingsDefaults().timeout;

  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const { control, watch, handleSubmit, reset, setValue } =
    useForm<TimeoutForm>({
      defaultValues: toForm(defaults),
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const onSubmit = (values: TimeoutForm) => {
    const settingsReq = new ChangeTimeoutSettingsRequestT();
    settingsReq.delay = values.delay;
    sendRPCPacket(RpcMessage.ChangeTimeoutSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.TimeoutSettingsRequest,
      new TimeoutSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    if (!settings) return;
    reset(toForm(settings));
  }, [settings]);

  useRPCPacket(
    RpcMessage.TimeoutSettingsResponse,
    (settings: TimeoutSettingsResponseT) => {
      setSettings(settings);
    }
  );

  const resetDelay = settingReset<TimeoutForm, 'delay'>(
    defaults.delay,
    watch('delay'),
    (next) => {
      setValue('delay', next);
      handleSubmit(onSubmit)();
    }
  );

  return (
    <SettingRow
      setting={setting}
      description={null}
      details={
        <Localized
          id="settings-general-tracker_mechanics-timeout_delay-description"
          elems={{ b: <b /> }}
        >
          <Typography />
        </Localized>
      }
      controlWidth="wide"
      {...resetDelay}
      control={
        <NumberSelector
          control={control}
          name="delay"
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0.0}
          max={3600}
          step={1.0}
        />
      }
    />
  );
}
