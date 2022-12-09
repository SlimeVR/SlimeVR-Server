import Quaternion from 'quaternion';
import { QuatT } from 'solarxr-protocol';

export function QuaternionFromQuatT(q: {
  x: number;
  y: number;
  z: number;
  w: number;
}) {
  return new Quaternion(q.w, q.x, q.y, q.z);
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

export function GetYaw(q: { x: number; y: number; z: number; w: number }) {
  const squareX = q.x * q.x,
    squareY = q.y * q.y,
    squareZ = q.z * q.z,
    squareW = q.w * q.w;

  // This value is 1 if the quaternion is a unit (normalized) quaternion,
  // otherwise this will be a factor to compensate for the singularity pole checks
  const correctionFactor = squareX + squareY + squareZ + squareW;
  // This value is to test for a singularity, it will be 0.5 at the north singularity,
  // -0.5 at the south singularity, and anything else at any other value
  const singularityTest = q.x * q.y + q.z * q.w;

  // Singularity cutoff points are 0.499 (86.3 degrees) to compensate for error
  if (singularityTest > 0.499 * correctionFactor) {
    // Handle the singularity at the attitude of 90 degrees (north pole)
    return 2 * Math.atan2(q.x, q.w);
  } else if (singularityTest < -0.499 * correctionFactor) {
    // Handle the singularity at the attitude of -90 degrees (south pole)
    return -2 * Math.atan2(q.x, q.w);
  }

  // Otherwise calculate the yaw normally
  return Math.atan2(
    2 * q.y * q.w - 2 * q.x * q.z,
    squareX - squareY - squareZ + squareW
  );
}
