import { useMemo } from 'react';
import { FlatDeviceTracker } from './app';
import { ImuType } from 'solarxr-protocol';

export function useBnoExists(connectedTrackers: FlatDeviceTracker[]): boolean {
  const bnoExists = useMemo(
    () =>
      connectedTrackers.some(
        (tracker) =>
          tracker.tracker.info?.imuType &&
          [ImuType.BNO055, ImuType.BNO080, ImuType.BNO085, ImuType.LSM6DSV].includes(
            tracker.tracker.info?.imuType
          )
      ),
    [connectedTrackers]
  );

  return bnoExists;
}
