import { useState, forwardRef } from 'react';

export const KeybindRecorder = forwardRef<
  HTMLInputElement,
  {
    keys: string[];
    onKeysChange: (v: string[]) => void;
  }
>(function KeybindRecorder({ keys, onKeysChange }, ref) {
  const [isRecording, setIsRecording] = useState(false);
  const [oldKeys, setOldKeys] = useState<string[]>([]);

  return (
    <div className="relative w-full">
      <input
        ref={ref}
        className="opacity-0 absolute inset-0 cursor-pointer"
        onFocus={() => {
          setOldKeys(keys);
          onKeysChange([]);
          setIsRecording(true);
        }}
        onBlur={() => {
          setIsRecording(false);
          if (keys.length < 4) onKeysChange(oldKeys);
        }}
        onKeyDown={(e) => {
          const key = e.key.toUpperCase();
          if (!keys.includes(key) && keys.length < 4) {
            onKeysChange([...keys, key]);
          }
        }}
      />

      <div className="flex gap-2 min-h-[42px] items-center px-3 rounded-lg bg-background-80">
        <div className="flex flex-grow gap-2 flex-wrap">
          {keys.map((key, i) => (
            <div
              key={i}
              className="bg-accent-background-50 px-3 py-1 rounded-md text-sm"
            >
              {key}
            </div>
          ))}
        </div>

        <div className="text-accent-background-10 text-sm font-medium">
          {keys.length < 4 && isRecording ? 'Recording…' : 'Click to record'}
        </div>
      </div>
    </div>
  );
});
