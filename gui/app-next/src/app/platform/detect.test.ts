import type { IElectronAPI } from '@slimevr/gui-shared';
import { describe, expect, it, vi } from 'vitest';
import { memoryCrossStorage } from '@/lib/platform-fake';
import { createPlatform } from './detect';

describe('createPlatform', () => {
  it('resolves the OS before returning on electron', async () => {
    const storage = memoryCrossStorage();
    vi.stubGlobal('electronAPI', {
      osStats: async () => ({ type: 'linux' }),
      getStorage: async () => storage,
    } as Partial<IElectronAPI>);

    const platform = await createPlatform();
    expect(platform.kind).toBe('desktop');
    expect(platform.os).toBe('linux');
    expect(platform.storage('settings')).toBe(storage);
    expect(platform.window).toBeDefined();
  });

  it('picks android when the bridge is there', async () => {
    vi.stubGlobal('__ANDROID__', {
      isThere: () => true,
      updateInsetBackground: () => {},
      saveFile: () => {},
    });

    const platform = await createPlatform();
    expect(platform.kind).toBe('android');
    expect(platform.systemInsets).toBeDefined();
    expect(platform.window).toBeUndefined();
  });

  it('falls back to web', async () => {
    const platform = await createPlatform();
    expect(platform.kind).toBe('web');
    expect(platform.pickFile).toBeUndefined();
  });
});
