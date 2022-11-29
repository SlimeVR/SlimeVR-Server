import { useState } from 'react';
import { ResetRequestT, ResetType, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import { BigButton } from '../commons/BigButton';
import {
  MountingResetIcon,
  QuickResetIcon,
  ResetIcon,
} from '../commons/icon/ResetIcon';

export function ResetButton({ type }: { type: ResetType }) {
  const [resetting, setResetting] = useState(false);
  const [timer, setDisplayTimer] = useState(0);
  const { sendRPCPacket } = useWebsocketAPI();

  const getText = () => {
    switch (type) {
      case ResetType.Quick:
        return 'Quick Reset';
      case ResetType.Mounting:
        return 'Reset Mounting';
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

  const TIMER_DURATION = 3;
  const resetStart = () => {
    setResetting(true);
    setDisplayTimer(TIMER_DURATION);
    if (type !== ResetType.Quick) {
      for (let i = 1; i < TIMER_DURATION; i++) {
        setTimeout(() => setDisplayTimer(TIMER_DURATION - i), i * 1000);
      }
      setTimeout(resetEnd, TIMER_DURATION * 1000);
    } else {
      resetEnd();
    }
  };

  const resetEnd = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    sendRPCPacket(RpcMessage.ResetRequest, req);
    setResetting(false);
  };

  return (
    <BigButton
      text={!resetting ? getText() : String(timer)}
      icon={getIcon()}
      onClick={resetStart}
      disabled={resetting}
    ></BigButton>
  );
}
