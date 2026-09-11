export const MODIFIER_ORDER = ['CTRL', 'ALT', 'SHIFT', 'SUPER'];
const NON_SHIFT_MODIFIERS = ['CTRL', 'ALT', 'SUPER'];
const MODIFIER_KEY_NAMES: Record<string, string> = {
  CONTROL: 'CTRL',
  ALT: 'ALT',
  SHIFT: 'SHIFT',
  META: 'SUPER',
  OS: 'SUPER',
};

// `KeyboardEvent.code` -> canonical modifier name. The held-key set stores codes (so left/right
// variants of the same modifier don't collide), so lookups against it need this, not
// MODIFIER_KEY_NAMES (which is keyed by the location-less `e.key`).
const MODIFIER_CODE_NAMES: Record<string, string> = {
  ControlLeft: 'CTRL',
  ControlRight: 'CTRL',
  AltLeft: 'ALT',
  AltRight: 'ALT',
  ShiftLeft: 'SHIFT',
  ShiftRight: 'SHIFT',
  MetaLeft: 'SUPER',
  MetaRight: 'SUPER',
  OSLeft: 'SUPER',
  OSRight: 'SUPER',
};

// Keys bindable without a modifier: function, media/volume, and standalone system keys.
const MODIFIER_OPTIONAL_KEYS = new Set([
  ...Array.from({ length: 24 }, (_, i) => `F${i + 1}`),
  'MEDIA_PLAY_PAUSE',
  'MEDIA_STOP',
  'MEDIA_NEXT',
  'MEDIA_PREVIOUS',
  'VOLUME_UP',
  'VOLUME_DOWN',
  'VOLUME_MUTE',
  'PRINT_SCREEN',
  'PAUSE',
  'SCROLL_LOCK',
]);

// `KeyboardEvent.code` -> our canonical key name, kept in sync with
// server/core/.../keybind/keys.kt. Falls back to null (unbindable) for anything else.
const CODE_TO_KEY: Record<string, string> = {
  ArrowUp: 'UP',
  ArrowDown: 'DOWN',
  ArrowLeft: 'LEFT',
  ArrowRight: 'RIGHT',
  Home: 'HOME',
  End: 'END',
  PageUp: 'PAGE_UP',
  PageDown: 'PAGE_DOWN',
  Insert: 'INSERT',
  Delete: 'DELETE',
  Space: 'SPACE',
  Enter: 'ENTER',
  Tab: 'TAB',
  Backspace: 'BACKSPACE',
  Escape: 'ESCAPE',
  NumpadAdd: 'NUMPAD_ADD',
  NumpadSubtract: 'NUMPAD_SUBTRACT',
  NumpadMultiply: 'NUMPAD_MULTIPLY',
  NumpadDivide: 'NUMPAD_DIVIDE',
  NumpadDecimal: 'NUMPAD_DECIMAL',
  NumpadEnter: 'NUMPAD_ENTER',
  Minus: 'MINUS',
  Equal: 'EQUAL',
  BracketLeft: 'BRACKET_LEFT',
  BracketRight: 'BRACKET_RIGHT',
  Backslash: 'BACKSLASH',
  Semicolon: 'SEMICOLON',
  Quote: 'QUOTE',
  Backquote: 'BACKQUOTE',
  Comma: 'COMMA',
  Period: 'PERIOD',
  Slash: 'SLASH',
  PrintScreen: 'PRINT_SCREEN',
  Pause: 'PAUSE',
  ScrollLock: 'SCROLL_LOCK',
  NumLock: 'NUM_LOCK',
  CapsLock: 'CAPS_LOCK',
  MediaPlayPause: 'MEDIA_PLAY_PAUSE',
  MediaStop: 'MEDIA_STOP',
  MediaTrackNext: 'MEDIA_NEXT',
  MediaTrackPrevious: 'MEDIA_PREVIOUS',
  AudioVolumeUp: 'VOLUME_UP',
  AudioVolumeDown: 'VOLUME_DOWN',
  AudioVolumeMute: 'VOLUME_MUTE',
};

