import { atom } from 'jotai';
import {
  BodyPart,
  BoneT,
  DataFeedUpdateT,
  DeviceDataT,
  DeviceOrigin,
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

// Keyed by BodyPart, resolved from each Bone's registry ID as it arrives (see useProvideAppContext).
export const bonesAtom = atom<Map<BodyPart, BoneT>>(new Map());
export const boneRegistryAtom = atom<Map<number, BodyPart>>(new Map());

export function bodyPartOfBone(
  registry: Map<number, BodyPart>,
  boneId: number | undefined
): BodyPart {
  return (boneId != null ? registry.get(boneId) : undefined) ?? BodyPart.NONE;
}

// Inverse of boneRegistryAtom, derived so the two can never drift out of sync.
// BodyPart.NONE is excluded so it never resolves back to a real bone id.
export const boneIdRegistryAtom = atom((get) => {
  const registry = get(boneRegistryAtom);
  const inverse = new Map<BodyPart, number>();
  for (const [boneId, bodyPart] of registry) {
    if (bodyPart === BodyPart.NONE) continue;
    inverse.set(bodyPart, boneId);
  }
  return inverse;
});

export function boneIdOfBodyPart(
  idRegistry: Map<BodyPart, number>,
  bodyPart: BodyPart
): number | undefined {
  return idRegistry.get(bodyPart);
}

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
  | { kind: 'driver' }
);

export function groupTrackersByConnection(
  trackers: FlatDeviceTracker[],
  dongles: DongleDataT[],
  boneRegistry: Map<number, BodyPart>
): TrackerConnectionGroup[] {
  const dongleByDeviceId = new Map<number, DongleDataT>(
    dongles.flatMap((dongle) => dongle.devicesIds.map((id) => [id, dongle]))
  );

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

  const driverGroup: Extract<TrackerConnectionGroup, { kind: 'driver' }> = {
    key: 'driver',
    kind: 'driver',
    assigned: [],
    unassigned: [],
  };

  const getGroup = (flatTracker: FlatDeviceTracker): TrackerConnectionGroup => {
    if (flatTracker.tracker.origin == DeviceOrigin.DRIVER) {
      return driverGroup;
    }

    // 2. Dongle check
    const deviceId = flatTracker.device?.id;
    const dongle = deviceId != null ? dongleByDeviceId.get(deviceId) : undefined;

    if (!dongle) {
      return wifiGroup;
    }

    let dongleGroup = dongleGroups.get(dongle.id);
    if (!dongleGroup) {
      dongleGroup = {
        key: `dongle-${dongle.id}`,
        kind: 'dongle',
        dongleId: dongle.id,
        dongleName:
          dongle.customName?.toString() || dongle.displayName?.toString() || null,
        status: dongle.status,
        assigned: [],
        unassigned: [],
      };
      dongleGroups.set(dongle.id, dongleGroup);
    }

    return dongleGroup;
  };

  for (const flatTracker of trackers) {
    const group = getGroup(flatTracker);
    const isUnassigned =
      bodyPartOfBone(boneRegistry, flatTracker.tracker.info?.boneId) === BodyPart.NONE;
    const targetList = isUnassigned ? group.unassigned : group.assigned;

    targetList.push(flatTracker);
  }

  const isNotEmpty = (group: TrackerConnectionGroup) =>
    group.assigned.length > 0 || group.unassigned.length > 0;

  const getTrackerCount = (group: TrackerConnectionGroup) =>
    group.assigned.length + group.unassigned.length;

  const activeGroups = [
    ...dongleGroups.values(),
    ...(isNotEmpty(wifiGroup) ? [wifiGroup] : []),
    ...(isNotEmpty(driverGroup) ? [driverGroup] : []),
  ];

  return activeGroups.sort((a, b) => {
    // Driver group always goes last
    if (a.kind === 'driver') return 1;
    if (b.kind === 'driver') return -1;

    // Otherwise sort descending by total tracker count
    return getTrackerCount(b) - getTrackerCount(a);
  });
}

export function groupTrackersByDevice(
  trackers: FlatDeviceTracker[]
): FlatDeviceTracker[][] {
  const order: number[] = [];
  const byDevice = new Map<number, FlatDeviceTracker[]>();
  trackers.forEach((td) => {
    const key = td.device?.id ?? td.tracker.trackerId;
    if (!byDevice.has(key)) {
      order.push(key);
      byDevice.set(key, []);
    }
    byDevice.get(key)!.push(td);
  });
  return order.map((key) => byDevice.get(key)!);
}

export function groupTrackerByBodyPart(
  trackers: FlatDeviceTracker[],
  boneRegistry: Map<number, BodyPart>
): Partial<Record<BodyPart, FlatDeviceTracker>> {
  const byPart: Partial<Record<BodyPart, FlatDeviceTracker>> = {};
  trackers.forEach((td) => {
    byPart[bodyPartOfBone(boneRegistry, td.tracker.info?.boneId)] = td;
  });
  return byPart;
}

export const flatTrackersAtom = atom((get) => {
  const devices = get(devicesAtom);

  return devices.flatMap<FlatDeviceTracker>((device) =>
    device.trackers.map((tracker) => ({ tracker, device }))
  );
});

export const assignedTrackersAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);
  const boneRegistry = get(boneRegistryAtom);
  return trackers.filter(
    ({ tracker }) =>
      bodyPartOfBone(boneRegistry, tracker.info?.boneId) !== BodyPart.NONE
  );
});

export const trackerByBodyPartAtom = atom((get) =>
  groupTrackerByBodyPart(get(assignedTrackersAtom), get(boneRegistryAtom))
);

const assignedRolesRawAtom = atom((get) => {
  const boneRegistry = get(boneRegistryAtom);
  return get(assignedTrackersAtom).map(({ tracker }) =>
    bodyPartOfBone(boneRegistry, tracker.info?.boneId)
  );
});

export const assignedRolesAtom = selectAtom(
  assignedRolesRawAtom,
  (parts) => parts,
  (a, b) => a.length === b.length && a.every((part, i) => part === b[i])
);

export const unassignedTrackersAtom = atom((get) => {
  const trackers = get(flatTrackersAtom);
  const boneRegistry = get(boneRegistryAtom);
  return trackers.filter(
    ({ tracker }) =>
      bodyPartOfBone(boneRegistry, tracker.info?.boneId) === BodyPart.NONE
  );
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
