import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

import { useMemo, useEffect, useState, useRef, useLayoutEffect } from 'react';
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
import { useAtomValue } from 'jotai';
import { bonesAtom } from '@/store/app-store';
import { useConfig } from '@/hooks/config';

const GROUND_COLOR = '#4444aa';

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

export function ToggleableSkeletonVisualizerWidget({
  height,
  maxHeight,
}: SkeletonVisualizerWidgetProps) {
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
          <div
            style={{ height, maxHeight }}
            className="bg-background-60 p-1 rounded-md"
          >
            <SkeletonVisualizerWidget />
          </div>
        </div>
      )}
    </>
  );
}

type SkeletonPreviewContext = {
  resize: (width: number, height: number) => void;
  updatesBones: (bones: Map<BodyPart, BoneT>, render?: boolean) => void;
  rebuildSkeleton(
    newSkeleton: (BoneKind | THREE.Bone)[],
    bones: Map<BodyPart, BoneT>
  ): void;
  setFrameInterval: (interval: number) => void;
  destroy: () => void;
};

function initializePreview(
  canvas: HTMLCanvasElement,
  skeleton: (BoneKind | THREE.Bone)[]
): SkeletonPreviewContext {
  let lastRenderTimeRef = 0;
  let frameInterval = 0;

  const resolution = new THREE.Vector2(canvas.clientWidth, canvas.clientHeight);
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(
    20,
    resolution.width / resolution.height,
    0.1,
    1000
  );

  const renderer = new THREE.WebGLRenderer({
    canvas,
    alpha: true,
    antialias: true,
  });
  renderer.setSize(canvas.clientWidth, canvas.clientHeight);

  const controls = new OrbitControls(camera, renderer.domElement);
  controls.maxDistance = 20;
  controls.dampingFactor = 0.2;
  controls.enableDamping = true;
  controls.maxPolarAngle = Math.PI / 2;

  const grid = new THREE.GridHelper(10, 50, GROUND_COLOR, GROUND_COLOR);
  grid.position.set(0, 0, 0);
  scene.add(grid);

  camera.position.set(3, 2.5, -3);

  const skeletonGroup = new THREE.Group();
  let skeletonHelper = new BasedSkeletonHelper(skeleton[0]);
  skeletonHelper.resolution.copy(resolution);
  skeletonGroup.add(skeletonHelper);

  scene.add(skeletonGroup);
  scene.add(skeleton[0]);

  let heightOffset = 0;

  const rebuildSkeleton = (
    newSkeleton: (BoneKind | THREE.Bone)[],
    bones: Map<BodyPart, BoneT>
  ) => {
    skeletonGroup.remove(skeletonHelper);
    skeletonHelper.dispose();
    scene.remove(skeleton[0]);

    skeleton = newSkeleton;

    skeletonHelper = new BasedSkeletonHelper(newSkeleton[0]);
    skeletonHelper.resolution.copy(resolution);
    skeletonGroup.add(skeletonHelper);
    scene.add(newSkeleton[0]);

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
    const yawReset = new THREE.Quaternion(
      vec.x,
      vec.y,
      vec.z,
      quat.w
    ).normalize();

    skeletonGroup.rotation.setFromQuaternion(yawReset);
  };

  const computeHeight = (bones: Map<BodyPart, BoneT>) => {
    const hmd = bones.get(BodyPart.HEAD);
    // If I know the head position, don't use an offset
    if (hmd?.headPositionG?.y !== undefined && hmd.headPositionG?.y > 0) {
      return 0;
    }
    const yLength = Y_PARTS.map((x) => bones.get(x));
    if (yLength.some((x) => x === undefined)) return 0;
    return (yLength as BoneT[]).reduce((prev, cur) => prev + cur.boneLength, 0);
  };

  const render = (delta: number) => {
    controls.update(delta);
    renderer.render(scene, camera);
  };

  let animationFrameId: number;
  const animate = (currentTime: number) => {
    animationFrameId = requestAnimationFrame(animate);

    if (currentTime - lastRenderTimeRef > frameInterval) {
      lastRenderTimeRef = currentTime;
      render(currentTime);
    }
  };

  animationFrameId = requestAnimationFrame(animate);

  return {
    resize: (width: number, height: number) => {
      resolution.set(width, height);
      skeletonHelper.resolution.copy(resolution);
      renderer.setSize(width, height);
      camera.aspect = width / height;
      camera.updateProjectionMatrix();
    },
    setFrameInterval: (interval) => {
      frameInterval = interval;
    },
    rebuildSkeleton,
    updatesBones: (bones, forceRender = false) => {
      skeleton.forEach(
        (bone) => bone instanceof BoneKind && bone.updateData(bones)
      );
      const newHeight = computeHeight(bones);
      if (newHeight !== heightOffset) {
        heightOffset = newHeight;
        controls.target.set(0, heightOffset / 2, 0);
        const scale = Math.max(1.8, heightOffset) / 1.8;
        camera.zoom = 1 / scale;
        skeletonGroup.position.set(0, heightOffset, 0);
      }

      if (forceRender) {
        render(0);
      }
    },
    destroy: () => {
      skeletonHelper.dispose();
      renderer.dispose();
      cancelAnimationFrame(animationFrameId);
    },
  };
}

