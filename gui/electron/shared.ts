import {
  OpenDialogOptions,
  OpenDialogReturnValue,
  SaveDialogOptions,
  SaveDialogReturnValue,
} from 'electron';
import { DiscordPresence, GHGet, GHReturn, OSStats, ServerStatusEvent } from './preload/interface';

export const IPC_CHANNELS = {
  SERVER_STATUS: 'server-status',
  OPEN_URL: 'open-url',
  OS_STATS: 'os-stats',
  WINDOW_ACTIONS: 'window-actions',
  LOG: 'log',
  STORAGE: 'storage',
  OPEN_DIALOG: 'open-dialog',
  SAVE_DIALOG: 'save-dialog',
  I18N_OVERRIDE: 'i18n-override',
  OPEN_FILE: 'open-file',
  GET_FOLDER: 'get-folder',
  GH_FETCH: 'gh-fetch',
  DISCORD_PRESENCE: 'discord-presence'
} as const;

export interface IpcInvokeMap {
  [IPC_CHANNELS.OPEN_URL]: (url: string) => void;
  [IPC_CHANNELS.OS_STATS]: () => Promise<OSStats>;
  [IPC_CHANNELS.WINDOW_ACTIONS]: (action: 'close' | 'minimize' | 'maximize') => void;
  [IPC_CHANNELS.LOG]: (type: 'info' | 'error' | 'warn', ...args: unknown[]) => void;
  [IPC_CHANNELS.OPEN_DIALOG]: (
    options: OpenDialogOptions
  ) => Promise<OpenDialogReturnValue>;
  [IPC_CHANNELS.SAVE_DIALOG]: (
    options: SaveDialogOptions
  ) => Promise<SaveDialogReturnValue>;
  [IPC_CHANNELS.I18N_OVERRIDE]: () => Promise<string | false>;
  [IPC_CHANNELS.STORAGE]: (args: {
    type: 'settings' | 'cache';
    method: 'get' | 'set' | 'delete' | 'save';
    key?: string;
    value?: unknown;
  }) => Promise<unknown>;
  [IPC_CHANNELS.OPEN_FILE]: (path: string) => void;
  [IPC_CHANNELS.GET_FOLDER]: (folder: 'config' | 'logs') => string;
  [IPC_CHANNELS.GH_FETCH]: <T extends GHGet>(
    options: T
  ) => Promise<GHReturn[T['type']]>;
  [IPC_CHANNELS.DISCORD_PRESENCE]: (options: DiscordPresence) => void;
}

/**
 * Mapping for Events (Main -> Renderer)
 */
export interface IpcEventMap {
  [IPC_CHANNELS.SERVER_STATUS]: ServerStatusEvent;
}
