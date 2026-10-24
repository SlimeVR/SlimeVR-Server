import { client } from '@/solarxr/client';
import { defineResource, type Resource } from '@/solarxr/resource';
import type { Payload, PayloadCtor } from '@/solarxr/rpc-map';

// Data fields of an object-API class, without pack()
export type Fields<T> = {
  [K in keyof T as T[K] extends (...args: never[]) => unknown ? never : K]: T[K];
};

export interface Settings<R, C> extends Resource<R> {
  update(patch: Partial<Fields<C>>): boolean;
}

export interface SettingsOptions<R extends Payload, C extends Payload> {
  request: () => Payload;
  response: PayloadCtor<R>;
  change: PayloadCtor<C>;
}

// Change handlers on the server are whole-replace, so every update sends all
// Change fields. The response must carry every Change field (compile error
// otherwise). extra response fields are ignored.
export function defineSettings<C extends Payload, R extends Payload & Fields<C>>({
  request,
  response,
  change,
}: SettingsOptions<R, C>): Settings<R, C> {
  const resource = defineResource({ request, response });
  // Merge base until the server answers, so two writes inside one round trip
  // do not undo each other
  let lastSent: Record<string, unknown> | undefined;
  let listening = false;

  function listen() {
    if (listening) return;
    listening = true;
    client.on(response, () => {
      lastSent = undefined;
    });
    client.onStatus(() => {
      lastSent = undefined;
    });
  }

  function update(patch: Partial<Fields<C>>) {
    const current = resource.value();
    if (resource.state() !== 'fresh' || !current) return false;
    listen();
    const base = lastSent ?? (current as unknown as Record<string, unknown>);
    const values = patch as Record<string, unknown>;
    const msg = new change();
    const fields = msg as unknown as Record<string, unknown>;
    for (const key of Object.keys(fields))
      fields[key] = key in values ? values[key] : base[key];
    if (!client.send(msg)) return false;
    lastSent = fields;
    return true;
  }

  return { ...resource, update };
}
