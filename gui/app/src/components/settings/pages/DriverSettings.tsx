import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import { Button } from '@/components/commons/Button';
import {
  ChangeDriverSettingsRequestT,
  DriverConnectionState,
  DriverSettingsRequestT,
  DriverSettingsResponseT,
  DriverStatusChangeResponseT,
  DriverStatusRequestT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import {
  StatusBadge,
  StatusRow,
  type StatusVariant,
} from '@/components/commons/StatusBadge';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';

type DriverForm = {
  enabled: boolean;
};

const defaultValues: DriverForm = {
  enabled: true,
};

type Badge = 'connected' | 'waiting' | 'disabled' | 'unavailable';

const BADGE_VARIANTS: Record<Badge, StatusVariant> = {
  connected: 'success',
  waiting: 'special',
  disabled: 'neutral',
  unavailable: 'neutral',
};

const CONNECTION_BADGES: Record<DriverConnectionState, Badge> = {
  [DriverConnectionState.UNSUPPORTED]: 'unavailable',
  [DriverConnectionState.DISABLED]: 'disabled',
  [DriverConnectionState.WAITING]: 'waiting',
  [DriverConnectionState.CONNECTED]: 'connected',
};

function StatusCard({ status }: { status: DriverStatusChangeResponseT }) {
  const state = status.state ?? DriverConnectionState.UNSUPPORTED;
  const badge = CONNECTION_BADGES[state];

  return (
    <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-md divide-y divide-background-60">
      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-driver-status-connection"
          />
        }
        badge={
          <StatusBadge
            variant={BADGE_VARIANTS[badge]}
            id={`settings-driver-status-badge-${badge}`}
          />
        }
      >
        <Typography
          color="secondary"
          id={`settings-driver-status-connection-${badge}`}
        />
      </StatusRow>
    </div>
  );
}

export function DriverSettings() {
  const [settings, setSettings] = useState<DriverSettingsResponseT | null>(
    null
  );
  const [status, setStatus] = useState<DriverStatusChangeResponseT | null>(
    null
  );
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
    settingsReq.enabled = values.enabled;
    sendRPCPacket(RpcMessage.ChangeDriverSettingsRequest, settingsReq);
  };

  const enabled = watch('enabled');

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
    sendRPCPacket(RpcMessage.DriverStatusRequest, new DriverStatusRequestT());
  }, []);

  useEffect(() => {
    if (!settings) return;
    const formData: DefaultValues<DriverForm> = {
      enabled: settings.enabled,
    };
    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.DriverSettingsResponse,
    (res: DriverSettingsResponseT) => setSettings(res)
  );

  useRPCPacket(
    RpcMessage.DriverStatusChangeResponse,
    (res: DriverStatusChangeResponseT) => setStatus(res)
  );

  return (
    <SettingsPageLayout>
      <SettingsPagePaneLayout icon={<SteamIcon size={24} />} id="driver">
        <>
          <Typography variant="main-title" id="settings-driver" />
          <div className="flex flex-col pt-1 pb-4">
            <Typography id="settings-driver-description" />
          </div>

          <Typography variant="section-title" id="settings-driver-enable" />
          <div className="flex flex-col pt-1 pb-2">
            <Typography id="settings-driver-enable-description" />
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="enabled"
              label={l10n.getString('settings-driver-enable-label')}
            />
          </div>

          {enabled && status && (
            <>
              <Typography
                variant="section-title"
                id="settings-driver-status-title"
              />
              <StatusCard status={status} />
            </>
          )}

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
        </>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
