import { type Accessor, createMemo, createSignal, onCleanup, untrack } from 'solid-js';
import { createStore, produce, type SetStoreFunction } from 'solid-js/store';
import { addBreadcrumb, defineMetric, setReportTag } from '@/domain/error-reporting';
import {
  END,
  firstUnreached,
  type Flow,
  type FlowEnv,
  isReachable,
  predecessor,
  progress,
} from './flow';

const stepReached = defineMetric<{ flow: string; step: string }>('flow.step_reached', {
  once: 'session',
});

export interface FlowSessionOptions<A> {
  env: FlowEnv;
  answers: A;
  onEnd(answers: A): void;
}

export interface FlowSession<A extends object, S extends string> {
  flow: Flow<A, S>;
  env: FlowEnv;
  step: Accessor<S>;
  trail: Accessor<readonly S[]>;
  answers: A;
  setAnswers: SetStoreFunction<A>;
  canPrevious: Accessor<boolean>;
  progress: Accessor<number>;
  // Merges `patch` into the answers first (answer cards)
  next(patch?: Partial<A>): void;
  previous(): boolean;
  // Reachable step (clears the trail), else the first unreached one
  enter(step: S): void;
}

export function createFlowSession<A extends object, S extends string>(
  flow: Flow<A, S>,
  options: FlowSessionOptions<A>
): FlowSession<A, S> {
  const { env } = options;
  const [answers, setAnswers] = createStore<A>(options.answers);
  const [step, setStep] = createSignal<S>(flow.first);
  const [trail, setTrail] = createSignal<readonly S[]>([]);
  let ended = false;

  function show(next: S) {
    const from = step();
    setStep(() => next);
    setReportTag('step', next);
    stepReached({ flow: flow.name, step: next });
    if (from !== next) {
      addBreadcrumb({
        category: 'flow',
        message: `${flow.name}: ${from} -> ${next}`,
      });
    }
  }

  function enter(target: S) {
    const reachable = isReachable(flow, target, answers, env);
    if (reachable) setTrail([]);
    show(reachable ? target : firstUnreached(flow, trail(), answers, env));
  }

  function next(patch?: Partial<A>) {
    if (ended) return;
    if (patch) setAnswers(produce((draft) => Object.assign(draft, patch)));
    const current = step();
    const target = flow.next(current, answers, env);
    if (target === END) {
      ended = true;
      addBreadcrumb({
        category: 'flow',
        message: `${flow.name}: ${current} -> end`,
      });
      options.onEnd(answers);
      return;
    }
    setTrail((list) => [...list, current]);
    show(target);
  }

  function previous(): boolean {
    const list = trail();
    const last = list.at(-1);
    if (last !== undefined) {
      setTrail(list.slice(0, -1));
      show(last);
      return true;
    }
    const before = predecessor(flow, step(), answers, env);
    if (before === undefined) return false;
    show(before);
    return true;
  }

  setReportTag('flow', flow.name);
  onCleanup(() => {
    setReportTag('flow', undefined);
    setReportTag('step', undefined);
  });
  untrack(() => show(flow.first));
  const progressOf = createMemo(() => progress(flow, trail(), step(), answers, env));

  return {
    flow,
    env,
    step,
    trail,
    answers,
    setAnswers,
    canPrevious: () =>
      trail().length > 0 || predecessor(flow, step(), answers, env) !== undefined,
    progress: progressOf,
    next,
    previous,
    enter,
  };
}
