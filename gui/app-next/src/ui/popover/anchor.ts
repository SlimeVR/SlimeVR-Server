import { createEffect, onCleanup, onMount } from 'solid-js';

export type AnchorPlacement =
  | 'bottom'
  | 'bottom-start'
  | 'bottom-end'
  | 'top'
  | 'top-start'
  | 'top-end'
  | 'left'
  | 'right';

const AREA: Record<AnchorPlacement, string> = {
  bottom: 'block-end',
  'bottom-start': 'block-end span-inline-end',
  'bottom-end': 'block-end span-inline-start',
  top: 'block-start',
  'top-start': 'block-start span-inline-end',
  'top-end': 'block-start span-inline-start',
  left: 'inline-start',
  right: 'inline-end',
};

// One element can anchor a tooltip and a menu at once
const anchorNames = new WeakMap<HTMLElement, string[]>();
let nextAnchor = 0;

function writeNames(el: HTMLElement, names: string[]) {
  anchorNames.set(el, names);
  if (names.length) el.style.setProperty('anchor-name', names.join(', '));
  else el.style.removeProperty('anchor-name');
}

// Positions a popover element on its anchor with CSS anchor positioning
export function useAnchor(
  el: () => HTMLElement,
  anchor: () => HTMLElement | undefined,
  placement: () => AnchorPlacement
) {
  const name = `--anchor-${++nextAnchor}`;

  createEffect(() => {
    const target = anchor();
    if (!target) return;
    writeNames(target, [...(anchorNames.get(target) ?? []), name]);
    onCleanup(() =>
      writeNames(
        target,
        (anchorNames.get(target) ?? []).filter((n) => n !== name)
      )
    );
  });

  onMount(() => {
    el().style.setProperty('position-anchor', name);
    el().style.setProperty('position-try-fallbacks', 'flip-block, flip-inline');
  });

  createEffect(() => {
    el().style.setProperty('position-area', AREA[placement()]);
  });
}

export function showPopover(el: HTMLElement) {
  if (typeof el.showPopover === 'function' && !el.matches(':popover-open'))
    el.showPopover();
}

export function hidePopover(el: HTMLElement) {
  if (typeof el.hidePopover === 'function' && el.matches(':popover-open'))
    el.hidePopover();
}
