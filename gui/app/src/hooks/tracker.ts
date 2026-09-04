import { CSSProperties, useEffect, useMemo, useRef, useState } from 'react';
import { BodyPart, TrackerDataT, TrackerInfoT } from 'solarxr-protocol';
import { QuaternionFromQuatT, QuaternionToEulerDegrees } from '@/maths/quaternion';
import { ReactLocalization, useLocalization } from '@fluent/react';
import { useDataFeedConfig } from './datafeed-config';
import { Quaternion, Vector3 } from 'three';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { useAtomValue } from 'jotai';
import { trackerFromIdAtom } from '@/store/app-store';

export const getLocalizedTrackerName = (
  l10n: ReactLocalization,
  info: TrackerInfoT | null
) => {
  if (info?.customName) return info?.customName;
  if (info?.bodyPart) return l10n.getString('body_part-' + BodyPart[info?.bodyPart]);
  return info?.displayName || 'NONE';
};

export function getTrackerName(info: TrackerInfoT | null): string {
  return (info?.customName ?? info?.displayName)?.toString() ?? '';
}

export const velocityGlowStyle = (velocity: number): CSSProperties => {
  const spread = Math.floor(velocity * 8);
  return {
    boxShadow: `0px 0px ${spread}px ${spread}px rgb(var(--accent-background-30))`,
  };
};

export const useTracker = (tracker: TrackerDataT) => {
  const { l10n } = useLocalization();

  return {
    useName: () =>
      useMemo(() => getLocalizedTrackerName(l10n, tracker.info), [tracker.info, l10n]),
    useRawRotationEulerDegrees: () =>
      useMemo(() => QuaternionToEulerDegrees(tracker?.rotation), [tracker.rotation]),
    useRefAdjRotationEulerDegrees: () =>
      useMemo(
        () =>
          tracker?.rotationReferenceAdjusted &&
          QuaternionToEulerDegrees(tracker?.rotationReferenceAdjusted),
        [tracker.rotationReferenceAdjusted]
      ),
    useIdentAdjRotationEulerDegrees: () =>
      useMemo(
        () =>
          tracker?.rotationIdentityAdjusted &&
          QuaternionToEulerDegrees(tracker?.rotationIdentityAdjusted),
        [tracker.rotationIdentityAdjusted]
      ),
    useVelocity: () => useVelocity(tracker),
  };
};

export const useVelocity = (tracker?: TrackerDataT): number => {
  const trackers = useMemo(() => (tracker ? [tracker] : []), [tracker]);

  return useTrackersVelocity(trackers);
};

export const useTrackersVelocity = (trackers: TrackerDataT[]): number => {
  const { feedMaxTps } = useDataFeedConfig();
  const previous = useRef<
    Record<
      number,
      {
        rot: Quaternion;
        acc: Vector3;
        deltas: number[];
      }
    >
  >({});
  const [velocity, setVelocity] = useState<number>(0);

  useEffect(() => {
    const trackerIds = new Set(trackers.map((tracker) => tracker.trackerId));
    Object.keys(previous.current).forEach((trackerId) => {
      if (!trackerIds.has(Number(trackerId))) {
        delete previous.current[Number(trackerId)];
      }
    });

    const velocities = trackers.map((tracker) => {
      if (!tracker.rotation) return 0;

      const trackerId = tracker.trackerId;
      previous.current[trackerId] ??= {
        rot: QuaternionFromQuatT(tracker.rotation),
        acc: Vector3FromVec3fT(tracker.linearAcceleration),
        deltas: [],
      };
      const trackerPrevious = previous.current[trackerId];

      const rot = QuaternionFromQuatT(tracker.rotation).multiply(
        trackerPrevious.rot.clone().invert()
      );
      const acc = Vector3FromVec3fT(tracker.linearAcceleration).sub(
        trackerPrevious.acc
      );
      const dif = Math.min(
        1,
        (rot.x ** 2 + rot.y ** 2 + rot.z ** 2) * 50 +
          (acc.x ** 2 + acc.y ** 2 + acc.z ** 2) / 1000
      );
      // Use sum of the rotation and acceleration delta vector lengths over 0.3sec
      // for smoother movement and better detection of slow movement.
      if (trackerPrevious.deltas.length >= 0.5 * feedMaxTps) {
        trackerPrevious.deltas.shift();
      }
      trackerPrevious.deltas.push(dif);
      trackerPrevious.rot = QuaternionFromQuatT(tracker.rotation);
      trackerPrevious.acc = Vector3FromVec3fT(tracker.linearAcceleration);

      return Math.min(
        1,
        Math.max(
          0,
          trackerPrevious.deltas.reduce((a, b) => a + b, 0)
        )
      );
    });

    setVelocity(Math.max(0, ...velocities));
  }, [trackers, feedMaxTps]);

  return velocity;
};

export const useTrackerFromId = (
  trackerNum: string | number | undefined,
  deviceId: string | number | undefined
) => {
  const trackerAtom = useMemo(
    () => trackerFromIdAtom({ trackerNum, deviceId }),
    [trackerNum, deviceId]
  );
  return useAtomValue(trackerAtom);
};
