import { KeybindRecorderModal } from '@/components/commons/KeybindRecorderModal';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { Button } from '@/components/commons/Button';
import { KeybindsRow } from '@/components/commons/KeybindsRow';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { ReactNode, useEffect, useMemo, useRef, useState } from 'react';
import {
  ChangeKeybindRequestT,
  KeybindRequestT,
  KeybindResponseT,
  KeybindSupport,
  KeybindT,
  OpenKeybindSettingsRequestT,
  RpcMessage,
  SetKeybindRecordingRequestT,
} from 'solarxr-protocol';
import { FormProvider, useFieldArray, useForm } from 'react-hook-form';

export type KeybindForm = {
  keybinds: {
    keybindId: number;
    name: string;
    binding: string[];
    delay: number;
  }[];
};

function parseBinding(val: string | Uint8Array | null | undefined): string[] {
  if (!val) return [];
  const str = val.toString().trim();
  if (!str) return [];
  return str
    .split('+')
    .map((s) => s.trim())
    .filter(Boolean);
}

function areBindingsEqual(a?: string[], b?: string[]): boolean {
  if (a === b) return true;
  if (!a || !b) return false;
  if (a.length !== b.length) return false;

  const setB = new Set(b);
  return a.every((key) => setB.has(key));
}

export function KeybindSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [defaultKeybindsState, setDefaultKeybindsState] = useState<KeybindForm>(
    { keybinds: [] }
  );
  const currentIndex = useRef<number | null>(null);
  const [support, setSupport] = useState<KeybindSupport>(
    KeybindSupport.UNSUPPORTED
  );

  const methods = useForm<KeybindForm>({
    defaultValues: defaultKeybindsState,
    mode: 'onChange',
  });

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    getValues,
    clearErrors,
    watch,
  } = methods;

  const defaultKeybindsMap = useMemo(() => {
    return new Map(
      defaultKeybindsState.keybinds.map((kb) => [kb.keybindId, kb])
    );
  }, [defaultKeybindsState]);

  // Snapshot of the edited binding, restored if the modal is cancelled
  const bindingSnapshot = useRef<string[] | null>(null);

  const { fields } = useFieldArray({
    control,
    name: 'keybinds',
  });

  const onSubmit = (data?: KeybindForm) => {
    const value = data ?? getValues();

    value.keybinds.forEach((kb) => {
      const currentBindingStr = kb.binding.join('+');
      const changeKeybindRequest = new ChangeKeybindRequestT();
      const keybind = new KeybindT();
      keybind.keybindId = kb.keybindId;
      keybind.keybindValue = currentBindingStr;
      keybind.keybindDelay = kb.delay;
      changeKeybindRequest.keybind = keybind;
      sendRPCPacket(RpcMessage.ChangeKeybindRequest, changeKeybindRequest);
    });

    setIsOpen(false);
  };

  const handleOpenSystemSettingsButton = () => {
    sendRPCPacket(
      RpcMessage.OpenKeybindSettingsRequest,
      new OpenKeybindSettingsRequestT()
    );
  };

  const handleResetSingle = (index: number) => {
    const targetKeybind = getValues(`keybinds.${index}`);
    if (!targetKeybind) return;

    const defaultKb = defaultKeybindsMap.get(targetKeybind.keybindId);

    if (defaultKb) {
      setValue(`keybinds.${index}.binding`, defaultKb.binding);
      setValue(`keybinds.${index}.delay`, defaultKb.delay);
      handleSubmit(onSubmit)();
    }
  };

  useRPCPacket(
    RpcMessage.KeybindResponse,
    ({ keybind, defaultKeybinds, support }: KeybindResponseT) => {
      setSupport(support);
      if (!keybind) return;

      const mappedDefaults = defaultKeybinds.map((kb) => ({
        keybindId: kb.keybindId,
        name: kb.keybindNameId?.toString() ?? '',
        binding: parseBinding(kb.keybindValue),
        delay: kb.keybindDelay,
      }));

      setDefaultKeybindsState({ keybinds: mappedDefaults });

      const mappedSaved = keybind.map((kb) => ({
        keybindId: kb.keybindId,
        name: kb.keybindNameId?.toString() ?? '',
        binding: parseBinding(kb.keybindValue),
        delay: kb.keybindDelay,
      }));

      reset({ keybinds: mappedSaved });
    }
  );

  useEffect(() => {
    const req = new SetKeybindRecordingRequestT();
    req.recording = isOpen;
    sendRPCPacket(RpcMessage.SetKeybindRecordingRequest, req);
  }, [isOpen]);

  const handleOpenRecorderModal = (index: number) => {
    currentIndex.current = index;
    bindingSnapshot.current = getValues(`keybinds.${index}.binding`);
    setIsOpen(true);
  };

  const onClose = () => {
    if (currentIndex.current != null && bindingSnapshot.current != null) {
      setValue(
        `keybinds.${currentIndex.current}.binding`,
        bindingSnapshot.current
      );
    }
    setIsOpen(false);
    clearErrors('keybinds');
  };

  const editedIndex = currentIndex.current;

  const takenBindings =
    editedIndex != null
      ? getValues('keybinds')
          .filter((_, i) => i !== editedIndex)
          .filter((kb) => kb.binding.length > 0)
          .map((kb) => ({
            name: l10n.getString('settings-keybinds_' + kb.name),
            binding: kb.binding,
          }))
      : [];

  const createKeybindRows = (): ReactNode => {
    return fields.map((field, index) => {
      const currentKb = watch(`keybinds.${index}`);
      const defaultKb = currentKb
        ? defaultKeybindsMap.get(currentKb.keybindId)
        : undefined;

      const isModified =
        defaultKb != null &&
        currentKb != null &&
        (currentKb.delay !== defaultKb.delay ||
          !areBindingsEqual(currentKb.binding, defaultKb.binding));

      return (
        <KeybindsRow
          key={field.id}
          id={field.name}
          control={control}
          index={index}
          openKeybindRecorderModal={handleOpenRecorderModal}
          onResetSingle={handleResetSingle}
          isModified={isModified}
        />
      );
    });
  };

  useEffect(() => {
    sendRPCPacket(RpcMessage.KeybindRequest, new KeybindRequestT());
  }, []);

  useEffect(() => {
    const subscription = watch((_, { name, type }) => {
      if (type === 'change' && name?.endsWith('.delay'))
        handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  return (
    <SettingsPageLayout>
      <SettingsPagePaneLayout icon={<WrenchIcon />} id="keybinds">
        <div className="flex flex-col gap-4">
          <div>
            <Typography variant="main-title" id="settings-keybinds" />
            <div className="flex flex-col pt-2 text-background-30">
              {l10n
                .getString('settings-keybinds-description')
                .split('\n')
                .map((line, i) => (
                  <Typography key={i}>{line}</Typography>
                ))}
            </div>
          </div>

          {support === KeybindSupport.UNSUPPORTED && (
            <div className="flex items-center gap-3 p-4 rounded-2xl bg-status-critical/10 border border-status-critical/30 text-status-critical">
              <WarningIcon className="w-5 h-5 flex-shrink-0" />
              <Typography id="settings-keybinds-unsupported-description" />
            </div>
          )}

          {support === KeybindSupport.SYSTEM_MANAGED && (
            <div className="flex flex-col gap-4 p-4 rounded-2xl bg-background-80">
              <Typography id="settings-keybinds-system-managed-description" />
              <div>
                <Button
                  id="settings-keybinds-open-system-settings-button"
                  onClick={handleOpenSystemSettingsButton}
                  variant="primary"
                />
              </div>
            </div>
          )}

          {support === KeybindSupport.APP_MANAGED && (
            <FormProvider {...methods}>
              <div className="rounded-2xl bg-background-80 shadow-sm my-1 overflow-hidden">
                <table className="w-full text-left border-collapse block md:table">
                  <thead className="hidden md:table-header-group">
                    <tr className="bg-background-80 border-b border-background-60 uppercase tracking-wider">
                      <th className="py-4 pl-6 pr-4 w-0 whitespace-nowrap">
                        <Typography
                          id="keybind_config-keybind_name"
                          variant="section-title"
                        />
                      </th>
                      <th className="py-4 px-4 w-full text-center">
                        <Typography
                          id="keybind_config-keybind_value"
                          variant="section-title"
                        />
                      </th>
                      <th className="py-4 px-6 text-right w-0 whitespace-nowrap">
                        <Typography
                          id="keybind_config-keybind_delay"
                          variant="section-title"
                          whitespace="whitespace-nowrap"
                        />
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-background-60 block md:table-row-group">
                    {createKeybindRows()}
                  </tbody>
                </table>
              </div>

              <div className="flex justify-end pt-2">
                <Button
                  id="settings-keybinds_reset-all-button"
                  onClick={() => {
                    reset(defaultKeybindsState);
                    handleSubmit(onSubmit)();
                  }}
                  variant="secondary"
                />
              </div>

              {editedIndex != null && (
                <KeybindRecorderModal
                  id={fields[editedIndex].name}
                  control={control}
                  name={`keybinds.${editedIndex}.binding`}
                  isVisisble={isOpen}
                  onClose={onClose}
                  onUnbind={() => {
                    setValue(`keybinds.${editedIndex}.binding`, []);
                    handleSubmit(onSubmit)();
                  }}
                  onSubmit={() => handleSubmit(onSubmit)()}
                  onReset={() => handleResetSingle(editedIndex)}
                  takenBindings={takenBindings}
                />
              )}
            </FormProvider>
          )}
        </div>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
