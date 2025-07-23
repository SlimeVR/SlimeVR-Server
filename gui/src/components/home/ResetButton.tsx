import { Localized } from '@fluent/react';
import { ResetType } from 'solarxr-protocol';
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
  className,
  onReseted,
}: {
  className?: string;
  type: ResetType;
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
