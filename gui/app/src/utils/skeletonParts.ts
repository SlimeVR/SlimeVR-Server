import { BodyPart } from 'solarxr-protocol';
import {
  Color,
  DoubleSide,
  Euler,
  MathUtils,
  MeshLambertMaterial,
  Quaternion,
  Vector3,
} from 'three';
import { SkeletonProportions } from './skeletonProportions';

/**
 * Measured off the loaded glTF. The model is drawn in Blender bone space, so
 * its own axes are x across the bone, y through it, and z along it -- none of
 * them the scene's up. These are named for the body so the axes can't be
 * misread: a bone's `rotation` is what puts them onto the scene.
 */
export interface ModelDimensions {
  /** Left to right across the bone. */
  width: number;
  /** Front to back through the bone. */
  depth: number;
  /** How far the model reaches down the bone from its origin. */
  length: number;
}

export interface ShapeScaleContext {
  model: ModelDimensions;
  proportions: SkeletonProportions;
  boneLength: number;
}

/**
 * Applied after the model is rotated, so these are the bone's own axes however
 * the model is turned onto it.
 */
export interface ShapeSize {
  /** Left to right across the bone. */
  width: number;
  /** Front to back through the bone. */
  depth: number;
  /** Along the bone. */
  length: number;
}

export interface ShapeScale {
  compute: (ctx: ShapeScaleContext) => ShapeSize;
}

/**
 * What a part's girth follows. Height suits a limb, but the torso should widen
 * with the body it sits in rather than with how tall it is.
 */
type GirthBasis = 'height' | 'shoulders' | 'hips' | 'hand' | 'foot';

/**
 * Ratios against the model as authored, so 1 is its own size at the body's
 * scale. `length` measures against whatever that builder sizes the model to.
 */
type ModelRatios = {
  width?: number;
  depth?: number;
  length?: number;
  girthFrom?: GirthBasis;
};

const RIG_SHOULDER_WIDTH = 0.338;
const RIG_HIP_WIDTH = 0.26;
const RIG_HAND_LENGTH = 0.08;
const RIG_FOOT_LENGTH = 0.13;

const girthScale = (proportions: SkeletonProportions, basis: GirthBasis) => {
  if (basis === 'shoulders' && proportions.shoulderWidth > 0) {
    return proportions.shoulderWidth / RIG_SHOULDER_WIDTH;
  }
  if (basis === 'hips' && proportions.hipWidth > 0) {
    return proportions.hipWidth / RIG_HIP_WIDTH;
  }
  if (basis === 'hand' && proportions.handLength > 0) {
    return proportions.handLength / RIG_HAND_LENGTH;
  }
  if (basis === 'foot' && proportions.footLength > 0) {
    return proportions.footLength / RIG_FOOT_LENGTH;
  }
  return proportions.bodyScale;
};

export const spanBone = ({
  width = 1,
  depth = 1,
  length = 1,
  girthFrom = 'height',
}: ModelRatios = {}): ShapeScale => ({
  compute: ({ model, proportions, boneLength }) => {
    const girth = girthScale(proportions, girthFrom);
    return {
      width: width * girth,
      depth: depth * girth,
      length: (boneLength / model.length) * length,
    };
  },
});

/**
 * Where the model sits, from the bone's head, in the bone's own axes. Worked
 * out per bone like {@link ShapeScale}, so a part says what its offset
 * measures against rather than the two being mixed together.
 */
export type ShapeOffset = (ctx: ShapeScaleContext) => Vector3;

export const inBoneLengths =
  ({ width = 0, depth = 0, length = 0 }: ModelRatios): ShapeOffset =>
  ({ boneLength }) =>
    new Vector3(width, length, depth).multiplyScalar(boneLength);

export const inMetres =
  ({ width = 0, depth = 0, length = 0 }: ModelRatios): ShapeOffset =>
  ({ proportions }) =>
    new Vector3(width, length, depth).multiplyScalar(proportions.bodyScale);

