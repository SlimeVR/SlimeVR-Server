import { useEffect, useMemo, useRef, useState } from 'react';
import { BodyPart, TrackerDataT, TrackerInfoT } from 'solarxr-protocol';
import { QuaternionFromQuatT, QuaternionToEulerDegrees } from '@/maths/quaternion';
import { ReactLocalization, useLocalization } from '@fluent/react';
import { useDataFeedConfig } from './datafeed-config';
import { Quaternion, Vector3 } from 'three';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { useAtomValue } from 'jotai';
import { trackerFromIdAtom, computedTrackersAtom } from '@/store/app-store';
import { useVelocitySettings } from './velocity-settings';

export function getTrackerName(l10n: ReactLocalization, info: TrackerInfoT | null) {
  if (info?.customName) return info?.customName;
  if (info?.bodyPart) return l10n.getString('body_part-' + BodyPart[info?.bodyPart]);
  return info?.displayName || 'NONE';
}

/**
 * TrackerPositions that are explicitly allowed to have velocity in VelocityRolePolicy.ROLE_TO_GROUPS
 * These correspond to synthetic tracker body parts that CAN have velocity.
 *
 * Implicitly blacklisted (NOT in this list): HEAD, NECK, shoulders, hands
 */
const ALLOWED_VELOCITY_BODY_PARTS = new Set([
  10, // LEFT_FOOT
  11, // RIGHT_FOOT
  8,  // LEFT_LOWER_LEG (ankles)
  9,  // RIGHT_LOWER_LEG (ankles)
  6,  // LEFT_UPPER_LEG (knees)
  7,  // RIGHT_UPPER_LEG (knees)
  4,  // WAIST
  5,  // HIP
  3,  // CHEST (allowed)
  22, // UPPER_CHEST (allowed)
  16, // LEFT_UPPER_ARM (elbows)
  17, // RIGHT_UPPER_ARM (elbows)
]);

/**
 * Logical groupings for body parts that belong to the same role group.
 * Used as fallback when server doesn't provide explicit mapping.
 *
 * IMPORTANT: Only maps to synthetic body parts that are in the ALLOWED list above.
 * This respects the implicit blacklist (e.g., NECK excluded from velocity).
 */
const LOGICAL_BODY_PART_GROUPS: Record<number, number> = {
  // Waist/Hip group - WAIST has no TrackerRole, but logically maps to HIP synthetic
  4: 5,   // WAIST → HIP
  5: 5,   // HIP → HIP

  // CHEST group - CHEST has no TrackerRole, but logically maps to UPPER_CHEST synthetic
  3: 22,  // CHEST → UPPER_CHEST
  22: 22, // UPPER_CHEST → UPPER_CHEST

  // Ankles→Feet mapping - LOWER_LEG (ankles) have no TrackerRole, but logically map to FEET synthetic
  8: 10,  // LEFT_LOWER_LEG (left ankle) → LEFT_FOOT
  9: 11,  // RIGHT_LOWER_LEG (right ankle) → RIGHT_FOOT

  // These map 1:1
  1: 1,   // HEAD → HEAD
  6: 6,   // LEFT_UPPER_LEG → LEFT_UPPER_LEG
  7: 7,   // RIGHT_UPPER_LEG → RIGHT_UPPER_LEG
  10: 10, // LEFT_FOOT → LEFT_FOOT
  11: 11, // RIGHT_FOOT → RIGHT_FOOT
  16: 16, // LEFT_UPPER_ARM → LEFT_UPPER_ARM
  17: 17, // RIGHT_UPPER_ARM → RIGHT_UPPER_ARM

  // Explicitly NOT mapping (will return null, showing "No computed tracker"):
  // - LEFT_LOWER_ARM (14) - truly has no TrackerRole, no synthetic tracker
  // - RIGHT_LOWER_ARM (15) - truly has no TrackerRole, no synthetic tracker

  // Implicitly blacklisted (will be found but filtered):
  // - NECK (2) - has synthetic but blacklisted
  // - LEFT_HAND (18) - has synthetic but should show HMD/controller message
  // - RIGHT_HAND (19) - has synthetic but should show HMD/controller message
  // - Shoulders (20, 21) - have synthetic but blacklisted
};

/**
 * Finds the synthetic (computed) tracker that corresponds to a physical tracker's body part.
 *
 * Resolution order:
 * 1. Use server-provided mapping (authoritative, future-proof)
 * 2. Fallback to logical grouping (respects implicit blacklist)
 * 3. If no mapping found, assume 1:1 (bodyPart = syntheticBodyPart)
 *
 * Respects implicit blacklist: Will not show velocity for trackers whose synthetic
 * is not in the allowed list (UPPER_CHEST, NECK, shoulders, hands excluded).
 *
 * Synthetic trackers have position and velocity data.
 */
export function useSyntheticTrackerForBodyPart(bodyPart: BodyPart | null | undefined) {
  const syntheticTrackers = useAtomValue(computedTrackersAtom);
  const { trackerBodyPartMappings } = useVelocitySettings();

  return useMemo(() => {
    if (!bodyPart) return null;

    let syntheticBodyPart = bodyPart; // Default: assume 1:1 mapping

    // Priority 1: Use server-provided mapping (authoritative)
    if (trackerBodyPartMappings?.mappings) {
      const serverMapping = trackerBodyPartMappings.mappings.find(
        m => m.physicalBodyPart === bodyPart
      );
      if (serverMapping) {
        syntheticBodyPart = serverMapping.syntheticBodyPart;
      } else {
        // Priority 2: Use logical grouping as fallback
        const logicalMapping = LOGICAL_BODY_PART_GROUPS[bodyPart];
        if (logicalMapping !== undefined) {
          syntheticBodyPart = logicalMapping;
        }
      }
    } else {
      // No server mappings available, use logical grouping
      const logicalMapping = LOGICAL_BODY_PART_GROUPS[bodyPart];
      if (logicalMapping !== undefined) {
        syntheticBodyPart = logicalMapping;
      }
    }

    // Find the synthetic tracker with the resolved body part
    // NOTE: We return the tracker even if it's implicitly blacklisted
    // The widget will check ALLOWED_VELOCITY_BODY_PARTS to determine if velocity should be shown
    return syntheticTrackers.find(
      ({ tracker }) => tracker.info?.bodyPart === syntheticBodyPart
    )?.tracker || null;
  }, [syntheticTrackers, bodyPart, trackerBodyPartMappings]);
}

/**
 * Check if a body part is allowed to have velocity based on VelocityRolePolicy.ROLE_TO_GROUPS whitelist
 */
export function isBodyPartAllowedVelocity(bodyPart: number | null | undefined): boolean {
  if (bodyPart === null || bodyPart === undefined) return false;
  return ALLOWED_VELOCITY_BODY_PARTS.has(bodyPart);
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
    useRawVelocityData: () =>
      useMemo(() => tracker.rawVelocity, [tracker.rawVelocity]),
    useScaledVelocityData: () =>
      useMemo(() => tracker.scaledVelocity, [tracker.scaledVelocity]),
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
