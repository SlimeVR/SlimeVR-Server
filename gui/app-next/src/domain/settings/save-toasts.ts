import type { MessageId } from '@/lib/l10n';
import { showToast } from '@/ui/toast';
import { observeSaves } from './save-settings';

const SAVE_KEY = 'settings-save';
// Saving and saved share the width, so the swap does not shift the toast
const RESERVE: MessageId[] = ['toast-saving', 'toast-saved'];
// A server that answers within this never shows the saving state
export const SAVING_GRACE_MS = 300;

// One toast goes from saving (only when slow) to saved or failed. A refused
// save (never sent) gets its own
export function initSaveToasts(): () => void {
  let slow: ReturnType<typeof setTimeout> | undefined;

  function settle() {
    clearTimeout(slow);
    slow = undefined;
  }

  const stop = observeSaves((event) => {
    switch (event.outcome) {
      case 'pending':
        // A burst keeps the first timer
        slow ??= setTimeout(() => {
          slow = undefined;
          showToast({
            kind: 'pending',
            message: 'toast-saving',
            key: SAVE_KEY,
            reserve: RESERVE,
          });
        }, SAVING_GRACE_MS);
        return;
      case 'saved':
        settle();
        showToast({
          kind: 'success',
          message: 'toast-saved',
          key: SAVE_KEY,
          reserve: RESERVE,
        });
        return;
      case 'not-saved':
        settle();
        showToast({
          kind: 'error',
          message: 'toast-save-failed',
          key: SAVE_KEY,
          reserve: RESERVE,
        });
        return;
      case 'refused':
        showToast({ kind: 'error', message: 'toast-not-saved' });
    }
  });

  return () => {
    settle();
    stop();
  };
}
