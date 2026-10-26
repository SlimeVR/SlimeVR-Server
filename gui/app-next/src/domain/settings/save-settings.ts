import { createSignal } from 'solid-js';
import type { Fields, Settings } from '@/solarxr/settings';

// pending until the server confirms, then saved or not-saved. refused = never
// sent (not loaded or disconnected)
export type SaveOutcome = 'pending' | 'saved' | 'not-saved' | 'refused';

// One save emits pending then its result under the same id, or refused alone
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

function emit(event: SaveEvent) {
  setLastSave(event);
  for (const observer of [...observers]) observer(event);
}

// The only way settings are written. true when sent, the result comes later
// through the save store
export function saveSettings<C>(
  settings: Settings<unknown, C>,
  patch: Partial<Fields<C>>,
  { sensitive = false }: SaveOptions = {}
): boolean {
  const result = settings.update(patch);
  const base = {
    id: nextId++,
    keys: Object.keys(patch),
    values: sensitive ? undefined : { ...patch },
  };
  if (!result) {
    emit({ ...base, outcome: 'refused' });
    return false;
  }
  emit({ ...base, outcome: 'pending' });
  void result.then((outcome) => emit({ ...base, outcome }));
  return true;
}
