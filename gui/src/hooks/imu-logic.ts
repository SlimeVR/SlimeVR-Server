import { useMemo } from 'react';
import { FlatDeviceTracker } from './app';

export function useIsRestCalibrationTrackers(
  connectedTrackers: FlatDeviceTracker[]
): boolean {
  const imuExists = useMemo(
    () => connectedTrackers.some((tracker) => tracker.tracker.info?.isImu),
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
