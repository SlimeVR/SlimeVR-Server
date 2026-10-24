import * as solarxr from 'solarxr-protocol';
import {
  DataFeedMessage,
  RpcMessage,
  type DataFeedMessageHeaderT,
  type RpcMessageHeaderT,
} from 'solarxr-protocol';

export type RpcPayload = NonNullable<RpcMessageHeaderT['message']>;
export type DataFeedPayload = NonNullable<DataFeedMessageHeaderT['message']>;
export type Payload = RpcPayload | DataFeedPayload;
export type PayloadCtor<M extends Payload = Payload> = new (...args: never[]) => M;

export type Route =
  | { kind: 'rpc'; type: RpcMessage; name: string }
  | { kind: 'datafeed'; type: DataFeedMessage; name: string };

function buildRoutes(): Map<PayloadCtor, Route> {
  const exports = solarxr as unknown as Record<string, unknown>;
  const routes = new Map<PayloadCtor, Route>();
  function add(kind: Route['kind'], members: Record<string, string | number>) {
    for (const [name, type] of Object.entries(members)) {
      if (typeof type !== 'number' || type === 0) continue;
      const ctor = exports[`${name}T`];
      if (typeof ctor !== 'function')
        throw new Error(`solarxr-protocol has no ${name}T`);
      routes.set(ctor as PayloadCtor, { kind, type, name } as Route);
    }
  }
  add('rpc', RpcMessage);
  add('datafeed', DataFeedMessage);
  return routes;
}

const routes = buildRoutes();

export function routeOf(ctor: PayloadCtor): Route {
  const route = routes.get(ctor);
  if (!route) throw new Error(`${ctor.name} is not an RpcMessage or DataFeedMessage`);
  return route;
}
