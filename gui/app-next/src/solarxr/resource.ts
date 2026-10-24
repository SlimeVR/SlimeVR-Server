import { batch, createSignal, untrack, type Accessor } from 'solid-js';
import { client } from '@/solarxr/client';
import type { Payload, PayloadCtor } from '@/solarxr/rpc-map';

export type ResourceState = 'never-loaded' | 'stale' | 'fresh';

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
  let active = false;
  let loaded = false;

  function load() {
    client.send(request());
  }

  function activate() {
    if (active) return;
    active = true;
    untrack(() => {
      client.on(response, (msg) => {
        loaded = true;
        batch(() => {
          setValue(() => msg);
          setState('fresh');
        });
      });
      client.onStatus((status) => {
        if (loaded) setState('stale');
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
