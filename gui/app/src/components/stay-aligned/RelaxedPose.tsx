import { useLocalization } from '@fluent/react';
import {
  DetectStayAlignedRelaxedPoseRequestT,
  RpcMessage,
  StayAlignedRelaxedPose,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { MouseEventHandler } from 'react';
import { Button, ButtonProps } from '@/components/commons/Button';

/**
 * Tells the server to set a relaxed pose to the current pose's angles.
 */
export function DetectRelaxedPoseButton({
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
        const req = new DetectStayAlignedRelaxedPoseRequestT();
        req.pose = pose;
        sendRPCPacket(RpcMessage.DetectStayAlignedRelaxedPoseRequest, req);
        if (onClick) {
          onClick(e);
        }
      }}
    >
      {l10n.getString('settings-stay_aligned-relaxed_poses-save_pose')}
    </Button>
  );
}

/**
 * Tells the server to reset the angles in a relaxed pose.
 */
export function ResetRelaxedPoseButton({
  pose,
  variant = 'primary',
  onClick,
  children,
}: {
  variant: ButtonProps['variant'];
  pose: StayAlignedRelaxedPose;
  onClick?: MouseEventHandler<HTMLButtonElement>;
} & React.PropsWithChildren) {
  const { sendRPCPacket } = useWebsocketAPI();
  const { l10n } = useLocalization();

  return (
    <Button
      variant={variant}
      onClick={(e) => {
        const req = new DetectStayAlignedRelaxedPoseRequestT();
        req.pose = pose;
        sendRPCPacket(RpcMessage.ResetStayAlignedRelaxedPoseRequest, req);
        if (onClick) {
          onClick(e);
        }
      }}
    >
      {children ||
        l10n.getString('settings-stay_aligned-relaxed_poses-reset_pose')}
    </Button>
  );
}
