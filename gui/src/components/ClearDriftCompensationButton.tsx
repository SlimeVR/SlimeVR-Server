import { useLocalization } from '@fluent/react';
import { ClearDriftCompensationRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { Button } from './commons/Button';

export function ClearDriftCompensationButton() {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();

  const clearDriftCompensation = () => {
    const record = new ClearDriftCompensationRequestT();
    sendRPCPacket(RpcMessage.ClearDriftCompensationRequest, record);
  };

  return (
    <Button variant="secondary" onClick={clearDriftCompensation}>
      {l10n.getString('widget-drift_compensation-clear')}
    </Button>
  );
}
