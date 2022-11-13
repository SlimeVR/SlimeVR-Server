import { Quaternion } from 'math3d';
import { QuatT } from 'solarxr-protocol';

export function QuaternionFromQuatT(q: {
  x: number;
  y: number;
  z: number;
  w: number;
}) {
  return new Quaternion(q.x, q.y, q.z, q.w);
}

export function QuaternionToQuatT(q: {
  x: number;
  y: number;
  z: number;
  w: number;
}) {
  const quat = new QuatT();

  quat.x = q.x;
  quat.y = q.y;
  quat.z = q.z;
  quat.w = q.w;
  return quat;
}

export function FixEuler(yaw: number) {
  if (yaw > 180) {
    yaw *= -1;
    yaw += 180;
  }
  return Math.round(yaw);
}
