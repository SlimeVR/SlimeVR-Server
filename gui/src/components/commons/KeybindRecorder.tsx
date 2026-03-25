import { useLocalization } from '@fluent/react';
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
  const { l10n } = useLocalization();

  return (
    <div className="relative w-full">
      <style>
        {`
        @keyframes keyslot {
          0%, 100% { transform: scale(1); opacity: 0.6; }
          50% { transform: scale(1.08); opacity: 1; }
        }

        .keyslot-animate {
          animation: keyslot 1s ease-in-out infinite;
        }
      `}
      </style>
      <input
        ref={ref}
        className="opacity-0 absolute inset-0 cursor-pointer"
        onFocus={() => {
          setOldKeys(keys);
          onKeysChange(['CTRL', 'ALT']);
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
      <div className="flex gap-2 min-h-[42px] items-center px-3 py-2 rounded-lg bg-background-80">
        <div className="flex flex-grow gap-2 min-w-[180px]">
          {Array.from({ length: 4 }).map((_, i) => {
            const key = keys[i];
            const isActive = isRecording && i === keys.length;
            return (
              <div
                key={i}
                className={`
                px-2 py-1 rounded-md text-sm min-w-[32px] text-center
                ${key ? 'bg-accent-background-50' : 'bg-accent-background-50/30'}
                ${isActive ? 'keyslot-animate ring-2 ring-accent' : ''}
              `}
              >
                {key ?? ''}
              </div>
            );
          })}
        </div>
        <div className="w-40 flex-shrink-0 text-accent-background-10 text-right text-sm font-medium">
          {keys.length < 4 && isRecording
            ? l10n.getString('settings-keybinds_now-recording')
            : l10n.getString('settings-keybinds_record-keybind')}
        </div>
      </div>
    </div>
  );
});
