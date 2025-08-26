import { atom } from 'jotai';
import {
  BodyPart,
  BoneT,
  DataFeedUpdateT,
  DeviceDataT,
  TrackerDataT,
  TrackerStatus,
} from 'solarxr-protocol';
import { selectAtom } from 'jotai/utils';
import { isEqual } from '@react-hookz/deep-equal';

export interface FlatDeviceTracker {
  device?: DeviceDataT;
  tracker: TrackerDataT;
}

export const ignoredTrackersAtom = atom(new Set<string>());

export const datafeedAtom = atom(new DataFeedUpdateT());

export const bonesAtom = atom<BoneT[]>([]);

export const devicesAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.devices,
  isEqual
);

export const flatTrackersAtom = atom((get) => {
  const devices = get(devicesAtom);

  return devices.flatMap<FlatDeviceTracker>((device) =>
    device.trackers.map((tracker) => ({ tracker, device }))
  );
});

export const assignedTrackersAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);
  return trackers.filter(({ tracker }) => tracker.info?.bodyPart !== BodyPart.NONE);
});

export const unassignedTrackersAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);
  return trackers.filter(({ tracker }) => tracker.info?.bodyPart === BodyPart.NONE);
});

export const connectedIMUTrackersAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);
  return trackers.filter(
    ({ tracker }) =>
      tracker.status !== TrackerStatus.DISCONNECTED && tracker.info?.isImu
  );
});

export const computedTrackersAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.syntheticTrackers.map((tracker) => ({ tracker })),
  isEqual
);

export const hasHMDTrackerAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);

  return trackers.some(
    (tracker) =>
      tracker.tracker.info?.bodyPart === BodyPart.HEAD &&
      (tracker.tracker.info.isHmd || tracker.tracker.position?.y !== undefined)
  );
});

export const stayAlignedPoseAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.stayAlignedPose,
  isEqual
);

export const trackerFromIdAtom = ({
  trackerNum,
  deviceId,
}: {
  trackerNum: string | number | undefined;
  deviceId: string | number | undefined;
}) =>
  selectAtom(
    atom((get) =>
      get(flatTrackersAtom).find(
        ({ tracker }) =>
          trackerNum &&
          deviceId &&
          tracker?.trackerId?.trackerNum == trackerNum &&
          tracker?.trackerId?.deviceId?.id == deviceId
      )
    ),
    (a) => a,
    isEqual
  );

export const FEET_BODY_PARTS = [BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT];
export const FINGER_BODY_PARTS = new Set([
  BodyPart.LEFT_THUMB_METACARPAL,
  BodyPart.LEFT_THUMB_PROXIMAL,
  BodyPart.LEFT_THUMB_DISTAL,
  BodyPart.LEFT_INDEX_PROXIMAL,
  BodyPart.LEFT_INDEX_INTERMEDIATE,
  BodyPart.LEFT_INDEX_DISTAL,
  BodyPart.LEFT_MIDDLE_PROXIMAL,
  BodyPart.LEFT_MIDDLE_INTERMEDIATE,
  BodyPart.LEFT_MIDDLE_DISTAL,
  BodyPart.LEFT_RING_PROXIMAL,
  BodyPart.LEFT_RING_INTERMEDIATE,
  BodyPart.LEFT_RING_DISTAL,
  BodyPart.LEFT_LITTLE_PROXIMAL,
  BodyPart.LEFT_LITTLE_INTERMEDIATE,
  BodyPart.LEFT_LITTLE_DISTAL,
  BodyPart.RIGHT_THUMB_METACARPAL,
  BodyPart.RIGHT_THUMB_PROXIMAL,
  BodyPart.RIGHT_THUMB_DISTAL,
  BodyPart.RIGHT_INDEX_PROXIMAL,
  BodyPart.RIGHT_INDEX_INTERMEDIATE,
  BodyPart.RIGHT_INDEX_DISTAL,
  BodyPart.RIGHT_MIDDLE_PROXIMAL,
  BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
  BodyPart.RIGHT_MIDDLE_DISTAL,
  BodyPart.RIGHT_RING_PROXIMAL,
  BodyPart.RIGHT_RING_INTERMEDIATE,
  BodyPart.RIGHT_RING_DISTAL,
  BodyPart.RIGHT_LITTLE_PROXIMAL,
  BodyPart.RIGHT_LITTLE_INTERMEDIATE,
  BodyPart.RIGHT_LITTLE_DISTAL,
]);

export const fingerAssignedTrackers = atom((get) =>
  get(assignedTrackersAtom).some(
    (t) => t.tracker.info?.bodyPart && FINGER_BODY_PARTS.has(t.tracker.info.bodyPart)
  )
);

export const feetAssignedTrackers = atom((get) =>
  get(assignedTrackersAtom).some(
    (t) => t.tracker.info?.bodyPart && FEET_BODY_PARTS.includes(t.tracker.info.bodyPart)
  )
);
