export {
  answerConsent,
  consent,
  crashReportAvailable,
  errorReportingSettings,
  identity,
  needsConsentAnswer,
  sendCrashReport,
  type ConsentAnswer,
  type ReportingIdentity,
} from './consent';
export { defineMetric, ignoreError, reportError, type MetricOptions } from './report';
export { initErrorReporting, lastReportId, setKnownValues } from './sentry';
export {
  setReportSink,
  type Breadcrumb,
  type ErrorContext,
  type MetricAttrs,
  type ReportLevel,
  type ReportSink,
} from './sink';
