import {
  ChangeResetsSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
} from 'solarxr-protocol';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { saveSettings } from '@/domain/settings/save-settings';
import { initSaveToasts } from '@/domain/settings/save-toasts';
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
  it('coalesces successful saves into one toast', () => {
    const settings = resetsSettings();
    settings.value();
    fake.push(new ResetsSettingsResponseT());
    saveSettings(settings, { yawResetSmoothTime: 1 });
    saveSettings(settings, { yawResetSmoothTime: 2 });
    expect(toasts()).toEqual([
      expect.objectContaining({ kind: 'success', message: 'toast-saved' }),
    ]);
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
