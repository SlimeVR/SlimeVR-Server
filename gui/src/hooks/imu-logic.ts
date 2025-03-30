import { useMemo } from 'react';
import { FlatDeviceTracker } from './app';

const IGNORED_BOARDS = new Set(['Sony Mocopi', 'Haritora']);

export function useIsRestCalibrationTrackers(
  connectedTrackers: FlatDeviceTracker[]
): boolean {
  const imuExists = useMemo(
    () =>
      connectedTrackers.some(
        (tracker) =>
          tracker.tracker.info?.isImu &&
          !(
            tracker.device?.hardwareInfo?.boardType &&
            IGNORED_BOARDS.has(tracker.device?.hardwareInfo?.boardType as string)
          )
      ),
    [connectedTrackers]
  );

  return imuExists;
}

export function useRestCalibrationTrackers(
  connectedTrackers: FlatDeviceTracker[]
): FlatDeviceTracker[] {
  const restTrackers = useMemo(
    () => connectedTrackers.filter((tracker) => tracker.tracker.info?.isImu),
    [connectedTrackers]
  );

  return restTrackers;
}
