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
        await client.login();
      } catch (e) {
        logger.error(e, 'unable to connect to discord rpc');
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
          logger.error(e, 'unable to update rpc activity');
        });
    },
    destroy: () => {
      client.destroy();
      Object.assign(state, initialState());
    },
  };
};

export const discordPresence = richPresence();
