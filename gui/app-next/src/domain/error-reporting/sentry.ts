import * as Sentry from '@sentry/solid';
import { redactText } from '@slimevr/gui-shared';
import { createEffect, createRoot } from 'solid-js';
import { ErrorReportingConsent } from 'solarxr-protocol';
import { observeSaves } from '@/domain/save/save-settings';
import { platform } from '@/lib/platform';
import { client } from '@/solarxr/client';
import { observeResources } from '@/solarxr/resource';
import {
  consent,
  identity,
  shouldHoldReports,
  shouldSendReports,
  type ReportingIdentity,
} from './consent';
import { reportError } from './report';
import { sink } from './sink';

const OFFLINE_DB_NAME = 'sentry-offline';
const OFFLINE_STORE_NAME = 'queue';
const HELD_LIMIT = 500;

const enabled =
  import.meta.env.MODE !== 'test' &&
  (import.meta.env.PROD || __SENTRY_RELEASE_FORCED__) &&
  !!__SENTRY_RELEASE__ &&
  !!__SENTRY_DSN__;

function platformTag() {
  return platform.kind === 'desktop' ? 'electron' : platform.kind;
}

const knownSources = new Map<string, readonly string[]>();
let knownValues: readonly string[] = [];

// Each source replaces its own list
export function setKnownValues(source: string, values: readonly string[]) {
  knownSources.set(source, values);
  knownValues = [...new Set([...knownSources.values()].flat())];
}

function redact(text: string) {
  return redactText(text, knownValues);
}

function initSentry() {
  Sentry.init({
    dsn: __SENTRY_DSN__,
    release: __SENTRY_RELEASE__,
    environment: __SENTRY_ENVIRONMENT__,
    integrations: (defaults) => [
      ...defaults.filter((integration) => integration.name !== 'BrowserSession'),
      Sentry.browserTracingIntegration(),
      Sentry.browserProfilingIntegration(),
      Sentry.consoleLoggingIntegration({ levels: ['warn', 'error'] }),
    ],
    beforeSend: (event) => {
      if (event.message) event.message = redact(event.message);
      for (const exception of event.exception?.values ?? []) {
        if (exception.value) exception.value = redact(exception.value);
      }
      return event;
    },
    beforeBreadcrumb: (crumb) => {
      if (crumb.message) crumb.message = redact(crumb.message);
      return crumb;
    },
    beforeSendLog: (log) => {
      log.message = redact(String(log.message));
      const sessionId = Sentry.getIsolationScope().getScopeData().tags.session_id;
      if (sessionId) log.attributes = { ...log.attributes, ['session_id']: sessionId };
      return log;
    },
    transport: (options) =>
      Sentry.makeBrowserOfflineTransport(Sentry.makeFetchTransport)({
        ...options,
        dbName: OFFLINE_DB_NAME,
        storeName: OFFLINE_STORE_NAME,
        maxQueueSize: HELD_LIMIT,
        flushAtStartup: true,
        shouldSend: shouldSendReports,
        shouldStore: shouldHoldReports,
      }),
    tracesSampleRate: 0.05,
    profileSessionSampleRate: 0.2,
    profileLifecycle: 'trace',
    replaysSessionSampleRate: 0.02,
    replaysOnErrorSampleRate: 1.0,
    normalizeDepth: 8,
  });
  Sentry.setTag('platform', platformTag());
  Sentry.setTag('os', platform.os);
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

function applyGate(next: ErrorReportingConsent) {
  console.info(`Error reporting consent: ${ErrorReportingConsent[next]}`);
  if (next === ErrorReportingConsent.ALLOWED) {
    startReplay();
    void Sentry.getClient()?.getTransport()?.flush();
    return;
  }
  Sentry.getReplay()?.stop();
  if (next === ErrorReportingConsent.DENIED) clearOfflineQueue();
}

function applyIdentity({ userId, sessionId }: ReportingIdentity) {
  Sentry.setUser({ id: userId });
  Sentry.setTag('session_id', sessionId);
  if (!Sentry.getIsolationScope().getSession()) {
    Sentry.startSession({ ignoreDuration: true, user: { id: userId } });
    Sentry.captureSession();
  }
}

let appliedConsent = ErrorReportingConsent.UNDECIDED;

function applyErrorReporting(
  next: ErrorReportingConsent,
  id: ReportingIdentity | null
) {
  platform.errorReporting?.setState({
    consent: next,
    userId: id?.userId ?? '',
    sessionId: id?.sessionId ?? '',
  });
  if (!Sentry.isInitialized()) return;
  if (id) applyIdentity(id);
  if (next === appliedConsent) return;
  appliedConsent = next;
  applyGate(next);
}

// Never datafeed payloads, they would flood the breadcrumb buffer
function watchLayers() {
  client.observe((event) => {
    switch (event.kind) {
      case 'status':
        sink().breadcrumb({
          category: 'solarxr',
          message: `connection ${event.status}`,
          data:
            event.closeCode === undefined ? undefined : { closeCode: event.closeCode },
        });
        return;
      case 'handler-error':
        reportError(event.error, { tags: { rpc: event.name } });
        return;
      default:
        sink().breadcrumb({
          category: 'solarxr.rpc',
          message: `${event.kind} ${event.name}`,
        });
    }
  });

  observeResources((event) => {
    if (event.kind === 'timeout') {
      sink().log('warn', `request for ${event.name} timed out`);
      return;
    }
    sink().breadcrumb({
      category: 'solarxr.resource',
      message: `${event.name} ${event.state}`,
    });
  });

  observeSaves((event) => {
    sink().breadcrumb({
      category: 'settings',
      message: `${event.outcome} ${event.keys.join(', ')}`,
      data: event.values,
    });
  });
}

export async function initErrorReporting() {
  if (enabled && !Sentry.isInitialized()) {
    initSentry();
    const steam = (await platform.steam?.isSteam()) ?? false;
    Sentry.setTag('distribution', steam ? 'steam' : 'standalone');
    console.info('Initialized the Sentry client, reports are held until consent');
  }
  watchLayers();
  createRoot(() => {
    createEffect(() => applyErrorReporting(consent(), identity()));
  });
}
