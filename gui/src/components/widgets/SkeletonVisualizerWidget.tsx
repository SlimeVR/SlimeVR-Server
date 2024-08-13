import { Canvas, Object3DNode, extend, useThree } from '@react-three/fiber';
import { useAppContext } from '@/hooks/app';
import { Bone } from 'three';
import { useMemo, useEffect, useRef, useState } from 'react';
import {
  OrbitControls,
  OrthographicCamera,
  PerspectiveCamera,
} from '@react-three/drei';
import {
  BoneKind,
  createChildren,
  BasedSkeletonHelper,
} from '@/utils/skeletonHelper';
import * as THREE from 'three';
import { BodyPart, BoneT } from 'solarxr-protocol';
import { QuaternionFromQuatT, isIdentity } from '@/maths/quaternion';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import { useLocalization } from '@fluent/react';
import { ErrorBoundary } from 'react-error-boundary';
import { Typography } from '@/components/commons/Typography';

extend({ BasedSkeletonHelper });

declare module '@react-three/fiber' {
  interface ThreeElements {
    basedSkeletonHelper: Object3DNode<
      BasedSkeletonHelper,
      typeof BasedSkeletonHelper
    >;
  }
}

const GROUND_COLOR = '#4444aa';
const FRUSTUM_SIZE = 10;
const FACTOR = 2;
// Not currently used but nice to have
export function OrthographicCameraWrapper() {
  const { size } = useThree();
  const aspect = useMemo(() => size.width / size.height, [size]);

  return (
    <OrthographicCamera
      makeDefault
      zoom={200}
      top={FRUSTUM_SIZE / FACTOR}
      bottom={FRUSTUM_SIZE / -FACTOR}
      left={(0.5 * FRUSTUM_SIZE * aspect) / -FACTOR}
      right={(0.5 * FRUSTUM_SIZE * aspect) / FACTOR}
      near={0.1}
      far={1000}
      position={[25, 75, 50]}
    />
  );
}

export function SkeletonHelper({ object }: { object: Bone }) {
  const { size } = useThree();
  const res = useMemo(() => new THREE.Vector2(size.width, size.height), [size]);

  return (
    <basedSkeletonHelper
      frustumCulled={false}
      resolution={res}
      args={[object]}
    />
  );
}

// Just need to know the length of the total body, so don't need right legs
const Y_PARTS = [
  BodyPart.NECK,
  BodyPart.UPPER_CHEST,
  BodyPart.CHEST,
  BodyPart.WAIST,
  BodyPart.HIP,
  BodyPart.LEFT_UPPER_LEG,
  BodyPart.LEFT_LOWER_LEG,
];

interface SkeletonVisualizerWidgetProps {
  height?: number | string;
  maxHeight?: number | string;
}

export function ToggleableSkeletonVisualizerWidget(
  props: SkeletonVisualizerWidgetProps
) {
  const { l10n } = useLocalization();
  const [enabled, setEnabled] = useState(false);

  useEffect(() => {
    const state = localStorage.getItem('skeletonModelPreview');
    if (state) setEnabled(state === 'true');
  }, []);

  return (
    <>
      {!enabled && (
        <Button
          variant="secondary"
          className="w-full"
          onClick={() => {
            setEnabled(true);
            localStorage.setItem('skeletonModelPreview', 'true');
          }}
        >
          {l10n.getString('widget-skeleton_visualizer-preview')}
        </Button>
      )}
      {enabled && (
        <div className="flex flex-col gap-2">
          <Button
            className="w-full"
            variant="secondary"
            onClick={() => {
              setEnabled(false);
              localStorage.setItem('skeletonModelPreview', 'false');
            }}
          >
            {l10n.getString('widget-skeleton_visualizer-hide')}
          </Button>
          <SkeletonVisualizerWidget {...props} />
        </div>
      )}
    </>
  );
}

export function SkeletonVisualizerWidget({
  height = '35vh',
  maxHeight = 400,
}: SkeletonVisualizerWidgetProps) {
  const { bones: _bones } = useAppContext();

  const { l10n } = useLocalization();
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
    // If I know the head position, don't use an offset
    if (hmd?.headPositionG?.y !== undefined && hmd.headPositionG?.y > 0) {
      return 0;
    }
    const yLength = Y_PARTS.map((x) => bones.get(x));
    if (yLength.some((x) => x === undefined)) return 0;
    return (yLength as BoneT[]).reduce((prev, cur) => prev + cur.boneLength, 0);
  }, [bones]);

  const bonesInitialized = bones.size > 0;

  const targetCamera = useMemo(() => {
    const hmd = bones.get(BodyPart.HEAD);
    if (hmd?.headPositionG?.y && hmd.headPositionG.y > 0) {
      return hmd.headPositionG.y / 2;
    }
    return heightOffset / 2;
  }, [bonesInitialized]);

  const yawReset = useMemo(() => {
    const hmd = bones.get(BodyPart.HEAD);
    const chest = bones.get(BodyPart.UPPER_CHEST);
    // Check if HMD is identity, if it's then use upper chest's rotation
    const quat = isIdentity(hmd?.rotationG)
      ? QuaternionFromQuatT(chest?.rotationG).normalize().invert()
      : QuaternionFromQuatT(hmd?.rotationG).normalize().invert();

    // Project quat to (0x, 1y, 0z)
    const VEC_Y = new THREE.Vector3(0, 1, 0);
    const vec = VEC_Y.multiplyScalar(
      new THREE.Vector3(quat.x, quat.y, quat.z).dot(VEC_Y) / VEC_Y.lengthSq()
    );
    return new THREE.Quaternion(vec.x, vec.y, vec.z, quat.w).normalize();
  }, [bonesInitialized]);

  const scale = useMemo(
    () => Math.max(1.8, heightOffset) / 1.8,
    [heightOffset]
  );

  if (!skeleton.current) return <></>;
  return (
    <div className="bg-background-60 flex flex-col p-3 rounded-lg gap-2">
      <ErrorBoundary
        fallback={
          <Typography color="primary" textAlign="text-center">
            {l10n.getString('tips-failed_webgl')}
          </Typography>
        }
      >
        <Canvas
          className={classNames('container mx-auto')}
          style={{ height, background: 'transparent', maxHeight }}
        >
          <gridHelper args={[10, 50, GROUND_COLOR, GROUND_COLOR]} />
          <group position={[0, heightOffset, 0]} quaternion={yawReset}>
            <SkeletonHelper object={skeleton.current[0]}></SkeletonHelper>
          </group>
          <primitive object={skeleton.current[0]} />
          <PerspectiveCamera
            makeDefault
            position={[3, 2.5, -3]}
            fov={20}
            zoom={1 / scale}
          />
          <OrbitControls
            target={[0, targetCamera, 0]}
            maxDistance={20}
            maxPolarAngle={Math.PI / 2}
          />
        </Canvas>
      </ErrorBoundary>
    </div>
  );
}
