import { useState, forwardRef, useRef } from 'react';
import { Typography } from './Typography';
import classNames from 'classnames';
import { useFormContext } from 'react-hook-form';

const excludedKeys = [' ', 'SPACE', 'META'];
const maxKeybindLength = 4;

export const KeybindRecorder = forwardRef<
  HTMLInputElement,
  {
    keys: string[];
    onKeysChange: (v: string[]) => void;
    error?: string;
  }
>(function KeybindRecorder({ keys, onKeysChange, error }) {
  const [localKeys, setLocalKeys] = useState<string[]>(keys);
  const [isRecording, setIsRecording] = useState(false);
  const [oldKeys, setOldKeys] = useState<string[]>([]);
  const [invalidSlot, setInvalidSlot] = useState<number | null>(null);
  const [errorText, setErrorText] = useState<string>('');
  const inputRef = useRef<HTMLInputElement>(null);
  const displayKeys = isRecording ? localKeys : keys;
  const activeIndex = isRecording ? displayKeys.length : -1;
  const displayError = errorText || error;

  const { clearErrors } = useFormContext();

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
      setTimeout(() => {
        setInvalidSlot(null);
      }, 350);
      return;
    }

    if (displayKeys.length < maxKeybindLength) {
      const updatedKeys = [...displayKeys, key];
      setLocalKeys(updatedKeys);
      onKeysChange(updatedKeys);
      if (updatedKeys.length == maxKeybindLength) {
        inputRef.current?.blur();
      }
    }
  };

  const handleOnBlur = () => {
    setIsRecording(false);
    if (displayKeys.length < maxKeybindLength - 2 || error) {
      onKeysChange(oldKeys);
      setLocalKeys(oldKeys);
    }
  };

  const handleOnFocus = () => {
    clearErrors('keybinds');
    const initialKeys: string[] = [];
    setOldKeys(keys);
    setLocalKeys(initialKeys);
    onKeysChange(initialKeys);
    setIsRecording(true);
  };

  return (
    <div className="w-full justify-center items-center flex flex-col gap-2">
      <div className="flex gap-2 p-2 items-center rounded-lg relative">
        <input
          autoFocus
          ref={inputRef}
          className="opacity-0 absolute cursor-pointer w-full"
          onFocus={handleOnFocus}
          onBlur={handleOnBlur}
          onKeyDown={handleKeyDown}
        />
        <div className="flex flex-grow gap-2 justify-center h-full">
          {Array.from({ length: maxKeybindLength }).map((_, i) => {
            const key = displayKeys[i];
            const isActive = isRecording && i === activeIndex;
            const isInvalid = invalidSlot === i;
            return (
              <div key={i} className="flex flex-row">
                <div
                  className={classNames(
                    'flex p-2 rounded-lg min-w-[50px] min-h-[50px] text-main-title justify-center items-center bg-background-80 mobile:text-sm',
                    {
                      'keyslot-invalid ring-2 ring-status-critical': isInvalid,
                      'keyslot-animate ring-2 ring-accent':
                        isActive && !isInvalid,
                      'ring-accent': !isInvalid && !isInvalid,
                    }
                  )}
                >
                  {key ?? ''}
                </div>
                <div className="flex pl-2 text-main-title justify-center items-center mobile:text-sm">
                  {i < maxKeybindLength - 1 ? '+' : ''}
                </div>
              </div>
            );
          })}
        </div>
      </div>
      {displayError && (
        <div className="isInvalid keyslot-invalid">
          <Typography color="text-status-critical">{`${errorText} ${error}`}</Typography>
        </div>
      )}
    </div>
  );
});
