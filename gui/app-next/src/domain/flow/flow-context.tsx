import { createContext, type JSX, Show, untrack, useContext } from 'solid-js';
import { useBack } from '@/lib/back-stack';
import type { Flow } from './flow';
import type { FlowSession } from './session';

export interface FlowControls {
  next(): void;
  previous(): boolean;
  canPrevious(): boolean;
}

type AnySession = FlowSession<object, string>;

const SessionContext = createContext<AnySession>();

// Pages render their own controls from it, undefined outside a flow
export function useFlow(): FlowControls | undefined {
  return useContext(SessionContext);
}

// Typed session for flow-only pages (questions), checked against the host's flow
export function useFlowSession<A extends object, S extends string>(
  flow: Flow<A, S>
): FlowSession<A, S> {
  const session = useContext(SessionContext);
  if (session?.flow !== flow) {
    throw new Error(`useFlowSession(${flow.name}) outside its flow host`);
  }
  return session as unknown as FlowSession<A, S>;
}

// Extras kept out of the first setup (e.g. reset assignments)
export function OutsideSetup(props: { children: JSX.Element }) {
  const flow = useFlow();
  return <Show when={!flow}>{props.children}</Show>;
}

// Shared by both hosts: session context, and Escape/pad B/Android back go to
// the previous step (falls through on the first one)
export function FlowProvider<A extends object, S extends string>(props: {
  session: FlowSession<A, S>;
  children: JSX.Element;
}) {
  const session = untrack(() => props.session);
  useBack(() => session.previous(), { when: () => session.canPrevious() });

  return (
    <SessionContext.Provider value={session as unknown as AnySession}>
      {props.children}
    </SessionContext.Provider>
  );
}
