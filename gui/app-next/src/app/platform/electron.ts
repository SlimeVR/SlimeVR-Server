import type { IElectronAPI } from '@slimevr/gui-shared';
import type { Platform } from '@/lib/platform';
import { saveWithBrowser } from './browser-save';

export async function createElectronPlatform(api: IElectronAPI): Promise<Platform> {
  const [os, settings, cache] = await Promise.all([
    api.osStats(),
    api.getStorage('settings'),
    api.getStorage('cache'),
  ]);

  return {
    kind: 'desktop',
    os: os.type,
    storage: (name) => (name === 'settings' ? settings : cache),
    openUrl: (url) => void api.openUrl(url),
    saveFile: saveWithBrowser,
    pickFile: async ({ mode, title, filters, defaultPath }) => {
      if (mode === 'save') {
        const res = await api.saveDialog({ title, filters, defaultPath });
        return res.canceled || !res.filePath ? null : res.filePath;
      }
      const res = await api.openDialog({
        title,
        filters,
        defaultPath,
        properties: [mode === 'directory' ? 'openDirectory' : 'openFile'],
      });
      return res.canceled ? null : (res.filePaths[0] ?? null);
    },
    window: {
      minimize: () => api.minimize(),
      toggleMaximize: () => api.toggleMaximize(),
      hide: () => api.hide(),
      close: () => api.close(),
      showDecorations: (on) => api.showDecorations(on),
    },
    folders: {
      openLogs: () => api.openLogsFolder(),
      openConfig: () => api.openConfigFolder(),
      installDir: () => api.getInstallDir(),
    },
    guiLogs: {
      onEvents: (cb) => api.onGuiLogEvents(cb),
      files: () => api.guiLogFiles(),
      read: (name) => api.readGuiLogFile(name),
    },
    log: (level, ...args) => api.log(level, ...args),
    errorReporting: {
      setState: (state) => api.setErrorReporting(state),
      sendCrash: (kind) => api.sendCrashReport(kind),
    },
    discordPresence: { set: (presence) => api.setPresence(presence) },
    translations: {
      set: (translations) => api.setTranslations(translations),
      override: () => api.i18nOverride(),
      onCrowdinPopupClosed: (cb) => api.onCrowdinPopupClosed(cb),
    },
    github: { get: (request) => api.ghGet(request) },
    steam: { isSteam: () => api.isSteam() },
    keybindRecording: (on) => api.setKeybindRecording(on),
  };
}
