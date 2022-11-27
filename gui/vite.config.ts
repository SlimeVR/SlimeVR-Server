import { esbuildCommonjs, viteCommonjs } from '@originjs/vite-plugin-commonjs';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
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
