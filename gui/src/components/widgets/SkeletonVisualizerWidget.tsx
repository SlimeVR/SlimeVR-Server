import { Canvas } from '@react-three/fiber';
import { useAppContext } from '@/hooks/app';
import { Bone, PerspectiveCamera, Quaternion, Skeleton } from 'three';
import { getHelperFromSkeleton } from 'three/examples/jsm/utils/SkeletonUtils';
import { useMemo } from 'react';
import { Vector3FromVec3fT } from '@/maths/vector3';
import { QuaternionFromQuatT } from '@/maths/quaternion';

export const threeSkeleton = (() => {
  const bones = [];

  const head = new Bone();
  const neck = new Bone();
  head.add(neck);

  const chest = new Bone();
  neck.add(chest);
  const waist = new Bone();
  chest.add(waist);
  const hip = new Bone();
  waist.add(hip);

  const leftShoulder = new Bone();
  neck.add(leftShoulder);
  const leftUpperArm = new Bone();
  leftShoulder.add(leftUpperArm);
  const leftLowerArm = new Bone();
  leftUpperArm.add(leftLowerArm);
  const leftHand = new Bone();
  leftLowerArm.add(leftHand);

  const rightShoulder = new Bone();
  neck.add(rightShoulder);
  const rightUpperArm = new Bone();
  rightShoulder.add(rightUpperArm);
  const rightLowerArm = new Bone();
  rightUpperArm.add(rightLowerArm);
  const rightHand = new Bone();
  rightLowerArm.add(rightHand);

  const leftUpperLeg = new Bone();
  hip.add(leftUpperLeg);
  const leftLowerLeg = new Bone();
  leftUpperLeg.add(leftLowerLeg);
  const leftFoot = new Bone();
  leftLowerLeg.add(leftFoot);

  const rightUpperLeg = new Bone();
  hip.add(rightUpperLeg);
  const rightLowerLeg = new Bone();
  rightUpperLeg.add(rightLowerLeg);
  const rightFoot = new Bone();
  rightLowerLeg.add(rightFoot);

  bones.push(
    head,
    neck,
    chest,
    waist,
    hip,
    leftUpperLeg,
    rightUpperLeg,
    leftLowerLeg,
    rightLowerLeg,
    leftFoot,
    rightFoot,
    leftLowerArm,
    rightLowerArm,
    leftUpperArm,
    rightUpperArm,
    leftHand,
    rightHand,
    leftShoulder,
    rightShoulder
  );

  return new Skeleton(bones);
})();

export function SkeletonVisualizerWidget() {
  const { bones } = useAppContext();

  const skeleton = useMemo(() => {
    const skeleton = threeSkeleton.clone();
    for (const solarBone of bones) {
      const bone = skeleton.bones[solarBone.bodyPart - 1];
      const localPos = bone.worldToLocal(
        Vector3FromVec3fT(solarBone.headPositionG)
      );
      bone.position.set(localPos.x, localPos.y, localPos.z);

      if (!bone.parent) {
        bone.applyQuaternion(QuaternionFromQuatT(solarBone.rotationG));
        continue;
      }

      const parentQuatInvert = bone.parent
        .getWorldQuaternion(new Quaternion())
        .invert();
      bone.quaternion.multiplyQuaternions(
        QuaternionFromQuatT(solarBone.rotationG),
        parentQuatInvert
      );
    }
    return getHelperFromSkeleton(skeleton);
  }, [bones]);

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Canvas
        className="container"
        style={{ height: 200, background: 'transparent' }}
        onCreated={({ camera }) => {
          (camera as PerspectiveCamera).fov = 60;
        }}
      >
        <group scale={0.04}>
          <primitive object={skeleton}></primitive>
        </group>
      </Canvas>
    </div>
  );
}
