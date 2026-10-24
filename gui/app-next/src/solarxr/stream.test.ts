import { createRoot, createSignal } from 'solid-js';
import {
  StartTrackerImuTelemetryRequestT,
  StopTrackerImuTelemetryRequestT,
} from 'solarxr-protocol';
import { beforeEach, describe, expect, it } from 'vitest';
import { setClient } from '@/solarxr/client';
import { defineStream } from '@/solarxr/stream';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';

let fake: FakeClient;

function imuStream() {
  return defineStream<number>({
    start: (trackerId) => new StartTrackerImuTelemetryRequestT(trackerId),
    stop: () => new StopTrackerImuTelemetryRequestT(),
  });
}

function started() {
  return fake.sentOf(StartTrackerImuTelemetryRequestT).map((msg) => msg.trackerId);
}

function stops() {
  return fake.sentOf(StopTrackerImuTelemetryRequestT).length;
}

beforeEach(() => {
  fake = createFakeClient();
  setClient(fake);
});

describe('defineStream', () => {
  it('starts on the first subscriber and stops after the last', () => {
    const stream = imuStream();
    const a = stream.subscribe(1);
    const b = stream.subscribe(1);
    expect(started()).toEqual([1]);
    a();
    expect(stops()).toBe(0);
    b();
    expect(stops()).toBe(1);
  });

  it('counts keys separately', () => {
    const stream = imuStream();
    stream.subscribe(1);
    stream.subscribe(2);
    expect(started()).toEqual([1, 2]);
  });

  it('ignores a second unsubscribe', () => {
    const stream = imuStream();
    const a = stream.subscribe(1);
    stream.subscribe(1);
    a();
    a();
    expect(stops()).toBe(0);
  });

  it('restarts active keys on reconnect', () => {
    const stream = imuStream();
    stream.subscribe(1);
    stream.subscribe(2)();
    fake.disconnect();
    fake.connect();
    expect(started()).toEqual([1, 2, 1]);
  });

  it('starts on connect when subscribed while disconnected', () => {
    fake = createFakeClient({ connected: false });
    setClient(fake);
    const stream = imuStream();
    stream.subscribe(3);
    expect(started()).toEqual([]);
    fake.connect();
    expect(started()).toEqual([3]);
  });

  it('follows the key and unsubscribes on cleanup in components', async () => {
    const stream = imuStream();
    const [id, setId] = createSignal(1);
    const dispose = createRoot((dispose) => {
      stream.use(id);
      return dispose;
    });
    await Promise.resolve();
    expect(started()).toEqual([1]);
    setId(2);
    await Promise.resolve();
    expect(started()).toEqual([1, 2]);
    expect(stops()).toBe(1);
    dispose();
    expect(stops()).toBe(2);
  });
});
