import { createSignal } from 'solid-js';
import { handleBack, pushBack } from '@/lib/back-stack';
import { isCapturing, isTextEntry } from '@/lib/keyboard';
import { motionTuning } from './tuning';

export type Direction = 'up' | 'down' | 'left' | 'right';
// Which arrow directions the focused item keeps for itself
export type MotionOwns = 'none' | 'horizontal' | 'all';
// Where focus lands when a move enters a container
export type MotionEntry = 'last' | 'first' | 'nearest';
export type MotionLayout = 'list' | 'grid' | 'region' | 'root';
export type MotionOrientation = 'vertical' | 'horizontal';

export interface MotionItem {
  kind: 'item';
  el: HTMLElement;
  parent: MotionContainer;
  owns(): MotionOwns;
  // Pad only: directions reach the widget after A enters edit mode
  edit(): boolean;
}

export interface MotionContainer {
  kind: MotionLayout;
  el: HTMLElement | null;
  parent: MotionContainer | null;
  readonly orientation: MotionOrientation;
  readonly entry: MotionEntry;
  // Moves never leave it (dialogs)
  readonly trap: boolean;
  children: Set<MotionNode>;
  last: MotionNode | null;
}

export type MotionNode = MotionItem | MotionContainer;

interface Rect {
  left: number;
  top: number;
  right: number;
  bottom: number;
}

const ZERO: Rect = { left: 0, top: 0, right: 0, bottom: 0 };

const ARROW: Record<Direction, string> = {
  up: 'ArrowUp',
  down: 'ArrowDown',
  left: 'ArrowLeft',
  right: 'ArrowRight',
};

const KEY_DIR: Record<string, Direction | undefined> = {
  ArrowUp: 'up',
  ArrowDown: 'down',
  ArrowLeft: 'left',
  ArrowRight: 'right',
};

const items = new WeakMap<HTMLElement, MotionItem>();
const synthetic = new WeakSet<Event>();
const bumpListeners = new Set<(dir: Direction) => void>();
let root: MotionContainer | null = null;
// Last focused rect, so a move after focus was lost resumes nearby
let origin: Rect | null = null;
let releaseEdit: (() => void) | null = null;

const [active, setActive] = createSignal(false);
const [editing, setEditing] = createSignal<MotionItem | null>(null);

// Directional motion in use: the ring is shown
export const motionActive = active;
export const motionEditing = editing;

export function setMotionActive(on: boolean) {
  setActive(on);
  if (!on) exitEdit();
}

export function onBump(cb: (dir: Direction) => void): () => void {
  bumpListeners.add(cb);
  return () => bumpListeners.delete(cb);
}

export function createContainer(
  kind: MotionLayout,
  parent: MotionContainer | null,
  options: {
    orientation?: () => MotionOrientation;
    entry?: () => MotionEntry;
    trap?: () => boolean;
  } = {}
): MotionContainer {
  return {
    kind,
    el: null,
    parent,
    get orientation() {
      return options.orientation?.() ?? 'vertical';
    },
    get entry() {
      return options.entry?.() ?? 'last';
    },
    get trap() {
      return options.trap?.() ?? false;
    },
    children: new Set(),
    last: null,
  };
}

export function setMotionRoot(container: MotionContainer | null) {
  root = container;
}

export function attachContainer(container: MotionContainer): () => void {
  container.parent?.children.add(container);
  return () => {
    const parent = container.parent;
    if (!parent) return;
    parent.children.delete(container);
    if (parent.last === container) parent.last = null;
  };
}

export function registerItem(
  parent: MotionContainer,
  el: HTMLElement,
  owns: () => MotionOwns,
  edit: () => boolean
): () => void {
  const item: MotionItem = { kind: 'item', el, parent, owns, edit };
  items.set(el, item);
  parent.children.add(item);
  return () => {
    items.delete(el);
    parent.children.delete(item);
    if (parent.last === item) parent.last = null;
    if (editing() === item) exitEdit();
  };
}

function elOf(node: MotionNode): HTMLElement | null {
  return node.el;
}

function rectOf(node: MotionNode): Rect {
  return elOf(node)?.getBoundingClientRect() ?? ZERO;
}

function visible(el: HTMLElement): boolean {
  if (!el.isConnected || el.closest('[inert]')) return false;
  return typeof el.checkVisibility === 'function' ? el.checkVisibility() : true;
}

