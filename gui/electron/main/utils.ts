import os from 'os'
import { OSStats } from "../preload/interface";
import { ipcMain, IpcMainInvokeEvent } from 'electron';
import { IpcInvokeMap } from '../shared';

export const getPlatform = (): OSStats['type'] => {
  switch (os.platform()) {
    case 'darwin':
      return 'macos';
    case 'win32':
      return 'windows';
    case 'linux':
      return 'linux';
    default:
      return 'unknown';
  }
};


export function handleIpc<K extends keyof IpcInvokeMap>(
  channel: K,
  handler: (
    event: IpcMainInvokeEvent,
    ...args: Parameters<IpcInvokeMap[K]>
  ) => ReturnType<IpcInvokeMap[K]>
) {
  ipcMain.handle(channel, (event, ...args) => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return handler(event, ...args as any);
  });
}
