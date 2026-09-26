import { defineConfig } from 'electron-vite';
import { resolve } from 'path';

export default defineConfig({
  main: {
    build: {
      // @slimevr/gui-shared is a workspace source package (TS). electron-vite
      // externalizes all package.json deps by default; exclude this one so it
      // gets bundled in (its runtime IPC_CHANNELS compiled into the output
      // instead of imported as raw .ts at runtime).
      externalizeDeps: { exclude: ['@slimevr/gui-shared'] },
      rollupOptions: {
        input: resolve(__dirname, 'main/index.ts'),
        external: ['pino', 'pino-pretty', 'pino-roll', 'commander', 'open'],
      },
    },
  },
  preload: {
    build: {
      externalizeDeps: { exclude: ['@slimevr/gui-shared'] },
      rollupOptions: {
        input: resolve(__dirname, 'preload/index.ts'),
        output: {
          format: 'cjs', // Force CJS for the preload
          entryFileNames: 'index.js', // Change back to .js
        },
      },
    },
  },
});
