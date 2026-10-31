export const DEG_TO_RAD = Math.PI / 180.0;

export const RAD_TO_DEG = 180.0 / Math.PI;

export function angleIsNearZero(angle: number, maxError = 1e-6): boolean {
  return Math.abs(angle) < maxError;
}
