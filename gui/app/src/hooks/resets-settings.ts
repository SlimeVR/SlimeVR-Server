import { useRef } from 'react';
import { useAtomValue } from 'jotai';
import {
  ChangeResetsSettingsRequestT,
  ResetsSettingsRequestT,
  RpcMessage,
} from 'solarxr-protocol';
import { resetsSettingsAtom } from '@/store/app-store';
import { useWebsocketAPI } from './websocket-api';

export function useResetsSettings() {
  const resetsSettings = useAtomValue(resetsSettingsAtom);
  const { sendRPCPacket } = useWebsocketAPI();
  const latestSettings = useRef(resetsSettings);
  latestSettings.current = resetsSettings;

  const setResetsSettings = (partial: Partial<ChangeResetsSettingsRequestT>) => {
    const request = Object.assign(
      new ChangeResetsSettingsRequestT(),
      latestSettings.current,
      partial
    );
    sendRPCPacket(RpcMessage.ChangeResetsSettingsRequest, request);
    sendRPCPacket(RpcMessage.ResetsSettingsRequest, new ResetsSettingsRequestT());
  };

  return { resetsSettings, setResetsSettings };
}
