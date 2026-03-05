import {
  OpenDialogOptions,
  OpenDialogReturnValue,
  SaveDialogOptions,
  SaveDialogReturnValue,
} from 'electron';

export type ServerStatusEvent = {
  type: 'stdout' | 'stderr' | 'error' | 'terminated' | 'other';
  message: string;
};

export type OSStats = {
  type: 'linux' | 'windows' | 'macos' | 'unknown';
};

export interface CrossStorage {
  set(key: string, value: unknown): Promise<void>;
  get<T>(key: string): Promise<T | undefined>;
  delete(key: string): Promise<boolean>;
  save(): Promise<boolean>;
}

export type GHGet = { type: 'fw-releases' } | { type: 'asset'; url: string };
export type GHReturn = {
  asset: [number, string][] | null;
  ['fw-releases']:
    | {
        assets: { browser_download_url: string; name: string; digest: string }[];
        prerelease: boolean;
        tag_name: string;
        body: string;
      }[]
    | null;
};

export type DiscordPresence = { enable: false } | { enable: true, activity: string }

export interface IElectronAPI {
  onServerStatus: (cb: (data: ServerStatusEvent) => void) => () => void;
  openUrl: (url: string) => Promise<void>;
  osStats: () => Promise<OSStats>;
  openLogsFolder: () => Promise<void>;
  openConfigFolder: () => Promise<void>;
  close: () => void;
  minimize: () => void;
  maximize: () => void;
  showDecorations: (decorations: boolean) => void;
  setTranslations: (translations: Record<string, string>) => void;
  i18nOverride: () => Promise<string | false>;
  getStorage: (type: 'settings' | 'cache') => Promise<CrossStorage>;
  openDialog: (options: OpenDialogOptions) => Promise<OpenDialogReturnValue>;
  saveDialog: (options: SaveDialogOptions) => Promise<SaveDialogReturnValue>;
  log: (type: 'info' | 'error' | 'warn', ...args: unknown[]) => void;
  openFile: (path: string) => void;
  ghGet: <T extends GHGet>(options: T) => Promise<GHReturn[T['type']]>;
  setPresence: (options: DiscordPresence) => void;
  getInstallDir: () => Promise<string>;
}

declare global {
  interface Window {
    electronAPI: IElectronAPI;
  }
}
