import { atom } from 'jotai';
import { BodyPart, DataFeedUpdateT, DeviceDataT, TrackerDataT, TrackerStatus } from 'solarxr-protocol';
import { selectAtom } from 'jotai/utils';
import { isEqual } from '@react-hookz/deep-equal';


export interface FlatDeviceTracker {
  device?: DeviceDataT;
  tracker: TrackerDataT;
}

console.log('APP STORE INIT');

export const ignoredTrackersAtom = atom(new Set());

export const datafeedAtom = atom(new DataFeedUpdateT());

export const devicesAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.devices,
  isEqual
)

export const flatTrackersAtom = atom((get) => {
  const devices = get(devicesAtom);

  return devices.reduce<FlatDeviceTracker[]>(
    (curr, device) => [
      ...curr,
      ...device.trackers.map((tracker) => ({ tracker, device })),
    ],
    []
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
)

export const bonesAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.bones,
  isEqual
)

export const hasHMDTrackerAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);

  return trackers.some(
    (tracker) =>
      tracker.tracker.info?.bodyPart === BodyPart.HEAD &&
      (tracker.tracker.info.isHmd || tracker.tracker.position?.y)
  );
});

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
