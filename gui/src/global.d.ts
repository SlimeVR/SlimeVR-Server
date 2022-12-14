/* eslint-disable no-var */
// Use var for globalThis:
// https://www.typescriptlang.org/docs/handbook/release-notes/typescript-3-4.html#type-checking-for-globalthis

declare global {
  interface Window {
    __COMMIT_HASH__: string;
    __VERSION_TAG__: string;
    __GIT_CLEAN__: boolean;
  }
}

// Export is necessary for types to export
export {};
