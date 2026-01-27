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
import def from 'ajv/dist/vocabularies/discriminator';

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

export function useKeybindsForm() {
  const {
    register,
    reset,
    handleSubmit,
    formState,
    control,
    getValues,
    resetField,
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
    resetField,
    watch,
  };
}

export function KeybindSettings() {
  const { l10n } = useLocalization();
  const { control, reset, handleSubmit, watch, getValues, resetField } =
    useKeybindsForm();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const onSubmit = (values: KeybindsForm) => {
    const keybinds = new ChangeKeybindRequestT();

    const fullResetKeybind = new KeybindT();
    fullResetKeybind.keybindName = values.names.fullResetName;
    fullResetKeybind.keybindValue = values.bindings.fullResetBinding.join('+');
    fullResetKeybind.keybindDelay = values.delays.fullResetDelay
    keybinds.keybind.push(fullResetKeybind);

    const yawResetKeybind = new KeybindT();
    yawResetKeybind.keybindName = values.names.yawResetName;
    yawResetKeybind.keybindValue = values.bindings.yawResetBinding.join('+');
    yawResetKeybind.keybindDelay = values.delays.yawResetDelay
    keybinds.keybind.push(yawResetKeybind);

    const mountingResetKeybind = new KeybindT();
    mountingResetKeybind.keybindName = values.names.mountingResetName;
    mountingResetKeybind.keybindValue = values.bindings.mountingResetBinding.join('+');
    mountingResetKeybind.keybindDelay = values.delays.mountingResetDelay;
    keybinds.keybind.push(mountingResetKeybind);

    const pauseTrackingKeybind = new KeybindT();
    pauseTrackingKeybind.keybindName = values.names.pauseTrackingName;
    pauseTrackingKeybind.keybindValue = values.bindings.pauseTrackingBinding.join('+');
    pauseTrackingKeybind.keybindDelay = values.delays.pauseTrackingDelay
    keybinds.keybind.push(pauseTrackingKeybind);

    const feetResetKeybind = new KeybindT();
    feetResetKeybind.keybindName = values.names.feetResetName;
    feetResetKeybind.keybindValue = values.bindings.feetResetBinding.join('+');
    feetResetKeybind.keybindDelay = values.delays.pauseTrackingDelay;
    keybinds.keybind.push(feetResetKeybind);

    console.log(`Delay ${Number(fullResetKeybind.keybindDelay)}`)
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

    const keybindValues: KeybindsForm = {
      names: {
        fullResetName: KeybindName.FULL_RESET,
        yawResetName: KeybindName.YAW_RESET,
        mountingResetName: KeybindName.MOUNTING_RESET,
        pauseTrackingName: KeybindName.PAUSE_TRACKING,
        feetResetName: KeybindName.FEET_MOUNTING_RESET,
      },
      bindings: {
        fullResetBinding: (typeof keybind[KeybindName.FULL_RESET]
          .keybindValue === 'string'
          ? keybind[KeybindName.FULL_RESET].keybindValue
          : ''
          ).split('+') ||
          defaultValues.bindings.fullResetBinding,

        yawResetBinding: (typeof keybind[KeybindName.YAW_RESET].keybindValue ===
        'string'
          ? keybind[KeybindName.YAW_RESET].keybindValue
          : ''
          ).split('+') ||
          defaultValues.bindings.yawResetBinding,

        mountingResetBinding: (typeof keybind[KeybindName.MOUNTING_RESET]
          .keybindValue === 'string'
          ? keybind[KeybindName.MOUNTING_RESET].keybindValue
          : ''
          ).split('+') ||
          defaultValues.bindings.mountingResetBinding,

        pauseTrackingBinding: (typeof keybind[KeybindName.PAUSE_TRACKING]
          .keybindValue === 'string'
          ? keybind[KeybindName.PAUSE_TRACKING].keybindValue
          : ''
          ).split('+') ||
          defaultValues.bindings.pauseTrackingBinding,

        feetResetBinding: (typeof keybind[KeybindName.FEET_MOUNTING_RESET]
          .keybindValue === 'string'
          ? keybind[KeybindName.FEET_MOUNTING_RESET].keybindValue
          : ''
          ).split('+') ||
          defaultValues.bindings.feetResetBinding,
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
          defaultValues.delays.mountingResetDelay,
        feetResetDelay: 
          keybind[KeybindName.FEET_MOUNTING_RESET].keybindDelay ||
          defaultValues.delays.feetResetDelay,
      },
    };

    reset({ ...getValues(), ...keybindValues });
  });

  const handleResetButton = () => {
    reset(defaultValues);
  };

  function Table({ children }: { children: ReactNode }) {
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
            <th scope="col" className="px-6 py-3 text-middle">
              <Localized id={'keybind_config-keybind_value'}>
                <Typography />
              </Localized>
              Combination
            </th>
            <th scope="col" className="px-6 py-3 text-middle">
              <Localized id={'keybind_config-keybind_delay'}>
                <Typography />
              </Localized>
              Delay before trigger (S)
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
              {l10n.getString('settings-keybinds')}
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
                resetField={resetField}
                bindingName="bindings.fullResetBinding"
                delayName="delays.fullResetDelay"
              />
              <KeybindRow
                label="Yaw Reset"
                control={control}
                resetField={resetField}
                bindingName="bindings.yawResetBinding"
                delayName="delays.yawResetDelay"
              />
              <KeybindRow
                label="Mounting Reset"
                control={control}
                resetField={resetField}
                bindingName="bindings.mountingResetBinding"
                delayName="delays.mountingResetDelay"
              />
              <KeybindRow
                label="Feet Mounting Reset"
                control={control}
                resetField={resetField}
                bindingName="bindings.feetResetBinding"
                delayName="delays.feetResetDelay"
              />
              <KeybindRow
                label="Pause Tracking"
                control={control}
                resetField={resetField}
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
              Reset All
            </Button>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
