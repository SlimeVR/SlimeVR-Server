import { useAtomValue, useStore } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { isEqual } from '@react-hookz/deep-equal';
import { ChangeResetsSettingsRequestT, RpcMessage } from 'solarxr-protocol';
import { resetsSettingsAtom } from '@/store/app-store';
import { useWebsocketAPI } from './websocket-api';

const resetsSettingsValueAtom = selectAtom(
  resetsSettingsAtom,
  (settings) => settings,
  isEqual
);

export function useResetsSettings() {
  const resetsSettings = useAtomValue(resetsSettingsValueAtom);
  const store = useStore();
  const { sendRPCPacket } = useWebsocketAPI();

  const setResetsSettings = (partial: Partial<ChangeResetsSettingsRequestT>) => {
    const request = Object.assign(
      new ChangeResetsSettingsRequestT(),
      store.get(resetsSettingsAtom),
      partial
    );
    sendRPCPacket(RpcMessage.ChangeResetsSettingsRequest, request);
  };

  return { resetsSettings, setResetsSettings };
}
