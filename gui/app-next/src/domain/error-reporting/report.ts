import { sink, type Breadcrumb, type ErrorContext, type MetricAttrs } from './sink';

export function reportError(error: unknown, context: ErrorContext = {}) {
  console.error(error);
  sink().error(error, context);
}

export function addBreadcrumb(crumb: Breadcrumb) {
  sink().breadcrumb(crumb);
}

// Scope tag on every later event, undefined clears it
export function setReportTag(name: string, value: string | undefined) {
  sink().tag(name, value);
}

export function ignoreError(reason: string) {
  return (error: unknown) => {
    if (import.meta.env.DEV) console.debug(`ignored error (${reason})`, error);
  };
}

export interface MetricOptions {
  once?: 'session';
}

type MetricArgs<A> = A extends MetricAttrs ? [attrs: A] : [];

export function defineMetric<A extends MetricAttrs | void = void>(
  name: string,
  { once }: MetricOptions = {}
) {
  const sent = new Set<string>();
  return (...[attrs]: MetricArgs<A>) => {
    if (once) {
      const key = JSON.stringify(attrs ?? {});
      if (sent.has(key)) return;
      sent.add(key);
    }
    sink().metric(name, attrs);
  };
}
