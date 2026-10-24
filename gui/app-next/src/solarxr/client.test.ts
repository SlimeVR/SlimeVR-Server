import {
  HeartbeatRequestT,
  PollDataFeedT,
  ResetRequestT,
  ResetType,
  ServerInfosResponseT,
} from 'solarxr-protocol';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import {
  createSocketClient,
  decodeBundle,
  encodeBundle,
  serverUrl,
  type ClientEvent,
} from '@/solarxr/client';
import { routeOf, type PayloadCtor } from '@/solarxr/rpc-map';
import { createFakeClient } from '@/solarxr/testing';

class FakeSocket {
  static readonly OPEN = 1;
  static instances: FakeSocket[] = [];
  readyState = 0;
  binaryType = 'blob';
  sent: Uint8Array[] = [];
  closed = false;
  onopen: (() => void) | null = null;
  onclose: ((event: { code: number }) => void) | null = null;
  onmessage: ((event: { data: unknown }) => void) | null = null;

  constructor(readonly url: string) {
    FakeSocket.instances.push(this);
  }

  send(data: Uint8Array) {
    this.sent.push(data);
  }

  close() {
    this.closed = true;
  }

  serverOpen() {
    this.readyState = FakeSocket.OPEN;
    this.onopen?.();
  }

  serverClose(code = 1006) {
    this.readyState = 3;
    this.onclose?.({ code });
  }
}

function latestSocket() {
  const socket = FakeSocket.instances.at(-1);
  if (!socket) throw new Error('no socket opened');
  return socket;
}

describe('rpc map', () => {
  it('routes rpc and datafeed classes', () => {
    expect(routeOf(ResetRequestT)).toMatchObject({ kind: 'rpc', name: 'ResetRequest' });
    expect(routeOf(PollDataFeedT)).toMatchObject({
      kind: 'datafeed',
      name: 'PollDataFeed',
    });
  });

  it('rejects classes outside the unions', () => {
    class NotAMessage {}
    expect(() => routeOf(NotAMessage as unknown as PayloadCtor)).toThrow();
  });
});

describe('bundle codec', () => {
  it('round trips a message', () => {
    const msg = new ResetRequestT(ResetType.FULL);
    const [decoded] = decodeBundle(encodeBundle(routeOf(ResetRequestT), msg));
    expect(decoded).toBeInstanceOf(ResetRequestT);
    expect((decoded as ResetRequestT).resetType).toBe(ResetType.FULL);
  });
});

describe('serverUrl', () => {
  it('defaults to localhost and reads ip/port', () => {
    expect(serverUrl('')).toBe('ws://localhost:21110');
    expect(serverUrl('?ip=10.0.0.2&port=1234')).toBe('ws://10.0.0.2:1234');
  });
});

describe('dispatch', () => {
  it('calls handlers of the received type only', () => {
    const client = createFakeClient();
    const resets: ResetRequestT[] = [];
    const heartbeats = vi.fn();
    client.on(ResetRequestT, (msg) => resets.push(msg));
    client.on(HeartbeatRequestT, heartbeats);

    client.push(new ResetRequestT(ResetType.MOUNTING));

    expect(resets.map((msg) => msg.resetType)).toEqual([ResetType.MOUNTING]);
    expect(heartbeats).not.toHaveBeenCalled();
  });

  it('stops after unsubscribe', () => {
    const client = createFakeClient();
    const cb = vi.fn();
    const off = client.on(ServerInfosResponseT, cb);
    off();
    client.push(new ServerInfosResponseT());
    expect(cb).not.toHaveBeenCalled();
  });

  it('keeps dispatching when a handler throws and reports it', () => {
    vi.spyOn(console, 'error').mockImplementation(() => {});
    const client = createFakeClient();
    const events: ClientEvent[] = [];
    client.observe((event) => events.push(event));
    const after = vi.fn();
    client.on(ResetRequestT, () => {
      throw new Error('boom');
    });
    client.on(ResetRequestT, after);

    client.push(new ResetRequestT());

    expect(after).toHaveBeenCalledOnce();
    expect(events).toContainEqual(
      expect.objectContaining({ kind: 'handler-error', name: 'ResetRequest' })
    );
  });

  it('reports rpc receives but not datafeed', () => {
    const client = createFakeClient();
    const events: ClientEvent[] = [];
    client.observe((event) => events.push(event));
    client.push(new PollDataFeedT(), new HeartbeatRequestT());
    expect(events).toEqual([{ kind: 'receive', name: 'HeartbeatRequest' }]);
  });
});

