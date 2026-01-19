import { useState, useRef, forwardRef, useEffect } from 'react';
import { Controller, Control } from 'react-hook-form';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from './Button';
import { ClearIcon } from './icon/ClearIcon';



interface keybindInternalProps {
  label: string;
  value: string[];
  onChange: (value: string[]) => void;
}

interface KeybindProps {
  label: string;
  name: string;
  delay: number;
  control: Control<any>
}

/*
export const KeybindInputInternal = forwardRef<
  HTMLInputElement,
  {
    disabled?: boolean;
    label?: string;
    name: string;
  } & Partial<React.HTMLProps<HTMLInputElement>>
  >(function AppKeybindInput(
    {
      label,
      onChange,
      name,
    },
    ref
  ) {

  })
  */


export function KeybindInput({
    label,
    control,
    name,
    delay
}: KeybindProps) {
  const keyCountRef = useRef(0);
  const ref = useRef<HTMLInputElement>(null);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const handleKeyDown = (event: any, value: string[], onChange: any) => {
    if (keyCountRef.current < 3) {
      const newKeys = [...value, event.key];
      keyCountRef.current++;
    }
  };

  const setFocus = () => {
    if (ref.current) {
      ref.current.focus();
    }
  };

  const emptyKeybind = (onchange: any) => {
    keyCountRef.current = 0;
    onchange([]);
  };

  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value = [], ref} }) => (
      <label className="flex flex-col gap-1">
          {label}
        <div className="relative w-full flex gap-2 items-center" onClick={setFocus}>
          <input
            className="opacity-0 absolute"
            onKeyDown={(event) => {
              if (keyCountRef.current < 3) {
                const newKeys = [...value, event.key];
                keyCountRef.current++;
                onChange(newKeys);
              }
            }}
            ref={ref}
          />
          <div className="flex flex-grow h-full gap-2" onClick={() => setFocus()}>
            {value.map((key: String) => <div className='bg-red-500 p-2 rounded-md'>{key}</div>)}
          </div>
          <div>current value {value}</div>
          <div>
            <label>
              Delay:
              <input
                className=''
              />  
            </label>
          </div>
          <Button
            onClick={() => {
              keyCountRef.current = 0
              onChange([]);
            }}
            variant='primary'
          >
              <ClearIcon size={12}/>
          </Button>

        </div>
      </label>
      )}
    />
  );
}
