// eslint-disable-next-line spaced-comment
/// <reference types="vite/client" />

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
      }
    | undefined;
  _jipt?: [string, unknown][];
  jipt?: {
    start: () => void;
  };
}

declare module 'tailwind-gradient-mask-image';

declare module '*?asset' {
  const content: string;
  export default content;
}

declare module '*?asset&asarUnpack' {
  const content: string;
  export default content;
}
