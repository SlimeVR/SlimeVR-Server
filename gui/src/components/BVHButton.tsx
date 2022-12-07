import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  RecordBVHRequestT,
  RecordBVHStatusT,
  RpcMessage
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { RecordIcon } from './commons/icon/RecordIcon';

export function BVHButton() {
  const { t } = useTranslation();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [recording, setRecording] = useState(false);

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
      text={t(recording ? 'bvh.recording' : 'bvh.start-recording')}
      icon={<RecordIcon width={20} />}
      onClick={toggleBVH}
    ></BigButton>
  );
}
