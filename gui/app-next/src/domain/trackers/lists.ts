import { BodyPart, TrackerStatus } from 'solarxr-protocol';
import { createMemo, createRoot } from 'solid-js';
import { trackerByKey, trackerKeys, type Tracker, type TrackerKey } from './store';

function sameKeys(a: readonly TrackerKey[], b: readonly TrackerKey[]) {
  return a.length === b.length && a.every((key, i) => key === b[i]);
}

function bodyPartOf(tracker: Tracker) {
  return tracker.info?.bodyPart ?? BodyPart.NONE;
}

function filtered(keep: (tracker: Tracker) => boolean) {
  return createMemo(
    () =>
      trackerKeys().filter((key) => {
        const tracker = trackerByKey(key);
        return tracker !== undefined && keep(tracker);
      }),
    [],
    { equals: sameKeys }
  );
}

const lists = createRoot(() => ({
  assigned: filtered((t) => bodyPartOf(t) !== BodyPart.NONE),
  unassigned: filtered((t) => bodyPartOf(t) === BodyPart.NONE),
  connected: filtered((t) => t.status !== TrackerStatus.DISCONNECTED),
  connectedImu: filtered(
    (t) => t.status !== TrackerStatus.DISCONNECTED && t.info?.isImu === true
  ),
}));

export function assignedTrackerKeys(): readonly TrackerKey[] {
  return lists.assigned();
}

export function unassignedTrackerKeys(): readonly TrackerKey[] {
  return lists.unassigned();
}

export function connectedTrackerKeys(): readonly TrackerKey[] {
  return lists.connected();
}

export function connectedImuTrackerKeys(): readonly TrackerKey[] {
  return lists.connectedImu();
}
