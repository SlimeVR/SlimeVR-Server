import { BodyPart, BoneT } from 'solarxr-protocol';
import { EYE_HEIGHT_TO_HEIGHT_RATIO } from '@/hooks/height';

/**
 * Just need to know the length of the total body, so don't need right legs
 */
export const Y_PARTS = [
  BodyPart.NECK,
  BodyPart.UPPER_CHEST,
  BodyPart.CHEST,
  BodyPart.WAIST,
  BodyPart.HIP,
  BodyPart.LEFT_UPPER_LEG,
  BodyPart.LEFT_LOWER_LEG,
];

/**
 * Height the proportion presets in `skeletonParts.ts` are authored against.
 */
const REFERENCE_HEIGHT = 1.65;

export interface SkeletonProportions {
  /** User height relative to {@link REFERENCE_HEIGHT}. */
  bodyScale: number;
  shoulderWidth: number;
  hipWidth: number;
}

function sumBoneLengths(bones: Map<BodyPart, BoneT>, parts: BodyPart[]) {
  let sum = 0;
  for (const part of parts) {
    const bone = bones.get(part);
    if (!bone) return null;
    sum += bone.boneLength;
  }
  return sum;
}

export function computeUserHeight(bones: Map<BodyPart, BoneT>) {
  const yLength = sumBoneLengths(bones, Y_PARTS);
  if (yLength === null) return 0;
  return yLength / EYE_HEIGHT_TO_HEIGHT_RATIO;
}

export function computeHeadYOffset(bones: Map<BodyPart, BoneT>) {
  const hmd = bones.get(BodyPart.HEAD);
  if (hmd?.headPosition?.y && hmd.headPosition.y > 0) {
    return hmd.headPosition.y / EYE_HEIGHT_TO_HEIGHT_RATIO;
  }
  const yLength = sumBoneLengths(bones, Y_PARTS);
  if (yLength === null) return 0;
  return yLength / EYE_HEIGHT_TO_HEIGHT_RATIO;
}

/**
 * Derives the few body measurements the mesh preview needs from the skeleton.
 *
 * `BoneT` only carries lengths, so girths have to come from the bones that
 * happen to run sideways: the shoulder and hip bones.
 */
export function deriveSkeletonProportions(
  bones: Map<BodyPart, BoneT>
): SkeletonProportions {
  const height = computeUserHeight(bones);
  const bodyScale = height > 0 ? height / REFERENCE_HEIGHT : 1;

  const shoulderWidth =
    sumBoneLengths(bones, [BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER]) ?? 0;
  const hipWidth = sumBoneLengths(bones, [BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP]) ?? 0;

  return {
    bodyScale,
    shoulderWidth,
    hipWidth,
  };
}
