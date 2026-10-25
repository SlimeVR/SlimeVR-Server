import { createEffect, type JSX, onCleanup, onMount } from 'solid-js';
import { useBack } from '@/lib/back-stack';
import { pushLayer } from '@/ui/floating';
import { focusEntry, MotionRegion } from '@/ui/motion';

export type PopoverPlacement =
  | 'bottom'
  | 'bottom-start'
  | 'bottom-end'
  | 'top'
  | 'top-start'
  | 'top-end'
  | 'left'
  | 'right';

const AREA: Record<PopoverPlacement, string> = {
  bottom: 'block-end',
  'bottom-start': 'block-end span-inline-end',
  'bottom-end': 'block-end span-inline-start',
  top: 'block-start',
  'top-start': 'block-start span-inline-end',
  'top-end': 'block-start span-inline-start',
  left: 'inline-start',
  right: 'inline-end',
};

export interface PopoverProps {
  open: boolean;
  // Escape, pad B, a press outside
  onClose: () => void;
  anchor: () => HTMLElement | undefined;
  placement?: PopoverPlacement;
  class?: string;
  children: JSX.Element;
}

let nextAnchor = 0;

function show(el: HTMLElement) {
  if (typeof el.showPopover === 'function' && !el.matches(':popover-open'))
    el.showPopover();
}

function hide(el: HTMLElement) {
  if (typeof el.hidePopover === 'function' && el.matches(':popover-open'))
    el.hidePopover();
}

// Menus, dropdown lists, drawers: a manual popover positioned on its anchor.
// Never put a transform on it: Floating renders the fixed focus ring inside the
// open popover, and a transform would position the ring against the popover.
export function Popover(props: PopoverProps) {
  let el!: HTMLDivElement;
  const anchorName = `--popover-anchor-${++nextAnchor}`;

  useBack(() => props.onClose(), { when: () => props.open });

  createEffect(() => {
    const anchor = props.anchor();
    if (!anchor) return;
    anchor.style.setProperty('anchor-name', anchorName);
    onCleanup(() => anchor.style.removeProperty('anchor-name'));
  });

  onMount(() => {
    el.style.setProperty('position-anchor', anchorName);
    el.style.setProperty('position-try-fallbacks', 'flip-block, flip-inline');
  });

  createEffect(() => {
    el.style.setProperty(
      'position-area',
      AREA[props.placement ?? 'bottom-start']
    );
  });

  createEffect(() => {
    if (!props.open) {
      hide(el);
      return;
    }
    show(el);
    // A shown popover is in the top layer, above everything outside it. The
    // focus ring and toasts move inside it while open (see Floating), else the
    // ring would sit under the menu while pad users move through its items.
    onCleanup(pushLayer(el));

    // Items register in their onMount, queued after this effect. Solid flushes
    // effects synchronously, so a microtask runs once they are all registered.
    queueMicrotask(() => focusEntry(el));

    // The anchor handles its own presses (it toggles)
    function onOutside(e: PointerEvent) {
      const target = e.target as Node;
      if (el.contains(target) || props.anchor()?.contains(target)) return;
      props.onClose();
    }
    document.addEventListener('pointerdown', onOutside, true);
    onCleanup(() => {
      document.removeEventListener('pointerdown', onOutside, true);
      if (el.contains(document.activeElement)) props.anchor()?.focus();
    });
  });

  return (
    <div
      ref={el}
      popover="manual"
      class={`
        surface-overlay inset-auto m-xs overflow-visible rounded-md bg-surface p-0 text-fg shadow-lg
        ${props.class ?? ''}
      `}
    >
      <MotionRegion
        trap
        class="flex max-h-[60dvh] flex-col overflow-y-auto p-sm"
      >
        {props.children}
      </MotionRegion>
    </div>
  );
}
