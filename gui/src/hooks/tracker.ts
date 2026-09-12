import { useEffect, useMemo, useRef, useState } from 'react';
import { BodyPart, TrackerDataT, TrackerInfoT } from 'solarxr-protocol';
import { QuaternionFromQuatT, QuaternionToEulerDegrees } from '@/maths/quaternion';
import { ReactLocalization, useLocalization } from '@fluent/react';
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
      const deltas = useRef<{ time: number; value: number }[]>([]);

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
          if (dif > 0) {
            deltas.current.push({ time: performance.now(), value: dif });
          }
          previousRot.current = QuaternionFromQuatT(tracker.rotation);
          previousAcc.current = Vector3FromVec3fT(tracker.linearAcceleration);
        }

        let timeout: ReturnType<typeof setTimeout>;
        const updateVelocity = () => {
          const now = performance.now();
          // Keep half a second of motion, even when the data feed stops changing.
          deltas.current = deltas.current.filter(({ time }) => now - time < 500);
          setVelocity(
            Math.min(
              1,
              deltas.current.reduce((sum, { value }) => sum + value, 0)
            )
          );
          if (deltas.current.length > 0) {
            timeout = setTimeout(
              updateVelocity,
              Math.max(1, deltas.current[0].time + 500 - now)
            );
          }
        };
        updateVelocity();

        return () => clearTimeout(timeout);
      }, [tracker.rotation, tracker.linearAcceleration]);

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
