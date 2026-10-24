import * as Sentry from '@sentry/solid';

export type ReportLevel = 'info' | 'warn' | 'error';
export type MetricAttrs = Record<string, string | number | boolean>;

export interface ErrorContext {
  tags?: Record<string, string>;
  extra?: Record<string, unknown>;
}

export interface Breadcrumb {
  category: string;
  message: string;
  data?: Record<string, unknown>;
}

export interface ReportSink {
  error(error: unknown, context: ErrorContext): void;
  breadcrumb(crumb: Breadcrumb): void;
  log(level: ReportLevel, message: string): void;
  metric(name: string, attrs: MetricAttrs | undefined): void;
}

export const sentrySink: ReportSink = {
  error(error, { tags, extra }) {
    Sentry.captureException(error, { tags, extra });
  },
  breadcrumb(crumb) {
    Sentry.addBreadcrumb(crumb);
  },
  log(level, message) {
    Sentry.logger[level](message);
  },
  metric(name, attrs) {
    Sentry.metrics.count(name, 1, attrs ? { attributes: attrs } : undefined);
  },
};

let current: ReportSink = sentrySink;

export function sink() {
  return current;
}

export function setReportSink(next: ReportSink) {
  current = next;
}
