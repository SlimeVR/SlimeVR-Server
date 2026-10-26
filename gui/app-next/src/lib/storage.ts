import type { CrossStorage } from '@slimevr/gui-shared';
import { type Accessor, createMemo, createRoot, createSignal } from 'solid-js';
import * as v from 'valibot';
import { platform } from '@/lib/platform';

const SETTINGS_KEY = 'config.json';
const FLUSH_DELAY_MS = 100;

// Old config key -> new key, copied on load when the new key is absent
const renamedKeys: Record<string, string> = {
  telemetry: 'dongleTelemetry',
};

export type Pref<T> = Accessor<T> & { readonly fallback: T; set(value: T): void };

export interface Cache<T> {
  get(id?: string): Promise<T | null>;
  set(value: T, id?: string): Promise<void>;
  delete(id?: string): Promise<void>;
  wrap(load: () => Promise<T | null>, id?: string): Promise<T | null>;
}

const [settings, setSettings] = createSignal<Record<string, unknown>>({});
const definedKeys = new Set<string>();
let flushTimer: ReturnType<typeof setTimeout> | undefined;

const cacheEntry = v.object({ value: v.unknown(), expiry: v.number() });

function parseJson(json: unknown): unknown {
  if (typeof json !== 'string') return undefined;
  try {
    return JSON.parse(json);
  } catch {
    return undefined;
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

function migrate(raw: Record<string, unknown>) {
  const out = { ...raw };
  for (const [from, to] of Object.entries(renamedKeys)) {
    if (from in out && !(to in out)) out[to] = out[from];
  }
  return out;
}

function claimKey(scope: string, key: string) {
  const id = `${scope}:${key}`;
  if (definedKeys.has(id)) throw new Error(`storage key defined twice: ${id}`);
  definedKeys.add(id);
}

function scheduleFlush() {
  clearTimeout(flushTimer);
  flushTimer = setTimeout(() => void flushPrefs(), FLUSH_DELAY_MS);
}

// localStorage-backed CrossStorage for platforms without a native store
export function localCrossStorage(prefix: string): CrossStorage {
  return {
    get: async <T>(key: string) =>
      (localStorage.getItem(prefix + key) as T | null) ?? undefined,
    set: async (key, value) =>
      localStorage.setItem(
        prefix + key,
        typeof value === 'string' ? value : JSON.stringify(value)
      ),
    delete: async (key) => {
      localStorage.removeItem(prefix + key);
      return true;
    },
    save: async () => true,
  };
}

// Awaited in boot after the platform is set. Prefs read defaults until then
export async function loadPrefs() {
  const raw = parseJson(await platform.storage('settings').get<string>(SETTINGS_KEY));
  setSettings(migrate(isRecord(raw) ? raw : {}));
}

export async function flushPrefs() {
  clearTimeout(flushTimer);
  flushTimer = undefined;
  const store = platform.storage('settings');
  await store.set(SETTINGS_KEY, JSON.stringify(settings()));
  await store.save();
}

export function definePref<T>(
  key: string,
  schema: v.GenericSchema<unknown, T>,
  fallback: T
): Pref<T> {
  claimKey('pref', key);
  const value = createRoot(() => {
    const raw = createMemo(() => settings()[key]);
    return createMemo(() => {
      const result = v.safeParse(schema, raw());
      return result.success ? result.output : fallback;
    });
  });
  return Object.assign(() => value(), {
    fallback,
    set(next: T) {
      setSettings((s) => ({ ...s, [key]: next }));
      scheduleFlush();
    },
  });
}

export function defineCache<T>(
  key: string,
  schema: v.GenericSchema<unknown, T>,
  ttlMs?: number
): Cache<T> {
  claimKey('cache', key);
  const store = () => platform.storage('cache');
  const storageKey = (id?: string) => (id === undefined ? key : `${key}/${id}`);

  async function get(id?: string) {
    const entry = v.safeParse(
      cacheEntry,
      parseJson(await store().get<string>(storageKey(id)))
    );
    if (!entry.success) return null;
    if (entry.output.expiry > 0 && Date.now() > entry.output.expiry) {
      await store().delete(storageKey(id));
      return null;
    }
    const value = v.safeParse(schema, entry.output.value);
    return value.success ? value.output : null;
  }

  async function set(value: T, id?: string) {
    const expiry = ttlMs ? Date.now() + ttlMs : 0;
    await store().set(storageKey(id), JSON.stringify({ value, expiry }));
  }

  return {
    get,
    set,
    delete: async (id) => {
      await store().delete(storageKey(id));
    },
    wrap: async (load, id) => {
      const cached = await get(id);
      if (cached !== null) return cached;
      const loaded = await load();
      if (loaded !== null) await set(loaded, id);
      return loaded;
    },
  };
}

// Survives reloads, cleared on restart
export function defineSession<T>(
  key: string,
  schema: v.GenericSchema<unknown, T>,
  fallback: T
): Pref<T> {
  claimKey('session', key);
  const storageKey = `slimevr-session/${key}`;
  const stored = v.safeParse(schema, parseJson(sessionStorage.getItem(storageKey)));
  const [value, setValue] = createSignal<T>(stored.success ? stored.output : fallback);
  // The accessor is returned, callers read it in their own tracked scope
  // eslint-disable-next-line solid/reactivity
  return Object.assign(value, {
    fallback,
    set(next: T) {
      setValue(() => next);
      sessionStorage.setItem(storageKey, JSON.stringify(next));
    },
  });
}
