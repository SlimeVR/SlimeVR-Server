import { useLocalization } from '@fluent/react';
import { useState } from 'react';
import { SetPauseTrackingRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { PlayIcon } from './commons/icon/PlayIcon';
import { PauseIcon } from './commons/icon/PauseIcon';

export function TrackingPauseButton(
  props: React.HTMLAttributes<HTMLButtonElement>
) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const [trackingPause, setTrackingPause] = useState(false);

  const toggleTracking = () => {
    const pause = new SetPauseTrackingRequestT();
    pause.pauseTracking = !trackingPause;
    setTrackingPause(pause.pauseTracking);
    sendRPCPacket(RpcMessage.SetPauseTrackingRequest, pause);
  };

  return (
    <BigButton
      text={l10n.getString(
        trackingPause ? 'tracking-paused' : 'tracking-unpaused'
      )}
      icon={trackingPause ? <PlayIcon width={20} /> : <PauseIcon width={20} />}
      onClick={toggleTracking}
      className={props.className}
    ></BigButton>
  );
}
