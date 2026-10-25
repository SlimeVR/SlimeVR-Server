import { createEffect, type JSX, onCleanup } from 'solid-js';
import { useBack } from '@/lib/back-stack';
import { cx } from '@/lib/cx';
import {
  type AnchorPlacement,
  hidePopover,
  showPopover,
  useAnchor,
} from './anchor';
import { pushLayer } from '@/ui/floating';
import { focusEntry, MotionRegion } from '@/ui/motion';

export type PopoverPlacement = AnchorPlacement;

export interface PopoverProps {
  open: boolean;
  // Escape, pad B, a press outside
  onClose: () => void;
  anchor: () => HTMLElement | undefined;
  placement?: PopoverPlacement;
  class?: string;
  children: JSX.Element;
}

// Menus, dropdown lists, drawers: a manual popover positioned on its anchor.
// Never put a transform on it: Floating renders the fixed focus ring inside the
// open popover, and a transform would position the ring against the popover.
export function Popover(props: PopoverProps) {
  let el!: HTMLDivElement;

  useBack(() => props.onClose(), { when: () => props.open });

  useAnchor(
    () => el,
    () => props.anchor(),
    () => props.placement ?? 'bottom-start'
  );

  createEffect(() => {
    if (!props.open) {
      hidePopover(el);
      return;
    }
    showPopover(el);
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
      class={cx('flex-col text-fg open:flex', props.class)}
    >
      <MotionRegion
        trap
        class="flex min-h-0 flex-col overflow-y-auto overscroll-contain"
      >
        {props.children}
      </MotionRegion>
    </div>
  );
}
