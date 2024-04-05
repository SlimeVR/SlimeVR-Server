import { Euler, Quaternion } from 'three';
import { QuatT } from 'solarxr-protocol';

export const rotationToQuatMap = {
  FRONT: new Quaternion(0, 1, 0, 0),
  FRONT_LEFT: new Quaternion(0, 0.924, 0, 0.383),
  LEFT: new Quaternion(0, 0.707, 0, 0.707),
  BACK_LEFT: new Quaternion(0, 0.383, 0, 0.924),
  FRONT_RIGHT: new Quaternion(0, -0.924, 0, 0.383),
  RIGHT: new Quaternion(0, -0.707, 0, 0.707),
  BACK_RIGHT: new Quaternion(0, -0.383, 0, 0.924),
  BACK: new Quaternion(0, 0, 0, 1),
};

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

export function MountingOrientationDegreesToQuatT(orientation: Quaternion) {
  return QuaternionToQuatT(orientation);
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

export function compareQuatT(a: QuatT | null, b: QuatT | null): boolean {
  if (!a || !b) return false;
  return a.w === b.w && a.x === b.x && a.y === b.y && a.z === b.z;
}

export function isIdentity(a?: QuatT | null): boolean {
  if (!a) return false;
  return a.w === 1 && a.x === 0 && a.y === 0 && a.z === 0;
}

export function similarQuaternions(
  a: Quaternion | null,
  b: Quaternion | null,
  tolerance = 1e-5
): boolean {
  if (!a || !b) return false;
  const len = new Quaternion(b.x - a.x, b.y - a.y, b.z - a.z, b.w - a.w).lengthSq();
  const squareSum = a.lengthSq() + b.lengthSq();

  return len <= tolerance ** 2 * squareSum;
}
