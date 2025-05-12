import {
  FlightListRequestT,
  FlightListResponseT,
  FlightListStepChangeResponseT,
  FlightListStepId,
  FlightListStepT,
  FlightListStepVisibility,
  RpcMessage,
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
};

export type FlightListStepStatus = 'complete' | 'skipped' | 'blocked' | 'invalid';
export type FlightListStep = FlightListStepT & { status: FlightListStepStatus };

export function useSessionFlightlist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [steps, setSteps] = useState<FlightListStep[]>([]);
  const [ignoredSteps, setIgnoredSteps] = useState<FlightListStepId[]>([]);

  const createStep = (
    steps: FlightListStepT[],
    step: FlightListStepT,
    index: number
  ) => {
    const previousSteps = steps.slice(0, index);
    const blocked = previousSteps.some(({ valid, optional }) => !valid && !optional);

    let status = 'complete';
    if (blocked) status = 'blocked';
    if (!blocked && !step.valid) status = 'invalid';
    if (!blocked && step.optional && !step.valid && index !== previousSteps.length)
      status = 'skipped';

    return { ...step, status } as FlightListStep;
  };

  useRPCPacket(RpcMessage.FlightListResponse, (data: FlightListResponseT) => {
    setIgnoredSteps(data.ignoredSteps);
    const visibleSteps = data.steps.filter(
      (step) => !data.ignoredSteps.includes(step.id)
    );
    const steps = visibleSteps
      .map((step: FlightListStepT, index) => createStep(visibleSteps, step, index))
      .filter(
        ({ visibility, status }) =>
          visibility == FlightListStepVisibility.ALWAYS ||
          (visibility === FlightListStepVisibility.WHEN_INVALID &&
            ['invalid', 'skipped'].includes(status))
      );

    setSteps(steps);

    console.log(steps);
  });

  useRPCPacket(
    RpcMessage.FlightListStepChangeResponse,
    (data: FlightListStepChangeResponseT) => {
      const step = data.step;
      if (!step) throw 'invalid state - step should be set';
      console.log('Update', step);
      setSteps((steps) => {
        const visibleSteps = steps.filter((step) => !ignoredSteps.includes(step.id));
        const newsteps = visibleSteps
          .map((step: FlightListStepT, index) => createStep(visibleSteps, step, index))
          .filter(
            ({ visibility, status }) =>
              visibility == FlightListStepVisibility.ALWAYS ||
              (visibility === FlightListStepVisibility.WHEN_INVALID &&
                ['invalid', 'blocked', 'skipped'].includes(status))
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
    steps,
    ignoredSteps,
  };
}
