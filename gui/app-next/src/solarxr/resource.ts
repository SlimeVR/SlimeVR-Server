import { batch, createSignal, untrack, type Accessor } from 'solid-js';
import { client } from '@/solarxr/client';
import { routeOf, type Payload, type PayloadCtor } from '@/solarxr/rpc-map';

export type ResourceState = 'never-loaded' | 'stale' | 'fresh';

export type ResourceEvent =
  | { kind: 'state'; name: string; state: ResourceState }
  | { kind: 'timeout'; name: string };

const REQUEST_TIMEOUT_MS = 10_000;
const observers = new Set<(event: ResourceEvent) => void>();

export function observeResources(cb: (event: ResourceEvent) => void) {
  observers.add(cb);
  return () => {
    observers.delete(cb);
  };
}

function emit(event: ResourceEvent) {
  for (const observer of [...observers]) observer(event);
}

export interface Resource<R> {
  value: Accessor<R | undefined>;
  state: Accessor<ResourceState>;
}

export interface ResourceOptions<R extends Payload> {
  request: () => Payload;
  response: PayloadCtor<R>;
}

export interface KeyedResourceOptions<K, R extends Payload> {
  request: (key: K) => Payload;
  response: PayloadCtor<R>;
  // Cache key a response belongs to, e.g. its tracker id
  key: (msg: R) => K;
}

interface Entry<R> {
  resource: Resource<R>;
  setValue: (msg: R) => void;
  state: Accessor<ResourceState>;
  setState: (state: ResourceState) => void;
  active: boolean;
  loaded: boolean;
  timeout?: ReturnType<typeof setTimeout>;
}

// One cache per key over a response type shared by every key, e.g. mag toggle
// per tracker id. Each key is requested on first read, then cached for the
// session. The response is the only writer. Every reconnect re-requests the
// read keys since the server likely restarted.
export function defineKeyedResource<K, R extends Payload>({
  request,
  response,
  key,
}: KeyedResourceOptions<K, R>): (key: K) => Resource<R> {
  const name = routeOf(response).name;
  const entries = new Map<K, Entry<R>>();
  let listening = false;

  function transition(entry: Entry<R>, next: ResourceState) {
    if (untrack(entry.state) === next) return;
    entry.setState(next);
    emit({ kind: 'state', name, state: next });
  }

  function clearTimer(entry: Entry<R>) {
    clearTimeout(entry.timeout);
    entry.timeout = undefined;
  }

  function load(k: K, entry: Entry<R>) {
    if (!client.send(request(k))) return;
    clearTimer(entry);
    entry.timeout = setTimeout(() => {
      entry.timeout = undefined;
      emit({ kind: 'timeout', name });
    }, REQUEST_TIMEOUT_MS);
  }

  function listen() {
    if (listening) return;
    listening = true;
    client.on(response, (msg) => {
      const entry = entries.get(key(msg));
      if (!entry?.active) return;
      entry.loaded = true;
      clearTimer(entry);
      batch(() => {
        entry.setValue(msg);
        transition(entry, 'fresh');
      });
    });
    client.onStatus((status) => {
      for (const [k, entry] of entries) {
        if (!entry.active) continue;
        clearTimer(entry);
        if (entry.loaded) transition(entry, 'stale');
        if (status === 'connected') load(k, entry);
      }
    });
  }

  function activate(k: K, entry: Entry<R>) {
    if (entry.active) return;
    entry.active = true;
    untrack(() => {
      listen();
      if (client.status() === 'connected') load(k, entry);
    });
  }

  function create(k: K): Entry<R> {
    const [value, setValue] = createSignal<R>();
    const [state, setState] = createSignal<ResourceState>('never-loaded');
    const entry: Entry<R> = {
      setValue: (msg) => setValue(() => msg),
      state,
      setState,
      active: false,
      loaded: false,
      resource: {
        value() {
          activate(k, entry);
          return value();
        },
        state() {
          activate(k, entry);
          return state();
        },
      },
    };
    return entry;
  }

  return (k) => {
    let entry = entries.get(k);
    if (!entry) {
      entry = create(k);
      entries.set(k, entry);
    }
    return entry.resource;
  };
}

export function defineResource<R extends Payload>({
  request,
  response,
}: ResourceOptions<R>): Resource<R> {
  return defineKeyedResource({ request, response, key: () => undefined })(undefined);
}
