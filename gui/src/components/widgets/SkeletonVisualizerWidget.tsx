import { Canvas, Object3DNode, extend, useThree } from '@react-three/fiber';
import { useAppContext } from '@/hooks/app';
import { Bone } from 'three';
import { useMemo, useEffect, useRef } from 'react';
import { OrbitControls, OrthographicCamera } from '@react-three/drei';
import {
  BoneKind,
  createChildren,
  BasedSkeletonHelper,
} from '../../utils/skeletonHelper';
import * as THREE from 'three';

extend({ BasedSkeletonHelper });

declare module '@react-three/fiber' {
  interface ThreeElements {
    basedSkeletonHelper: Object3DNode<
      BasedSkeletonHelper,
      typeof BasedSkeletonHelper
    >;
  }
}

const groundColor = '#4444aa';
const frustumSize = 600;

function OrthographicCameraWrapper() {
  const { size } = useThree();
  const aspect = useMemo(() => size.width / size.height, [size]);

  return (
    <OrthographicCamera
      makeDefault
      zoom={90}
      top={frustumSize / 2}
      bottom={frustumSize / -2}
      left={(0.5 * frustumSize * aspect) / -2}
      right={(0.5 * frustumSize * aspect) / 2}
      near={0.1}
      far={1000}
      position={[50, 150, 100]}
    />
  );
}

export function SkeletonVisualizerWidget() {
  const { bones: _bones } = useAppContext();

  const bones = useMemo(
    () => new Map(_bones.map((b) => [b.bodyPart, b])),
    [JSON.stringify(_bones)]
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
      >
        <mesh position={[0, -3, 0]} rotation={[-Math.PI / 2, 0, 0]}>
          <planeGeometry args={[10, 10, 50, 50]} />
          <meshBasicMaterial
            wireframe
            color={groundColor}
            transparent
            opacity={0.2}
            side={THREE.DoubleSide}
          />
        </mesh>
        <group scale={2}>
          <basedSkeletonHelper
            frustumCulled={false}
            args={[skeleton.current[0]]}
          ></basedSkeletonHelper>
        </group>
        <primitive object={skeleton.current[0]} />
        <OrbitControls />
        <OrthographicCameraWrapper />
      </Canvas>
    </div>
  );
}
