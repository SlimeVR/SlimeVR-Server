import { MountingMethod, ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { useReset } from '@/hooks/reset';

// `large` fills the width and uses the bigger label, to be easy to hit with a controller
export function StepMountingRetryButton({
  large = false,
}: {
  large?: boolean;
}) {
  const { triggerReset, disabled, error } = useReset({
    type: ResetType.MOUNTING,
    group: 'default',
  });

  return (
    <Tooltip
      preferedDirection="top"
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
        variant="primary"
        className={large ? 'w-full !min-h-[64px]' : undefined}
        disabled={disabled}
        onClick={() => triggerReset(MountingMethod.STEP)}
        id={large ? undefined : 'step_mounting-retry'}
      >
        {large && (
          <Typography variant="vr-accessible" id="step_mounting-retry" />
        )}
      </Button>
    </Tooltip>
  );
}
