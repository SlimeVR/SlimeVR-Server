import { KeybindRecorderModal } from '@/components/commons/KeybindRecorderModal';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import './KeybindSettings.scss';
import { Button } from '@/components/commons/Button';
import { NewKeybindsRow } from '@/components/commons/newKeybindsRow';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { ReactNode, useEffect, useState } from 'react';
import {
  ChangeKeybindRequestT,
  KeybindRequestT,
  KeybindResponseT,
  KeybindT,
  RpcMessage,
} from 'solarxr-protocol';
import { useForm } from 'react-hook-form';

export type KeybindForm = {
  id: number;
  name: string;
  binding: string[];
  delay: number;
};

export function NewKeybindSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [keybinds, setKeybinds] = useState<KeybindT[]>();
  const [selectedKeybind, setSelectedKeybind] = useState<KeybindT | null>(null);

  const { control, resetField, reset, handleSubmit, watch } =
    useForm<KeybindForm>({});

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (value: KeybindForm) => {
    const changeKeybindRequest = new ChangeKeybindRequestT();

    const keybind = new KeybindT();
    keybind.keybindId = value.id;
    keybind.keybindNameId = value.name;
    keybind.keybindValue = value.binding.join('+');
    keybind.keybindDelay = value.delay;

    changeKeybindRequest.keybind = keybind;

    console.log(`Onsubmit: ${keybind.keybindValue}`);

    sendRPCPacket(RpcMessage.ChangeKeybindRequest, changeKeybindRequest);
  };

  useRPCPacket(RpcMessage.KeybindResponse, ({ keybind }: KeybindResponseT) => {
    if (!keybind) return;
    setKeybinds(keybind);
  });

  const handleOnClick = () => {
    console.log('pressed');
  };

  const handleOpenRecorderModal = (index: number) => {
    if (keybinds == null) return;
    console.log('Handle open recorder modal');
    const kb = keybinds[index];

    setSelectedKeybind(kb);
    setIsOpen(true);

    reset({
      id: kb.keybindId,
      name: typeof kb.keybindNameId === 'string' ? kb.keybindNameId : '',
      binding:
        typeof kb.keybindValue === 'string' ? kb.keybindValue.split('+') : [],
      delay: kb.keybindDelay,
    });
  };

  const createKeybindRows = (): ReactNode => {
    if (keybinds == null) return '';
    return keybinds.map((key, i) => {
      return (
        <div className="keybind-row" onClick={() => handleOpenRecorderModal(i)}>
          <NewKeybindsRow
            key={i}
            id={typeof key.keybindNameId === 'string' ? key.keybindNameId : ''}
            keybind={
              (typeof key.keybindValue === 'string'
                ? key.keybindValue
                : ''
              ).split('+') || ''
            }
            delay={key.keybindDelay}
          />
        </div>
      );
    });
  };

  useEffect(() => {
    sendRPCPacket(RpcMessage.KeybindRequest, new KeybindRequestT());
  }, [isOpen]);

  return (
    <SettingsPageLayout>
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
        <div className="keybind-settings">
          <Typography id="keybind_config-keybind_name" />
          <Typography id="keybind_config-keybind_value" />
          <Typography id="keybind_config-keybind_delay" />
          {createKeybindRows()}
          <Button
            id="settings-keybinds_reset-all-button"
            className="justify-self-start"
            onClick={handleOnClick}
            variant="primary"
          />
        </div>
        {selectedKeybind && (
          <KeybindRecorderModal
            id={
              typeof selectedKeybind.keybindNameId === 'string'
                ? selectedKeybind.keybindNameId
                : ''
            }
            control={control}
            resetField={resetField}
            name="binding"
            delay={selectedKeybind.keybindDelay.toString()}
            isVisisble={isOpen}
            onClose={() => {
              console.log('onclose');
              setIsOpen(false);
              setSelectedKeybind(null);
            }}
          />
        )}
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
