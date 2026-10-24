import { useState } from 'react';
import {
  RpcMessage,
  SerialDevicesRequestT,
  SerialDevicesResponseT,
  SerialDeviceT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

export function useSerialDevices() {
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const [devices, setDevices] = useState<SerialDeviceT[] | null>(null);

  useConnectedEffect(() => {
    sendRPCPacket(RpcMessage.SerialDevicesRequest, new SerialDevicesRequestT());
  }, []);

  useRPCPacket(RpcMessage.SerialDevicesResponse, (res: SerialDevicesResponseT) =>
    setDevices(res.devices)
  );

  return devices;
}
