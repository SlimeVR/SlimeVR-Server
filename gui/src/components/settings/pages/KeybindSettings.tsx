import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { useForm } from 'react-hook-form';
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
} from 'solarxr-protocol';

export type KeybindsForm = {
  names: {
    fullResetName: KeybindName;
    yawResetName: KeybindName;
    mountingResetName: KeybindName;
    pauseTrackingName: KeybindName;
  };
  bindings: {
    fullResetBinding: string[];
    yawResetBinding: string[];
    mountingResetBinding: string[];
    pauseTrackingBinding: string[];
  };
  delays: {
    fullResetDelay: number;
    yawResetDelay: number;
    mountingResetDelay: number;
    pauseTrackingDelay: number;
  };
};

const defaultValues: KeybindsForm = {
  names: {
    fullResetName: KeybindName.FULL_RESET,
    yawResetName: KeybindName.YAW_RESET,
    mountingResetName: KeybindName.MOUNTING_RESET,
    pauseTrackingName: KeybindName.PAUSE_TRACKING,
  },
  bindings: {
    fullResetBinding: ['CTRL', 'ALT', 'SHIFT', 'Y'],
    yawResetBinding: ['CTRL', 'ALT', 'SHIFT', 'U'],
    mountingResetBinding: ['CTRL', 'ALT', 'SHIFT', 'I'],
    pauseTrackingBinding: ['CTRL', 'ALT', 'SHIFT', 'O'],
  },
  delays: {
    fullResetDelay: 0,
    yawResetDelay: 0,
    mountingResetDelay: 0,
    pauseTrackingDelay: 0,
  },
};


export function useKeybindsForm() {
  const {
    register,
    reset,
    handleSubmit,
    formState,
    control,
    getValues,
    setValue,
    watch,
  } = useForm<KeybindsForm>({
    defaultValues,
  });

  return {
    control,
    register,
    reset,
    handleSubmit,
    formState,
    getValues,
    setValue,
    watch,
  };
}