function SkeletonVisualizer() {
  const { config } = useConfig();
  const previewContext = useRef<SkeletonPreviewContext | null>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const resizeObserver = useRef(new ResizeObserver(([e]) => onResize(e)));
  const _bones = useAtomValue(bonesAtom);

  const bones = useMemo(() => {
    return new Map(_bones.map((b) => [b.bodyPart, b]));
  }, [_bones]);

  useEffect(() => {
    if (bones.size === 0) return;
    const context = previewContext.current;
    if (!context) return;
    context.rebuildSkeleton(createChildren(bones, BoneKind.root), bones);
  }, [bones.size]);

  useEffect(() => {
    const context = previewContext.current;
    if (!context) return;
    context.updatesBones(bones);
  }, [bones]);

  const onResize = (e: ResizeObserverEntry) => {
    const context = previewContext.current;
    if (!context || !containerRef.current || !canvasRef.current) return;
    context.resize(e.contentRect.width, e.contentRect.height);
  };

  const onEnter = () => {
    if (config?.devSettings.fastDataFeed) return;
    const context = previewContext.current;
    if (!context) return;
    context.setFrameInterval(1000 / 30);
  };

  const onLeave = () => {
    if (config?.devSettings.fastDataFeed) return;
    const context = previewContext.current;
    if (!context) return;
    context.setFrameInterval(1000 / 15);
  };

  useLayoutEffect(() => {
    if (!canvasRef.current || !containerRef.current)
      throw 'invalid state - no canvas or container';
    resizeObserver.current.observe(containerRef.current);

    previewContext.current = initializePreview(
      canvasRef.current,
      createChildren(bones, BoneKind.root)
    );
    if (!config?.devSettings.fastDataFeed)
      previewContext.current.setFrameInterval(1000 / 15);

    const rect = containerRef.current.getBoundingClientRect();
    previewContext.current.resize(rect.width, rect.height);

    containerRef.current.addEventListener('mouseenter', onEnter);
    containerRef.current.addEventListener('mouseleave', onLeave);

    return () => {
      if (!previewContext.current || !containerRef.current) return;
      resizeObserver.current.unobserve(containerRef.current);
      previewContext.current.destroy();

      containerRef.current.removeEventListener('mouseenter', onEnter);
      containerRef.current.removeEventListener('mouseleave', onLeave);
    };
  }, []);

  return (
    <div ref={containerRef} className={classNames('w-full h-full')}>
      <canvas ref={canvasRef} className="w-full h-full"></canvas>
    </div>
  );
}

export function SkeletonVisualizerWidget() {
  const { l10n } = useLocalization();

  return (
    <ErrorBoundary
      fallback={
        <Typography color="primary" textAlign="text-center">
          {l10n.getString('tips-failed_webgl')}
        </Typography>
      }
    >
      <SkeletonVisualizer></SkeletonVisualizer>
    </ErrorBoundary>
  );
}
