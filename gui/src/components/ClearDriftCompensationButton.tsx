import { useLocalization } from '@fluent/react';
import { ClearDriftCompensationRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { TrashIcon } from './commons/icon/TrashIcon';
import { Button } from './commons/Button';

export function ClearDriftCompensationButton({
  disabled,
}: {
  disabled: boolean;
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();

  const clearDriftCompensation = () => {
    const record = new ClearDriftCompensationRequestT();
    sendRPCPacket(RpcMessage.ClearDriftCompensationRequest, record);
  };

  return (
    <Button
      icon={<TrashIcon size={20} />}
      onClick={clearDriftCompensation}
      disabled={disabled}
      variant="secondary"
    >
      {l10n.getString('widget-drift_compensation-clear')}
    </Button>
  );
}
