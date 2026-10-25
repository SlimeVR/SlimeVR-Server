import '@fontsource/jetbrains-mono/500.css';
import '@fontsource/poppins/500.css';
import '@fontsource/poppins/700.css';
import { render } from 'solid-js/web';
import { App } from '@/app/app';
import { createPlatform } from '@/app/platform/detect';
import { initErrorReporting } from '@/domain/error-reporting';
import { initSaveToasts } from '@/domain/settings/save-toasts';
import { initKeyboard } from '@/lib/keyboard';
import { initL10n } from '@/lib/l10n';
import { setPlatform } from '@/lib/platform';
import { loadPrefs } from '@/lib/storage';
import { createSocketClient, serverUrl, setClient } from '@/solarxr/client';
import './styles.css';

const root = document.getElementById('root');
if (!root) throw new Error('missing #root element');

setPlatform(await createPlatform());
setClient(createSocketClient({ url: serverUrl() }));
await initErrorReporting();
await loadPrefs();
await initL10n();
initKeyboard();
initSaveToasts();

render(() => <App />, root);