// Canonical key name -> what to render in the Kbd slot.
const KEY_LABELS: Record<string, string> = {
  UP: '↑',
  DOWN: '↓',
  LEFT: '←',
  RIGHT: '→',
  PAGE_UP: 'Pg Up',
  PAGE_DOWN: 'Pg Dn',
  PRINT_SCREEN: 'Prt Scn',
  SCROLL_LOCK: 'Scroll Lk',
  NUM_LOCK: 'Num Lk',
  CAPS_LOCK: 'Caps Lk',
  NUMPAD_ADD: 'Num +',
  NUMPAD_SUBTRACT: 'Num -',
  NUMPAD_MULTIPLY: 'Num *',
  NUMPAD_DIVIDE: 'Num /',
  NUMPAD_DECIMAL: 'Num .',
  NUMPAD_ENTER: 'Num ↵',
  MEDIA_PLAY_PAUSE: '⏯',
  MEDIA_STOP: '⏹',
  MEDIA_NEXT: '⏭',
  MEDIA_PREVIOUS: '⏮',
  VOLUME_UP: 'Vol +',
  VOLUME_DOWN: 'Vol -',
  VOLUME_MUTE: 'Vol Mute',
  MINUS: '-',
  EQUAL: '=',
  BRACKET_LEFT: '[',
  BRACKET_RIGHT: ']',
  BACKSLASH: '\\',
  SEMICOLON: ';',
  QUOTE: "'",
  BACKQUOTE: '`',
  COMMA: ',',
  PERIOD: '.',
  SLASH: '/',
  SPACE: 'Space',
  ENTER: 'Enter',
  TAB: 'Tab',
  BACKSPACE: 'Backspace',
  ESCAPE: 'Esc',
  HOME: 'Home',
  END: 'End',
  INSERT: 'Ins',
  DELETE: 'Del',
};
Array.from({ length: 10 }, (_, i) => i).forEach((i) => {
  KEY_LABELS[`NUMPAD_${i}`] = `Num ${i}`;
});

export function keybindLabel(key: string): string {
  return KEY_LABELS[key] ?? key;
}

function orderModifiers(mods: string[]): string[] {
  return MODIFIER_ORDER.filter((m) => mods.includes(m));
}

export function isValidKeybind(keys: string[]): boolean {
  const main = keys.filter((k) => !MODIFIER_ORDER.includes(k));
  if (main.length !== 1) return false;
  if (MODIFIER_OPTIONAL_KEYS.has(main[0])) return true;
  return keys.some((k) => NON_SHIFT_MODIFIERS.includes(k));
}

// Ordered the same way as the server's canonicalKeybind, so the two stay in sync.
export function keybindKey(keys: string[]): string {
  const modifiers = orderModifiers(keys.filter((k) => MODIFIER_ORDER.includes(k)));
  const main = keys.filter((k) => !MODIFIER_ORDER.includes(k));
  return [...modifiers, ...main].join('+');
}

const F_KEY = /^F([1-9]|1\d|2[0-4])$/;

export function resolveKey(e: React.KeyboardEvent): string | null {
  if (MODIFIER_KEY_NAMES[e.key.toUpperCase()]) return null;
  if (e.code.startsWith('Key')) return e.code.slice(3);
  if (e.code.startsWith('Digit')) return e.code.slice(5);
  if (e.code.startsWith('Numpad') && e.code.length === 7 && /^\d$/.test(e.code[6]))
    return `NUMPAD_${e.code[6]}`;
  // KeyboardEvent.code for function keys is already "F1".."F24", matching our canonical name.
  if (F_KEY.test(e.code)) return e.code;
  return CODE_TO_KEY[e.code] ?? null;
}

export function modifierNameForCode(code: string): string | undefined {
  return MODIFIER_CODE_NAMES[code];
}

export function modifierNameForKey(key: string): string | undefined {
  return MODIFIER_KEY_NAMES[key.toUpperCase()];
}

export function modifiersFromHeld(held: Set<string>): string[] {
  const mods: string[] = [];
  for (const code of held) {
    const name = MODIFIER_CODE_NAMES[code];
    if (name && !mods.includes(name)) mods.push(name);
  }
  return orderModifiers(mods);
}