export const turn = ({
  width = 0,
  depth = 0,
  length = 0,
}: ModelRatios = {}): Quaternion =>
  new Quaternion().setFromEuler(
    new Euler(
      MathUtils.degToRad(width),
      MathUtils.degToRad(length),
      MathUtils.degToRad(depth)
    )
  );

export const otherSide = (scale: ShapeScale): ShapeScale => ({
  compute: (ctx) => {
    const size = scale.compute(ctx);
    return { ...size, width: -size.width };
  },
});

export const DefaultBoneModelUrl = '/models/skeleton/body.glb';
export interface BoneShapeConfig {
  modelUrl?: string;
  objectName?: string;
  offset?: ShapeOffset;
  rotation?: Quaternion;
  scale?: ShapeScale;
}

export interface BonePartConfig {
  visible: boolean;
  shapes: BoneShapeConfig[];
  /** Where an assigned tracker sits from this bone's head (0) to tail (1). */
  trackerOffset?: number;
  /**
   * Where the marker starts inside the part's first shape, in the bone's own
   * axes: -1 to 1 on each, 0 at the shape's centre. Used instead of
   * `trackerOffset` for bones whose length doesn't span their shape.
   */
  trackerAnchor?: Vector3;
}

export const shape = (overrides: Partial<BoneShapeConfig> = {}): BoneShapeConfig => ({
  ...overrides,
});

const part = (
  shapes: BoneShapeConfig | BoneShapeConfig[],
  overrides: Partial<BonePartConfig> = {}
): BonePartConfig => ({
  visible: true,
  shapes: Array.isArray(shapes) ? shapes : [shapes],
  ...overrides,
});

const model = (
  objectName: string,
  overrides: Omit<BoneShapeConfig, 'objectName'> = {}
): BoneShapeConfig => ({
  objectName,
  ...overrides,
});

const fingerScale = spanBone({ girthFrom: 'hand' });
const finger = (file: string) =>
  part(model(file, { scale: fingerScale, offset: inMetres({ width: -0.003 }) }));
const fingerRight = (file: string) =>
  part(
    model(file, {
      scale: otherSide(fingerScale),
      offset: inMetres({ width: 0.003 }),
    })
  );

// Toe bones are drawn along their local y like the foot, so +y is back toward the ankle.
// The base of each toe mesh is a painted cap, tucked into the foot so it never
// shows. The length ratio keeps the tip on the bone's tail despite the tuck.
const TOE_TUCK = 0.5;
const toeScale = (width: number, depth: number) =>
  spanBone({ width, depth, length: 1 + TOE_TUCK, girthFrom: 'foot' });
const toeOffset = inBoneLengths({ length: TOE_TUCK, depth: 0.11 });
const toe = (file: string, scale = toeScale(1.3, 1.0)) =>
  part(model(file, { scale, offset: toeOffset }));
const toeRight = (file: string, scale = toeScale(1.3, 1.0)) =>
  part(model(file, { scale: otherSide(scale), offset: toeOffset }));
const bigToeScale = toeScale(1.3, 1.15);

const HEAD_SIZE_EXPONENT = 0.25;
const headSize = ({ bodyScale }: SkeletonProportions) =>
  bodyScale ** HEAD_SIZE_EXPONENT;
// The head bone has no length, so the skull keeps the size it had at the old 0.1 m bone.
const headScale: ShapeScale = {
  compute: ({ proportions }) => {
    const size = headSize(proportions);
    return { width: size, depth: size, length: 1.04 * size };
  },
};
const headOffset: ShapeOffset = ({ proportions }) =>
  new Vector3(0, 0.005 * headSize(proportions), 0);

// Neck circumference goes as stature^0.59 (Snyder et al. 1977), so the neck is thicker
// than linear scaling gives on a short body and thinner on a tall one.
const NECK_GIRTH_EXPONENT = 0.6;
const neckScale: ShapeScale = {
  compute: ({ model, proportions, boneLength }) => {
    const girth = proportions.bodyScale ** NECK_GIRTH_EXPONENT;
    return { width: girth, depth: girth, length: boneLength / model.length };
  },
};

