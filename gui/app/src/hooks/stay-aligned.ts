import { useEffect, useState } from 'react';
import {
  CancelStayAlignedPoseRecordingRequestT,
  ChangeStayAlignedSettingsRequestT,
  MountingMethod,
  ResetStayAlignedRelaxedPoseRequestT,
  ResetType,
  RpcMessage,
  StartStayAlignedPoseRecordingRequestT,
  StartStayAlignedStatusRequestT,
  StayAlignedPart,
  StayAlignedPoseCountdownT,
  StayAlignedPoseRecordingDetail,
  StayAlignedPoseRecordingLifecycle,
  StayAlignedPoseRecordingStatusResponseT,
  StayAlignedPoseToggleT,
  StayAlignedRelaxedPose,
  StayAlignedSettingsRequestT,
  StayAlignedSettingsResponseT,
  StayAlignedStatusResponseT,
  StopStayAlignedStatusRequestT,
  TrackingChecklistStepId,
} from 'solarxr-protocol';
import { playCountdownTick, resetSounds, restartAndPlay } from '@/sounds/sounds';
import { useConfig } from './config';
import { useResetsSettings } from './resets-settings';
import { useTrackingChecklist } from './tracking-checklist';
import { useWebsocketAPI } from './websocket-api';

export const POSE_ORDER = [
  StayAlignedRelaxedPose.STANDING,
  StayAlignedRelaxedPose.SITTING,
  StayAlignedRelaxedPose.FLAT,
  // StayAlignedRelaxedPose.KNEELING, // TODO: Do we use it or not?
] as const;

export type RecordablePose = (typeof POSE_ORDER)[number];

export type PoseKey = 'standing' | 'sitting' | 'flat';

export type PrepStep = 'trackers' | 'full-reset' | 'mounting' | 'feet-mounting';
export type RecorderStep = PrepStep | 'record';

export const POSE_KEY: Record<RecordablePose, PoseKey> = {
  [StayAlignedRelaxedPose.STANDING]: 'standing',
  [StayAlignedRelaxedPose.SITTING]: 'sitting',
  [StayAlignedRelaxedPose.FLAT]: 'flat',
};

export const POSE_TITLE: Record<RecordablePose, string> = {
  [StayAlignedRelaxedPose.STANDING]: 'settings-stay_aligned-pose-standing',
  [StayAlignedRelaxedPose.SITTING]: 'settings-stay_aligned-pose-sitting',
  [StayAlignedRelaxedPose.FLAT]: 'settings-stay_aligned-pose-flat',
};

export const POSE_DESCRIPTION: Record<RecordablePose, string> = {
  [StayAlignedRelaxedPose.STANDING]: 'settings-stay_aligned-pose-standing-description',
  [StayAlignedRelaxedPose.SITTING]: 'settings-stay_aligned-pose-sitting-description',
  [StayAlignedRelaxedPose.FLAT]: 'settings-stay_aligned-pose-flat-description',
};

export const POSE_IMAGE: Record<RecordablePose, string> = {
  [StayAlignedRelaxedPose.STANDING]: '/images/stay-aligned/StayAlignedStanding.webp',
  [StayAlignedRelaxedPose.SITTING]: '/images/stay-aligned/StayAlignedSitting.webp',
  [StayAlignedRelaxedPose.FLAT]: '/images/stay-aligned/StayAlignedFloor.webp',
};

export const POSITION_LABEL: Record<StayAlignedRelaxedPose, string> = {
  [StayAlignedRelaxedPose.UNKNOWN]: 'settings-stay_aligned-position-unknown',
  [StayAlignedRelaxedPose.STANDING]: 'settings-stay_aligned-position-standing',
  [StayAlignedRelaxedPose.SITTING]: 'settings-stay_aligned-position-sitting',
  [StayAlignedRelaxedPose.FLAT]: 'settings-stay_aligned-position-flat',
  [StayAlignedRelaxedPose.KNEELING]: 'settings-stay_aligned-position-kneeling',
};

