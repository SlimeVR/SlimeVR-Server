import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import {
  ARROW_KEY,
  firstFocusable,
  pickBest,
  scrollableToward,
  synthKey,
  type Direction,
} from '@/utils/focus-nav';
import {
  aimBearing,
  BUTTON_A,
  BUTTON_B,
  decodeInput,
  pressedDirections,
  resolveStick,
} from '@/utils/gamepad';
import { useConfig } from './config';

const ACTIVE_CLASS = 'controller-active';
const EDIT_CLASS = 'nav-editing';

const ARROW_OWNER = '[aria-expanded="true"],[data-nav-arrows]';
const EDIT_WIDGET = '[data-nav-edit]';
/** A widget that reads the stick angle itself; the nav layer leaves its directions alone. */
const AIM_WIDGET = '[data-nav-aim]';

const KEY_DIR: Record<string, Direction | undefined> = {
  ArrowUp: 'up',
  ArrowDown: 'down',
  ArrowLeft: 'left',
  ArrowRight: 'right',
};

const TEXT_INPUT_TYPES = new Set([
  'text',
  'search',
  'url',
  'tel',
  'email',
  'password',
  'number',
  'date',
  'datetime-local',
  'month',
  'week',
  'time',
]);

/** A field where keystrokes are text. The nav layer stays out. */
function isTextEntry(el: EventTarget | null): boolean {
  if (!(el instanceof HTMLElement)) return false;
  if (el.isContentEditable) return true;
  const tag = el.tagName;
  if (tag === 'TEXTAREA' || tag === 'SELECT') return true;
  if (tag === 'INPUT') return TEXT_INPUT_TYPES.has((el as HTMLInputElement).type);
  return false;
}

const REPEAT_DELAY_MS = 400;
const REPEAT_INTERVAL_MS = 120;
/** How long to keep looking for a control to start on after a page change. */
const SEED_TIMEOUT_MS = 1500;
/** How long to wait for a swapped-in layout to bring its navbar back. */
const SHELL_RESTORE_GRACE_MS = 400;

const nav = {
  origin: null as DOMRect | null,
  /** Shell link the cursor followed, so a remount can put it back on it. */
  shellHref: null as string | null,
  shellArea: null as string | null,
  editing: null as HTMLElement | null,
  held: new Map<Direction, { firedAt: number; repeating: boolean }>(),
  prevButtons: new Set<number>(),
  stickDir: null as Direction | null,
  /** A dial-style widget listening for the raw stick/D-pad angle. */
  aim: null as ((bearing: number | null) => void) | null,
  pads: new Set<number>(),
  rafId: 0,
  kbStarts: 0,
  starts: 0,
};

function markActive() {
  document.documentElement.classList.add(ACTIVE_CLASS);
}

function isActive() {
  return document.documentElement.classList.contains(ACTIVE_CLASS);
}

function enterEdit(el: HTMLElement) {
  nav.editing = el;
  el.classList.add(EDIT_CLASS);
}

function exitEdit() {
  nav.editing?.classList.remove(EDIT_CLASS);
  nav.editing = null;
}

function clearActive() {
  document.documentElement.classList.remove(ACTIVE_CLASS);
  exitEdit();
}

function resetHeld() {
  nav.held.clear();
  nav.prevButtons.clear();
  nav.stickDir = null;
}

/** Whether the cursor is already somewhere real. */
function placed(): boolean {
  const active = document.activeElement;
  return !!active && active !== document.body;
}

/**
 * The link `href` in shell area `area`. Scoped to the area because an href is
 * not unique: the top bar logo and the navbar's Home entry both point at the
 * root, and the logo comes first in the DOM.
 */
