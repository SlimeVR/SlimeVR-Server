import { useEffect, useMemo, useRef, useState } from 'react';
import { BodyPart, TrackerDataT, TrackerInfoT } from 'solarxr-protocol';
import { QuaternionFromQuatT, QuaternionToEulerDegrees } from '@/maths/quaternion';
import { ReactLocalization, useLocalization } from '@fluent/react';
import { useDataFeedConfig } from './datafeed-config';
import { Quaternion, Vector3 } from 'three';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { useAtomValue } from 'jotai';
import { trackerFromIdAtom } from '@/store/app-store';

export function getTrackerName(l10n: ReactLocalization, info: TrackerInfoT | null) {
  if (info?.customName) return info?.customName;
  if (info?.bodyPart) return l10n.getString('body_part-' + BodyPart[info?.bodyPart]);
  return info?.displayName || 'NONE';
}

export function useTracker(tracker: TrackerDataT) {
  const { l10n } = useLocalization();
  const { feedMaxTps } = useDataFeedConfig();

  return {
    useName: () =>
      useMemo(() => getTrackerName(l10n, tracker.info), [tracker.info, l10n]),
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
    useVelocity: () => {
      const previousRot = useRef<Quaternion>(QuaternionFromQuatT(tracker.rotation));
      const previousAcc = useRef<Vector3>(
        Vector3FromVec3fT(tracker.linearAcceleration)
      );
      const [velocity, setVelocity] = useState<number>(0);
      const [deltas] = useState<number[]>([]);

      useEffect(() => {
        if (tracker.rotation) {
          const rot = QuaternionFromQuatT(tracker.rotation).multiply(
            previousRot.current.clone().invert()
          );
          const acc = Vector3FromVec3fT(tracker.linearAcceleration).sub(
            previousAcc.current
          );
          const dif = Math.min(
            1,
            (rot.x ** 2 + rot.y ** 2 + rot.z ** 2) * 50 +
              (acc.x ** 2 + acc.y ** 2 + acc.z ** 2) / 1000
          );
          // Use sum of the rotation and acceleration delta vector lengths over 0.3sec
          // for smoother movement and better detection of slow movement.
          if (deltas.length >= 0.5 * feedMaxTps) {
            deltas.shift();
          }
          deltas.push(dif);
          setVelocity(
            Math.min(
              1,
              Math.max(
                0,
                deltas.reduce((a, b) => a + b)
              )
            )
          );
          previousRot.current = QuaternionFromQuatT(tracker.rotation);
          previousAcc.current = Vector3FromVec3fT(tracker.linearAcceleration);
        }
      }, [tracker.rotation]);

      return velocity;
    },
  };
}

export function useTrackerFromId(
  trackerNum: string | number | undefined,
  deviceId: string | number | undefined
) {
  const trackerAtom = useMemo(
    () => trackerFromIdAtom({ trackerNum, deviceId }),
    [trackerNum, deviceId]
  );
  return useAtomValue(trackerAtom);
}
