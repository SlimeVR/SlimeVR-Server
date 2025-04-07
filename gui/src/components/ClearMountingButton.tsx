import { useLocalization } from '@fluent/react';
import { ClearMountingResetRequestT, RpcMessage } from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { BigButton } from './commons/BigButton';
import { TrashIcon } from './commons/icon/TrashIcon';
import { useTrackers } from '@/hooks/tracker';
import { Quaternion } from 'three';
import { QuaternionFromQuatT, similarQuaternions } from '@/maths/quaternion';
import { useMemo } from 'react';
import { Button } from './commons/Button';

const _q = new Quaternion();

export function ClearMountingButton() {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const { useAssignedTrackers } = useTrackers();
  const assignedTrackers = useAssignedTrackers();

  const trackerWithMounting = useMemo(
    () =>
      assignedTrackers.some(
        (d) =>
          !similarQuaternions(
            QuaternionFromQuatT(d?.tracker.info?.mountingResetOrientation),
            _q
          )
      ),
    [assignedTrackers]
  );

  const clearMounting = () => {
    const record = new ClearMountingResetRequestT();
    sendRPCPacket(RpcMessage.ClearMountingResetRequest, record);
  };

  return (
    <Button
      icon={<TrashIcon size={20} />}
      onClick={clearMounting}
      disabled={!trackerWithMounting}
      variant="primary"
    >
      {l10n.getString('widget-clear_mounting')}
    </Button>
  );
}
