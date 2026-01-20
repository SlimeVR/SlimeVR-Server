import { useState, useRef, forwardRef, useEffect } from 'react';
import { Controller, Control, ResetFieldConfig, UseFormResetField } from 'react-hook-form';
import { useMemo } from 'react';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Typography } from './Typography';
import { Button } from './Button';
import { ClearIcon } from './icon/ClearIcon';
import { RecordIcon } from './icon/RecordIcon';


export const KeybindInputInternal = forwardRef<
  HTMLInputElement,
  {
    label: string;
    keys: string[];
    delay: number;
    resetField: UseFormResetField<any>;
    onKeysChange: (v: string[]) => void;
    onDelayChange: (v: number) => void;
  } & Partial<React.HTMLProps<HTMLInputElement>>
  >(function AppKeybindInputInternal(
    {
      label,
      keys,
      delay,
      resetField,
      onKeysChange,
      onDelayChange,
    },
    ref
  ) {
    //TODO: This is probably bad code but it fixes a bug where after loading you can add more than 4 keys
    const keyCountRef = useRef(keys.length);
    const [isRecordingKeybind, setIsRecordingKeybind] = useState(false)

    const classNames = "max-h-[32px] placeholder:text-background-10 placeholder:italic bg-background-60 border-background-60 w-full focus:ring-transparent focus:ring-offset-transparent min-h-[42px] z-10 focus:outline-transparent rounded-md focus:border-accent-background-40 text-standard text-background-10 relative transition-colors"
    console.log(`Hello from ${label}`)
    console.log(keys)
    console.log(delay)
    return (
        <label className="flex flex-col gap-1">
          {label}

          <div className="relative w-full flex gap-3 items-center rounded-lg cursor-pointer ">
            <div className='flex flex-grow h-full hover:ring-4 rounded-lg bg-background-80 '>
              <input
                className="opacity-0 absolute cursor-pointer h-full"
                ref={ref}
                onKeyDown={(event) => {
                  if (keyCountRef.current < 4) {

                    const newKeys = [...keys, event.key];
                    keyCountRef.current++;
                    onKeysChange(newKeys);
                  }
                }}
              />

              <div className="flex flex-grow h-full gap-1 min-h-[42px] items-center">
                {keys.map((key) => (
                  <div key={key} className="bg-red-500 p-2 rounded-md text-center max-h-[42px]">
                    {key}
                  </div>
                ))}
              </div>

              <Button
                variant="primary"
                  onClick={() => {
                  setIsRecordingKeybind(!isRecordingKeybind);
                }}
              >
                {isRecordingKeybind ? "Recording Keybind" : "Record Keybind"}
              </Button>
            </div>

            <label className="flex flex-col gap-1">
              Delay
              <input
                type="number"
                className={classNames}
                value={delay}
                onChange={(e) => onDelayChange(Number(e.target.value))}
              />
            </label>
          

            <Button
              onClick={() => {
                //TODO: Reset field to default
              }}
              variant="primary"
            >
              Reset<ClearIcon size={12} />
            </Button>

            <Button
              onClick={() => {
                keyCountRef.current = 0;
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
    resetField,
    bindingName,
    delayName
}: {
  label: string;
  control: Control<any>;
  resetField: UseFormResetField<any>;
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
              resetField={resetField}
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