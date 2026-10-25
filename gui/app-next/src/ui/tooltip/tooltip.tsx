import { createEffect, createSignal, onCleanup } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import {
  type AnchorPlacement,
  hidePopover,
  showPopover,
  useAnchor,
} from '@/ui/popover';
import { motionActive } from '@/ui/motion';

const TOUCH_HOLD_MS = 500;

const GAP: Record<AnchorPlacement, string> = {
  bottom: 'mt-[5px]',
  'bottom-start': 'mt-[5px]',
  'bottom-end': 'mt-[5px]',
  top: 'mb-[5px]',
  'top-start': 'mb-[5px]',
  'top-end': 'mb-[5px]',
  left: 'mr-[5px]',
  right: 'ml-[5px]',
};

export interface TooltipProps {
  // Referenced by the anchor's aria-describedby when the text is a description
  id: string;
  anchor: () => HTMLElement | undefined;
  text?: MessageId;
  args?: MessageArgs;
  placement?: AnchorPlacement;
}

function focusVisible(el: HTMLElement): boolean {
  return el.matches(':focus-visible') || !!el.querySelector(':focus-visible');
}

// Shows at once on mouse hover or keyboard/pad focus, after a touch hold. Never focusable.
export function Tooltip(props: TooltipProps) {
  let el!: HTMLDivElement;
  const [open, setOpen] = createSignal(false);
  const placement = () => props.placement ?? 'bottom';

  useAnchor(
    () => el,
    () => props.anchor(),
    () => placement()
  );

  createEffect(() => {
    const anchor = props.anchor();
    if (!anchor || !props.text) {
      setOpen(false);
      return;
    }
    let timer: ReturnType<typeof setTimeout> | undefined;
    let held = false;

    function openAfter(ms: number) {
      clearTimeout(timer);
      timer = setTimeout(() => {
        held = true;
        setOpen(true);
      }, ms);
    }
    function close() {
      clearTimeout(timer);
      setOpen(false);
    }
    function onEnter(e: PointerEvent) {
      if (e.pointerType === 'mouse') setOpen(true);
    }
    function onLeave(e: PointerEvent) {
      if (e.pointerType === 'mouse') close();
    }
    function onDown(e: PointerEvent) {
      held = false;
      if (e.pointerType === 'mouse') close();
      else openAfter(TOUCH_HOLD_MS);
    }
    // A hold shows the tooltip instead of pressing the anchor
    function onUp(e: PointerEvent) {
      if (e.pointerType === 'mouse') return;
      clearTimeout(timer);
      if (held)
        anchor!.addEventListener('click', swallow, {
          capture: true,
          once: true,
        });
    }
    function swallow(e: Event) {
      e.preventDefault();
      e.stopPropagation();
    }
    function onFocus() {
      if (focusVisible(anchor!) || motionActive()) setOpen(true);
    }
    function onBlur(e: FocusEvent) {
      if (!anchor!.contains(e.relatedTarget as Node | null)) close();
    }

    anchor.addEventListener('pointerenter', onEnter);
    anchor.addEventListener('pointerleave', onLeave);
    anchor.addEventListener('pointerdown', onDown);
    anchor.addEventListener('pointerup', onUp);
    anchor.addEventListener('pointercancel', close);
    anchor.addEventListener('focusin', onFocus);
    anchor.addEventListener('focusout', onBlur);
    onCleanup(() => {
      clearTimeout(timer);
      anchor.removeEventListener('pointerenter', onEnter);
      anchor.removeEventListener('pointerleave', onLeave);
      anchor.removeEventListener('pointerdown', onDown);
      anchor.removeEventListener('pointerup', onUp);
      anchor.removeEventListener('pointercancel', close);
      anchor.removeEventListener('focusin', onFocus);
      anchor.removeEventListener('focusout', onBlur);
      anchor.removeEventListener('click', swallow, { capture: true });
    });
  });

  createEffect(() => {
    if (!open()) {
      hidePopover(el);
      return;
    }
    showPopover(el);
    function onOutside(e: Event) {
      if (e.target instanceof Node && props.anchor()?.contains(e.target))
        return;
      setOpen(false);
    }
    document.addEventListener('pointerdown', onOutside, true);
    document.addEventListener('scroll', onOutside, true);
    window.addEventListener('resize', onOutside);
    onCleanup(() => {
      document.removeEventListener('pointerdown', onOutside, true);
      document.removeEventListener('scroll', onOutside, true);
      window.removeEventListener('resize', onOutside);
    });
  });

  return (
    <div
      ref={el}
      id={props.id}
      role="tooltip"
      popover="manual"
      class={cx(
        'surface-app pointer-events-none inset-auto m-0 w-max max-w-[calc(100vw-20px)]',
        'rounded-md bg-surface p-sm text-fg',
        GAP[placement()]
      )}
    >
      {props.text ? t(props.text, props.args) : ''}
    </div>
  );
}
