import '@fontsource/poppins/500.css';
import '@fontsource/poppins/700.css';
import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import Modal from 'react-modal';
import App from './App';
import { AppLocalizationProvider } from './i18n/config';
import './index.scss';

Modal.setAppElement('#root');

const container = document.getElementById('root');

if (container) {
  const root = ReactDOMClient.createRoot(container);
  root.render(
    <React.StrictMode>
      <AppLocalizationProvider>
        <App />
      </AppLocalizationProvider>
    </React.StrictMode>
  );
}
