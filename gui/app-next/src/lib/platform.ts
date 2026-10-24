import type {
  CrashReportKind,
  CrossStorage,
  DiscordPresence,
  ErrorReportingState,
  GHGet,
  GHReturn,
  GuiLogEvent,
  GuiLogFile,
} from '@slimevr/gui-shared';

export type PlatformKind = 'desktop' | 'android' | 'web';
export type PlatformOs = 'windows' | 'linux' | 'macos' | 'android' | 'unknown';
export type StorageName = 'settings' | 'cache';
export type LogLevel = 'info' | 'error' | 'warn';
export type CrashKind = CrashReportKind;

export interface SaveOptions {
  blob: Blob;
  fileName: string;
  extensions: string[];
}
export type SaveResult = 'saved' | 'canceled';

export interface FileFilter {
  name: string;
  extensions: string[];
}

export interface PickOptions {
  mode: 'open' | 'directory' | 'save';
  title?: string;
  filters?: FileFilter[];
  defaultPath?: string;
}

export interface WindowApi {
  minimize(): void;
  toggleMaximize(): void;
  hide(): void;
  close(): void;
  showDecorations(on: boolean): void;
}

export interface FoldersApi {
  openLogs(): Promise<void>;
  openConfig(): Promise<void>;
  installDir(): Promise<string>;
}

export interface GuiLogsApi {
  onEvents(cb: (events: GuiLogEvent[], backlog: boolean) => void): () => void;
  files(): Promise<GuiLogFile[]>;
  read(name: string): Promise<Uint8Array | null>;
}

export interface ErrorReportingApi {
  setState(state: ErrorReportingState): void;
  sendCrash(kind: CrashKind): Promise<boolean>;
}

export interface PresenceApi {
  set(presence: DiscordPresence): void;
}

export interface TranslationsApi {
  set(translations: Record<string, string>): void;
  override(): Promise<string | false>;
  onCrowdinPopupClosed(cb: () => void): () => void;
}

export interface GithubApi {
  get<T extends GHGet>(request: T): Promise<GHReturn[T['type']]>;
}

export interface SystemInsetsApi {
  setBackground(color: string): void;
}

export interface Platform {
  kind: PlatformKind;
  os: PlatformOs;
  storage(name: StorageName): CrossStorage;
  openUrl(url: string): void;
  saveFile(opts: SaveOptions): Promise<SaveResult>;

  pickFile?(opts: PickOptions): Promise<string | null>;
  window?: WindowApi;
  folders?: FoldersApi;
  guiLogs?: GuiLogsApi;
  log?(level: LogLevel, ...args: unknown[]): void;
  errorReporting?: ErrorReportingApi;
  discordPresence?: PresenceApi;
  translations?: TranslationsApi;
  github?: GithubApi;
  steam?: { isSteam(): Promise<boolean> };
  keybindRecording?(on: boolean): void;
  systemInsets?: SystemInsetsApi;
}

export let platform: Platform;

export function setPlatform(next: Platform) {
  platform = next;
}
