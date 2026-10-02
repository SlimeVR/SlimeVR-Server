import classNames from 'classnames';
import { ResetLifecycle, StepMountingStatus } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useBreakpoint } from '@/hooks/breakpoint';
import { StepMountingProgress } from '@/hooks/step-mounting';

// Order matters, be careful
const progressSteps = [
  StepMountingStatus.WAITING_FOR_MOVEMENT,
  StepMountingStatus.RECORDING,
  StepMountingStatus.PROCESSING,
];

// RECORDING keeps playing the demo since the user may still be mid-step when it starts
const videoSteps = [
  StepMountingStatus.WAITING_FOR_MOVEMENT,
  StepMountingStatus.RECORDING,
];

export function StepMedia({
  status,
  className,
}: {
  status: StepMountingStatus | null;
  className?: string;
}) {
  if (status === null || videoSteps.includes(status)) {
    return (
      <video
        autoPlay
        muted
        loop
        playsInline
        className={className ?? 'object-contain h-full w-full'}
        src="/videos/step-mounting.webm"
      />
    );
  }
  return (
    <div className="flex items-center justify-center w-full h-full min-h-48">
      <LoaderIcon slimeState={SlimeState.JUMPY} />
    </div>
  );
}

function Stepper({ status, lifecycle }: StepMountingProgress) {
  const isDone = lifecycle === ResetLifecycle.DONE;
  const isError = lifecycle === ResetLifecycle.FAILED;
  const stepIndex = progressSteps.indexOf(status);
  const progress =
    isDone || isError ? 1 : (stepIndex + 1) / progressSteps.length;
  const label = isDone ? 'DONE' : StepMountingStatus[status];

  const { isXs } = useBreakpoint('xs');

  return (
    <div className="flex flex-col gap-2 px-2">
      <div className="flex gap-2 items-center">
        <div
          className={classNames(
            'w-8 aspect-square rounded-full fill-background-10 flex items-center justify-center shrink-0',
            {
              'bg-background-70': !isDone && !isError,
              'bg-accent-background-10': isDone,
              'bg-status-critical': isError,
            }
          )}
        >
          {!isDone && !isError && (
            <Typography variant={isXs ? 'section-title' : 'standard'}>
              {stepIndex + 1}
            </Typography>
          )}
          {isDone && <CheckIcon size={12} />}
          {isError && <CrossIcon />}
        </div>
        <Typography
          id={`step_mounting-status-${label}`}
          variant={isXs ? 'section-title' : 'standard'}
        />
      </div>
      <ProgressBar
        progress={progress}
        animated
        colorClass={
          isDone
            ? 'bg-status-success'
            : isError
              ? 'bg-status-critical'
              : undefined
        }
      />
    </div>
  );
}

export function StepMountingStatusContent({
  status,
  lifecycle,
  showMedia = true,
}: StepMountingProgress & { showMedia?: boolean }) {
  const isRunning = lifecycle === ResetLifecycle.RUNNING;

  return (
    <div className="flex flex-col gap-4">
      <Stepper status={status} lifecycle={lifecycle} />
      {isRunning && (
        <>
          {showMedia && (
            <div className="h-48">
              <StepMedia status={status} />
            </div>
          )}
          <Typography
            id={`step_mounting-instructions-${StepMountingStatus[status]}`}
          />
        </>
      )}
    </div>
  );
}
