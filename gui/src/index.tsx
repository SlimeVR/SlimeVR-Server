import '@fontsource/poppins/500.css';
import '@fontsource/poppins/700.css';
import React from 'react';
import * as ReactDOMClient from 'react-dom/client';
import Modal from 'react-modal';
import App from './App';
import { AppLocalizationProvider } from './i18n/config';
import './index.scss';
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: 'https://e9ef9f8541352c50cff8600ba520d348@o4507810483535872.ingest.de.sentry.io/4507810579284048',
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.browserProfilingIntegration(),
    Sentry.replayIntegration({
      maskAllText: false,
      maskAllInputs: false,
      blockAllMedia: false,
    }),
  ],
  environment: import.meta.env.MODE,
  release:
    (__VERSION_TAG__ || __COMMIT_HASH__) + (__GIT_CLEAN__ ? '' : '-dirty'),
  // Tracing
  tracesSampleRate: import.meta.env.PROD ? 0.5 : 1.0, // Capture 10% of the transactions
  // Set 'tracePropagationTargets' to control for which URLs distributed tracing should be enabled
  tracePropagationTargets: ['localhost', /^https:\/\/yourserver\.io\/api/],
  // Set profilesSampleRate to 1.0 to profile every transaction.
  // Since profilesSampleRate is relative to tracesSampleRate,
  // the final profiling rate can be computed as tracesSampleRate * profilesSampleRate
  // For example, a tracesSampleRate of 0.5 and profilesSampleRate of 0.5 would
  // results in 25% of transactions being profiled (0.5*0.5=0.25)
  profilesSampleRate: 0.2,
  // Session Replay
  replaysSessionSampleRate: import.meta.env.PROD ? 0.1 : 1.0, // This sets the sample rate at 10%. You may want to change it to 100% while in development and then sample at a lower rate in production.
  replaysOnErrorSampleRate: 1.0, // If you're not already sampling the entire session, change the sample rate to 100% when sampling sessions where errors occur.
});

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
