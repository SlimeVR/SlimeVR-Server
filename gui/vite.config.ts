import { esbuildCommonjs, viteCommonjs } from '@originjs/vite-plugin-commonjs';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import { execSync } from 'child_process';

const commitHash = execSync('git rev-parse --short HEAD').toString();

// https://vitejs.dev/config/
export default defineConfig({
  define: {
    __COMMIT_HASH__: JSON.stringify(commitHash),
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
