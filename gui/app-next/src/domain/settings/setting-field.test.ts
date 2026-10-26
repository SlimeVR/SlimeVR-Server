import {
  ChangeResetsSettingsRequestT,
  ChangeSkeletonSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  SkeletonRatiosT,
  SkeletonSettingsRequestT,
  SkeletonSettingsResponseT,
  SkeletonTogglesT,
} from 'solarxr-protocol';
import * as v from 'valibot';
import { beforeEach, describe, expect, it } from 'vitest';
import {
  prefRow,
  settingField,
  settingsReset,
  tableSettingField,
  tableSettingsReset,
} from '@/domain/settings/setting-field';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { defineSettingsPage } from '@/lib/settings-def';
import { definePref } from '@/lib/storage';
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
  setPlatform(fakePlatform());
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

function skeleton() {
  return defineSettings({
    request: () => new SkeletonSettingsRequestT(),
    response: SkeletonSettingsResponseT,
    change: ChangeSkeletonSettingsRequestT,
  });
}

function skeletonResponse(toggles: Partial<SkeletonTogglesT>) {
  const msg = new SkeletonSettingsResponseT();
  msg.toggles = Object.assign(new SkeletonTogglesT(), toggles);
  msg.ratios = new SkeletonRatiosT(0.3);
  return msg;
}

describe('tableSettingField', () => {
  it('reads and saves one key of a nested table', () => {
    const settings = skeleton();
    const field = tableSettingField(settings, 'toggles', 'floorClip');
    settings.value();
    fake.push(skeletonResponse({ floorClip: false, toeSnap: true }));
    expect(field.value).toBe(false);
    field.onChange(true);
    const [sent] = fake.sentOf(ChangeSkeletonSettingsRequestT);
    expect(sent?.toggles).toBeInstanceOf(SkeletonTogglesT);
    expect(sent?.toggles).toMatchObject({ floorClip: true, toeSnap: true });
    expect(sent?.ratios?.spineCurvature).toBeCloseTo(0.3);
    expect(field.value).toBe(true);
  });

  it('resets keys of the table to the defaults', () => {
    const settings = skeleton();
    const defaults = () => skeletonResponse({ floorClip: false, toeSnap: false });
    const reset = tableSettingsReset(settings, defaults, 'toggles', ['floorClip']);
    settings.value();
    fake.push(skeletonResponse({ floorClip: true, toeSnap: true }));
    expect(reset.modified).toBe(true);
    reset.onReset();
    const [sent] = fake.sentOf(ChangeSkeletonSettingsRequestT);
    expect(sent?.toggles).toMatchObject({ floorClip: false, toeSnap: true });
  });
});

describe('prefRow', () => {
  const page = defineSettingsPage({
    path: 'test',
    title: 'settings-sidebar-behavior',
    icon: () => null,
    groups: { main: { rows: { sound: { title: 'settings-sidebar-behavior' } } } },
  });

  it('writes the pref and resets it to its fallback', () => {
    const pref = definePref('test.prefRow', v.boolean(), true);
    const row = prefRow(page.groups.main.rows.sound, pref);
    expect(row.field.value).toBe(true);
    expect(row.reset?.modified).toBe(false);
    row.field.onChange(false);
    expect(pref()).toBe(false);
    expect(row.reset?.modified).toBe(true);
    row.reset?.onReset();
    expect(pref()).toBe(true);
  });
});
