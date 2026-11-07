import { useAtomValue, useSetAtom } from 'jotai';
import {
  RpcMessage,
  SettingsDefaultsRequestT,
  SettingsDefaultsResponseT,
} from 'solarxr-protocol';
import { SettingsDefaults, settingsDefaultsAtom } from '@/store/app-store';
import { useWebsocketAPI } from './websocket-api';

export function useProvideSettingsDefaults() {
  const { useRPCPacket, sendRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const setSettingsDefaults = useSetAtom(settingsDefaultsAtom);

  useRPCPacket(
    RpcMessage.SettingsDefaultsResponse,
    (packet: SettingsDefaultsResponseT) => {
      setSettingsDefaults(packet as SettingsDefaults);
    }
  );

  useConnectedEffect(() => {
    sendRPCPacket(RpcMessage.SettingsDefaultsRequest, new SettingsDefaultsRequestT());
  }, []);
}

export function useSettingsDefaults(): SettingsDefaults {
  const defaults = useAtomValue(settingsDefaultsAtom);
  if (!defaults) {
    throw new Error('Settings defaults read before the router loaded them');
  }
  return defaults;
}
