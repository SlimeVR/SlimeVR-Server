import { type Accessor, onCleanup, onMount, useContext } from 'solid-js';
import { motionEditing, type MotionOwns, registerItem } from './core';
import { MotionParent } from './containers';

export interface MotionItemOptions {
  // passthrough (none), owns-horizontal, owns-all
  owns?: MotionOwns | Accessor<MotionOwns>;
  // Pad: A enters edit mode, then every direction goes to the widget
  edit?: boolean | Accessor<boolean>;
}

function read<T>(value: T | Accessor<T> | undefined, fallback: T): T {
  if (value === undefined) return fallback;
  return typeof value === 'function' ? (value as Accessor<T>)() : value;
}

// Registers a focusable element with the enclosing motion container. No-op outside MotionRoot.
export function useMotionItem(
  ref: () => HTMLElement | undefined,
  options: MotionItemOptions = {}
) {
  const parent = useContext(MotionParent);
  onMount(() => {
    const el = ref();
    if (!parent || !el) return;
    onCleanup(
      registerItem(
        parent,
        el,
        () => read(options.owns, 'none'),
        () => read(options.edit, false)
      )
    );
  });
  return { editing: () => motionEditing()?.el === ref() };
}
