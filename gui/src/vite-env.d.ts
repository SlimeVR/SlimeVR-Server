// eslint-disable-next-line spaced-comment
/// <reference types="vite/client" />

declare const __COMMIT_HASH__: string;
declare const __VERSION_TAG__: string;
declare const __GIT_CLEAN__: boolean;

interface Window {
  readonly __ANDROID__:
    | {
        isThere: () => boolean;
      }
    | undefined;
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
