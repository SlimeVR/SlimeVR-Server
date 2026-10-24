import type { Direction } from './core';
import { motionTuning } from './tuning';

const BUTTON_A = 0;
const BUTTON_B = 1;

const DPAD: Record<number, Direction> = {
  12: 'up',
  13: 'down',
  14: 'left',
  15: 'right',
};

export interface GamepadFrame {
  buttons: Set<number>;
  axX: number;
  axY: number;
}

export interface PadCommands {
  move(dir: Direction): void;
  activate(): void;
  back(): void;
  connected(): void;
}

export function decodeInput(pads: readonly (Gamepad | null)[]): GamepadFrame {
  const buttons = new Set<number>();
  const stronger = (a: number, b: number) => (Math.abs(a) > Math.abs(b) ? a : b);
  let axX = 0;
  let axY = 0;
  for (const pad of pads) {
    if (!pad) continue;
    pad.buttons.forEach((b, i) => {
      if (b.pressed) buttons.add(i);
    });
    axX = stronger(pad.axes[0] ?? 0, axX);
    axY = stronger(pad.axes[1] ?? 0, axY);
  }
  return { buttons, axX, axY };
}

export function resolveStick(
  axX: number,
  axY: number,
  prev: Direction | null
): Direction | null {
  const ax = Math.abs(axX);
  const ay = Math.abs(axY);
  const mag = Math.max(ax, ay);
  if (mag < motionTuning.deadzoneExit) return null;
  if (mag < motionTuning.deadzoneEnter && !prev) return null;

  const ratio = motionTuning.axisSwitchRatio;
  let horizontal = ax > ay;
  if (prev === 'left' || prev === 'right') horizontal = ax * ratio >= ay;
  else if (prev === 'up' || prev === 'down') horizontal = ax >= ay * ratio;

  if (horizontal) return axX > 0 ? 'right' : 'left';
  return axY > 0 ? 'down' : 'up';
}

export function pressedDirections(
  frame: GamepadFrame,
  stickDir: Direction | null
): Set<Direction> {
  const dirs = new Set<Direction>();
  for (const i of frame.buttons) {
    const dir = DPAD[i];
    if (dir) dirs.add(dir);
  }
  if (stickDir) dirs.add(stickDir);
  return dirs;
}

export function repeatInterval(repeats: number): number {
  return Math.max(
    motionTuning.repeatMinMs,
    motionTuning.repeatStartMs - motionTuning.repeatRampMs * repeats
  );
}

interface Held {
  firedAt: number;
  repeats: number;
}

export function startGamepad(commands: PadCommands): () => void {
  if (typeof navigator === 'undefined' || !navigator.getGamepads) return () => {};

  const pads = new Set<number>();
  const held = new Map<Direction, Held>();
  let prevButtons = new Set<number>();
  let stickDir: Direction | null = null;
  let rafId = 0;

  function resetHeld() {
    held.clear();
    prevButtons = new Set();
    stickDir = null;
  }

  function pressDir(dir: Direction, now: number) {
    const state = held.get(dir);
    if (!state) {
      held.set(dir, { firedAt: now, repeats: 0 });
      commands.move(dir);
      return;
    }
    const wait =
      state.repeats === 0 ? motionTuning.repeatDelayMs : repeatInterval(state.repeats);
    if (now - state.firedAt < wait) return;
    state.firedAt = now;
    state.repeats++;
    commands.move(dir);
  }

  function poll() {
    const now = performance.now();
    const frame = decodeInput(navigator.getGamepads());
    stickDir = resolveStick(frame.axX, frame.axY, stickDir);
    const dirs = pressedDirections(frame, stickDir);

    const { buttons } = frame;
    if (buttons.has(BUTTON_A) && !prevButtons.has(BUTTON_A)) commands.activate();
    if (buttons.has(BUTTON_B) && !prevButtons.has(BUTTON_B)) commands.back();
    prevButtons = buttons;

    for (const dir of dirs) pressDir(dir, now);
    for (const dir of [...held.keys()]) {
      if (!dirs.has(dir)) held.delete(dir);
    }
    rafId = pads.size > 0 ? requestAnimationFrame(poll) : 0;
  }

  function startLoop() {
    if (!rafId) rafId = requestAnimationFrame(poll);
  }

  function onConnect(e: GamepadEvent) {
    pads.add(e.gamepad.index);
    startLoop();
    commands.connected();
  }

  function onDisconnect(e: GamepadEvent) {
    pads.delete(e.gamepad.index);
  }

  window.addEventListener('gamepadconnected', onConnect);
  window.addEventListener('gamepaddisconnected', onDisconnect);
  window.addEventListener('blur', resetHeld);
  document.addEventListener('visibilitychange', resetHeld);
  for (const pad of navigator.getGamepads()) {
    if (pad) pads.add(pad.index);
  }
  if (pads.size) startLoop();

  return () => {
    if (rafId) cancelAnimationFrame(rafId);
    rafId = 0;
    window.removeEventListener('gamepadconnected', onConnect);
    window.removeEventListener('gamepaddisconnected', onDisconnect);
    window.removeEventListener('blur', resetHeld);
    document.removeEventListener('visibilitychange', resetHeld);
    resetHeld();
    pads.clear();
  };
}
