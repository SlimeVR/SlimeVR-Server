import { useEffect, useState } from 'react';
import { useWebsocketAPI } from './websocket-api';
import {
  RpcMessage,
  SetPauseTrackingRequestT,
  TrackingPauseStateRequestT,
  TrackingPauseStateResponseT,
} from 'solarxr-protocol';

export function usePauseTracking() {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [paused, setPaused] = useState(false);

  const toggle = () => {
    const pause = new SetPauseTrackingRequestT(!paused);
    sendRPCPacket(RpcMessage.SetPauseTrackingRequest, pause);
  };

  useRPCPacket(
    RpcMessage.TrackingPauseStateResponse,
    (data: TrackingPauseStateResponseT) => {
      setPaused(data.trackingPaused);
    }
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.TrackingPauseStateRequest,
      new TrackingPauseStateRequestT()
    );
  }, []);

  return {
    toggle,
    paused,
  };
}
