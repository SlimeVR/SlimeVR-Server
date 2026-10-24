import { afterEach, describe, expect, it, vi } from 'vitest';
import * as v from 'valibot';

// Fresh module state per test: storage keeps definitions and settings at module level
async function setup(initial?: Record<string, unknown>) {
  vi.resetModules();
  const { setPlatform } = await import('@/lib/platform');
  const { fakePlatform } = await import('@/lib/platform-fake');
  const storage = await import('@/lib/storage');
  const platform = fakePlatform();
  if (initial)
    await platform.storage('settings').set('config.json', JSON.stringify(initial));
  setPlatform(platform);
  await storage.loadPrefs();
  return {
    storage,
    settings: platform.storage('settings'),
    cache: platform.storage('cache'),
  };
}

afterEach(() => {
  vi.useRealTimers();
  sessionStorage.clear();
});

describe('definePref', () => {
  it('validates stored values and falls back to the default', async () => {
    const { storage } = await setup({ valid: 5, invalid: 'nope' });
    expect(storage.definePref('valid', v.number(), 1)()).toBe(5);
    expect(storage.definePref('invalid', v.number(), 1)()).toBe(1);
    expect(storage.definePref('missing', v.number(), 1)()).toBe(1);
  });

  it('copies renamed old keys', async () => {
    const { storage } = await setup({ telemetry: { x: 1 } });
    const pref = storage.definePref('dongleTelemetry', v.object({ x: v.number() }), {
      x: 0,
    });
    expect(pref()).toEqual({ x: 1 });
  });

  it('updates memory synchronously and debounces the write', async () => {
    const { storage, settings } = await setup({ other: true });
    vi.useFakeTimers();
    const write = vi.spyOn(settings, 'set');
    const pref = storage.definePref('count', v.number(), 0);

    pref.set(2);
    pref.set(3);
    expect(pref()).toBe(3);
    expect(write).not.toHaveBeenCalled();

    await vi.advanceTimersByTimeAsync(100);
    expect(write).toHaveBeenCalledOnce();
    expect(JSON.parse(write.mock.calls[0]![1] as string)).toEqual({
      other: true,
      count: 3,
    });
  });

  it('flushPrefs writes immediately and cancels the pending write', async () => {
    const { storage, settings } = await setup();
    vi.useFakeTimers();
    const write = vi.spyOn(settings, 'set');
    const pref = storage.definePref('count', v.number(), 0);

    pref.set(1);
    await storage.flushPrefs();
    expect(write).toHaveBeenCalledOnce();
    await vi.advanceTimersByTimeAsync(100);
    expect(write).toHaveBeenCalledOnce();
  });

  it('rejects a key defined twice', async () => {
    const { storage } = await setup();
    storage.definePref('dup', v.number(), 0);
    expect(() => storage.definePref('dup', v.number(), 0)).toThrow();
  });
});

describe('defineCache', () => {
  it('expires entries after the ttl', async () => {
    const { storage, cache } = await setup();
    vi.useFakeTimers();
    vi.setSystemTime(0);
    const entry = storage.defineCache('releases', v.string(), 1000);

    await entry.set('a');
    expect(await entry.get()).toBe('a');
    vi.setSystemTime(1001);
    expect(await entry.get()).toBeNull();
    expect(await cache.get('releases')).toBeUndefined();
  });

  it('wrap loads once while fresh and keeps ids apart', async () => {
    const { storage } = await setup();
    const entry = storage.defineCache('deploy', v.string(), 1000);
    const load = vi.fn(async () => 'x');

    expect(await entry.wrap(load, '1.0')).toBe('x');
    expect(await entry.wrap(load, '1.0')).toBe('x');
    expect(load).toHaveBeenCalledOnce();
    expect(await entry.get('2.0')).toBeNull();
  });

  it('treats an invalid cached value as a miss', async () => {
    const { storage, cache } = await setup();
    await cache.set('n', JSON.stringify({ value: 'str', expiry: 0 }));
    expect(await storage.defineCache('n', v.number()).get()).toBeNull();
  });
});

describe('defineSession', () => {
  it('survives a module reload', async () => {
    const first = await setup();
    first.storage.defineSession('mode', v.picklist(['off', 'on']), 'off').set('on');

    const second = await setup();
    expect(
      second.storage.defineSession('mode', v.picklist(['off', 'on']), 'off')()
    ).toBe('on');
  });
});
