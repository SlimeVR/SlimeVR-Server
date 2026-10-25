import { showToast } from '@/ui/toast';
import { observeSaves } from './save-settings';

const SAVED_KEY = 'settings-saved';

// Successful saves share one toast, every refused save gets its own
export function initSaveToasts(): () => void {
  return observeSaves((event) => {
    if (event.outcome === 'saved') {
      showToast({ kind: 'success', message: 'toast-saved', key: SAVED_KEY });
      return;
    }
    showToast({ kind: 'error', message: 'toast-not-saved' });
  });
}
