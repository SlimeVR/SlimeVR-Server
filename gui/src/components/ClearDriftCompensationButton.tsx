import { Localized } from '@fluent/react';
import { ClearDriftCompensationRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { TrashIcon } from './commons/icon/TrashIcon';

export function ClearDriftCompensationButton({
  disabled,
}: {
  disabled: boolean;
}) {
  const { sendRPCPacket } = useWebsocketAPI();

  const clearDriftCompensation = () => {
    const record = new ClearDriftCompensationRequestT();
    sendRPCPacket(RpcMessage.ClearDriftCompensationRequest, record);
  };

  return (
    <Localized id="widget-drift_compensation-clear">
      <BigButton
        icon={<TrashIcon size={20} />}
        onClick={clearDriftCompensation}
        disabled={disabled}
      ></BigButton>
    </Localized>
  );
}
