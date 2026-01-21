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
        <label className="flex flex-col gap-1">
          {label}

          <div className="relative w-full flex gap-3 items-center rounded-lg">
            <div className='flex flex-grow h-full hover:ring-4 rounded-lg bg-background-80 cursor-pointer '>
              
              <input
                className="opacity-0 absolute cursor-pointer h-full"
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

              <div className="flex flex-grow h-full gap-1 min-h-[42px] items-center">
                {keys.map((key, index) => (
                  <div key={index} className="bg-red-500 p-2 rounded-md text-center max-h-[42px]">
                    {key}
                  </div>
                ))}
              </div>
              
              {isRecordingKeybind && (
                <div className="bsolute right-2 text-red-accent-500 text-sm flex items-center gap-1">
                  Recording keybind...
                </div>
              )}
            </div>

            <label className="flex flex-col gap-1">
              Delay (S)
              <input
                type="input"
                className={classNames}
                value={Number(delay)}
                onChange={(e) => onDelayChange(BigInt(e.target.value))}
              />
            </label>
          

            { /*
            <Button
              onClick={() => {
                //TODO: Reset field to default
              }}
              variant="primary"
            >
              Reset<ClearIcon size={12} />
            </Button>
            */ }

            <Button
              onClick={() => {
                onKeysChange([]);
              }}
              variant="primary"
            >
              Clear<ClearIcon size={12} />
            </Button>
          </div>
      </label>
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