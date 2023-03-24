import { useState } from 'react';
import { TrackerDataT } from 'solarxr-protocol';
import { useTracker } from '../../hooks/tracker';
import { Typography } from '../commons/Typography';
import { formatVector3 } from '../utils/formatting';
import { Canvas, useLoader } from '@react-three/fiber';
import * as THREE from 'three';
import { PerspectiveCamera } from 'three';
import { Button } from '../commons/Button';
import { QuatObject } from '../../maths/quaternion';
import { useLocalization } from '@fluent/react';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader';

const groundColor = '#4444aa';

const scale = 0.05;

export function TrackerModel() {
  const gltf = useLoader(GLTFLoader, '/models/tracker.gltf', (loader) => {
    const dracoLoader = new DRACOLoader();
    dracoLoader.setDecoderPath('/draco/');
    loader.setDRACOLoader(dracoLoader);
  });
  return (
    <group scale={scale} rotation={[Math.PI/2, 0, 0]}>
      <primitive object={gltf.scene} />
    </group>
  );
}

function SceneRenderer({ x, y, z, w }: QuatObject) {
  return (
    <Canvas
      className="container"
      style={{ height: 200, background: 'transparent' }}
      onCreated={({ camera }) => {
        (camera as PerspectiveCamera).fov = 60;
      }}
    >
      <ambientLight intensity={0.5} />
      <spotLight position={[20, 20, 20]} angle={0.09} penumbra={1} />
      <group quaternion={new THREE.Quaternion(x, y, z, w)}>
        <TrackerModel></TrackerModel>
        <axesHelper args={[10]} />
      </group>

      <mesh position={[0, -3, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <planeGeometry args={[50, 50, 10, 10]} />
        <meshBasicMaterial
          wireframe
          color={groundColor}
          transparent
          opacity={0.2}
          side={THREE.DoubleSide}
        />
      </mesh>
    </Canvas>
  );
}

export function IMUVisualizerWidget({ tracker }: { tracker: TrackerDataT }) {
  const { l10n } = useLocalization();
  const [enabled, setEnabled] = useState(false);

  const { useRawRotationEulerDegrees, useIdentAdjRotationEulerDegrees } =
    useTracker(tracker);

  const rotationRaw = useRawRotationEulerDegrees();
  const rotationIdent = useIdentAdjRotationEulerDegrees() || rotationRaw;
  const quat =
    tracker?.rotationIdentityAdjusted ||
    tracker?.rotation ||
    new THREE.Quaternion();

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Typography variant="section-title">
        {l10n.getString('widget-imu_visualizer')}
      </Typography>

      <div className="flex justify-between">
        <Typography color="secondary">
          {l10n.getString('widget-imu_visualizer-rotation_raw')}
        </Typography>
        <Typography>{formatVector3(rotationRaw, 2)}</Typography>
      </div>

      <div className="flex justify-between">
        <Typography color="secondary">
          {l10n.getString('widget-imu_visualizer-rotation_preview')}
        </Typography>
        <Typography>{formatVector3(rotationIdent, 2)}</Typography>
      </div>

      {!enabled && (
        <Button variant="secondary" onClick={() => setEnabled(true)}>
          {l10n.getString('widget-imu_visualizer-rotation_preview')}
        </Button>
      )}
      {enabled && <SceneRenderer {...quat}></SceneRenderer>}
    </div>
  );
}
