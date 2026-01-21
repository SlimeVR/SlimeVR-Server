import { useState, forwardRef } from 'react';
import { Controller, Control } from 'react-hook-form';
import { Button } from './Button';
import { ClearIcon } from './icon/ClearIcon';


export const KeybindInputInternal = forwardRef<
  HTMLInputElement,
  {
    label: string;
    keys: string[];
    delay: bigint;
    onKeysChange: (v: string[]) => void;
    onDelayChange: (v: bigint) => void;
  } & Partial<React.HTMLProps<HTMLInputElement>>
  >(function AppKeybindInputInternal(
    {
      label,
      keys,
      delay,
      onKeysChange,
      onDelayChange,
    },
    ref
  ) {
    //TODO: This is probably bad code but it fixes a bug where after loading you can add more than 4 keys
    //TODO: Add a way to reset values to default
    //TODO: Empty box when starting keybind record without throwing away old keybind, only overwrite keybind when new keybind is valid
    //TODO: Make delay work
    const [isRecordingKeybind, setIsRecordingKeybind] = useState(false)
    const [oldKeybind, setOldKeybind] = useState<string[]>([])
    const classNames = "max-h-[32px] placeholder:text-background-10 placeholder:italic bg-background-60 border-background-60 w-full focus:ring-transparent focus:ring-offset-transparent min-h-[42px] z-10 focus:outline-transparent rounded-md focus:border-accent-background-40 text-standard text-background-10 relative transition-colors"

    return (
      <table className="w-full border-collapse">
        <thead>
          <tr>
            <th className="text-left pb-3">
              <label className="text-sm font-medium text-background-10">
                {label}
              </label>
            </th>
            <th className="text-left pb-3 pl-4">
              <label className="text-sm font-medium text-background-10">
                Delay (S)
              </label>
            </th>
            <th className="pb-3"></th>
          </tr>
        </thead>

        <tbody>
          <tr>
            <td className="pr-4">
              <div className="relative w-full">
                <input
                  className="opacity-0 absolute cursor-pointer inset-0"
                  name="keybindInput"
                  ref={ref}
                  onFocus={() => {
                    setOldKeybind(keys)
                    onKeysChange([])
                    setIsRecordingKeybind(true)
                  }}
                  onBlur={() => {
                    setIsRecordingKeybind(false)
                    if (keys.length < 4) {
                        onKeysChange(oldKeybind)
                    }
                  }}
                  onKeyDown={(event) => {
                    if (keys.length < 4) {
                      const key = event.key.toUpperCase()
                      //Prevent duplicate keys logic might not be good
                      if (!keys.includes(key)) {
                      const newKeys = [...keys, event.key.toUpperCase()];
                      onKeysChange(newKeys);
                      }
                    } else {
                      setIsRecordingKeybind(false)
                    }
                  }}
                />

                <div className='flex gap-2 min-h-[42px] min-w-[420px] items-center px-3 rounded-lg bg-background-80 hover:ring-4 hover:ring-accent-background-40 transition-all cursor-pointer'>
                  <div className="flex flex-grow gap-2 flex-wrap">
                    {keys.map((key, index) => (
                      <div key={index} className="bg-accent-background-40 px-3 py-1 rounded-md text-center text-sm font-medium text-background-10">
                        {key}
                      </div>
                    ))}
                  </div>
                
                  {isRecordingKeybind && (
                    <div className="text-accent-background-40 text-sm whitespace-nowrap font-medium">
                      Recording...
                    </div>
                  )}
                  {!isRecordingKeybind && (
                    <div className="text-accent-background-40 text-sm whitespace-nowrap font-medium">
                      Click to record
                    </div>
                  )}
                </div>
              </div>
            </td>

            <td className="pl-4 pr-4">
              <input
                type="number"
                className={classNames}
                value={Number(delay)}
                onChange={(e) => onDelayChange(BigInt(e.target.value))}
              />
            </td>

            <td>
              <Button
                onClick={() => {
                  onKeysChange([]);
                }}
                variant="primary"
              >
                Clear<ClearIcon size={12} />
              </Button>
            </td>
          </tr>
        </tbody>
      </table>
    );
  });
  


export function KeybindInput({
    label,
    control,
    bindingName,
    delayName
}: {
  label: string;
  control: Control<any>;
  bindingName: string;
  delayName: string;
}) {
  return (
    <Controller
      control={control}
      name={bindingName}
      render={({ field: { value: keys = [], onChange: onKeysChange, ref} }) => (
        <Controller
          control={control}
          name={delayName}
          render={({ field: { value: delay, onChange: onDelayChange } }) => (
            <KeybindInputInternal
              label={label}
              keys={keys}
              delay={delay}
              onKeysChange={onKeysChange}
              onDelayChange={onDelayChange}
              ref={ref}
            />
          )}
        />
      )}
    />
  );
}