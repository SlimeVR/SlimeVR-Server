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
  // Accessible name: raw text, or the id of a heading inside
  label?: string;
  labelledBy?: string;
  // Look and placement: surface, size, margins (UA dialog styles are reset)
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

function outside(el: HTMLElement, e: PointerEvent) {
  const r = el.getBoundingClientRect();
  return (
    e.clientX < r.left ||
    e.clientX > r.right ||
    e.clientY < r.top ||
    e.clientY > r.bottom
  );
}

// Bare modal in the top layer, rendered in place: no look, no placement, the
// caller's class decides both. TitledDialog is the titled bottom sheet one.
export function Dialog(props: DialogProps) {
  let el!: HTMLDialogElement;
  const closeable = () => props.closeable ?? true;
  let pressedBackdrop = false;

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
    focusEntry(el);
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
    pressedBackdrop = e.target === el && outside(el, e);
  }

  function onPointerUp(e: PointerEvent) {
    if (pressedBackdrop && e.target === el && outside(el, e)) requestClose();
    pressedBackdrop = false;
  }

  return (
    <dialog
      ref={el}
      aria-label={props.label}
      aria-labelledby={props.labelledBy}
      onCancel={onCancel}
      onClose={onNativeClose}
      onPointerDown={onPointerDown}
      onPointerUp={onPointerUp}
      class={cx(
        `
          flex-col text-fg opacity-0 transition-[opacity,display,overlay] transition-discrete
          duration-150 ease-out backdrop:bg-backdrop backdrop:opacity-0
          backdrop:transition-[opacity,display,overlay] backdrop:transition-discrete
          backdrop:duration-150 open:flex open:opacity-100 open:backdrop:opacity-100
          starting:open:opacity-0 starting:open:backdrop:opacity-0 motion-reduce:transition-none
        `,
        props.class
      )}
    >
      <MotionRegion trap class="flex min-h-0 grow flex-col">
        {props.children}
      </MotionRegion>
    </dialog>
  );
}

export interface TitledDialogProps extends Omit<
  DialogProps,
  'label' | 'labelledBy' | 'class'
> {
  title?: MessageId;
  titleArgs?: MessageArgs;
}

export function TitledDialog(props: TitledDialogProps) {
  let panel!: HTMLDivElement;
  const titleId = createUniqueId();
  const closeable = () => props.closeable ?? true;
  let dragFrom: number | null = null;

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
    if (distance > DISMISS_DRAG_PX && closeable()) props.onClose();
  }

  return (
    <Dialog
      open={props.open}
      onClose={props.onClose}
      closeable={props.closeable}
      labelledBy={props.title ? titleId : undefined}
      class="mt-auto max-h-[90dvh] w-full sm:m-auto sm:w-lg sm:max-w-[calc(100%-2rem)]"
    >
      <div
        ref={panel}
        class="
          flex min-h-0 flex-col rounded-t-lg surface-overlay transition-[translate] duration-150
          ease-out starting:translate-y-md motion-reduce:transition-none sm:rounded-lg
        "
      >
        <Show when={closeable()}>
          <div
            aria-hidden="true"
            onPointerDown={onDragStart}
            onPointerMove={onDragMove}
            onPointerUp={onDragEnd}
            onPointerCancel={onDragEnd}
            class="flex shrink-0 touch-none justify-center pt-sm sm:hidden"
          >
            <div class="h-xs w-10 rounded-full bg-surface-hover" />
          </div>
        </Show>
        <div class="flex min-h-0 flex-col gap-lg overflow-y-auto p-xl">
          <Show when={props.title}>
            {(title) => (
              <h2 id={titleId} class="text-section">
                {t(title(), props.titleArgs)}
              </h2>
            )}
          </Show>
          {props.children}
        </div>
      </div>
    </Dialog>
  );
}
