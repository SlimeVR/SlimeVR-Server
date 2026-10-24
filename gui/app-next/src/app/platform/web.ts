import type { Platform } from '@/lib/platform';
import { localCrossStorage } from '@/lib/storage';
import { saveWithBrowser } from './browser-save';

export function createWebPlatform(): Platform {
  const settings = localCrossStorage('');
  const cache = localCrossStorage('slimevr-cache/');

  return {
    kind: 'web',
    os: 'unknown',
    storage: (name) => (name === 'settings' ? settings : cache),
    openUrl: (url) => void window.open(url, '_blank'),
    saveFile: saveWithBrowser,
  };
}
