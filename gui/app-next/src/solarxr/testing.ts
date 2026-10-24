import { createSignal } from 'solid-js';
import { createHub, type Client } from '@/solarxr/client';
import type { Payload, PayloadCtor } from '@/solarxr/rpc-map';

export interface FakeClient extends Client {
  sent: Payload[];
  sentOf<M extends Payload>(ctor: PayloadCtor<M>): M[];
  push(...msgs: Payload[]): void;
  connect(): void;
  disconnect(closeCode?: number): void;
  setTimedOut(on: boolean): void;
}

export function createFakeClient({ connected = true } = {}): FakeClient {
  const sent: Payload[] = [];
  let open = connected;
  const [timedOut, setTimedOut] = createSignal(false);
  const hub = createHub(
    { isOpen: () => open, write: (_route, msg) => sent.push(msg) },
    connected ? 'connected' : 'connecting'
  );

  function connect() {
    open = true;
    hub.setStatus('connected');
  }

  function disconnect(closeCode?: number) {
    open = false;
    if (hub.status() === 'connected') hub.setStatus('disconnected', closeCode);
  }

  return {
    status: hub.status,
    timedOut,
    send: hub.send,
    on: hub.on,
    onStatus: hub.onStatus,
    observe: hub.observe,
    reconnect: connect,
    close: () => disconnect(),
    sent,
    sentOf: <M extends Payload>(ctor: PayloadCtor<M>) =>
      sent.filter((msg): msg is M => msg instanceof ctor),
    push: (...msgs) => hub.dispatch(msgs),
    connect,
    disconnect,
    setTimedOut,
  };
}
