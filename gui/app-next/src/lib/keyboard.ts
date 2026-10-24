import { createSignal, onCleanup } from 'solid-js';
import { dismiss, modalOpen } from '@/lib/back-stack';
import { platform } from '@/lib/platform';

export type ShortcutScope = 'global' | 'page';

export interface ShortcutOptions {
  scope?: ShortcutScope;
  label?: string;
  inTextEntry?: boolean;
}

export interface ShortcutInfo {
  combo: string;
  scope: ShortcutScope;
  label: string;
}

interface Combo {
  key: string;
  ctrl: boolean;
  alt: boolean;
  meta: boolean;
  shift: boolean | null;
}

interface Shortcut {
  combo: string;
  parsed: Combo;
  fn: (e: KeyboardEvent) => void;
  scope: ShortcutScope;
  label?: string;
  inTextEntry: boolean;
}

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

// A field where keystrokes are text
export function isTextEntry(el: EventTarget | null): boolean {
  if (!(el instanceof HTMLElement)) return false;
  if (el.isContentEditable) return true;
  if (el instanceof HTMLTextAreaElement || el instanceof HTMLSelectElement) return true;
  return el instanceof HTMLInputElement && TEXT_INPUT_TYPES.has(el.type);
}

function normalizeKey(key: string): string {
  return key.length === 1 ? key.toLowerCase() : key;
}

// "Mod+K", "Shift+F1", "?". Mod is Cmd on macOS, Ctrl elsewhere
function parseCombo(combo: string): Combo {
  const parts = combo.split('+');
  const key = normalizeKey(parts.pop() ?? '');
  const mods = new Set(parts.map((p) => p.toLowerCase()));
  const mod = mods.has('mod');
  const mac = platform.os === 'macos';
  const symbol =
    key.length === 1 && key.toUpperCase() === key.toLowerCase() && key !== ' ';
  return {
    key,
    ctrl: mods.has('ctrl') || (mod && !mac),
    alt: mods.has('alt'),
    meta: mods.has('meta') || (mod && mac),
    shift: symbol && !mods.has('shift') ? null : mods.has('shift'),
  };
}

function matches(c: Combo, e: KeyboardEvent): boolean {
  return (
    normalizeKey(e.key) === c.key &&
    e.ctrlKey === c.ctrl &&
    e.altKey === c.alt &&
    e.metaKey === c.meta &&
    (c.shift === null || e.shiftKey === c.shift)
  );
}

const [registered, setRegistered] = createSignal<readonly Shortcut[]>([]);
const captures: ((e: KeyboardEvent) => void)[] = [];

// Cheatsheet source
export function shortcuts(): ShortcutInfo[] {
  return registered().flatMap((s) =>
    s.label ? [{ combo: s.combo, scope: s.scope, label: s.label }] : []
  );
}

export function useShortcut(
  combo: string,
  fn: (e: KeyboardEvent) => void,
  options: ShortcutOptions = {}
) {
  const shortcut: Shortcut = {
    combo,
    parsed: parseCombo(combo),
    fn,
    scope: options.scope ?? 'page',
    label: options.label,
    inTextEntry: options.inTextEntry ?? false,
  };
  if (import.meta.env.DEV) {
    const same = registered().find(
      (s) =>
        s.scope === shortcut.scope &&
        JSON.stringify(s.parsed) === JSON.stringify(shortcut.parsed)
    );
    if (same)
      console.warn(`Shortcut ${combo} registered twice in scope ${shortcut.scope}`);
  }
  setRegistered((list) => [...list, shortcut]);
  onCleanup(() => setRegistered((list) => list.filter((s) => s !== shortcut)));
}

export function captureKeys(fn: (e: KeyboardEvent) => void): () => void {
  captures.push(fn);
  return () => {
    const i = captures.lastIndexOf(fn);
    if (i >= 0) captures.splice(i, 1);
  };
}

export function isCapturing(): boolean {
  return captures.length > 0;
}

function onCapturePhase(e: KeyboardEvent) {
  const capture = captures.at(-1);
  if (!capture) return;
  e.preventDefault();
  e.stopPropagation();
  capture(e);
}

function onBubblePhase(e: KeyboardEvent) {
  if (e.defaultPrevented || isCapturing()) return;

  if (e.key === 'Escape' && !e.ctrlKey && !e.altKey && !e.metaKey && !e.shiftKey) {
    // Never history: desktop Escape does not go to the previous page
    if (dismiss()) e.preventDefault();
    return;
  }

  const typing = isTextEntry(e.target);
  const suspended = modalOpen();
  // Latest registration wins
  for (const s of [...registered()].reverse()) {
    if (!matches(s.parsed, e)) continue;
    if (typing && !s.inTextEntry) continue;
    if (s.scope === 'page' && suspended) continue;
    e.preventDefault();
    s.fn(e);
    return;
  }
}

export function initKeyboard(target: Window = window): () => void {
  target.addEventListener('keydown', onCapturePhase, true);
  target.addEventListener('keydown', onBubblePhase);
  return () => {
    target.removeEventListener('keydown', onCapturePhase, true);
    target.removeEventListener('keydown', onBubblePhase);
  };
}
