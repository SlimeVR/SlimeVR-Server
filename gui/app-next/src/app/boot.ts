import { createPlatform } from '@/app/platform/detect';
import { startReleaseCheck } from '@/domain/app-update/release-check';
import { initErrorReporting } from '@/domain/error-reporting';
import { initSaveToasts } from '@/domain/settings/save-toasts';
import { initKeyboard } from '@/lib/keyboard';
import { initL10n } from '@/lib/l10n';
import { setPlatform } from '@/lib/platform';
import { loadPrefs } from '@/lib/storage';
import { createSocketClient, serverUrl, setClient } from '@/solarxr/client';

// Everything that runs before render, in order. Returns the stop for the
// background services.
export async function boot(): Promise<() => void> {
  setPlatform(await createPlatform());
  setClient(createSocketClient({ url: serverUrl() }));
  await initErrorReporting();
  await loadPrefs();
  await initL10n();

  const services = [initKeyboard(), initSaveToasts(), startReleaseCheck()];
  return () => {
    for (const stop of services) stop();
  };
}
