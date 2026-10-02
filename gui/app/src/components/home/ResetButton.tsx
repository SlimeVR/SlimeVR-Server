import { Localized } from '@fluent/react';
import { MountingMethod, ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import classNames from 'classnames';
import { useReset, UseResetOptions } from '@/hooks/reset';
import {
  FullResetIcon,
  YawResetIcon,
} from '@/components/commons/icon/ResetIcon';
import { ReactNode } from 'react';
import { SkiIcon } from '@/components/commons/icon/SkiIcon';
import { StepIcon } from '@/components/commons/icon/StepIcon';
import { TuneIcon } from '@/components/commons/icon/TuneIcon';
import { useResetsSettings } from '@/hooks/resets-settings';
import { FootIcon } from '@/components/commons/icon/FootIcon';
import { FingersIcon } from '@/components/commons/icon/FingersIcon';
import { ToesIcon } from '@/components/commons/icon/ToesIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { MountingMethodModal } from '@/components/mounting/MountingMethodModal';
import { useMountingMethod } from '@/hooks/mounting-method';

export function ResetButtonIcon(options: UseResetOptions) {
  const { resetsSettings } = useResetsSettings();
  if (options.type === ResetType.MOUNTING && !options.group)
    options.group = 'default';

  if (options.type === ResetType.YAW) return <YawResetIcon width={18} />;
  if (options.type === ResetType.FULL) return <FullResetIcon width={18} />;
  if (options.type === ResetType.MOUNTING) {
    if (options.group === 'default') {
      switch (resetsSettings?.mountingMethod) {
        case MountingMethod.STEP:
          return <StepIcon />;
        case MountingMethod.MANUAL:
          return <TuneIcon />;
        default:
          return <SkiIcon />;
      }
    }
    if (options.group === 'feet') return <FootIcon />;
    if (options.group === 'toes') return <ToesIcon />;
    if (options.group === 'fingers') return <FingersIcon width={16} />;
  }
}

export function ResetButton({
  onClick,
  className,
  onReseted,
  children,
  onFailed,
  ...options
}: {
  onClick?: () => void;
  className?: string;
  children?: ReactNode;
  onReseted?: () => void;
  onFailed?: () => void;
} & UseResetOptions) {
  const { triggerReset, status, timer, disabled, name, error } = useReset(
    options,
    onReseted,
    onFailed
  );
  const { onClick: onResetClick, ...picker } = useMountingMethod(
    options,
    triggerReset
  );

  return (
    <>
      <Tooltip
        preferedDirection={'top'}
        disabled={!error}
        content={
          error ? (
            <Typography
              id={error}
              textAlign="text-center"
              color="text-status-critical"
            />
          ) : (
            <></>
          )
        }
      >
        <Button
          icon={<ResetButtonIcon {...options} />}
          onClick={() => {
            if (onClick) onClick();
            onResetClick();
          }}
          className={classNames(
            'border-2 py-[5px]',
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
              {children || <Localized id={name} />}
            </div>
            {status !== 'counting' || options.type === ResetType.YAW
              ? children || <Localized id={name} />
              : String(timer)}
          </div>
        </Button>
      </Tooltip>
      <MountingMethodModal
        isOpen={picker.pickerOpen}
        onClose={picker.closePicker}
        onSelect={picker.onPick}
      />
    </>
  );
}
