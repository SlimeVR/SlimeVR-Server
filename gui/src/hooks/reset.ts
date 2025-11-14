import { useEffect, useMemo, useRef, useState } from 'react';
import {
  BodyPart,
  ResetRequestT,
  ResetResponseT,
  ResetStatus,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useAtomValue } from 'jotai';
import { assignedTrackersAtom } from '@/store/app-store';
import { FEET_BODY_PARTS, FINGER_BODY_PARTS } from './body-parts';

export type ResetBtnStatus = 'idle' | 'counting' | 'finished';

export type MountingResetGroup = 'default' | 'feet' | 'fingers';
export type UseResetOptions =
  | { type: ResetType.Full | ResetType.Yaw }
  | { type: ResetType.Mounting; group: MountingResetGroup };

export const BODY_PARTS_GROUPS: Record<MountingResetGroup, BodyPart[]> = {
  default: [],
  feet: FEET_BODY_PARTS,
  fingers: FINGER_BODY_PARTS,
};

export function useReset(options: UseResetOptions, onReseted?: () => void) {
  if (options.type === ResetType.Mounting && !options.group) options.group = 'default';

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const finishedTimeoutRef = useRef<NodeJS.Timeout>();
  const [status, setStatus] = useState<ResetBtnStatus>('idle');
  const [progress, setProgress] = useState(0);
  const [duration, setDuration] = useState(0);

  const parts = BODY_PARTS_GROUPS['group' in options ? options.group : 'default'];

  const triggerReset = () => {
    const req = new ResetRequestT();
    req.resetType = options.type;
    req.bodyParts = parts;
    sendRPCPacket(RpcMessage.ResetRequest, req);
  };

  const onResetFinished = () => {
    setStatus('finished');
    if (onReseted) onReseted();
  };

  useEffect(() => {
    if (status === 'finished') {
      finishedTimeoutRef.current = setTimeout(() => {
        setStatus('idle'); // only do that if we were on finished status. Allows to reset the outlined border
      }, 2000);
    } else {
      clearTimeout(finishedTimeoutRef.current);
    }
    return () => {
      clearTimeout(finishedTimeoutRef.current);
    };
  }, [status]);

  const onResetProgress = (progress: number, duration: number) => {
    setProgress(progress / 1000);
    setDuration(duration / 1000);
  };

  useRPCPacket(
    RpcMessage.ResetResponse,
    ({ status, resetType, progress, duration, bodyParts }: ResetResponseT) => {
      if (
        resetType !== options.type ||
        JSON.stringify(parts) !== JSON.stringify(bodyParts)
      )
        return;
      switch (status) {
        case ResetStatus.FINISHED: {
          onResetFinished();
          break;
        }
        case ResetStatus.STARTED: {
          setStatus('counting');
          onResetProgress(progress, duration);
          break;
        }
      }
    }
  );

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
    progress,
    duration,
    status,
    disabled,
    name,
    timer: duration - progress,
  };
}

export function useMountingReset() {}