function shellLink(href: string, area: string | null): HTMLElement | null {
  const escaped = href.replace(/["\\]/g, '\\$&');
  const scope = area
    ? `[data-nav-area="${area.replace(/["\\]/g, '\\$&')}"]`
    : '[data-nav-region="shell"]';
  return document.querySelector<HTMLElement>(`${scope} a[href="${escaped}"]`);
}

/**
 * Put the cursor somewhere sensible after arriving on a page.
 *
 * It has to wait a bit to try to look for the same component just in case a page load happen
 * Usually is needed for the navbar, since a change of layout would destroy and re-create
 * the whole navbar
 */
function placeCursorSoon(start = performance.now(), previous?: HTMLElement) {
  if (placed()) return;
  const elapsed = performance.now() - start;

  if (nav.shellHref) {
    const link = shellLink(nav.shellHref, nav.shellArea);
    if (link) {
      link.focus({ preventScroll: true });
      return;
    }
    if (elapsed < SHELL_RESTORE_GRACE_MS) {
      requestAnimationFrame(() => placeCursorSoon(start, previous));
      return;
    }
  }

  const target = firstFocusable();
  const expired = elapsed >= SEED_TIMEOUT_MS;
  if (target && (target === previous || expired)) {
    target.focus({ preventScroll: true });
    return;
  }
  if (expired) return;
  requestAnimationFrame(() => placeCursorSoon(start, target));
}

function doMove(dir: Direction) {
  markActive();
  const active = document.activeElement as HTMLElement | null;

  // A dial-style widget owns every direction while focused.
  if (active?.closest(AIM_WIDGET)) return;

  if (nav.editing) {
    if (active === nav.editing) {
      synthKey(nav.editing, ARROW_KEY[dir]);
      return;
    }
    exitEdit();
  }

  if (!active || active === document.body) {
    const resumed = nav.origin && pickBest(nav.origin, dir);
    (resumed || firstFocusable())?.focus({ preventScroll: true });
    return;
  }

  if (active.closest(ARROW_OWNER) && synthKey(active, ARROW_KEY[dir])) return;

  const target = pickBest(active.getBoundingClientRect(), dir, active);

  // Scroll the region the cursor sits in rather than leaving it early.
  const scroller = scrollableToward(active, dir);
  if (scroller && (!target || !scroller.contains(target))) {
    const vertical = dir === 'up' || dir === 'down';
    const span = vertical ? scroller.clientHeight : scroller.clientWidth;
    const by = (dir === 'up' || dir === 'left' ? -1 : 1) * (span * 0.6);
    scroller.scrollBy(vertical ? { top: by } : { left: by });
    return;
  }

  if (target) {
    target.focus({ preventScroll: true });
    target.scrollIntoView({ block: 'nearest', inline: 'nearest' });
  }
}

function doActivate() {
  markActive();
  const active = document.activeElement as HTMLElement | null;
  if (!active) return;
  if (nav.editing) return exitEdit();
  if (active.matches(EDIT_WIDGET)) return enterEdit(active);
  if (active.getAttribute('aria-disabled') === 'true') return;

  const shell = active.closest<HTMLElement>('[data-nav-region="shell"]');
  nav.shellHref = shell ? active.getAttribute('href') : null;
  nav.shellArea = shell?.dataset.navArea ?? null;

  if (!synthKey(active, 'Enter')) active.click();
}

function doBack() {
  markActive();
  if (nav.editing) return exitEdit();
  synthKey(document.activeElement, 'Escape');
}

function pressDir(dir: Direction, now: number) {
  const state = nav.held.get(dir);
  if (!state) {
    nav.held.set(dir, { firedAt: now, repeating: false });
    doMove(dir);
  } else if (!state.repeating && now - state.firedAt >= REPEAT_DELAY_MS) {
    state.repeating = true;
    state.firedAt = now;
    doMove(dir);
  } else if (state.repeating && now - state.firedAt >= REPEAT_INTERVAL_MS) {
    state.firedAt = now;
    doMove(dir);
  }
}

function poll() {
  const now = performance.now();
  const frame = decodeInput(navigator.getGamepads());

  // A dial widget wants the real angle, not the four-way resolution.
  if (nav.aim) nav.aim(aimBearing(frame));

  nav.stickDir = resolveStick(frame.axX, frame.axY, nav.stickDir);
  const pressedDirs = pressedDirections(frame, nav.stickDir);

  const { buttons } = frame;
  if (buttons.has(BUTTON_A) && !nav.prevButtons.has(BUTTON_A)) doActivate();
  if (buttons.has(BUTTON_B) && !nav.prevButtons.has(BUTTON_B)) doBack();
  nav.prevButtons = buttons;

  for (const dir of pressedDirs) pressDir(dir, now);
  for (const dir of Array.from(nav.held.keys())) {
    if (!pressedDirs.has(dir)) nav.held.delete(dir);
  }

  nav.rafId = nav.pads.size > 0 ? requestAnimationFrame(poll) : 0;
}

function startLoop() {
  if (!nav.rafId) nav.rafId = requestAnimationFrame(poll);
}

function onConnect(e: GamepadEvent) {
  nav.pads.add(e.gamepad.index);
  startLoop();
  markActive();
  nav.shellHref = null;
  placeCursorSoon();
}

function onDisconnect(e: GamepadEvent) {
  nav.pads.delete(e.gamepad.index);
}

function onInterrupt() {
  resetHeld();
}

function onPointerInput(e: Event) {
  if (!e.isTrusted) return;
  clearActive();
}

function onKeyNav(e: KeyboardEvent) {
  if (!e.isTrusted) return;
  // A widget that records or interprets raw keystrokes owns every key.
  if ((e.target as HTMLElement | null)?.closest('[data-nav-raw]')) return;

  const dir = KEY_DIR[e.key];
  if (dir && (e.ctrlKey || e.altKey) && !e.shiftKey && !e.metaKey) {
    e.preventDefault();
    markActive();
    doMove(dir);
    return;
  }

  if (isTextEntry(e.target)) return;

  if (e.key === 'Enter' || e.key === ' ') {
    const active = document.activeElement as HTMLElement | null;
    const shell = active?.closest<HTMLElement>('[data-nav-region="shell"]');
    nav.shellHref = shell ? (active?.getAttribute('href') ?? null) : null;
    nav.shellArea = shell?.dataset.navArea ?? null;

    if ((e.ctrlKey || e.altKey) && !e.metaKey && active) {
      e.preventDefault();
      markActive();
      if (!synthKey(active, 'Enter')) active.click();
    }
    return;
  }

  if (e.ctrlKey || e.altKey || e.metaKey) return;

  if (e.key === 'Escape') {
    if (nav.editing) {
      exitEdit();
      e.stopPropagation();
    }
    return;
  }

  clearActive();
}

/** Focus is still measurable during the blur, so a later move can resume. */
function onFocusOut(e: FocusEvent) {
  const el = e.target as HTMLElement | null;
  if (!el || el === document.body || !el.getClientRects().length) return;
  nav.origin = el.getBoundingClientRect();
}

function startKeyboard() {
  if (++nav.kbStarts > 1) return;
  document.addEventListener('focusout', onFocusOut, true);
  window.addEventListener('pointerdown', onPointerInput, true);
  window.addEventListener('keydown', onKeyNav, true);
}

function stopKeyboard() {
  if (--nav.kbStarts > 0) return;
  document.removeEventListener('focusout', onFocusOut, true);
  window.removeEventListener('pointerdown', onPointerInput, true);
  window.removeEventListener('keydown', onKeyNav, true);
  clearActive();
}

function startPad() {
  if (++nav.starts > 1) return;
  if (typeof navigator === 'undefined' || !navigator.getGamepads) return;

  window.addEventListener('gamepadconnected', onConnect);
  window.addEventListener('gamepaddisconnected', onDisconnect);
  window.addEventListener('blur', onInterrupt);
  document.addEventListener('visibilitychange', onInterrupt);

  for (const pad of navigator.getGamepads()) {
    if (pad) nav.pads.add(pad.index);
  }
  if (nav.pads.size) startLoop();
}

function stopPad() {
  if (--nav.starts > 0) return;

  if (nav.rafId) cancelAnimationFrame(nav.rafId);
  nav.rafId = 0;
  window.removeEventListener('gamepadconnected', onConnect);
  window.removeEventListener('gamepaddisconnected', onDisconnect);
  window.removeEventListener('blur', onInterrupt);
  document.removeEventListener('visibilitychange', onInterrupt);
  resetHeld();
  nav.pads.clear();
  nav.origin = null;
}

export function useControllerNav() {
  const { config } = useConfig();
  const location = useLocation();
  const padEnabled = config?.controllerNav !== false;

  useEffect(() => {
    startKeyboard();
    return stopKeyboard;
  }, []);

  useEffect(() => {
    if (!padEnabled) return;
    startPad();
    return stopPad;
  }, [padEnabled]);

  useEffect(() => {
    nav.origin = null;
    if (!isActive()) return;
    placeCursorSoon();
  }, [location.pathname]);
}

/**
 * Receive the raw stick / D-pad bearing (degrees clockwise from up) each frame
 * while `enabled`, for a widget that aims rather than steps. Rides the nav poll
 * loop, so it only fires while a pad is connected and pad nav is on. Mark the
 * element `data-nav-aim` so the nav layer leaves its directions alone.
 */
export function useNavAim(enabled: boolean, onAim: (bearing: number) => void) {
  const onAimRef = useRef(onAim);
  onAimRef.current = onAim;

  useEffect(() => {
    if (!enabled) return;
    nav.aim = (bearing) => {
      if (bearing !== null) onAimRef.current(bearing);
    };
    return () => {
      nav.aim = null;
    };
  }, [enabled]);
}