function navigable(node: MotionNode): boolean {
  if (node.kind === 'item') {
    return (
      visible(node.el) &&
      node.el.tabIndex >= 0 &&
      (node.el as Partial<HTMLButtonElement>).disabled !== true
    );
  }
  if (node.el && !visible(node.el)) return false;
  for (const child of node.children) if (navigable(child)) return true;
  return false;
}

function domOrder(a: MotionNode, b: MotionNode): number {
  const ea = elOf(a);
  const eb = elOf(b);
  if (!ea || !eb || ea === eb) return 0;
  return ea.compareDocumentPosition(eb) & Node.DOCUMENT_POSITION_FOLLOWING ? -1 : 1;
}

function ordered(container: MotionContainer): MotionNode[] {
  return [...container.children].filter(navigable).sort(domOrder);
}

function isVertical(dir: Direction): boolean {
  return dir === 'up' || dir === 'down';
}

function isBackward(dir: Direction): boolean {
  return dir === 'up' || dir === 'left';
}

// Gap along the move axis, or null when `to` is not in that direction
function gapToward(from: Rect, to: Rect, dir: Direction): number | null {
  const gap = {
    up: from.top - to.bottom,
    down: to.top - from.bottom,
    left: from.left - to.right,
    right: to.left - from.right,
  }[dir];
  return gap >= -1 ? Math.max(0, gap) : null;
}

function crossOverlap(from: Rect, to: Rect, dir: Direction): number {
  return isVertical(dir)
    ? Math.min(from.right, to.right) - Math.max(from.left, to.left)
    : Math.min(from.bottom, to.bottom) - Math.max(from.top, to.top);
}

function crossDistance(from: Rect, to: Rect, dir: Direction): number {
  return isVertical(dir)
    ? Math.abs(from.left + from.right - to.left - to.right) / 2
    : Math.abs(from.top + from.bottom - to.top - to.bottom) / 2;
}

function lessThan(a: number[], b: number[]): boolean {
  for (let i = 0; i < a.length; i++) {
    if (a[i] !== b[i]) return a[i]! < b[i]!;
  }
  return false;
}

// Cross-axis alignment first, then distance along the axis, then off-axis distance
function pickInDirection(
  from: Rect,
  candidates: MotionNode[],
  dir: Direction,
  alignedOnly: boolean
): MotionNode | null {
  let best: MotionNode | null = null;
  let bestScore: number[] = [];
  for (const candidate of candidates) {
    const rect = rectOf(candidate);
    const gap = gapToward(from, rect, dir);
    if (gap === null) continue;
    const aligned = crossOverlap(from, rect, dir) > 0;
    if (alignedOnly && !aligned) continue;
    const score = [aligned ? 0 : 1, gap, crossDistance(from, rect, dir)];
    if (best === null || lessThan(score, bestScore)) {
      best = candidate;
      bestScore = score;
    }
  }
  return best;
}

function nearest(from: Rect, candidates: MotionNode[]): MotionNode | null {
  let best: MotionNode | null = null;
  let bestDistance = Infinity;
  for (const candidate of candidates) {
    const r = rectOf(candidate);
    const dx = Math.max(0, r.left - from.right, from.left - r.right);
    const dy = Math.max(0, r.top - from.bottom, from.top - r.bottom);
    const distance = Math.hypot(dx, dy);
    if (distance < bestDistance) {
      best = candidate;
      bestDistance = distance;
    }
  }
  return best;
}

// Next sibling inside `container`, or null to bubble up
function step(
  container: MotionContainer,
  node: MotionNode,
  dir: Direction
): MotionNode | null {
  const siblings = ordered(container);
  if (container.kind === 'grid') {
    return pickInDirection(
      rectOf(node),
      siblings.filter((s) => s !== node),
      dir,
      true
    );
  }
  if (isVertical(dir) !== (container.orientation === 'vertical')) return null;
  const i = siblings.indexOf(node);
  if (i < 0) return null;
  return siblings[i + (isBackward(dir) ? -1 : 1)] ?? null;
}

// The item focus lands on when entering `node`
function enter(node: MotionNode, from: Rect): MotionItem | null {
  if (node.kind === 'item') return node;
  const children = ordered(node);
  if (!children.length) return null;
  let pick: MotionNode | null = null;
  if (node.entry === 'last' && node.last && children.includes(node.last))
    pick = node.last;
  else if (node.entry === 'first') pick = children[0]!;
  pick ??= nearest(from, children) ?? children[0]!;
  return enter(pick, from);
}

