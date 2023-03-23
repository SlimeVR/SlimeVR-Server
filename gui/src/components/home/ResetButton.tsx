import { useLocalization } from '@fluent/react';
import { useMemo } from 'react';
import { ResetRequestT, ResetType, RpcMessage } from 'solarxr-protocol';
import { useConfig } from '../../hooks/config';
import { useCountdown } from '../../hooks/countdown';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import { playSoundOnResetStarted } from '../../sounds/sounds';
import { BigButton } from '../commons/BigButton';
import { Button } from '../commons/Button';
import {
  MountingResetIcon,
  YawResetIcon,
  FullResetIcon,
} from '../commons/icon/ResetIcon';

export function ResetButton({
  type,
  variant = 'big',
  onReseted,
}: {
  type: ResetType;
  variant: 'big' | 'small';
  onReseted?: () => void;
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const { config } = useConfig();

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    sendRPCPacket(RpcMessage.ResetRequest, req);
  };

  const { isCounting, startCountdown, timer } = useCountdown({
    onCountdownEnd: () => {
      reset();
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
    return l10n.getString('reset-full');
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

  const maybePlaySoundOnResetStarted = (type: ResetType) => {
    if (!config?.feedbackSound) return;
    playSoundOnResetStarted(type, config?.feedbackSoundVolume);
  }

  const variantsMap = {
    small:
      type == ResetType.Yaw ? (
        <Button
          icon={getIcon()}
          onClick={() => {
            reset();
            maybePlaySoundOnResetStarted(type);
          }}
          variant="primary"
        >
          {text}
        </Button>
      ) : (
        <Button
          icon={getIcon()}
          onClick={() => {
            startCountdown();
            maybePlaySoundOnResetStarted(type);
          }}
          variant="primary"
          disabled={isCounting}
        >
          <div className="relative">
            <div className="opacity-0 h-0">{text}</div>
            {!isCounting ? text : String(timer)}
          </div>
        </Button>
      ),
    big:
      type == ResetType.Yaw ? (
        <BigButton
          text={text}
          icon={getIcon()}
          onClick={() => {
            reset();
            maybePlaySoundOnResetStarted(type);
          }}
        ></BigButton>
      ) : (
        <BigButton
          text={!isCounting ? text : String(timer)}
          icon={getIcon()}
          onClick={() => {
            startCountdown();
            maybePlaySoundOnResetStarted(type);
          }}
          disabled={isCounting}
        ></BigButton>
      ),
  };

  return variantsMap[variant];
}
