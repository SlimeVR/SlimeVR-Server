import { Controller, Control, UseFormSetValue, UseFormResetField } from 'react-hook-form';
import { Button } from './Button';
import { ClearIcon } from './icon/ClearIcon';
import { NumberSelector } from './NumberSelector';
import { KeybindRecorder } from './KeybindRecorder';
import { ResetSettingIcon } from './icon/ResetSettingIcon';

export function KeybindRow({
  label,
  control,
  setValue,
  resetField,
  bindingName,
  delayName,
}: {
  label: string;
  control: Control<any>;
  setValue: UseFormSetValue<any>;
  resetField: UseFormResetField<any>;
  bindingName: string;
  delayName: string;
}) {
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
            min={0}
            max={10}
            step={1.0}
          />
      </td>

      <td className="px-2">
        <div className="flex gap-2 justify-center px-4">
          <Button
            variant="primary"
            onClick={() => resetField(bindingName)}
            >
              Reset
          </Button>
        </div>
      </td>
    </tr>
  );
}
