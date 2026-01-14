import { useState, useRef, forwardRef, useEffect } from 'react';
import { FieldError } from 'react-hook-form';
import { Button } from './Button';
import { ClearIcon } from './icon/ClearIcon';


interface KeybindProps {
    label?: string;
    name: string;
}


export function Keybind({
    label,
    name
}: KeybindProps) {
  const [key, setKey] = useState('');
  const [recordedKeybind, setRecordedKeybind] = useState<string[]>([]);
  const keyCountRef = useRef(0);
  const ref = useRef<HTMLInputElement>(null);

  const handleKeyDown = (event: any) => {
    console.log("HEY")
    if (keyCountRef.current < 3) {
      setKey(event.key);
      setRecordedKeybind((curr) => [...curr, event.key]);
      keyCountRef.current++;
    }
  };

  const setFocus = () => {
    if (ref.current) {
      ref.current.focus();
    }
  };

  const emptyKeybind = () => {
    setRecordedKeybind((curr) => [])
    keyCountRef.current = 0;
  };

  return (
    <label className="flex flex-col gap-1">
        {label}
      <div className="relative w-full flex gap-2 items-center hover:border-purple-400" onClick={setFocus}>
        <input
          className="opacity-0 absolute"
          onKeyDown={handleKeyDown}
          ref={ref}
        />
        <div className="flex flex-grow h-full gap-2" onClick={() => setFocus()}>
          {recordedKeybind.map((key) => <div className='bg-red-500 p-2 rounded-md'>{key}</div>)}
        </div>
        <Button
          onClick={emptyKeybind}
          variant='primary'
        >
            <ClearIcon size={12}/>
        </Button>
      </div>
    </label>
  );
}
