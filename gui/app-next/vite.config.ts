/// <reference types="vitest/config" />
import tailwindcss from '@tailwindcss/vite';
import { execSync } from 'node:child_process';
import path from 'node:path';
import Icons from 'unplugin-icons/vite';
import { defineConfig, loadEnv, type PluginOption } from 'vite';
import solid from 'vite-plugin-solid';

const commitHash = execSync('git rev-parse --verify --short=8 HEAD').toString().trim();
const versionTag = execSync('git --no-pager tag --sort -taggerdate --points-at HEAD')
  .toString()
  .split('\n')[0]
  .trim();
// Non-empty means the tree is dirty
const gitCleanString = execSync('git status --porcelain').toString();
const gitClean = !gitCleanString;
if (!gitClean) console.log('Git is dirty because of:\n' + gitCleanString);

console.log(`version is ${versionTag || commitHash}${gitClean ? '' : '-dirty'}`);

// SENTRY_* from the repo root .env.local, see .env.example. Real environment variables win.
// Only clean tagged builds are Sentry releases, SENTRY_RELEASE forces one (CI never sets it).
const sentryEnv = loadEnv('', path.resolve(__dirname, '../..'), 'SENTRY_');
const sentryReleaseOverride = sentryEnv.SENTRY_RELEASE ?? '';
const sentryRelease = sentryReleaseOverride || (gitClean ? versionTag : '');

// Tells the app to reload its Fluent bundles when a .ftl file changes
function i18nHotReload(): PluginOption {
  return {
    name: 'i18n-hot-reload',
    handleHotUpdate({ file, server }) {
      if (file.endsWith('.ftl')) {
        console.log('Fluent files updated');
        server.hot.send({ type: 'custom', event: 'locales-update' });
      }
    },
  };
}

export default defineConfig({
  define: {
    __COMMIT_HASH__: JSON.stringify(commitHash),
    __VERSION_TAG__: JSON.stringify(versionTag),
    __GIT_CLEAN__: gitClean,
    // Sentry project of the gui, empty disables error reporting
    __SENTRY_DSN__: JSON.stringify(sentryEnv.SENTRY_GUI_DSN ?? ''),
    __SENTRY_RELEASE__: JSON.stringify(sentryRelease),
    // A forced release also reports from the dev server, for trying things locally
    __SENTRY_RELEASE_FORCED__: !!sentryReleaseOverride,
    __SENTRY_ENVIRONMENT__: JSON.stringify(sentryEnv.SENTRY_ENVIRONMENT || 'production'),
  },
  plugins: [solid(), tailwindcss(), Icons({ compiler: 'solid' }), i18nHotReload()],
  // FIXME: share i18n, images, fonts with the old app until parity
  publicDir: '../app/public',
  server: {
    host: '127.0.0.1',
    port: 5174,
    strictPort: true,
  },
  build: {
    target: 'es2022',
    emptyOutDir: true,
    sourcemap: true,
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test-setup.ts'],
    include: ['src/**/*.test.{ts,tsx}'],
  },
});
