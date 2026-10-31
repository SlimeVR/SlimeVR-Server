import {
  OpenDialogOptions,
  OpenDialogReturnValue,
  SaveDialogOptions,
  SaveDialogReturnValue,
} from 'electron';
import { ErrorReportingConsent, LogLevel, LogSource } from 'solarxr-protocol';

export interface GuiLogEvent {
  source: LogSource;
  timeMs: number;
  level: LogLevel;
  logger: string;
  message: string;
  stackTrace: string | null;
}

export interface GuiLogFile {
  source: LogSource;
  name: string;
  size: number;
  modifiedMs: number;
  current: boolean;
}

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

export type DiscordPresence =
  | { enable: false }
  | { enable: true; activity: string; iconText: string | undefined };

/** Which crash screen the user sent a report from */
export type CrashReportKind = 'timeout' | 'connection_lost';

export interface ErrorReportingState {
  consent: ErrorReportingConsent;
  userId: string;
  sessionId: string;
}

export interface IElectronAPI {
  onGuiLogEvents: (
    cb: (events: GuiLogEvent[], backlog: boolean) => void
  ) => () => void;
  guiLogFiles: () => Promise<GuiLogFile[]>;
  readGuiLogFile: (name: string) => Promise<Uint8Array | null>;
  onCrowdinPopupClosed: (cb: () => void) => () => void;
  openUrl: (url: string) => Promise<void>;
  osStats: () => Promise<OSStats>;
  openLogsFolder: () => Promise<void>;
  openConfigFolder: () => Promise<void>;
  close: () => void;
  hide: () => void;
  minimize: () => void;
  toggleMaximize: () => void;
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
  isSteam: () => Promise<boolean>;
  setKeybindRecording: (recording: boolean) => void;
  setErrorReporting: (state: ErrorReportingState) => void;
  sendCrashReport: (kind: CrashReportKind) => Promise<boolean>;
}

declare global {
  interface Window {
    electronAPI: IElectronAPI;
  }
}
