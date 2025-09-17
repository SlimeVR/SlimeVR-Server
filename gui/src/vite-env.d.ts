// eslint-disable-next-line spaced-comment
/// <reference types="vite/client" />

declare const __COMMIT_HASH__: string;
declare const __VERSION_TAG__: string;
declare const __GIT_CLEAN__: boolean;
declare const __ANDROID__:
  | {
      isThere: () => boolean;
    }
  | undefined;

interface Window {
  readonly isTauri: boolean;
  readonly __IOS__: boolean | undefined;
}

declare module 'tailwind-gradient-mask-image';
