import { Vec3fT } from 'solarxr-protocol';
import { Vector3 } from 'three';

export type Vector3Object = { x: number; y: number; z: number };

export function Vector3FromVec3fT(vec?: Vector3Object | null) {
  return vec ? new Vector3(vec.x, vec.y, vec.z) : new Vector3();
}

export function Vector3ToVec3fT(q: Vector3Object) {
  const vec = new Vec3fT();
  vec.x = q.x;
  vec.y = q.y;
  vec.z = q.z;
  return vec;
}
