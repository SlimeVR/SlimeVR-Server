import { Client } from '@xhayper/discord-rpc';
import { logger } from './logger';

export const richPresence = () => {
  const initialState = () => ({ ready: false, start: Date.now() });

  const state = initialState();

  const client = new Client({
    clientId: '1237970689009647639',
    transport: { type: 'ipc' },
  });
  client.on('ready', () => {
    state.ready = true;
  });

  client.on('disconnected', () => {
    state.ready = false;
  });

  return {
    state,
    connect: async () => {
      try {
        logger.info('Logging into Discord RPC');
        await client.login();
      } catch (e) {
        logger.error(e, 'Unable to connect to Discord RPC');
      }
    },
    updateActivity: (content: string) => {
      if (!state.ready) return;
      client.user
        ?.setActivity({
          state: content,
          largeImageKey: 'icon',
          startTimestamp: state.start,
        })
        .catch((e) => {
          logger.error(e, 'Failed to update Discord RPC activity');
        });
    },
    destroy: () => {
      logger.info('Destroying Discord RPC');
      client.destroy();
      Object.assign(state, initialState());
    },
  };
};

export const discordPresence = richPresence();
