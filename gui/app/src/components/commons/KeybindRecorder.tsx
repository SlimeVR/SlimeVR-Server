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
import {
  MODIFIER_ORDER,
  keybindLabel,
  modifierNameForKey,
  modifiersFromHeld,
  resolveKey,
} from './keybind-keys';

const maxKeybindLength = 5;

type RecorderState = {
  held: Set<string>;
  best: string[] | null;
  flash: { slot: number; msgId: string } | null;
};

type RecorderAction =
  | { type: 'keydown'; code: string; keys: string[] }
  | { type: 'keyup'; code: string }
  | { type: 'clear' }
  | { type: 'flash'; slot: number; msgId: string }
  | { type: 'clearFlash' };

function recorderReducer(
  state: RecorderState,
  action: RecorderAction
): RecorderState {
  switch (action.type) {
    case 'keydown': {
      const held = new Set(state.held);
      held.add(action.code);
      const hasMain = action.keys.some((k) => !MODIFIER_ORDER.includes(k));
      const bestHasMain = (state.best ?? []).some(
        (k) => !MODIFIER_ORDER.includes(k)
      );
      const best =
        hasMain || !bestHasMain ? action.keys : (state.best ?? action.keys);
      return { held, best, flash: null };
    }
    case 'keyup': {
      const held = new Set(state.held);
      held.delete(action.code);
      return { ...state, held };
    }
    case 'clear':
      return { held: new Set(), best: null, flash: null };
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
    held: new Set<string>(),
    best: null,
    flash: null,
  });
  const [focused, setFocused] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  useImperativeHandle(ref, () => inputRef.current!, []);

  const recording = state.held.size > 0;
  const showing = recording ? (state.best ?? []) : keys;
  const activeIndex = showing.length;
  const slotCount = recording
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
    const hasModifier = state.held.size > 0;

    if (!hasModifier && e.key === 'Escape') {
      onCloseModal?.();
      return;
    }
    if (!hasModifier && (e.key === 'Backspace' || e.key === 'Delete')) {
      onUnbindModal?.();
      return;
    }
    if (!hasModifier && e.key === 'Enter') {
      onSubmitModal?.();
      return;
    }

    e.preventDefault();
    e.stopPropagation();
    if (e.repeat) return;

    const modifierName = modifierNameForKey(e.key);
    if (modifierName) {
      dispatch({ type: 'keydown', code: e.code, keys: modifiersFromHeld(new Set(state.held).add(e.code)) });
      return;
    }

    const key = resolveKey(e);
    if (!key) {
      dispatch({
        type: 'flash',
        slot: activeIndex,
        msgId: 'settings-keybinds-error-unsupported-key',
      });
      return;
    }

    dispatch({
      type: 'keydown',
      code: e.code,
      keys: [...modifiersFromHeld(state.held), key],
    });
  };

  const handleKeyUp = (e: React.KeyboardEvent) => {
    const wasRecording = state.held.size > 0;
    dispatch({ type: 'keyup', code: e.code });

    if (!wasRecording) return;
    const stillHeld = new Set(state.held);
    stillHeld.delete(e.code);
    if (stillHeld.size === 0 && state.best) {
      onKeysChange(state.best);
      dispatch({ type: 'clear' });
    }
  };

  const handleBlur = () => {
    setFocused(false);
    if (state.held.size > 0) dispatch({ type: 'clear' });
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
          data-nav-raw
          onFocus={() => setFocused(true)}
          onBlur={handleBlur}
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
                  {key != null ? keybindLabel(key) : isActive ? '...' : ''}
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
