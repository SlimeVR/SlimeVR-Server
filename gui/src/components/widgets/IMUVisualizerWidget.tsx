import { useMemo, useState } from 'react';
import { TrackerDataT } from 'solarxr-protocol';
import { useTracker } from '../../hooks/tracker';
import { Typography } from '../commons/Typography';
import { formatVector3 } from '../utils/formatting';
import { Canvas, useThree } from '@react-three/fiber';
import * as THREE from 'three';
import { CanvasTexture, PerspectiveCamera } from 'three';
import { Button } from '../commons/Button';
import { QuatObject } from '../../maths/quaternion';
import { useLocalization } from '@fluent/react';

const groundColor = '#4444aa';
const R = '#f01662';
const G = '#92ff1a';
const B = '#00b0ff';

const scale = 1.4;
const width = 2 * scale;
const height = 1 * scale;
const depth = 3 * scale;
const defaultFaces = ['Right', 'Left', 'Top', 'Bottom', 'Back', 'Front'];
const faceParams = [
  { scaleX: depth, scaleY: height, color: R }, // left right
  { scaleX: width, scaleY: depth, color: G }, // top bottom
  { scaleX: width, scaleY: height, color: B }, // front back
];

type FaceTypeProps = {
  index: number;
  scaleX: number;
  scaleY: number;
  resolution?: number;
  font?: string;
  opacity?: number;
  color?: string;
  hoverColor?: string;
  textColor?: string;
  strokeColor?: string;
  faces?: string[];
};

const FaceMaterial = ({
  index,
  scaleX = 1,
  scaleY = 1,
  resolution = 128,
  font = 'bold 46px monospace',
  faces = defaultFaces,
  color = '#f0f0f0',
  textColor = 'black',
  strokeColor = 'black',
  opacity = 1,
}: FaceTypeProps) => {
  const gl = useThree((state) => state.gl);
  const texture = useMemo(() => {
    const canvas = document.createElement('canvas');
    canvas.width = resolution * scaleX;
    canvas.height = resolution * scaleY;
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const context = canvas.getContext('2d')!;
    context.fillStyle = color;
    context.fillRect(0, 0, canvas.width, canvas.height);
    context.strokeStyle = strokeColor;
    context.lineWidth = 2;
    context.strokeRect(0, 0, canvas.width, canvas.height);
    context.font = font;
    context.textAlign = 'center';
    context.fillStyle = textColor;
    context.fillText(
      faces[index].toUpperCase(),
      canvas.width / 2,
      canvas.height / 2 + 20
    );
    return new CanvasTexture(canvas);
  }, [index, faces, font, color, textColor, strokeColor]);
  return (
    <meshLambertMaterial
      map={texture}
      map-encoding={gl.outputEncoding}
      map-anisotropy={gl.capabilities.getMaxAnisotropy() || 1}
      attach={`material-${index}`}
      color={'white'}
      transparent
      opacity={opacity}
    />
  );
};

const FaceCube = (props: Partial<FaceTypeProps>) => (
  <mesh>
    {[...Array(6)].map((_, i) => (
      <FaceMaterial key={i} {...props} index={i} {...faceParams[(i / 2) | 0]} />
    ))}
    <boxGeometry args={[width, height, depth]} />
  </mesh>
);

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
        <FaceCube />
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
  const quat = tracker?.rotationIdentityAdjusted || new THREE.Quaternion();

  const { useRawRotationEulerDegrees, useRefAdjRotationEulerDegrees } =
    useTracker(tracker);

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Typography variant="section-title">
        {l10n.getString('widget-imu_visualizer')}
      </Typography>

      <div className="flex justify-between">
        <Typography color="secondary">
          {l10n.getString('widget-imu_visualizer-rotation_raw')}
        </Typography>
        <Typography>
          {formatVector3(useRawRotationEulerDegrees(), 2)}
        </Typography>
      </div>

      <div className="flex justify-between">
        <Typography color="secondary">
          {l10n.getString('widget-imu_visualizer-rotation_adjusted')}
        </Typography>
        <Typography>
          {formatVector3(useRefAdjRotationEulerDegrees(), 2)}
        </Typography>
      </div>

      {!enabled && (
        <Button variant="secondary" onClick={() => setEnabled(true)}>
          Preview
        </Button>
      )}
      {enabled && <SceneRenderer {...quat}></SceneRenderer>}
    </div>
  );
}
