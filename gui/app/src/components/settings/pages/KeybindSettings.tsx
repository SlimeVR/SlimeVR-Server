import { KeybindRecorderModal } from '@/components/commons/KeybindRecorderModal';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
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
  KeybindSupport,
  KeybindT,
  OpenKeybindSettingsRequestT,
  RpcMessage,
} from 'solarxr-protocol';
import { FormProvider, useFieldArray, useForm } from 'react-hook-form';

export type KeybindForm = {
  keybinds: {
    id: number;
    name: string;
    binding: string[];
    delay: number;
  }[];
};

export function KeybindSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [defaultKeybindsState, setDefaultKeybindsState] = useState<KeybindForm>(
    {
      keybinds: [],
    }
  );
  const currentIndex = useRef<number | null>(null);
  const [support, setSupport] = useState<KeybindSupport>(
    KeybindSupport.UNSUPPORTED
  );

  const methods = useForm<KeybindForm>({
    defaultValues: defaultKeybindsState,
  });

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    getValues,
    setError,
    clearErrors,
    resetField,
    watch,
  } = methods;

  const { fields } = useFieldArray({
    control,
    name: 'keybinds',
  });

  const onSubmit = () => {
    const value = getValues();
    if (checkDuplicates(value)) {
      return;
    }
    clearErrors('keybinds');

    value.keybinds.forEach((kb) => {
      const changeKeybindRequest = new ChangeKeybindRequestT();

      const keybind = new KeybindT();
      keybind.keybindId = kb.id;
      keybind.keybindValue = kb.binding.join('+');
      keybind.keybindDelay = kb.delay;

      changeKeybindRequest.keybind = keybind;

      sendRPCPacket(RpcMessage.ChangeKeybindRequest, changeKeybindRequest);
      setIsOpen(false);
    });
  };

  const checkDuplicates = (value: KeybindForm) => {
    const normalized = value.keybinds
      .filter((kb) => kb.binding.length > 0)
      .map((kb) => JSON.stringify([...kb.binding].sort()));

    const unique = new Set(normalized);

    if (unique.size !== normalized.length) {
      setError('keybinds', {
        type: 'manual',
        message: 'Duplicate keybind combinations are not allowed',
      });
      return true;
    }

    return false;
  };

  const handleOpenSystemSettingsButton = () => {
    sendRPCPacket(
      RpcMessage.OpenKeybindSettingsRequest,
      new OpenKeybindSettingsRequestT()
    );
  };

  useRPCPacket(
    RpcMessage.KeybindResponse,
    ({ keybind, defaultKeybinds, support }: KeybindResponseT) => {
      setSupport(support);
      if (!keybind) return;

      const mappedDefaults = defaultKeybinds.map((kb) => ({
        id: kb.keybindId,
        name: kb.keybindNameId?.toString() ?? '',
        binding: kb.keybindValue?.toString().split('+') ?? [],
        delay: kb.keybindDelay,
      }));

      setDefaultKeybindsState({ keybinds: mappedDefaults });
      reset({ keybinds: mappedDefaults });

      const mapped = keybind.map((kb) => ({
        id: kb.keybindId,
        name: kb.keybindNameId?.toString() ?? '',
        binding: kb.keybindValue?.toString().split('+') ?? [],
        delay: kb.keybindDelay,
      }));

      mapped.forEach((keybind, index) => {
        setValue(`keybinds.${index}.binding`, keybind.binding);
        setValue(`keybinds.${index}.delay`, keybind.delay);
      });
    }
  );

  const handleOpenRecorderModal = (index: number) => {
    currentIndex.current = index;
    setIsOpen(true);
  };

  const onClose = () => {
    if (currentIndex.current != null) {
      resetField(`keybinds.${currentIndex.current}.binding`);
    }
    setIsOpen(false);
  };

  // Captured so the modal's callbacks keep the index they were opened with
  const editedIndex = currentIndex.current;

  const createKeybindRows = (): ReactNode => {
    return fields.map((field, index) => {
      return (
        <div className="keybind-row" key={index}>
          <KeybindsRow
            id={field.name}
            control={control}
            index={index}
            openKeybindRecorderModal={handleOpenRecorderModal}
          />
        </div>
      );
    });
  };

  useEffect(() => {
    sendRPCPacket(RpcMessage.KeybindRequest, new KeybindRequestT());
  }, []);

  // Only the recorder modal submits, so a delay change would otherwise never reach the server
  useEffect(() => {
    const subscription = watch((_, { name, type }) => {
      if (type === 'change' && name?.endsWith('.delay')) onSubmit();
    });
    return () => subscription.unsubscribe();
  }, [watch]);

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
          {support === KeybindSupport.UNSUPPORTED && (
            <Typography id="settings-keybinds-unsupported-description" />
          )}
          {support === KeybindSupport.SYSTEM_MANAGED && (
            <div className="flex flex-col gap-4">
              <Typography id="settings-keybinds-system-managed-description" />
              <div>
                <Button
                  id="settings-keybinds-open-system-settings-button"
                  className="flex flex-col"
                  onClick={handleOpenSystemSettingsButton}
                  variant="primary"
                />
              </div>
            </div>
          )}
          {support === KeybindSupport.APP_MANAGED && (
            <>
              <FormProvider {...methods}>
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
                {editedIndex != null && (
                  <KeybindRecorderModal
                    id={fields[editedIndex].name}
                    control={control}
                    name={`keybinds.${editedIndex}.binding`}
                    isVisisble={isOpen}
                    onClose={onClose}
                    onUnbind={() =>
                      setValue(`keybinds.${editedIndex}.binding`, [])
                    }
                    onSubmit={onSubmit}
                  />
                )}
              </FormProvider>
            </>
          )}
        </div>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
