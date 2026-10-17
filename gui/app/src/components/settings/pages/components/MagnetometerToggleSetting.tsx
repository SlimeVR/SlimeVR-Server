import { SettingDef } from '@/components/settings/settings-definitions';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Localized, useLocalization } from '@fluent/react';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeMagToggleRequestT,
  MagToggleRequestT,
  MagToggleResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { Link } from 'react-router-dom';
import { SettingRow } from '@/components/settings/SettingRow';
import { settingReset } from '@/components/settings/SettingReset';
import { useSettingsDefaults } from '@/hooks/settings-defaults';

interface MagnetometerToggleForm {
  magToggle: boolean;
}

type MagnetometerToggleSettingProps =
  | { settingType: 'general'; setting: SettingDef; trackerId?: undefined }
  | { settingType: 'tracker'; trackerId: number; setting?: undefined };

export function MagnetometerToggleSetting({
  trackerId,
  settingType,
  setting,
}: MagnetometerToggleSettingProps) {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const originalValue = useRef<boolean | null>(null);
  // used to disable the tracker specific toggle if false
  const [globalToggle, setGlobalToggle] = useState(false);
  const [waitingMag, setWaitingMag] = useState(false);
  const { control, watch, handleSubmit, reset, setValue } =
    useForm<MagnetometerToggleForm>({
      defaultValues: { magToggle: settingType === 'tracker' },
    });

  const defaultMag = useSettingsDefaults().mag.enable;

  const onSubmit = useCallback(
    (values: MagnetometerToggleForm) => {
      if (originalValue.current === values.magToggle) return;
      setWaitingMag(true);
      const req = new ChangeMagToggleRequestT();
      if (trackerId !== undefined) {
        req.trackerId = trackerId;
      }

      req.enable = values.magToggle;
      sendRPCPacket(RpcMessage.ChangeMagToggleRequest, req);
    },
    [trackerId]
  );

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    const req = new MagToggleRequestT();
    if (trackerId !== undefined) {
      req.trackerId = trackerId;
      sendRPCPacket(RpcMessage.MagToggleRequest, new MagToggleRequestT());
    }
    sendRPCPacket(RpcMessage.MagToggleRequest, req);
  }, [trackerId]);

  useRPCPacket(RpcMessage.MagToggleResponse, (mag: MagToggleResponseT) => {
    if (trackerId !== undefined && mag.trackerId === undefined) {
      setGlobalToggle(mag.enable);
    }
    if (mag.trackerId !== trackerId) {
      return;
    }
    originalValue.current = mag.enable;
    setWaitingMag(false);
    reset({ magToggle: mag.enable });
  });

  const resetMag = settingReset<MagnetometerToggleForm, 'magToggle'>(
    defaultMag,
    watch('magToggle'),
    (next) => setValue('magToggle', next)
  );

  return settingType === 'general' ? (
    <SettingRow
      setting={setting}
      description={null}
      details={
        <Localized
          id="settings-general-tracker_mechanics-use_mag_on_all_trackers-description"
          elems={{ b: <b /> }}
        >
          <Typography whitespace="whitespace-pre-line" />
        </Localized>
      }
      controlId="trackers-magnetometer"
      {...resetMag}
      control={
        <CheckBox
          variant="toggle"
          control={control}
          loading={waitingMag}
          name="magToggle"
          id="trackers-magnetometer"
        />
      }
    />
  ) : (
    <div className="flex flex-col gap-2 w-full mt-3">
      <Typography variant="section-title">
        {l10n.getString('tracker-settings-use_mag')}
      </Typography>
      <Localized
        id="tracker-settings-use_mag-description"
        elems={{
          b: <b />,
          magSetting: <Link to="/settings/trackers" className="underline" />,
        }}
      >
        <Typography whitespace="whitespace-pre-line" />
      </Localized>
      <div className="flex">
        <CheckBox
          variant="toggle"
          outlined
          loading={waitingMag}
          disabled={!globalToggle}
          name="magToggle"
          control={control}
          label={l10n.getString('tracker-settings-use_mag-label')}
        />
      </div>
    </div>
  );
}
