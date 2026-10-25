import { describe, expect, it } from 'vitest';
import {
  MOVEMENT_WINDOW_MS,
  stepMovement,
  type MovementHistory,
  type Quat,
} from './movement';

const IDENTITY: Quat = { x: 0, y: 0, z: 0, w: 1 };

function yaw(deg: number): Quat {
  const half = (deg * Math.PI) / 360;
  return { x: 0, y: Math.sin(half), z: 0, w: Math.cos(half) };
}

describe('stepMovement', () => {
  it('counts the first sample as still', () => {
    expect(stepMovement(undefined, yaw(90), null, 0).movement).toBe(0);
  });

  it('stays at 0 without rotation or acceleration change', () => {
    let history: MovementHistory | undefined;
    for (let at = 0; at < 1000; at += 100) {
      const step = stepMovement(history, IDENTITY, { x: 1, y: 2, z: 3 }, at);
      history = step.history;
      expect(step.movement).toBe(0);
    }
  });

  it('sums deltas inside the time window and forgets older ones', () => {
    let { history } = stepMovement(undefined, IDENTITY, null, 0);
    const moved = stepMovement(history, yaw(5), null, 100);
    history = moved.history;
    expect(moved.movement).toBeGreaterThan(0);

    // Still samples keep the earlier delta until it leaves the window
    const inside = stepMovement(history, yaw(5), null, 100 + MOVEMENT_WINDOW_MS - 1);
    expect(inside.movement).toBeCloseTo(moved.movement);
    const outside = stepMovement(
      inside.history,
      yaw(5),
      null,
      100 + MOVEMENT_WINDOW_MS
    );
    expect(outside.movement).toBe(0);
  });

  it('uses timestamps, not sample counts', () => {
    // Same motion at 10 and 90 samples per second, measured after 100ms
    const slow = stepMovement(
      stepMovement(undefined, IDENTITY, null, 0).history,
      yaw(1),
      null,
      100
    );
    let history = stepMovement(undefined, IDENTITY, null, 0).history;
    for (let i = 1; i <= 9; i++)
      history = stepMovement(history, IDENTITY, null, i * 11).history;
    const fast = stepMovement(history, yaw(1), null, 100);
    expect(fast.movement).toBeCloseTo(slow.movement);
  });

  it('caps at 1', () => {
    const { history } = stepMovement(undefined, IDENTITY, null, 0);
    const step = stepMovement(history, yaw(180), { x: 100, y: 100, z: 100 }, 10);
    expect(step.movement).toBe(1);
  });

  it('copies its inputs', () => {
    const rotation = { ...IDENTITY };
    const { history } = stepMovement(undefined, rotation, null, 0);
    rotation.w = 0;
    expect(history.rotation.w).toBe(1);
  });
});
