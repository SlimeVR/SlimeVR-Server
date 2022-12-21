import { esbuildCommonjs, viteCommonjs } from '@originjs/vite-plugin-commonjs';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import { execSync } from 'child_process';

const commitHash = execSync('git rev-parse --verify --short HEAD')
  .toString()
  .trim();
const versionTag = execSync('git --no-pager tag --points-at HEAD')
  .toString()
  .trim();
// If not empty then it's not clean
const gitClean = execSync('git status --porcelain').toString() ? false : true;

console.log(`version is ${versionTag || commitHash}${gitClean ? "" : "-dirty"}`);

// https://vitejs.dev/config/
export default defineConfig({
  define: {
    __COMMIT_HASH__: JSON.stringify(commitHash),
    __VERSION_TAG__: JSON.stringify(versionTag),
    __GIT_CLEAN__: gitClean,
  },
  plugins: [viteCommonjs(), react()],
  build: {
    target: 'es2020',
    emptyOutDir: true,
    commonjsOptions: {
      include: [/solarxr-protocol/, /node_modules/],
    },
  },
  optimizeDeps: {
    esbuildOptions: {
      target: 'es2020',
      plugins: [esbuildCommonjs(['solarxr-protocol'])],
    },
    needsInterop: ['solarxr-protocol'],
    include: ['solarxr-protocol'],
  },
});
