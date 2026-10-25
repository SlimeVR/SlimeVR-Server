import {
  createEffect,
  createUniqueId,
  type JSX,
  onCleanup,
  Show,
} from 'solid-js';
import { useBack } from '@/lib/back-stack';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { pushLayer } from '@/ui/floating';
import { focusEntry, MotionRegion } from '@/ui/motion';

const DISMISS_DRAG_PX = 96;

export interface DialogProps {
  open: boolean;
  onClose: () => void;
  closeable?: boolean;
  title?: MessageId;
  titleArgs?: MessageArgs;
  class?: string;
  children: JSX.Element;
}

function showModal(el: HTMLDialogElement) {
  if (el.hasAttribute('open')) return;
  if (typeof el.showModal === 'function') el.showModal();
  else el.setAttribute('open', '');
}

function closeModal(el: HTMLDialogElement) {
  if (!el.hasAttribute('open')) return;
  if (typeof el.close === 'function') el.close();
  else el.removeAttribute('open');
}

function hasAutofocus(el: HTMLElement): boolean {
  const active = document.activeElement;
  return (
    active instanceof HTMLElement &&
    el.contains(active) &&
    active.hasAttribute('autofocus')
  );
}

// Modal in the top layer, rendered in place. Bottom sheet on narrow screens.
export function Dialog(props: DialogProps) {
  let el!: HTMLDialogElement;
  let panel!: HTMLDivElement;
  const titleId = createUniqueId();
  const closeable = () => props.closeable ?? true;
  let pressedBackdrop = false;
  let dragFrom: number | null = null;

  function requestClose() {
    if (closeable()) props.onClose();
  }

  useBack(requestClose, { modal: true, when: () => props.open });

  createEffect(() => {
    if (!props.open) {
      closeModal(el);
      return;
    }
    showModal(el);
    onCleanup(pushLayer(el));
    // Items register in their onMount, queued after this effect. Solid flushes
    // effects synchronously, so a microtask runs once they are all registered.
    queueMicrotask(() => {
      if (!hasAutofocus(el)) focusEntry(el);
    });
  });

  onCleanup(() => closeModal(el));

  function onCancel(e: Event) {
    e.preventDefault();
  }

  function onNativeClose() {
    if (!props.open) return;
    if (closeable()) props.onClose();
    else showModal(el);
  }

  function onPointerDown(e: PointerEvent) {
    pressedBackdrop = e.target === el;
  }

  function onPointerUp(e: PointerEvent) {
    if (pressedBackdrop && e.target === el) requestClose();
    pressedBackdrop = false;
  }

  function onDragStart(e: PointerEvent) {
    dragFrom = e.clientY;
    panel.style.transition = 'none';
    (e.currentTarget as HTMLElement).setPointerCapture(e.pointerId);
  }

  function onDragMove(e: PointerEvent) {
    if (dragFrom === null) return;
    panel.style.translate = `0 ${Math.max(0, e.clientY - dragFrom)}px`;
  }

  function onDragEnd(e: PointerEvent) {
    if (dragFrom === null) return;
    const distance = e.clientY - dragFrom;
    dragFrom = null;
    panel.style.transition = '';
    panel.style.translate = '';
    if (distance > DISMISS_DRAG_PX) requestClose();
  }

  return (
    <dialog
      ref={el}
      aria-labelledby={props.title ? titleId : undefined}
      onCancel={onCancel}
      onClose={onNativeClose}
      onPointerDown={onPointerDown}
      onPointerUp={onPointerUp}
      class={cx(
        `
          m-0 mt-auto max-h-[90dvh] w-full max-w-full overflow-visible bg-transparent p-0 text-fg
          opacity-0 transition-[opacity,display,overlay] transition-discrete duration-150 ease-out
          backdrop:bg-backdrop backdrop:opacity-0 backdrop:transition-[opacity,display,overlay]
          backdrop:transition-discrete backdrop:duration-150 open:opacity-100
          open:backdrop:opacity-100 starting:open:opacity-0 starting:open:backdrop:opacity-0
          motion-reduce:transition-none sm:m-auto sm:w-lg sm:max-w-[calc(100%-2rem)]
        `,
        props.class
      )}
    >
      <div
        ref={panel}
        class="
          surface-overlay rounded-t-lg bg-surface transition-[translate] duration-150 ease-out
          starting:translate-y-md motion-reduce:transition-none sm:rounded-lg
        "
      >
        <Show when={closeable()}>
          <div
            aria-hidden="true"
            onPointerDown={onDragStart}
            onPointerMove={onDragMove}
            onPointerUp={onDragEnd}
            onPointerCancel={onDragEnd}
            class="flex touch-none justify-center pt-sm sm:hidden"
          >
            <div class="h-xs w-10 rounded-full bg-surface-hover" />
          </div>
        </Show>
        <MotionRegion
          trap
          class="flex max-h-[90dvh] flex-col gap-lg overflow-y-auto p-xl"
        >
          <Show when={props.title}>
            {(title) => (
              <h2 id={titleId} class="text-section">
                {t(title(), props.titleArgs)}
              </h2>
            )}
          </Show>
          {props.children}
        </MotionRegion>
      </div>
    </dialog>
  );
}
