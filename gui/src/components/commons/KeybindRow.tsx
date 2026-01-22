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
    <tr className="border-b border-background-60">
      <td className="px-6 py-4">
        <label className="text-sm font-medium text-background-10">
          {label}
        </label>
      </td>
      <td className="pr-4">
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
      <td className="pr-4">
        <NumberSelector
          control={control}
          name={delayName}
          min={0}
          max={10}
          step={0.5}
        />
      </td>

      <td className="pr-4 flex gap-2 justify-center">
        <Button
          variant="primary"
          onClick={() => setValue(bindingName, [])}
        >
          <ClearIcon size={12} />
        </Button>
        <Button
          variant="primary"
          onClick={() => resetField(bindingName)}
          >
            <ResetSettingIcon size={16} />
        </Button>
      </td>
    </tr>
  );
}