const shoulderScale = spanBone({ width: 0.8, depth: 0.8, length: 1.3 });
const handScale = spanBone({ width: 1.0, depth: 1.0, length: 1.0, girthFrom: 'hand' });
const upperArmScale = spanBone({ girthFrom: 'shoulders', width: 0.87, depth: 0.87 });
const lowerArmScale = spanBone({ girthFrom: 'shoulders', width: 0.89, depth: 0.89 });
const upperLegScale = spanBone({ girthFrom: 'hips', length: 1.0 });
const lowerLegScale = spanBone({ girthFrom: 'hips', length: 0.9 });
const footScale: ShapeScale = {
  compute: ({ boneLength }) => {
    const ratio = boneLength / RIG_FOOT_LENGTH;
    return { width: 0.9 * ratio, length: 1.12 * ratio, depth: 1.14 * ratio };
  },
};
// Forward follows foot length; the drop from the ankle to the sole doesn't.
const footOffset: ShapeOffset = ({ boneLength, proportions }) =>
  new Vector3(0, -0.1 * boneLength, -0.1 * proportions.bodyScale);

const CHEST_TRACKER_OFFSET = 0.2;
const WAIST_TRACKER_OFFSET = 0.8;
const HIP_TRACKER_OFFSET = 0.75;
const UPPER_ARM_TRACKER_OFFSET = 0.75;
const LOWER_ARM_TRACKER_OFFSET = 0.15;
const UPPER_LEG_TRACKER_OFFSET = 0.7;
const LOWER_LEG_TRACKER_OFFSET = 0.75;

const FOOT_MARKER: Partial<BonePartConfig> = {
  trackerAnchor: new Vector3(0, -0.3, 0),
};

