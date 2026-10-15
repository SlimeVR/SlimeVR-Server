import { useSetAtom } from 'jotai';
import {
  AddUnknownDeviceRequestT,
  IgnoreUnknownDeviceRequestT,
  RpcMessage,
  UnknownDeviceStatus,
  UnknownDevicesResponseT,
  UnknownDeviceT,
} from 'solarxr-protocol';
import { unknownDevicesAtom } from '@/store/app-store';
import { useWebsocketAPI } from './websocket-api';

export function useProvideUnknownDevices() {
  const { useRPCPacket } = useWebsocketAPI();
  const setUnknownDevices = useSetAtom(unknownDevicesAtom);

  useRPCPacket(
    RpcMessage.UnknownDevicesResponse,
    ({ devices }: UnknownDevicesResponseT) => {
      setUnknownDevices(devices);
    }
  );
}

export function usePendingTrackerActions() {
  const { sendRPCPacket } = useWebsocketAPI();

  const add = (macAddress: string) => {
    sendRPCPacket(
      RpcMessage.AddUnknownDeviceRequest,
      new AddUnknownDeviceRequestT(macAddress)
    );
  };

  const ignore = (macAddress: string) => {
    sendRPCPacket(
      RpcMessage.IgnoreUnknownDeviceRequest,
      new IgnoreUnknownDeviceRequestT(macAddress)
    );
  };

  const addAll = (devices: UnknownDeviceT[]) => {
    devices
      .filter(
        (device) => device.status !== UnknownDeviceStatus.ADDING && device.macAddress
      )
      .forEach((device) => add(device.macAddress as string));
  };

  return { add, ignore, addAll };
}
