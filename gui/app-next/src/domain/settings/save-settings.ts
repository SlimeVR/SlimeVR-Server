import { createSignal } from 'solid-js';
import type { Fields, Settings } from '@/solarxr/settings';

export type SaveOutcome = 'saved' | 'refused';

export interface SaveEvent {
  id: number;
  outcome: SaveOutcome;
  keys: string[];
  // left out for sensitive settings
  values?: Record<string, unknown>;
}

export interface SaveOptions {
  sensitive?: boolean;
}

const [lastSave, setLastSave] = createSignal<SaveEvent>();
const observers = new Set<(event: SaveEvent) => void>();
let nextId = 1;

// Toasts read this
export { lastSave };

export function observeSaves(cb: (event: SaveEvent) => void) {
  observers.add(cb);
  return () => {
    observers.delete(cb);
  };
}

// The only way settings are written
export function saveSettings<C>(
  settings: Settings<unknown, C>,
  patch: Partial<Fields<C>>,
  { sensitive = false }: SaveOptions = {}
): boolean {
  const saved = settings.update(patch);
  const event: SaveEvent = {
    id: nextId++,
    outcome: saved ? 'saved' : 'refused',
    keys: Object.keys(patch),
    values: sensitive ? undefined : { ...patch },
  };
  setLastSave(event);
  for (const observer of [...observers]) observer(event);
  return saved;
}
