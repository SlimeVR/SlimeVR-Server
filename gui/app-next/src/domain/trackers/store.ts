import type {
  DataFeedUpdateT,
  DeviceDataT,
  DongleDataT,
  TrackerDataT,
} from 'solarxr-protocol';
import { createStore, reconcile } from 'solid-js/store';
import { toPlain, type Plain } from '@/solarxr/plain';
import { stepMovement, type MovementHistory } from './movement';

export type TrackerKey = `${number}:${number}`;
export type Tracker = Plain<TrackerDataT>;
export type TrackerInfo = NonNullable<Tracker['info']>;
export type Device = Omit<Plain<DeviceDataT>, 'trackers'>;
export type Dongle = Plain<DongleDataT>;

interface TrackersState {
  keys: TrackerKey[];
  trackers: Record<TrackerKey, Tracker>;
  devices: Record<number, Device>;
  dongleIds: number[];
  dongles: Record<number, Dongle>;
  movement: Record<TrackerKey, number>;
}

function emptyState(): TrackersState {
  return {
    keys: [],
    trackers: {},
    devices: {},
    dongleIds: [],
    dongles: {},
    movement: {},
  };
}

const [state, setState] = createStore<TrackersState>(emptyState());
const histories = new Map<TrackerKey, MovementHistory>();

export function trackerKey(deviceId: number, trackerId: number): TrackerKey {
  return `${deviceId}:${trackerId}`;
}

function measure(key: TrackerKey, tracker: Tracker, now: number) {
  if (!tracker.rotation) {
    histories.delete(key);
    return 0;
  }
  const { history, movement } = stepMovement(
    histories.get(key),
    tracker.rotation,
    tracker.linearAcceleration,
    now
  );
  histories.set(key, history);
  return movement;
}

// The only writer. `now` is the arrival time in ms, used by the movement window.
// Reconcile keeps the references of everything that did not change.
export function ingestTrackers(update: DataFeedUpdateT, now: number) {
  const next = emptyState();
  for (const device of update.devices) {
    const { trackers, ...rest } = toPlain(device);
    next.devices[rest.id] = rest;
    for (const tracker of trackers) {
      const key = trackerKey(tracker.deviceId, tracker.trackerId);
      next.keys.push(key);
      next.trackers[key] = tracker;
      next.movement[key] = measure(key, tracker, now);
    }
  }
  for (const key of histories.keys()) {
    if (!(key in next.trackers)) histories.delete(key);
  }
  for (const dongle of update.dongles) {
    next.dongleIds.push(dongle.id);
    next.dongles[dongle.id] = toPlain(dongle);
  }
  setState(reconcile(next));
}

// Tests and HMR
export function resetTrackers() {
  histories.clear();
  setState(reconcile(emptyState()));
}

export function trackerKeys(): readonly TrackerKey[] {
  return state.keys;
}

export function trackerByKey(key: TrackerKey): Tracker | undefined {
  return state.trackers[key];
}

export function trackerDevice(key: TrackerKey): Device | undefined {
  const tracker = state.trackers[key];
  return tracker ? state.devices[tracker.deviceId] : undefined;
}

export function dongleIds(): readonly number[] {
  return state.dongleIds;
}

export function dongleById(id: number): Dongle | undefined {
  return state.dongles[id];
}

// 0 when still, 1 when moving a lot, over the last MOVEMENT_WINDOW_MS
export function trackerMovement(key: TrackerKey): number {
  return state.movement[key] ?? 0;
}

export function maxMovement(keys: readonly TrackerKey[]): number {
  let max = 0;
  for (const key of keys) max = Math.max(max, trackerMovement(key));
  return max;
}
