// eslint-disable-next-line spaced-comment
/// <reference types="vite/client" />

declare const __COMMIT_HASH__: string;
declare const __VERSION_TAG__: string;
declare const __GIT_CLEAN__: boolean;

interface Window {
  readonly isTauri: boolean;
}

declare module 'tailwind-gradient-mask-image';