export const SKELETON_PART_PRESETS: Record<BodyPart, BonePartConfig> = {
  [BodyPart.NONE]: part(shape(), { visible: false }),
  [BodyPart.HEAD]: part(
    model('head', {
      scale: headScale,
      offset: headOffset,
    }),
    { trackerAnchor: new Vector3(0, 0.2, 0) }
  ),
  [BodyPart.NECK]: part(
    model('neck', {
      scale: neckScale,
      offset: inBoneLengths({ length: -0.0 }),
    })
  ),
  [BodyPart.UPPER_CHEST]: part(
    model('upper_chest', {
      scale: spanBone({
        girthFrom: 'shoulders',
        length: 0.95,
        width: 0.95,
        depth: 0.95,
      }),
      offset: inBoneLengths({ length: -0.2 }),
    })
  ),
  [BodyPart.LOWER_CHEST]: part(
    model('lower_chest', {
      scale: spanBone({
        girthFrom: 'shoulders',
        length: 0.95,
        width: 0.95,
        depth: 0.95,
      }),
      offset: inBoneLengths({ length: -0.1 }),
    }),
    { trackerOffset: CHEST_TRACKER_OFFSET }
  ),
  [BodyPart.UPPER_WAIST]: part(
    model('upper_waist', {
      scale: spanBone({ girthFrom: 'hips', length: 0.9, width: 1.0, depth: 1.0 }),
      offset: inBoneLengths({ length: 0.3 }),
    }),
    { trackerOffset: WAIST_TRACKER_OFFSET }
  ),
  [BodyPart.LOWER_WAIST]: part(
    model('lower_waist', {
      scale: spanBone({ girthFrom: 'hips', length: 0.85, width: 1.0, depth: 1.0 }),
      offset: inBoneLengths({ length: 0.1 }),
    }),
    { trackerOffset: WAIST_TRACKER_OFFSET }
  ),
  [BodyPart.HIP]: part(
    model('hip', {
      scale: spanBone({ girthFrom: 'hips', width: 1, depth: 1, length: 1 }),
      offset: inBoneLengths({ length: -1.6 }),
    }),
    { trackerOffset: HIP_TRACKER_OFFSET }
  ),
  [BodyPart.LEFT_SHOULDER]: part(
    model('shoulder', {
      scale: shoulderScale,
      offset: inBoneLengths({ width: 0.075 }),
    })
  ),
  [BodyPart.RIGHT_SHOULDER]: part(
    model('shoulder', {
      scale: otherSide(shoulderScale),
      offset: inBoneLengths({ width: -0.075 }),
    })
  ),
  [BodyPart.LEFT_UPPER_ARM]: part(model('upper_arm', { scale: upperArmScale }), {
    trackerOffset: UPPER_ARM_TRACKER_OFFSET,
  }),
  [BodyPart.RIGHT_UPPER_ARM]: part(
    model('upper_arm', { scale: otherSide(upperArmScale) }),
    { trackerOffset: UPPER_ARM_TRACKER_OFFSET }
  ),
  [BodyPart.LEFT_LOWER_ARM]: part(model('lower_arm', { scale: lowerArmScale }), {
    trackerOffset: LOWER_ARM_TRACKER_OFFSET,
  }),
  [BodyPart.RIGHT_LOWER_ARM]: part(
    model('lower_arm', { scale: otherSide(lowerArmScale) }),
    { trackerOffset: LOWER_ARM_TRACKER_OFFSET }
  ),
  [BodyPart.LEFT_HAND]: part(
    model('hand', {
      rotation: turn({ length: 174, width: 5, depth: -5 }),
      scale: handScale,
    })
  ),
  [BodyPart.RIGHT_HAND]: part(
    model('hand', {
      rotation: turn({ length: 186, width: 5, depth: 5 }),
      scale: otherSide(handScale),
    })
  ),

  [BodyPart.LEFT_UPPER_LEG]: part(model('upper_leg', { scale: upperLegScale }), {
    trackerOffset: UPPER_LEG_TRACKER_OFFSET,
  }),
  [BodyPart.RIGHT_UPPER_LEG]: part(
    model('upper_leg', { scale: otherSide(upperLegScale) }),
    { trackerOffset: UPPER_LEG_TRACKER_OFFSET }
  ),
  [BodyPart.LEFT_LOWER_LEG]: part(
    model('lower_leg', {
      scale: lowerLegScale,
      offset: inBoneLengths({ depth: -0.0 }),
    }),
    { trackerOffset: LOWER_LEG_TRACKER_OFFSET }
  ),
  [BodyPart.RIGHT_LOWER_LEG]: part(
    model('lower_leg', {
      scale: otherSide(lowerLegScale),
      offset: inBoneLengths({ depth: -0.0 }),
    }),
    { trackerOffset: LOWER_LEG_TRACKER_OFFSET }
  ),
  [BodyPart.LEFT_FOOT]: part(
    model('foot', {
      offset: footOffset,
      scale: footScale,
      rotation: turn({ width: -42 }),
    }),
    FOOT_MARKER
  ),
  [BodyPart.RIGHT_FOOT]: part(
    model('foot', {
      offset: footOffset,
      scale: otherSide(footScale),
      rotation: turn({ width: -42 }),
    }),
    FOOT_MARKER
  ),

  [BodyPart.LEFT_THUMB_METACARPAL]: finger('thumb_metacarpal'),
  [BodyPart.LEFT_THUMB_PROXIMAL]: finger('thumb_proximal'),
  [BodyPart.LEFT_THUMB_DISTAL]: finger('thumb_distal'),
  [BodyPart.LEFT_INDEX_PROXIMAL]: finger('index_proximal'),
  [BodyPart.LEFT_INDEX_INTERMEDIATE]: finger('index_intermediate'),
  [BodyPart.LEFT_INDEX_DISTAL]: finger('index_distal'),
  [BodyPart.LEFT_MIDDLE_PROXIMAL]: finger('middle_proximal'),
  [BodyPart.LEFT_MIDDLE_INTERMEDIATE]: finger('middle_intermediate'),
  [BodyPart.LEFT_MIDDLE_DISTAL]: finger('middle_distal'),
  [BodyPart.LEFT_RING_PROXIMAL]: finger('ring_proximal'),
  [BodyPart.LEFT_RING_INTERMEDIATE]: finger('ring_intermediate'),
  [BodyPart.LEFT_RING_DISTAL]: finger('ring_distal'),
  [BodyPart.LEFT_LITTLE_PROXIMAL]: finger('little_proximal'),
  [BodyPart.LEFT_LITTLE_INTERMEDIATE]: finger('little_intermediate'),
  [BodyPart.LEFT_LITTLE_DISTAL]: finger('little_distal'),

  [BodyPart.RIGHT_THUMB_METACARPAL]: fingerRight('thumb_metacarpal'),
  [BodyPart.RIGHT_THUMB_PROXIMAL]: fingerRight('thumb_proximal'),
  [BodyPart.RIGHT_THUMB_DISTAL]: fingerRight('thumb_distal'),
  [BodyPart.RIGHT_INDEX_PROXIMAL]: fingerRight('index_proximal'),
  [BodyPart.RIGHT_INDEX_INTERMEDIATE]: fingerRight('index_intermediate'),
  [BodyPart.RIGHT_INDEX_DISTAL]: fingerRight('index_distal'),
  [BodyPart.RIGHT_MIDDLE_PROXIMAL]: fingerRight('middle_proximal'),
  [BodyPart.RIGHT_MIDDLE_INTERMEDIATE]: fingerRight('middle_intermediate'),
  [BodyPart.RIGHT_MIDDLE_DISTAL]: fingerRight('middle_distal'),
  [BodyPart.RIGHT_RING_PROXIMAL]: fingerRight('ring_proximal'),
  [BodyPart.RIGHT_RING_INTERMEDIATE]: fingerRight('ring_intermediate'),
  [BodyPart.RIGHT_RING_DISTAL]: fingerRight('ring_distal'),
  [BodyPart.RIGHT_LITTLE_PROXIMAL]: fingerRight('little_proximal'),
  [BodyPart.RIGHT_LITTLE_INTERMEDIATE]: fingerRight('little_intermediate'),
  [BodyPart.RIGHT_LITTLE_DISTAL]: fingerRight('little_distal'),

  [BodyPart.LEFT_BIG_TOE]: toe('big_toe', bigToeScale),
  [BodyPart.LEFT_INDEX_TOE]: toe('index_toe'),
  [BodyPart.LEFT_MIDDLE_TOE]: toe('middle_toe'),
  [BodyPart.LEFT_RING_TOE]: toe('ring_toe'),
  [BodyPart.LEFT_LITTLE_TOE]: toe('little_toe'),
  [BodyPart.RIGHT_BIG_TOE]: toeRight('big_toe', bigToeScale),
  [BodyPart.RIGHT_INDEX_TOE]: toeRight('index_toe'),
  [BodyPart.RIGHT_MIDDLE_TOE]: toeRight('middle_toe'),
  [BodyPart.RIGHT_RING_TOE]: toeRight('ring_toe'),
  [BodyPart.RIGHT_LITTLE_TOE]: toeRight('little_toe'),
};

export function getTrackerBoneOffset(bodyPart: BodyPart) {
  return SKELETON_PART_PRESETS[bodyPart]?.trackerOffset ?? 0.5;
}

const DEFAULT_SCALE = spanBone();

export function computeShapeScale(
  config: BoneShapeConfig,
  proportions: SkeletonProportions,
  boneLength: number,
  model?: ModelDimensions
) {
  const scale = config.scale ?? DEFAULT_SCALE;
  if (!model) return { width: 1, depth: 1, length: 1 };

  return scale.compute({ model, proportions, boneLength });
}

const materialCache = new Map<string, MeshLambertMaterial>();

export function getPartMaterial(color: Color) {
  const key = color.getHexString();
  let material = materialCache.get(key);
  if (!material) {
    material = new MeshLambertMaterial({ color, side: DoubleSide });
    materialCache.set(key, material);
  }
  return material;
}
