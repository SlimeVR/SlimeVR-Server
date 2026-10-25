import {
  BodyPart,
  BoneT,
  DataFeedUpdateT,
  DeviceDataT,
  ServerGuardsT,
  StartDataFeedT,
  TrackerDataT,
} from 'solarxr-protocol';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { debugPref, devSettingsPref } from '@/domain/developer';
import { resetTrackers, trackerKeys } from '@/domain/trackers';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { setClient } from '@/solarxr/client';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';
import { BONES_FEED, TRACKERS_FEED } from './config';
import {
  boneByPart,
  bones,
  resetDatafeed,
  serverGuards,
  startDatafeed,
} from './datafeed';

let fake: FakeClient;
let stop: () => void;

function trackersUpdate() {
  const tracker = new TrackerDataT();
  tracker.deviceId = 1;
  const device = new DeviceDataT();
  device.id = 1;
  device.trackers = [tracker];
  const update = new DataFeedUpdateT();
  update.index = TRACKERS_FEED;
  update.devices = [device];
  update.serverGuards = new ServerGuardsT();
  return update;
}

beforeEach(() => {
  setPlatform(fakePlatform());
  resetTrackers();
  resetDatafeed();
  debugPref.set(false);
  fake = createFakeClient({ connected: false });
  setClient(fake);
  stop = startDatafeed();
});

afterEach(() => {
  stop();
});

describe('startDatafeed', () => {
  it('starts both feeds on every connect', () => {
    expect(fake.sentOf(StartDataFeedT)).toHaveLength(0);
    fake.connect();
    const [start] = fake.sentOf(StartDataFeedT);
    expect(start?.dataFeeds).toHaveLength(2);
    expect(start?.dataFeeds[TRACKERS_FEED]?.minimumTimeSinceLast).toBe(100);
    expect(start?.dataFeeds[BONES_FEED]?.boneMask?.orientation).toBe(true);

    fake.disconnect();
    fake.connect();
    expect(fake.sentOf(StartDataFeedT)).toHaveLength(2);
  });

  it('restarts at the fast rate only with debug and the dev setting on', () => {
    fake.connect();
    devSettingsPref.set({ ...devSettingsPref(), fastDataFeed: true });
    expect(fake.sentOf(StartDataFeedT)).toHaveLength(1);
    debugPref.set(true);
    const starts = fake.sentOf(StartDataFeedT);
    expect(starts).toHaveLength(2);
    expect(starts[1]?.dataFeeds[TRACKERS_FEED]?.minimumTimeSinceLast).toBeCloseTo(
      1000 / 90
    );
    devSettingsPref.set({ ...devSettingsPref(), fastDataFeed: false });
  });

  it('routes updates by feed index', () => {
    fake.connect();
    fake.push(trackersUpdate());
    expect(trackerKeys()).toEqual(['1:0']);
    expect(serverGuards()?.canDoUserHeightCalibration).toBe(false);

    const bone = new BoneT();
    bone.bodyPart = BodyPart.HEAD;
    const update = new DataFeedUpdateT();
    update.index = BONES_FEED;
    update.bones = [bone];
    fake.push(update);
    expect(bones()).toHaveLength(1);
    expect(boneByPart(BodyPart.HEAD)?.bodyPart).toBe(BodyPart.HEAD);
    expect(trackerKeys()).toEqual(['1:0']);
  });

  it('stops listening once stopped', () => {
    stop();
    fake.connect();
    fake.push(trackersUpdate());
    expect(fake.sentOf(StartDataFeedT)).toHaveLength(0);
    expect(trackerKeys()).toEqual([]);
  });
});
