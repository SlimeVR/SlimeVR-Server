import {
  ChangeResetsSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
} from 'solarxr-protocol';
import { beforeEach, describe, expect, it } from 'vitest';
import { settingField, settingsReset } from '@/domain/settings/setting-field';
import { setClient } from '@/solarxr/client';
import { defineSettings } from '@/solarxr/settings';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';

let fake: FakeClient;

function resets() {
  return defineSettings({
    request: () => new ResetsSettingsRequestT(),
    response: ResetsSettingsResponseT,
    change: ChangeResetsSettingsRequestT,
  });
}

function response(patch: Partial<ResetsSettingsResponseT>) {
  return Object.assign(new ResetsSettingsResponseT(), patch);
}

beforeEach(() => {
  fake = createFakeClient();
  setClient(fake);
});

describe('settingField', () => {
  it('is loading until the first response, then shows the value', () => {
    const settings = resets();
    const field = settingField(settings, 'yawResetSmoothTime');
    expect(field.value).toBeUndefined();
    expect(field.loading).toBe(true);
    fake.push(response({ yawResetSmoothTime: 0.2 }));
    expect(field.loading).toBe(false);
    expect(field.disabled).toBe(false);
    expect(field.value).toBe(0.2);
  });

  it('saves the whole settings with the changed key', () => {
    const settings = resets();
    const field = settingField(settings, 'resetMountingFeet');
    settings.value();
    fake.push(response({ yawResetSmoothTime: 0.2 }));
    field.onChange(true);
    const [sent] = fake.sentOf(ChangeResetsSettingsRequestT);
    expect(sent?.resetMountingFeet).toBe(true);
    expect(sent?.yawResetSmoothTime).toBe(0.2);
  });

  it('is disabled while disconnected, keeping the value', () => {
    const settings = resets();
    const field = settingField(settings, 'resetMountingFeet');
    settings.value();
    fake.push(response({ resetMountingFeet: true }));
    fake.disconnect();
    expect(field.disabled).toBe(true);
    expect(field.loading).toBe(false);
    expect(field.value).toBe(true);
  });
});

describe('settingsReset', () => {
  it('is modified only once both sides loaded and differ', () => {
    const settings = resets();
    let defaults: ResetsSettingsResponseT | undefined;
    const reset = settingsReset(settings, () => defaults, ['resetMountingFeet']);
    settings.value();
    expect(reset.modified).toBe(false);
    fake.push(response({ resetMountingFeet: true }));
    expect(reset.modified).toBe(false);
    defaults = response({ resetMountingFeet: false });
    expect(reset.modified).toBe(true);
    defaults = response({ resetMountingFeet: true });
    expect(reset.modified).toBe(false);
  });

  it('resets every key in one save', () => {
    const settings = resets();
    const defaults = () =>
      response({ resetMountingFeet: false, yawResetSmoothTime: 0.1 });
    const reset = settingsReset(settings, defaults, [
      'resetMountingFeet',
      'yawResetSmoothTime',
    ]);
    settings.value();
    fake.push(
      response({
        resetMountingFeet: true,
        yawResetSmoothTime: 0.4,
        saveMountingReset: true,
      })
    );
    reset.onReset();
    const sent = fake.sentOf(ChangeResetsSettingsRequestT);
    expect(sent).toHaveLength(1);
    expect(sent[0]).toMatchObject({
      resetMountingFeet: false,
      yawResetSmoothTime: 0.1,
      saveMountingReset: true,
    });
  });
});
