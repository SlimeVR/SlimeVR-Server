import {
  ResetType,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
} from 'solarxr-protocol';
import { ResetButton } from './home/ResetButton';
import { ClearDriftCompensationButton } from './ClearDriftCompensationButton';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect, useState } from 'react';
import { ClearMountingButton } from './ClearMountingButton';

export function Toolbar() {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [driftCompensationEnabled, setDriftCompensationEnabled] =
    useState(false);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    if (settings.driftCompensation != null)
      setDriftCompensationEnabled(settings.driftCompensation.enabled);
  });

  return (
    <div className="flex p-2 gap-2 bg-background-70 rounded-md mr-2 my-2">
      <ResetButton type={ResetType.Yaw} size="small"></ResetButton>
      <ResetButton type={ResetType.Full} size="small"></ResetButton>
      <ResetButton type={ResetType.Mounting} size="small"></ResetButton>
      <ClearMountingButton></ClearMountingButton>
      <ClearDriftCompensationButton
        disabled={!driftCompensationEnabled}
      ></ClearDriftCompensationButton>
    </div>
  );
}
