import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

import { useMemo, useEffect, useState, useRef, useLayoutEffect } from 'react';
import {
  BoneKind,
  createChildren,
  BasedSkeletonHelper,
} from '@/utils/skeletonHelper';
import { BasedSkeletonMeshHelper } from '@/utils/skeletonMeshHelper';
import {
  computeSkeletonOffset,
  computeUserHeight,
  deriveSkeletonProportions,
} from '@/utils/skeletonProportions';
import {
  Bone,
  Color,
  DirectionalLight,
  Group,
  HemisphereLight,
  Mesh,
  PerspectiveCamera,
  PlaneGeometry,
  Quaternion,
  Scene,
  ShaderMaterial,
  Vector2,
  Vector3,
  WebGLRenderer,
} from 'three';
import { BodyPart, BoneT } from 'solarxr-protocol';
import { QuaternionFromQuatT } from '@/maths/quaternion';
import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { ErrorBoundary } from 'react-error-boundary';
import { Typography } from '@/components/commons/Typography';
import { useAtomValue } from 'jotai';
import { bonesAtom } from '@/store/app-store';
import { Config, useConfig } from '@/hooks/config';
import { Tween } from '@tweenjs/tween.js';
import { EyeIcon } from '@/components/commons/icon/EyeIcon';

type SkeletonHelper = BasedSkeletonHelper | BasedSkeletonMeshHelper;

export type SkeletonPreviewView = {
  left: number;
  bottom: number;
  width: number;
  height: number;
  camera: PerspectiveCamera;
  controls: OrbitControls;
  hidden: boolean;
  tween: Tween<Vector3>;
  onHeightChange: (view: SkeletonPreviewView, newHeight: number) => void;
};

function createRadialFloorMesh(size = 8.0): Mesh {
  const geometry = new PlaneGeometry(size, size, 1, 1);
  const material = new ShaderMaterial({
    transparent: true,
    depthWrite: false,
    uniforms: {
      uColorGround: { value: new Color('#14283d') },
      uColorGridMinor: { value: new Color('#6fa3cc') },
      uColorGridMajor: { value: new Color('#b588f7') },
      uColorRing: { value: new Color('#48e59b') },
      uColorAxis: { value: new Color('#bca5e8') },
      uRadius: { value: size / 2 },
    },
    vertexShader: `
      varying vec3 vWorldPosition;
      void main() {
        vec4 worldPos = modelMatrix * vec4(position, 1.0);
        vWorldPosition = worldPos.xyz;
        gl_Position = projectionMatrix * viewMatrix * worldPos;
      }
    `,
    fragmentShader: `
      varying vec3 vWorldPosition;
      uniform vec3 uColorGround;
      uniform vec3 uColorGridMinor;
      uniform vec3 uColorGridMajor;
      uniform vec3 uColorRing;
      uniform vec3 uColorAxis;
      uniform float uRadius;

      // Screen-space anti-aliased Cartesian grid
      float getGrid(vec2 pos, float spacing, float pixelWidth) {
        vec2 coord = pos / spacing;
        vec2 grid = abs(fract(coord - 0.5) - 0.5) / fwidth(coord);
        float line = min(grid.x, grid.y);
        return 1.0 - min(line / pixelWidth, 1.0);
      }

      // Screen-space anti-aliased radial ring
      float getRing(float dist, float radius, float pixelWidth) {
        float d = abs(dist - radius) / fwidth(dist);
        return 1.0 - min(d / pixelWidth, 1.0);
      }

      void main() {
        vec2 pos = vWorldPosition.xz;
        float dist = length(pos);
        if (dist > uRadius) discard;

        // Smooth radial horizon falloff with gentle ambient glow
        float normDist = dist / uRadius;
        float horizonFade = pow(clamp(1.0 - normDist, 0.0, 1.0), 1.2);
        float groundGlow = pow(clamp(1.0 - normDist, 0.0, 1.0), 1.8) * 0.15;

        float minorGrid = getGrid(pos, 0.5, 1.45) * 0.82;
        float majorGrid = getGrid(pos, 1.0, 2.2) * 1.00;

        // Concentric Metric Rings
        float ring05 = getRing(dist, 0.5, 1.6) * 0.65; // 0.5m standing circle
        float ring10 = getRing(dist, 1.0, 1.6) * 0.75; // 1.0m metric circle
        float ring20 = getRing(dist, 2.0, 1.5) * 0.60; // 2.0m metric circle
        float ring30 = getRing(dist, 3.0, 1.4) * 0.45; // 3.0m metric circle
        float allRings = max(ring05, max(ring10, max(ring20, ring30)));

        vec2 axisCoord = abs(pos) / fwidth(pos);
        float axisX = 1.0 - min(axisCoord.y / 2.0, 1.0);
        float axisZ = 1.0 - min(axisCoord.x / 2.0, 1.0);
        float axes = max(axisX, axisZ) * 0.70;

        vec3 col = uColorGround;
        col = mix(col, uColorGridMinor, minorGrid);
        col = mix(col, uColorGridMajor, majorGrid);
        col = mix(col, uColorRing, allRings);
        col = mix(col, uColorAxis, axes);

        float linesAlpha = max(minorGrid * 0.78, max(majorGrid * 0.98, max(allRings * 0.85, axes * 0.85)));
        float alpha = (groundGlow * 0.25 + linesAlpha) * horizonFade;
        alpha = clamp(alpha, 0.0, 0.98);

        gl_FragColor = vec4(col, alpha);
      }
    `,
  });

  const mesh = new Mesh(geometry, material);
  mesh.rotation.x = -Math.PI / 2;
  mesh.position.y = 0;
  return mesh;
}

