import { BodyPart } from 'solarxr-protocol';
import {
  BufferGeometry,
  Color,
  CylinderGeometry,
  MeshLambertMaterial,
  SphereGeometry,
  Vector3,
} from 'three';
import { SkeletonProportions } from './skeletonProportions';

export interface BoneShapeConfig {
  geometry?: BufferGeometry;
  scaleRatio: Vector3;
  offset?: number;
  localOffset?: Vector3;
  modelUrl?: string;
  customScale?: (
    boneLength: number,
    proportions: SkeletonProportions,
    scaleRatio: Vector3
  ) => { scaleX: number; scaleY: number; scaleZ: number };
}

export interface BonePartConfig {
  visible: boolean;
  joint?: number;
  shapes: BoneShapeConfig[];
}

/** Flat-capped tube spanning `y` in [-1, 1], radius 1 (default primitive). */
export const CYLINDER_GEOMETRY: BufferGeometry = new CylinderGeometry(1, 1, 2, 20);

/** Small dark ball joints. */
export const JOINT_GEOMETRY: BufferGeometry = new SphereGeometry(1, 12, 9);
export const JOINT_MATERIAL = new MeshLambertMaterial({ color: '#24242b' });

export const shape = (
  scaleRatio: Vector3 | { x: number; y?: number; z?: number },
  overrides: Partial<Omit<BoneShapeConfig, 'scaleRatio'>> = {}
): BoneShapeConfig => ({
  scaleRatio:
    scaleRatio instanceof Vector3
      ? scaleRatio
      : new Vector3(scaleRatio.x, scaleRatio.y ?? 1.0, scaleRatio.z ?? scaleRatio.x),
  offset: 0,
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

const finger = (): BonePartConfig =>
  part(shape({ x: 0.15, y: 0.9, z: 0.15 }), { visible: false });

export const SKELETON_PART_PRESETS: Record<BodyPart, BonePartConfig> = {
  [BodyPart.NONE]: part(shape({ x: 0.1, y: 1.0, z: 0.1 }), { visible: false }),
  [BodyPart.HEAD]: part(
    shape(
      { x: 0.155, y: 1.4, z: 0.18 },
      {
        localOffset: new Vector3(0, 0, 0.02),
        modelUrl: '/models/skeleton/head.gltf',
        customScale: (boneLength, proportions, ratio) => ({
          scaleX: (ratio.x * proportions.bodyScale) / 2,
          scaleY: (boneLength * ratio.y) / 2,
          scaleZ: (ratio.z * proportions.bodyScale) / 2,
        }),
      }
    )
  ),

  [BodyPart.NECK]: part(
    shape({ x: 0.6, y: 1.05, z: 0.6 }, { modelUrl: '/models/skeleton/spine.gltf' }),
    { joint: 0.45 }
  ),
  [BodyPart.UPPER_CHEST]: part(
    shape({ x: 0.35, y: 1.05, z: 0.35 }, { modelUrl: '/models/skeleton/spine.gltf' })
  ),
  [BodyPart.CHEST]: part([
    shape({ x: 0.35, y: 1.05, z: 0.35 }, { modelUrl: '/models/skeleton/spine.gltf' }),
    shape(
      { x: 0.72, y: 1.5, z: 0.47 },
      {
        offset: -0.4,
        modelUrl: '/models/skeleton/chest.gltf',
        customScale: (boneLength, proportions, ratio) => ({
          scaleX: (ratio.x * proportions.shoulderWidth) / 2,
          scaleY: (boneLength * ratio.y) / 2,
          scaleZ: (ratio.z * proportions.shoulderWidth) / 2,
        }),
      }
    ),
  ]),

  [BodyPart.WAIST]: part(
    shape({ x: 0.45, y: 1.05, z: 0.45 }, { modelUrl: '/models/skeleton/spine.gltf' })
  ),
  [BodyPart.HIP]: part(
    shape({ x: 0.45, y: 1.05, z: 0.45 }, { modelUrl: '/models/skeleton/spine.gltf' })
  ),
  [BodyPart.LEFT_SHOULDER]: part(
    shape(
      { x: 0.55, y: 1.0, z: 0.47 },
      { modelUrl: '/models/skeleton/shoulder_cap.gltf' }
    )
  ),
  [BodyPart.RIGHT_SHOULDER]: part(
    shape(
      { x: 0.55, y: 1.0, z: 0.47 },
      { modelUrl: '/models/skeleton/shoulder_cap.gltf' }
    )
  ),
  [BodyPart.LEFT_UPPER_ARM]: part(
    shape(
      { x: 0.32, y: 1.0, z: 0.29 },
      { modelUrl: '/models/skeleton/upper_arm.gltf' }
    ),
    { joint: 0.5 }
  ),
  [BodyPart.RIGHT_UPPER_ARM]: part(
    shape(
      { x: 0.32, y: 1.0, z: 0.29 },
      { modelUrl: '/models/skeleton/upper_arm.gltf' }
    ),
    { joint: 0.5 }
  ),
  [BodyPart.LEFT_LOWER_ARM]: part(
    shape({ x: 0.28, y: 1.0, z: 0.25 }, { modelUrl: '/models/skeleton/forearm.gltf' }),
    { joint: 0.5 }
  ),
  [BodyPart.RIGHT_LOWER_ARM]: part(
    shape({ x: 0.28, y: 1.0, z: 0.25 }, { modelUrl: '/models/skeleton/forearm.gltf' }),
    { joint: 0.5 }
  ),
  [BodyPart.LEFT_HAND]: part(shape({ x: 0.75, y: 1.0, z: 0.3 }), { joint: 0.45 }),
  [BodyPart.RIGHT_HAND]: part(shape({ x: 0.75, y: 1.0, z: 0.3 }), { joint: 0.45 }),
  [BodyPart.LEFT_HIP]: part(shape({ x: 0.85, y: 1.05, z: 0.85 })),
  [BodyPart.RIGHT_HIP]: part(shape({ x: 0.85, y: 1.05, z: 0.85 })),
  [BodyPart.LEFT_UPPER_LEG]: part(
    shape({ x: 0.32, y: 1.0, z: 0.29 }, { modelUrl: '/models/skeleton/thigh.gltf' }),
    { joint: 0.35 }
  ),
  [BodyPart.RIGHT_UPPER_LEG]: part(
    shape({ x: 0.32, y: 1.0, z: 0.29 }, { modelUrl: '/models/skeleton/thigh.gltf' }),
    { joint: 0.35 }
  ),
  [BodyPart.LEFT_LOWER_LEG]: part(
    shape({ x: 0.26, y: 1.0, z: 0.24 }, { modelUrl: '/models/skeleton/calf.gltf' }),
    { joint: 0.45 }
  ),
  [BodyPart.RIGHT_LOWER_LEG]: part(
    shape({ x: 0.26, y: 1.0, z: 0.24 }, { modelUrl: '/models/skeleton/calf.gltf' }),
    { joint: 0.45 }
  ),
  [BodyPart.LEFT_FOOT]: part(
    shape({ x: 0.65, y: 1.0, z: 0.42 }, { modelUrl: '/models/skeleton/foot.gltf' }),
    { joint: 0.45 }
  ),
  [BodyPart.RIGHT_FOOT]: part(
    shape({ x: 0.65, y: 1.0, z: 0.42 }, { modelUrl: '/models/skeleton/foot.gltf' }),
    { joint: 0.45 }
  ),
  [BodyPart.LEFT_THUMB_METACARPAL]: finger(),
  [BodyPart.LEFT_THUMB_PROXIMAL]: finger(),
  [BodyPart.LEFT_THUMB_DISTAL]: finger(),
  [BodyPart.LEFT_INDEX_PROXIMAL]: finger(),
  [BodyPart.LEFT_INDEX_INTERMEDIATE]: finger(),
  [BodyPart.LEFT_INDEX_DISTAL]: finger(),
  [BodyPart.LEFT_MIDDLE_PROXIMAL]: finger(),
  [BodyPart.LEFT_MIDDLE_INTERMEDIATE]: finger(),
  [BodyPart.LEFT_MIDDLE_DISTAL]: finger(),
  [BodyPart.LEFT_RING_PROXIMAL]: finger(),
  [BodyPart.LEFT_RING_INTERMEDIATE]: finger(),
  [BodyPart.LEFT_RING_DISTAL]: finger(),
  [BodyPart.LEFT_LITTLE_PROXIMAL]: finger(),
  [BodyPart.LEFT_LITTLE_INTERMEDIATE]: finger(),
  [BodyPart.LEFT_LITTLE_DISTAL]: finger(),
  [BodyPart.RIGHT_THUMB_METACARPAL]: finger(),
  [BodyPart.RIGHT_THUMB_PROXIMAL]: finger(),
  [BodyPart.RIGHT_THUMB_DISTAL]: finger(),
  [BodyPart.RIGHT_INDEX_PROXIMAL]: finger(),
  [BodyPart.RIGHT_INDEX_INTERMEDIATE]: finger(),
  [BodyPart.RIGHT_INDEX_DISTAL]: finger(),
  [BodyPart.RIGHT_MIDDLE_PROXIMAL]: finger(),
  [BodyPart.RIGHT_MIDDLE_INTERMEDIATE]: finger(),
  [BodyPart.RIGHT_MIDDLE_DISTAL]: finger(),
  [BodyPart.RIGHT_RING_PROXIMAL]: finger(),
  [BodyPart.RIGHT_RING_INTERMEDIATE]: finger(),
  [BodyPart.RIGHT_RING_DISTAL]: finger(),
  [BodyPart.RIGHT_LITTLE_PROXIMAL]: finger(),
  [BodyPart.RIGHT_LITTLE_INTERMEDIATE]: finger(),
  [BodyPart.RIGHT_LITTLE_DISTAL]: finger(),
};

export function computeShapeScale(
  config: BoneShapeConfig,
  proportions: SkeletonProportions,
  boneLength: number
) {
  if (config.customScale) {
    return config.customScale(boneLength, proportions, config.scaleRatio);
  }

  const length = Math.max(boneLength, 0.05);
  const scaleX = (config.scaleRatio.x * length) / 2;
  const scaleY = (boneLength * config.scaleRatio.y) / 2;
  const scaleZ = (config.scaleRatio.z * length) / 2;

  return { scaleX, scaleY, scaleZ };
}

const materialCache = new Map<string, MeshLambertMaterial>();

export function getPartMaterial(color: Color) {
  const key = color.getHexString();
  let material = materialCache.get(key);
  if (!material) {
    material = new MeshLambertMaterial({ color });
    materialCache.set(key, material);
  }
  return material;
}
