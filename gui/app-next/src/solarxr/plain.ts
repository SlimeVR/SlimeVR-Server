// Solid stores only track plain objects and arrays, not object-API class
// instances. Strings typed `string | Uint8Array` always unpack as strings.
export type Plain<T> = T extends Uint8Array
  ? string
  : T extends string | number | bigint | boolean | null | undefined
    ? T
    : T extends readonly (infer U)[]
      ? Plain<U>[]
      : T extends object
        ? {
            [
              K in keyof T as T[K] extends (...args: never[]) => unknown ? never : K
            ]: Plain<T[K]>;
          }
        : T;

const decoder = new TextDecoder();

export function toPlain<T>(value: T): Plain<T> {
  return convert(value) as Plain<T>;
}

function convert(value: unknown): unknown {
  if (value === null || typeof value !== 'object') return value;
  if (value instanceof Uint8Array) return decoder.decode(value);
  if (Array.isArray(value)) return value.map(convert);
  const out: Record<string, unknown> = {};
  for (const key of Object.keys(value)) {
    out[key] = convert((value as Record<string, unknown>)[key]);
  }
  return out;
}
