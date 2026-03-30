import { BaseModal } from './BaseModal';
import {
  Controller,
  Control,
  UseFormResetField,
} from 'react-hook-form';
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

  return (
    <BaseModal
      isOpen={isVisisble}
      appendClasses="w-full max-w-xl h-full max-h-60"
    >
      <div className="flex-col gap-4 w-full">
        <div className="flex flex-col w-full">
          <div className="flex flex-col gap-3 w-full">
            <Typography variant="section-title">
              Assign keybind for {l10n.getString(`settings-keybinds_${id}`)}
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
                  id="settings-keybinds_reset-button"
                  variant="primary"
                  onClick={() => {
                    resetField(name)
                  }}
                />
                <Button variant="primary" onClick={onUnbind}>
                  unbind
                </Button>
              </div>
              <div className="flex flex-row justify-end">
                <Button id="" variant="primary" onClick={onClose}>
                  Done
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </BaseModal>
  );
}
