import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { RecordBVHRequestT, RecordBVHStatusT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useConfig } from './config';
import { useElectron } from './electron';

export function useBHV() {
  const electron = useElectron()
  const { config } = useConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [state, setState] = useState<'idle' | 'recording' | 'saving'>('idle');
  const { l10n } = useLocalization();

  useEffect(() => {
    sendRPCPacket(RpcMessage.RecordBVHStatusRequest, new RecordBVHRequestT());
  }, []);

  const toggle = async () => {
    const record = new RecordBVHRequestT(state === 'recording');

    if (electron.isElectron && state === 'idle') {
      if (config?.bvhDirectory) {
        record.path = config.bvhDirectory;
      } else {
        setState('saving');
        const open = await electron.api.saveDialog({
          title: l10n.getString('bvh-save_title'),
          filters: [
            {
              name: 'BVH',
              extensions: ['bvh'],
            },
          ],
          defaultPath: 'bvh-recording.bvh',
        })
        record.path = open.filePath;
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
