import { Euler, Quaternion } from 'three';
import { QuatT } from 'solarxr-protocol';
import { DEG_TO_RAD } from './angle';

export type QuatObject = { x: number; y: number; z: number; w: number };

export function QuaternionFromQuatT(q?: QuatObject | null) {
  return q ? new Quaternion(q.x, q.y, q.z, q.w) : new Quaternion();
}

export function QuaternionToQuatT(q: QuatObject) {
  const quat = new QuatT();
  quat.x = q.x;
  quat.y = q.y;
  quat.z = q.z;
  quat.w = q.w;
  return quat;
}

export function MountingOrientationDegreesToQuatT(
  mountingOrientationDegrees: number
) {
  return QuaternionToQuatT(
    new Quaternion().setFromEuler(
      new Euler(0, +mountingOrientationDegrees * DEG_TO_RAD, 0)
    )
  )
}

const RAD_TO_DEG = 180 / Math.PI;

export function getYawInDegrees(q?: QuatObject) {
  if (!q) return 0;

  //           X   Y    Z  Result
  // back:     0   0    0       0
  // front: -180 0.. -180     180
  // left:     0  90    0      90
  // right:    0 -90    0     -90

  const angles = new Euler().setFromQuaternion(QuaternionFromQuatT(q));
  return angles.y | 0
    ? Math.round(angles.y * RAD_TO_DEG)
    : Math.round(-angles.z * RAD_TO_DEG);
}

export function QuaternionToEulerDegrees(q?: QuatObject | null) {
  const angles = { x: 0, y: 0, z: 0 };
  if (!q) return angles;

  const a = new Euler().setFromQuaternion(new Quaternion(q.x, q.y, q.z, q.w));
  return { x: a.x * RAD_TO_DEG, y: a.y * RAD_TO_DEG, z: a.z * RAD_TO_DEG };
}
