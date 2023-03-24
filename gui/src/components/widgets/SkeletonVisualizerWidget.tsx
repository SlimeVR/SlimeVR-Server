import { Canvas } from '@react-three/fiber';
import { useAppContext } from '../../hooks/app';
import { PerspectiveCamera } from 'three';

export function SkeletonVisualizerWidget() {
  const { bones } = useAppContext();

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2">
      <Canvas
        className="container"
        style={{ height: 200, background: 'transparent' }}
        onCreated={({ camera }) => {
          (camera as PerspectiveCamera).fov = 60;
        }}
      ></Canvas>
    </div>
  );
}
