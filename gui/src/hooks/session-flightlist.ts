import {
  FlightListRequestT,
  FlightListResponseT,
  FlightListStepId,
  FlightListStepT,
  FlightListStepVisibility,
  IgnoreFlightListStepRequestT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { createContext, useContext, useEffect, useMemo, useState } from 'react';

export const flightlistIdtoLabel: Record<FlightListStepId, string> = {
  [FlightListStepId.UNKNOWN]: '',
  [FlightListStepId.TRACKERS_REST_CALIBRATION]: 'flight_list-TRACKERS_REST_CALIBRATION',
  [FlightListStepId.FULL_RESET]: 'flight_list-FULL_RESET',
  [FlightListStepId.VRCHAT_SETTINGS]: 'flight_list-VRCHAT_SETTINGS',
  [FlightListStepId.STEAMVR_DISCONNECTED]: 'flight_list-STEAMVR_DISCONNECTED',
  [FlightListStepId.UNASSIGNED_HMD]: 'flight_list-UNASSIGNED_HMD',
  [FlightListStepId.TRACKER_ERROR]: 'flight_list-TRACKER_ERROR',
  [FlightListStepId.NETWORK_PROFILE_PUBLIC]: 'flight_list-NETWORK_PROFILE_PUBLIC',
  [FlightListStepId.MOUNTING_CALIBRATION]: 'flight_list-MOUNTING_CALIBRATION',
  [FlightListStepId.STAY_ALIGNED_CONFIGURED]: 'flight_list-STAY_ALIGNED_CONFIGURED',
};

export type FlightListStepStatus = 'complete' | 'skipped' | 'blocked' | 'invalid';
export type FlightListStep = FlightListStepT & {
  status: FlightListStepStatus;
  firstRequired: boolean;
};

const stepVisibility = ({ visibility, status, firstRequired }: FlightListStep) =>
  firstRequired ||
  visibility === FlightListStepVisibility.ALWAYS ||
  (visibility === FlightListStepVisibility.WHEN_INVALID && status != 'complete');

const createStep = (
  steps: FlightListStepT[],
  step: FlightListStepT,
  index: number
): FlightListStep => {
  const previousSteps = steps.slice(0, index);
  const previousBlocked = previousSteps.some(
    ({ valid, optional }) => !valid && !optional
  );

  let status: FlightListStepStatus = 'complete';
  if (previousBlocked && !step.valid) status = 'blocked';
  if (!previousBlocked && !step.valid) status = 'invalid';

  const firstRequiredIndex = steps.findIndex(
    (s, index) => !s.valid || (index === steps.length - 1 && !s.valid)
  );

  const skipped =
    steps.find(
      (s, sIndex) =>
        (sIndex > index && s.valid && !s.optional) || sIndex === steps.length - 1
    ) || index === steps.length - 1;
  if (!step.valid && step.optional && skipped) status = 'skipped';

  return {
    ...step,
    status,
    firstRequired:
      firstRequiredIndex === index ||
      (firstRequiredIndex === -1 && index === steps.length - 1 && !step.valid),
    pack: () => 0,
  };
};

export type SessionFlightListContext = ReturnType<typeof provideSessionFlightlist>;
export type Steps = {
  steps: FlightListStepT[];
  visibleSteps: FlightListStep[];
  ignoredSteps: FlightListStepId[];
};
export function provideSessionFlightlist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [steps, setSteps] = useState<Steps>({
    steps: [],
    visibleSteps: [],
    ignoredSteps: [],
  });

  useRPCPacket(RpcMessage.FlightListResponse, (data: FlightListResponseT) => {
    const activeSteps = data.steps.filter(
      (step) => !data.ignoredSteps.includes(step.id) && step.enabled
    );
    setSteps({
      steps: data.steps,
      visibleSteps: activeSteps
        .map((step: FlightListStepT, index) => createStep(activeSteps, step, index))
        .filter(stepVisibility),
      ignoredSteps: data.ignoredSteps,
    });
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.FlightListRequest, new FlightListRequestT());
  }, []);

  const firstRequired = useMemo(
    () =>
      steps.visibleSteps.find(
        (step) => !step.valid && step.status != 'blocked' && !step.optional
      ),
    [steps]
  );

  const hightlightedTrackers = useMemo(() => {
    if (!firstRequired || !firstRequired.extraData) return [];
    if ('trackersId' in firstRequired.extraData) {
      return firstRequired.extraData.trackersId;
    }
    if ('trackerId' in firstRequired.extraData) {
      return [firstRequired.extraData.trackerId];
    }
    return [];
  }, [firstRequired]);

  const progress = useMemo(() => {
    const completeSteps = steps.visibleSteps.filter(
      (step) => step.status === 'complete' || step.status === 'skipped'
    );
    return Math.min(1, completeSteps.length / steps.visibleSteps.length);
  }, [steps]);

  const completion: 'complete' | 'partial' | 'incomplete' = useMemo(() => {
    if (progress === 1 && steps.visibleSteps.find((step) => step.status === 'skipped'))
      return 'partial';
    return progress === 1 || steps.visibleSteps.length === 0
      ? 'complete'
      : 'incomplete';
  }, [progress, steps]);

  const warnings = useMemo(
    () => steps.visibleSteps.filter((step) => !step.valid),
    [steps]
  );

  const ignoreStep = (step: FlightListStepId, ignore: boolean) => {
    const res = new IgnoreFlightListStepRequestT();
    res.stepId = step;
    res.ignore = ignore;
    sendRPCPacket(RpcMessage.IgnoreFlightListStepRequest, res);
  };

  return {
    ...steps,
    firstRequired,
    hightlightedTrackers,
    progress,
    completion,
    warnings,
    ignoreStep,
    toggle: (step: FlightListStepId) =>
      ignoreStep(step, !steps.ignoredSteps.includes(step)),
  };
}

export const FlightListContextC = createContext<SessionFlightListContext>(
  undefined as never
);

export function useSessionFlightlist() {
  const context = useContext(FlightListContextC);
  if (!context) {
    throw new Error('useSessionFlightlist must be within a FlightList Provider');
  }
  return context;
}
