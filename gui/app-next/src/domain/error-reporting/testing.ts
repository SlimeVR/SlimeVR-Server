import type {
  Breadcrumb,
  ErrorContext,
  MetricAttrs,
  ReportLevel,
  ReportSink,
} from './sink';

export interface FakeSink extends ReportSink {
  errors: { error: unknown; context: ErrorContext }[];
  breadcrumbs: Breadcrumb[];
  logs: { level: ReportLevel; message: string }[];
  metrics: { name: string; attrs: MetricAttrs | undefined }[];
}

export function createFakeSink(): FakeSink {
  const fake: FakeSink = {
    errors: [],
    breadcrumbs: [],
    logs: [],
    metrics: [],
    error: (error, context) => fake.errors.push({ error, context }),
    breadcrumb: (crumb) => fake.breadcrumbs.push(crumb),
    log: (level, message) => fake.logs.push({ level, message }),
    metric: (name, attrs) => fake.metrics.push({ name, attrs }),
  };
  return fake;
}
