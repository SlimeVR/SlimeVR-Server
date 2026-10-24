import type { Platform } from '@/lib/platform';
import { localCrossStorage } from '@/lib/storage';
import { saveWithBrowser } from './browser-save';

type AndroidBridge = NonNullable<Window['__ANDROID__']>;

function toBase64(blob: Blob) {
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const url = reader.result as string;
      resolve(url.slice(url.indexOf(',') + 1));
    };
    reader.onerror = () => reject(reader.error);
    reader.readAsDataURL(blob);
  });
}

export function createAndroidPlatform(android: AndroidBridge): Platform {
  const settings = localCrossStorage('');
  const cache = localCrossStorage('slimevr-cache/');

  return {
    kind: 'android',
    os: 'android',
    storage: (name) => (name === 'settings' ? settings : cache),
    openUrl: (url) => void window.open(url, '_blank'),
    saveFile: async (opts) => {
      if (typeof android.saveFile !== 'function') return saveWithBrowser(opts);
      const { blob, fileName } = opts;
      android.saveFile(
        fileName,
        blob.type || 'application/octet-stream',
        await toBase64(blob)
      );
      return 'saved';
    },
    systemInsets: { setBackground: (color) => android.updateInsetBackground(color) },
  };
}
