import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import {
  RecordBVHRequestT,
  RecordBVHStatusT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { RecordIcon } from './commons/icon/RecordIcon';
import classNames from 'classnames';
import { isTauri } from '@tauri-apps/api/core';
import { save } from '@tauri-apps/plugin-dialog';

export function BVHButton(props: React.HTMLAttributes<HTMLButtonElement>) {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [recording, setRecording] = useState(false);
  const [saving, setSaving] = useState(false);
  const { l10n } = useLocalization();

  useEffect(() => {
    sendRPCPacket(RpcMessage.RecordBVHStatusRequest, new RecordBVHRequestT());
  }, []);

  const toggleBVH = async () => {
    const record = new RecordBVHRequestT(recording, null);
    if (isTauri() && !recording) {
      setSaving(true);
      record.filePath = await save({
        title: l10n.getString('bvh-save_title'),
        filters: [
          {
            name: 'BVH',
            extensions: ['bvh'],
          },
        ],
        defaultPath: 'bvh-recording.bvh',
      });
      setSaving(false);
    }
    sendRPCPacket(RpcMessage.RecordBVHRequest, record);
  };

  useRPCPacket(RpcMessage.RecordBVHStatus, (data: RecordBVHStatusT) => {
    setRecording(data.recording);
  });

  return (
    <Localized id={recording ? 'bvh-recording' : 'bvh-start_recording'}>
      <BigButton
        icon={<RecordIcon width={20} />}
        onClick={toggleBVH}
        disabled={saving}
        className={classNames(
          props.className,
          'border',
          recording ? 'border-status-critical' : 'border-transparent'
        )}
      ></BigButton>
    </Localized>
  );
}
