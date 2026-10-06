import { useEffect, useMemo, useRef, useState } from 'react';
import {
  BodyPart,
  CancelResetRequestT,
  CountdownDetailT,
  MountingMethod,
  ResetAvailability,
  ResetDetail,
  ResetLifecycle,
  ResetRequestT,
  ResetStatusResponseT,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useAtomValue } from 'jotai';
import {
  assignedTrackersAtom,
  resetStatusAtom,
  serverGuardsAtom,
} from '@/store/app-store';
import { FEET_BODY_PARTS, FINGER_BODY_PARTS, TOE_BODY_PARTS } from './body-parts';
import { useLocaleConfig } from '@/i18n/config';
import { useResetsSettings } from './resets-settings';

export type ResetBtnStatus = 'idle' | 'counting' | 'finished';

export type MountingResetGroup = 'default' | 'feet' | 'fingers' | 'toes';
export type UseResetOptions =
  | { type: ResetType.FULL | ResetType.YAW }
  | { type: ResetType.MOUNTING; group: MountingResetGroup };

export const BODY_PARTS_GROUPS: Record<MountingResetGroup, BodyPart[]> = {
  default: [],
  feet: FEET_BODY_PARTS,
  toes: TOE_BODY_PARTS,
  fingers: FINGER_BODY_PARTS,
};

export function useCancelReset() {
  const { sendRPCPacket } = useWebsocketAPI();
  return () => {
    sendRPCPacket(RpcMessage.CancelResetRequest, new CancelResetRequestT());
  };
}

function sameBodyParts(a: BodyPart[], b: BodyPart[]) {
  return a.length === b.length && [...a].sort().join() === [...b].sort().join();
}

function guardError(type: ResetType, availability: ResetAvailability) {
  switch (availability) {
    case ResetAvailability.NEEDS_FULL_RESET:
      return type === ResetType.YAW
        ? 'reset-error-yaw-need_full_reset'
        : 'reset-error-mounting-need_full_reset';
    case ResetAvailability.NEEDS_POSITIONAL_HEAD:
      return 'reset-error-need_positional_head';
    case ResetAvailability.NO_TRACKERS:
      return 'reset-error-no_trackers';
    default:
      return null;
  }
}

export function useReset(
  options: UseResetOptions,
  onReseted?: () => void,
  onFailed?: () => void
) {
  if (options.type === ResetType.MOUNTING && !options.group) options.group = 'default';

  const serverGuards = useAtomValue(serverGuardsAtom);
  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const resetStatus = useAtomValue(resetStatusAtom);
  const { resetsSettings } = useResetsSettings();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket } = useWebsocketAPI();
  const finishedTimeoutRef = useRef<NodeJS.Timeout>();
  const handledStatusRef = useRef<ResetStatusResponseT | null>(resetStatus);
  const [finished, setFinished] = useState(false);

  const parts = BODY_PARTS_GROUPS['group' in options ? options.group : 'default'];

  // check if it is this hook instance that triggered the reset
  const isMine = (status: ResetStatusResponseT | null) =>
    !!status &&
    status.resetType === options.type &&
    (options.type !== ResetType.MOUNTING || sameBodyParts(status.bodyParts, parts));

  const triggerReset = (method = resetsSettings?.mountingMethod) => {
    const req = new ResetRequestT();
    req.resetType = options.type;
    req.bodyParts = parts;
    switch (options.type) {
      case ResetType.YAW:
        req.delay = 0;
        break;
      case ResetType.FULL:
        req.delay = 3;
        break;
      case ResetType.MOUNTING:
        req.delay = method === MountingMethod.STEP ? 0 : 3;
        break;
    }
    sendRPCPacket(RpcMessage.ResetRequest, req);
  };

  const cancel = useCancelReset();

  useEffect(() => {
    if (resetStatus === handledStatusRef.current) return;
    handledStatusRef.current = resetStatus;
    if (!resetStatus || !isMine(resetStatus)) return;

    if (resetStatus.lifecycle === ResetLifecycle.DONE) {
      setFinished(true);
      if (onReseted) onReseted();
    } else if (
      resetStatus.lifecycle === ResetLifecycle.CANCELED ||
      resetStatus.lifecycle === ResetLifecycle.FAILED
    ) {
      if (onFailed) onFailed();
    }
  }, [resetStatus]);

  useEffect(() => {
    if (finished) {
      finishedTimeoutRef.current = setTimeout(() => {
        setFinished(false);
      }, 2000);
    }
    return () => {
      clearTimeout(finishedTimeoutRef.current);
    };
  }, [finished]);

  const counting =
    isMine(resetStatus) && resetStatus?.lifecycle === ResetLifecycle.RUNNING;
  const status: ResetBtnStatus = counting ? 'counting' : finished ? 'finished' : 'idle';

  // Step mounting runs without a countdown
  const countdown =
    counting && resetStatus?.detailType === ResetDetail.CountdownDetail
      ? (resetStatus.detail as CountdownDetailT)
      : null;
  const progress = (countdown?.progress ?? 0) / 1000;
  const duration = (countdown?.duration ?? 0) / 1000;

  const name = useMemo(() => {
    switch (options.type) {
      case ResetType.YAW:
        return 'reset-yaw';
      case ResetType.FULL:
        return 'reset-full';
      case ResetType.MOUNTING:
        if (options.group !== 'default') return `reset-mounting-${options.group}`;
        return 'reset-mounting';
      default:
        return 'unhandled';
    }
  }, [options.type]);

  let disabled = status === 'counting';
  let error: string | null = null;
  // A manual or unpicked method runs no reset, so its button has nothing to guard
  const mountingRuns =
    resetsSettings?.mountingMethod === MountingMethod.STEP ||
    resetsSettings?.mountingMethod === MountingMethod.POSE;
  const availability =
    options.type === ResetType.MOUNTING
      ? mountingRuns
        ? serverGuards?.mountingReset
        : ResetAvailability.AVAILABLE
      : options.type === ResetType.YAW
        ? serverGuards?.yawReset
        : ResetAvailability.AVAILABLE;
  if (availability !== undefined && availability !== ResetAvailability.AVAILABLE) {
    disabled = true;
    error = guardError(options.type, availability);
  } else if (options.type === ResetType.MOUNTING && options.group !== 'default') {
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
    cancel,
    progress,
    duration,
    status,
    disabled,
    name,
    error,
    timer: localized.format(duration - progress),
  };
}
