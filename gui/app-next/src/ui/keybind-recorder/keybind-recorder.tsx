import {
  createEffect,
  createSignal,
  createUniqueId,
  For,
  onCleanup,
  Show,
} from 'solid-js';
import { useBack } from '@/lib/back-stack';
import { cx } from '@/lib/cx';
import { captureKeys } from '@/lib/keyboard';
import {
  keybindKey,
  keybindLabel,
  MODIFIER_ORDER,
  modifierNameForKey,
  modifiersFromHeld,
  resolveKey,
} from '@/lib/keybind-keys';
import { t } from '@/lib/l10n';
import { platform } from '@/lib/platform';
import { fieldBlocked, type FieldProps, fieldState, idList } from '@/ui/field';
import { Kbd, type KbdVariant } from './kbd';
import { Skeleton } from '@/ui/skeleton';
import { useMotionItem } from '@/ui/motion';
import { Tooltip } from '@/ui/tooltip';

export const MAX_KEYBIND_LENGTH = 5;
export const UNSUPPORTED_FLASH_MS = 350;

export interface KeybindRecorderProps extends FieldProps<string[]> {
  class?: string;
}

const hasMain = (keys: readonly string[]) =>
  keys.some((k) => !MODIFIER_ORDER.includes(k));

export function KeybindRecorder(props: KeybindRecorderProps) {
  let el!: HTMLDivElement;
  const tipId = createUniqueId();
  const hintId = createUniqueId();
  const state = () => fieldState(props);
  const reason = () =>
    state() === 'disabled' ? props.disabledReason : undefined;

  const [recording, setRecording] = createSignal(false);
  // Longest combination seen while keys were held
  const [best, setBest] = createSignal<string[] | null>(null);
  const [flash, setFlash] = createSignal(false);
  // KeyboardEvent.code values, left and right modifiers stay distinct
  const held = new Set<string>();
  let flashTimer: ReturnType<typeof setTimeout> | undefined;
  onCleanup(() => clearTimeout(flashTimer));

  useMotionItem(() => el, { owns: () => (recording() ? 'all' : 'none') });
  useBack(stop, { when: recording });

  createEffect(() => {
    if (!recording()) return;
    const release = captureKeys(onCapture);
    platform.keybindRecording?.(true);
    onCleanup(() => {
      release();
      platform.keybindRecording?.(false);
    });
  });

  createEffect(() => {
    if (fieldBlocked(state())) stop();
  });

  function start() {
    if (!fieldBlocked(state())) setRecording(true);
  }

  function stop() {
    held.clear();
    clearTimeout(flashTimer);
    setFlash(false);
    setBest(null);
    setRecording(false);
  }

  function record(keys: string[]) {
    // A main key once seen is kept while modifiers change
    setBest((prev) => (hasMain(keys) || !prev || !hasMain(prev) ? keys : prev));
  }

  function onCapture(e: KeyboardEvent): boolean | void {
    if (held.size === 0 && e.key === 'Escape') return stop();
    if (held.size === 0 && e.key === 'Tab') {
      stop();
      return true;
    }
    if (e.repeat) return;
    setFlash(false);
    if (modifierNameForKey(e.key)) {
      held.add(e.code);
      record(modifiersFromHeld(held));
      return;
    }
    const key = resolveKey(e);
    if (!key) {
      clearTimeout(flashTimer);
      setFlash(true);
      flashTimer = setTimeout(() => setFlash(false), UNSUPPORTED_FLASH_MS);
      return;
    }
    record([...modifiersFromHeld(held), key]);
    held.add(e.code);
  }

  function onKeyUp(e: KeyboardEvent) {
    if (!recording() || !held.delete(e.code) || held.size > 0) return;
    const keys = best();
    stop();
    if (keys && keybindKey(keys) !== keybindKey(props.value))
      props.onChange(keys);
  }

  // Pad A arrives as a synthetic Enter
  function onKeyDown(e: KeyboardEvent) {
    if (recording() || (e.key !== 'Enter' && e.key !== ' ')) return;
    e.preventDefault();
    start();
  }

  const shown = () => (recording() ? (best() ?? []) : props.value);
  const slots = () =>
    recording()
      ? Math.min(shown().length + 1, MAX_KEYBIND_LENGTH)
      : Math.max(shown().length, 1);

  function variant(i: number): KbdVariant {
    const key = shown()[i];
    if (key !== undefined) return 'default';
    if (!recording()) return 'empty';
    return flash() ? 'invalid' : 'active';
  }

  return (
    <>
      <div
        ref={el}
        role="button"
        tabIndex={0}
        id={props.id}
        aria-labelledby={props.labelledBy}
        aria-describedby={idList(
          props.describedBy,
          recording() && hintId,
          reason() && tipId
        )}
        aria-disabled={state() === 'disabled' ? 'true' : undefined}
        aria-invalid={state() === 'error' ? 'true' : undefined}
        inert={state() === 'loading'}
        class={cx(
          `
            flex min-h-control cursor-pointer flex-wrap items-center gap-xs rounded-md
            bg-surface-control px-3 py-xs text-fg not-aria-disabled:hover:bg-surface-hover
            aria-disabled:cursor-not-allowed aria-disabled:opacity-50 aria-invalid:ring-1
            aria-invalid:ring-critical
          `,
          recording() && 'ring-2 ring-accent',
          props.class
        )}
        onClick={start}
        onKeyDown={onKeyDown}
        onKeyUp={onKeyUp}
        onBlur={stop}
      >
        <Show when={state() === 'loading'}>
          <Skeleton class="h-6 w-24" />
        </Show>
        <For
          each={
            state() === 'loading'
              ? []
              : Array.from({ length: slots() }, (_, i) => i)
          }
        >
          {(i) => (
            <>
              <Show when={i > 0}>
                <span class="font-bold" aria-hidden="true">
                  +
                </span>
              </Show>
              <Kbd variant={variant(i)}>
                {shown()[i] !== undefined
                  ? keybindLabel(shown()[i]!)
                  : recording() && '...'}
              </Kbd>
            </>
          )}
        </For>
        <Show when={recording()}>
          <span id={hintId} class="ml-auto text-fg-muted">
            {t('settings-keybinds-recorder-hint-recording')}
          </span>
        </Show>
      </div>
      <span class="sr-only" role="status">
        {flash() ? t('settings-keybinds-error-unsupported-key') : ''}
      </span>
      <Tooltip id={tipId} anchor={() => el} text={reason()} />
    </>
  );
}
