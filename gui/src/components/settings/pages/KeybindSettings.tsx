import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { DefaultValues, useForm } from 'react-hook-form';
import './KeybindSettings.scss';
import { ReactNode, useEffect } from 'react';
import { KeybindRow } from '@/components/commons/KeybindRow';
import { Button } from '@/components/commons/Button';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  KeybindRequestT,
  KeybindResponseT,
  RpcMessage,
  KeybindT,
  KeybindName,
  ChangeKeybindRequestT,
  OpenUriRequestT,
} from 'solarxr-protocol';
import { useAppContext } from '@/hooks/app';

function Table({ children }: { children: ReactNode }) {
  return (
    <table className="min-w-full divide-y divide-background-50">
      <thead>
        <tr>
          <th scope="col" className="px-6 py-3 text-start">
            <Typography id="keybind_config-keybind_name" />
          </th>
          <th scope="col" className="px-6 py-3 text-middle">
            <Typography id="keybind_config-keybind_value" />
          </th>
          <th scope="col" className="px-6 py-3 text-middle">
            <Typography id="keybind_config-keybind_delay" />
          </th>
        </tr>
      </thead>
      <tbody>{children}</tbody>
    </table>
  );
}

export type KeybindsForm = {
  names: {
    fullResetName: KeybindName;
    yawResetName: KeybindName;
    mountingResetName: KeybindName;
    pauseTrackingName: KeybindName;
    feetResetName: KeybindName;
  };
  bindings: {
    fullResetBinding: string[];
    yawResetBinding: string[];
    mountingResetBinding: string[];
    pauseTrackingBinding: string[];
    feetResetBinding: string[];
  };
  delays: {
    fullResetDelay: number;
    yawResetDelay: number;
    mountingResetDelay: number;
    pauseTrackingDelay: number;
    feetResetDelay: number;
  };
};

const defaultValues: KeybindsForm = {
  names: {
    fullResetName: KeybindName.FULL_RESET,
    yawResetName: KeybindName.YAW_RESET,
    mountingResetName: KeybindName.MOUNTING_RESET,
    pauseTrackingName: KeybindName.PAUSE_TRACKING,
    feetResetName: KeybindName.FEET_MOUNTING_RESET,
  },
  bindings: {
    fullResetBinding: ['CTRL', 'ALT', 'SHIFT', 'Y'],
    yawResetBinding: ['CTRL', 'ALT', 'SHIFT', 'U'],
    mountingResetBinding: ['CTRL', 'ALT', 'SHIFT', 'I'],
    pauseTrackingBinding: ['CTRL', 'ALT', 'SHIFT', 'O'],
    feetResetBinding: ['CTRL', 'ALT', 'SHIFT', 'P'],
  },
  delays: {
    fullResetDelay: 0,
    yawResetDelay: 0,
    mountingResetDelay: 0,
    pauseTrackingDelay: 0,
    feetResetDelay: 0,
  },
};

