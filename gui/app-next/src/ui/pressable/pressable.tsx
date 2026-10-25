import type { JSX } from 'solid-js';
import { cx } from '@/lib/cx';
import { useMotionItem } from '@/ui/motion';

export interface PressableProps {
  onClick: (e: MouseEvent) => void;
  // Toggle state, sets aria-pressed
  pressed?: boolean;
  disabled?: boolean;
  // Accessible name when the content has no text
  label?: string;
  // Layout and the pressed look (`rounded-lg aria-pressed:ring-2`)
  class?: string;
  ref?: (el: HTMLButtonElement) => void;
  children: JSX.Element;
}

export function Pressable(props: PressableProps) {
  let el!: HTMLButtonElement;
  useMotionItem(() => el);

  function setRef(node: HTMLButtonElement) {
    el = node;
    props.ref?.(node);
  }

  function onClick(e: MouseEvent) {
    if (props.disabled) return;
    props.onClick(e);
  }

  return (
    <button
      ref={(node) => setRef(node)}
      type="button"
      aria-label={props.label}
      aria-pressed={props.pressed}
      aria-disabled={props.disabled ? 'true' : undefined}
      class={cx(
        'group/press block w-full text-left',
        'aria-disabled:cursor-not-allowed aria-disabled:opacity-50',
        props.class
      )}
      onClick={onClick}
    >
      {props.children}
    </button>
  );
}
