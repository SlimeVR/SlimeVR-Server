import { Canvas, Object3DNode, extend } from '@react-three/fiber';
import { useAppContext } from '@/hooks/app';
import { Bone, PerspectiveCamera, Quaternion, Skeleton, Vector3 } from 'three';
import { useMemo, useEffect, useRef } from 'react';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { QuaternionFromQuatT } from '@/maths/quaternion';
import { BodyPart, BoneT } from 'solarxr-protocol';
import { OrbitControls } from '@react-three/drei';

class BoneKind extends Bone {
  boneT: BoneT;

  constructor(bones: Map<BodyPart, BoneT>, bodyPart: BodyPart) {
    super();
    const bone = bones.get(bodyPart);
    if (!bone) {
      console.log(bones);
      throw 'Couldnt find bone ' + BodyPart[bodyPart];
    }
    this.boneT = bone;
    this.name = BodyPart[bodyPart];
    this.updateData(bones);
  }

  updateData(bones: Map<BodyPart, BoneT>) {
    const parent = BoneKind.parent(this.boneT.bodyPart);
    const parentBone = parent === null ? undefined : bones.get(parent);
    this.setRotationFromQuaternion(
      QuaternionFromQuatT(this.boneT.rotationG).multiply(
        parentBone === undefined
          ? new Quaternion().identity()
          : QuaternionFromQuatT(parentBone.rotationG).invert()
      )
    );
    // console.log(this.quaternion);
    // console.log(
    //   parentBone === undefined
    //     ? new Vector3(0, 0, 0)
    //     : Vector3FromVec3fT(parentBone.headPositionG),
    //   Vector3FromVec3fT(this.boneT.headPositionG)
    // );
    const localPosition = Vector3FromVec3fT(this.boneT.headPositionG).sub(
      Vector3FromVec3fT(
        parentBone === undefined
          ? new Vector3(0, 0, 0)
          : Vector3FromVec3fT(parentBone.headPositionG)
      )
    );
    this.position.set(localPosition.x, localPosition.y, localPosition.z);
    // console.log(this.position);
  }

  static root: BodyPart = BodyPart.HEAD;

  static children(part: BodyPart): BodyPart[] {
    switch (part) {
      case BodyPart.NONE:
        throw 'Unexpected body part';
      case BodyPart.HEAD:
        return [BodyPart.NECK];
      case BodyPart.NECK:
        return [
          BodyPart.UPPER_CHEST,
          BodyPart.LEFT_SHOULDER,
          BodyPart.RIGHT_SHOULDER,
        ];
      case BodyPart.UPPER_CHEST:
        return [BodyPart.CHEST];
      case BodyPart.CHEST:
        return [BodyPart.WAIST];
      case BodyPart.WAIST:
        return [BodyPart.HIP];
      case BodyPart.HIP:
        return [BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG];
      case BodyPart.LEFT_UPPER_LEG:
        return [BodyPart.LEFT_LOWER_LEG];
      case BodyPart.RIGHT_UPPER_LEG:
        return [BodyPart.RIGHT_LOWER_LEG];
      case BodyPart.LEFT_LOWER_LEG:
        return [BodyPart.LEFT_FOOT];
      case BodyPart.RIGHT_LOWER_LEG:
        return [BodyPart.RIGHT_FOOT];
      case BodyPart.LEFT_FOOT:
        return [];
      case BodyPart.RIGHT_FOOT:
        return [];

      case BodyPart.LEFT_SHOULDER:
        return [BodyPart.LEFT_UPPER_ARM];
      case BodyPart.RIGHT_SHOULDER:
        return [BodyPart.RIGHT_UPPER_ARM];
      case BodyPart.LEFT_UPPER_ARM:
        return [BodyPart.LEFT_LOWER_ARM];
      case BodyPart.RIGHT_UPPER_ARM:
        return [BodyPart.RIGHT_LOWER_ARM];
      case BodyPart.LEFT_LOWER_ARM:
        return [BodyPart.LEFT_HAND];
      case BodyPart.RIGHT_LOWER_ARM:
        return [BodyPart.RIGHT_HAND];
      case BodyPart.LEFT_HAND:
        return [];
      case BodyPart.RIGHT_HAND:
        return [];
    }
  }

