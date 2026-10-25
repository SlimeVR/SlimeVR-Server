import { FluentBundle, FluentResource, type FluentVariable } from '@fluent/bundle';
import { createSignal } from 'solid-js';

// `& {}` keeps the alias name on the type, so lint finds every id position by type
export type MessageId = string & {};
export type MessageArgs = Record<string, string | number>;

const [bundles, setBundles] = createSignal<FluentBundle[]>([]);

export function setL10nBundles(next: FluentBundle[]) {
  setBundles(next);
}

export function createBundle(locale: string, source: string) {
  const bundle = new FluentBundle(locale);
  for (const error of bundle.addResource(new FluentResource(source))) {
    console.warn(`[l10n] ${locale}:`, error);
  }
  return bundle;
}

function format(
  bundle: FluentBundle,
  pattern: Parameters<FluentBundle['formatPattern']>[0],
  args: MessageArgs | undefined
) {
  const errors: Error[] = [];
  const out = bundle.formatPattern(
    pattern,
    args as Record<string, FluentVariable>,
    errors
  );
  for (const error of errors) console.warn(`[l10n] ${bundle.locales[0]}:`, error);
  return out;
}

// Tests load no bundles and query by id, lint already checks ids against en-US
function missing(id: string) {
  if (import.meta.env.DEV && import.meta.env.MODE !== 'test') {
    console.warn(`[l10n] missing message ${id}`);
  }
  return id;
}

function translate(id: MessageId, args?: MessageArgs): string {
  for (const bundle of bundles()) {
    const value = bundle.getMessage(id)?.value;
    if (value) return format(bundle, value, args);
  }
  return missing(id);
}

function translateAttr(id: MessageId, attr: string, args?: MessageArgs): string {
  for (const bundle of bundles()) {
    const pattern = bundle.getMessage(id)?.attributes[attr];
    if (pattern) return format(bundle, pattern, args);
  }
  return missing(`${id}.${attr}`);
}

// Reactive: reads the bundles signal, so JSX and memos update on locale change.
// Callers read it in their own tracked scope.
// eslint-disable-next-line solid/reactivity
export const t = Object.assign(translate, { attr: translateAttr });