function initializePreview(
  canvas: HTMLCanvasElement,
  skeleton: (BoneKind | Bone)[],
  style: Config['skeletonPreviewStyle']
) {
  let lastRenderTimeRef = 0;
  let frameInterval = 0;

  const views: SkeletonPreviewView[] = [];

  const resolution = new Vector2(canvas.clientWidth, canvas.clientHeight);
  const scene = new Scene();
  let renderer: WebGLRenderer | null = new WebGLRenderer({
    canvas,
    alpha: true,
    antialias: true,
  });
  renderer.setSize(canvas.clientWidth, canvas.clientHeight);

  const hemiLight = new HemisphereLight(0xdfe6ff, 0x20233a, 2.2);
  scene.add(hemiLight);
  const dirLight = new DirectionalLight(0xffffff, 1.6);
  dirLight.position.set(2, 4, 3);
  scene.add(dirLight);
  const fillLight = new DirectionalLight(0x65459a, 0.5);
  fillLight.position.set(-3, 1, -2);
  scene.add(fillLight);

  const floor = createRadialFloorMesh(6.0);
  scene.add(floor);

  const makeHelper = (root: Bone | BoneKind): SkeletonHelper => {
    if (style === 'lines') {
      const helper = new BasedSkeletonHelper(root);
      helper.resolution.copy(resolution);
      return helper;
    }
    return new BasedSkeletonMeshHelper(root);
  };

  const skeletonGroup = new Group();
  let skeletonHelper = makeHelper(skeleton[0]);
  skeletonGroup.add(skeletonHelper);

  scene.add(skeletonGroup);
  scene.add(skeleton[0]);

  let heightOffset = 0;
  let skeletonOffset = 0;

  const rebuildSkeleton = (
    newSkeleton: (BoneKind | Bone)[],
    bones: Map<BodyPart, BoneT>
  ) => {
    skeletonGroup.remove(skeletonHelper);
    skeletonHelper.dispose();
    scene.remove(skeleton[0]);

    skeleton = newSkeleton;

    skeletonHelper = makeHelper(newSkeleton[0]);
    if (skeletonHelper instanceof BasedSkeletonMeshHelper) {
      skeletonHelper.setProportions(deriveSkeletonProportions(bones));
    }
    skeletonGroup.add(skeletonHelper);
    scene.add(newSkeleton[0]);

    const hmd = bones.get(BodyPart.HEAD);
    const quat = QuaternionFromQuatT(hmd?.orientationG).normalize().invert();

    // Project quat to (0x, 1y, 0z)
    const VEC_Y = new Vector3(0, 1, 0);
    const vec = VEC_Y.multiplyScalar(
      new Vector3(quat.x, quat.y, quat.z).dot(VEC_Y) / VEC_Y.lengthSq()
    );
    const yawReset = new Quaternion(vec.x, vec.y, vec.z, quat.w).normalize();

    skeletonGroup.rotation.setFromQuaternion(yawReset);
  };

  const render = (delta: number) => {
    views.forEach((v) => {
      if (v.hidden || !renderer) return;
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
      if (skeletonHelper instanceof BasedSkeletonHelper) {
        skeletonHelper.resolution.copy(resolution);
      }
      if (!renderer) return;
      renderer.setSize(width, height);
    },
    setFrameInterval: (interval: number) => {
      frameInterval = interval;
    },
    rebuildSkeleton,
    updatesBones: (bones: Map<BodyPart, BoneT>) => {
      skeleton.forEach(
        (bone) => bone instanceof BoneKind && bone.updateData(bones)
      );
      if (skeletonHelper instanceof BasedSkeletonMeshHelper) {
        skeletonHelper.setProportions(deriveSkeletonProportions(bones));
      }
      const newHeight = computeUserHeight(bones);
      if (newHeight !== heightOffset) {
        heightOffset = newHeight;
        views.forEach((v) => {
          v.onHeightChange(v, heightOffset);
        });
      }

      const newSkeletonOffset = computeSkeletonOffset(bones);
      if (newSkeletonOffset !== skeletonOffset) {
        skeletonOffset = newSkeletonOffset;
        skeletonGroup.position.set(0, skeletonOffset, 0);
      }
    },
    destroy: () => {
      cancelAnimationFrame(animationFrameId);
      skeletonHelper.dispose();
      floor.geometry.dispose();
      (floor.material as ShaderMaterial).dispose();
      if (!renderer) return;
      renderer.dispose();
      renderer = null; // Very important for js to free the WebGL context. dispose does not to it alone
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
      position: Vector3;
      hidden?: boolean;
      onHeightChange: (view: SkeletonPreviewView, newHeight: number) => void;
    }) => {
      if (!renderer) return;

      const camera = new PerspectiveCamera(
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
  disabled = false,
}: {
  onInit: (context: PreviewContext) => void;
  disabled?: boolean;
}) {
  const { config } = useConfig();
  const style = config?.skeletonPreviewStyle ?? 'mesh';

  const previewContext = useRef<PreviewContext | null>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const resizeObserver = useRef(new ResizeObserver(([e]) => onResize(e)));
  const bonesList = useAtomValue(bonesAtom);

  const bones = useMemo(() => {
    return new Map(bonesList.map((b) => [b.bodyPart, b]));
  }, [bonesList]);

  useEffect(() => {
    if (bones.size === 0) return;
    const context = previewContext.current;
    if (!context || disabled) return;
    context.rebuildSkeleton(createChildren(bones, BoneKind.root), bones);
  }, [bones.size, disabled]);

  useEffect(() => {
    const context = previewContext.current;
    if (!context || disabled) return;
    context.updatesBones(bones);
  }, [bones, disabled]);

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
    if (disabled) return;
    if (!canvasRef.current || !containerRef.current)
      throw 'invalid state - no canvas or container';
    resizeObserver.current.observe(containerRef.current);

    previewContext.current = initializePreview(
      canvasRef.current,
      createChildren(bones, BoneKind.root),
      style
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
      previewContext.current = null;

      containerRef.current.removeEventListener('mouseenter', onEnter);
      containerRef.current.removeEventListener('mouseleave', onLeave);
    };
  }, [disabled, style]);

  return (
    <div ref={containerRef} className={classNames('w-full h-full')}>
      <canvas ref={canvasRef} className="w-full h-full" />
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
      position: new Vector3(3, 2.5, -3),
      onHeightChange(v, newHeight) {
        v.controls.target.set(0, newHeight / 2, 0);
        const scale = Math.max(1, newHeight) / 1.5;
        v.camera.zoom = 1 / scale;
      },
    });
  },
  disabled = false,
  toggleDisabled,
}: {
  onInit?: (context: PreviewContext) => void;
  disabled?: boolean;
  toggleDisabled?: () => void;
}) {
  const { l10n } = useLocalization();
  const [error, setError] = useState(false);

  return (
    <div className={classNames('w-full h-full relative')}>
      <div
        className={classNames('w-full h-full transition-all', {
          blur: disabled,
        })}
      >
        <ErrorBoundary onError={() => setError(true)} fallback={<></>}>
          <SkeletonVisualizer onInit={onInit} disabled={disabled} />
        </ErrorBoundary>
      </div>
      <div
        className={classNames(
          'absolute h-full w-full top-0 flex items-center justify-center transition-opacity duration-300',
          { 'opacity-0 pointer-events-none': !disabled || error }
        )}
      >
        <div
          className={classNames(
            'bg-background-90 rounded-lg p-2 px-3 flex gap-2 items-center',
            {
              'hover:bg-background-60 cursor-pointer': !!toggleDisabled,
              'cursor-not-allowed': !toggleDisabled,
            }
          )}
          onClick={() => toggleDisabled?.()}
        >
          <EyeIcon closed width={20} />
          <Typography id="preview-disabled_render" />
        </div>
      </div>
      <div
        className={classNames(
          'absolute h-full w-full top-0 flex items-center justify-center transition-opacity duration-300',
          { 'opacity-0 pointer-events-none': !error }
        )}
      >
        <div className="bg-background-90 rounded-lg p-2 px-3 flex gap-2 items-center">
          <Typography color="primary" textAlign="text-center">
            {l10n.getString('tips-failed_webgl')}
          </Typography>
        </div>
      </div>
    </div>
  );
}
