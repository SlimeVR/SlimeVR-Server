export interface Quat {
  x: number;
  y: number;
  z: number;
  w: number;
}

export interface Vec3 {
  x: number;
  y: number;
  z: number;
}

export interface MovementHistory {
  rotation: Quat;
  acceleration: Vec3;
  samples: { at: number; delta: number }[];
}

// Deltas are summed over this window so slow movement still registers
export const MOVEMENT_WINDOW_MS = 500;

const ZERO: Vec3 = { x: 0, y: 0, z: 0 };

// Squared vector part of current * inverse(previous), unit quaternions
function rotationDelta(current: Quat, previous: Quat) {
  const { x: ax, y: ay, z: az, w: aw } = current;
  const bx = -previous.x;
  const by = -previous.y;
  const bz = -previous.z;
  const bw = previous.w;
  const x = ax * bw + aw * bx + ay * bz - az * by;
  const y = ay * bw + aw * by + az * bx - ax * bz;
  const z = az * bw + aw * bz + ax * by - ay * bx;
  return x * x + y * y + z * z;
}

function accelerationDelta(current: Vec3, previous: Vec3) {
  const x = current.x - previous.x;
  const y = current.y - previous.y;
  const z = current.z - previous.z;
  return x * x + y * y + z * z;
}

// Adds one feed sample at `now` (ms) and returns the movement in 0..1.
// Mutates `history` when given, the first sample of a tracker counts as still.
export function stepMovement(
  history: MovementHistory | undefined,
  rotation: Quat,
  acceleration: Vec3 | null,
  now: number
): { history: MovementHistory; movement: number } {
  const acc = acceleration ?? ZERO;
  const next = history ?? { rotation, acceleration: acc, samples: [] };
  const delta = Math.min(
    1,
    rotationDelta(rotation, next.rotation) * 50 +
      accelerationDelta(acc, next.acceleration) / 1000
  );
  next.rotation = { ...rotation };
  next.acceleration = { ...acc };
  next.samples.push({ at: now, delta });
  while (next.samples[0] && next.samples[0].at <= now - MOVEMENT_WINDOW_MS) {
    next.samples.shift();
  }
  const sum = next.samples.reduce((total, sample) => total + sample.delta, 0);
  return { history: next, movement: Math.min(1, Math.max(0, sum)) };
}
