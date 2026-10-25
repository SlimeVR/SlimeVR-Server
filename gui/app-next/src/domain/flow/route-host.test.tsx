import { A, createMemoryHistory, MemoryRouter } from '@solidjs/router';
import { cleanup, fireEvent, render, screen } from '@solidjs/testing-library';
import { type Component, Show } from 'solid-js';
import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { handleBack } from '@/lib/back-stack';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { OutsideSetup, useFlow, useFlowSession } from './flow-context';
import { flowRoute, type FlowPage, useStepPages } from './route-host';
import { onboardingFlow, type OnboardingAnswers, type StepId } from './testing';

beforeAll(() => setPlatform(fakePlatform({ kind: 'desktop' })));
afterEach(cleanup);

// Pages render their own controls, nothing outside a flow
function Nav() {
  const flow = useFlow();
  return (
    <Show when={flow}>
      {(f) => (
        <>
          <Show when={f().canPrevious()}>
            <button type="button" onClick={() => f().previous()}>
              previous
            </button>
          </Show>
          <button type="button" onClick={() => f().next()}>
            continue
          </button>
        </>
      )}
    </Show>
  );
}

function plain(text: string): Component {
  return () => (
    <>
      <p>{text}</p>
      <Nav />
    </>
  );
}

function linkTo(page: string, text: string): Component {
  return () => (
    <>
      <A href={useStepPages().href(page)}>{text}</A>
      <Nav />
    </>
  );
}

function SlimeSetPage() {
  const flow = useFlowSession(onboardingFlow);
  return (
    <button type="button" onClick={() => flow.next({ slimeSet: 'butterfly' })}>
      pick butterfly
    </button>
  );
}

function AssignPage() {
  return (
    <>
      <p>assign page</p>
      <Nav />
      <OutsideSetup>
        <p>reset assignments</p>
      </OutsideSetup>
    </>
  );
}

const pages: Record<StepId, FlowPage> = {
  welcome: plain('welcome page'),
  'slime-set': SlimeSetPage,
  dongle: plain('dongle page'),
  connect: plain('connect page'),
  'more-sets': plain('more-sets page'),
  assign: AssignPage,
  mounting: {
    choose: linkTo('auto', 'mounting choose'),
    auto: linkTo('choose', 'mounting auto'),
  },
  proportions: plain('proportions page'),
  usage: plain('usage page'),
  runtime: plain('runtime page'),
  'mocap-pos': plain('mocap-pos page'),
};

function renderAt(path: string) {
  const onEnd = vi.fn<(answers: OnboardingAnswers) => void>();
  const history = createMemoryHistory();
  history.set({ value: path });
  const routes = [
    flowRoute({
      flow: onboardingFlow,
      path: '/onboarding',
      pages,
      answers: () => ({}),
      onEnd,
    }),
    { path: '/', component: () => <p>home page</p> },
    { path: '/assign', component: AssignPage },
  ];
  render(() => <MemoryRouter history={history}>{routes}</MemoryRouter>);
  return { history, onEnd };
}

function continueButton() {
  return screen.queryByRole('button', { name: 'continue' });
}

function previousButton() {
  return screen.queryByRole('button', { name: 'previous' });
}

describe('flowRoute', () => {
  it('starts at the first step and keeps the URL on the flow step', async () => {
    const { history } = renderAt('/onboarding');
    expect(await screen.findByText('welcome page')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/welcome');
    expect(previousButton()).toBeNull();

    fireEvent.click(continueButton()!);
    expect(await screen.findByText('pick butterfly')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/slime-set');
  });

  it('lets the page move on with an answer', async () => {
    renderAt('/onboarding/slime-set');
    fireEvent.click(await screen.findByText('pick butterfly'));
    expect(await screen.findByText('dongle page')).toBeInTheDocument();
  });

  it('goes back through page controls and the back stack', async () => {
    const { history } = renderAt('/onboarding/slime-set');
    fireEvent.click(await screen.findByText('pick butterfly'));
    await screen.findByText('dongle page');
    fireEvent.click(previousButton()!);
    expect(await screen.findByText('pick butterfly')).toBeInTheDocument();

    expect(handleBack()).toBe(true);
    expect(await screen.findByText('welcome page')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/welcome');
    // First step: back falls through
    expect(handleBack()).toBe(false);
  });

  it('keeps in-page sub-routes inside the step', async () => {
    const { history } = renderAt('/onboarding/assign');
    await screen.findByText('assign page');
    fireEvent.click(continueButton()!);

    expect(await screen.findByText('mounting choose')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/mounting/choose');
    fireEvent.click(screen.getByText('mounting choose'));
    expect(await screen.findByText('mounting auto')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/mounting/auto');
    fireEvent.click(screen.getByText('mounting auto'));
    expect(await screen.findByText('mounting choose')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/mounting/choose');

    fireEvent.click(screen.getByText('mounting choose'));
    await screen.findByText('mounting auto');
    fireEvent.click(continueButton()!);
    expect(await screen.findByText('proportions page')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/proportions');
  });

  it('sends a bare or unknown step page to its entry page', async () => {
    const { history } = renderAt('/onboarding/mounting');
    expect(await screen.findByText('mounting choose')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/mounting/choose');
    cleanup();

    const unknown = renderAt('/onboarding/mounting/nope');
    expect(await screen.findByText('mounting choose')).toBeInTheDocument();
    expect(unknown.history.get()).toBe('/onboarding/mounting/choose');
  });

  it('redirects an unreachable deep link to the first unreached step', async () => {
    const { history } = renderAt('/onboarding/runtime');
    expect(await screen.findByText('welcome page')).toBeInTheDocument();
    expect(history.get()).toBe('/onboarding/welcome');
  });

  it('hides OutsideSetup extras inside the flow', async () => {
    renderAt('/onboarding/assign');
    expect(await screen.findByText('assign page')).toBeInTheDocument();
    expect(screen.queryByText('reset assignments')).toBeNull();
  });

  it('shows OutsideSetup extras on the standalone page', async () => {
    renderAt('/assign');
    expect(await screen.findByText('reset assignments')).toBeInTheDocument();
    expect(continueButton()).toBeNull();
  });

  it('runs onEnd then leaves to home', async () => {
    const { history, onEnd } = renderAt('/onboarding/mocap-pos');
    await screen.findByText('mocap-pos page');
    fireEvent.click(continueButton()!);
    expect(await screen.findByText('home page')).toBeInTheDocument();
    expect(onEnd).toHaveBeenCalledWith({});
    expect(history.get()).toBe('/');
  });
});
