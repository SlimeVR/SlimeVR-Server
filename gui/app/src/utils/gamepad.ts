import type { Direction } from './focus-nav';

export const BUTTON_A = 0;
export const BUTTON_B = 1;

const DPAD: Record<number, Direction> = {
  12: 'up',
  13: 'down',
  14: 'left',
  15: 'right',
};

const DEADZONE_ENTER = 0.35;
const DEADZONE_EXIT = 0.25;
/** How far the other axis must lead before a diagonal hold switches axis. */
const AXIS_SWITCH_RATIO = 1.4;

export interface GamepadFrame {
  buttons: Set<number>;
  axX: number;
  axY: number;
}

export function decodeInput(pads: (Gamepad | null)[]): GamepadFrame {
  const buttons = new Set<number>();
  const stronger = (a: number, b: number) => (Math.abs(a) > Math.abs(b) ? a : b);
  let axX = 0;
  let axY = 0;

  for (const pad of pads) {
    if (!pad) continue;
    pad.buttons.forEach((b, i) => b.pressed && buttons.add(i));
    axX = stronger(pad.axes[0] ?? 0, axX);
    axY = stronger(pad.axes[1] ?? 0, axY);
  }
  return { buttons, axX, axY };
}

export function pressedDirections(
  frame: GamepadFrame,
  stickDir: Direction | null
): Set<Direction> {
  const dirs = new Set<Direction>();
  for (const i of frame.buttons) if (DPAD[i]) dirs.add(DPAD[i]);
  if (stickDir) dirs.add(stickDir);
  return dirs;
}

export function resolveStick(
  axX: number,
  axY: number,
  prev: Direction | null
): Direction | null {
  const ax = Math.abs(axX);
  const ay = Math.abs(axY);
  const mag = Math.max(ax, ay);

  if (mag < DEADZONE_EXIT) return null;
  // Between the deadzones: hold what is committed, commit to nothing new.
  if (mag < DEADZONE_ENTER && !prev) return null;

  const wasHoriz = prev === 'left' || prev === 'right';
  const wasVert = prev === 'up' || prev === 'down';
  const horiz = wasHoriz
    ? ax * AXIS_SWITCH_RATIO >= ay
    : wasVert
      ? ax >= ay * AXIS_SWITCH_RATIO
      : ax > ay;

  return horiz ? (axX > 0 ? 'right' : 'left') : axY > 0 ? 'down' : 'up';
}
