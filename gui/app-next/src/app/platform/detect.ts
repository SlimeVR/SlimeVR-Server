import type { Platform } from '@/lib/platform';
import { createAndroidPlatform } from './android';
import { createElectronPlatform } from './electron';
import { createWebPlatform } from './web';

export async function createPlatform(): Promise<Platform> {
  if (window.electronAPI) return createElectronPlatform(window.electronAPI);
  const android = window.__ANDROID__;
  if (android?.isThere()) return createAndroidPlatform(android);
  return createWebPlatform();
}
