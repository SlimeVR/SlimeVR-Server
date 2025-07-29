import { Localized } from '@fluent/react';
import { BodyPart, ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import classNames from 'classnames';
import { useReset } from '@/hooks/reset';
import {
  FullResetIcon,
  YawResetIcon,
} from '@/components/commons/icon/ResetIcon';
import { SkiIcon } from '@/components/commons/icon/SkiIcon';
import { useMemo } from 'react';

export function ResetButton({
  type,
  bodyPartsToReset = 'default',
  className,
  onReseted,
}: {
  className?: string;
  type: ResetType;
  bodyPartsToReset?: 'default' | 'feet' | 'fingers';
  onReseted?: () => void;
}) {
  const { triggerReset, status, timer, disabled, name } = useReset(
    type,
    onReseted
  );

  const icon = useMemo(() => {
    switch (type) {
      case ResetType.Yaw:
        return <YawResetIcon width={18} />;
      case ResetType.Mounting:
        return <SkiIcon size={18} />;
    }
    return <FullResetIcon width={18} />;
  }, [type]);

  // const feetBodyParts = [BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT];
  // const fingerBodyParts = [
  //   BodyPart.LEFT_THUMB_METACARPAL,
  //   BodyPart.LEFT_THUMB_PROXIMAL,
  //   BodyPart.LEFT_THUMB_DISTAL,
  //   BodyPart.LEFT_INDEX_PROXIMAL,
  //   BodyPart.LEFT_INDEX_INTERMEDIATE,
  //   BodyPart.LEFT_INDEX_DISTAL,
  //   BodyPart.LEFT_MIDDLE_PROXIMAL,
  //   BodyPart.LEFT_MIDDLE_INTERMEDIATE,
  //   BodyPart.LEFT_MIDDLE_DISTAL,
  //   BodyPart.LEFT_RING_PROXIMAL,
  //   BodyPart.LEFT_RING_INTERMEDIATE,
  //   BodyPart.LEFT_RING_DISTAL,
  //   BodyPart.LEFT_LITTLE_PROXIMAL,
  //   BodyPart.LEFT_LITTLE_INTERMEDIATE,
  //   BodyPart.LEFT_LITTLE_DISTAL,
  //   BodyPart.RIGHT_THUMB_METACARPAL,
  //   BodyPart.RIGHT_THUMB_PROXIMAL,
  //   BodyPart.RIGHT_THUMB_DISTAL,
  //   BodyPart.RIGHT_INDEX_PROXIMAL,
  //   BodyPart.RIGHT_INDEX_INTERMEDIATE,
  //   BodyPart.RIGHT_INDEX_DISTAL,
  //   BodyPart.RIGHT_MIDDLE_PROXIMAL,
  //   BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
  //   BodyPart.RIGHT_MIDDLE_DISTAL,
  //   BodyPart.RIGHT_RING_PROXIMAL,
  //   BodyPart.RIGHT_RING_INTERMEDIATE,
  //   BodyPart.RIGHT_RING_DISTAL,
  //   BodyPart.RIGHT_LITTLE_PROXIMAL,
  //   BodyPart.RIGHT_LITTLE_INTERMEDIATE,
  //   BodyPart.RIGHT_LITTLE_DISTAL,
  // ];

  // const reset = () => {
  //   const req = new ResetRequestT();
  //   req.resetType = type;
  //   if (bodyPartsToReset === 'default') {
  //     // Default (server handles it)
  //     req.bodyParts = [];
  //   } else if (bodyPartsToReset === 'feet') {
  //     // Feet
  //     req.bodyParts = feetBodyParts;
  //   } else if (bodyPartsToReset === 'fingers') {
  //     // Fingers
  //     req.bodyParts = fingerBodyParts;
  //   }
  //   sendRPCPacket(RpcMessage.ResetRequest, req);
  // };

  // const text = useMemo(() => {
  //   switch (type) {
  //     case ResetType.Yaw:
  //       return l10n.getString(
  //         'reset-yaw' +
  //           (bodyPartsToReset !== 'default' ? '-' + bodyPartsToReset : '')
  //       );
  //     case ResetType.Mounting:
  //       return l10n.getString(
  //         'reset-mounting' +
  //           (bodyPartsToReset !== 'default' ? '-' + bodyPartsToReset : '')
  //       );
  //     case ResetType.Full:
  //       return l10n.getString(
  //         'reset-full' +
  //           (bodyPartsToReset !== 'default' ? '-' + bodyPartsToReset : '')
  //       );
  //   }
  // }, [type, bodyPartsToReset]);

  return (
    <Button
      icon={icon}
      onClick={triggerReset}
      className={classNames(
        'border-2 m-1',
        status === 'finished'
          ? 'border-status-success'
          : 'transition-[border-color] duration-500 ease-in-out border-transparent',
        className
      )}
      variant="primary"
      disabled={disabled}
    >
      <div className="flex flex-col">
        <div className="opacity-0 h-0">
          <Localized id={name}></Localized>
        </div>
        {status !== 'counting' || type === ResetType.Yaw ? (
          <Localized id={name}></Localized>
        ) : (
          String(timer)
        )}
      </div>
    </Button>
  );
}
