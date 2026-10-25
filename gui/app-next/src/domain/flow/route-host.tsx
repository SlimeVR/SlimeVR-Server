import {
  Navigate,
  type RouteDefinition,
  type RouteSectionProps,
  useNavigate,
} from '@solidjs/router';
import {
  type Component,
  createContext,
  createEffect,
  createSignal,
  Show,
  untrack,
  useContext,
} from 'solid-js';
import { Dynamic } from 'solid-js/web';
import { reportError } from '@/domain/error-reporting';
import { platform } from '@/lib/platform';
import type { Flow, FlowEnv } from './flow';
import { FlowProvider } from './flow-context';
import { createFlowSession } from './session';

// A step page, or named pages inside the step (the first one is its entry)
export type FlowPage = Component | Record<string, Component>;

export interface StepPages {
  // URL of a page of the current step, by name
  href(name: string): string;
}

export interface FlowRouteOptions<A extends object, S extends string> {
  flow: Flow<A, S>;
  // Base path, steps live at `${path}/${step}`
  path: string;
  pages: Record<S, FlowPage>;
  // Fresh answers for every start
  answers(): A;
  // Then navigates to `exit` (default home)
  onEnd(answers: A): void | Promise<void>;
  exit?: string;
}

interface Host {
  step(): string;
  // A step route mounted: re-enters the flow when the URL came from outside
  arrive(step: string): void;
  stepPath(step: string, name?: string): string;
}

const HostContext = createContext<Host>();

function useHost(): Host {
  const host = useContext(HostContext);
  if (!host) throw new Error('flow route outside its host');
  return host;
}

export function useStepPages(): StepPages {
  const host = useHost();
  return { href: (name) => host.stepPath(host.step(), name) };
}

export function flowEnv(): FlowEnv {
  return { android: platform.kind === 'android' };
}

function entryPage(page: FlowPage): string | undefined {
  return typeof page === 'function' ? undefined : Object.keys(page)[0];
}

function RouteFlowHost<A extends object, S extends string>(
  props: RouteSectionProps & { options: FlowRouteOptions<A, S> }
) {
  const options = untrack(() => props.options);
  const { flow, path, pages } = options;
  const navigate = useNavigate();
  const [urlStep, setUrlStep] = createSignal<string>();

  const session = createFlowSession(flow, {
    env: flowEnv(),
    answers: options.answers(),
    // Leaves even when onEnd fails, an ended session ignores Continue
    onEnd: (answers) => {
      void Promise.resolve(options.onEnd(answers))
        .catch((error: unknown) =>
          reportError(error, { tags: { flow: flow.name } })
        )
        .finally(() => navigate(options.exit ?? '/', { replace: true }));
    },
  });

  const host: Host = {
    step: session.step,
    arrive(step) {
      setUrlStep(step);
      if (step !== session.step()) session.enter(step as S);
    },
    stepPath(step, name = entryPage(pages[step as S])) {
      return name ? `${path}/${step}/${name}` : `${path}/${step}`;
    },
  };

  // The flow owns the step, the URL follows it with replace
  createEffect(() => {
    const step = session.step();
    if (urlStep() !== step) navigate(host.stepPath(step), { replace: true });
  });

  return (
    <FlowProvider session={session}>
      <HostContext.Provider value={host}>{props.children}</HostContext.Provider>
    </FlowProvider>
  );
}

// Renders only while the flow is on its step, so a page the flow redirects
// away from never shows
function StepRoute(props: { step: string; page: Component }) {
  const host = useHost();
  untrack(() => host.arrive(props.step));

  return (
    <Show when={host.step() === props.step}>
      <Dynamic component={props.page} />
    </Show>
  );
}

// Bare or unknown page of a step: its entry page, once the flow is on it
function StepEntry(props: { step: string }) {
  const host = useHost();
  untrack(() => host.arrive(props.step));

  return (
    <Show when={host.step() === props.step}>
      <Navigate href={host.stepPath(props.step)} />
    </Show>
  );
}

function CurrentStep() {
  const host = useHost();
  return <Navigate href={host.stepPath(host.step())} />;
}

// Flat routes from the step ids, under one layout route
export function flowRoute<A extends object, S extends string>(
  options: FlowRouteOptions<A, S>
): RouteDefinition {
  const route = (path: string, step: S, page: Component) => ({
    path,
    component: () => <StepRoute step={step} page={page} />,
  });

  return {
    path: options.path,
    component: (props: RouteSectionProps) => (
      <RouteFlowHost {...props} options={options} />
    ),
    children: [
      ...options.flow.steps.flatMap((step): RouteDefinition[] => {
        const page = options.pages[step];
        if (typeof page === 'function') return [route(`/${step}`, step, page)];
        return [
          ...Object.entries(page).map(([name, component]) =>
            route(`/${step}/${name}`, step, component)
          ),
          {
            path: [`/${step}`, `/${step}/*`],
            component: () => <StepEntry step={step} />,
          },
        ];
      }),
      // Bare base path and unknown URLs
      { path: '/*', component: CurrentStep },
    ],
  };
}