export function KeybindSettings() {
  const { l10n } = useLocalization();
  const { control, reset, handleSubmit, watch, resetField, getValues } =
    useForm<KeybindsForm>({
      defaultValues,
    });
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { installInfo } = useAppContext();

  const onSubmit = (values: KeybindsForm) => {
    console.log('Onsubmit in KeybindSettings');
    const keybinds = new ChangeKeybindRequestT();

    const fullResetKeybind = new KeybindT();
    fullResetKeybind.keybindName = values.names.fullResetName;
    fullResetKeybind.keybindValue = values.bindings.fullResetBinding.join('+');
    fullResetKeybind.keybindDelay = values.delays.fullResetDelay;
    keybinds.keybind.push(fullResetKeybind);

    const yawResetKeybind = new KeybindT();
    yawResetKeybind.keybindName = values.names.yawResetName;
    yawResetKeybind.keybindValue = values.bindings.yawResetBinding.join('+');
    yawResetKeybind.keybindDelay = values.delays.yawResetDelay;
    keybinds.keybind.push(yawResetKeybind);

    const mountingResetKeybind = new KeybindT();
    mountingResetKeybind.keybindName = values.names.mountingResetName;
    mountingResetKeybind.keybindValue =
      values.bindings.mountingResetBinding.join('+');
    mountingResetKeybind.keybindDelay = values.delays.mountingResetDelay;
    keybinds.keybind.push(mountingResetKeybind);

    const pauseTrackingKeybind = new KeybindT();
    pauseTrackingKeybind.keybindName = values.names.pauseTrackingName;
    pauseTrackingKeybind.keybindValue =
      values.bindings.pauseTrackingBinding.join('+');
    pauseTrackingKeybind.keybindDelay = values.delays.pauseTrackingDelay;
    keybinds.keybind.push(pauseTrackingKeybind);

    const feetResetKeybind = new KeybindT();
    feetResetKeybind.keybindName = values.names.feetResetName;
    feetResetKeybind.keybindValue = values.bindings.feetResetBinding.join('+');
    feetResetKeybind.keybindDelay = values.delays.pauseTrackingDelay;
    keybinds.keybind.push(feetResetKeybind);

    sendRPCPacket(RpcMessage.ChangeKeybindRequest, keybinds);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.KeybindRequest, new KeybindRequestT());
  }, []);

  useRPCPacket(RpcMessage.KeybindResponse, ({ keybind }: KeybindResponseT) => {
    if (!keybind) return;

    reset(
      {
        names: {
          fullResetName: KeybindName.FULL_RESET,
          yawResetName: KeybindName.YAW_RESET,
          mountingResetName: KeybindName.MOUNTING_RESET,
          pauseTrackingName: KeybindName.PAUSE_TRACKING,
          feetResetName: KeybindName.FEET_MOUNTING_RESET,
        },
        bindings: {
          fullResetBinding:
            (typeof keybind[KeybindName.FULL_RESET].keybindValue === 'string'
              ? keybind[KeybindName.FULL_RESET].keybindValue
              : ''
            ).split('+') || defaultValues.bindings.fullResetBinding,
          yawResetBinding:
            (typeof keybind[KeybindName.YAW_RESET].keybindValue === 'string'
              ? keybind[KeybindName.YAW_RESET].keybindValue
              : ''
            ).split('+') || defaultValues.bindings.yawResetBinding,
          mountingResetBinding:
            (typeof keybind[KeybindName.MOUNTING_RESET].keybindValue ===
            'string'
              ? keybind[KeybindName.MOUNTING_RESET].keybindValue
              : ''
            ).split('+') || defaultValues.bindings.mountingResetBinding,
          pauseTrackingBinding:
            (typeof keybind[KeybindName.PAUSE_TRACKING].keybindValue ===
            'string'
              ? keybind[KeybindName.PAUSE_TRACKING].keybindValue
              : ''
            ).split('+') || defaultValues.bindings.pauseTrackingBinding,
          feetResetBinding:
            (typeof keybind[KeybindName.FEET_MOUNTING_RESET].keybindValue ===
            'string'
              ? keybind[KeybindName.FEET_MOUNTING_RESET].keybindValue
              : ''
            ).split('+') || defaultValues.bindings.feetResetBinding,
        },
        delays: {
          fullResetDelay:
            keybind[KeybindName.FULL_RESET].keybindDelay ||
            defaultValues.delays.fullResetDelay,
          yawResetDelay:
            keybind[KeybindName.YAW_RESET].keybindDelay ||
            defaultValues.delays.yawResetDelay,
          mountingResetDelay:
            keybind[KeybindName.MOUNTING_RESET].keybindDelay ||
            defaultValues.delays.mountingResetDelay,
          pauseTrackingDelay:
            keybind[KeybindName.PAUSE_TRACKING].keybindDelay ||
            defaultValues.delays.pauseTrackingDelay,
          feetResetDelay:
            keybind[KeybindName.FEET_MOUNTING_RESET].keybindDelay ||
            defaultValues.delays.feetResetDelay,
        },
      },
      {
        keepDefaultValues: true,
      }
    );
  });

  const handleOpenSystemSettingsButton = () => {
    sendRPCPacket(RpcMessage.OpenUriRequest, new OpenUriRequestT());
  };

  const handleResetAllButton = () => {
    reset(defaultValues);
  };

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<WrenchIcon />} id="keybinds">
          <Typography variant="main-title" id="settings-keybinds" />
          <div className="flex flex-col pt-2 pb-4">
            {l10n
              .getString('settings-keybinds-description')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </div>
          {!installInfo?.isWayland ? (
            <div className="flex flex-col gap-4">
              <Typography id="settings-keybinds-wayland-description" />
              <div>
                <Button
                  id="settings-keybinds-wayland-open-system-settings-button"
                  className="flex flex-col"
                  onClick={handleOpenSystemSettingsButton}
                  variant="primary"
                />
              </div>
            </div>
          ) : (
            <div className="keybind-settings">
                <Typography id="keybind_config-keybind_name" />
                <Typography id="keybind_config-keybind_value" />
                <Typography id="keybind_config-keybind_delay" />
              <div />
                <KeybindRow
                  id="settings-keybinds_full-reset"
                  label='Full Reset'
                  control={control}
                  resetField={resetField}
                  name="bindings.fullResetBinding"
                  delay="delays.fullResetDelay"
                />
                <KeybindRow
                  id="settings-keybinds_yaw-reset"
                  control={control}
                  resetField={resetField}
                  name="bindings.yawResetBinding"
                  delay="delays.yawResetDelay"
                />
                <KeybindRow
                  id="settings-keybinds_mounting-reset"
                  control={control}
                  resetField={resetField}
                  name="bindings.mountingResetBinding"
                  delay="delays.mountingResetDelay"
                />
                <KeybindRow
                  id="settings-keybinds_feet-mounting-reset"
                  control={control}
                  resetField={resetField}
                  name="bindings.feetResetBinding"
                  delay="delays.feetResetDelay"
                />
                <KeybindRow
                  id="settings-keybinds_pause-tracking"
                  control={control}
                  resetField={resetField}
                  name="bindings.pauseTrackingBinding"
                  delay="delays.pauseTrackingDelay"
                />
                <Button
                  id="settings-keybinds_reset-all-button"
                  className="justify-self-start"
                  onClick={handleResetAllButton}
                  variant="primary"
                />
            </div>
          )}
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
