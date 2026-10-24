import { cleanup, render, screen } from '@solidjs/testing-library';
import type { JSX } from 'solid-js';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { handleBack } from '@/lib/back-stack';
import {
  type Direction,
  motionEditing,
  onBump,
  padActivate,
  padMove,
  setMotionActive,
} from './core';
import { MotionList, MotionRegion, MotionToolbar } from './containers';
import { MotionRoot } from './motion-root';
import { type MotionItemOptions, useMotionItem } from './use-motion-item';

afterEach(() => {
  setMotionActive(false);
  cleanup();
});

function Item(props: { name: string } & MotionItemOptions) {
  let ref!: HTMLButtonElement;
  useMotionItem(() => ref, props);
  return <button ref={ref}>{props.name}</button>;
}

function Field(props: { name: string } & MotionItemOptions) {
  let ref!: HTMLInputElement;
  useMotionItem(() => ref, props);
  return <input ref={ref} aria-label={props.name} />;
}

function setup(tree: () => JSX.Element) {
  render(() => <MotionRoot>{tree()}</MotionRoot>);
}

const KEYS: Record<Direction, string> = {
  up: 'ArrowUp',
  down: 'ArrowDown',
  left: 'ArrowLeft',
  right: 'ArrowRight',
};

function arrow(dir: Direction, init: KeyboardEventInit = {}) {
  const target = document.activeElement ?? document.body;
  const event = new KeyboardEvent('keydown', {
    key: KEYS[dir],
    bubbles: true,
    cancelable: true,
    ...init,
  });
  target.dispatchEvent(event);
  return event;
}

function button(name: string) {
  return screen.getByRole('button', { name });
}

function focused() {
  return document.activeElement;
}

// jsdom has no layout
function place(
  el: HTMLElement,
  left: number,
  top: number,
  width: number,
  height: number
) {
  vi.spyOn(el, 'getBoundingClientRect').mockReturnValue({
    left,
    top,
    width,
    height,
    right: left + width,
    bottom: top + height,
    x: left,
    y: top,
    toJSON: () => ({}),
  });
}

describe('motion moves', () => {
  it('steps along a list and bubbles out of a nested toolbar', () => {
    setup(() => (
      <MotionList>
        <Item name="a" />
        <MotionToolbar>
          <Item name="b1" />
          <Item name="b2" />
        </MotionToolbar>
        <Item name="c" />
      </MotionList>
    ));
    button('a').focus();
    arrow('down');
    expect(focused()).toBe(button('b1'));
    arrow('right');
    expect(focused()).toBe(button('b2'));
    arrow('down');
    expect(focused()).toBe(button('c'));
    // Re-entering the toolbar lands on the remembered item
    arrow('up');
    expect(focused()).toBe(button('b2'));
  });

  it('bumps at the end instead of wrapping', () => {
    const bumps: Direction[] = [];
    const stop = onBump((dir) => bumps.push(dir));
    setup(() => (
      <MotionList>
        <Item name="a" />
        <Item name="b" />
      </MotionList>
    ));
    button('b').focus();
    arrow('down');
    arrow('right');
    stop();
    expect(focused()).toBe(button('b'));
    expect(bumps).toEqual(['down', 'right']);
  });

  it('leaves owned axes to the widget', () => {
    setup(() => (
      <MotionList>
        <Field name="field" owns="horizontal" />
        <Item name="next" />
      </MotionList>
    ));
    const field = screen.getByLabelText('field');
    field.focus();
    expect(arrow('left').defaultPrevented).toBe(false);
    expect(focused()).toBe(field);
    expect(arrow('down').defaultPrevented).toBe(true);
    expect(focused()).toBe(button('next'));
  });

  it('force moves with Ctrl+Arrow out of a widget that owns everything', () => {
    setup(() => (
      <MotionList>
        <Field name="field" owns="all" />
        <Item name="next" />
      </MotionList>
    ));
    screen.getByLabelText('field').focus();
    arrow('down');
    expect(focused()).toBe(screen.getByLabelText('field'));
    arrow('down', { ctrlKey: true });
    expect(focused()).toBe(button('next'));
  });

  it('crosses between regions by geometry', () => {
    setup(() => (
      <>
        <MotionRegion data-testid="rail">
          <Item name="rail" />
        </MotionRegion>
        <MotionRegion data-testid="page">
          <Item name="p1" />
          <Item name="p2" />
        </MotionRegion>
      </>
    ));
    place(screen.getByTestId('rail'), 0, 0, 100, 500);
    place(button('rail'), 10, 10, 80, 40);
    place(screen.getByTestId('page'), 100, 0, 400, 500);
    place(button('p1'), 110, 10, 200, 40);
    place(button('p2'), 110, 60, 200, 40);
    button('p2').focus();
    arrow('left');
    expect(focused()).toBe(button('rail'));
    arrow('right');
    // Remembered item of the page region
    expect(focused()).toBe(button('p2'));
  });

  it('keeps moves inside a trapped region', () => {
    const bumps: Direction[] = [];
    const stop = onBump((dir) => bumps.push(dir));
    setup(() => (
      <>
        <MotionRegion data-testid="page">
          <Item name="page" />
        </MotionRegion>
        <MotionRegion data-testid="dialog" trap>
          <Item name="ok" />
        </MotionRegion>
      </>
    ));
    place(screen.getByTestId('page'), 0, 0, 500, 500);
    place(button('page'), 10, 10, 80, 40);
    place(screen.getByTestId('dialog'), 100, 100, 200, 100);
    place(button('ok'), 110, 110, 80, 40);
    button('ok').focus();
    arrow('up');
    stop();
    expect(focused()).toBe(button('ok'));
    expect(bumps).toEqual(['up']);
  });
});

describe('pad', () => {
  it('only reveals on the first press', () => {
    setup(() => (
      <MotionList>
        <Item name="a" />
        <Item name="b" />
      </MotionList>
    ));
    button('a').focus();
    padMove('down');
    expect(focused()).toBe(button('a'));
    padMove('down');
    expect(focused()).toBe(button('b'));
  });

  it('edit mode sends directions to the widget until back', () => {
    const keys: string[] = [];
    function Dial() {
      let ref!: HTMLButtonElement;
      useMotionItem(() => ref, { edit: true });
      return (
        <button ref={ref} onKeyDown={(e) => keys.push(e.key)}>
          dial
        </button>
      );
    }
    setup(() => (
      <MotionList>
        <Dial />
        <Item name="b" />
      </MotionList>
    ));
    button('dial').focus();
    setMotionActive(true);
    padMove('down');
    expect(focused()).toBe(button('b'));
    button('dial').focus();
    padActivate();
    expect(motionEditing()?.el).toBe(button('dial'));
    padMove('down');
    expect(focused()).toBe(button('dial'));
    expect(keys).toEqual(['ArrowDown']);
    expect(handleBack()).toBe(true);
    expect(motionEditing()).toBeNull();
    padMove('down');
    expect(focused()).toBe(button('b'));
  });
});