describe('send', () => {
  it('records sends while connected', () => {
    const client = createFakeClient();
    expect(client.send(new ResetRequestT())).toBe(true);
    expect(client.sentOf(ResetRequestT)).toHaveLength(1);
  });

  it('refuses when disconnected', () => {
    const client = createFakeClient();
    const events: ClientEvent[] = [];
    client.observe((event) => events.push(event));
    client.disconnect();

    expect(client.send(new ResetRequestT())).toBe(false);
    expect(client.sent).toEqual([]);
    expect(events).toContainEqual({ kind: 'refused', name: 'ResetRequest' });
  });

  it('refuses before the first connection', () => {
    const client = createFakeClient({ connected: false });
    expect(client.status()).toBe('connecting');
    expect(client.send(new ResetRequestT())).toBe(false);
  });
});

describe('socket client', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    FakeSocket.instances = [];
    vi.stubGlobal('WebSocket', FakeSocket);
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('connects, encodes sends and dispatches received bundles', () => {
    const client = createSocketClient({ url: 'ws://test' });
    const socket = latestSocket();
    expect(socket.binaryType).toBe('arraybuffer');
    expect(client.send(new ResetRequestT())).toBe(false);

    socket.serverOpen();
    expect(client.status()).toBe('connected');

    expect(client.send(new ResetRequestT(ResetType.FULL))).toBe(true);
    const [sent] = decodeBundle(socket.sent[0]!);
    expect((sent as ResetRequestT).resetType).toBe(ResetType.FULL);

    const received = vi.fn();
    client.on(ServerInfosResponseT, received);
    const bytes = encodeBundle(
      routeOf(ServerInfosResponseT),
      new ServerInfosResponseT()
    );
    socket.onmessage?.({ data: bytes.slice().buffer });
    expect(received).toHaveBeenCalledOnce();
    client.close();
  });

  it('reconnects after the retry delay and notifies status handlers', () => {
    const client = createSocketClient({ url: 'ws://test', retryMs: 3000 });
    const statuses: string[] = [];
    client.onStatus((status) => statuses.push(status));
    latestSocket().serverOpen();

    latestSocket().serverClose();
    expect(client.status()).toBe('disconnected');
    expect(FakeSocket.instances).toHaveLength(1);

    vi.advanceTimersByTime(3000);
    expect(FakeSocket.instances).toHaveLength(2);
    latestSocket().serverClose();
    expect(client.status()).toBe('disconnected');

    vi.advanceTimersByTime(3000);
    latestSocket().serverOpen();
    expect(statuses).toEqual(['connected', 'disconnected', 'connected']);
    client.close();
  });

  it('flags a first connection timeout and clears it on connect', () => {
    const client = createSocketClient({ url: 'ws://test', timeoutMs: 10_000 });
    vi.advanceTimersByTime(9_999);
    expect(client.timedOut()).toBe(false);
    vi.advanceTimersByTime(1);
    expect(client.timedOut()).toBe(true);
    latestSocket().serverOpen();
    expect(client.timedOut()).toBe(false);
    client.close();
  });

  it('manual reconnect drops the old socket', () => {
    const client = createSocketClient({ url: 'ws://test' });
    const first = latestSocket();
    first.serverOpen();
    client.reconnect();
    expect(first.closed).toBe(true);
    expect(client.status()).toBe('disconnected');
    // A late close from the dropped socket must not schedule a retry
    first.serverClose();
    vi.advanceTimersByTime(10_000);
    expect(FakeSocket.instances).toHaveLength(2);
    client.close();
  });

  it('stops retrying after close', () => {
    const client = createSocketClient({ url: 'ws://test' });
    latestSocket().serverOpen();
    client.close();
    vi.advanceTimersByTime(10_000);
    expect(FakeSocket.instances).toHaveLength(1);
    expect(client.status()).toBe('disconnected');
  });
});
