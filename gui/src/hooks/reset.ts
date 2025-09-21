import { playSoundOnResetEnded, playSoundOnResetStarted } from '@/sounds/sounds';
import { useEffect, useMemo, useRef, useState } from 'react';
import {
  BodyPart,
  ResetRequestT,
  ResetResponseT,
  ResetStatus,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { useConfig } from './config';
import { useWebsocketAPI } from './websocket-api';
import { useCountdown } from './countdown';
import { useAtomValue } from 'jotai';
import { assignedTrackersAtom } from '@/store/app-store';

export type ResetBtnStatus = 'idle' | 'counting' | 'finished';

export type MountingResetGroup = 'default' | 'feet' | 'fingers';
export type UseResetOptions =
  | { type: ResetType.Full | ResetType.Yaw }
  | { type: ResetType.Mounting; group: MountingResetGroup };

const feetBodyParts = [BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT];
const fingerBodyParts = [
  BodyPart.LEFT_THUMB_METACARPAL,
  BodyPart.LEFT_THUMB_PROXIMAL,
  BodyPart.LEFT_THUMB_DISTAL,
  BodyPart.LEFT_INDEX_PROXIMAL,
  BodyPart.LEFT_INDEX_INTERMEDIATE,
  BodyPart.LEFT_INDEX_DISTAL,
  BodyPart.LEFT_MIDDLE_PROXIMAL,
  BodyPart.LEFT_MIDDLE_INTERMEDIATE,
  BodyPart.LEFT_MIDDLE_DISTAL,
  BodyPart.LEFT_RING_PROXIMAL,
  BodyPart.LEFT_RING_INTERMEDIATE,
  BodyPart.LEFT_RING_DISTAL,
  BodyPart.LEFT_LITTLE_PROXIMAL,
  BodyPart.LEFT_LITTLE_INTERMEDIATE,
  BodyPart.LEFT_LITTLE_DISTAL,
  BodyPart.RIGHT_THUMB_METACARPAL,
  BodyPart.RIGHT_THUMB_PROXIMAL,
  BodyPart.RIGHT_THUMB_DISTAL,
  BodyPart.RIGHT_INDEX_PROXIMAL,
  BodyPart.RIGHT_INDEX_INTERMEDIATE,
  BodyPart.RIGHT_INDEX_DISTAL,
  BodyPart.RIGHT_MIDDLE_PROXIMAL,
  BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
  BodyPart.RIGHT_MIDDLE_DISTAL,
  BodyPart.RIGHT_RING_PROXIMAL,
  BodyPart.RIGHT_RING_INTERMEDIATE,
  BodyPart.RIGHT_RING_DISTAL,
  BodyPart.RIGHT_LITTLE_PROXIMAL,
  BodyPart.RIGHT_LITTLE_INTERMEDIATE,
  BodyPart.RIGHT_LITTLE_DISTAL,
];

export const BODY_PARTS_GROUPS: Record<MountingResetGroup, BodyPart[]> = {
  default: [],
  feet: feetBodyParts,
  fingers: fingerBodyParts,
};

export function useReset(options: UseResetOptions, onReseted?: () => void) {
  if (options.type === ResetType.Mounting && !options.group) options.group = 'default';

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { config } = useConfig();
  const finishedTimeoutRef = useRef(-1);
  const [status, setStatus] = useState<ResetBtnStatus>('idle');

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = options.type;
    req.bodyParts = BODY_PARTS_GROUPS['group' in options ? options.group : 'default'];
    sendRPCPacket(RpcMessage.ResetRequest, req);
  };

  const duration = 3;
  const { startCountdown, timer, abortCountdown } = useCountdown({
    duration: options.type === ResetType.Yaw ? 0 : duration,
    onCountdownEnd: () => {
      maybePlaySoundOnResetEnd(options.type);
      reset();
      onResetFinished();
    },
  });

  const onResetFinished = () => {
    setStatus('finished');

    // If a timer was already running / clear it
    abortCountdown();
    if (finishedTimeoutRef.current !== -1) clearTimeout(finishedTimeoutRef.current);

    // After 2s go back to idle state
    finishedTimeoutRef.current = setTimeout(() => {
      setStatus('idle');
      finishedTimeoutRef.current = -1;
    }, 2000);

    if (onReseted) onReseted();
  };

  const maybePlaySoundOnResetEnd = (type: ResetType) => {
    if (!config?.feedbackSound) return;
    playSoundOnResetEnded(type, config?.feedbackSoundVolume);
  };

  const maybePlaySoundOnResetStart = () => {
    if (!config?.feedbackSound) return;
    if (options.type !== ResetType.Yaw)
      playSoundOnResetStarted(config?.feedbackSoundVolume);
  };

  const triggerReset = () => {
    setStatus('counting');
    startCountdown();
    maybePlaySoundOnResetStart();
  };

  useEffect(() => {
    return () => {
      if (finishedTimeoutRef.current !== -1) clearTimeout(finishedTimeoutRef.current);
    };
  }, []);

  useRPCPacket(RpcMessage.ResetResponse, ({ status, resetType }: ResetResponseT) => {
    if (resetType !== options.type) return;
    switch (status) {
      case ResetStatus.FINISHED: {
        onResetFinished();
        break;
      }
    }
  });

  const name = useMemo(() => {
    switch (options.type) {
      case ResetType.Yaw:
        return 'reset-yaw';
      case ResetType.Full:
        return 'reset-full';
      case ResetType.Mounting:
        if (options.group !== 'default') return `reset-mounting-${options.group}`;
        return 'reset-mounting';
      default:
        return 'unhandled';
    }
  }, [options.type]);

  let disabled = status === 'counting';
  if (options.type === ResetType.Mounting && options.group !== 'default') {
    const assignedTrackers = useAtomValue(assignedTrackersAtom);

    if (
      !assignedTrackers.some(
        ({ tracker }) =>
          tracker.info?.bodyPart &&
          BODY_PARTS_GROUPS[options.group].includes(tracker.info?.bodyPart)
      )
    )
      disabled = true;
  }

  return {
    triggerReset,
    timer,
    status,
    disabled,
    name,
    duration,
  };
}

export function useMountingReset() {}
