import { createRoot } from 'solid-js';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { setReportSink } from '@/domain/error-reporting';
import { createFakeSink, type FakeSink } from '@/domain/error-reporting/testing';
import { defineFlow } from './flow';
import { createFlowSession, type FlowSessionOptions } from './session';
import {
  android,
  desktop,
  onboardingFlow,
  type OnboardingAnswers,
  type StepId,
} from './testing';

let sink: FakeSink;
let dispose: (() => void) | undefined;

beforeEach(() => {
  sink = createFakeSink();
  setReportSink(sink);
});

afterEach(() => dispose?.());

// `at` enters like a deep link
function start(
  options: Partial<FlowSessionOptions<OnboardingAnswers>> & { at?: StepId } = {}
) {
  const flow = createRoot((d) => {
    dispose = d;
    return createFlowSession(onboardingFlow, {
      env: desktop,
      answers: {},
      onEnd: () => {},
      ...options,
    });
  });
  if (options.at) flow.enter(options.at);
  return flow;
}

describe('createFlowSession', () => {
  it('pushes the trail on next and pops it on previous across the loop', () => {
    const flow = start();
    flow.next();
    flow.next({ slimeSet: 'butterfly' });
    expect(flow.step()).toBe('dongle');
    flow.next();
    flow.next({ moreSets: true });
    expect(flow.step()).toBe('slime-set');
    flow.next({ slimeSet: 'wifi-slime' });
    flow.next();
    flow.next({ moreSets: false });
    expect(flow.step()).toBe('assign');
    expect(flow.trail()).toEqual([
      'welcome',
      'slime-set',
      'dongle',
      'more-sets',
      'slime-set',
      'connect',
      'more-sets',
    ]);

    const back: StepId[] = [];
    while (flow.previous()) back.push(flow.step());
    expect(back).toEqual([
      'more-sets',
      'connect',
      'slime-set',
      'more-sets',
      'dongle',
      'slime-set',
      'welcome',
    ]);
    expect(flow.canPrevious()).toBe(false);
  });

  it('merges answers given to next', () => {
    const flow = start();
    flow.next({ usage: 'mocap' });
    flow.next({ slimeSet: 'slime-v1' });
    expect(flow.answers).toEqual({ usage: 'mocap', slimeSet: 'slime-v1' });
  });

  it('enters a reachable deep link and falls back to the path predecessor', () => {
    const flow = start({ at: 'usage' });
    expect(flow.step()).toBe('usage');
    expect(flow.trail()).toEqual([]);
    expect(flow.canPrevious()).toBe(true);
    expect(flow.previous()).toBe(true);
    expect(flow.step()).toBe('proportions');
  });

  it('redirects an unreachable deep link to the first unreached step', () => {
    expect(start({ at: 'runtime' }).step()).toBe('welcome');
    expect(start({ at: 'dongle' }).step()).toBe('welcome');
    expect(start({ at: 'dongle', answers: { slimeSet: 'butterfly' } }).step()).toBe(
      'dongle'
    );
  });

  it('re-enters mid flow: reachable clears the trail, else first unreached', () => {
    const flow = start();
    flow.next();
    flow.next();
    flow.enter('runtime');
    expect(flow.step()).toBe('connect');
    flow.enter('mounting');
    expect(flow.step()).toBe('mounting');
    expect(flow.trail()).toEqual([]);
  });

  it('calls onEnd once with the answers', () => {
    const onEnd = vi.fn();
    const flow = start({
      at: 'usage',
      env: android,
      answers: { usage: 'vr-gaming' },
      onEnd,
    });
    flow.next();
    flow.next();
    expect(onEnd).toHaveBeenCalledTimes(1);
    expect(onEnd).toHaveBeenCalledWith({ usage: 'vr-gaming' });
    expect(flow.step()).toBe('usage');
  });

  it('tracks progress', () => {
    const flow = start();
    expect(flow.progress()).toBe(0);
    flow.next();
    expect(flow.progress()).toBe(1 / 9);
  });

  // Own flow: the step metric dedupes per session, across tests too
  const tagged = defineFlow<object, 'a' | 'b'>('tagged', { a: {}, b: {} });

  it('tags flow and step, adds transition breadcrumbs, one metric per step', () => {
    const flow = createRoot((d) => {
      dispose = d;
      return createFlowSession(tagged, {
        env: desktop,
        answers: {},
        onEnd: () => {},
      });
    });
    flow.next();
    flow.previous();
    flow.next();
    expect(sink.tags).toEqual({ flow: 'tagged', step: 'b' });
    expect(sink.breadcrumbs.map((b) => b.message)).toEqual([
      'tagged: a -> b',
      'tagged: b -> a',
      'tagged: a -> b',
    ]);
    const reached = sink.metrics.filter((m) => m.name === 'flow.step_reached');
    expect(reached.map((m) => m.attrs?.step)).toEqual(['a', 'b']);

    dispose?.();
    dispose = undefined;
    expect(sink.tags).toEqual({ flow: undefined, step: undefined });
  });
});
