import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useRef, useState } from 'react';
import {
  FlightListStepId,
  ResetRequestT,
  ResetType,
  RpcMessage,
} from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { useCountdown } from '@/hooks/countdown';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  playSoundOnResetEnded,
  playSoundOnResetStarted,
} from '@/sounds/sounds';
import { BigButton } from '@/components/commons/BigButton';
import { Button } from '@/components/commons/Button';
import {
  MountingResetIcon,
  YawResetIcon,
  FullResetIcon,
} from '@/components/commons/icon/ResetIcon';
import classNames from 'classnames';
import { useSessionFlightlist } from '@/hooks/session-flightlist';

export function ResetButton({
  type,
  size = 'big',
  className,
  onReseted,
}: {
  className?: string;
  type: ResetType;
  size: 'big' | 'small';
  onReseted?: () => void;
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const { visibleSteps: steps } = useSessionFlightlist();
  const { config } = useConfig();
  const finishedTimeoutRef = useRef(-1);
  const [isFinished, setFinished] = useState(false);

  const needsFullReset = useMemo(
    () =>
      type === ResetType.Mounting &&
      steps.some(
        (step) => step.id === FlightListStepId.FULL_RESET && !step.valid
      ),
    [steps, type]
  );

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    sendRPCPacket(RpcMessage.ResetRequest, req);
  };

  const { isCounting, startCountdown, timer } = useCountdown({
    duration: type === ResetType.Yaw ? 0 : undefined,
    onCountdownEnd: () => {
      maybePlaySoundOnResetEnd(type);
      reset();
      setFinished(true);
      if (finishedTimeoutRef.current !== -1)
        clearTimeout(finishedTimeoutRef.current);
      finishedTimeoutRef.current = setTimeout(() => {
        setFinished(false);
        finishedTimeoutRef.current = -1;
      }, 2000);
      if (onReseted) onReseted();
    },
  });

  const text = useMemo(() => {
    switch (type) {
      case ResetType.Yaw:
        return l10n.getString('reset-yaw');
      case ResetType.Mounting:
        return l10n.getString('reset-mounting');
      case ResetType.Full:
        return l10n.getString('reset-full');
    }
  }, [type]);

  const getIcon = () => {
    switch (type) {
      case ResetType.Yaw:
        return <YawResetIcon width={20} />;
      case ResetType.Mounting:
        return <MountingResetIcon width={20} />;
    }
    return <FullResetIcon width={20} />;
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
    setFinished(false);
    startCountdown();
    maybePlaySoundOnResetStart();
  };

  useEffect(() => {
    return () => {
      if (finishedTimeoutRef.current !== -1)
        clearTimeout(finishedTimeoutRef.current);
    };
  }, []);

  return size === 'small' ? (
    <Button
      icon={getIcon()}
      onClick={triggerReset}
      className={classNames(
        'border-2',
        isFinished
          ? 'border-status-success'
          : 'transition-[border-color] duration-500 ease-in-out border-transparent',
        className
      )}
      variant="primary"
      disabled={isCounting || needsFullReset}
    >
      {!isCounting || type === ResetType.Yaw ? text : String(timer)}
    </Button>
  ) : (
    <BigButton
      icon={getIcon()}
      onClick={triggerReset}
      className={classNames(
        'border-2',
        isFinished
          ? 'border-status-success'
          : 'transition-[border-color] duration-500 ease-in-out border-transparent',
        className
      )}
      disabled={isCounting || needsFullReset}
    >
      {!isCounting || type === ResetType.Yaw ? text : String(timer)}
    </BigButton>
  );
}
