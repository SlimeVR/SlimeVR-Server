import { defineConfig } from 'electron-vite'
import { resolve } from 'path'
import rendererConfig from './vite.config' // Import your existing React config

export default defineConfig({
  main: {
    build: {
      rollupOptions: {
        input: resolve(__dirname, 'electron/main/index.ts'),
      }
    }
  },
  preload: {
    build: {
      rollupOptions: {
        input: resolve(__dirname, 'electron/preload/index.ts'),
        output: {
          format: 'cjs', // Force CJS for the preload
          entryFileNames: 'index.js' // Change back to .js
        }
      }
    }
  },
 renderer: {
    ...rendererConfig,
    root: '.',
    build: {
      commonjsOptions: {
        // Force Rollup to treat the protocol directory as CommonJS
        // even though it's not in node_modules
        include: [/solarxr-protocol/, /node_modules/],
        // Required for Flatbuffers/Generated code interop
        transformMixedEsModules: true,
      },
      rollupOptions: {
        input: resolve(__dirname, 'index.html')
      }
    }
  }
})
