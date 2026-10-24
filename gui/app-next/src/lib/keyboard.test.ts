import { createRoot } from 'solid-js';
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { pushBack, setBackFallback } from '@/lib/back-stack';
import { captureKeys, initKeyboard, shortcuts, useShortcut } from '@/lib/keyboard';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';

let uninstall: () => void;
const disposers: (() => void)[] = [];

function shortcut(...args: Parameters<typeof useShortcut>) {
  createRoot((dispose) => {
    useShortcut(...args);
    disposers.push(dispose);
  });
}

function press(
  key: string,
  init: KeyboardEventInit = {},
  target: EventTarget = document.body
) {
  const event = new KeyboardEvent('keydown', {
    key,
    bubbles: true,
    cancelable: true,
    ...init,
  });
  target.dispatchEvent(event);
  return event;
}

beforeAll(() => setPlatform(fakePlatform()));
beforeEach(() => {
  uninstall = initKeyboard();
});
afterEach(() => {
  uninstall();
  disposers.splice(0).forEach((d) => d());
  document.body.innerHTML = '';
});

describe('keyboard', () => {
  it('sends Escape to the top overlay', () => {
    const back = vi.fn();
    const remove = pushBack(back);
    const event = press('Escape');
    remove();
    expect(back).toHaveBeenCalledOnce();
    expect(event.defaultPrevented).toBe(true);
    expect(press('Escape').defaultPrevented).toBe(false);
  });

  it('never sends Escape to history', () => {
    const history = vi.fn();
    setBackFallback({ can: () => true, back: history });
    press('Escape');
    setBackFallback(null);
    expect(history).not.toHaveBeenCalled();
  });

  it('matches Mod as Ctrl off macOS and ignores shift on symbols', () => {
    const k = vi.fn();
    const help = vi.fn();
    shortcut('Mod+K', k, { scope: 'global' });
    shortcut('?', help, { scope: 'global' });
    press('k', { ctrlKey: true });
    press('K', { ctrlKey: true, shiftKey: true });
    press('?', { shiftKey: true });
    expect(k).toHaveBeenCalledOnce();
    expect(help).toHaveBeenCalledOnce();
  });

  it('ignores shortcuts while typing unless opted in', () => {
    const plain = vi.fn();
    const typed = vi.fn();
    shortcut('F1', plain);
    shortcut('F2', typed, { inTextEntry: true });
    const input = document.createElement('input');
    document.body.append(input);
    press('F1', {}, input);
    press('F2', {}, input);
    expect(plain).not.toHaveBeenCalled();
    expect(typed).toHaveBeenCalledOnce();
  });

  it('suspends page shortcuts while a dialog is open', () => {
    const page = vi.fn();
    const global = vi.fn();
    shortcut('F1', page);
    shortcut('F2', global, { scope: 'global' });
    const remove = pushBack(() => {}, { modal: true });
    press('F1');
    press('F2');
    remove();
    press('F1');
    expect(page).toHaveBeenCalledOnce();
    expect(global).toHaveBeenCalledOnce();
  });

  it('gives every key to a capture, Escape included', () => {
    const back = vi.fn();
    const f1 = vi.fn();
    const captured: string[] = [];
    const removeBack = pushBack(back);
    shortcut('F1', f1);
    const release = captureKeys((e) => captured.push(e.key));
    press('Escape');
    press('F1');
    release();
    removeBack();
    expect(captured).toEqual(['Escape', 'F1']);
    expect(back).not.toHaveBeenCalled();
    expect(f1).not.toHaveBeenCalled();
  });

  it('lists labelled shortcuts and warns on duplicates', () => {
    const warn = vi.spyOn(console, 'warn').mockImplementation(() => {});
    shortcut('F1', () => {}, { label: 'help' });
    shortcut('F1', () => {});
    shortcut('F1', () => {}, { scope: 'global' });
    expect(warn).toHaveBeenCalledOnce();
    expect(shortcuts()).toEqual([{ combo: 'F1', scope: 'page', label: 'help' }]);
  });
});
