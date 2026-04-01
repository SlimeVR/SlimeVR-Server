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
import { KeybindsRow } from '@/components/commons/KeybindsRow';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { ReactNode, useEffect, useRef, useState } from 'react';
import {
  ChangeKeybindRequestT,
  KeybindRequestT,
  KeybindResponseT,
  KeybindT,
  OpenUriRequestT,
  RpcMessage,
} from 'solarxr-protocol';
import { useFieldArray, useForm } from 'react-hook-form';
import { useAppContext } from '@/hooks/app';
import { useElectron } from '@/hooks/electron';

export type KeybindForm = {
  keybinds: {
    id: number;
    name: string;
    binding: string[];
    delay: number;
  }[];
};

export function KeybindSettings() {
  const electron = useElectron();
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [defaultKeybindsState, setDefaultKeybindsState] = useState<KeybindForm>(
    {
      keybinds: [],
    }
  );
  const currentIndex = useRef<number | null>(null);
  const { installInfo } = useAppContext();

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

  const handleOpenSystemSettingsButton = () => {
    sendRPCPacket(RpcMessage.OpenUriRequest, new OpenUriRequestT());
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
    currentIndex.current = index;
    if (currentIndex !== null) {
      setIsOpen(true);
    }
  };

  const createKeybindRows = (): ReactNode => {
    return fields.map((field, index) => {
      return (
        <div className="keybind-row" key={index}>
          <KeybindsRow
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
        <div className="flex flex-col gap-2">
          <Typography variant="main-title" id="settings-keybinds" />
          <div className="flex flex-col pt-2 pb-4">
            {l10n
              .getString('settings-keybinds-description')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </div>
          {installInfo?.isWayland ? (
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
            electron.isElectron &&
            electron.data().os.type === 'windows' && (
              <>
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
                </div>
                <div className="flex justify-end">
                  <Button
                    id="settings-keybinds_reset-all-button"
                    onClick={() => {
                      reset(defaultKeybindsState);
                      handleSubmit(onSubmit)();
                    }}
                    variant="primary"
                  />
                </div>

                <KeybindRecorderModal
                  id={
                    currentIndex.current != null
                      ? fields[currentIndex.current].name
                      : ''
                  }
                  control={control}
                  resetField={resetField}
                  name={
                    currentIndex.current != null
                      ? `keybinds.${currentIndex.current}.binding`
                      : ''
                  }
                  isVisisble={isOpen}
                  onClose={() => {
                    setIsOpen(false);
                    handleSubmit(onSubmit)();
                  }}
                  onUnbind={() => {
                    if (currentIndex.current != null)
                      setValue(`keybinds.${currentIndex.current}.binding`, []);
                  }}
                />
              </>
            )
          )}
        </div>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