export function KeybindSettings() {
  const { l10n } = useLocalization();
  const { control, reset, handleSubmit, watch, getValues, setValue } = useKeybindsForm();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const onSubmit = (values: KeybindsForm) => {
    const keybinds = new ChangeKeybindRequestT();

    const fullResetKeybind = new KeybindT();
    fullResetKeybind.keybindName = values.names.fullResetName;
    fullResetKeybind.keybindValue = values.bindings.fullResetBinding.join('+');
    fullResetKeybind.keybindDelay = BigInt(values.delays.fullResetDelay);
    keybinds.keybind.push(fullResetKeybind);

    const yawResetKeybind = new KeybindT();
    yawResetKeybind.keybindName = values.names.yawResetName;
    yawResetKeybind.keybindValue = values.bindings.yawResetBinding.join('+');
    yawResetKeybind.keybindDelay = BigInt(values.delays.yawResetDelay);
    keybinds.keybind.push(yawResetKeybind);

    const mountingResetKeybind = new KeybindT();
    mountingResetKeybind.keybindName = values.names.mountingResetName;
    mountingResetKeybind.keybindValue =
      values.bindings.mountingResetBinding.join('+');
    mountingResetKeybind.keybindDelay = BigInt(values.delays.mountingResetDelay);
    keybinds.keybind.push(mountingResetKeybind);

    const pauseTrackingKeybind = new KeybindT();
    pauseTrackingKeybind.keybindName = values.names.pauseTrackingName;
    pauseTrackingKeybind.keybindValue =
      values.bindings.pauseTrackingBinding.join('+');
    pauseTrackingKeybind.keybindDelay = BigInt(values.delays.pauseTrackingDelay);
    keybinds.keybind.push(pauseTrackingKeybind);

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

    console.log(`Keybind Name ${keybind[0].keybindName}`);
    console.log(`Keybind value ${keybind[0].keybindValue}`);
    console.log(`Keybind Delay ${keybind[0].keybindDelay}`);
    const keybindValues: KeybindsForm = {
      names: {
        fullResetName: KeybindName.FULL_RESET,
        yawResetName: KeybindName.YAW_RESET,
        mountingResetName: KeybindName.MOUNTING_RESET,
        pauseTrackingName: KeybindName.PAUSE_TRACKING,
      },
      bindings: {
        fullResetBinding: (typeof keybind[0].keybindValue === 'string'
          ? keybind[0].keybindValue
          : ''
        ).split('+'),

        yawResetBinding: (typeof keybind[1].keybindValue === 'string'
          ? keybind[1].keybindValue
          : ''
        ).split('+'),

        mountingResetBinding: (typeof keybind[2].keybindValue === 'string'
          ? keybind[2].keybindValue
          : ''
        ).split('+'),

        pauseTrackingBinding: (typeof keybind[3].keybindValue === 'string'
          ? keybind[3].keybindValue
          : ''
        ).split('+'),
      },
      delays: {
        fullResetDelay: Number(keybind[0].keybindDelay) ?? 0,
        yawResetDelay: Number(keybind[1].keybindDelay) ?? 0,
        mountingResetDelay: Number(keybind[2].keybindDelay) ?? 0,
        pauseTrackingDelay: Number(keybind[3].keybindDelay) ?? 0,
      },
    };
    console.log(keybindValues);
    // Is this the correct syntax for setting the form with received data?
    reset({ ...getValues(), ...keybindValues });
  });

  const handleResetButton = () => {
    reset(defaultValues);
  };

  function Table({ children }: { children: ReactNode}) {
    return (
        <table className="min-w-full divide-y divide-background-50">
            <thead>
                <tr>
                    <th scope="col" className="px-6 py-3 text-start">
                    <Localized id={'keybind_config-keybind_name'}>
                        <Typography />
                    </Localized>
                        Keybind
                    </th>
                    <th scope="col" className="px-6 py-3 text-center">
                    <Localized id={'keybind_config-keybind_value'}>
                        <Typography />
                    </Localized>
                        Combination
                    </th>
                    <th scope="col" className="px-6 py-3 text-center">
                    <Localized id={'keybind_config-keybind_delay'}>
                        <Typography />
                    </Localized>
                        Delay before trigger
                    </th>
                    <th scope="col" className="px-6 py-3 text-center">
                    <Localized id={'keybind_config-kybind_actions'}>
                        <Typography />
                    </Localized>
                    Actions
                    </th>
                </tr>
            </thead>
            <tbody>{children}</tbody>
        </table>
    );
  }

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<WrenchIcon />} id="keybinds">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-keybinds')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              {l10n
                .getString('settings-keybinds-description')
                .split('\n')
                .map((line, i) => (
                  <Typography key={i}>{line}</Typography>
                ))}
            </div>
            <Table>
                <KeybindRow
                  label="Full Reset"
                  control={control}
                  setValue={setValue}
                  bindingName="bindings.fullResetBinding"
                  delayName="delays.fullResetDelay"
                />
                <KeybindRow
                  label="Yaw Reset"
                  control={control}
                  setValue={setValue}
                  bindingName="bindings.yawResetBinding"
                  delayName="delays.yawResetDelay"
                />
                <KeybindRow
                  label="Mounting Reset"
                  control={control}
                  setValue={setValue}
                  bindingName="bindings.mountingResetBinding"
                  delayName="delays.mountingResetDelay"
                />
                <KeybindRow
                  label="Pause Tracking"
                  control={control}
                  setValue={setValue}
                  bindingName="bindings.pauseTrackingBinding"
                  delayName="delays.pauseTrackingDelay"
                />
            </Table>
            <div className="flex flex-col pt-4" />
            <Button
              className="flex flex-col"
              onClick={handleResetButton}
              variant="primary"
            >
              Reset all
            </Button>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
