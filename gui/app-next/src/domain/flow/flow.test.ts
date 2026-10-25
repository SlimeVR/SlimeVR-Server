import { describe, expect, it } from 'vitest';
import {
  defineFlow,
  END,
  firstUnreached,
  isReachable,
  predecessor,
  progress,
  walk,
} from './flow';
import { android, desktop, onboardingFlow, type StepId } from './testing';

const DEFAULT_PATH: StepId[] = [
  'welcome',
  'slime-set',
  'connect',
  'more-sets',
  'assign',
  'mounting',
  'proportions',
  'usage',
  'mocap-pos',
];

describe('defineFlow next', () => {
  const next = onboardingFlow.next;

  it('defaults to the following step in declaration order', () => {
    expect(onboardingFlow.first).toBe('welcome');
    expect(next('welcome', {}, desktop)).toBe('slime-set');
    expect(next('connect', {}, desktop)).toBe('more-sets');
    expect(next('assign', {}, desktop)).toBe('mounting');
    expect(next('proportions', {}, desktop)).toBe('usage');
  });

  it('ends after the last declared step without next', () => {
    const flow = defineFlow<object, 'a' | 'b'>('two', { a: {}, b: {} });
    expect(flow.next('b', {}, desktop)).toBe(END);
  });

  it('branches slime-set on the set kind', () => {
    expect(next('slime-set', { slimeSet: 'butterfly' }, desktop)).toBe('dongle');
    expect(next('slime-set', { slimeSet: 'dongle-slime' }, desktop)).toBe('dongle');
    expect(next('slime-set', { slimeSet: 'wifi-slime' }, desktop)).toBe('connect');
    expect(next('slime-set', {}, desktop)).toBe('connect');
    expect(next('dongle', {}, desktop)).toBe('more-sets');
  });

  it('loops more-sets back to slime-set', () => {
    expect(next('more-sets', { moreSets: true }, desktop)).toBe('slime-set');
    expect(next('more-sets', { moreSets: false }, desktop)).toBe('assign');
  });

  it('branches usage on usage and platform', () => {
    expect(next('usage', { usage: 'vr-gaming' }, desktop)).toBe('runtime');
    expect(next('usage', { usage: 'vr-gaming' }, android)).toBe(END);
    expect(next('usage', { usage: 'mocap' }, desktop)).toBe('mocap-pos');
    expect(next('usage', { usage: 'vtubing' }, android)).toBe('mocap-pos');
    expect(next('runtime', {}, desktop)).toBe(END);
    expect(next('mocap-pos', {}, desktop)).toBe(END);
  });
});

describe('walk', () => {
  it('takes the default branches for unanswered questions', () => {
    expect(walk(onboardingFlow, 'welcome', {}, desktop)).toEqual(DEFAULT_PATH);
  });

  it('stops before a repeated step', () => {
    const path = walk(
      onboardingFlow,
      'welcome',
      { slimeSet: 'butterfly', moreSets: true },
      desktop
    );
    expect(path).toEqual(['welcome', 'slime-set', 'dongle', 'more-sets']);
  });

  it('starts at the given step', () => {
    expect(walk(onboardingFlow, 'usage', { usage: 'vr-gaming' }, desktop)).toEqual([
      'usage',
      'runtime',
    ]);
  });
});

describe('isReachable', () => {
  it('accepts steps on the path from the start', () => {
    expect(isReachable(onboardingFlow, 'usage', {}, desktop)).toBe(true);
    expect(isReachable(onboardingFlow, 'dongle', {}, desktop)).toBe(false);
    expect(
      isReachable(onboardingFlow, 'dongle', { slimeSet: 'butterfly' }, desktop)
    ).toBe(true);
  });

  it('follows the platform', () => {
    const vr = { usage: 'vr-gaming' } as const;
    expect(isReachable(onboardingFlow, 'runtime', vr, desktop)).toBe(true);
    expect(isReachable(onboardingFlow, 'runtime', vr, android)).toBe(false);
    expect(isReachable(onboardingFlow, 'runtime', {}, desktop)).toBe(false);
  });
});

describe('predecessor', () => {
  it('goes back from usage to proportions', () => {
    expect(predecessor(onboardingFlow, 'usage', {}, desktop)).toBe('proportions');
  });

  it('has none for the first or an unreachable step', () => {
    expect(predecessor(onboardingFlow, 'welcome', {}, desktop)).toBeUndefined();
    expect(predecessor(onboardingFlow, 'runtime', {}, desktop)).toBeUndefined();
  });

  it('follows the answered branch', () => {
    const dongle = { slimeSet: 'butterfly' } as const;
    expect(predecessor(onboardingFlow, 'more-sets', dongle, desktop)).toBe('dongle');
    expect(predecessor(onboardingFlow, 'more-sets', {}, desktop)).toBe('connect');
  });
});

describe('firstUnreached', () => {
  it('is the start for an empty trail', () => {
    expect(firstUnreached(onboardingFlow, [], {}, desktop)).toBe('welcome');
  });

  it('is the first path step not in the trail', () => {
    expect(firstUnreached(onboardingFlow, ['welcome', 'slime-set'], {}, desktop)).toBe(
      'connect'
    );
  });
});

describe('progress', () => {
  it('is 0 on the first step', () => {
    expect(progress(onboardingFlow, [], 'welcome', {}, desktop)).toBe(0);
  });

  it('counts visited steps against what is left', () => {
    const trail: StepId[] = ['welcome', 'slime-set', 'connect', 'more-sets'];
    // 4 visited, then assign mounting proportions usage mocap-pos
    expect(progress(onboardingFlow, trail, 'assign', {}, desktop)).toBe(4 / 9);
    // vr-gaming on android ends at usage
    expect(
      progress(
        onboardingFlow,
        [...trail, 'assign', 'mounting', 'proportions'],
        'usage',
        { usage: 'vr-gaming' },
        android
      )
    ).toBe(7 / 8);
  });
});
