import {
  ChangeResetsSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
} from 'solarxr-protocol';
import { beforeEach, describe, expect, it } from 'vitest';
import {
  saveSettings,
  lastSave,
  observeSaves,
  type SaveEvent,
} from '@/domain/settings/save-settings';
import { setClient } from '@/solarxr/client';
import { defineSettings } from '@/solarxr/settings';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';

let fake: FakeClient;

function loadedSettings() {
  const settings = defineSettings({
    request: () => new ResetsSettingsRequestT(),
    response: ResetsSettingsResponseT,
    change: ChangeResetsSettingsRequestT,
  });
  settings.value();
  fake.push(new ResetsSettingsResponseT());
  return settings;
}

beforeEach(() => {
  fake = createFakeClient();
  setClient(fake);
});

describe('saveSettings', () => {
  it('sends and records a save', () => {
    const settings = loadedSettings();
    expect(saveSettings(settings, { resetMountingFeet: true })).toBe(true);
    expect(fake.sentOf(ChangeResetsSettingsRequestT)).toHaveLength(1);
    expect(lastSave()).toMatchObject({
      outcome: 'saved',
      keys: ['resetMountingFeet'],
      values: { resetMountingFeet: true },
    });
  });

  it('records a refusal while disconnected', () => {
    const settings = loadedSettings();
    fake.disconnect();
    expect(saveSettings(settings, { resetMountingFeet: true })).toBe(false);
    expect(lastSave()?.outcome).toBe('refused');
  });

  it('keeps values out of sensitive saves', () => {
    const settings = loadedSettings();
    const events: SaveEvent[] = [];
    const stop = observeSaves((event) => events.push(event));
    saveSettings(settings, { yawResetSmoothTime: 3 }, { sensitive: true });
    stop();
    saveSettings(settings, { yawResetSmoothTime: 4 });
    expect(events).toHaveLength(1);
    expect(events[0]?.keys).toEqual(['yawResetSmoothTime']);
    expect(events[0]?.values).toBeUndefined();
  });

  it('gives each save a new id', () => {
    const settings = loadedSettings();
    saveSettings(settings, { resetMountingFeet: true });
    const first = lastSave()?.id ?? 0;
    saveSettings(settings, { resetMountingFeet: true });
    expect(lastSave()?.id).toBe(first + 1);
  });
});
