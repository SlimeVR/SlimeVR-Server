import { type Component, untrack } from 'solid-js';
import { Dynamic } from 'solid-js/web';
import type { Flow } from './flow';
import { FlowProvider } from './flow-context';
import { flowEnv } from './route-host';
import { createFlowSession } from './session';

export interface FlowStepperProps<A extends object, S extends string> {
  flow: Flow<A, S>;
  pages: Record<S, Component>;
  answers: A;
  onEnd(answers: A): void;
}

export function FlowStepper<A extends object, S extends string>(
  props: FlowStepperProps<A, S>
) {
  const session = untrack(() =>
    createFlowSession(props.flow, {
      env: flowEnv(),
      answers: props.answers,
      onEnd: (answers) => props.onEnd(answers),
    })
  );

  const page = (): Component => props.pages[session.step()];

  return (
    <FlowProvider session={session}>
      <Dynamic component={page()} />
    </FlowProvider>
  );
}
