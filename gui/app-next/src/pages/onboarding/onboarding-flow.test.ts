import { describe, expect, it } from 'vitest';
import { END, type FlowEnv, walk } from '@/domain/flow';
import { onboardingFlow } from './onboarding-flow';

const desktop: FlowEnv = { android: false };
const android: FlowEnv = { android: true };
const next = onboardingFlow.next;

describe('onboardingFlow', () => {
  it('sends dongle sets to the dongle step, wifi sets to connect', () => {
    expect(next('slime-set', { slimeSet: 'butterfly' }, desktop)).toBe('dongle');
    expect(next('slime-set', { slimeSet: 'dongle-slime' }, desktop)).toBe('dongle');
    expect(next('slime-set', { slimeSet: 'slime-v1' }, desktop)).toBe('connect');
    expect(next('slime-set', { slimeSet: 'wifi-slime' }, desktop)).toBe('connect');
    expect(next('dongle', {}, desktop)).toBe('more-sets');
    expect(next('connect', {}, desktop)).toBe('more-sets');
  });

  it('loops more-sets back to slime-set', () => {
    expect(next('more-sets', { moreSets: true }, desktop)).toBe('slime-set');
    expect(next('more-sets', { moreSets: false }, desktop)).toBe('assign');
  });

  it('ends VR gaming on android, asks the runtime elsewhere', () => {
    expect(next('usage', { usage: 'vr-gaming' }, desktop)).toBe('runtime');
    expect(next('usage', { usage: 'vr-gaming' }, android)).toBe(END);
    expect(next('usage', { usage: 'mocap' }, android)).toBe('mocap-pos');
    expect(next('runtime', {}, desktop)).toBe(END);
    expect(next('mocap-pos', {}, desktop)).toBe(END);
  });

  it('runs setup steps in order before the usage question', () => {
    expect(walk(onboardingFlow, 'assign', {}, desktop)).toEqual([
      'assign',
      'mounting',
      'proportions',
      'usage',
      'mocap-pos',
    ]);
  });
});
