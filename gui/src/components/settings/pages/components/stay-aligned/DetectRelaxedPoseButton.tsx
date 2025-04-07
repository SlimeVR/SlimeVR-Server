import { useLocalization } from '@fluent/react';
import {
  DetectStayAlignedRelaxedPoseRequestT,
  RpcMessage,
  StayAlignedRelaxedPose,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { MouseEventHandler } from 'react';
import { RPCPacketType, useWebsocketAPI } from '@/hooks/websocket-api';

function detectAngles(
  sendRPCPacket: (type: RpcMessage, data: RPCPacketType) => void,
  pose: StayAlignedRelaxedPose
) {
  const req = new DetectStayAlignedRelaxedPoseRequestT();
  req.pose = pose;
  sendRPCPacket(RpcMessage.DetectStayAlignedRelaxedPoseRequest, req);
}

export function DetectStayAlignedRelaxedPoseButton({
  pose,
  onClick,
}: {
  pose: StayAlignedRelaxedPose;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}) {
  const { sendRPCPacket } = useWebsocketAPI();
  const { l10n } = useLocalization();

  return (
    <Button
      variant="primary"
      onClick={(e) => {
        detectAngles(sendRPCPacket, pose);
        if (onClick) {
          onClick(e);
        }
      }}
    >
      {l10n.getString(
        'settings-general-stay_aligned-relaxed_body_angles-auto_detect'
      )}
    </Button>
  );
}
