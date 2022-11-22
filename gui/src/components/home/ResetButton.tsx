import { useRef, useState } from 'react';
import { ResetRequestT, ResetType, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import { BigButton } from '../commons/BigButton';
import {
  MountingResetIcon,
  QuickResetIcon,
  ResetIcon,
} from '../commons/icon/ResetIcon';

export function ResetButton({ type }: { type: ResetType }) {
  const timerid = useRef<NodeJS.Timer | null>(null);
  const [reseting, setReseting] = useState(false);
  const [timer, setTimer] = useState(0);
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

  const reset = () => {
    const req = new ResetRequestT();
    req.resetType = type;
    setReseting(true);
    setTimer(0);
    if (type !== ResetType.Quick) {
      if (timerid.current) clearInterval(timerid.current);
      timerid.current = setInterval(() => {
        setTimer((timer) => {
          const newTimer = timer + 1;
          if (newTimer >= 3) {
            // Stop the current interval
            if (timerid.current) clearInterval(timerid.current);

            // Only actually reset on exactly 0 so it doesn't repeatedly reset if bugged
            if (newTimer === 3) sendRPCPacket(RpcMessage.ResetRequest, req);
            else
              console.warn(
                `Reset timer is still running after 3 seconds (newTimer = ${newTimer})`
              );

            // Reset the state
            // Don't reset the timer in-case the interval keeps running
            setReseting(false);
          }
          return newTimer;
        });
      }, 1000);
    } else {
      sendRPCPacket(RpcMessage.ResetRequest, req);
      setReseting(false);
    }
  };

  return (
    <BigButton
      text={!reseting || timer >= 3 ? getText() : `${3 - timer}`}
      icon={getIcon()}
      onClick={reset}
      disabled={reseting}
    ></BigButton>
  );
}
