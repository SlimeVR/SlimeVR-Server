import { Controller, Control, UseFormResetField } from 'react-hook-form';
import { Button } from './Button';
import { NumberSelector } from './NumberSelector';
import { KeybindRecorder } from './KeybindRecorder';
import { KeybindRecorderModal } from './KeybindRecorderModal';
import { useLocaleConfig } from '@/i18n/config';
import { Typography } from './Typography';
import './KeybindRow.scss';

export function KeybindRow({
  id,
  label,
  control,
  resetField,
  name,
  delay,
}: {
  id?: string;
  label?: string;
  control: Control<any>;
  resetField: UseFormResetField<any>;
  name: string;
  delay: string;
}) {
  const { currentLocales } = useLocaleConfig();
  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  return (
    <div className="keybind-row">
      <label className="text-sm font-medium text-background-10">
        <Typography id={id} />
      </label>
      <Controller
        control={control}
        name={name}
        render={({ field }) => (
          <KeybindRecorder
            keys={field.value}
            onKeysChange={field.onChange}
            ref={field.ref}
          />
        )}
      />
      <KeybindRecorderModal
        id=""
        label={label}
        control={control}
        resetField={resetField}
        name={name}
        delay={delay}
        isVisisble={true}
      />
      <div className="max-w-[45px]">
        <Button
          id="settings-keybinds_reset-button"
          variant="primary"
          onClick={() => {
            resetField(name);
            resetField(delay);
          }}
        />
      </div>
    </div>
  );
}
