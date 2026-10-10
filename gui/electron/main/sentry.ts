import * as Sentry from '@sentry/electron/main';
import {
  makeMultiplexedTransport,
  parseEnvelope,
  serializeEnvelope,
} from '@sentry/core';
import type { Envelope, Transport } from '@sentry/core';
import { app } from 'electron';
import { join } from 'node:path';
import { mkdir, readdir, readFile, rm, unlink, writeFile } from 'node:fs/promises';
import { randomUUID } from 'node:crypto';
import { ErrorReportingState } from '@slimevr/gui-shared';
import { ErrorReportingConsent } from 'solarxr-protocol';
import { getGuiDataFolder } from './paths';
import { logger, setLogForwarder } from './logger';

const HELD_LIMIT = 500;
const SERVER_ROUTE_TAG = 'route';

type OfflineTransportOptions = Parameters<
  ReturnType<typeof Sentry.makeElectronOfflineTransport>
>[0];

/** Caches everything on disk until the user answered */
function createConsentGate() {
  const folder = join(getGuiDataFolder(), 'sentry-held');
  let consent = ErrorReportingConsent.UNDECIDED;
  let inner: Transport | null = null;

  async function hold(envelope: Envelope) {
    await mkdir(folder, { recursive: true });
    await writeFile(
      join(folder, `${Date.now()}-${randomUUID()}.envelope`),
      serializeEnvelope(envelope)
    );
    const files = (await readdir(folder)).sort();
    await Promise.all(
      files
        .slice(0, Math.max(0, files.length - HELD_LIMIT))
        .map((file) => unlink(join(folder, file)))
    );
  }

  async function sendHeld(transport: Transport) {
    const files = await readdir(folder).catch(() => [] as string[]);
    for (const file of files.sort()) {
      const path = join(folder, file);
      try {
        await transport.send(parseEnvelope(await readFile(path)));
      } catch (e) {
        logger.warn({ err: e }, 'Failed to send a held error report');
      }
      await unlink(path).catch(() => undefined);
    }
  }

  return {
    wrap(transport: Transport): Transport {
      inner = transport;
      return {
        send: async (envelope) => {
          if (consent === ErrorReportingConsent.ALLOWED)
            return transport.send(envelope);
          if (consent === ErrorReportingConsent.UNDECIDED) await hold(envelope);
          return {};
        },
        flush: (timeout) => transport.flush(timeout),
      };
    },
    apply(next: ErrorReportingConsent) {
      if (consent === next) return;
      consent = next;
      if (next === ErrorReportingConsent.ALLOWED && inner) {
        sendHeld(inner).catch((e) =>
          logger.warn({ err: e }, 'Failed to send held error reports')
        );
      } else if (next === ErrorReportingConsent.DENIED) {
        rm(folder, { recursive: true, force: true }).catch(() => undefined);
      }
    },
  };
}

const consentGate = createConsentGate();

const routedTransport = makeMultiplexedTransport(
  Sentry.makeElectronOfflineTransport(),
  ({ getEvent }) => {
    const toServer = getEvent()?.tags?.[SERVER_ROUTE_TAG] === 'server';
    return toServer && __SENTRY_SERVER_DSN__ ? [__SENTRY_SERVER_DSN__] : [];
  }
);

export function initSentry(isSteam: boolean) {
  const version = app.getVersion();
  const release =
    __SENTRY_RELEASE__ || (app.isPackaged && version !== '0.0.0' ? `v${version}` : '');
  if (!release || !__SENTRY_ELECTRON_DSN__) return;

  Sentry.init({
    dsn: __SENTRY_ELECTRON_DSN__,
    release,
    environment: __SENTRY_ENVIRONMENT__,
    // The renderer reports on its own
    ipcMode: Sentry.IPCMode.Classic,
    integrations: (defaults) => defaults.filter((i) => i.name !== 'PreloadInjection'),
    transport: ((options: OfflineTransportOptions) =>
      consentGate.wrap(
        routedTransport(options)
      )) as Sentry.ElectronMainOptions['transport'],
    sendDefaultPii: false,
    enableLogs: true,
    beforeSendLog: (log) => {
      const sessionId = Sentry.getIsolationScope().getScopeData().tags.session_id;
      if (sessionId) log.attributes = { ...log.attributes, ['session_id']: sessionId };
      return log;
    },
  });
  Sentry.setTag('platform', 'electron-main');
  Sentry.setTag('distribution', isSteam ? 'steam' : 'standalone');

  setLogForwarder((level, message, error) => {
    Sentry.logger[level](
      message,
      error ? { error: error.stack ?? error.message } : undefined
    );
  });
  logger.info('Initialized Sentry, reports are held until consent');
}

export function applyErrorReporting(state: ErrorReportingState) {
  consentGate.apply(state.consent);
  if (!Sentry.isInitialized()) return;
  Sentry.setUser({ id: state.userId });
  Sentry.setTag('session_id', state.sessionId);
}

const serverScope = { tags: { [SERVER_ROUTE_TAG]: 'server' } };

export function captureServerExit(
  code: number | null,
  signal: string | null,
  outputTail: string
) {
  Sentry.captureMessage('server_exit_nonzero', {
    ...serverScope,
    level: 'error',
    extra: { code, signal, outputTail },
  });
}

export function captureServerLaunchError(err: Error) {
  Sentry.captureException(err, serverScope);
}

export function captureJavaNotFound() {
  Sentry.captureMessage('java_not_found', { ...serverScope, level: 'error' });
}
