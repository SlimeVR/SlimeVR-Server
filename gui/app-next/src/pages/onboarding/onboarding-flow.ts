import { defineFlow, END } from '@/domain/flow';

export type SlimeSet = 'butterfly' | 'slime-v1' | 'dongle-slime' | 'wifi-slime';
export type Usage = 'vr-gaming' | 'mocap';

export interface OnboardingAnswers {
  slimeSet?: SlimeSet;
  moreSets?: boolean;
  usage?: Usage;
  vrcOsc?: boolean;
  mocapPos?: 'forehead' | 'face';
  playspace?: 'sitting' | 'standing';
}

export type OnboardingStep =
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

export function isDongleSet(set: SlimeSet | undefined): boolean {
  return set === 'butterfly' || set === 'dongle-slime';
}

export const onboardingFlow = defineFlow<OnboardingAnswers, OnboardingStep>(
  'onboarding',
  {
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
  }
);
