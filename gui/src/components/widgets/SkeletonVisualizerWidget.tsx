import { Canvas, Object3DNode, extend } from '@react-three/fiber';
import { useAppContext } from '@/hooks/app';
import { Bone, PerspectiveCamera } from 'three';
import { useMemo, useEffect, useRef } from 'react';
import { OrbitControls } from '@react-three/drei';
import {
  BoneKind,
  createChildren,
  BasedSkeletonHelper,
} from '../../utils/SkeletonHelper';

extend({ BasedSkeletonHelper });

declare module '@react-three/fiber' {
  interface ThreeElements {
    basedSkeletonHelper: Object3DNode<BasedSkeletonHelper, typeof BasedSkeletonHelper>
  }
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

  if (!skeleton.current) return <></>;

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
          <basedSkeletonHelper args={[skeleton.current[0]]}></basedSkeletonHelper>
        </group>
        <primitive object={skeleton.current[0]} />
        <OrbitControls />
      </Canvas>
    </div>
  );
}
