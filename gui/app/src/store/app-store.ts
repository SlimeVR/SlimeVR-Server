import { atom } from 'jotai';
import {
  BodyPart,
  BoneT,
  DataFeedUpdateT,
  DeviceDataT,
  DongleDataT,
  DongleStatus,
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

export const serverGuardsAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.serverGuards,
  isEqual
);

export const donglesAtom = selectAtom(
  datafeedAtom,
  (datafeed) => datafeed.dongles,
  isEqual
);

export type TrackerConnectionGroup = {
  key: string;
  assigned: FlatDeviceTracker[];
  unassigned: FlatDeviceTracker[];
} & (
  | {
      kind: 'dongle';
      dongleId: number;
      dongleName: string | null;
      status: DongleStatus;
    }
  | { kind: 'wifi' }
);

export function groupTrackersByConnection(
  trackers: FlatDeviceTracker[],
  dongles: DongleDataT[]
): TrackerConnectionGroup[] {
  const dongleByDeviceId = new Map<number, DongleDataT>();
  for (const dongle of dongles) {
    for (const deviceId of dongle.devicesIds) {
      dongleByDeviceId.set(deviceId, dongle);
    }
  }

  const dongleGroups = new Map<
    number,
    Extract<TrackerConnectionGroup, { kind: 'dongle' }>
  >();
  const wifiGroup: Extract<TrackerConnectionGroup, { kind: 'wifi' }> = {
    key: 'wifi',
    kind: 'wifi',
    assigned: [],
    unassigned: [],
  };

  for (const flatTracker of trackers) {
    const dongle =
      flatTracker.device?.id != null
        ? dongleByDeviceId.get(flatTracker.device.id)
        : undefined;

    let group: TrackerConnectionGroup = wifiGroup;
    if (dongle) {
      const dongleGroup = dongleGroups.get(dongle.id) ?? {
        key: `dongle-${dongle.id}`,
        kind: 'dongle' as const,
        dongleId: dongle.id,
        dongleName: dongle.displayName?.toString() ?? null,
        status: dongle.status,
        assigned: [],
        unassigned: [],
      };
      dongleGroups.set(dongle.id, dongleGroup);
      group = dongleGroup;
    }

    if (flatTracker.tracker.info?.bodyPart === BodyPart.NONE) {
      group.unassigned.push(flatTracker);
    } else {
      group.assigned.push(flatTracker);
    }
  }

  const groups: TrackerConnectionGroup[] = [...dongleGroups.values()];

  if (wifiGroup.assigned.length > 0 || wifiGroup.unassigned.length > 0) {
    groups.push(wifiGroup);
  }

  return groups;
}

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
          tracker?.trackerId == trackerNum &&
          tracker?.deviceId == deviceId
      )
    ),
    (a) => a,
    isEqual
  );
