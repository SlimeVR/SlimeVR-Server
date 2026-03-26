import { useLocalization } from '@fluent/react';
import { useState, forwardRef, useRef } from 'react';
import { Typography } from './Typography';

const excludedKeys = ['CONTROL', ' ', 'SPACE', 'ALT', 'META'];
const keyOffset = 2;
const maxKeybindLength = 4;

export const KeybindRecorder = forwardRef<
  HTMLInputElement,
  {
    keys: string[];
    onKeysChange: (v: string[]) => void;
  }
>(function KeybindRecorder({ keys, onKeysChange }, ref) {
  const [localKeys, setLocalKeys] = useState<string[]>(keys);
  const [isRecording, setIsRecording] = useState(false);
  const [oldKeys, setOldKeys] = useState<string[]>([]);
  const [invalidSlot, setInvalidSlot] = useState<number | null>(null);
  const [showError, setShowError] = useState<boolean>(false);
  const [errorText, setErrorText] = useState<string>('');
  const inputRef = useRef<HTMLInputElement>(null);

  const { l10n } = useLocalization();

  const displayKeys = isRecording ? localKeys : keys;
  const activeIndex = isRecording ? displayKeys.length : -1;

  const handleKeyDown = (e: React.KeyboardEvent) => {
    e.preventDefault();
    const key = e.key.toUpperCase();
    const errorMsg = excludedKeys.includes(key)
      ? `Cannot use ${key}!`
      : displayKeys.includes(key)
        ? `${key} is a Duplicate Key!`
        : null;
    if (errorMsg) {
      setErrorText(errorMsg);
      setInvalidSlot(activeIndex);
      setShowError(true);
      setTimeout(() => {
        setInvalidSlot(null);
      }, 350);
      return;
    }

    if (displayKeys.length < maxKeybindLength) {
      setShowError(false);
      const updatedKeys = [...displayKeys, key];
      setLocalKeys(updatedKeys);
      onKeysChange([...keys, key]);
      if (updatedKeys.length === maxKeybindLength) {
        handleOnBlur();
      }
    }
  };

  const handleOnBlur = () => {
    console.log(`onblur keys length ${keys.length}`);
    if (inputRef != null && typeof inputRef !== 'function') {
      inputRef.current?.blur();
    }
    setIsRecording(false);
    setShowError(false);
    if (displayKeys.length == keyOffset) {
      onKeysChange(oldKeys);
      setLocalKeys(oldKeys);
    }
  };

  const handleOnFocus = () => {
    const initialKeys = ['CTRL', 'ALT'];
    setOldKeys(keys);
    setLocalKeys(initialKeys);
    onKeysChange(initialKeys);
    setIsRecording(true);
  };

  return (
    <div className="relative w-full">
      {!showError ? (
        <div className="absolute bottom-">
          <Typography>{errorText}</Typography>
        </div>
      ) : (
        ''
      )}
      <input
        ref={inputRef}
        className="opacity-0 absolute inset-0 cursor-pointer"
        onFocus={handleOnFocus}
        onBlur={handleOnBlur}
        onKeyDown={handleKeyDown}
      />
      <div className="flex gap-2 min-h-[42px] items-center px-3 py-2 rounded-lg bg-background-80">
        <div className="flex flex-grow gap-2 min-w-[180px]">
          {Array.from({ length: maxKeybindLength }).map((_, i) => {
            const key = displayKeys[i];
            const isActive = isRecording && i === activeIndex;
            const isInvalid = invalidSlot === i;
            return (
              <div
                key={i}
                className={`
                px-2 py-1 rounded-md text-sm min-w-[32px] text-center
                ${key ? 'bg-accent-background-50' : 'bg-accent-background-50/30'}
                ${
                  isInvalid
                    ? 'keyslot-invalid ring-2 ring-red-600'
                    : isActive
                      ? 'keyslot-animate ring-2 ring-accent'
                      : 'ring-accent'
                }
              `}
              >
                {key ?? ''}
              </div>
            );
          })}
        </div>
        <div className="w-40 flex-shrink-0 text-accent-background-10 text-right text-sm font-medium">
          {displayKeys.length < maxKeybindLength && isRecording
            ? l10n.getString('settings-keybinds_now-recording')
            : l10n.getString('settings-keybinds_record-keybind')}
        </div>
      </div>
    </div>
  );
});