// Item to container to parent; geometry only between regions
function resolve(item: MotionItem, dir: Direction): MotionItem | null {
  const from = item.el.getBoundingClientRect();
  let node: MotionNode = item;
  for (
    let container: MotionContainer | null = item.parent;
    container;
    container = container.parent
  ) {
    const next =
      node.kind === 'region'
        ? pickInDirection(
            rectOf(node),
            ordered(container).filter((s) => s !== node),
            dir,
            false
          )
        : step(container, node, dir);
    const target = next && enter(next, from);
    if (target) return target;
    if (container.trap) return null;
    node = container;
  }
  return null;
}

function scrollableToward(el: HTMLElement, dir: Direction): HTMLElement | null {
  const vertical = isVertical(dir);
  for (let n = el.parentElement; n; n = n.parentElement) {
    const style = getComputedStyle(n);
    const overflow = vertical ? style.overflowY : style.overflowX;
    if (overflow !== 'auto' && overflow !== 'scroll') continue;
    const pos = vertical ? n.scrollTop : n.scrollLeft;
    const span = vertical ? n.clientHeight : n.clientWidth;
    const max = vertical
      ? n.scrollHeight - n.clientHeight
      : n.scrollWidth - n.clientWidth;
    // A vertical column reports a few px of sideways overflow (scrollbar, scaled child)
    if (max <= Math.max(8, span * 0.1)) continue;
    const atEdge = isBackward(dir) ? pos <= 1 : pos >= max - 1;
    if (!atEdge) return n;
  }
  return null;
}

function focused(): HTMLElement | null {
  const el = document.activeElement;
  return el instanceof HTMLElement && el !== document.body ? el : null;
}

function focusItem(item: MotionItem) {
  item.el.focus({ preventScroll: true });
  item.el.scrollIntoView?.({ block: 'nearest', inline: 'nearest' });
}

export function move(dir: Direction) {
  const el = focused();
  const item = el ? items.get(el) : undefined;
  // Not on an item: resume at the remembered item, or nearest to where focus was last
  if (!item) {
    placeCursor();
    return;
  }
  const target = resolve(item, dir);

  // Scroll the region the cursor sits in rather than leaving it early
  const scroller = scrollableToward(item.el, dir);
  if (scroller && (!target || !scroller.contains(target.el))) {
    const span = isVertical(dir) ? scroller.clientHeight : scroller.clientWidth;
    const by = (isBackward(dir) ? -1 : 1) * span * motionTuning.scrollFraction;
    scroller.scrollBy(isVertical(dir) ? { top: by } : { left: by });
    return;
  }
  if (!target) {
    for (const cb of bumpListeners) cb(dir);
    return;
  }
  focusItem(target);
}

// Dispatch a keydown so the widget runs its own logic; true when it was consumed
function synthKey(el: HTMLElement, key: string): boolean {
  const event = new KeyboardEvent('keydown', {
    key,
    code: key,
    bubbles: true,
    cancelable: true,
  });
  synthetic.add(event);
  el.dispatchEvent(event);
  return event.defaultPrevented;
}

function press(el: HTMLElement) {
  if (el.getAttribute('aria-disabled') === 'true') return;
  if (!synthKey(el, 'Enter')) el.click();
}

function ownsOf(el: HTMLElement): MotionOwns {
  return items.get(el)?.owns() ?? 'none';
}

function ownsDirection(owns: MotionOwns, dir: Direction): boolean {
  return owns === 'all' || (owns === 'horizontal' && !isVertical(dir));
}

function enterEdit(item: MotionItem) {
  exitEdit();
  setEditing(item);
  releaseEdit = pushBack(exitEdit);
}

function exitEdit() {
  releaseEdit?.();
  releaseEdit = null;
  setEditing(null);
}

function findContainer(node: MotionContainer, el: HTMLElement): MotionContainer | null {
  if (node.el && el.contains(node.el)) return node;
  for (const child of node.children) {
    if (child.kind === 'item') continue;
    const found = findContainer(child, el);
    if (found) return found;
  }
  return null;
}

function hasAutofocus(el: HTMLElement): boolean {
  const active = document.activeElement;
  return (
    active instanceof HTMLElement &&
    el.contains(active) &&
    active.hasAttribute('autofocus')
  );
}

