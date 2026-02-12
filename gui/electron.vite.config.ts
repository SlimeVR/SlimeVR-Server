import { defineConfig } from 'electron-vite'
import { resolve } from 'path'
import rendererConfig from './vite.config' // Import your existing React config

export default defineConfig({
  main: {
    build: {
      rollupOptions: {
        input: resolve(__dirname, 'electron/main/index.ts')
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
    // 1. Spread your existing React config (plugins, resolve, etc.)
    ...rendererConfig,

    // 2. Ensure the root and input are correct for the Electron build
    root: '.',
    build: {
      rollupOptions: {
        input: resolve(__dirname, 'index.html')
      }
    }
  }
})
