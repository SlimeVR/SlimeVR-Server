import { createSignal, untrack } from 'solid-js';
import { client } from '@/solarxr/client';
import { defineResource, type Resource } from '@/solarxr/resource';
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

// Change handlers on the server are whole-replace, so every update sends all
// Change fields. The response must carry every Change field (compile error
// otherwise). extra response fields are ignored.
// The server answers a config change with a Response only when the config
// differs from the last one it sent, so a save is confirmed by a Response that
// matches what was sent, not by the next Response.
export function defineSettings<C extends Payload, R extends Payload & Fields<C>>({
  request,
  response,
  change,
  confirm = (sent, got) => matches(sent as Values, got),
}: SettingsOptions<R, C>): Settings<R, C> {
  const resource = defineResource({ request, response });
  // Sent but not confirmed yet: merge base for the next update and shown over
  // the server value
  let pending: Pending | undefined;
  const [overlay, setOverlay] = createSignal<Values>();
  let listening = false;

  function settle(result: SaveResult) {
    if (!pending) return;
    const { waiters, timer } = pending;
    clearTimeout(timer);
    pending = undefined;
    setOverlay(undefined);
    for (const resolve of waiters) resolve(result);
  }

  function listen() {
    if (listening) return;
    listening = true;
    client.on(response, (msg) => {
      if (pending && confirm(pending.sent as Fields<C>, msg)) settle('saved');
    });
    client.onStatus((status) => {
      if (status !== 'connected') settle('not-saved');
    });
  }

  function value() {
    const current = resource.value();
    const shown = overlay();
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
    const base = pending?.sent ?? (current as unknown as Values);
    const values = patch as Values;
    const msg = new change();
    const fields = msg as unknown as Values;
    for (const key of Object.keys(fields))
      fields[key] = key in values ? values[key] : base[key];
    if (!client.send(msg)) return null;

    const sent = { ...fields };
    const timer = setTimeout(() => {
      const latest = untrack(resource.value);
      settle(latest && confirm(sent as Fields<C>, latest) ? 'saved' : 'not-saved');
    }, SAVE_TIMEOUT_MS);
    if (pending) clearTimeout(pending.timer);
    pending = { sent, waiters: pending?.waiters ?? [], timer };
    setOverlay(sent);
    const waiters = pending.waiters;
    return new Promise<SaveResult>((resolve) => waiters.push(resolve));
  }

  return { ...resource, value, update };
}