// Focus the entry item of the outermost container inside `el`, unless an
// autofocus element in it holds focus. Call from onMount or an effect: items
// register in their own onMount, queued after the caller's, and Solid flushes
// those synchronously, so a microtask runs once they are all registered.
export function focusEntry(el: HTMLElement) {
  queueMicrotask(() => {
    if (hasAutofocus(el)) return;
    const container = root && findContainer(root, el);
    const target = container && enter(container, origin ?? ZERO);
    if (target) focusItem(target);
  });
}

function placeCursor() {
  const target = root && enter(root, origin ?? ZERO);
  if (target) focusItem(target);
}

// A pad's first press after pointer use only reveals the ring
function reveal(): boolean {
  if (active()) return false;
  setActive(true);
  if (focused()) return true;
  placeCursor();
  return true;
}

export function padMove(dir: Direction) {
  if (reveal()) return;
  const el = focused();
  if (el) {
    const edited = editing();
    if (edited?.el === el) {
      synthKey(el, ARROW[dir]);
      return;
    }
    const item = items.get(el);
    if (!item?.edit() && ownsDirection(ownsOf(el), dir) && synthKey(el, ARROW[dir]))
      return;
  }
  move(dir);
}

export function padActivate() {
  if (reveal()) return;
  const el = focused();
  if (!el) return;
  if (editing()) {
    exitEdit();
    return;
  }
  const item = items.get(el);
  if (item?.edit()) {
    enterEdit(item);
    return;
  }
  press(el);
}

export function padBack() {
  setActive(true);
  handleBack();
}

export function padConnected() {
  setActive(true);
  if (!focused()) placeCursor();
}

// Ctrl/Alt+Arrow moves from anywhere, Ctrl/Alt+Enter/Space activates.
// Ctrl/Alt+Left/Right in a text field stay word jumps.
function onForceKey(e: KeyboardEvent) {
  if (synthetic.has(e) || isCapturing()) return;
  if (!(e.ctrlKey || e.altKey) || e.metaKey || e.shiftKey) return;
  const dir = KEY_DIR[e.key];
  if (dir) {
    if (!isVertical(dir) && isTextEntry(e.target)) return;
    e.preventDefault();
    e.stopPropagation();
    setActive(true);
    exitEdit();
    move(dir);
    return;
  }
  const el = focused();
  if ((e.key === 'Enter' || e.key === ' ') && el) {
    e.preventDefault();
    e.stopPropagation();
    setActive(true);
    press(el);
  }
}

// Plain arrows move focus unless the focused item owns that axis
function onKey(e: KeyboardEvent) {
  if (synthetic.has(e) || e.defaultPrevented || isCapturing()) return;
  if (e.key === 'Tab') {
    setActive(true);
    return;
  }
  const dir = KEY_DIR[e.key];
  if (!dir || e.ctrlKey || e.altKey || e.metaKey || e.shiftKey) return;
  const el = focused();
  if (el && (editing()?.el === el || ownsDirection(ownsOf(el), dir))) return;
  e.preventDefault();
  setActive(true);
  move(dir);
}

function onFocusIn(e: FocusEvent) {
  const item = e.target instanceof HTMLElement ? items.get(e.target) : undefined;
  if (editing() && editing() !== item) exitEdit();
  if (!item) return;
  let node: MotionNode = item;
  for (
    let container: MotionContainer | null = item.parent;
    container;
    container = container.parent
  ) {
    container.last = node;
    node = container;
  }
}

// Still measurable during the blur, so a later move can resume
function onFocusOut(e: FocusEvent) {
  const el = e.target;
  if (!(el instanceof HTMLElement) || !el.getClientRects().length) return;
  const r = el.getBoundingClientRect();
  origin = { left: r.left, top: r.top, right: r.right, bottom: r.bottom };
}

function onPointerDown(e: PointerEvent) {
  if (e.isTrusted) setMotionActive(false);
}

export function initMotion(): () => void {
  window.addEventListener('keydown', onForceKey, true);
  window.addEventListener('keydown', onKey);
  window.addEventListener('pointerdown', onPointerDown, true);
  document.addEventListener('focusin', onFocusIn);
  document.addEventListener('focusout', onFocusOut, true);
  return () => {
    window.removeEventListener('keydown', onForceKey, true);
    window.removeEventListener('keydown', onKey);
    window.removeEventListener('pointerdown', onPointerDown, true);
    document.removeEventListener('focusin', onFocusIn);
    document.removeEventListener('focusout', onFocusOut, true);
    exitEdit();
    setActive(false);
    origin = null;
  };
}
