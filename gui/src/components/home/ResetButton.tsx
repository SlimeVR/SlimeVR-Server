import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { ResetRequestT, ResetType, RpcMessage } from 'solarxr-protocol';
import { useCountdown } from '../../hooks/countdown';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import { BigButton } from '../commons/BigButton';
import { Button } from '../commons/Button';
import {
  MountingResetIcon,
  QuickResetIcon,
  ResetIcon
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
  const { t } = useTranslation();
  const { sendRPCPacket } = useWebsocketAPI();

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
      case ResetType.Quick:
        return t('reset.quick');
      case ResetType.Mounting:
        return t('reset.mounting');
      case ResetType.Full:
        return t('reset.full');
    }
    return t('reset.full');
  }, [type]);

  const getIcon = () => {
    switch (type) {
      case ResetType.Quick:
        return <QuickResetIcon width={20} />;
      case ResetType.Mounting:
        return <MountingResetIcon width={20} />;
    }
    return <ResetIcon width={20} />;
  };

  const variantsMap = {
    small:
      type == ResetType.Quick ? (
        <Button icon={getIcon()} onClick={reset} variant="primary">
          {text}
        </Button>
      ) : (
        <Button
          icon={getIcon()}
          onClick={startCountdown}
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
      type == ResetType.Quick ? (
        <BigButton text={text} icon={getIcon()} onClick={reset}></BigButton>
      ) : (
        <BigButton
          text={!isCounting ? text : String(timer)}
          icon={getIcon()}
          onClick={startCountdown}
          disabled={isCounting}
        ></BigButton>
      ),
  };

  return variantsMap[variant];
}
