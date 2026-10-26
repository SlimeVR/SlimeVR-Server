import { createSignal, untrack } from 'solid-js';
import { client } from '@/solarxr/client';
import { defineKeyedResource, type Resource } from '@/solarxr/resource';
import type { Payload, PayloadCtor } from '@/solarxr/rpc-map';

// Data fields of an object-API class, without pack()
export type Fields<T> = {
  [K in keyof T as T[K] extends (...args: never[]) => unknown ? never : K]: T[K];
};

export type SaveResult = 'saved' | 'not-saved';

export interface Settings<R, C> extends Resource<R> {
  // null when refused (nothing sent), else settles once the server confirms
  update(patch: Partial<Fields<C>>): Promise<SaveResult> | null;
}

export interface SettingsOptions<R extends Payload, C extends Payload> {
  request: () => Payload;
  response: PayloadCtor<R>;
  change: PayloadCtor<C>;
  // For settings the server stores differently from what was sent. Default:
  // every sent field equals the response (null = keep)
  confirm?: (sent: Fields<C>, response: R) => boolean;
}

export const SAVE_TIMEOUT_MS = 10_000;

type Values = Record<string, unknown>;

// Flatbuffers carries floats as float32, so 0.05 comes back as 0.0500000007.
// A null sent field means "keep" on the server, so it matches anything
function same(sent: unknown, got: unknown): boolean {
  if (sent === null || sent === undefined) return true;
  if (typeof sent === 'number' && typeof got === 'number')
    return Object.is(Math.fround(sent), Math.fround(got));
  if (sent && got && typeof sent === 'object' && typeof got === 'object') {
    const a = sent as Values;
    const b = got as Values;
    return Object.keys(a).every(
      (key) => typeof a[key] === 'function' || same(a[key], b[key])
    );
  }
  return sent === got;
}

function matches(sent: Values, got: unknown) {
  if (!got) return false;
  const values = got as Values;
  return Object.keys(sent).every((key) => same(sent[key], values[key]));
}

interface Pending {
  sent: Values;
  waiters: ((result: SaveResult) => void)[];
  timer: ReturnType<typeof setTimeout>;
}

export interface KeyedSettingsOptions<
  K,
  R extends Payload,
  C extends Payload,
> extends Omit<SettingsOptions<R, C>, 'request'> {
  request: (key: K) => Payload;
  // Cache key a response belongs to, e.g. its tracker id
  key: (msg: R) => K;
}

interface Slot {
  pending?: Pending;
  overlay: () => Values | undefined;
  setOverlay: (values: Values | undefined) => void;
}

// One settings cache per key over a response type shared by every key, e.g.
// mag toggle per tracker id (0 = all trackers). A save is confirmed only by a
// response of its own key.
// Change handlers on the server are whole-replace, so every update sends all
// Change fields. The response must carry every Change field (compile error
// otherwise). extra response fields are ignored.
// The server answers a config change with a Response only when the config
// differs from the last one it sent, so a save is confirmed by a Response that
// matches what was sent, not by the next Response.
export function defineKeyedSettings<
  K,
  C extends Payload,
  R extends Payload & Fields<C>,
>({
  request,
  response,
  change,
  key,
  confirm = (sent, got) => matches(sent as Values, got),
}: KeyedSettingsOptions<K, R, C>): (key: K) => Settings<R, C> {
  const resources = defineKeyedResource({ request, response, key });
  // Sent but not confirmed yet: merge base for the next update and shown over
  // the server value
  const slots = new Map<K, Slot>();
  const cache = new Map<K, Settings<R, C>>();
  let listening = false;

  function settle(slot: Slot, result: SaveResult) {
    const { pending } = slot;
    if (!pending) return;
    clearTimeout(pending.timer);
    slot.pending = undefined;
    slot.setOverlay(undefined);
    for (const resolve of pending.waiters) resolve(result);
  }

  function listen() {
    if (listening) return;
    listening = true;
    client.on(response, (msg) => {
      const slot = slots.get(key(msg));
      if (slot?.pending && confirm(slot.pending.sent as Fields<C>, msg))
        settle(slot, 'saved');
    });
    client.onStatus((status) => {
      if (status === 'connected') return;
      for (const slot of slots.values()) settle(slot, 'not-saved');
    });
  }

  function create(k: K): Settings<R, C> {
    const resource = resources(k);
    const [overlay, setOverlay] = createSignal<Values>();
    const slot: Slot = { overlay, setOverlay: (values) => setOverlay(values) };
    slots.set(k, slot);

    function value() {
      const current = resource.value();
      const shown = slot.overlay();
      if (!current || !shown) return current;
      return Object.assign(
        Object.create(Object.getPrototypeOf(current)) as R,
        current,
        shown
      );
    }

    function update(patch: Partial<Fields<C>>) {
      const current = untrack(resource.value);
      if (untrack(resource.state) !== 'fresh' || !current) return null;
      listen();
      const base = slot.pending?.sent ?? (current as unknown as Values);
      const values = patch as Values;
      const msg = new change();
      const fields = msg as unknown as Values;
      for (const field of Object.keys(fields))
        fields[field] = field in values ? values[field] : base[field];
      if (!client.send(msg)) return null;

      const sent = { ...fields };
      const timer = setTimeout(() => {
        const latest = untrack(resource.value);
        settle(
          slot,
          latest && confirm(sent as Fields<C>, latest) ? 'saved' : 'not-saved'
        );
      }, SAVE_TIMEOUT_MS);
      if (slot.pending) clearTimeout(slot.pending.timer);
      const waiters = slot.pending?.waiters ?? [];
      slot.pending = { sent, waiters, timer };
      slot.setOverlay(sent);
      return new Promise<SaveResult>((resolve) => waiters.push(resolve));
    }

    return { ...resource, value, update };
  }

  return (k) => {
    let settings = cache.get(k);
    if (!settings) {
      settings = create(k);
      cache.set(k, settings);
    }
    return settings;
  };
}

export function defineSettings<C extends Payload, R extends Payload & Fields<C>>({
  request,
  ...options
}: SettingsOptions<R, C>): Settings<R, C> {
  return defineKeyedSettings<undefined, C, R>({
    ...options,
    request: () => request(),
    key: () => undefined,
  })(undefined);
}
