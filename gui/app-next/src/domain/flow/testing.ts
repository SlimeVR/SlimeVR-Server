import { defineFlow, END, type FlowEnv } from './flow';

// Onboarding graph from the design for tests, the real one lands with its pages
type SlimeSet = 'butterfly' | 'slime-v1' | 'dongle-slime' | 'wifi-slime';
export interface OnboardingAnswers {
  slimeSet?: SlimeSet;
  moreSets?: boolean;
  usage?: 'vr-gaming' | 'mocap' | 'vtubing';
}
export type StepId =
  | 'welcome'
  | 'slime-set'
  | 'dongle'
  | 'connect'
  | 'more-sets'
  | 'assign'
  | 'mounting'
  | 'proportions'
  | 'usage'
  | 'runtime'
  | 'mocap-pos';

function isDongleSet(set: SlimeSet | undefined) {
  return set === 'butterfly' || set === 'dongle-slime';
}

export const onboardingFlow = defineFlow<OnboardingAnswers, StepId>('onboarding', {
  welcome: {},
  'slime-set': {
    next: (a) => (isDongleSet(a.slimeSet) ? 'dongle' : 'connect'),
  },
  dongle: { next: () => 'more-sets' },
  connect: {},
  'more-sets': { next: (a) => (a.moreSets ? 'slime-set' : 'assign') },
  assign: {},
  mounting: {},
  proportions: {},
  usage: {
    next: (a, env) =>
      a.usage !== 'vr-gaming' ? 'mocap-pos' : env.android ? END : 'runtime',
  },
  runtime: { next: () => END },
  'mocap-pos': { next: () => END },
});

export const desktop: FlowEnv = { android: false };
export const android: FlowEnv = { android: true };
