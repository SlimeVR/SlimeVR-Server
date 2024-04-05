import { useLocalization } from '@fluent/react';
import { ClearDriftCompensationRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { TrashIcon } from './commons/icon/TrashIcon';

export function ClearDriftCompensationButton() {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();

  const clearDriftCompensation = () => {
    const record = new ClearDriftCompensationRequestT();
    sendRPCPacket(RpcMessage.ClearDriftCompensationRequest, record);
  };

  return (
    <BigButton
      text={l10n.getString('widget-drift_compensation-clear')}
      icon={<TrashIcon size={20} />}
      onClick={clearDriftCompensation}
    >
      {}
    </BigButton>
  );
}