  static parent(part: BodyPart): BodyPart | null {
    switch (part) {
      case BodyPart.NONE:
        throw 'Unexpected body part';
      case BodyPart.HEAD:
        return null;
      case BodyPart.NECK:
        return BodyPart.HEAD;
      case BodyPart.UPPER_CHEST:
        return BodyPart.NECK;
      case BodyPart.CHEST:
        return BodyPart.UPPER_CHEST;
      case BodyPart.WAIST:
        return BodyPart.CHEST;
      case BodyPart.HIP:
        return BodyPart.WAIST;

      case BodyPart.LEFT_UPPER_LEG:
        return BodyPart.HIP;
      case BodyPart.RIGHT_UPPER_LEG:
        return BodyPart.HIP;
      case BodyPart.LEFT_LOWER_LEG:
        return BodyPart.LEFT_UPPER_LEG;
      case BodyPart.RIGHT_LOWER_LEG:
        return BodyPart.RIGHT_UPPER_LEG;
      case BodyPart.LEFT_FOOT:
        return BodyPart.LEFT_LOWER_LEG;
      case BodyPart.RIGHT_FOOT:
        return BodyPart.RIGHT_LOWER_LEG;

      case BodyPart.LEFT_SHOULDER:
        return BodyPart.HIP;
      case BodyPart.RIGHT_SHOULDER:
        return BodyPart.HIP;
      case BodyPart.LEFT_UPPER_ARM:
        return BodyPart.LEFT_SHOULDER;
      case BodyPart.RIGHT_UPPER_ARM:
        return BodyPart.RIGHT_SHOULDER;
      case BodyPart.LEFT_LOWER_ARM:
        return BodyPart.LEFT_UPPER_ARM;
      case BodyPart.RIGHT_LOWER_ARM:
        return BodyPart.RIGHT_UPPER_ARM;
      case BodyPart.LEFT_HAND:
        return BodyPart.LEFT_LOWER_ARM;
      case BodyPart.RIGHT_HAND:
        return BodyPart.RIGHT_LOWER_ARM;
    }
  }
}

function createChildren(
  bones: Map<BodyPart, BoneT>,
  body: BodyPart,
  parentBone?: BoneKind
): (BoneKind | Bone)[] {
  if (bones.size === 0) return [new Bone()];
  const childrenBodies = BoneKind.children(body);
  const parent = new BoneKind(bones, body);
  parentBone?.add(parent);
  if (childrenBodies.length === 0) return [parent];

  const children = childrenBodies.flatMap((bodyPart) =>
    createChildren(bones, bodyPart, parent)
  );
  return [parent, ...children];
}

export function SkeletonVisualizerWidget() {
  const { bones: _bones } = useAppContext();

  const bones = useMemo(
    () => new Map(_bones.map((b) => [b.bodyPart, b])),
    [_bones]
  );

  const skeleton = useRef<Bone[]>();

  useEffect(() => {
    skeleton.current = createChildren(bones, BoneKind.root);
  }, [bones.size]);

  useEffect(() => {
    skeleton.current?.forEach(
      (bone) => bone instanceof BoneKind && bone.updateData(bones)
    );
  }, [bones]);

  if(!skeleton.current) return <></>

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Canvas
        className="container"
        style={{ height: 400, background: 'transparent' }}
        onCreated={({ camera }) => {
          (camera as PerspectiveCamera).fov = 60;
        }}
      >
        <group scale={2}>
          <skeletonHelper args={[skeleton.current[0]]}></skeletonHelper>
        </group>
        <primitive object={skeleton.current[0]} />
        <OrbitControls />
      </Canvas>
    </div>
  );
}
