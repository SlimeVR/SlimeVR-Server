import { createSignal, For, untrack } from 'solid-js';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { Floating } from '@/ui/floating';
import CheckRoundedIcon from '~icons/material-symbols/check-rounded';
import ErrorRoundedIcon from '~icons/material-symbols/error-rounded';

export type ToastKind = 'success' | 'error';

export interface ToastOptions {
  kind: ToastKind;
  message: MessageId;
  args?: MessageArgs;
  // A toast with the same key is replaced and its timer restarts
  key?: string;
  durationMs?: number;
}

export interface Toast {
  id: number;
  kind: ToastKind;
  message: MessageId;
  args?: MessageArgs;
  key?: string;
}

const MAX_TOASTS = 3;
const DURATION_MS: Record<ToastKind, number> = { success: 2000, error: 5000 };

const [toasts, setToasts] = createSignal<readonly Toast[]>([]);
const timers = new Map<number, ReturnType<typeof setTimeout>>();
let nextId = 1;

export { toasts };

function clearTimer(id: number) {
  clearTimeout(timers.get(id));
  timers.delete(id);
}

export function dismissToast(id: number) {
  clearTimer(id);
  setToasts((list) => list.filter((toast) => toast.id !== id));
}

export function showToast(options: ToastOptions): number {
  const list = untrack(toasts);
  const existing =
    options.key === undefined
      ? undefined
      : list.find((toast) => toast.key === options.key);
  const same =
    existing?.message === options.message && existing.args === options.args;
  const toast: Toast =
    existing && same
      ? existing
      : {
          id: existing?.id ?? nextId++,
          kind: options.kind,
          message: options.message,
          args: options.args,
          key: options.key,
        };

  let next = existing
    ? list.map((item) => (item === existing ? toast : item))
    : [...list, toast];
  for (const dropped of next.slice(0, -MAX_TOASTS)) clearTimer(dropped.id);
  next = next.slice(-MAX_TOASTS);
  setToasts(next);

  clearTimer(toast.id);
  timers.set(
    toast.id,
    setTimeout(
      () => dismissToast(toast.id),
      options.durationMs ?? DURATION_MS[options.kind]
    )
  );
  return toast.id;
}

// Tests
export function clearToasts() {
  for (const timer of timers.values()) clearTimeout(timer);
  timers.clear();
  setToasts([]);
}

export function ToastHost() {
  return (
    <Floating>
      <div
        aria-live="polite"
        class="
          pointer-events-none fixed inset-x-0 bottom-0 z-40 mb-(--toast-offset,0px) flex flex-col
          items-center gap-sm p-lg
        "
      >
        <For each={toasts()}>
          {(toast) => (
            <div
              role={toast.kind === 'error' ? 'alert' : 'status'}
              onPointerUp={() => dismissToast(toast.id)}
              class="
                surface-app pointer-events-auto flex items-center gap-sm rounded-md border-2
                bg-surface px-lg py-md text-fg shadow-lg icon-sm
              "
              classList={{
                'border-success': toast.kind === 'success',
                'border-critical': toast.kind === 'error',
              }}
            >
              {toast.kind === 'error' ? (
                <ErrorRoundedIcon class="icon text-critical" />
              ) : (
                <CheckRoundedIcon class="icon text-success" />
              )}
              {t(toast.message, toast.args)}
            </div>
          )}
        </For>
      </div>
    </Floating>
  );
}
