import { isTauri } from '@tauri-apps/api/core';
import { LazyStore } from '@tauri-apps/plugin-store';

interface CrossStorage {
  set(key: string, value: unknown): Promise<void>;
  get<T>(key: string): Promise<T | undefined>;
  delete(key: string): Promise<boolean>;
}

const localStore: CrossStorage = {
  get: async <T>(key: string) =>
    (localStorage.getItem(`slimevr-cache/${key}`) as T) ?? undefined,
  set: async (key, value) =>
    localStorage.setItem(`slimevr-cache/${key}`, value as string),
  delete: async (key) => {
    localStorage.removeItem(`slimevr-cache/${key}`);
    return true;
  },
};

const store: CrossStorage = isTauri()
  ? new LazyStore('gui-cache.dat', { autoSave: 100, defaults: {} })
  : localStore;

export async function cacheGet(key: string): Promise<string | null> {
  const itemStr = await store.get<string>(key);

  if (!itemStr) {
    return null;
  }

  const item = JSON.parse(itemStr);
  const now = new Date();

  if (item.expiry > 0 && now.getTime() > item.expiry) {
    await store.delete(key);
    return null;
  }

  return item.value;
}

export async function cacheSet(key: string, value: unknown, ttl: number | undefined) {
  const now = new Date();
  const item = {
    value,
    expiry: ttl ? now.getTime() + ttl : 0,
  };

  await store.set(key, JSON.stringify(item));
}

export async function cacheWrap(
  key: string,
  orDefault: () => Promise<string | null>,
  ttl: number | undefined
) {
  const realItem = await cacheGet(key);
  if (!realItem) {
    const defaultItem = await orDefault();
    await cacheSet(key, defaultItem, ttl);
    return defaultItem;
  } else {
    return realItem;
  }
}

export async function cacheDelete(key: string) {
  await store.delete(key);
}
