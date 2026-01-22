import { Controller, Control, UseFormSetValue } from 'react-hook-form';
import { Button } from './Button';
import { ClearIcon } from './icon/ClearIcon';
import { NumberSelector } from './NumberSelector';
import { KeybindRecorder } from './KeybindRecorder';

export function KeybindRow({
  label,
  control,
  setValue,
  bindingName,
  delayName,
}: {
  label: string;
  control: Control<any>;
  setValue: UseFormSetValue<any>;
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

      <td>
        <Button
          variant="primary"
          onClick={() => setValue(bindingName, [])}
        >
          <ClearIcon size={12} />
        </Button>

        <Button
          variant="primary"
          onClick={}
          >
            Reset
          </Button>
      </td>
    </tr>
  );
}
