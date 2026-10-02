import classNames from 'classnames';
import { ResetLifecycle, StepMountingStatus } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
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

const errorSteps = [
  StepMountingStatus.ERROR_NO_DATA,
  StepMountingStatus.ERROR_TIMEOUT,
];

export function StepMedia({
  status,
  done = false,
  className,
}: {
  status: StepMountingStatus | null;
  done?: boolean;
  className?: string;
}) {
  if (done) {
    return (
      <img
        className="max-h-full max-w-full object-contain"
        src="/images/user-height/done.webp"
      />
    );
  }

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
  if (errorSteps.includes(status)) {
    return (
      <img
        className="max-h-full max-w-full object-contain"
        src="/images/user-height/timeout.webp"
      />
    );
  }
  return (
    <div className="flex items-center justify-center w-full h-full min-h-48">
      <LoaderIcon slimeState={SlimeState.JUMPY} />
    </div>
  );
}

type SegmentState = 'done' | 'current' | 'todo';

function segmentState(
  index: number,
  progress: StepMountingProgress | null
): SegmentState {
  if (!progress) return 'todo';
  const { status, lifecycle } = progress;
  if (lifecycle === ResetLifecycle.DONE) return 'done';
  const stepIndex = progressSteps.indexOf(status);
  if (index < stepIndex) return 'done';
  return index === stepIndex ? 'current' : 'todo';
}

export function StepMountingSegments({
  progress,
}: {
  progress: StepMountingProgress | null;
}) {
  const allDone = progress?.lifecycle === ResetLifecycle.DONE;

  if (progress?.lifecycle === ResetLifecycle.FAILED) {
    return (
      <div className="flex w-full max-w-2xl flex-col gap-2">
        <div className="h-2 rounded-full bg-status-critical" />
        <Typography
          color="text-status-critical"
          id={`step_mounting-status-${StepMountingStatus[progress.status]}`}
        />
      </div>
    );
  }

  return (
    <div className="grid w-full max-w-2xl grid-cols-3 gap-4">
      {progressSteps.map((step, index) => {
        const state = segmentState(index, progress);
        return (
          <div key={step} className="flex flex-col gap-2">
            <div
              className={classNames('h-2 rounded-full transition-colors', {
                'bg-accent-background-20': state === 'done' && !allDone,
                'bg-status-success': state === 'done' && allDone,
                'bg-accent-background-30 animate-pulse': state === 'current',
                'bg-background-50': state === 'todo',
              })}
            />
            <Typography
              id={`step_mounting-status-${StepMountingStatus[step]}`}
              bold={state === 'current'}
              color={state === 'todo' ? 'secondary' : 'primary'}
            />
          </div>
        );
      })}
    </div>
  );
}

export function StepMountingMessage({
  status,
  lifecycle,
}: StepMountingProgress) {
  if (lifecycle === ResetLifecycle.RUNNING) {
    return (
      <Typography
        variant="section-title"
        id={`step_mounting-instructions-${StepMountingStatus[status]}`}
      />
    );
  }
  return <Typography variant="section-title" id="step_mounting-status-DONE" />;
}
