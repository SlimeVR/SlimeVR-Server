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
  [FlightListStepId.TRACKERS_CALIBRATION]: 'flight_list-TRACKERS_CALIBRATION',
  [FlightListStepId.FULL_RESET]: 'flight_list-FULL_RESET',
  [FlightListStepId.VRCHAT_SETTINGS]: 'flight_list-VRCHAT_SETTINGS',
  [FlightListStepId.STEAMVR_DISCONNECTED]: 'flight_list-STEAMVR_DISCONNECTED',
  [FlightListStepId.UNASSIGNED_HMD]: 'flight_list-UNASSIGNED_HMD',
  [FlightListStepId.TRACKER_ERROR]: 'flight_list-TRACKER_ERROR',
  [FlightListStepId.NETWORK_PROFILE_PUBLIC]: 'flight_list-NETWORK_PROFILE_PUBLIC',
  [FlightListStepId.MOUNTING_CALIBRATION]: 'flight_list-MOUNTING_CALIBRATION',
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
  if (blocked && !step.valid) status = 'blocked';
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

export type SessionFlightListContext = ReturnType<typeof provideSessionFlightlist>;

const stepVisibility = ({ visibility, status, firstInvalid }: FlightListStep) =>
  firstInvalid ||
  visibility === FlightListStepVisibility.ALWAYS ||
  (visibility === FlightListStepVisibility.WHEN_INVALID &&
    ['invalid', 'blocked'].includes(status));

export function provideSessionFlightlist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [steps, setSteps] = useState<FlightListStep[]>([]);
  const [ignoredSteps, setIgnoredSteps] = useState<FlightListStepId[]>([]);

  useRPCPacket(RpcMessage.FlightListResponse, (data: FlightListResponseT) => {
    setIgnoredSteps(data.ignoredSteps);
    const activeSteps = data.steps.filter(
      (step) => !data.ignoredSteps.includes(step.id) && step.enabled
    );
    const steps = activeSteps.map((step: FlightListStepT, index) =>
      createStep(activeSteps, step, index)
    );
    setSteps(steps);
  });

  useEffect(() => {
    sendRPCPacket(RpcMessage.FlightListRequest, new FlightListRequestT());
  }, []);

  const visibleSteps = useMemo(() => steps.filter(stepVisibility), [steps]);
  const firstInvalid = useMemo(
    () =>
      visibleSteps.find(
        (step) => !step.valid && step.status != 'blocked' && !step.optional
      ),
    [visibleSteps]
  );

  const hightlightedTrackers = useMemo(() => {
    if (!firstInvalid || !firstInvalid.extraData) return [];
    if ('trackersId' in firstInvalid.extraData) {
      return firstInvalid.extraData.trackersId;
    }
    if ('trackerId' in firstInvalid.extraData) {
      return [firstInvalid.extraData.trackerId];
    }
    return [];
  }, [firstInvalid]);

  return {
    steps: steps.filter(stepVisibility),
    firstInvalid,
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
