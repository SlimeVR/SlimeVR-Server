import { Builder, ByteBuffer } from 'flatbuffers';
import { batch, createSignal, type Accessor } from 'solid-js';
import {
  DataFeedMessageHeaderT,
  MessageBundle,
  MessageBundleT,
  RpcMessageHeaderT,
} from 'solarxr-protocol';
import {
  routeOf,
  type DataFeedPayload,
  type Payload,
  type PayloadCtor,
  type Route,
  type RpcPayload,
} from '@/solarxr/rpc-map';

export type ConnectionStatus = 'connecting' | 'connected' | 'disconnected';

export type ClientEvent =
  | { kind: 'status'; status: ConnectionStatus; closeCode?: number }
  | { kind: 'send'; name: string }
  | { kind: 'receive'; name: string }
  | { kind: 'refused'; name: string }
  | { kind: 'handler-error'; name: string; error: unknown };

export interface Client {
  status: Accessor<ConnectionStatus>;
  timedOut: Accessor<boolean>;
  // false when not connected, nothing is queued
  send(msg: Payload): boolean;
  on<M extends Payload>(ctor: PayloadCtor<M>, cb: (msg: M) => void): () => void;
  onStatus(cb: (status: ConnectionStatus) => void): () => void;
  observe(cb: (event: ClientEvent) => void): () => void;
  reconnect(): void;
  close(): void;
}

export interface Transport {
  isOpen(): boolean;
  write(route: Route, msg: Payload): void;
}

export type Hub = Omit<Client, 'timedOut' | 'reconnect' | 'close'> & {
  setStatus(next: ConnectionStatus, closeCode?: number): void;
  dispatch(msgs: Payload[]): void;
};

// Shared by the socket client and the fake client
export function createHub(transport: Transport, initial: ConnectionStatus): Hub {
  const [status, setStatusSignal] = createSignal<ConnectionStatus>(initial);
  const handlers = new Map<PayloadCtor, Set<(msg: Payload) => void>>();
  const statusHandlers = new Set<(status: ConnectionStatus) => void>();
  const observers = new Set<(event: ClientEvent) => void>();

  function emit(event: ClientEvent) {
    for (const observer of [...observers]) observer(event);
  }

  function guarded(name: string, run: () => void) {
    try {
      run();
    } catch (error) {
      console.error(`handler for ${name} failed`, error);
      emit({ kind: 'handler-error', name, error });
    }
  }

  function setStatus(next: ConnectionStatus, closeCode?: number) {
    if (status() === next) return;
    setStatusSignal(next);
    emit({ kind: 'status', status: next, closeCode });
    for (const cb of [...statusHandlers]) guarded(`status ${next}`, () => cb(next));
  }

  function dispatch(msgs: Payload[]) {
    batch(() => {
      for (const msg of msgs) {
        const ctor = msg.constructor as PayloadCtor;
        const route = routeOf(ctor);
        if (route.kind === 'rpc') emit({ kind: 'receive', name: route.name });
        for (const cb of [...(handlers.get(ctor) ?? [])])
          guarded(route.name, () => cb(msg));
      }
    });
  }

  function send(msg: Payload) {
    const route = routeOf(msg.constructor as PayloadCtor);
    if (!transport.isOpen()) {
      emit({ kind: 'refused', name: route.name });
      return false;
    }
    transport.write(route, msg);
    if (route.kind === 'rpc') emit({ kind: 'send', name: route.name });
    return true;
  }

  function on<M extends Payload>(ctor: PayloadCtor<M>, cb: (msg: M) => void) {
    routeOf(ctor);
    let set = handlers.get(ctor);
    if (!set) handlers.set(ctor, (set = new Set()));
    const handler = cb as (msg: Payload) => void;
    set.add(handler);
    return () => {
      set.delete(handler);
    };
  }

  function subscribe<T>(set: Set<T>, cb: T) {
    set.add(cb);
    return () => {
      set.delete(cb);
    };
  }

  return {
    status,
    send,
    on,
    onStatus: (cb) => subscribe(statusHandlers, cb),
    observe: (cb) => subscribe(observers, cb),
    setStatus,
    dispatch,
  };
}

export function encodeBundle(route: Route, msg: Payload): Uint8Array<ArrayBuffer> {
  const bundle = new MessageBundleT();
  if (route.kind === 'rpc') {
    const header = new RpcMessageHeaderT();
    header.messageType = route.type;
    header.message = msg as RpcPayload;
    bundle.rpcMsgs = [header];
  } else {
    const header = new DataFeedMessageHeaderT();
    header.messageType = route.type;
    header.message = msg as DataFeedPayload;
    bundle.dataFeedMsgs = [header];
  }
  const builder = new Builder(1);
  builder.finish(bundle.pack(builder));
  return builder.asUint8Array().slice();
}

export function decodeBundle(bytes: Uint8Array): Payload[] {
  const bundle = MessageBundle.getRootAsMessageBundle(new ByteBuffer(bytes)).unpack();
  const msgs: Payload[] = [];
  for (const header of bundle.rpcMsgs) if (header.message) msgs.push(header.message);
  for (const header of bundle.dataFeedMsgs)
    if (header.message) msgs.push(header.message);
  return msgs;
}

export function serverUrl(search = window.location.search): string {
  const params = new URLSearchParams(search);
  return `ws://${params.get('ip') ?? 'localhost'}:${params.get('port') ?? '21110'}`;
}

export interface SocketClientOptions {
  url: string;
  retryMs?: number;
  timeoutMs?: number;
}

export function createSocketClient({
  url,
  retryMs = 3000,
  timeoutMs = 10_000,
}: SocketClientOptions): Client {
  let socket: WebSocket | null = null;
  let retryTimer: ReturnType<typeof setTimeout> | undefined;
  let closed = false;
  const [timedOut, setTimedOut] = createSignal(false);

  const hub = createHub(
    {
      isOpen: () => socket?.readyState === WebSocket.OPEN,
      write: (route, msg) => socket?.send(encodeBundle(route, msg)),
    },
    'connecting'
  );

  const timeoutTimer = setTimeout(() => {
    if (hub.status() === 'connecting') setTimedOut(true);
  }, timeoutMs);

  function detach() {
    if (!socket) return;
    const old = socket;
    socket = null;
    old.onopen = old.onclose = old.onmessage = null;
    old.close();
  }

  function lost(closeCode?: number) {
    if (hub.status() === 'connected') hub.setStatus('disconnected', closeCode);
  }

  function open() {
    clearTimeout(retryTimer);
    const ws = new WebSocket(url);
    ws.binaryType = 'arraybuffer';
    ws.onopen = () => {
      setTimedOut(false);
      hub.setStatus('connected');
    };
    ws.onclose = (event) => {
      if (socket !== ws) return;
      socket = null;
      lost(event.code);
      if (!closed) retryTimer = setTimeout(open, retryMs);
    };
    ws.onmessage = (event) => {
      if (event.data instanceof ArrayBuffer)
        hub.dispatch(decodeBundle(new Uint8Array(event.data)));
    };
    socket = ws;
  }

  open();

  return {
    status: hub.status,
    timedOut,
    send: hub.send,
    on: hub.on,
    onStatus: hub.onStatus,
    observe: hub.observe,
    reconnect() {
      if (closed) return;
      detach();
      lost();
      open();
    },
    close() {
      closed = true;
      clearTimeout(retryTimer);
      clearTimeout(timeoutTimer);
      detach();
      lost();
    },
  };
}

export let client: Client;

export function setClient(next: Client) {
  client = next;
}
