export const END = Symbol('flow end');
export type FlowEnd = typeof END;

export interface FlowEnv {
  android: boolean;
}

export interface FlowStepDef<A, S extends string> {
  next?: (answers: A, env: FlowEnv) => S | FlowEnd;
}

export interface Flow<A, S extends string> {
  name: string;
  steps: readonly S[];
  first: S;
  next(step: S, answers: A, env: FlowEnv): S | FlowEnd;
}

export function defineFlow<A, S extends string>(
  name: string,
  defs: Record<S, FlowStepDef<A, S>>
): Flow<A, S> {
  const steps = Object.keys(defs) as S[];
  const first = steps[0];
  if (first === undefined) throw new Error(`flow ${name} has no steps`);

  return {
    name,
    steps,
    first,
    next(step, answers, env) {
      const next = defs[step].next;
      if (next) return next(answers, env);
      return steps[steps.indexOf(step) + 1] ?? END;
    },
  };
}

export function walk<A, S extends string>(
  flow: Flow<A, S>,
  from: S,
  answers: A,
  env: FlowEnv
): S[] {
  const path: S[] = [];
  const seen = new Set<S>();
  let step: S | FlowEnd = from;
  while (step !== END && !seen.has(step)) {
    seen.add(step);
    path.push(step);
    step = flow.next(step, answers, env);
  }
  return path;
}

export function isReachable<A, S extends string>(
  flow: Flow<A, S>,
  step: S,
  answers: A,
  env: FlowEnv
): boolean {
  return walk(flow, flow.first, answers, env).includes(step);
}

export function predecessor<A, S extends string>(
  flow: Flow<A, S>,
  step: S,
  answers: A,
  env: FlowEnv
): S | undefined {
  const path = walk(flow, flow.first, answers, env);
  const index = path.indexOf(step);
  return index > 0 ? path[index - 1] : undefined;
}

export function firstUnreached<A, S extends string>(
  flow: Flow<A, S>,
  trail: readonly S[],
  answers: A,
  env: FlowEnv
): S {
  const path = walk(flow, flow.first, answers, env);
  return path.find((step) => !trail.includes(step)) ?? flow.first;
}

export function progress<A, S extends string>(
  flow: Flow<A, S>,
  trail: readonly S[],
  step: S,
  answers: A,
  env: FlowEnv
): number {
  const done = trail.length;
  return done / (done + walk(flow, step, answers, env).length);
}
