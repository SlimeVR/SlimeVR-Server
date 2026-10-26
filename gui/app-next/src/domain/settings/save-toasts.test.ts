import {
  ChangeResetsSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
} from 'solarxr-protocol';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { saveSettings } from '@/domain/settings/save-settings';
import { initSaveToasts, SAVING_GRACE_MS } from '@/domain/settings/save-toasts';
import { setClient } from '@/solarxr/client';
import { defineSettings } from '@/solarxr/settings';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';
import { clearToasts, toasts } from '@/ui/toast';

let fake: FakeClient;
let stop: () => void;

function resetsSettings() {
  return defineSettings({
    request: () => new ResetsSettingsRequestT(),
    response: ResetsSettingsResponseT,
    change: ChangeResetsSettingsRequestT,
  });
}

function loaded() {
  const settings = resetsSettings();
  settings.value();
  fake.push(new ResetsSettingsResponseT());
  return settings;
}

beforeEach(() => {
  fake = createFakeClient();
  setClient(fake);
  stop = initSaveToasts();
});

afterEach(() => {
  stop();
  clearToasts();
});

describe('save toasts', () => {
  it('a fast server goes straight to saved', async () => {
    const settings = loaded();
    saveSettings(settings, { yawResetSmoothTime: 1 });
    saveSettings(settings, { yawResetSmoothTime: 2 });
    expect(toasts()).toEqual([]);
    fake.push(Object.assign(new ResetsSettingsResponseT(), { yawResetSmoothTime: 2 }));
    await vi.waitFor(() =>
      expect(toasts()).toEqual([
        expect.objectContaining({ kind: 'success', message: 'toast-saved' }),
      ])
    );
    await new Promise((resolve) => setTimeout(resolve, SAVING_GRACE_MS + 50));
    expect(toasts()).toEqual([expect.objectContaining({ kind: 'success' })]);
  });

  it('a slow server shows saving, then saved in the same toast', async () => {
    const settings = loaded();
    saveSettings(settings, { yawResetSmoothTime: 2 });
    await vi.waitFor(() =>
      expect(toasts()).toEqual([
        expect.objectContaining({ kind: 'pending', message: 'toast-saving' }),
      ])
    );
    const [saving] = toasts();
    fake.push(Object.assign(new ResetsSettingsResponseT(), { yawResetSmoothTime: 2 }));
    await vi.waitFor(() =>
      expect(toasts()).toEqual([
        expect.objectContaining({ id: saving?.id, kind: 'success' }),
      ])
    );
  });

  it('a failed save replaces the saving toast', async () => {
    const settings = loaded();
    saveSettings(settings, { yawResetSmoothTime: 1 });
    fake.disconnect();
    await vi.waitFor(() =>
      expect(toasts()).toEqual([
        expect.objectContaining({ kind: 'error', message: 'toast-save-failed' }),
      ])
    );
  });

  it('shows every refused save', () => {
    // never loaded, so refused
    const settings = resetsSettings();
    saveSettings(settings, { yawResetSmoothTime: 1 });
    saveSettings(settings, { yawResetSmoothTime: 2 });
    expect(toasts()).toHaveLength(2);
    expect(toasts().every((t) => t.kind === 'error')).toBe(true);
  });
});
