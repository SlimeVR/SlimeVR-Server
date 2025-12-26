import {
  TrackingChecklistRequestT,
  TrackingChecklistResponseT,
  TrackingChecklistStepId,
  TrackingChecklistStepT,
  TrackingChecklistStepVisibility,
  IgnoreTrackingChecklistStepRequestT,
  RpcMessage,
  TrackerIdT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import * as Sentry from '@sentry/react';

export const trackingchecklistIdtoLabel: Record<TrackingChecklistStepId, string> = {
  [TrackingChecklistStepId.UNKNOWN]: '',
  [TrackingChecklistStepId.TRACKERS_REST_CALIBRATION]:
    'tracking_checklist-TRACKERS_REST_CALIBRATION',
  [TrackingChecklistStepId.FULL_RESET]: 'tracking_checklist-FULL_RESET',
  [TrackingChecklistStepId.VRCHAT_SETTINGS]: 'tracking_checklist-VRCHAT_SETTINGS',
  [TrackingChecklistStepId.STEAMVR_DISCONNECTED]:
    'tracking_checklist-STEAMVR_DISCONNECTED',
  [TrackingChecklistStepId.UNASSIGNED_HMD]: 'tracking_checklist-UNASSIGNED_HMD',
  [TrackingChecklistStepId.TRACKER_ERROR]: 'tracking_checklist-TRACKER_ERROR',
  [TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC]:
    'tracking_checklist-NETWORK_PROFILE_PUBLIC',
  [TrackingChecklistStepId.MOUNTING_CALIBRATION]:
    'tracking_checklist-MOUNTING_CALIBRATION',
  [TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION]:
    'tracking_checklist-FEET_MOUNTING_CALIBRATION',
  [TrackingChecklistStepId.STAY_ALIGNED_CONFIGURED]:
    'tracking_checklist-STAY_ALIGNED_CONFIGURED',
};

export type TrackingChecklistStepStatus =
  | 'complete'
  | 'skipped'
  | 'blocked'
  | 'invalid';
export type TrackingChecklistStep = TrackingChecklistStepT & {
  status: TrackingChecklistStepStatus;
  firstRequired: boolean;
};
export type highlightedTrackers = {
  step: TrackingChecklistStep;
  trackers: Array<TrackerIdT>;
};

const stepVisibility = ({ visibility, status, firstRequired }: TrackingChecklistStep) =>
  firstRequired ||
  visibility === TrackingChecklistStepVisibility.ALWAYS ||
  (visibility === TrackingChecklistStepVisibility.WHEN_INVALID && status != 'complete');

const createStep = (
  steps: TrackingChecklistStepT[],
  step: TrackingChecklistStepT,
  index: number
): TrackingChecklistStep => {
  const previousSteps = steps.slice(0, index);
  const previousBlocked = previousSteps.some(
    ({ valid, optional }) => !valid && !optional
  );

  let status: TrackingChecklistStepStatus = 'complete';
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

export type TrackingChecklistContext = ReturnType<typeof provideTrackingChecklist>;
export type Steps = {
  steps: TrackingChecklistStepT[];
  visibleSteps: TrackingChecklistStep[];
  ignoredSteps: TrackingChecklistStepId[];
};

const filterActive =
  (ignoredSteps: TrackingChecklistStepId[]) => (step: TrackingChecklistStepT) =>
    !ignoredSteps.includes(step.id) && step.enabled;

export function provideTrackingChecklist() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [sessionIgnoredSteps, setSessionIgnoredSteps] = useState<
    TrackingChecklistStepId[]
  >([]);
  const [steps, setSteps] = useState<Steps>({
    steps: [],
    visibleSteps: [],
    ignoredSteps: [],
  });

  useRPCPacket(
    RpcMessage.TrackingChecklistResponse,
    (data: TrackingChecklistResponseT) => {
      const activeSteps = data.steps.filter(
        filterActive([...data.ignoredSteps, ...sessionIgnoredSteps])
      );
      setSteps({
        steps: data.steps,
        visibleSteps: activeSteps
          .map((step: TrackingChecklistStepT, index) =>
            createStep(activeSteps, step, index)
          )
          .filter(stepVisibility),
        ignoredSteps: data.ignoredSteps,
      });
    }
  );

  useEffect(() => {
    sendRPCPacket(RpcMessage.TrackingChecklistRequest, new TrackingChecklistRequestT());
  }, []);

  const firstRequired = useMemo(
    () =>
      steps.visibleSteps.find(
        (step) => !step.valid && step.status != 'blocked' && !step.optional
      ),
    [steps]
  );

  const highlightedTrackers: highlightedTrackers | undefined = useMemo(() => {
    if (!firstRequired || !firstRequired.extraData) return undefined;
    if ('trackersId' in firstRequired.extraData) {
      return { step: firstRequired, trackers: firstRequired.extraData.trackersId };
    }
    if ('trackerId' in firstRequired.extraData && firstRequired.extraData.trackerId) {
      return { step: firstRequired, trackers: [firstRequired.extraData.trackerId] };
    }
    return { step: firstRequired, trackers: [] };
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

  const ignoreStep = (
    step: TrackingChecklistStepId,
    ignore: boolean,
    session = true
  ) => {
    setSessionIgnoredSteps((curr) => {
      if (ignore && !curr.includes(step)) return [...curr, step];
      if (!ignore && curr.includes(step)) {
        curr.splice(curr.indexOf(step), 1);
        return curr;
      }
      return curr;
    });
    Sentry.metrics.count(ignore ? 'mute_checklist_step' : 'unmute_checklist_step', 1, {
      attributes: { step: TrackingChecklistStepId[step], session },
    });
    if (session) {
      // Force refresh of the flightlist when ignoring a step as the filtering
      // is done only in one place to simplify the data flow
      sendRPCPacket(
        RpcMessage.TrackingChecklistRequest,
        new TrackingChecklistRequestT()
      );
    } else {
      const res = new IgnoreTrackingChecklistStepRequestT();
      res.stepId = step;
      res.ignore = ignore;
      sendRPCPacket(RpcMessage.IgnoreTrackingChecklistStepRequest, res);
    }
  };

  return {
    ...steps,
    sessionIgnoredSteps,
    firstRequired,
    highlightedTrackers,
    progress,
    completion,
    warnings,
    ignoreStep,
    toggleSession: (step: TrackingChecklistStepId) =>
      ignoreStep(step, !sessionIgnoredSteps.includes(step)),
  };
}

export const TrackingChecklistContectC = createContext<TrackingChecklistContext>(
  undefined as never
);

export function useTrackingChecklist() {
  const context = useContext(TrackingChecklistContectC);
  if (!context) {
    throw new Error('useTrackingChecklist must be within a TrackingChecklistProvider');
  }
  return context;
}