export const PART_LABEL: Record<StayAlignedPart, string> = {
  [StayAlignedPart.UPPER_BODY]: 'settings-stay_aligned-recorder-part-upper_body',
  [StayAlignedPart.LEFT_UPPER_LEG]: 'body_part-LEFT_UPPER_LEG',
  [StayAlignedPart.RIGHT_UPPER_LEG]: 'body_part-RIGHT_UPPER_LEG',
  [StayAlignedPart.LEFT_LOWER_LEG]: 'body_part-LEFT_LOWER_LEG',
  [StayAlignedPart.RIGHT_LOWER_LEG]: 'body_part-RIGHT_LOWER_LEG',
  [StayAlignedPart.LEFT_FOOT]: 'body_part-LEFT_FOOT',
  [StayAlignedPart.RIGHT_FOOT]: 'body_part-RIGHT_FOOT',
};

export const PART_ORDER = [
  StayAlignedPart.UPPER_BODY,
  StayAlignedPart.LEFT_UPPER_LEG,
  StayAlignedPart.RIGHT_UPPER_LEG,
  StayAlignedPart.LEFT_LOWER_LEG,
  StayAlignedPart.RIGHT_LOWER_LEG,
  StayAlignedPart.LEFT_FOOT,
  StayAlignedPart.RIGHT_FOOT,
];

const PREP_ORDER: PrepStep[] = ['trackers', 'full-reset', 'mounting', 'feet-mounting'];

export function useStayAlignedSettings() {
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const [settings, setSettings] = useState<StayAlignedSettingsResponseT | null>(null);

  useRPCPacket(
    RpcMessage.StayAlignedSettingsResponse,
    (response: StayAlignedSettingsResponseT) => setSettings(response)
  );

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.StayAlignedSettingsRequest,
      new StayAlignedSettingsRequestT()
    );
  }, []);

  const change = (override: {
    enabled?: boolean;
    pose?: { pose: StayAlignedRelaxedPose; enabled: boolean };
  }) => {
    if (!settings) return;

    const request = new ChangeStayAlignedSettingsRequestT();
    request.enabled = override.enabled ?? settings.enabled;
    request.poseToggles = settings.poses.map((pose) => {
      const toggle = new StayAlignedPoseToggleT();
      toggle.pose = pose.pose;
      toggle.enabled =
        override.pose?.pose === pose.pose ? override.pose.enabled : pose.enabled;
      return toggle;
    });
    sendRPCPacket(RpcMessage.ChangeStayAlignedSettingsRequest, request);
  };

  const resetPose = (pose: StayAlignedRelaxedPose) => {
    const request = new ResetStayAlignedRelaxedPoseRequestT();
    request.pose = pose;
    sendRPCPacket(RpcMessage.ResetStayAlignedRelaxedPoseRequest, request);
  };

  return {
    settings,
    setEnabled: (enabled: boolean) => change({ enabled }),
    setPoseEnabled: (pose: StayAlignedRelaxedPose, enabled: boolean) =>
      change({ pose: { pose, enabled } }),
    resetPose,
  };
}

export function useStayAlignedStatus() {
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const [status, setStatus] = useState<StayAlignedStatusResponseT | null>(null);

  useRPCPacket(
    RpcMessage.StayAlignedStatusResponse,
    (response: StayAlignedStatusResponseT) => setStatus(response)
  );

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.StartStayAlignedStatusRequest,
      new StartStayAlignedStatusRequestT()
    );
    return () => {
      sendRPCPacket(
        RpcMessage.StopStayAlignedStatusRequest,
        new StopStayAlignedStatusRequestT()
      );
    };
  }, []);

  return status;
}

function playRecordingSounds(
  volume: number,
  {
    lifecycle,
    detailType,
    detail,
  }: Omit<StayAlignedPoseRecordingStatusResponseT, 'pack'>
) {
  const sounds = resetSounds[ResetType.MOUNTING];

  if (lifecycle === StayAlignedPoseRecordingLifecycle.DONE) {
    restartAndPlay(sounds.end, volume);
    restartAndPlay(sounds.mew, volume);
    return;
  }

  if (
    lifecycle !== StayAlignedPoseRecordingLifecycle.RUNNING ||
    detailType !== StayAlignedPoseRecordingDetail.StayAlignedPoseCountdown
  ) {
    return;
  }

  const { progress } = detail as StayAlignedPoseCountdownT;
  if (progress === 0) {
    restartAndPlay(sounds.initial, volume);
  }
  playCountdownTick(sounds.tick, progress, volume);
}

