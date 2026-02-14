import { useEffect, useMemo, useRef, useState } from 'react';
import { TrackerDataT } from 'solarxr-protocol';
import { useTracker } from '@/hooks/tracker';
import { Typography } from '@/components/commons/Typography';
import { formatVector3 } from '@/utils/formatting';
import {
  AmbientLight,
  ArrowHelper,
  AxesHelper,
  Color,
  DoubleSide,
  Group,
  Mesh,
  MeshBasicMaterial,
  PerspectiveCamera,
  PlaneGeometry,
  Scene,
  SpotLight,
  Vector3,
  WebGLRenderer,
} from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { Button } from '@/components/commons/Button';
import { QuatObject } from '@/maths/quaternion';
import { useLocalization } from '@fluent/react';
import { Vector3Object, Vector3FromVec3fT } from '@/maths/vector3';
import { ErrorBoundary } from 'react-error-boundary';
import { StayAlignedInfo } from '@/components/stay-aligned/StayAlignedInfo';

const GROUND_COLOR = '#4444aa';
const MODEL_SCALE = 6.5;
const CANVAS_HEIGHT = 200;

// Three.js context - isolated from React
type IMUVisualizerContext = {
  scene: Scene;
  camera: PerspectiveCamera;
  renderer: WebGLRenderer;
  trackerGroup: Group;
  accelArrow: ArrowHelper;
  magArrow: ArrowHelper;
  animationId: number | null;
  update: (quat: QuatObject, vec: Vector3Object, mag: Vector3Object) => void;
  dispose: () => void;
};

async function initializeIMUVisualizer(
  canvas: HTMLCanvasElement,
  modelPath: string
): Promise<IMUVisualizerContext> {
  const scene = new Scene();

  const camera = new PerspectiveCamera(
    60,
    canvas.clientWidth / CANVAS_HEIGHT,
    0.1,
    1000
  );
  camera.position.set(0, 0, 7);

  const renderer = new WebGLRenderer({ canvas, alpha: true, antialias: true });
  renderer.setSize(canvas.clientWidth, CANVAS_HEIGHT);

  const ambientLight = new AmbientLight(0xffffff, 0.5 * Math.PI);
  scene.add(ambientLight);

  const spotLight = new SpotLight(0xffffff, 4000);
  spotLight.position.set(20, 20, 20);
  spotLight.angle = 0.09;
  spotLight.penumbra = 1;
  scene.add(spotLight);

  const trackerGroup = new Group();
  scene.add(trackerGroup);

  const loader = new GLTFLoader();
  const gltf = await loader.loadAsync(modelPath);
  const modelGroup = new Group();
  modelGroup.scale.setScalar(MODEL_SCALE);
  modelGroup.rotation.x = Math.PI / 2;
  modelGroup.add(gltf.scene);
  trackerGroup.add(modelGroup);

  const axesHelper = new AxesHelper(10);
  trackerGroup.add(axesHelper);

  const accelArrow = new ArrowHelper(
    new Vector3(0, 1, 0),
    new Vector3(0, 0, 0),
    1,
    0xffff00
  );
  scene.add(accelArrow);

  const magArrow = new ArrowHelper(
    new Vector3(0, 1, 0),
    new Vector3(0, 0, 0),
    1,
    Color.NAMES.aqua
  );
  scene.add(magArrow);

  const groundGeometry = new PlaneGeometry(50, 50, 10, 10);
  const groundMaterial = new MeshBasicMaterial({
    wireframe: true,
    color: GROUND_COLOR,
    transparent: true,
    opacity: 0.2,
    side: DoubleSide,
  });
  const ground = new Mesh(groundGeometry, groundMaterial);
  ground.position.set(0, -3, 0);
  ground.rotation.x = -Math.PI / 2;
  scene.add(ground);

  let animationId: number | null = null;
  const animate = () => {
    animationId = requestAnimationFrame(animate);
    renderer.render(scene, camera);
  };
  animate();

  const update = (quat: QuatObject, vec: Vector3Object, mag: Vector3Object) => {
    trackerGroup.quaternion.set(quat.x, quat.y, quat.z, quat.w);

    const accelVec = Vector3FromVec3fT(vec);
    const accelLength = accelVec.length();
    if (accelLength > 0) {
      accelArrow.setDirection(accelVec.normalize());
      accelArrow.setLength(Math.sqrt(accelLength) * 2);
    }

    const magVec = new Vector3(mag.x, mag.y, mag.z);
    const magLen = magVec.length();
    const magMag = Math.sqrt(magLen / 100);
    if (magLen > 0) {
      const magDir = magVec.clone().normalize();
      magArrow.position.copy(magDir.clone().multiplyScalar(-magMag));
      magArrow.setDirection(magDir);
      magArrow.setLength(2 * magMag);
    }
  };

  const dispose = () => {
    if (animationId !== null) {
      cancelAnimationFrame(animationId);
    }
    renderer.dispose();
    groundGeometry.dispose();
    groundMaterial.dispose();
    scene.clear();
  };

  return {
    scene,
    camera,
    renderer,
    trackerGroup,
    accelArrow,
    magArrow,
    animationId,
    update,
    dispose,
  };
}

