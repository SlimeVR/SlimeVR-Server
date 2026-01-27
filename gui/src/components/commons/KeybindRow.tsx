import { Controller, Control, UseFormResetField } from 'react-hook-form';
import { Button } from './Button';
import { NumberSelector } from './NumberSelector';
import { KeybindRecorder } from './KeybindRecorder';
import { useLocaleConfig } from '@/i18n/config';

export function KeybindRow({
  label,
  control,
  resetField,
  bindingName,
  delayName,
}: {
  label: string;
  control: Control<any>;
  resetField: UseFormResetField<any>;
  bindingName: string;
  delayName: string;
}) {
  const { currentLocales } = useLocaleConfig();
  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });
  return (
    <tr className="border-b border-background-60 h-20">
      <td className="px-6 py-4 pr-4">
        <label className="text-sm font-medium text-background-10">
          {label}
        </label>
      </td>
      <td className="px-4">
        <Controller
          control={control}
          name={bindingName}
          render={({ field }) => (
            <KeybindRecorder
              keys={field.value ?? []}
              onKeysChange={field.onChange}
              ref={field.ref}
            />
          )}
        />
      </td>
      <td className="px-4">
        <NumberSelector
          control={control}
          name={delayName}
          valueLabelFormat={(value) => secondsFormat.format(value)}
          min={0}
          max={10}
          step={0.2}
        />
      </td>

      <td className="px-2">
        <div className="flex gap-2 justify-center px-4">
          <Button
            variant="primary"
            onClick={() => {
              resetField(bindingName);
              resetField(delayName);
            }}
          >
            Reset
          </Button>
        </div>
      </td>
    </tr>
  );
}
