import {
  ChangeErrorReportingSettingsRequestT,
  ChangeResetsSettingsRequestT,
  ErrorReportingConsent,
  ErrorReportingSettingsResponseT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
} from 'solarxr-protocol';
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { saveSettings } from '@/domain/save/save-settings';
import { platform, setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { setClient } from '@/solarxr/client';
import { defineResource } from '@/solarxr/resource';
import { defineSettings } from '@/solarxr/settings';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';
import {
  answerConsent,
  consent,
  crashReportAvailable,
  defineMetric,
  initErrorReporting,
  needsConsentAnswer,
  reportError,
  setReportSink,
} from '.';
import { shouldHoldReports, shouldSendReports } from './consent';
import { sentrySink } from './sink';
import { createFakeSink, type FakeSink } from './testing';

let fake: FakeClient;
let sink: FakeSink;
const setState = vi.fn();

beforeAll(async () => {
  setPlatform(
    fakePlatform({ errorReporting: { setState, sendCrash: async () => true } })
  );
  fake = createFakeClient();
  setClient(fake);
  await initErrorReporting();
});

beforeEach(() => {
  sink = createFakeSink();
  setReportSink(sink);
  vi.spyOn(console, 'error').mockImplementation(() => {});
});

afterEach(() => {
  setReportSink(sentrySink);
});

function answer(value: ErrorReportingConsent) {
  fake.push(new ErrorReportingSettingsResponseT(value, 'user-1', 'session-1'));
}

describe('consent', () => {
  it('holds reports until the server answers, then follows it', () => {
    expect(consent()).toBe(ErrorReportingConsent.UNDECIDED);
    expect(shouldSendReports()).toBe(false);
    expect(shouldHoldReports()).toBe(true);
    expect(needsConsentAnswer()).toBe(false);

    answer(ErrorReportingConsent.UNDECIDED);
    expect(needsConsentAnswer()).toBe(true);

    answer(ErrorReportingConsent.ALLOWED);
    expect(shouldSendReports()).toBe(true);
    expect(needsConsentAnswer()).toBe(false);
    expect(crashReportAvailable()).toBe(false);

    answer(ErrorReportingConsent.DENIED);
    expect(shouldSendReports()).toBe(false);
    expect(shouldHoldReports()).toBe(false);
    expect(crashReportAvailable()).toBe(true);
  });

  it('pushes consent and identity to the platform', () => {
    answer(ErrorReportingConsent.ALLOWED);
    expect(setState).toHaveBeenLastCalledWith({
      consent: ErrorReportingConsent.ALLOWED,
      userId: 'user-1',
      sessionId: 'session-1',
    });
  });

  it('sends the answer as a settings change', () => {
    answer(ErrorReportingConsent.UNDECIDED);
    expect(answerConsent(ErrorReportingConsent.DENIED)).toBe(true);
    expect(fake.sentOf(ChangeErrorReportingSettingsRequestT).at(-1)?.consent).toBe(
      ErrorReportingConsent.DENIED
    );
  });

  it('crash reports need the platform capability', () => {
    answer(ErrorReportingConsent.UNDECIDED);
    expect(crashReportAvailable()).toBe(true);
    const errorReporting = platform.errorReporting;
    setPlatform({ ...platform, errorReporting: undefined });
    expect(crashReportAvailable()).toBe(false);
    setPlatform({ ...platform, errorReporting });
  });
});

describe('layer signals', () => {
  it('reports handler exceptions tagged with the message type', () => {
    const error = new Error('boom');
    const off = fake.on(ResetsSettingsResponseT, () => {
      throw error;
    });
    fake.push(new ResetsSettingsResponseT());
    off();
    expect(sink.errors).toEqual([
      { error, context: { tags: { rpc: 'ResetsSettingsResponse' } } },
    ]);
  });

  it('records rpc sends and receives by type only', () => {
    fake.send(new ResetsSettingsRequestT());
    expect(sink.breadcrumbs).toContainEqual({
      category: 'solarxr.rpc',
      message: 'send ResetsSettingsRequest',
    });
  });

  it('records connection changes with the close code', () => {
    fake.disconnect(1006);
    fake.connect();
    expect(sink.breadcrumbs).toContainEqual({
      category: 'solarxr',
      message: 'connection disconnected',
      data: { closeCode: 1006 },
    });
  });

  it('logs request timeouts and records resource states', () => {
    vi.useFakeTimers();
    const resource = defineResource({
      request: () => new ResetsSettingsRequestT(),
      response: ResetsSettingsResponseT,
    });
    resource.value();
    vi.advanceTimersByTime(10_000);
    expect(sink.logs).toEqual([
      { level: 'warn', message: 'request for ResetsSettingsResponse timed out' },
    ]);
    fake.push(new ResetsSettingsResponseT());
    expect(sink.breadcrumbs).toContainEqual({
      category: 'solarxr.resource',
      message: 'ResetsSettingsResponse fresh',
    });
    vi.useRealTimers();
  });

  it('records setting saves, keys only when sensitive', () => {
    const settings = defineSettings({
      request: () => new ResetsSettingsRequestT(),
      response: ResetsSettingsResponseT,
      change: ChangeResetsSettingsRequestT,
    });
    settings.value();
    fake.push(new ResetsSettingsResponseT());
    saveSettings(settings, { resetMountingFeet: true }, { sensitive: true });
    expect(sink.breadcrumbs.at(-1)).toEqual({
      category: 'settings',
      message: 'saved resetMountingFeet',
      data: undefined,
    });
  });
});

describe('reportError', () => {
  it('passes the context to the sink', () => {
    const error = new Error('x');
    reportError(error, { tags: { widget: 'skeleton' } });
    expect(sink.errors).toEqual([{ error, context: { tags: { widget: 'skeleton' } } }]);
  });
});

describe('defineMetric', () => {
  it('counts once per distinct attrs per session', () => {
    const used = defineMetric<{ method: string }>('test_method_used', {
      once: 'session',
    });
    used({ method: 'manual' });
    used({ method: 'manual' });
    used({ method: 'auto' });
    expect(sink.metrics).toEqual([
      { name: 'test_method_used', attrs: { method: 'manual' } },
      { name: 'test_method_used', attrs: { method: 'auto' } },
    ]);
  });

  it('counts every call without once', () => {
    const clicked = defineMetric('test_clicked');
    clicked();
    clicked();
    expect(sink.metrics).toHaveLength(2);
  });
});
