import pino from 'pino';
import { getLogsFolder } from './paths';
import { join } from 'node:path';

const transport = pino.transport({
  targets: [
    {
      target: 'pino-roll',
      options: {
        file: join(getLogsFolder(), 'slimevr-gui.log'),
        frequency: 'daily',
        size: '10m',
        mkdir: true,
        limit: { count: 7 },
      },
      level: 'info',
    },
    {
      target: 'pino-pretty',
      options: { colorize: true },
      level: 'debug',
    },
  ],
});

type LogForwarder = (
  level: 'warn' | 'error' | 'fatal',
  message: string,
  error: Error | undefined
) => void;

let forwarder: LogForwarder | null = null;

export const setLogForwarder = (next: LogForwarder) => {
  forwarder = next;
};

const PINO_WARN = 40;
const PINO_ERROR = 50;
const PINO_FATAL = 60;

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

export const logger = pino(
  {
    hooks: {
      logMethod(args, method, level) {
        if (this.bindings().source !== 'renderer') forward(args, level);
        return method.apply(this, args);
      },
    },
  },
  transport
);

export const rendererLogger = logger.child({ source: 'renderer' });

export const closeLogger = () =>
  new Promise<void>((resolve) => {
    logger.flush(() => {
      transport.once('close', resolve);
      transport.end();
    });
  });
