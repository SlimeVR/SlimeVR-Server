import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
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
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { settingReset } from '@/components/settings/SettingReset';
import { CheckBox } from '@/components/commons/Checkbox';
import {
  StatusBadge,
  StatusRow,
  type StatusVariant,
} from '@/components/commons/StatusBadge';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
  SettingsGroupLabel,
} from '@/components/settings/SettingRow';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

type DriverForm = {
  enabled: boolean;
  sendVelocity: boolean;
};

function toForm(settings: DriverSettingsResponseT): DriverForm {
  return {
    enabled: settings.enabled,
    sendVelocity: settings.sendVelocity,
  };
}

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
    <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-lg divide-y divide-background-60">
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
        <Typography id={`settings-driver-status-connection-${badge}`} />
      </StatusRow>
    </div>
  );
}

export const driverSettingsPage = defineSettingsPage({
  path: 'driver',
  title: 'settings-sidebar-driver',
  groups: {
    output: {
      title: 'settings-driver-output_group',
      rows: {
        enable: {
          title: 'settings-driver-enable',
          description: 'settings-driver-enable-description',
        },
        bones: {
          title: 'settings-driver-bones',
          description: 'settings-driver-bones-description',
        },
      },
    },
    data: {
      title: 'settings-driver-data_group',
      rows: {
        velocity: {
          title: 'settings-driver-velocity',
          description: 'settings-driver-velocity-description',
        },
      },
    },
  },
});

const { groups } = driverSettingsPage;

export function DriverSettings() {
  const [settings, setSettings] = useState<DriverSettingsResponseT | null>(
    null
  );
  const [status, setStatus] = useState<DriverStatusChangeResponseT | null>(
    null
  );
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const defaults = useSettingsDefaults().driver;

  const { control, watch, handleSubmit, reset, setValue } = useForm<DriverForm>(
    {
      defaultValues: toForm(defaults),
      mode: 'onChange',
      reValidateMode: 'onChange',
    }
  );

  const onSubmit = (values: DriverForm) => {
    const settingsReq = new ChangeDriverSettingsRequestT();
    settingsReq.enabled = values.enabled;
    settingsReq.sendVelocity = values.sendVelocity;
    sendRPCPacket(RpcMessage.ChangeDriverSettingsRequest, settingsReq);
  };

  const enabled = watch('enabled');

  const resetTo = (name: keyof DriverForm) =>
    settingReset<DriverForm, typeof name>(
      defaults?.[name],
      watch(name),
      (next) => {
        setValue(name, next);
        handleSubmit(onSubmit)();
      }
    );

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.DriverSettingsRequest,
      new DriverSettingsRequestT()
    );
    sendRPCPacket(RpcMessage.DriverStatusRequest, new DriverStatusRequestT());
  }, []);

  useEffect(() => {
    if (!settings) return;
    reset(toForm(settings));
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
    <SettingsPagePaneLayout
      icon={<SteamIcon size={24} />}
      title={driverSettingsPage.title}
    >
      <div className="flex flex-col">
        <div className="pb-4 max-w-[75ch]">
          <Typography id="settings-driver-description" />
        </div>

        <SettingsGroup group={groups.output}>
          <SettingRow
            setting={groups.output.rows.enable}
            controlId="driver-enabled"
            {...resetTo('enabled')}
            control={
              <CheckBox
                variant="toggle"
                control={control}
                name="enabled"
                id="driver-enabled"
              />
            }
          />
          <SettingRow
            setting={groups.output.rows.bones}
            control={
              <Button
                variant="secondary"
                className="whitespace-nowrap"
                to="/settings/routing"
                id="settings-driver-bones-link"
              />
            }
          />
        </SettingsGroup>

        {enabled && status && (
          <div className="flex flex-col gap-2 mt-8 min-w-0">
            <div className="px-3 xs:px-4">
              <SettingsGroupLabel id="settings-driver-status-title" />
            </div>
            <StatusCard status={status} />
          </div>
        )}

        <SettingsAdvanced>
          <SettingsGroup group={groups.data}>
            <SettingRow
              setting={groups.data.rows.velocity}
              controlId="driver-velocity"
              {...resetTo('sendVelocity')}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="sendVelocity"
                  id="driver-velocity"
                />
              }
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>
    </SettingsPagePaneLayout>
  );
}
