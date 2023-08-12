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
import { BodyPart, BoneT } from 'solarxr-protocol';

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
const frustumSize = 10;
const factor = 2;

function OrthographicCameraWrapper() {
  const { size } = useThree();
  const aspect = useMemo(() => size.width / size.height, [size]);

  return (
    <OrthographicCamera
      makeDefault
      zoom={200}
      top={frustumSize / factor}
      bottom={frustumSize / -factor}
      left={(0.5 * frustumSize * aspect) / -factor}
      right={(0.5 * frustumSize * aspect) / factor}
      near={0.1}
      far={1000}
      position={[25, 75, 50]}
    />
  );
}

const yParts = [
  BodyPart.NECK,
  BodyPart.UPPER_CHEST,
  BodyPart.CHEST,
  BodyPart.WAIST,
  BodyPart.HIP,
  BodyPart.LEFT_UPPER_LEG,
  BodyPart.LEFT_LOWER_LEG,
];

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

  const heightOffset = useMemo(() => {
    const hmd = bones.get(BodyPart.HEAD);
    if (hmd?.headPositionG?.y === undefined || hmd.headPositionG?.y > 0) {
      return 0;
    }
    const yLength = yParts.map((x) => bones.get(x));
    if (yLength.some((x) => x === undefined)) return 0;
    return (yLength as BoneT[]).reduce((prev, cur) => prev + cur.boneLength, 0);
  }, [bones]);

  const targetCamera = useMemo(() => {
    const hmd = bones.get(BodyPart.HEAD);
    if (hmd?.headPositionG?.y && hmd.headPositionG.y > 0) {
      return hmd.headPositionG.y / 2;
    }

    return heightOffset/ 2;
  }, [bones]);

  if (!skeleton.current) return <></>;
  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Canvas
        className="container"
        style={{ height: 400, background: 'transparent' }}
      >
        <mesh position={[0, 0, 0]} rotation={[-Math.PI / 2, 0, 0]}>
          <planeGeometry args={[10, 10, 50, 50]} />
          <meshBasicMaterial
            wireframe
            color={groundColor}
            transparent
            opacity={0.2}
            side={THREE.DoubleSide}
          />
        </mesh>
        <group position={[0, heightOffset, 0]}>
          <basedSkeletonHelper
            frustumCulled={false}
            args={[skeleton.current[0]]}
          ></basedSkeletonHelper>
        </group>
        <primitive object={skeleton.current[0]} />
        <OrbitControls target={[0, targetCamera, 0]} />
        <OrthographicCameraWrapper />
      </Canvas>
    </div>
  );
}
