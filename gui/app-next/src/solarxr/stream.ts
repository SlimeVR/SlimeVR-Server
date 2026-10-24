import { createEffect, onCleanup, untrack, type Accessor } from 'solid-js';
import { client } from '@/solarxr/client';
import type { Payload } from '@/solarxr/rpc-map';

export type StreamKey = string | number | void;

export interface StreamOptions<K extends StreamKey> {
  start: (key: K) => Payload;
  stop: (key: K) => Payload;
}

export interface Stream<K extends StreamKey> {
  // Start is sent for the first subscriber, stop after the last
  subscribe(key: K): () => void;
  // Subscribed while the owner is mounted. A changed key switches the subscription
  use(key?: Accessor<K>): void;
}

// Refcounted per key. Active keys are started again on every reconnect.
export function defineStream<K extends StreamKey = void>({
  start,
  stop,
}: StreamOptions<K>): Stream<K> {
  const subscribers = new Map<K, number>();
  let listening = false;

  function sendIfConnected(msg: () => Payload) {
    if (untrack(client.status) === 'connected') client.send(msg());
  }

  function listen() {
    if (listening) return;
    listening = true;
    client.onStatus((status) => {
      if (status !== 'connected') return;
      for (const key of subscribers.keys()) client.send(start(key));
    });
  }

  function subscribe(key: K) {
    listen();
    const count = subscribers.get(key) ?? 0;
    subscribers.set(key, count + 1);
    if (count === 0) sendIfConnected(() => start(key));
    let unsubscribed = false;
    return () => {
      if (unsubscribed) return;
      unsubscribed = true;
      const left = (subscribers.get(key) ?? 1) - 1;
      if (left > 0) {
        subscribers.set(key, left);
        return;
      }
      subscribers.delete(key);
      sendIfConnected(() => stop(key));
    };
  }

  function use(key?: Accessor<K>) {
    createEffect(() => {
      const unsubscribe = subscribe(key ? key() : (undefined as K));
      onCleanup(unsubscribe);
    });
  }

  return { subscribe, use };
}
