import * as ReactDOMClient from 'react-dom/client';
import "@fontsource/work-sans/variable.css";
import './index.css';
import App from './App';
import Modal from 'react-modal';

Modal.setAppElement('#root');

const container = document.getElementById('root');

if (container) {
  const root = ReactDOMClient.createRoot(container);
  root.render(
    <App />
  );
}
