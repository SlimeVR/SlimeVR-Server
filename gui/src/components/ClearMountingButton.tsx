import { useLocalization } from '@fluent/react';
import { ClearMountingResetRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { TrashIcon } from './commons/icon/TrashIcon';

export function ClearMountingButton() {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();

  const clearMounting = () => {
    const record = new ClearMountingResetRequestT();
    sendRPCPacket(RpcMessage.ClearMountingResetRequest, record);
  };

  return (
    <BigButton
      text={l10n.getString('widget-clear_mounting')}
      icon={<TrashIcon width={20} />}
      onClick={clearMounting}
    >
      {}
    </BigButton>
  );
}
