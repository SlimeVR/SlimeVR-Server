import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Localized, useLocalization } from '@fluent/react';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeMagToggleRequestT,
  DeviceIdT,
  MagToggleRequestT,
  MagToggleResponseT,
  RpcMessage,
  TrackerIdT,
} from 'solarxr-protocol';

interface MagnetometerToggleForm {
  magToggle: boolean;
}

export function MagnetometerToggleSetting({
  trackerNum,
  deviceId,
  settingType,
}: {
  trackerNum?: number;
  deviceId?: number;
  settingType: 'general' | 'tracker';
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const originalValue = useRef<boolean | null>(null);
  const [waitingMag, setWaitingMag] = useState(false);
  const { control, watch, handleSubmit, reset } =
    useForm<MagnetometerToggleForm>({
      defaultValues: { magToggle: settingType === 'tracker' },
    });

  const onSubmit = useCallback(
    (values: MagnetometerToggleForm) => {
      if (originalValue.current === values.magToggle) return;
      setWaitingMag(true);
      const req = new ChangeMagToggleRequestT();
      if (trackerNum !== undefined) {
        const id = new TrackerIdT(
          deviceId ? new DeviceIdT(deviceId) : undefined,
          trackerNum
        );
        req.trackerId = id;
      }

      req.enable = values.magToggle;
      sendRPCPacket(RpcMessage.ChangeMagToggleRequest, req);
    },
    [trackerNum, deviceId]
  );

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    const req = new MagToggleRequestT();
    if (trackerNum !== undefined) {
      const id = new TrackerIdT(
        deviceId ? new DeviceIdT(deviceId) : undefined,
        trackerNum
      );
      req.trackerId = id;
    }
    sendRPCPacket(RpcMessage.MagToggleRequest, req);
  }, [trackerNum, deviceId]);

  useRPCPacket(RpcMessage.MagToggleResponse, (mag: MagToggleResponseT) => {
    console.log(mag);
    if (
      mag.trackerId?.trackerNum !== trackerNum ||
      mag.trackerId?.deviceId?.id !== deviceId
    ) {
      return;
    }
    console.log('yay');
    originalValue.current = mag.enable;
    setWaitingMag(false);
    reset({ magToggle: mag.enable });
  });

  return settingType === 'general' ? (
    <>
      <div className="flex flex-col pt-5 pb-3">
        <Typography bold>
          {l10n.getString(
            'settings-general-tracker_mechanics-use_mag_on_all_trackers'
          )}
        </Typography>
        <Localized
          id="settings-general-tracker_mechanics-use_mag_on_all_trackers-description"
          elems={{ b: <b></b> }}
        >
          <Typography color="secondary"></Typography>
        </Localized>
      </div>
      <CheckBox
        variant="toggle"
        outlined
        control={control}
        loading={waitingMag}
        name="magToggle"
        label={l10n.getString(
          'settings-general-tracker_mechanics-use_mag_on_all_trackers-label'
        )}
      />
    </>
  ) : (
    <div className="flex flex-col gap-2 w-full mt-3">
      <Typography variant="section-title">
        {l10n.getString('tracker-settings-use_mag')}
      </Typography>
      <Typography color="secondary">
        {l10n.getString('tracker-settings-use_mag-description')}
      </Typography>
      <div className="flex">
        <CheckBox
          variant="toggle"
          outlined
          name="magToggle"
          control={control}
          label={l10n.getString('tracker-settings-use_mag-label')}
        />
      </div>
    </div>
  );
}
