import type { CrossStorage } from '@slimevr/gui-shared';
import type { Platform } from '@/lib/platform';

export function memoryCrossStorage(): CrossStorage {
  const data = new Map<string, unknown>();
  return {
    get: async <T>(key: string) => data.get(key) as T | undefined,
    set: async (key, value) => {
      data.set(key, value);
    },
    delete: async (key) => data.delete(key),
    save: async () => true,
  };
}

// In-memory platform for tests and the /design page
export function fakePlatform(overrides: Partial<Platform> = {}): Platform {
  const settings = memoryCrossStorage();
  const cache = memoryCrossStorage();
  return {
    kind: 'web',
    os: 'linux',
    storage: (name) => (name === 'settings' ? settings : cache),
    openUrl: () => {},
    saveFile: async () => 'saved',
    ...overrides,
  };
}
