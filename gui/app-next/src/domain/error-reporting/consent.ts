import {
  ChangeErrorReportingSettingsRequestT,
  ErrorReportingConsent,
  ErrorReportingSettingsRequestT,
  ErrorReportingSettingsResponseT,
} from 'solarxr-protocol';
import { saveSettings } from '@/domain/settings/save-settings';
import { platform, type CrashKind } from '@/lib/platform';
import { client } from '@/solarxr/client';
import { defineSettings } from '@/solarxr/settings';

export const errorReportingSettings = defineSettings({
  request: () => new ErrorReportingSettingsRequestT(),
  response: ErrorReportingSettingsResponseT,
  change: ChangeErrorReportingSettingsRequestT,
});

export type ConsentAnswer =
  ErrorReportingConsent.ALLOWED | ErrorReportingConsent.DENIED;

export interface ReportingIdentity {
  userId: string;
  sessionId: string;
}

// UNDECIDED until the server answered
export function consent() {
  return errorReportingSettings.value()?.consent ?? ErrorReportingConsent.UNDECIDED;
}

export function identity(): ReportingIdentity | null {
  const value = errorReportingSettings.value();
  if (!value) return null;
  return {
    userId: typeof value.userId === 'string' ? value.userId : '',
    sessionId: typeof value.sessionId === 'string' ? value.sessionId : '',
  };
}

export function shouldSendReports() {
  return consent() === ErrorReportingConsent.ALLOWED;
}

export function shouldHoldReports() {
  return consent() !== ErrorReportingConsent.DENIED;
}

export function needsConsentAnswer() {
  return (
    client.status() === 'connected' &&
    errorReportingSettings.state() === 'fresh' &&
    consent() === ErrorReportingConsent.UNDECIDED
  );
}

export function answerConsent(answer: ConsentAnswer) {
  return saveSettings(errorReportingSettings, { consent: answer });
}

// Only Electron main has the server crash data
export function crashReportAvailable() {
  return !!platform.errorReporting && consent() !== ErrorReportingConsent.ALLOWED;
}

export function sendCrashReport(kind: CrashKind) {
  return platform.errorReporting?.sendCrash(kind) ?? Promise.resolve(false);
}
