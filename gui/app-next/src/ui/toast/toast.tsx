import { createSignal, For, untrack } from 'solid-js';
import { Dynamic } from 'solid-js/web';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { Floating } from '@/ui/floating';
import CheckRoundedIcon from '~icons/material-symbols/check-rounded';
import ErrorRoundedIcon from '~icons/material-symbols/error-rounded';
import ProgressIcon from '~icons/material-symbols/progress-activity';

// pending stays until a toast with the same key replaces it
export type ToastKind = 'pending' | 'success' | 'error';

export interface ToastOptions {
  kind: ToastKind;
  message: MessageId;
  args?: MessageArgs;
  // A toast with the same key is replaced and its timer restarts
  key?: string;
  durationMs?: number;
  // Texts the toast keeps room for, so replacing it by key does not resize it
  reserve?: readonly MessageId[];
}

export interface Toast {
  id: number;
  kind: ToastKind;
  message: MessageId;
  args?: MessageArgs;
  key?: string;
  reserve?: readonly MessageId[];
}

const MAX_TOASTS = 3;
const DURATION_MS: Record<ToastKind, number | null> = {
  pending: null,
  success: 2000,
  error: 5000,
};

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
    existing?.kind === options.kind &&
    existing.message === options.message &&
    existing.args === options.args;
  const toast: Toast =
    existing && same
      ? existing
      : {
          id: existing?.id ?? nextId++,
          kind: options.kind,
          message: options.message,
          args: options.args,
          key: options.key,
          reserve: options.reserve,
        };

  let next = existing
    ? list.map((item) => (item === existing ? toast : item))
    : [...list, toast];
  for (const dropped of next.slice(0, -MAX_TOASTS)) clearTimer(dropped.id);
  next = next.slice(-MAX_TOASTS);
  setToasts(next);

  clearTimer(toast.id);
  const duration = options.durationMs ?? DURATION_MS[options.kind];
  if (duration !== null)
    timers.set(
      toast.id,
      setTimeout(() => dismissToast(toast.id), duration)
    );
  return toast.id;
}

// Tests
export function clearToasts() {
  for (const timer of timers.values()) clearTimeout(timer);
  timers.clear();
  setToasts([]);
}

const BORDER: Record<ToastKind, string> = {
  pending: 'border-line',
  success: 'border-success',
  error: 'border-critical',
};

const ICON: Record<ToastKind, typeof CheckRoundedIcon> = {
  pending: ProgressIcon,
  success: CheckRoundedIcon,
  error: ErrorRoundedIcon,
};

const ICON_CLASS: Record<ToastKind, string> = {
  pending: 'icon animate-spin text-fg-muted',
  success: 'icon text-success',
  error: 'icon text-critical',
};

export function ToastHost() {
  return (
    <Floating>
      <div
        aria-live="polite"
        class="
          pointer-events-none fixed top-0 right-4 z-40 mt-titlebar flex max-w-full flex-col
          items-end gap-sm p-md
        "
      >
        <For each={toasts()}>
          {(toast) => (
            <div
              role={toast.kind === 'error' ? 'alert' : 'status'}
              onPointerUp={() => dismissToast(toast.id)}
              class={cx(
                `
                  surface-app pointer-events-auto flex items-center gap-sm rounded-md border-2
                  bg-surface px-lg py-md text-fg shadow-lg icon-sm
                `,
                BORDER[toast.kind]
              )}
            >
              <Dynamic
                component={ICON[toast.kind]}
                class={ICON_CLASS[toast.kind]}
              />
              <span class="grid">
                <span class="col-start-1 row-start-1">
                  {t(toast.message, toast.args)}
                </span>
                <For each={toast.reserve}>
                  {(id) => (
                    <span
                      aria-hidden="true"
                      class="invisible col-start-1 row-start-1"
                    >
                      {t(id)}
                    </span>
                  )}
                </For>
              </span>
            </div>
          )}
        </For>
      </div>
    </Floating>
  );
}
