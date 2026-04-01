import { BaseModal } from './BaseModal';
import { Controller, Control, UseFormResetField } from 'react-hook-form';
import { KeybindRecorder } from './KeybindRecorder';
import { Typography } from './Typography';
import { Button } from './Button';
import './KeybindRow.scss';
import { useLocalization } from '@fluent/react';

export function KeybindRecorderModal({
  id,
  control,
  name,
  resetField,
  isVisisble,
  onClose,
  onUnbind,
}: {
  id?: string;
  control: Control<any>;
  name: string;
  resetField: UseFormResetField<any>;
  isVisisble: boolean;
  onClose: () => void;
  onUnbind: () => void;
}) {
  const { l10n } = useLocalization();
  const keybindlocalization = 'settings-keybinds_' + id;

  return (
    <BaseModal
      isOpen={isVisisble}
      onRequestClose={() => onClose()}
      appendClasses="w-full max-w-xl"
    >
      <div className="flex flex-col gap-3 w-full justify-between h-full">
        <Typography variant="section-title">
          {l10n.getString('settings-keybinds-recorder-modal-title')}{' '}
          {l10n.getString(keybindlocalization)}
        </Typography>
        <Controller
          control={control}
          name={name}
          render={({ field }) => (
            <KeybindRecorder
              keys={field.value ?? []}
              onKeysChange={field.onChange}
              ref={field.ref}
            />
          )}
        />
        <div className="flex flex-row justify-between w-full">
          <div className="flex flex-row justify-start gap-4">
            <Button
              id="settings-keybinds-recorder-modal-reset-button"
              variant="tertiary"
              onClick={() => {
                resetField(name);
                onClose()
              }}
            />
            <Button
              id="settings-keybinds-recorder-modal-unbind-button"
              variant="tertiary"
              onClick={() => {
                onUnbind()
                onClose()
              }}
            />
          </div>
          <div className="flex flex-row justify-end">
            <Button
              id="settings-keybinds-recorder-modal-done-button"
              variant="primary"
              onClick={onClose}
            />
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
