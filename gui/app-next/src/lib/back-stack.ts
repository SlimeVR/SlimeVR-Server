import {
  type Accessor,
  createEffect,
  createSignal,
  onCleanup,
  untrack,
} from 'solid-js';

export interface BackOptions {
  // Dialogs: page-scope shortcuts are suspended while one is on the stack
  modal?: boolean;
}

export interface BackFallback {
  can: Accessor<boolean>;
  back(): void;
}

interface Entry {
  handle: () => void;
  modal: boolean;
}

const [entries, setEntries] = createSignal<readonly Entry[]>([]);
const [fallback, setFallback] = createSignal<BackFallback | null>(null);

export function pushBack(handle: () => void, options: BackOptions = {}): () => void {
  const entry: Entry = { handle, modal: options.modal ?? false };
  setEntries((list) => [...list, entry]);
  return () => setEntries((list) => list.filter((e) => e !== entry));
}

export function useBack(
  handle: () => void,
  options: BackOptions & { when?: Accessor<boolean> } = {}
) {
  createEffect(() => {
    if (options.when && !options.when()) return;
    onCleanup(pushBack(handle, options));
  });
}

export function setBackFallback(next: BackFallback | null): () => void {
  const previous = untrack(() => fallback());
  setFallback(() => next);
  return () => setFallback(() => previous);
}

export function dismiss(): boolean {
  const top = entries().at(-1);
  if (!top) return false;
  top.handle();
  return true;
}

export function handleBack(): boolean {
  if (dismiss()) return true;
  const history = fallback();
  if (!history?.can()) return false;
  history.back();
  return true;
}

export function canHandleBack(): boolean {
  return entries().length > 0 || (fallback()?.can() ?? false);
}

export function modalOpen(): boolean {
  return entries().some((e) => e.modal);
}
