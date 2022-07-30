import * as ReactDOMClient from 'react-dom/client';
import '@fontsource/poppins/500.css';
import '@fontsource/poppins/700.css';
import './index.css';
import App from './App';
import Modal from 'react-modal';
import React from 'react';

Modal.setAppElement('#root');

const container = document.getElementById('root');

if (container) {
  const root = ReactDOMClient.createRoot(container);
  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
}
