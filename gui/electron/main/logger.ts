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

export const logger = pino(transport);
