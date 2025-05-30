import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { GLTF, GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader';

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
import { Tween } from '@tweenjs/tween.js';
import { Vector3FromVec3fT } from '@/maths/vector3';

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

export type SkeletonPreviewView = {
  left: number;
  bottom: number;
  width: number;
  height: number;
  camera: THREE.PerspectiveCamera;
  controls: OrbitControls;
  hidden: boolean;
  tween: Tween<THREE.Vector3>;
  onHeightChange: (view: SkeletonPreviewView, newHeight: number) => void;
};

type MannequinBone = {
  bodyPart: BodyPart;
  mBone: THREE.Object3D;
  mBoneLength: number;
  invert: boolean;
};

function initializePreview(
  canvas: HTMLCanvasElement,
  skeleton: (BoneKind | THREE.Bone)[]
) {
  let lastRenderTimeRef = 0;
  let frameInterval = 0;
  let heightOffset = 0;
  let skeletonOffset = 0;

  const views: SkeletonPreviewView[] = [];

  const resolution = new THREE.Vector2(canvas.clientWidth, canvas.clientHeight);
  const scene = new THREE.Scene();
  const renderer = new THREE.WebGLRenderer({
    canvas,
    alpha: true,
    antialias: true,
  });
  renderer.setSize(canvas.clientWidth, canvas.clientHeight);

  const grid = new THREE.GridHelper(10, 50, GROUND_COLOR, GROUND_COLOR);
  grid.position.set(0, 0, 0);
  scene.add(grid);

  const skeletonGroup = new THREE.Group();
  let skeletonHelper = new BasedSkeletonHelper(skeleton[0]);
  skeletonHelper.resolution.copy(resolution);
  skeletonGroup.add(skeletonHelper);

  scene.add(skeletonGroup);
  scene.add(skeleton[0]);

  const loader = new GLTFLoader();
  let mannequin: GLTF | null = null;
  const mannequinBones: Map<BodyPart, MannequinBone> = new Map();

  // Load a glTF resource
  loader.load(
    // resource URL
    '/models/mannequin/mannequin2.gltf',
    // called when the resource is loaded
    function (gltf) {
      mannequin = gltf;

      mannequinBones.clear();

      const addBone = (
        bodyPart: BodyPart,
        boneName: string,
        boneLength: number,
        invert: boolean
      ) => {
        const mBone = gltf.scene.getObjectByName(boneName);
        if (mBone) {
          mannequinBones.set(bodyPart, {
            bodyPart,
            mBone,
            mBoneLength: boneLength,
            invert,
          });
        }
      };

      addBone(BodyPart.HIP, 'mixamorigHips', 0.105592, true);
      addBone(BodyPart.WAIST, 'mixamorigSpine', 0.100027, true);
      addBone(BodyPart.CHEST, 'mixamorigSpine1', 0.0932207, true);
      addBone(BodyPart.UPPER_CHEST, 'mixamorigSpine2', 0.137015, true);
      addBone(BodyPart.NECK, 'mixamorigNeck', 0.0976436, true);
      addBone(BodyPart.LEFT_UPPER_LEG, 'mixamorigLeftUpLeg', 0.443714, false);
      addBone(BodyPart.LEFT_LOWER_LEG, 'mixamorigLeftLeg', 0.445279, false);
      addBone(BodyPart.LEFT_FOOT, 'mixamorigLeftFoot', 0.138169, false);
      addBone(BodyPart.RIGHT_UPPER_LEG, 'mixamorigRightUpLeg', 0.443714, false);
      addBone(BodyPart.RIGHT_LOWER_LEG, 'mixamorigRightLeg', 0.445279, false);
      addBone(BodyPart.RIGHT_FOOT, 'mixamorigRightFoot', 0.138169, false);

      scene.add(gltf.scene);
      gltf.scene.traverse((c) => {
        if (
          c instanceof THREE.SkinnedMesh &&
          c.material instanceof THREE.Material
        ) {
          c.material.transparent = true;
          c.material.depthWrite = false;
          c.material.opacity = 0.3;
        }
      });
    },
    // called while loading is progressing
    function (xhr) {
      console.log((xhr.loaded / xhr.total) * 100 + '% loaded');
    },
    // called when loading has errors
    function (error) {
      console.log('An error happened');
    }
  );

  const updateManequin = (bones: Map<BodyPart, BoneT>) => {
    mannequinBones.forEach(({ bodyPart, invert, mBone, mBoneLength }) => {
      const bone = bones.get(bodyPart);
      if (!bone) {
        return;
      }
      const p = Vector3FromVec3fT(bone.headPositionG);
      const q = QuaternionFromQuatT(bone.rotationG);

      // The SlimeVR bone position is always at the top of the bone, and the
      // quaternion points "up" (why??).
      if (invert) {
        // The mannequin bone wants to point the other way, so shift the position
        // to the tail of the SlimeVR bone.
        p.sub(new THREE.Vector3(0, mBoneLength, 0).applyQuaternion(q));
      } else {
        // We need the mannequin bone rotation to point in the direction of the
        // bone, so rotate the bone around its x axis.
        q.multiply(new THREE.Quaternion(1, 0, 0, 0));
      }

      // mBone.position.copy(
      //   new THREE.Vector3(
      //     bone.headPositionG?.x,
      //     bone.headPositionG?.y ?? 0,
      //     bone.headPositionG?.z
      //   ).add(new THREE.Vector3(0, skeletonOffset, 0))
      // );
      mBone.position.copy(new THREE.Vector3(0, heightOffset, 0)).add(p);
      mBone.setRotationFromQuaternion(q);
      mBone.scale.set(1, bone.boneLength / mBoneLength, 1);
    });
  };

  const light = new THREE.HemisphereLight(0xffffff, 0x222222, 2);
  scene.add(light);

  const updateBones = (bones: Map<BodyPart, BoneT>) => {
    skeleton.forEach(
      (bone) => bone instanceof BoneKind && bone.updateData(bones)
    );
    const newHeight = computeUserHeight(bones);
    if (newHeight !== heightOffset) {
      heightOffset = newHeight;
      views.forEach((v) => {
        v.onHeightChange(v, heightOffset);
      });
    }

    const newSkeletinOffset = computeSkeletonOffset(bones);
    if (newSkeletinOffset != skeletonOffset) {
      skeletonOffset = newSkeletinOffset;
      skeletonGroup.position.set(0, skeletonOffset, 0);
    }

    if (mannequin) {
      updateManequin(bones);
    }
  };

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

  const computeUserHeight = (bones: Map<BodyPart, BoneT>) => {
    const hmd = bones.get(BodyPart.HEAD);
    if (hmd?.headPositionG?.y && hmd.headPositionG.y > 0) {
      return hmd.headPositionG.y / 0.936;
    }
    const yLength = Y_PARTS.map((x) => bones.get(x));
    if (yLength.some((x) => x === undefined)) return 0;
    return (
      (yLength as BoneT[]).reduce((prev, cur) => prev + cur.boneLength, 0) /
      0.936
    );
  };

  const computeSkeletonOffset = (bones: Map<BodyPart, BoneT>) => {
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
    views.forEach((v) => {
      if (v.hidden) return;
      v.controls.update(delta);

      const left = Math.floor(resolution.x * v.left);
      const bottom = Math.floor(resolution.y * v.bottom);
      const width = Math.floor(resolution.x * v.width);
      const height = Math.floor(resolution.y * v.height);

      renderer.setViewport(left, bottom, width, height);
      renderer.setScissor(left, bottom, width, height);
      renderer.setScissorTest(true);

      v.tween.update();

      v.camera.aspect = width / height;
      v.camera.updateProjectionMatrix();

      renderer.render(scene, v.camera);
    });
  };

  let animationFrameId: number;
  const animate = (currentTime: number) => {
    animationFrameId = requestAnimationFrame(animate);

    const now = performance.now();
    const elapsed = now - lastRenderTimeRef;
    if (elapsed < frameInterval) return;
    render(currentTime);
    lastRenderTimeRef = now - (elapsed % frameInterval);
  };

  animationFrameId = requestAnimationFrame(animate);

  // Make sure orbit controls works only on the current view
  canvas.addEventListener('pointermove', (event) => {
    const x = event.offsetX / resolution.x;
    const y = 1 - event.offsetY / resolution.y;
    views.forEach((v) => {
      if (
        x >= v.left &&
        x <= v.left + v.width &&
        y >= v.bottom &&
        y <= v.bottom + v.height
      ) {
        v.controls.enabled = true;
      } else {
        v.controls.enabled = false;
      }
    });
  });

  return {
    resize: (width: number, height: number) => {
      resolution.set(width, height);
      skeletonHelper.resolution.copy(resolution);
      renderer.setSize(width, height);
    },
    setFrameInterval: (interval: number) => {
      frameInterval = interval;
    },
    rebuildSkeleton,
    updateBones,
    destroy: () => {
      skeletonHelper.dispose();
      renderer.dispose();
      cancelAnimationFrame(animationFrameId);
    },
    addView: ({
      left,
      bottom,
      width,
      height,
      position,
      hidden = false,
      onHeightChange,
    }: {
      left: number;
      bottom: number;
      width: number;
      height: number;
      position: THREE.Vector3;
      hidden?: boolean;
      onHeightChange: (view: SkeletonPreviewView, newHeight: number) => void;
    }) => {
      const camera = new THREE.PerspectiveCamera(
        20,
        resolution.width / resolution.height,
        0.1,
        1000
      );

      const controls = new OrbitControls(camera, renderer.domElement);
      controls.maxDistance = 20;
      controls.dampingFactor = 0.2;
      controls.enableDamping = true;

      const tween = new Tween(position)
        .onUpdate(() => {
          camera.position.copy(position);
        })
        .onStart(() => (frameInterval = 0))
        .onComplete(() => (frameInterval = 1000 / LOW_FRAMERATE));

      camera.position.copy(position);

      const view: SkeletonPreviewView = {
        camera,
        left,
        bottom,
        width,
        height,
        controls,
        tween,
        hidden,
        onHeightChange,
      };

      views.push(view);

      return view;
    },
  };
}

const BASE_FRAMERATE = 60;
const LOW_FRAMERATE = 30;

type PreviewContext = ReturnType<typeof initializePreview>;

function SkeletonVisualizer({
  onInit,
}: {
  onInit: (context: PreviewContext) => void;
}) {
  const { config } = useConfig();

  const previewContext = useRef<PreviewContext | null>(null);
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
    context.updateBones(bones);
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
    context.setFrameInterval(1000 / BASE_FRAMERATE);
  };

  const onLeave = () => {
    if (config?.devSettings.fastDataFeed) return;
    const context = previewContext.current;
    if (!context) return;
    context.setFrameInterval(1000 / LOW_FRAMERATE);
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
      previewContext.current.setFrameInterval(1000 / LOW_FRAMERATE);

    const rect = containerRef.current.getBoundingClientRect();
    previewContext.current.resize(rect.width, rect.height);

    containerRef.current.addEventListener('mouseenter', onEnter);
    containerRef.current.addEventListener('mouseleave', onLeave);

    onInit(previewContext.current);

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

export function SkeletonVisualizerWidget({
  onInit = (context) => {
    context.addView({
      left: 0,
      bottom: 0,
      width: 1,
      height: 1,
      position: new THREE.Vector3(3, 2.5, -3),
      onHeightChange(v, newHeight) {
        v.controls.target.set(0, newHeight / 2, 0);
        const scale = Math.max(1, newHeight) / 1.5;
        v.camera.zoom = 1 / scale;
      },
    });
  },
}: {
  onInit?: (context: PreviewContext) => void;
}) {
  const { l10n } = useLocalization();

  return (
    <ErrorBoundary
      fallback={
        <Typography color="primary" textAlign="text-center">
          {l10n.getString('tips-failed_webgl')}
        </Typography>
      }
    >
      <SkeletonVisualizer onInit={onInit}></SkeletonVisualizer>
    </ErrorBoundary>
  );
}
