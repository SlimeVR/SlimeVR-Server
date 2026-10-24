import {
  ArmsMountingResetMode,
  ChangeResetsSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  SkeletonSettingsRequestT,
  SkeletonSettingsResponseT,
} from 'solarxr-protocol';
import { beforeEach, describe, expect, it } from 'vitest';
import { setClient } from '@/solarxr/client';
import { defineSettings } from '@/solarxr/settings';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';

let fake: FakeClient;

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
  fake.push(new ResetsSettingsResponseT(false, ArmsMountingResetMode.SIDE, 2));
  return settings;
}

beforeEach(() => {
  fake = createFakeClient();
  setClient(fake);
});

describe('defineSettings', () => {
  it('rejects a change that the response does not cover', () => {
    defineSettings<
      ChangeResetsSettingsRequestT,
      // @ts-expect-error skeleton response lacks the resets fields
      SkeletonSettingsResponseT
    >({
      request: () => new SkeletonSettingsRequestT(),
      response: SkeletonSettingsResponseT,
      change: ChangeResetsSettingsRequestT,
    });
  });

  it('sends every change field merged onto the response', () => {
    const settings = loaded();
    expect(settings.update({ resetMountingFeet: true })).toBe(true);
    const [sent] = fake.sentOf(ChangeResetsSettingsRequestT);
    expect(sent).toMatchObject({
      resetMountingFeet: true,
      armsMountingResetMode: ArmsMountingResetMode.SIDE,
      yawResetSmoothTime: 2,
    });
  });

  it('does not write the value itself', () => {
    const settings = loaded();
    settings.update({ resetMountingFeet: true });
    expect(settings.value()?.resetMountingFeet).toBe(false);
  });

  it('merges onto the last sent change until the server answers', () => {
    const settings = loaded();
    settings.update({ resetMountingFeet: true });
    settings.update({ yawResetSmoothTime: 5 });
    const second = fake.sentOf(ChangeResetsSettingsRequestT)[1];
    expect(second).toMatchObject({ resetMountingFeet: true, yawResetSmoothTime: 5 });

    fake.push(new ResetsSettingsResponseT(false, ArmsMountingResetMode.BACK, 1));
    settings.update({ saveMountingReset: true });
    const third = fake.sentOf(ChangeResetsSettingsRequestT)[2];
    expect(third).toMatchObject({
      resetMountingFeet: false,
      armsMountingResetMode: ArmsMountingResetMode.BACK,
      yawResetSmoothTime: 1,
      saveMountingReset: true,
    });
  });

  it('refuses before the first response', () => {
    const settings = resetsSettings();
    settings.value();
    expect(settings.update({ resetMountingFeet: true })).toBe(false);
    expect(fake.sentOf(ChangeResetsSettingsRequestT)).toHaveLength(0);
  });

  it('refuses while stale', () => {
    const settings = loaded();
    fake.disconnect();
    expect(settings.update({ resetMountingFeet: true })).toBe(false);
    fake.connect();
    expect(settings.update({ resetMountingFeet: true })).toBe(false);
    expect(fake.sentOf(ChangeResetsSettingsRequestT)).toHaveLength(0);

    fake.push(new ResetsSettingsResponseT());
    expect(settings.update({ resetMountingFeet: true })).toBe(true);
  });
});
