import { render } from 'solid-js/web';
import { App } from '@/app/app';
import './styles.css';

const root = document.getElementById('root');
if (!root) throw new Error('missing #root element');

render(() => <App />, root);
