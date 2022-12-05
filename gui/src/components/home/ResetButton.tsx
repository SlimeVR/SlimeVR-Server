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

  const getText = () => {
    switch (type) {
      case ResetType.Quick:
        return 'Quick Reset';
      case ResetType.Mounting:
        return 'Reset Mounting';
      case ResetType.Full:
        return 'Reset';
    }
    return 'Reset';
  };

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
          {getText()}
        </Button>
      ) : (
        <Button
          icon={getIcon()}
          onClick={startCountdown}
          variant="primary"
          disabled={isCounting}
        >
          <div className="relative">
            <div className="opacity-0 h-0">{getText()}</div>
            {!isCounting ? getText() : String(timer)}
          </div>
        </Button>
      ),
    big:
      type == ResetType.Quick ? (
        <BigButton
          text={getText()}
          icon={getIcon()}
          onClick={reset}
        ></BigButton>
      ) : (
        <BigButton
          text={!isCounting ? getText() : String(timer)}
          icon={getIcon()}
          onClick={startCountdown}
          disabled={isCounting}
        ></BigButton>
      ),
  };

  return variantsMap[variant];
}
