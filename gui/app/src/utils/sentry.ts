import * as Sentry from '@sentry/react';
import { log } from './logging';
import { useEffect } from 'react';
import {
  createRoutesFromChildren,
  matchRoutes,
  useLocation,
  useNavigationType,
} from 'react-router-dom';
import { ErrorReportingConsent } from 'solarxr-protocol';
import { ErrorReportingState } from '@slimevr/gui-shared';

const OFFLINE_DB_NAME = 'sentry-offline';
const OFFLINE_STORE_NAME = 'queue';
const HELD_LIMIT = 500;

const release = __SENTRY_RELEASE__;
const isEnabled =
  (import.meta.env.PROD || __SENTRY_RELEASE_FORCED__) && !!release && !!__SENTRY_DSN__;

function platformName() {
  if (window.electronAPI) return 'electron';
  if (window.__ANDROID__?.isThere()) return 'android';
  return 'web';
}

export function initSentry(isSteam: boolean, getConsent: () => ErrorReportingConsent) {
  if (!isEnabled || Sentry.isInitialized()) return;

  Sentry.init({
    dsn: __SENTRY_DSN__,
    release,
    environment: __SENTRY_ENVIRONMENT__,
    integrations: (defaults) => [
      ...defaults.filter((integration) => integration.name !== 'BrowserSession'),
      Sentry.reactRouterV6BrowserTracingIntegration({
        useEffect,
        useLocation,
        useNavigationType,
        createRoutesFromChildren,
        matchRoutes,
      }),
      Sentry.browserProfilingIntegration(),
      Sentry.consoleLoggingIntegration({ levels: ['warn', 'error'] }),
    ],
    enableLogs: true,
    beforeSendLog: (log) => {
      const sessionId = Sentry.getIsolationScope().getScopeData().tags.session_id;
      if (sessionId) log.attributes = { ...log.attributes, ['session_id']: sessionId };
      return log;
    },
    // The offline queue caches everything until the user answered
    transport: (options) =>
      Sentry.makeBrowserOfflineTransport(Sentry.makeFetchTransport)({
        ...options,
        dbName: OFFLINE_DB_NAME,
        storeName: OFFLINE_STORE_NAME,
        maxQueueSize: HELD_LIMIT,
        flushAtStartup: true,
        shouldSend: () => getConsent() === ErrorReportingConsent.ALLOWED,
        shouldStore: () => getConsent() !== ErrorReportingConsent.DENIED,
      }),
    tracesSampleRate: 0.05,
    // Relative to tracesSampleRate
    profilesSampleRate: 0.2,
    replaysSessionSampleRate: 0.02,
    replaysOnErrorSampleRate: 1.0,
    normalizeDepth: 8,
  });
  Sentry.setTag('platform', platformName());
  Sentry.setTag('distribution', isSteam ? 'steam' : 'standalone');

  log('Initialized the Sentry client, reports are held until consent');
}

function clearOfflineQueue() {
  const request = indexedDB.open(OFFLINE_DB_NAME);
  request.onupgradeneeded = () => request.transaction?.abort();
  request.onsuccess = () => {
    const db = request.result;
    if (!db.objectStoreNames.contains(OFFLINE_STORE_NAME)) {
      db.close();
      return;
    }
    const tx = db.transaction(OFFLINE_STORE_NAME, 'readwrite');
    tx.objectStore(OFFLINE_STORE_NAME).clear();
    tx.oncomplete = () => db.close();
  };
}

function startReplay() {
  const replay = Sentry.getReplay();
  if (replay) {
    replay.startBuffering();
    return;
  }
  Sentry.addIntegration(
    Sentry.replayIntegration({
      maskAllText: false,
      maskAllInputs: true,
      blockAllMedia: false,
    })
  );
}

export function applyErrorReporting(server: ErrorReportingState) {
  window.electronAPI?.setErrorReporting(server);

  if (!isEnabled) return;
  Sentry.setUser({ id: server.userId });
  Sentry.setTag('session_id', server.sessionId);
  if (!Sentry.getIsolationScope().getSession()) {
    Sentry.startSession({ ignoreDuration: true, user: { id: server.userId } });
    Sentry.captureSession();
  }
}

/** Call when the consent changes */
export function applyErrorReportingConsent(consent: ErrorReportingConsent) {
  if (!isEnabled) return;
  log(`Error reporting consent: ${ErrorReportingConsent[consent]}`);

  if (consent === ErrorReportingConsent.ALLOWED) {
    startReplay();
    // No timeout: sends what the offline queue held
    Sentry.getClient()?.getTransport()?.flush();
  } else {
    Sentry.getReplay()?.stop();
    if (consent === ErrorReportingConsent.DENIED) clearOfflineQueue();
  }
}

export function track(name: string, attributes?: Record<string, unknown>) {
  Sentry.metrics.count(name, 1, attributes ? { attributes } : undefined);
}

const trackedThisSession = new Set<string>();

export function trackOncePerSession(
  name: string,
  attributes?: Record<string, unknown>
) {
  const key = `${name}:${JSON.stringify(attributes ?? {})}`;
  if (trackedThisSession.has(key)) return;
  trackedThisSession.add(key);
  track(name, attributes);
}
