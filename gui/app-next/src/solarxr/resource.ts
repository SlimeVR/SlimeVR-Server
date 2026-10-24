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

// Requested on first read, then cached for the session. The response is the
// only writer. Every reconnect re-requests since the server likely restarted.
export function defineResource<R extends Payload>({
  request,
  response,
}: ResourceOptions<R>): Resource<R> {
  const [value, setValue] = createSignal<R>();
  const [state, setState] = createSignal<ResourceState>('never-loaded');
  const name = routeOf(response).name;
  let active = false;
  let loaded = false;
  let timeout: ReturnType<typeof setTimeout> | undefined;

  function transition(next: ResourceState) {
    if (untrack(state) === next) return;
    setState(next);
    emit({ kind: 'state', name, state: next });
  }

  function clearTimer() {
    clearTimeout(timeout);
    timeout = undefined;
  }

  function load() {
    if (!client.send(request())) return;
    clearTimer();
    timeout = setTimeout(() => {
      timeout = undefined;
      emit({ kind: 'timeout', name });
    }, REQUEST_TIMEOUT_MS);
  }

  function activate() {
    if (active) return;
    active = true;
    untrack(() => {
      client.on(response, (msg) => {
        loaded = true;
        clearTimer();
        batch(() => {
          setValue(() => msg);
          transition('fresh');
        });
      });
      client.onStatus((status) => {
        clearTimer();
        if (loaded) transition('stale');
        if (status === 'connected') load();
      });
      if (client.status() === 'connected') load();
    });
  }

  return {
    value() {
      activate();
      return value();
    },
    state() {
      activate();
      return state();
    },
  };
}
