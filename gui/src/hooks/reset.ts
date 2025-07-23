import {
  playSoundOnResetEnded,
  playSoundOnResetStarted,
} from '@/sounds/sounds';
import { useEffect, useMemo, useRef, useState } from 'react';
import {
  FlightListStepId,
  ResetRequestT,
  ResetResponseT,
  ResetStatus,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { useConfig } from './config';
import { useSessionFlightlist } from './session-flightlist';
import { useWebsocketAPI } from './websocket-api';
import { useCountdown } from './countdown';

export type ResetBtnStatus = 'idle' | 'counting' | 'finished';
export function useReset(type: ResetType, onReseted?: () => void) {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { config } = useConfig();
  const finishedTimeoutRef = useRef(-1);
  const [status, setStatus] = useState<ResetBtnStatus>('idle');

  const { visibleSteps } = useSessionFlightlist();
  const needsFullReset = useMemo(
    () =>
      type == ResetType.Mounting &&
      visibleSteps.some(
        (step) => step.id === FlightListStepId.FULL_RESET && !step.valid
      ),
    [visibleSteps, type]
  );

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    sendRPCPacket(RpcMessage.ResetRequest, req);
  };

  const duration = 3;
  const { startCountdown, timer, abortCountdown } = useCountdown({
    duration: type === ResetType.Yaw ? 0 : duration,
    onCountdownEnd: () => {
      maybePlaySoundOnResetEnd(type);
      reset();
      onResetFinished();
    },
  });

  const onResetFinished = () => {
    setStatus('finished');

    // If a timer was already running / clear it
    abortCountdown();
    if (finishedTimeoutRef.current !== -1)
      clearTimeout(finishedTimeoutRef.current);

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
    if (type !== ResetType.Yaw)
      playSoundOnResetStarted(config?.feedbackSoundVolume);
  };

  const triggerReset = () => {
    setStatus('counting');
    startCountdown();
    maybePlaySoundOnResetStart();
  };

  useEffect(() => {
    return () => {
      if (finishedTimeoutRef.current !== -1)
        clearTimeout(finishedTimeoutRef.current);
    };
  }, []);

  useRPCPacket(
    RpcMessage.ResetResponse,
    ({ status, resetType }: ResetResponseT) => {
      if (resetType !== type) return;
      switch (status) {
        case ResetStatus.FINISHED: {
          onResetFinished();
          break;
        }
      }
    }
  );

  const name = useMemo(() => {
    switch (type) {
      case ResetType.Yaw:
        return 'reset-yaw';
      case ResetType.Mounting:
        return 'reset-mounting';
      case ResetType.Full:
        return 'reset-full';
    }
  }, [type]);

  return {
    triggerReset,
    timer,
    status,
    disabled: status === 'counting' || needsFullReset,
    name,
    duration,
  };
}
