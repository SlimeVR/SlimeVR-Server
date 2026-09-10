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
 * Height the proportion presets in `skeletonParts.ts` are authored against:
 * the rig the models came from, measured the same way {@link computeUserHeight}
 * measures the user, so the two are the same kind of number. Its headset sits
 * at 1.583, which over {@link EYE_HEIGHT_TO_HEIGHT_RATIO} is this. Its meshes
 * agree, running 1.704 from the top of the skull to the sole of the foot.
 */
const REFERENCE_HEIGHT = 1.691;

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
 * Shoulder girth comes from the sideways-running shoulder bones. Hip girth is the
 * gap between the two upper-leg heads, which the hip width spreads apart.
 */
export function deriveSkeletonProportions(
  bones: Map<BodyPart, BoneT>
): SkeletonProportions {
  const height = computeUserHeight(bones);
  const bodyScale = height > 0 ? height / REFERENCE_HEIGHT : 1;

  const shoulderWidth =
    sumBoneLengths(bones, [BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER]) ?? 0;

  const leftLegHead = bones.get(BodyPart.LEFT_UPPER_LEG)?.headPosition;
  const rightLegHead = bones.get(BodyPart.RIGHT_UPPER_LEG)?.headPosition;
  const hipWidth =
    leftLegHead && rightLegHead
      ? Math.hypot(
          leftLegHead.x - rightLegHead.x,
          leftLegHead.y - rightLegHead.y,
          leftLegHead.z - rightLegHead.z
        )
      : 0;

  return {
    bodyScale,
    shoulderWidth,
    hipWidth,
  };
}
