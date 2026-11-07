import { contextBridge, ipcRenderer, IpcRendererEvent } from 'electron';
import { GuiLogEvent, IElectronAPI } from '@slimevr/gui-shared';
import { IPC_CHANNELS } from '@slimevr/gui-shared';

contextBridge.exposeInMainWorld('electronAPI', {
  onGuiLogEvents: (callback) => {
    const subscription = (
      _event: IpcRendererEvent,
      events: GuiLogEvent[],
      backlog: boolean
    ) => callback(events, backlog);
    ipcRenderer.on(IPC_CHANNELS.GUI_LOG_EVENTS, subscription);
    ipcRenderer.invoke(IPC_CHANNELS.GUI_LOG_SUBSCRIBE);
    return () => {
      ipcRenderer.removeListener(IPC_CHANNELS.GUI_LOG_EVENTS, subscription);
      ipcRenderer.invoke(IPC_CHANNELS.GUI_LOG_UNSUBSCRIBE);
    };
  },
  guiLogFiles: () => ipcRenderer.invoke(IPC_CHANNELS.GUI_LOG_FILES),
  readGuiLogFile: (name) => ipcRenderer.invoke(IPC_CHANNELS.READ_GUI_LOG_FILE, name),
  onCrowdinPopupClosed: (callback) => {
    const subscription = () => callback();
    ipcRenderer.on(IPC_CHANNELS.CROWDIN_POPUP_CLOSED, subscription);
    return () =>
      ipcRenderer.removeListener(IPC_CHANNELS.CROWDIN_POPUP_CLOSED, subscription);
  },
  openUrl: (url) => ipcRenderer.invoke(IPC_CHANNELS.OPEN_URL, url),
  osStats: () => ipcRenderer.invoke(IPC_CHANNELS.OS_STATS),
  close: () => ipcRenderer.invoke(IPC_CHANNELS.WINDOW_ACTIONS, 'close'),
  hide: () => ipcRenderer.invoke(IPC_CHANNELS.WINDOW_ACTIONS, 'hide'),
  minimize: () => ipcRenderer.invoke(IPC_CHANNELS.WINDOW_ACTIONS, 'minimize'),
  toggleMaximize: () =>
    ipcRenderer.invoke(IPC_CHANNELS.WINDOW_ACTIONS, 'toggle-maximize'),
  getStorage: async (type) => {
    return {
      get: (key) =>
        ipcRenderer.invoke(IPC_CHANNELS.STORAGE, { type, method: 'get', key }),
      set: (key, value) =>
        ipcRenderer.invoke(IPC_CHANNELS.STORAGE, { type, method: 'set', key, value }),
      delete: (key) =>
        ipcRenderer.invoke(IPC_CHANNELS.STORAGE, { type, method: 'delete', key }),
      save: () => ipcRenderer.invoke(IPC_CHANNELS.STORAGE, { type, method: 'save' }),
    };
  },
  log: (type, ...args) => ipcRenderer.invoke(IPC_CHANNELS.LOG, type, ...args),
  i18nOverride: async () => ipcRenderer.invoke(IPC_CHANNELS.I18N_OVERRIDE),
  showDecorations: () => {},
  setTranslations: () => {},
  openDialog: (options) => ipcRenderer.invoke(IPC_CHANNELS.OPEN_DIALOG, options),
  saveDialog: (options) => ipcRenderer.invoke(IPC_CHANNELS.SAVE_DIALOG, options),
  openConfigFolder: async () =>
    ipcRenderer.invoke(
      IPC_CHANNELS.OPEN_FILE,
      await ipcRenderer.invoke(IPC_CHANNELS.GET_FOLDER, 'config')
    ),
  openLogsFolder: async () =>
    ipcRenderer.invoke(
      IPC_CHANNELS.OPEN_FILE,
      await ipcRenderer.invoke(IPC_CHANNELS.GET_FOLDER, 'logs')
    ),
  openFile: (path) => ipcRenderer.invoke(IPC_CHANNELS.OPEN_FILE, path),
  ghGet: (req) => ipcRenderer.invoke(IPC_CHANNELS.GH_FETCH, req),
  setPresence: (options) => ipcRenderer.invoke(IPC_CHANNELS.DISCORD_PRESENCE, options),
  getInstallDir: () => ipcRenderer.invoke(IPC_CHANNELS.GET_FOLDER, 'exe'),
  isSteam: () => ipcRenderer.invoke(IPC_CHANNELS.IS_STEAM),
  setKeybindRecording: (recording) =>
    ipcRenderer.invoke(IPC_CHANNELS.SET_KEYBIND_RECORDING, recording),
  setErrorReporting: (state) =>
    ipcRenderer.invoke(IPC_CHANNELS.SET_ERROR_REPORTING, state),
  sendCrashReport: (kind) => ipcRenderer.invoke(IPC_CHANNELS.SEND_CRASH_REPORT, kind),
} satisfies IElectronAPI);
