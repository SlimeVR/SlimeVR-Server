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
import { assignedTrackersAtom, serverGuardsAtom } from '@/store/app-store';
import { FEET_BODY_PARTS, FINGER_BODY_PARTS } from './body-parts';
import { useLocaleConfig } from '@/i18n/config';
import * as Sentry from '@sentry/react';

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

export function useReset(
  options: UseResetOptions,
  onReseted?: () => void,
  onFailed?: () => void
) {
  if (options.type === ResetType.Mounting && !options.group) options.group = 'default';

  const serverGuards = useAtomValue(serverGuardsAtom);
  const { currentLocales } = useLocaleConfig();
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

    Sentry.metrics.count('reset_click', 1, {
      attributes: {
        resetType: ResetType[options.type],
        group: options.type === ResetType.Mounting ? options.group : undefined,
      },
    });
  };

  const onResetFinished = () => {
    setStatus('finished');
    if (onReseted) onReseted();
  };

  const onResetCanceled = () => {
    if (status !== 'finished') setStatus('idle');
    if (onFailed) onFailed();
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
        (resetType == ResetType.Mounting &&
          JSON.stringify(parts) !== JSON.stringify(bodyParts))
      ) {
        onResetCanceled();
        return;
      }
      onResetProgress(progress, duration);
      switch (status) {
        case ResetStatus.FINISHED: {
          onResetFinished();
          break;
        }
        case ResetStatus.STARTED: {
          setStatus('counting');
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
  let error = null;
  if (options.type === ResetType.Mounting && options.group !== 'default') {
    const assignedTrackers = useAtomValue(assignedTrackersAtom);

    if (
      !assignedTrackers.some(
        ({ tracker }) =>
          tracker.info?.bodyPart &&
          BODY_PARTS_GROUPS[options.group].includes(tracker.info?.bodyPart)
      )
    ) {
      disabled = true;
      error = `reset-error-no_${options.group}_tracker`;
    }
  } else if (options.type === ResetType.Mounting && !serverGuards?.canDoMounting) {
    disabled = true;
    error = 'reset-error-mounting-need_full_reset';
  } else if (options.type === ResetType.Yaw && !serverGuards?.canDoYawReset) {
    disabled = true;
    error = 'reset-error-yaw-need_full_reset';
  }

  const localized = useMemo(
    () =>
      Intl.NumberFormat('en-US', {
        maximumFractionDigits: 1,
        unit: 'second',
        unitDisplay: 'narrow',
        style: 'unit',
      }),
    [currentLocales]
  );

  return {
    triggerReset,
    progress,
    duration,
    status,
    disabled,
    name,
    error,
    timer: localized.format(duration - progress),
  };
}
