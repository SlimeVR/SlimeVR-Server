import { useEffect, useState } from 'react';
import { RpcMessage, SettingsRequestT, SettingsResponseT, VelocitySettingsT, TrackerBodyPartMappingsT } from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

/**
 * Hook to access velocity settings and tracker mappings from the server
 */
export function useVelocitySettings() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [velocitySettings, setVelocitySettings] = useState<VelocitySettingsT | null>(null);
  const [trackerBodyPartMappings, setTrackerBodyPartMappings] = useState<TrackerBodyPartMappingsT | null>(null);

  useEffect(() => {
    // Request settings on mount
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    if (settings.velocitySettings) {
      setVelocitySettings(settings.velocitySettings);
    }
    if (settings.trackerBodyPartMappings) {
      setTrackerBodyPartMappings(settings.trackerBodyPartMappings);
    }
  });

  return { velocitySettings, trackerBodyPartMappings };
}

