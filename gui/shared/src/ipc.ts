import {
  OpenDialogOptions,
  OpenDialogReturnValue,
  SaveDialogOptions,
  SaveDialogReturnValue,
} from 'electron';
import {
  CrashReportKind,
  DiscordPresence,
  ErrorReportingState,
  GHGet,
  GHReturn,
  GuiLogFile,
  OSStats,
} from './interface';

export const IPC_CHANNELS = {
  GUI_LOG_EVENTS: 'gui-log-events',
  GUI_LOG_SUBSCRIBE: 'gui-log-subscribe',
  GUI_LOG_UNSUBSCRIBE: 'gui-log-unsubscribe',
  GUI_LOG_FILES: 'gui-log-files',
  READ_GUI_LOG_FILE: 'read-gui-log-file',
  CROWDIN_POPUP_CLOSED: 'crowdin-popup-closed',
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
  DISCORD_PRESENCE: 'discord-presence',
  IS_STEAM: 'is-steam',
  SET_KEYBIND_RECORDING: 'set-keybind-recording',
  SET_ERROR_REPORTING: 'set-error-reporting',
  SEND_CRASH_REPORT: 'send-crash-report',
} as const;

export interface IpcInvokeMap {
  [IPC_CHANNELS.OPEN_URL]: (url: string) => void;
  [IPC_CHANNELS.OS_STATS]: () => Promise<OSStats>;
  [IPC_CHANNELS.WINDOW_ACTIONS]: (
    action: 'close' | 'minimize' | 'toggle-maximize' | 'hide'
  ) => void;
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
  [IPC_CHANNELS.GET_FOLDER]: (folder: 'config' | 'logs' | 'exe') => string;
  [IPC_CHANNELS.GH_FETCH]: <T extends GHGet>(
    options: T
  ) => Promise<GHReturn[T['type']]>;
  [IPC_CHANNELS.DISCORD_PRESENCE]: (options: DiscordPresence) => void;
  [IPC_CHANNELS.IS_STEAM]: () => boolean;
  [IPC_CHANNELS.SET_KEYBIND_RECORDING]: (recording: boolean) => void;
  [IPC_CHANNELS.SET_ERROR_REPORTING]: (state: ErrorReportingState) => void;
  [IPC_CHANNELS.SEND_CRASH_REPORT]: (kind: CrashReportKind) => boolean;
  /** Starts sending this window GUI_LOG_EVENTS: the kept events, then new ones */
  [IPC_CHANNELS.GUI_LOG_SUBSCRIBE]: () => void;
  [IPC_CHANNELS.GUI_LOG_UNSUBSCRIBE]: () => void;
  [IPC_CHANNELS.GUI_LOG_FILES]: () => GuiLogFile[];
  [IPC_CHANNELS.READ_GUI_LOG_FILE]: (name: string) => Promise<Uint8Array | null>;
}