export function useStayAlignedRecording() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { config } = useConfig();
  const [recording, setRecording] =
    useState<StayAlignedPoseRecordingStatusResponseT | null>(null);

  useRPCPacket(
    RpcMessage.StayAlignedPoseRecordingStatusResponse,
    (response: StayAlignedPoseRecordingStatusResponseT) => {
      setRecording(response);
      if (!config?.feedbackSound) return;
      playRecordingSounds(config.feedbackSoundVolume ?? 1, response);
    }
  );

  const start = (pose: StayAlignedRelaxedPose) => {
    setRecording(null);
    const request = new StartStayAlignedPoseRecordingRequestT();
    request.pose = pose;
    sendRPCPacket(RpcMessage.StartStayAlignedPoseRecordingRequest, request);
  };

  const cancel = () => {
    sendRPCPacket(
      RpcMessage.CancelStayAlignedPoseRecordingRequest,
      new CancelStayAlignedPoseRecordingRequestT()
    );
  };

  return { recording, start, cancel, clear: () => setRecording(null) };
}

export function useRecorderSteps(
  recording: boolean,
  status: StayAlignedStatusResponseT | null
) {
  const { steps: checklistSteps } = useTrackingChecklist();
  const { resetsSettings } = useResetsSettings();
  const [redone, setRedone] = useState<PrepStep[]>([]);
  const [seen, setSeen] = useState<PrepStep[]>([]);

  const needsCalibration = (id: TrackingChecklistStepId) => {
    const step = checklistSteps.find((checklistStep) => checklistStep.id === id);
    return !!step && step.enabled && !step.valid;
  };
  const feetSeparate = !!checklistSteps.find(
    (checklistStep) =>
      checklistStep.id === TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION
  )?.enabled;

  const mountingRuns =
    resetsSettings?.mountingMethod === MountingMethod.STEP ||
    resetsSettings?.mountingMethod === MountingMethod.POSE;

  const mountingNeeded =
    mountingRuns &&
    (needsCalibration(TrackingChecklistStepId.MOUNTING_CALIBRATION) ||
      redone.includes('mounting'));
  const feetMountingNeeded =
    feetSeparate &&
    (needsCalibration(TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION) ||
      redone.includes('feet-mounting'));
  const fullResetNeeded =
    needsCalibration(TrackingChecklistStepId.FULL_RESET) ||
    redone.includes('full-reset');

  const isNeeded = (step: PrepStep) => {
    if (step === 'trackers') return (status?.missingParts.length ?? 0) > 0;
    if (step === 'full-reset') return fullResetNeeded;
    if (step === 'mounting') return mountingNeeded;
    return feetMountingNeeded;
  };
  const needed = PREP_ORDER.filter(isNeeded);
  const neededKey = needed.join();

  useEffect(() => {
    setSeen((previous) =>
      needed.every((step) => previous.includes(step))
        ? previous
        : [...previous, ...needed.filter((step) => !previous.includes(step))]
    );
  }, [neededKey]);

  // Once a recording runs it is not interrupted by what is asked
  const current: RecorderStep = recording ? 'record' : (needed[0] ?? 'record');
  const timeline: RecorderStep[] = [
    ...PREP_ORDER.filter((step) => seen.includes(step) || needed.includes(step)),
    'record',
  ];

  return {
    timeline,
    current,
    mountingMethod: resetsSettings?.mountingMethod,
    missingParts: status?.missingParts ?? [],
    redo: () => setRedone(['full-reset', 'mounting', 'feet-mounting']),
    done: (step: PrepStep) =>
      setRedone((previous) => previous.filter((s) => s !== step)),
  };
}
