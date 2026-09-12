import classNames from 'classnames';
import { ReactNode } from 'react';
import ReactModal from 'react-modal';
import { collectFocusables, isFocusable } from '@/utils/focus-nav';

/**
 * `react-modal` parks focus on its own `tabindex="-1"` content node, which is
 * not something a user can act on. Runs a frame late to land after react-modal's
 * own focus call, and leaves an explicit `autoFocus` in the content alone.
 * A `[data-nav-entry]` takes the priority
 *
 * FIXME: replace react-modal; its focus handling keeps forcing workarounds.
 */
function focusFirstControl(contentEl: HTMLDivElement) {
  requestAnimationFrame(() => {
    const active = document.activeElement;
    if (active && active !== contentEl && contentEl.contains(active)) return;

    const entry = contentEl.querySelector<HTMLElement>('[data-nav-entry]');
    const target =
      (entry && (isFocusable(entry) ? entry : collectFocusables(entry)[0])) ||
      collectFocusables(contentEl)[0];
    target?.focus();
  });
}

export function BaseModal({
  children,
  important = false,
  closeable = true,
  ...props
}: {
  isOpen: boolean;
  children: ReactNode;
  appendClasses?: string;
  important?: boolean;
  closeable?: boolean;
} & ReactModal.Props) {
  return (
    <ReactModal
      {...props}
      onAfterOpen={(obj) => {
        props.onAfterOpen?.(obj);
        if (obj?.contentEl) focusFirstControl(obj.contentEl);
      }}
      shouldCloseOnOverlayClick={closeable}
      shouldCloseOnEsc={closeable}
      overlayClassName={
        props.overlayClassName ||
        classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col justify-center',
          'items-center w-full h-full bg-background-90 bg-opacity-60',
          important ? 'z-50' : 'z-40'
        )
      }
      className={
        props.className ||
        classNames(
          'items-center focus:ring-transparent focus:ring-offset-transparent',
          'focus:outline-transparent outline-none bg-background-60 p-6 rounded-lg m-2',
          'text-background-10',
          props.appendClasses
        )
      }
    >
      {children}
    </ReactModal>
  );
}
