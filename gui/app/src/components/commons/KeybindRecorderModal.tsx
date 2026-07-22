import { BaseModal } from './BaseModal';
import {
  Controller,
  Control,
  useFormContext,
  FieldValues,
  FieldPath,
} from 'react-hook-form';
import { KeybindRecorder, isValidKeybind, keybindKey } from './KeybindRecorder';
import { Kbd } from './Kbd';
import { Typography } from './Typography';
import { KeyboardIcon } from './icon/KeyboardIcon';
import { useLocalization } from '@fluent/react';

function KeybindHint({ keyId, labelId }: { keyId: string; labelId: string }) {
  return (
    <div className="flex items-center gap-2">
      <Kbd id={keyId} className="px-2 py-2 leading-none" />
      <Typography variant="standard" id={labelId} />
    </div>
  );
}

export function KeybindRecorderModal<T extends FieldValues = FieldValues>({
  id,
  control,
  name,
  isVisisble,
  onClose,
  onUnbind,
  onSubmit,
  takenBindings,
}: {
  id?: string;
  control: Control<T>;
  name: FieldPath<T>;
  isVisisble: boolean;
  onClose: () => void;
  onUnbind: () => void;
  onSubmit: () => void;
  onReset?: () => void;
  takenBindings?: { name: string; binding: string[] }[];
}) {
  const keybindlocalization = 'settings-keybinds_' + id;
  const { l10n } = useLocalization();
  const { handleSubmit } = useFormContext();

  const handleModalSubmit = () => {
    handleSubmit(onSubmit)();
  };

  const validateBinding = (value: string[]) => {
    if (!value || value.length === 0) return true;
    if (!isValidKeybind(value))
      return l10n.getString('settings-keybinds-error-add-modifier');
    const clash = (takenBindings ?? []).find(
      (t) => keybindKey(t.binding) === keybindKey(value)
    );
    return clash
      ? l10n.getString('settings-keybinds-already-assigned', {
          name: clash.name,
        })
      : true;
  };

  return (
    <BaseModal
      isOpen={isVisisble}
      onRequestClose={onClose}
      appendClasses="w-full max-w-md"
    >
      <div className="flex flex-col gap-6 w-full">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-accent-background-40 flex justify-center items-center rounded-full fill-background-10 flex-shrink-0">
            <KeyboardIcon size={20} className="fill-background-10" />
          </div>
          <div className="flex flex-col">
            <Typography
              variant="standard"
              id="settings-keybinds-recorder-modal-title"
            />
            <Typography variant="section-title" id={keybindlocalization} />
          </div>
        </div>

        <Controller
          control={control}
          name={name}
          rules={{ validate: validateBinding }}
          render={({ field, fieldState }) => (
            <KeybindRecorder
              keys={field.value ?? []}
              onKeysChange={field.onChange}
              ref={field.ref}
              error={fieldState.error?.message}
              onSubmitModal={handleModalSubmit}
              onUnbindModal={onUnbind}
              onCloseModal={onClose}
            />
          )}
        />

        <div className="flex flex-wrap items-center justify-center gap-x-6 gap-y-2 pt-4 border-t border-background-60">
          <KeybindHint
            keyId="settings-keybinds-recorder-modal-key-enter"
            labelId="settings-keybinds-recorder-modal-done-button"
          />
          <KeybindHint
            keyId="settings-keybinds-recorder-modal-key-backspace"
            labelId="settings-keybinds-recorder-modal-unbind-button"
          />
          <KeybindHint
            keyId="settings-keybinds-recorder-modal-key-escape"
            labelId="settings-keybinds-recorder-modal-cancel-button"
          />
        </div>
      </div>
    </BaseModal>
  );
}
