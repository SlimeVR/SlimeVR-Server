import { useLocalization } from '@fluent/react';
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

export function BVHButton(props: React.HTMLAttributes<HTMLButtonElement>) {
  const { l10n } = useLocalization();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [recording, setRecording] = useState(false);

  useEffect(() => {
    sendRPCPacket(RpcMessage.RecordBVHStatusRequest, new RecordBVHRequestT());
  }, []);

  const toggleBVH = () => {
    const record = new RecordBVHRequestT();
    record.stop = recording;
    sendRPCPacket(RpcMessage.RecordBVHRequest, record);
  };

  useRPCPacket(RpcMessage.RecordBVHStatus, (data: RecordBVHStatusT) => {
    setRecording(data.recording);
  });

  return (
    <BigButton
      text={l10n.getString(recording ? 'bvh-recording' : 'bvh-start_recording')}
      icon={<RecordIcon width={20} />}
      onClick={toggleBVH}
      className={classNames(
        props.className,
        'border',
        recording ? 'border-status-critical' : 'border-transparent'
      )}
    ></BigButton>
  );
}
