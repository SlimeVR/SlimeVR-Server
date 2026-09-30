import { sentryVitePlugin } from '@sentry/vite-plugin';
import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv, PluginOption } from 'vite';
import { execSync } from 'child_process';
import path from 'path';
import { visualizer } from 'rollup-plugin-visualizer';
import jotaiReactRefresh from 'jotai/babel/plugin-react-refresh';

const commitHash = execSync('git rev-parse --verify --short=8 HEAD').toString().trim();
const versionTag = execSync('git --no-pager tag --sort -taggerdate --points-at HEAD')
  .toString()
  .split('\n')[0]
  .trim();
// If not empty then it's not clean
const gitCleanString = execSync('git status --porcelain').toString();
const gitClean = gitCleanString ? false : true;
if (!gitClean) console.log('Git is dirty because of:\n' + gitCleanString);

console.log(`version is ${versionTag || commitHash}${gitClean ? '' : '-dirty'}`);

// SENTRY_* from the repo root .env.local, see .env.example. Real environment variables win.
// Only clean tagged builds are Sentry releases, SENTRY_RELEASE forces one (CI never sets it).
const sentryEnv = loadEnv('', path.resolve(__dirname, '../..'), 'SENTRY_');
const sentryReleaseOverride = sentryEnv.SENTRY_RELEASE ?? '';
const sentryRelease = sentryReleaseOverride || (gitClean ? versionTag : '');

// Detect fluent file changes
export function i18nHotReload(): PluginOption {
  return {
    name: 'i18n-hot-reload',
    handleHotUpdate({ file, server }) {
      if (file.endsWith('.ftl')) {
        console.log('Fluent files updated');
        server.hot.send({
          type: 'custom',
          event: 'locales-update',
        });
      }
    },
  };
}

// https://vitejs.dev/config/
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
    __SENTRY_ENVIRONMENT__: JSON.stringify(
      sentryEnv.SENTRY_ENVIRONMENT || 'production'
    ),
  },
  plugins: [
    react({ babel: { plugins: [jotaiReactRefresh] } }),
    i18nHotReload(),
    visualizer() as PluginOption,
    sentryVitePlugin({
      org: 'slimevr',
      project: 'slimevr-server-gui-react',
      // A forced test release never uploads to the real project
      disable: !sentryRelease || !!sentryReleaseOverride,
      release: { name: sentryRelease },
      // Maps are uploaded to Sentry, not shipped to users
      sourcemaps: {
        // electron build passes --outDir ../electron/out/renderer
        filesToDeleteAfterUpload: [
          './dist/**/*.map',
          '../electron/out/renderer/**/*.map',
        ],
      },
    }),
  ],
  build: {
    target: 'es2022',
    emptyOutDir: true,
    sourcemap: true,
  },
  optimizeDeps: {
    esbuildOptions: {
      target: 'es2022',
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern',
      },
    },
  },
});
