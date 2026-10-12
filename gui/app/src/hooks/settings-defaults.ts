import { useEffect } from 'react';
import { useAtomValue, useSetAtom } from 'jotai';
import {
  RpcMessage,
  SettingsDefaultsRequestT,
  SettingsDefaultsResponseT,
} from 'solarxr-protocol';
import { SettingsDefaults, settingsDefaultsAtom } from '@/store/app-store';
import { useWebsocketAPI } from './websocket-api';

export function useProvideSettingsDefaults() {
  const { useRPCPacket, sendRPCPacket, isConnected } = useWebsocketAPI();
  const setSettingsDefaults = useSetAtom(settingsDefaultsAtom);

  useRPCPacket(
    RpcMessage.SettingsDefaultsResponse,
    (packet: SettingsDefaultsResponseT) => {
      setSettingsDefaults(packet as SettingsDefaults);
    }
  );

  useEffect(() => {
    if (!isConnected) return;
    sendRPCPacket(RpcMessage.SettingsDefaultsRequest, new SettingsDefaultsRequestT());
  }, [isConnected]);
}

export function useSettingsDefaults(): SettingsDefaults {
  const defaults = useAtomValue(settingsDefaultsAtom);
  if (!defaults) {
    throw new Error('Settings defaults read before the router loaded them');
  }
  return defaults;
}
