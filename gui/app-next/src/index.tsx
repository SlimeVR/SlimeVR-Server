import '@fontsource/jetbrains-mono/500.css';
import '@fontsource/poppins/500.css';
import '@fontsource/poppins/700.css';
import { render } from 'solid-js/web';
import { App } from '@/app/app';
import { boot } from '@/app/boot';
import './styles.css';

const root = document.getElementById('root');
if (!root) throw new Error('missing #root element');

await boot();

render(() => <App />, root);
