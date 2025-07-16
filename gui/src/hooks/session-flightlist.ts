import {
  FlightListRequestT,
  FlightListResponseT,
  FlightListStepChangeResponseT,
  FlightListStepId,
  FlightListStepT,
  FlightListStepVisibility,
  RpcMessage,
  ToggleFlightListStepRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useEffect, useState } from 'react';

export const flightlistIdtoLabel: Record<FlightListStepId, string> = {
  [FlightListStepId.UNKNOWN]: '',
  [FlightListStepId.TRACKERS_CALIBRATION]: 'flight_list-TRACKERS_CALIBRATION',
  [FlightListStepId.FULL_RESET]: 'flight_list-FULL_RESET',
  [FlightListStepId.VRCHAT_SETTINGS]: 'flight_list-VRCHAT_SETTINGS',
  [FlightListStepId.STEAMVR_DISCONNECTED]: 'flight_list-STEAMVR_DISCONNECTED',
  [FlightListStepId.UNASSIGNED_HMD]: 'flight_list-UNASSIGNED_HMD',
  [FlightListStepId.TRACKER_ERROR]: 'flight_list-TRACKER_ERROR',
  [FlightListStepId.NETWORK_PROFILE_PUBLIC]: 'flight_list-NETWORK_PROFILE_PUBLIC',
};

export type FlightListStepStatus = 'complete' | 'skipped' | 'blocked' | 'invalid';
export type FlightListStep = FlightListStepT & {
  status: FlightListStepStatus;
  firstInvalid: boolean;
};

const createStep = (
  steps: FlightListStepT[],
  step: FlightListStepT,
  index: number
): FlightListStep => {
  const previousSteps = steps.slice(0, index);
  const blocked = previousSteps.some(({ valid, optional }) => !valid && !optional);

  let status: FlightListStepStatus = 'complete';
  if (blocked) status = 'blocked';
  if (!blocked && !step.valid) status = 'invalid';
  if (!blocked && step.optional && !step.valid && index !== previousSteps.length)
    status = 'skipped';

  return {
    ...step,
    status,
    firstInvalid: steps.findIndex((s) => !s.valid) == index,
    pack: () => 0,
  };
};

export type SessionFlightListContext = ReturnType<typeof useSessionFlightlist>;

const stepVisibility = ({ visibility, status, firstInvalid }: FlightListStep) =>
  firstInvalid ||
  visibility == FlightListStepVisibility.ALWAYS ||
  (visibility === FlightListStepVisibility.WHEN_INVALID &&
    ['invalid', 'blocked', 'skipped'].includes(status));

export function useSessionFlightlist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [steps, setSteps] = useState<FlightListStep[]>([]);
  const [ignoredSteps, setIgnoredSteps] = useState<FlightListStepId[]>([]);

  useRPCPacket(RpcMessage.FlightListResponse, (data: FlightListResponseT) => {
    setIgnoredSteps(data.ignoredSteps);
    const visibleSteps = data.steps.filter(
      (step) => !data.ignoredSteps.includes(step.id)
    );
    const steps = data.steps.map((step: FlightListStepT, index) =>
      createStep(visibleSteps, step, index)
    );
    setSteps(steps);
  });

  useRPCPacket(
    RpcMessage.FlightListStepChangeResponse,
    (data: FlightListStepChangeResponseT) => {
      const step = data.step;
      console.log('STATE CHANGE', step);
      if (!step) throw 'invalid state - step should be set';
      setSteps((steps) => {
        const visibleSteps = steps.filter((step) => !ignoredSteps.includes(step.id));
        const newsteps = steps.map((step: FlightListStepT, index) =>
          createStep(visibleSteps, step, index)
        );
        const stepIndex = newsteps.findIndex(({ id }) => step.id === id);
        if (stepIndex == -1) return newsteps; // skip the step because it is not visible
        newsteps[stepIndex] = createStep(newsteps, step, stepIndex);
        return newsteps;
      });
    }
  );

  useEffect(() => {
    sendRPCPacket(RpcMessage.FlightListRequest, new FlightListRequestT());
  }, []);

  return {
    steps: steps
      .filter((step) => !ignoredSteps.includes(step.id))
      .filter(stepVisibility),
    ignoredSteps,
    toggle: (step: FlightListStepId) => {
      const res = new ToggleFlightListStepRequestT();
      res.stepId = step;
      sendRPCPacket(RpcMessage.ToggleFlightListStepRequest, res);
    },
  };
}
