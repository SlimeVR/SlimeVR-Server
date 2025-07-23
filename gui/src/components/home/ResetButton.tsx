import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useRef, useState } from 'react';
import {
  BodyPart,
  ResetRequestT,
  ResetType,
  RpcMessage,
  StatusData,
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
import { useStatusContext } from '@/hooks/status-system';
import classNames from 'classnames';

export function ResetButton({
  type,
  size = 'big',
  bodyPartsToReset = 'default',
  className,
  onReseted,
}: {
  className?: string;
  type: ResetType;
  size: 'big' | 'small';
  bodyPartsToReset?: 'default' | 'feet' | 'fingers';
  onReseted?: () => void;
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const { statuses } = useStatusContext();
  const { config } = useConfig();
  const finishedTimeoutRef = useRef(-1);
  const [isFinished, setFinished] = useState(false);

  const needsFullReset = useMemo(
    () =>
      type === ResetType.Mounting &&
      Object.values(statuses).some(
        (status) => status.dataType === StatusData.StatusTrackerReset
      ),
    [statuses]
  );

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

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    if (bodyPartsToReset === 'default') {
      // Default (server handles it)
      req.bodyParts = [];
    } else if (bodyPartsToReset === 'feet') {
      // Feet
      req.bodyParts = feetBodyParts;
    } else if (bodyPartsToReset === 'fingers') {
      // Fingers
      req.bodyParts = fingerBodyParts;
    }
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
        return l10n.getString(
          'reset-yaw' +
            (bodyPartsToReset !== 'default' ? '-' + bodyPartsToReset : '')
        );
      case ResetType.Mounting:
        return l10n.getString(
          'reset-mounting' +
            (bodyPartsToReset !== 'default' ? '-' + bodyPartsToReset : '')
        );
      case ResetType.Full:
        return l10n.getString(
          'reset-full' +
            (bodyPartsToReset !== 'default' ? '-' + bodyPartsToReset : '')
        );
    }
  }, [type, bodyPartsToReset]);

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
