import { useState } from 'react';
import { useAtomValue } from 'jotai';
import { MountingMethod, ResetLifecycle, ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { ResetButton } from '@/components/home/ResetButton';
import {
  StepMedia,
  StepMountingMessage,
  StepMountingSegments,
} from '@/components/mounting/StepMountingStatus';
import { StepMountingRetryButton } from '@/components/mounting/StepMountingRetryButton';
import { useCancelReset } from '@/hooks/reset';
import { useStepMountingProgress } from '@/hooks/step-mounting';
import { resetStatusAtom } from '@/store/app-store';
import {
  MountingFlowLayout,
  MountingPhaseProps,
  useAutoStartMounting,
  useCancelMountingOnLeave,
} from '@/components/onboarding/pages/mounting/MountingFlow';

export function StepMountingPhase({
  flow,
  back,
  next,
  advanced,
}: MountingPhaseProps) {
  useAutoStartMounting(advanced, MountingMethod.STEP);
  const cancel = useCancelReset();
  useCancelMountingOnLeave();
  const resetStatus = useAtomValue(resetStatusAtom);
  const progress = useStepMountingProgress();
  // A step mounting reported before this phase opened belongs to an earlier visit
  const [previousStatus] = useState(resetStatus);

  // A canceled run goes back to the idle instructions
  const current =
    resetStatus !== previousStatus &&
    progress?.lifecycle !== ResetLifecycle.CANCELED
      ? progress
      : null;

  return (
    <MountingFlowLayout
      flow={flow}
      body={
        current && current.lifecycle !== ResetLifecycle.FAILED ? (
          <StepMountingMessage {...current} />
        ) : (
          <>
            <Typography id="onboarding-step_mounting-step-0" />
            <Typography id="onboarding-step_mounting-step-1" />
          </>
        )
      }
      media={
        <div className="flex h-full w-full flex-col items-center gap-6">
          <div className="flex min-h-0 flex-1 items-center justify-center">
            <StepMedia
              status={current?.status ?? null}
              done={current?.lifecycle === ResetLifecycle.DONE}
              className="h-full object-contain"
            />
          </div>
          <StepMountingSegments progress={current} />
        </div>
      }
      actions={
        <>
          {back}
          {current?.lifecycle === ResetLifecycle.RUNNING ? (
            <Button
              variant="secondary"
              onClick={cancel}
              id="step_mounting-cancel"
            />
          ) : current?.lifecycle === ResetLifecycle.FAILED ? (
            <StepMountingRetryButton />
          ) : current?.lifecycle === ResetLifecycle.DONE ? (
            <Button variant="primary" onClick={next} id="step_mounting-next" />
          ) : (
            <ResetButton type={ResetType.MOUNTING} group="default" />
          )}
        </>
      }
    />
  );
}
