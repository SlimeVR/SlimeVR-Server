import { type Accessor, createSignal, onCleanup } from 'solid-js';

export const SHELL_QUERY = '(width >= 50rem)';

export function createMediaQuery(query: string): Accessor<boolean> {
  const list = window.matchMedia(query);
  const [matches, setMatches] = createSignal(list.matches);
  const update = () => setMatches(list.matches);
  list.addEventListener('change', update);
  onCleanup(() => list.removeEventListener('change', update));
  return matches;
}
