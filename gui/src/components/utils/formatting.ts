import {
  BodyPart,
  StatusData,
  StatusDoublyAssignedBodyT,
  StatusMessageT,
  StatusTrackerResetT,
  TrackerDataT,
} from 'solarxr-protocol';

export const bodypartToString = (id: BodyPart) => BodyPart[id].replace(/_/g, ' ');

type Vector3 = { x: number; y: number; z: number };
export const formatVector3 = ({ x, y, z }: Vector3, precision = 0) =>
  `${x.toFixed(precision)} / ${y.toFixed(precision)} / ${z.toFixed(precision)}`;

/**
 * Convert an ASCII string to a number with it's bytes represented in little endian
 */
export function magic(strings: TemplateStringsArray): number {
  return (
    strings
      // joins strings
      .join('')
      // splits per character
      .split('')
      .reduce((prev, cur, i) => prev + (cur.charCodeAt(0) << (i * 8)), 0)
  );
}

export const doesntContainTrackerInfo: readonly StatusData[] = [StatusData.NONE];
export function trackerStatusRelated(
  tracker: TrackerDataT,
  status: StatusMessageT
): boolean {
  if (doesntContainTrackerInfo.includes(status.dataType)) {
    return false;
  }

  switch (status.dataType) {
    case StatusData.StatusDoublyAssignedBody: {
      const data = status.data as StatusDoublyAssignedBodyT;
      return data.trackerIds.some(
        (id) =>
          id.trackerNum === tracker.trackerId?.trackerNum &&
          id.deviceId?.id === tracker.trackerId.deviceId?.id
      );
    }
    case StatusData.StatusTrackerReset: {
      const data = status.data as StatusTrackerResetT;
      return (
        data.trackerId?.trackerNum == tracker.trackerId?.trackerNum &&
        data.trackerId?.deviceId?.id === tracker.trackerId?.deviceId?.id
      );
    }
    default:
      return false;
  }
}
