import { BaseModal } from './BaseModal';
import { Controller, Control, useFormContext } from 'react-hook-form';
import { KeybindRecorder } from './KeybindRecorder';
import { Typography } from './Typography';
import { Button } from './Button';
import './KeybindRow.scss';
import { useLocalization } from '@fluent/react';

export function KeybindRecorderModal({
  id,
  control,
  name,
  isVisisble,
  onClose,
  onUnbind,
  onSubmit,
}: {
  id?: string;
  control: Control<any>;
  name: string;
  isVisisble: boolean;
  onClose: () => void;
  onUnbind: () => void;
  onSubmit: () => void;
}) {
  const { l10n } = useLocalization();
  const keybindlocalization = 'settings-keybinds_' + id;
  const {
    formState: { errors },
    resetField,
    handleSubmit,
  } = useFormContext();

  return (
    <BaseModal
      isOpen={isVisisble}
      onRequestClose={onClose}
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
              error={errors.keybinds?.message as string}
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
                handleSubmit(onSubmit)();
              }}
            />
            <Button
              id="settings-keybinds-recorder-modal-unbind-button"
              variant="tertiary"
              onClick={() => {
                onUnbind();
                handleSubmit(onSubmit)();
              }}
            />
          </div>
          <div className="flex flex-row justify-end">
            <Button
              id="settings-keybinds-recorder-modal-done-button"
              variant="primary"
              onClick={handleSubmit(onSubmit)}
            />
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