function IMUVisualizerCanvas({
  quat,
  vec,
  mag,
  model,
}: {
  quat: QuatObject;
  vec: Vector3Object;
  mag: Vector3Object;
  model: string;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const contextRef = useRef<IMUVisualizerContext | null>(null);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    if (!canvasRef.current) return;

    let mounted = true;

    initializeIMUVisualizer(canvasRef.current, model)
      .then((ctx) => {
        if (mounted) {
          contextRef.current = ctx;
          ctx.update(quat, vec, mag);
        } else {
          ctx.dispose();
        }
      })
      .catch((err) => {
        if (mounted) {
          setError(err);
        }
      });

    return () => {
      mounted = false;
      contextRef.current?.dispose();
      contextRef.current = null;
    };
  }, [model]);

  useEffect(() => {
    if (contextRef.current) {
      contextRef.current.update(quat, vec, mag);
    }
  }, [quat, vec, mag]);

  if (error) {
    throw error;
  }

  return (
    <canvas
      ref={canvasRef}
      className="container"
      style={{ width: '100%', height: CANVAS_HEIGHT, background: 'transparent' }}
    />
  );
}

export function IMUVisualizerWidget({ tracker }: { tracker: TrackerDataT }) {
  const { l10n } = useLocalization();
  const [enabled, setEnabled] = useState(false);
  const isExtension = useMemo(
    () => (tracker.trackerId?.trackerNum ?? 0) > 0,
    [tracker]
  );

  useEffect(() => {
    const state = localStorage.getItem('modelPreview');
    if (state) setEnabled(state === 'true');
  }, []);

  const { useRawRotationEulerDegrees, useIdentAdjRotationEulerDegrees } =
    useTracker(tracker);

  const rotationRaw = useRawRotationEulerDegrees();
  const rotationIdent = useIdentAdjRotationEulerDegrees() || rotationRaw;

  const quat = useMemo(
    () =>
      tracker?.rotationIdentityAdjusted ||
      tracker?.rotation || { x: 0, y: 0, z: 0, w: 1 },
    [tracker?.rotationIdentityAdjusted, tracker?.rotation]
  );

  const vec = useMemo(
    () =>
      tracker?.linearAcceleration ||
      tracker?.rawAcceleration || { x: 0, y: 0, z: 0 },
    [tracker?.linearAcceleration, tracker?.rawAcceleration]
  );

  const mag = useMemo(
    () => tracker?.rawMagneticVector || { x: 0, y: 0, z: 0 },
    [tracker?.rawMagneticVector]
  );

  const model = useMemo(
    () => (isExtension ? '/models/extension.gltf' : '/models/tracker.gltf'),
    [isExtension]
  );

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Typography variant="section-title">
        {l10n.getString('widget-imu_visualizer')}
      </Typography>

      {tracker.position && (
        <div className="flex justify-between">
          <Typography>
            {l10n.getString('widget-imu_visualizer-position')}
          </Typography>
          <Typography>{formatVector3(tracker.position, 2)}</Typography>
        </div>
      )}

      <div className="flex justify-between">
        <Typography>
          {l10n.getString('widget-imu_visualizer-rotation_raw')}
        </Typography>
        <Typography>{formatVector3(rotationRaw, 2)}</Typography>
      </div>

      <div className="flex justify-between">
        <Typography>
          {l10n.getString('widget-imu_visualizer-rotation_preview')}
        </Typography>
        <Typography>{formatVector3(rotationIdent, 2)}</Typography>
      </div>

      {tracker.linearAcceleration && (
        <div className="flex justify-between">
          <Typography>
            {l10n.getString('widget-imu_visualizer-acceleration')}
          </Typography>
          <Typography>
            {formatVector3(tracker.linearAcceleration, 1)}
          </Typography>
        </div>
      )}

      {tracker.rawMagneticVector && (
        <div className="flex justify-between">
          <Typography>
            {l10n.getString('tracker-infos-magnetometer')}
          </Typography>
          <Typography>{formatVector3(tracker.rawMagneticVector, 1)}</Typography>
        </div>
      )}

      {!!tracker.stayAligned && (
        <div className="flex justify-between">
          <Typography>
            {l10n.getString('widget-imu_visualizer-stay_aligned')}
          </Typography>
          <StayAlignedInfo color="primary" tracker={tracker} />
        </div>
      )}

      {!enabled && (
        <Button
          variant="secondary"
          onClick={() => {
            setEnabled(true);
            localStorage.setItem('modelPreview', 'true');
          }}
        >
          {l10n.getString('widget-imu_visualizer-preview')}
        </Button>
      )}
      {enabled && (
        <>
          <Button
            variant="secondary"
            onClick={() => {
              setEnabled(false);
              localStorage.setItem('modelPreview', 'false');
            }}
          >
            {l10n.getString('widget-imu_visualizer-hide')}
          </Button>
          <ErrorBoundary
            fallback={
              <Typography color="primary" textAlign="text-center">
                {l10n.getString('tips-failed_webgl')}
              </Typography>
            }
          >
            <IMUVisualizerCanvas
              quat={quat}
              vec={vec}
              mag={mag}
              model={model}
            />
          </ErrorBoundary>
        </>
      )}
    </div>
  );
}
