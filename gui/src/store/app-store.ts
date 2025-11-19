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
import { FEET_BODY_PARTS, FINGER_BODY_PARTS } from '@/hooks/body-parts';

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

export const serverGuardsAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.serverGuards,
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

export const connectedTrackersAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);
  return trackers.filter(
    ({ tracker }) => tracker.status !== TrackerStatus.DISCONNECTED
  );
});

export const connectedIMUTrackersAtom = atom((get) => {
  const trackers = get(connectedTrackersAtom);
  return trackers.filter(({ tracker }) => tracker.info?.isImu);
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

export const feetAssignedTrackers = atom((get) =>
  get(assignedTrackersAtom).some(
    (t) => t.tracker.info?.bodyPart && FEET_BODY_PARTS.includes(t.tracker.info.bodyPart)
  )
);

export const fingerAssignedTrackers = atom((get) =>
  get(assignedTrackersAtom).some(
    (t) =>
      t.tracker.info?.bodyPart && FINGER_BODY_PARTS.includes(t.tracker.info.bodyPart)
  )
);
