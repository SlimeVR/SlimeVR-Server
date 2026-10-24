import { render } from 'solid-js/web';
import { App } from '@/app/app';
import { createPlatform } from '@/app/platform/detect';
import { initL10n } from '@/lib/l10n';
import { setPlatform } from '@/lib/platform';
import { loadPrefs } from '@/lib/storage';
import { createSocketClient, serverUrl, setClient } from '@/solarxr/client';
import './styles.css';

const root = document.getElementById('root');
if (!root) throw new Error('missing #root element');

setPlatform(await createPlatform());
await loadPrefs();
await initL10n();
setClient(createSocketClient({ url: serverUrl() }));

render(() => <App />, root);
