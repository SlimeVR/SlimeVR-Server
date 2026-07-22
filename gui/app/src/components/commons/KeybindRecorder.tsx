import {
  forwardRef,
  useReducer,
  useRef,
  useState,
  useEffect,
  useImperativeHandle,
} from 'react';
import { Typography } from './Typography';
import { Kbd } from './Kbd';
import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import './KeybindRow.scss';

const MODIFIER_ORDER = ['CTRL', 'ALT', 'SHIFT', 'SUPER'];
const NON_SHIFT_MODIFIERS = ['CTRL', 'ALT', 'SUPER'];
const MODIFIER_KEY_NAMES: Record<string, string> = {
  CONTROL: 'CTRL',
  ALT: 'ALT',
  SHIFT: 'SHIFT',
  META: 'SUPER',
  OS: 'SUPER',
};

const maxKeybindLength = 5;

function orderModifiers(mods: string[]): string[] {
  return MODIFIER_ORDER.filter((m) => mods.includes(m));
}

export function isValidKeybind(keys: string[]): boolean {
  const main = keys.filter((k) => !MODIFIER_ORDER.includes(k));
  if (main.length !== 1 || !/^[A-Z0-9]$/.test(main[0])) return false;
  return keys.some((k) => NON_SHIFT_MODIFIERS.includes(k));
}

export function keybindKey(keys: string[]): string {
  return [...keys].sort().join('+');
}

function resolveMainKey(code: string): string | null {
  if (code.startsWith('Key')) return code.slice(3);
  if (code.startsWith('Digit')) return code.slice(5);
  if (code.startsWith('Numpad') && code.length === 7 && /^\d$/.test(code[6]))
    return code[6];
  return null;
}

function modifiersFromEvent(e: React.KeyboardEvent): string[] {
  const mods: string[] = [];
  if (e.ctrlKey) mods.push('CTRL');
  if (e.altKey) mods.push('ALT');
  if (e.shiftKey) mods.push('SHIFT');
  if (e.metaKey) mods.push('SUPER');
  return orderModifiers(mods);
}

type RecorderState = {
  preview: string[] | null;
  flash: { slot: number; msgId: string } | null;
};

type RecorderAction =
  | { type: 'preview'; keys: string[] }
  | { type: 'clearPreview' }
  | { type: 'flash'; slot: number; msgId: string }
  | { type: 'clearFlash' };

function recorderReducer(
  state: RecorderState,
  action: RecorderAction
): RecorderState {
  switch (action.type) {
    case 'preview':
      return { preview: action.keys, flash: null };
    case 'clearPreview':
      return { ...state, preview: null };
    case 'flash':
      return { ...state, flash: { slot: action.slot, msgId: action.msgId } };
    case 'clearFlash':
      return { ...state, flash: null };
  }
}

export const KeybindRecorder = forwardRef<
  HTMLInputElement,
  {
    keys: string[];
    onKeysChange: (v: string[]) => void;
    error?: string;
    onSubmitModal?: () => void;
    onUnbindModal?: () => void;
    onCloseModal?: () => void;
  }
>(function KeybindRecorder(
  { keys, onKeysChange, error, onSubmitModal, onUnbindModal, onCloseModal },
  ref
) {
  const { l10n } = useLocalization();
  const [state, dispatch] = useReducer(recorderReducer, {
    preview: null,
    flash: null,
  });
  const [focused, setFocused] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  useImperativeHandle(ref, () => inputRef.current!, []);

  const showing = state.preview ?? keys;
  const activeIndex = showing.length;
  const slotCount = state.preview
    ? Math.min(showing.length + 1, maxKeybindLength)
    : Math.max(showing.length, 1);
  const displayError = state.flash ? l10n.getString(state.flash.msgId) : error;

  useEffect(() => {
    const timer = setTimeout(() => inputRef.current?.focus(), 50);
    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    if (!state.flash) return;
    const timer = setTimeout(() => dispatch({ type: 'clearFlash' }), 350);
    return () => clearTimeout(timer);
  }, [state.flash]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    e.preventDefault();
    e.stopPropagation();

    if (e.key === 'Escape') {
      onCloseModal?.();
      return;
    }
    if (e.key === 'Backspace' || e.key === 'Delete') {
      onUnbindModal?.();
      return;
    }
    if (e.key === 'Enter') {
      onSubmitModal?.();
      return;
    }

    const modifiers = modifiersFromEvent(e);

    if (MODIFIER_KEY_NAMES[e.key.toUpperCase()]) {
      dispatch({ type: 'preview', keys: modifiers });
      return;
    }

    const mainKey = resolveMainKey(e.code);
    if (!mainKey) {
      dispatch({
        type: 'flash',
        slot: activeIndex,
        msgId: 'settings-keybinds-error-letters-numbers-only',
      });
      return;
    }

    dispatch({ type: 'clearPreview' });
    onKeysChange([...modifiers, mainKey]);
  };

  const handleKeyUp = (e: React.KeyboardEvent) => {
    if (state.preview === null) return;
    if (
      e.key === 'Escape' ||
      e.key === 'Enter' ||
      e.key === 'Backspace' ||
      e.key === 'Delete'
    )
      return;
    const modifiers = modifiersFromEvent(e);
    dispatch(
      modifiers.length > 0
        ? { type: 'preview', keys: modifiers }
        : { type: 'clearPreview' }
    );
  };

  return (
    <div className="relative w-full">
      <div
        className={classNames(
          'flex flex-col gap-4 p-4 rounded-2xl bg-background-70 transition-all relative z-10 shadow-lg border',
          displayError
            ? 'border-status-critical'
            : focused
              ? 'border-accent-background-30'
              : 'border-transparent'
        )}
        onClick={() => inputRef.current?.focus()}
      >
        <input
          className="absolute inset-0 opacity-0 cursor-pointer w-full h-full"
          ref={inputRef}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          onKeyDown={handleKeyDown}
          onKeyUp={handleKeyUp}
        />
        <div className="flex flex-wrap gap-2 justify-center items-center">
          {Array.from({ length: slotCount }).map((_, i) => {
            const key = showing[i];
            const isActive = i === activeIndex && key == null;
            const isInvalid = state.flash?.slot === i;
            const variant = isInvalid
              ? 'invalid'
              : isActive
                ? 'active'
                : key != null
                  ? 'default'
                  : 'empty';
            return (
              <div key={i} className="flex items-center gap-2">
                <Kbd
                  variant={variant}
                  className="px-4 py-2 min-w-[54px] h-[48px]"
                >
                  {key ?? (isActive ? '...' : '')}
                </Kbd>
                {i < slotCount - 1 && (
                  <Typography variant="standard" bold textAlign="text-center">
                    +
                  </Typography>
                )}
              </div>
            );
          })}
        </div>

        <div className="text-center">
          <Typography id="settings-keybinds-recorder-hint-recording" />
        </div>
      </div>

      {displayError && (
        <div className="absolute inset-x-0 top-full z-0 -mt-4 pt-6 pb-2 px-4 bg-background-80 rounded-b-2xl text-status-critical text-sm font-medium text-center">
          {displayError}
        </div>
      )}
    </div>
  );
});
