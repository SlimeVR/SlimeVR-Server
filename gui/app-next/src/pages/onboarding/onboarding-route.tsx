import type { Component } from 'solid-js';
import { flowRoute, type FlowPage } from '@/domain/flow';
import type { MessageId } from '@/lib/l10n';
import { type OnboardingStep, onboardingFlow } from './onboarding-flow';
import {
  MoreSetsStep,
  RuntimeStep,
  SlimeSetStep,
  StubStep,
  type StubLink,
  UsageStep,
} from './stub-steps';

function stub(title: MessageId, links?: StubLink[]): Component {
  return () => <StubStep title={title} links={links} />;
}

const toChoose: StubLink = {
  page: 'choose',
  label: 'onboarding-choose_mounting',
};

const pages: Record<OnboardingStep, FlowPage> = {
  welcome: stub('onboarding-home'),
  'slime-set': SlimeSetStep,
  dongle: stub('onboarding-wifi_creds-dongle-title'),
  connect: stub('onboarding-connect_tracker-title'),
  'more-sets': MoreSetsStep,
  assign: stub('onboarding-assign_trackers-title'),
  mounting: {
    choose: stub('onboarding-choose_mounting', [
      { page: 'step', label: 'mounting_method-step' },
      { page: 'auto', label: 'mounting_method-pose' },
      { page: 'manual', label: 'mounting_method-manual' },
    ]),
    step: stub('onboarding-step_mounting-title', [toChoose]),
    auto: stub('onboarding-automatic_mounting-title', [toChoose]),
    manual: stub('onboarding-manual_mounting', [toChoose]),
  },
  proportions: {
    scaled: stub('onboarding-user_height-title', [
      { page: 'manual', label: 'onboarding-user_height-manual-proportions' },
    ]),
    manual: stub('navbar-body_proportions', [
      { page: 'scaled', label: 'onboarding-manual_proportions-back-scaled' },
      {
        page: 'auto',
        label: 'onboarding-manual_proportions-fine_tuning_button',
      },
    ]),
    auto: stub('onboarding-automatic_proportions-title', [
      { page: 'manual', label: 'navbar-body_proportions' },
    ]),
  },
  usage: UsageStep,
  runtime: RuntimeStep,
  'mocap-pos': stub('onboarding-quiz-mocap_preferences-title'),
};

export function onboardingRoute(path: string) {
  return flowRoute({
    flow: onboardingFlow,
    path,
    pages,
    answers: () => ({}),
    onEnd: () => {},
  });
}
