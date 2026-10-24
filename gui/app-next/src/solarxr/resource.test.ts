import { createRoot, createEffect } from 'solid-js';
import { ResetsSettingsRequestT, ResetsSettingsResponseT } from 'solarxr-protocol';
import { beforeEach, describe, expect, it } from 'vitest';
import { setClient } from '@/solarxr/client';
import { defineResource } from '@/solarxr/resource';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';

let fake: FakeClient;

function resetsResource() {
  return defineResource({
    request: () => new ResetsSettingsRequestT(),
    response: ResetsSettingsResponseT,
  });
}

beforeEach(() => {
  fake = createFakeClient();
  setClient(fake);
});

describe('defineResource', () => {
  it('requests on first read only', () => {
    const resource = resetsResource();
    expect(fake.sentOf(ResetsSettingsRequestT)).toHaveLength(0);
    expect(resource.state()).toBe('never-loaded');
    resource.value();
    expect(fake.sentOf(ResetsSettingsRequestT)).toHaveLength(1);
  });

  it('becomes fresh from the response', () => {
    const resource = resetsResource();
    resource.value();
    const msg = new ResetsSettingsResponseT(true);
    fake.push(msg);
    expect(resource.state()).toBe('fresh');
    expect(resource.value()).toBe(msg);
  });

  it('waits for the connection before the first request', () => {
    fake = createFakeClient({ connected: false });
    setClient(fake);
    const resource = resetsResource();
    resource.value();
    expect(fake.sentOf(ResetsSettingsRequestT)).toHaveLength(0);
    fake.connect();
    expect(fake.sentOf(ResetsSettingsRequestT)).toHaveLength(1);
    expect(resource.state()).toBe('never-loaded');
  });

  it('keeps the value stale while disconnected and re-requests on reconnect', () => {
    const resource = resetsResource();
    resource.value();
    const msg = new ResetsSettingsResponseT(true);
    fake.push(msg);

    fake.disconnect();
    expect(resource.state()).toBe('stale');
    expect(resource.value()).toBe(msg);

    fake.connect();
    expect(resource.state()).toBe('stale');
    expect(fake.sentOf(ResetsSettingsRequestT)).toHaveLength(2);

    fake.push(new ResetsSettingsResponseT(false));
    expect(resource.state()).toBe('fresh');
    expect(resource.value()?.resetMountingFeet).toBe(false);
  });

  it('stays never-loaded across a disconnect without a value', () => {
    const resource = resetsResource();
    resource.value();
    fake.disconnect();
    expect(resource.state()).toBe('never-loaded');
  });

  it('does not make readers depend on the connection status', () => {
    const resource = resetsResource();
    let runs = 0;
    const dispose = createRoot((dispose) => {
      createEffect(() => {
        resource.value();
        runs++;
      });
      return dispose;
    });
    const before = runs;
    fake.disconnect();
    expect(runs).toBe(before);
    dispose();
  });
});
