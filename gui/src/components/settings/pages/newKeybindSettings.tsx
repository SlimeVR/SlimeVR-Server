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
import { useFieldArray, useForm } from 'react-hook-form';

export type KeybindForm = {
  keybinds: {
    id: number;
    name: string;
    binding: string[];
    delay: number;
  }[];
};

export function NewKeybindSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null);
  const [defaultKeybindsState, setDefaultKeybindsState] = useState<KeybindForm>(
    {
      keybinds: [],
    }
  );

  const { control, resetField, handleSubmit, reset, setValue, getValues } =
    useForm<KeybindForm>({
      defaultValues: defaultKeybindsState,
    });

  const { fields } = useFieldArray({
    control,
    name: 'keybinds',
  });


  const onSubmit = (value: KeybindForm) => {
    value.keybinds.forEach((kb) => {
      const changeKeybindRequest = new ChangeKeybindRequestT();

      const keybind = new KeybindT();
      keybind.keybindId = kb.id;
      keybind.keybindNameId = kb.name;
      keybind.keybindValue = kb.binding.join('+');
      keybind.keybindDelay = kb.delay;

      changeKeybindRequest.keybind = keybind;

      sendRPCPacket(RpcMessage.ChangeKeybindRequest, changeKeybindRequest);
    });
  };

  useRPCPacket(
    RpcMessage.KeybindResponse,
    ({ keybind, defaultKeybinds }: KeybindResponseT) => {
      if (!keybind) return;

      const mappedDefaults = defaultKeybinds.map((kb) => ({
        id: kb.keybindId,
        name: typeof kb.keybindNameId === 'string' ? kb.keybindNameId : '',
        binding:
          typeof kb.keybindValue === 'string' ? kb.keybindValue.split('+') : [],
        delay: kb.keybindDelay,
      }));

      setDefaultKeybindsState({ keybinds: mappedDefaults });

      const mapped = keybind.map((kb) => ({
        id: kb.keybindId,
        name: typeof kb.keybindNameId === 'string' ? kb.keybindNameId : '',
        binding:
          typeof kb.keybindValue === 'string' ? kb.keybindValue.split('+') : [],
        delay: kb.keybindDelay,
      }));
      reset({ keybinds: mappedDefaults });

      mapped.forEach((keybind, index) => {
        setValue(`keybinds.${index}.binding`, keybind.binding);
        setValue(`keybinds.${index}.delay`, keybind.delay);
      });
    }
  );

  const handleOpenRecorderModal = (index: number) => {
    console.log('Handle open recorder modal', index);
    setSelectedIndex(index);
    setIsOpen(true);
  };

  const createKeybindRows = (): ReactNode => {
    return fields.map((field, index) => {
      return (
        <div className="keybind-row">
          <NewKeybindsRow
            id={typeof field.name === 'string' ? field.name : ''}
            control={control}
            index={index}
            getValue={getValues}
            openKeybindRecorderModal={handleOpenRecorderModal}
          />
        </div>
      );
    });
  };

  useEffect(() => {
    sendRPCPacket(RpcMessage.KeybindRequest, new KeybindRequestT());
  }, []);

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
          <Typography
            id="keybind_config-keybind_name"
            variant="section-title"
          />
          <Typography
            id="keybind_config-keybind_value"
            variant="section-title"
          />
          <Typography
            id="keybind_config-keybind_delay"
            variant="section-title"
          />
          {createKeybindRows()}
          <Button
            id="settings-keybinds_reset-all-button"
            className="justify-self-start"
            onClick={() => reset(defaultKeybindsState)}
            variant="primary"
          />
        </div>
        {selectedIndex != null && (
          <KeybindRecorderModal
            id={fields[selectedIndex].name}
            control={control}
            resetField={resetField}
            name={`keybinds.${selectedIndex}.binding`}
            isVisisble={isOpen}
            onClose={() => {
              setIsOpen(false);
              setSelectedIndex(null);
              handleSubmit(onSubmit)()
            }}
            onUnbind={() => {
              setValue(`keybinds.${selectedIndex}.binding`, []);
            }}
          />
        )}
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
