import { useLocalization } from '@fluent/react';
import { isTauri } from '@tauri-apps/api/core';
import { useEffect, useState } from 'react';
import { RecordBVHRequestT, RecordBVHStatusT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useConfig } from './config';
import { save } from '@tauri-apps/plugin-dialog';

export function useBHV() {
  const { config } = useConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [state, setState] = useState<'idle' | 'recording' | 'saving'>('idle');
  const { l10n } = useLocalization();

  useEffect(() => {
    sendRPCPacket(RpcMessage.RecordBVHStatusRequest, new RecordBVHRequestT());
  }, []);

  const toggle = async () => {
    const record = new RecordBVHRequestT(state === 'recording');

    if (isTauri() && state === 'idle') {
      if (config?.bvhDirectory) {
        record.path = config.bvhDirectory;
      } else {
        setState('saving');
        record.path = await save({
          title: l10n.getString('bvh-save_title'),
          filters: [
            {
              name: 'BVH',
              extensions: ['bvh'],
            },
          ],
          defaultPath: 'bvh-recording.bvh',
        });
        setState('idle');
      }
    }

    sendRPCPacket(RpcMessage.RecordBVHRequest, record);
  };

  useRPCPacket(RpcMessage.RecordBVHStatus, (data: RecordBVHStatusT) => {
    setState(data.recording ? 'recording' : 'idle');
  });

  return {
    available:
      typeof window.__ANDROID__ === 'undefined' || !window.__ANDROID__?.isThere(),
    state,
    toggle,
  };
}
