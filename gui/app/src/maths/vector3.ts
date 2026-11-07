import { Vec3fT } from 'solarxr-protocol';
import { Vector3 } from 'three';

export type Vector3Object = { x: number; y: number; z: number };

export function averageVector(vecs: Vector3[]) {
  if (vecs.length === 0) return new Vector3();
  const sum = vecs.reduce((prev, curr) => prev.add(curr), new Vector3());
  return sum.divideScalar(vecs.length);
}

export function Vector3FromVec3fT(vec?: Vector3Object | null) {
  return vec ? new Vector3(vec.x, vec.y, vec.z) : new Vector3();
}

export function roundVector3(vec: Vector3Object, decimals = 4): Vector3Object {
  return {
    x: +vec.x.toFixed(decimals),
    y: +vec.y.toFixed(decimals),
    z: +vec.z.toFixed(decimals),
  };
}

export function Vector3ToVec3fT(q: Vector3Object) {
  const vec = new Vec3fT();
  vec.x = q.x;
  vec.y = q.y;
  vec.z = q.z;
  return vec;
}
