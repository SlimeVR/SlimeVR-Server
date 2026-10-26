import {
  ArmsMountingResetMode,
  ChangeMagToggleRequestT,
  ChangeResetsSettingsRequestT,
  MagToggleRequestT,
  MagToggleResponseT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  SkeletonSettingsRequestT,
  SkeletonSettingsResponseT,
} from 'solarxr-protocol';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { setClient } from '@/solarxr/client';
import {
  defineKeyedSettings,
  defineSettings,
  SAVE_TIMEOUT_MS,
} from '@/solarxr/settings';
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

afterEach(() => {
  vi.useRealTimers();
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
    expect(settings.update({ resetMountingFeet: true })).not.toBeNull();
    const [sent] = fake.sentOf(ChangeResetsSettingsRequestT);
    expect(sent).toMatchObject({
      resetMountingFeet: true,
      armsMountingResetMode: ArmsMountingResetMode.SIDE,
      yawResetSmoothTime: 2,
    });
  });

  it('shows the pending value until the server confirms', async () => {
    const settings = loaded();
    const result = settings.update({ resetMountingFeet: true });
    expect(settings.value()?.resetMountingFeet).toBe(true);
    fake.push(new ResetsSettingsResponseT(true, ArmsMountingResetMode.SIDE, 2));
    expect(await result).toBe('saved');
    expect(settings.value()?.resetMountingFeet).toBe(true);
  });

  it('a response that does not match keeps the save pending', () => {
    const settings = loaded();
    settings.update({ resetMountingFeet: true });
    fake.push(new ResetsSettingsResponseT(false, ArmsMountingResetMode.BACK, 2));
    expect(settings.value()).toMatchObject({
      resetMountingFeet: true,
      armsMountingResetMode: ArmsMountingResetMode.SIDE,
    });
  });

  it('compares numbers at float32 precision', async () => {
    const settings = loaded();
    const result = settings.update({ yawResetSmoothTime: 0.05 });
    fake.push(
      new ResetsSettingsResponseT(false, ArmsMountingResetMode.SIDE, Math.fround(0.05))
    );
    expect(await result).toBe('saved');
  });

  it('a confirm override decides for asymmetric settings', async () => {
    const settings = defineSettings({
      request: () => new ResetsSettingsRequestT(),
      response: ResetsSettingsResponseT,
      change: ChangeResetsSettingsRequestT,
      // Stand-in for a server that rewrites the smooth time
      confirm: (sent, got) => sent.resetMountingFeet === got.resetMountingFeet,
    });
    settings.value();
    fake.push(new ResetsSettingsResponseT(false, ArmsMountingResetMode.SIDE, 2));
    const result = settings.update({ resetMountingFeet: true, yawResetSmoothTime: 9 });
    fake.push(new ResetsSettingsResponseT(true, ArmsMountingResetMode.SIDE, 1));
    expect(await result).toBe('saved');
  });

  it('merges onto the pending change, then onto the confirmed response', () => {
    const settings = loaded();
    settings.update({ resetMountingFeet: true });
    settings.update({ yawResetSmoothTime: 5 });
    const second = fake.sentOf(ChangeResetsSettingsRequestT)[1];
    expect(second).toMatchObject({ resetMountingFeet: true, yawResetSmoothTime: 5 });

    fake.push(new ResetsSettingsResponseT(true, ArmsMountingResetMode.SIDE, 5));
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

  it('every save of a burst settles with the last one', async () => {
    const settings = loaded();
    const first = settings.update({ resetMountingFeet: true });
    const second = settings.update({ yawResetSmoothTime: 5 });
    fake.push(new ResetsSettingsResponseT(true, ArmsMountingResetMode.SIDE, 5));
    expect(await first).toBe('saved');
    expect(await second).toBe('saved');
  });

  it('fails without a matching response and drops the pending value', async () => {
    vi.useFakeTimers();
    const settings = loaded();
    const result = settings.update({ resetMountingFeet: true });
    vi.advanceTimersByTime(SAVE_TIMEOUT_MS);
    expect(await result).toBe('not-saved');
    expect(settings.value()?.resetMountingFeet).toBe(false);
  });

  it('a change back to the current values is saved at the timeout', async () => {
    vi.useFakeTimers();
    const settings = loaded();
    settings.update({ resetMountingFeet: true });
    // The server may conflate true then false into no Response at all
    const back = settings.update({ resetMountingFeet: false });
    vi.advanceTimersByTime(SAVE_TIMEOUT_MS);
    expect(await back).toBe('saved');
  });

  it('fails on disconnect', async () => {
    const settings = loaded();
    const result = settings.update({ resetMountingFeet: true });
    fake.disconnect();
    expect(await result).toBe('not-saved');
    expect(settings.value()?.resetMountingFeet).toBe(false);
  });

  it('refuses before the first response', () => {
    const settings = resetsSettings();
    settings.value();
    expect(settings.update({ resetMountingFeet: true })).toBeNull();
    expect(fake.sentOf(ChangeResetsSettingsRequestT)).toHaveLength(0);
  });

  it('refuses while stale', () => {
    const settings = loaded();
    fake.disconnect();
    expect(settings.update({ resetMountingFeet: true })).toBeNull();
    fake.connect();
    expect(settings.update({ resetMountingFeet: true })).toBeNull();
    expect(fake.sentOf(ChangeResetsSettingsRequestT)).toHaveLength(0);

    fake.push(new ResetsSettingsResponseT());
    expect(settings.update({ resetMountingFeet: true })).not.toBeNull();
  });
});

describe('defineKeyedSettings', () => {
  function mag() {
    return defineKeyedSettings({
      request: (trackerId: number) => new MagToggleRequestT(trackerId),
      key: (msg) => msg.trackerId,
      response: MagToggleResponseT,
      change: ChangeMagToggleRequestT,
    });
  }

  it('caches one settings per key and routes responses by key', () => {
    const settings = mag();
    expect(settings(0)).toBe(settings(0));
    settings(0).value();
    settings(7).value();
    expect(fake.sentOf(MagToggleRequestT).map((r) => r.trackerId)).toEqual([0, 7]);
    fake.push(new MagToggleResponseT(7, true));
    expect(settings(0).value()).toBeUndefined();
    expect(settings(7).value()?.enable).toBe(true);
    fake.push(new MagToggleResponseT(0, false));
    expect(settings(0).value()?.enable).toBe(false);
    expect(settings(7).value()?.enable).toBe(true);
  });

  it('a save on one key is confirmed only by its own response', async () => {
    const settings = mag();
    settings(0).value();
    fake.push(new MagToggleResponseT(0, false));
    const saved = settings(0).update({ enable: true });
    expect(fake.sentOf(ChangeMagToggleRequestT)[0]).toMatchObject({
      trackerId: 0,
      enable: true,
    });
    fake.push(new MagToggleResponseT(3, true));
    expect(settings(0).value()?.enable).toBe(true);
    fake.push(new MagToggleResponseT(0, true));
    await expect(saved).resolves.toBe('saved');
  });
});
