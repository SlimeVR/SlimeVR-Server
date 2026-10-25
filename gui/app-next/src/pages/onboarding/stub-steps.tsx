import { For, Show } from 'solid-js';
import { useFlow, useFlowSession, useStepPages } from '@/domain/flow';
import type { MessageId } from '@/lib/l10n';
import { TextButton } from '@/ui/button';
import { Page } from '@/ui/page';
import { Toolbar } from '@/ui/toolbar';
import { type OnboardingAnswers, onboardingFlow } from './onboarding-flow';

// Placeholders until the real step pages land

export interface StubLink {
  page: string;
  label: MessageId;
}

function StubLinks(props: { links: StubLink[] }) {
  const pages = useStepPages();
  return (
    <Toolbar>
      <For each={props.links}>
        {(link) => <TextButton to={pages.href(link.page)} label={link.label} />}
      </For>
    </Toolbar>
  );
}

function PreviousButton() {
  const flow = useFlow();
  return (
    <Show when={flow?.canPrevious()}>
      <TextButton
        label="onboarding-previous_step"
        onClick={() => flow?.previous()}
      />
    </Show>
  );
}

function ContinueButton() {
  const flow = useFlow();
  return (
    <TextButton
      variant="primary"
      label="onboarding-continue"
      onClick={() => flow?.next()}
    />
  );
}

export function StubStep(props: { title: MessageId; links?: StubLink[] }) {
  return (
    <Page title={props.title}>
      <Show when={props.links}>{(links) => <StubLinks links={links()} />}</Show>
      <Toolbar>
        <PreviousButton />
        <ContinueButton />
      </Toolbar>
    </Page>
  );
}

interface Answer {
  label: MessageId;
  patch: Partial<OnboardingAnswers>;
}

function Question(props: { title: MessageId; answers: Answer[] }) {
  const flow = useFlowSession(onboardingFlow);

  return (
    <Page title={props.title}>
      <Toolbar>
        <PreviousButton />
        <For each={props.answers}>
          {(answer) => (
            <TextButton
              label={answer.label}
              onClick={() => flow.next(answer.patch)}
            />
          )}
        </For>
      </Toolbar>
    </Page>
  );
}

export function SlimeSetStep() {
  return (
    <Question
      title="onboarding-quiz-slimeset-title"
      answers={[
        {
          label: 'onboarding-quiz-slimeset-answer-regular',
          patch: { slimeSet: 'slime-v1' },
        },
        {
          label: 'onboarding-quiz-slimeset-answer-butterfly',
          patch: { slimeSet: 'butterfly' },
        },
        {
          label: 'onboarding-quiz-slimeset-answer-wifi',
          patch: { slimeSet: 'wifi-slime' },
        },
        {
          label: 'onboarding-quiz-slimeset-answer-dongle',
          patch: { slimeSet: 'dongle-slime' },
        },
      ]}
    />
  );
}

export function MoreSetsStep() {
  return (
    <Question
      title="onboarding-quiz-more_sets_modal-title"
      answers={[
        {
          label: 'onboarding-quiz-more_sets_modal-confirm',
          patch: { moreSets: false },
        },
        {
          label: 'onboarding-quiz-more_sets_modal-cancel',
          patch: { moreSets: true },
        },
      ]}
    />
  );
}

export function UsageStep() {
  return (
    <Question
      title="onboarding-quiz-usage-title"
      answers={[
        {
          label: 'onboarding-quiz-usage-answer-VRC',
          patch: { usage: 'vr-gaming' },
        },
        {
          label: 'onboarding-quiz-usage-answer-mocap_vtubing',
          patch: { usage: 'mocap' },
        },
      ]}
    />
  );
}

export function RuntimeStep() {
  return (
    <Question
      title="onboarding-quiz-runtime-title"
      answers={[
        {
          label: 'onboarding-quiz-runtime-answer-steamvr',
          patch: { vrcOsc: false },
        },
        {
          label: 'onboarding-quiz-runtime-answer-standalone',
          patch: { vrcOsc: true },
        },
      ]}
    />
  );
}
