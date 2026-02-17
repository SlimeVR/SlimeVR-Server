import os from 'os'
import { OSStats } from "../preload/interface";
import { ipcMain, IpcMainInvokeEvent } from 'electron';
import { IpcInvokeMap } from '../shared';
import net from 'net'

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

export const isPortAvailable = (port: number) => {
  return new Promise((resolve) => {
        const s = net.createServer();
        s.once('error', (err) => {
            s.close();
            if ("code" in err && err["code"] == "EADDRINUSE") {
                resolve(false);
            } else {
                resolve(false);
            }
        });
        s.once('listening', () => {
            resolve(true);
            s.close();
        });
        s.listen(port);
    });
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
