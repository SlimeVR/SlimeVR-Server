import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useRef, useState } from 'react';
import {
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
import { FootIcon } from '@/components/commons/icon/FootIcon';
import { FingersIcon } from '@/components/commons/icon/FingersIcon';
import { FEET_BODY_PARTS, FINGER_BODY_PARTS } from '@/store/app-store';

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

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    switch (bodyPartsToReset) {
      case 'default':
        // Server handles it. Usually all body parts except fingers.
        req.bodyParts = [];
        break;
      case 'feet':
        req.bodyParts = FEET_BODY_PARTS;
        break;
      case 'fingers':
        req.bodyParts = [...FINGER_BODY_PARTS.values()];
        break;
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
        switch (bodyPartsToReset) {
          case 'default':
            return <MountingResetIcon width={20} />;
          case 'feet':
            return <FootIcon width={30} />;
          case 'fingers':
            return <FingersIcon width={20} />;
        }
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
