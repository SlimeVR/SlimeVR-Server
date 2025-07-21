import {
  FlightListRequestT,
  FlightListResponseT,
  FlightListStepId,
  FlightListStepT,
  FlightListStepVisibility,
  RpcMessage,
  ToggleFlightListStepRequestT,
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

export function provideSessionFlightlist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [steps, setSteps] = useState<FlightListStepT[]>([]);
  const [ignoredSteps, setIgnoredSteps] = useState<FlightListStepId[]>([]);

  useRPCPacket(RpcMessage.FlightListResponse, (data: FlightListResponseT) => {
    setIgnoredSteps(data.ignoredSteps);
    setSteps(data.steps);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.FlightListRequest, new FlightListRequestT());
  }, []);

  const visibleSteps = useMemo(() => {
    const activeSteps = steps.filter(
      (step) => !ignoredSteps.includes(step.id) && step.enabled
    );
    return steps
      .map((step: FlightListStepT, index) => createStep(activeSteps, step, index))
      .filter(stepVisibility);
  }, [steps, ignoredSteps]);

  const firstRequired = useMemo(
    () =>
      visibleSteps.find(
        (step) => !step.valid && step.status != 'blocked' && !step.optional
      ),
    [visibleSteps]
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

  return {
    steps,
    visibleSteps,
    firstRequired,
    ignoredSteps,
    hightlightedTrackers,
    toggle: (step: FlightListStepId) => {
      const res = new ToggleFlightListStepRequestT();
      res.stepId = step;
      sendRPCPacket(RpcMessage.ToggleFlightListStepRequest, res);
    },
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
