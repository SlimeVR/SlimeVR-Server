/// <reference types="vite/client" />
/// <reference types="unplugin-icons/types/solid" />

declare const __COMMIT_HASH__: string;
declare const __VERSION_TAG__: string;
declare const __GIT_CLEAN__: boolean;
declare const __SENTRY_DSN__: string;
declare const __SENTRY_RELEASE__: string;
declare const __SENTRY_RELEASE_FORCED__: boolean;
declare const __SENTRY_ENVIRONMENT__: string;

interface Window {
  readonly __ANDROID__:
    | {
        isThere: () => boolean;
        updateInsetBackground: (c: string) => void;
        saveFile: (name: string, mimeType: string, base64: string) => void;
      }
    | undefined;
  _jipt?: [string, unknown][];
  jipt?: {
    start: () => void;
  };
}
