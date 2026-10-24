import { render } from 'solid-js/web';
import { App } from '@/app/app';
import { createPlatform } from '@/app/platform/detect';
import { setPlatform } from '@/lib/platform';
import { loadPrefs } from '@/lib/storage';
import './styles.css';

const root = document.getElementById('root');
if (!root) throw new Error('missing #root element');

setPlatform(await createPlatform());
await loadPrefs();

render(() => <App />, root);
