// eslint-disable-next-line spaced-comment
/// <reference types="vite/client" />

// eslint-disable-next-line @typescript-eslint/naming-convention
declare const __COMMIT_HASH__: string;
// eslint-disable-next-line @typescript-eslint/naming-convention
declare const __VERSION_TAG__: string;
// eslint-disable-next-line @typescript-eslint/naming-convention
declare const __GIT_CLEAN__: boolean;
declare const __ANDROID__:
  | {
      isThere: () => boolean;
    }
  | undefined;

interface Window {
  readonly isTauri: boolean;
}

declare module 'tailwind-gradient-mask-image';
