import {
  BodyPart,
  DataFeedUpdateT,
  DeviceDataT,
  DongleDataT,
  QuatT,
  TrackerDataT,
  TrackerInfoT,
  TrackerStatus,
} from 'solarxr-protocol';
import { createEffect, createRoot } from 'solid-js';
import { beforeEach, describe, expect, it } from 'vitest';
import { createBundle, setL10nBundles } from '@/lib/l10n';
import { toPlain } from '@/solarxr/plain';
import {
  assignedTrackerKeys,
  connectedImuTrackerKeys,
  connectedTrackerKeys,
  dongleById,
  dongleIds,
  ingestTrackers,
  resetTrackers,
  toEulerDegrees,
  trackerByKey,
  trackerDevice,
  trackerKey,
  trackerKeys,
  trackerMovement,
  trackerName,
  unassignedTrackerKeys,
} from '.';

interface TrackerSpec {
  bodyPart?: BodyPart;
  status?: TrackerStatus;
  yaw?: number;
  isImu?: boolean;
}

function tracker(deviceId: number, trackerId: number, spec: TrackerSpec = {}) {
  const info = new TrackerInfoT();
  info.bodyPart = spec.bodyPart ?? BodyPart.NONE;
  info.displayName = 'IMU';
  info.isImu = spec.isImu ?? true;
  const data = new TrackerDataT();
  data.deviceId = deviceId;
  data.trackerId = trackerId;
  data.info = info;
  data.status = spec.status ?? TrackerStatus.OK;
  const half = ((spec.yaw ?? 0) * Math.PI) / 360;
  data.rotation = new QuatT(0, Math.sin(half), 0, Math.cos(half));
  return data;
}

function feed(devices: [number, TrackerDataT[]][], dongles: DongleDataT[] = []) {
  const update = new DataFeedUpdateT();
  update.devices = devices.map(([id, trackers]) => {
    const device = new DeviceDataT();
    device.id = id;
    device.customName = `device ${id}`;
    device.trackers = trackers;
    return device;
  });
  update.dongles = dongles;
  return update;
}

beforeEach(() => {
  resetTrackers();
});

describe('ingestTrackers', () => {
  it('normalizes devices and trackers by key, in feed order', () => {
    ingestTrackers(
      feed([
        [2, [tracker(2, 0), tracker(2, 1)]],
        [1, [tracker(1, 0)]],
      ]),
      0
    );
    expect(trackerKeys()).toEqual(['2:0', '2:1', '1:0']);
    expect(trackerByKey(trackerKey(2, 1))?.trackerId).toBe(1);
    expect(trackerDevice('1:0')?.customName).toBe('device 1');
    expect(trackerDevice('1:0')).not.toHaveProperty('trackers');
    expect(trackerByKey('3:0')).toBeUndefined();
  });

  it('keeps references of unchanged data across ticks', () => {
    ingestTrackers(feed([[1, [tracker(1, 0, { yaw: 0 })]]]), 0);
    const before = trackerByKey('1:0');
    const info = before?.info;
    const device = trackerDevice('1:0');

    ingestTrackers(feed([[1, [tracker(1, 0, { yaw: 10 })]]]), 100);
    const after = trackerByKey('1:0');
    expect(after).toBe(before);
    expect(after?.info).toBe(info);
    expect(trackerDevice('1:0')).toBe(device);
    expect(after?.rotation?.y).toBeCloseTo(Math.sin((10 * Math.PI) / 360));
  });

  it('only notifies readers of fields that changed', () => {
    ingestTrackers(feed([[1, [tracker(1, 0)]]]), 0);
    let infoRuns = 0;
    let listRuns = 0;
    const dispose = createRoot((d) => {
      createEffect(() => {
        void trackerByKey('1:0')?.info?.displayName;
        infoRuns++;
      });
      createEffect(() => {
        void assignedTrackerKeys();
        listRuns++;
      });
      return d;
    });
    ingestTrackers(feed([[1, [tracker(1, 0, { yaw: 20 })]]]), 100);
    ingestTrackers(feed([[1, [tracker(1, 0, { yaw: 40 })]]]), 200);
    expect(infoRuns).toBe(1);
    expect(listRuns).toBe(1);
    dispose();
  });

  it('drops trackers and devices that left the feed', () => {
    ingestTrackers(
      feed([
        [1, [tracker(1, 0)]],
        [2, [tracker(2, 0)]],
      ]),
      0
    );
    ingestTrackers(feed([[2, [tracker(2, 0)]]]), 100);
    expect(trackerKeys()).toEqual(['2:0']);
    expect(trackerByKey('1:0')).toBeUndefined();
    expect(trackerDevice('2:0')?.id).toBe(2);
  });

  it('measures movement per tracker and restarts it after a tracker leaves', () => {
    ingestTrackers(feed([[1, [tracker(1, 0)]]]), 0);
    ingestTrackers(feed([[1, [tracker(1, 0, { yaw: 30 })]]]), 100);
    expect(trackerMovement('1:0')).toBeGreaterThan(0);
    ingestTrackers(feed([]), 200);
    expect(trackerMovement('1:0')).toBe(0);
    ingestTrackers(feed([[1, [tracker(1, 0, { yaw: 90 })]]]), 300);
    expect(trackerMovement('1:0')).toBe(0);
  });

  it('stores dongles by id', () => {
    const dongle = new DongleDataT();
    dongle.id = 7;
    dongle.displayName = 'Dongle';
    dongle.devicesIds = [1];
    ingestTrackers(feed([], [dongle]), 0);
    expect(dongleIds()).toEqual([7]);
    expect(dongleById(7)?.displayName).toBe('Dongle');
  });
});

describe('tracker lists', () => {
  it('splits assigned, unassigned, connected and IMU trackers', () => {
    ingestTrackers(
      feed([
        [
          1,
          [
            tracker(1, 0, { bodyPart: BodyPart.HIP }),
            tracker(1, 1, { status: TrackerStatus.DISCONNECTED }),
            tracker(1, 2, { isImu: false }),
          ],
        ],
      ]),
      0
    );
    expect(assignedTrackerKeys()).toEqual(['1:0']);
    expect(unassignedTrackerKeys()).toEqual(['1:1', '1:2']);
    expect(connectedTrackerKeys()).toEqual(['1:0', '1:2']);
    expect(connectedImuTrackerKeys()).toEqual(['1:0']);
  });
});

describe('trackerName', () => {
  beforeEach(() => {
    setL10nBundles([
      createBundle('en', 'body_part-HIP = Hip\ntracker-unnamed = Unnamed tracker'),
    ]);
  });

  function info(fields: Partial<TrackerInfoT>) {
    return toPlain(Object.assign(new TrackerInfoT(), fields));
  }

  it('falls back from custom name to body part, display name, then unnamed', () => {
    expect(trackerName(info({ customName: 'Mine', bodyPart: BodyPart.HIP }))).toBe(
      'Mine'
    );
    expect(trackerName(info({ bodyPart: BodyPart.HIP, displayName: 'IMU' }))).toBe(
      'Hip'
    );
    expect(trackerName(info({ displayName: 'IMU' }))).toBe('IMU');
    expect(trackerName(null)).toBe('Unnamed tracker');
  });
});

describe('toEulerDegrees', () => {
  it('reads yaw around Y and zero for no rotation', () => {
    const half = Math.PI / 4;
    expect(
      toEulerDegrees({ x: 0, y: Math.sin(half), z: 0, w: Math.cos(half) }).y
    ).toBeCloseTo(90);
    expect(toEulerDegrees(null)).toEqual({ x: 0, y: 0, z: 0 });
  });
});
