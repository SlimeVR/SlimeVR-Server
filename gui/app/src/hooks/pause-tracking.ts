import { useEffect, useState } from 'react';
import { useWebsocketAPI } from './websocket-api';
import {
  RpcMessage,
  SetPauseTrackingRequestT,
  TrackingPauseStateRequestT,
  TrackingPauseStateResponseT,
} from 'solarxr-protocol';
import { restartAndPlay, trackingPauseSound, trackingPlaySound } from '@/sounds/sounds';
import { useConfig } from './config';

export function usePauseTracking() {
  const { config } = useConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [paused, setPaused] = useState(false);

  const toggle = () => {
    const pause = new SetPauseTrackingRequestT(!paused);
    sendRPCPacket(RpcMessage.SetPauseTrackingRequest, pause);

    if (!config) return;
    if (pause.pauseTracking) {
      restartAndPlay(trackingPauseSound, config.feedbackSoundVolume);
    } else {
      restartAndPlay(trackingPlaySound, config.feedbackSoundVolume);
    }
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
