import { Color, Vector2, Vector3 } from 'three';
import { LineSegments2 } from 'three/examples/jsm/lines/LineSegments2';
import { LineMaterial } from 'three/examples/jsm/lines/LineMaterial.js';
import { LineSegmentsGeometry } from 'three/examples/jsm/lines/LineSegmentsGeometry.js';
import { BodyPart, BoneT } from 'solarxr-protocol';
import { QuaternionFromQuatT } from '@/maths/quaternion';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { FINGER_BODY_PARTS, TOE_BODY_PARTS } from '@/hooks/body-parts';

const boneHead = new Vector3();
const boneTail = new Vector3();

const BONE_COLOR_GROUPS: [BodyPart[], string][] = [
  [[BodyPart.HEAD, BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT], 'gold'],
  [[BodyPart.NECK], 'silver'],
  [
    [BodyPart.UPPER_CHEST, BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG],
    'chartreuse',
  ],
  [[BodyPart.LOWER_CHEST], 'purple'],
  [[BodyPart.LOWER_WAIST, BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM], 'red'],
  [[BodyPart.HIP], 'orange'],
  [[BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG], 'teal'],
  [
    [BodyPart.UPPER_WAIST, BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM],
    'indianred',
  ],
  [[BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND], 'fuchsia'],
  [[BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER], '#00ffff'],
];

export function getBoneColor(bodyPart: BodyPart) {
  if (FINGER_BODY_PARTS.includes(bodyPart) || TOE_BODY_PARTS.includes(bodyPart)) {
    return new Color('pink');
  }
  const color = BONE_COLOR_GROUPS.find(([parts]) => parts.includes(bodyPart))?.[1];
  return new Color(color ?? 'white');
}

export function getBoneTail(bone: BoneT, target = new Vector3()) {
  return target
    .set(0, -bone.boneLength, 0)
    .applyQuaternion(QuaternionFromQuatT(bone.orientation).normalize())
    .add(Vector3FromVec3fT(bone.headPosition));
}

export interface SkeletonRenderPart {
  bone: BoneT;
}

export class BasedSkeletonHelper extends LineSegments2 {
  readonly type = 'SkeletonHelper';
  readonly isSkeletonHelper = true;
  private parts: SkeletonRenderPart[];

  get resolution() {
    return this.material.resolution;
  }

  set resolution(value: Vector2) {
    this.material.resolution = value;
  }

  constructor(bones: Map<BodyPart, BoneT>) {
    const parts = [...bones.values()]
      .filter((bone) => bone.bodyPart !== BodyPart.NONE)
      .map((bone) => ({ bone }));
    const geometry = new LineSegmentsGeometry();
    geometry.setPositions(parts.flatMap(() => [0, 0, 0, 0, 0, 0]));
    geometry.setColors(
      parts.flatMap(({ bone }) => {
        const color = getBoneColor(bone.bodyPart);
        return [color.r, color.g, color.b, color.r, color.g, color.b];
      })
    );
    const material = new LineMaterial({
      vertexColors: true,
      toneMapped: false,
      transparent: true,
      linewidth: 4,
    });

    super(geometry, material);
    this.parts = parts;
  }

  setBones(bones: Map<BodyPart, BoneT>) {
    for (const part of this.parts) {
      part.bone = bones.get(part.bone.bodyPart) ?? part.bone;
    }
  }

  updateMatrixWorld(force: boolean) {
    const vertices: number[] = [];

    for (const part of this.parts) {
      const { bone } = part;
      boneHead.copy(Vector3FromVec3fT(bone.headPosition));
      getBoneTail(bone, boneTail);
      vertices.push(boneHead.x, boneHead.y, boneHead.z);
      vertices.push(boneTail.x, boneTail.y, boneTail.z);
    }

    this.geometry.setPositions(vertices);
    super.updateMatrixWorld(force);
  }

  dispose() {
    this.geometry.dispose();
    if (Array.isArray(this.material)) {
      this.material.forEach((material) => material.dispose());
    } else {
      this.material.dispose();
    }
  }
}
