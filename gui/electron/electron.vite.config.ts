import { defineConfig, loadEnv } from 'electron-vite';
import { resolve } from 'path';

// SENTRY_* from the repo root .env.local, see .env.example. Real environment variables win.
const sentryEnv = loadEnv('', resolve(__dirname, '../..'), 'SENTRY_');

export default defineConfig({
  main: {
    define: {
      __SENTRY_ELECTRON_DSN__: JSON.stringify(sentryEnv.SENTRY_ELECTRON_DSN ?? ''),
      __SENTRY_SERVER_DSN__: JSON.stringify(sentryEnv.SENTRY_SERVER_DSN ?? ''),
      __SENTRY_RELEASE__: JSON.stringify(sentryEnv.SENTRY_RELEASE ?? ''),
      __SENTRY_ENVIRONMENT__: JSON.stringify(sentryEnv.SENTRY_ENVIRONMENT || 'production'),
    },
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
