import { useMemo } from 'react';
import { FlatDeviceTracker } from './app';
import { ImuType } from 'solarxr-protocol';

export function useIsRestCalibrationTrackers(
  connectedTrackers: FlatDeviceTracker[]
): boolean {
  const bnoExists = useMemo(
    () =>
      connectedTrackers.some(
        (tracker) =>
          tracker.tracker.info?.imuType &&
          [ImuType.BNO055, ImuType.BNO080, ImuType.BNO085].includes(
            tracker.tracker.info?.imuType
          )
      ),
    [connectedTrackers]
  );

  return bnoExists;
}

export function useRestCalibrationTrackers(
  connectedTrackers: FlatDeviceTracker[]
): FlatDeviceTracker[] {
  const restTrackers = useMemo(
    () =>
      connectedTrackers.filter(
        (tracker) =>
          tracker.tracker.info?.imuType &&
          [ImuType.BNO055, ImuType.BNO080, ImuType.BNO085].includes(
            tracker.tracker.info?.imuType
          )
      ),
    [connectedTrackers]
  );

  return restTrackers;
}
