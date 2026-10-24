import pino from 'pino';
import pretty from 'pino-pretty';
import { GuiLogEvent } from '@slimevr/gui-shared';
import { LogLevel, LogSource } from 'solarxr-protocol';
import { getLogsFolder } from './paths';
import { createLogFileWriter } from './log-files';

const BACKLOG_SIZE = 2000;
const PUSH_WINDOW_MS = 100;

type LogForwarder = (
  level: 'warn' | 'error' | 'fatal',
  message: string,
  error: Error | undefined
) => void;

let forwarder: LogForwarder | null = null;

export const setLogForwarder = (next: LogForwarder) => {
  forwarder = next;
};

const PINO_DEBUG = 20;
const PINO_INFO = 30;
const PINO_WARN = 40;
const PINO_ERROR = 50;
const PINO_FATAL = 60;

function toLogLevel(level: number) {
  if (level >= PINO_FATAL) return LogLevel.FATAL;
  if (level >= PINO_ERROR) return LogLevel.ERROR;
  if (level >= PINO_WARN) return LogLevel.WARN;
  if (level >= PINO_INFO) return LogLevel.INFO;
  if (level >= PINO_DEBUG) return LogLevel.DEBUG;
  return LogLevel.TRACE;
}

function forward(args: unknown[], level: number) {
  if (!forwarder || level < PINO_WARN) return;
  const first = args[0];
  const message = args.find((arg) => typeof arg === 'string') as string | undefined;
  const error =
    first instanceof Error
      ? first
      : first &&
          typeof first === 'object' &&
          (first as { err?: unknown }).err instanceof Error
        ? (first as { err: Error }).err
        : undefined;
  const name = level >= PINO_FATAL ? 'fatal' : level >= PINO_ERROR ? 'error' : 'warn';
  forwarder(name, message ?? error?.message ?? '', error);
}

const backlog: GuiLogEvent[] = [];
const listeners = new Set<(events: GuiLogEvent[], backlog: boolean) => void>();
let pending: GuiLogEvent[] = [];
let pushTimer: NodeJS.Timeout | null = null;

function pushPending() {
  pushTimer = null;
  const events = pending;
  pending = [];
  listeners.forEach((listener) => listener(events, false));
}

/** Turns pino's JSON lines into events for the logs page. */
const liveStream = {
  write(line: string) {
    let entry: Record<string, unknown>;
    try {
      entry = JSON.parse(line);
    } catch {
      return;
    }
    const err = entry.err as { stack?: string } | undefined;
    const event: GuiLogEvent = {
      source: LogSource.GUI,
      timeMs: typeof entry.time === 'number' ? entry.time : Date.now(),
      level: toLogLevel(typeof entry.level === 'number' ? entry.level : PINO_INFO),
      logger: typeof entry.source === 'string' ? entry.source : 'main',
      message: typeof entry.msg === 'string' ? entry.msg : '',
      stackTrace: err?.stack ?? null,
    };

    backlog.push(event);
    if (backlog.length > BACKLOG_SIZE) backlog.shift();

    if (listeners.size === 0) return;
    pending.push(event);
    pushTimer ??= setTimeout(pushPending, PUSH_WINDOW_MS);
  },
};

/**
 * Calls `listener` with the kept events right away, then with batches of new ones.
 * Returns the unsubscribe function.
 */
export function subscribeGuiLogs(
  listener: (events: GuiLogEvent[], backlog: boolean) => void
) {
  // Events still waiting for the next batch go out with that batch, not twice
  listener(backlog.slice(0, backlog.length - pending.length), true);
  listeners.add(listener);
  return () => {
    listeners.delete(listener);
  };
}

export const logFileWriter = createLogFileWriter(getLogsFolder());

export const logger = pino(
  {
    level: 'debug',
    hooks: {
      logMethod(args, method, level) {
        if (this.bindings().source !== 'renderer') forward(args, level);
        return method.apply(this, args);
      },
    },
  },
  pino.multistream([
    { level: 'info', stream: logFileWriter },
    { level: 'info', stream: liveStream },
    { level: 'debug', stream: pretty({ colorize: true }) },
  ])
);

export const rendererLogger = logger.child({ source: 'renderer' });

export const closeLogger = () =>
  new Promise<void>((resolve) => {
    logger.flush(() => {
      logFileWriter.close();
      resolve();
    });
  });
