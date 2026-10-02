import { useEffect, useState } from 'react';
import { useAtomValue } from 'jotai';
import { ResetLifecycle, ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { ResetButton } from '@/components/home/ResetButton';
import {
  StepMedia,
  StepMountingStatusContent,
} from '@/components/mounting/StepMountingStatus';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useCancelReset } from '@/hooks/reset';
import { useStepMountingProgress } from '@/hooks/step-mounting';
import { resetStatusAtom } from '@/store/app-store';
import { useLocalization } from '@fluent/react';

export function StepMountingStep({
  nextStep,
  prevStep,
  variant,
  active,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
  active: boolean;
}) {
  const { l10n } = useLocalization();
  const { isMobile } = useBreakpoint('mobile');
  const cancel = useCancelReset();
  const resetStatus = useAtomValue(resetStatusAtom);
  const progress = useStepMountingProgress();
  // A step mounting reported before this step opened belongs to an earlier visit
  const [previousStatus, setPreviousStatus] = useState(resetStatus);

  // The stepper keeps every step mounted, only the active one shows the progress
  useEffect(() => {
    if (active) setPreviousStatus(resetStatus);
  }, [active]);

  const current = resetStatus !== previousStatus ? progress : null;

  const media = (
    <div className="flex items-center justify-center fill-background-50">
      <StepMedia
        status={current?.status ?? null}
        className={isMobile ? 'h-72 object-contain' : 'h-96 object-contain'}
      />
    </div>
  );

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography
            variant="main-title"
            bold
            id="onboarding-step_mounting-step-title"
          />
          {current ? (
            <StepMountingStatusContent {...current} showMedia={false} />
          ) : (
            <div className="flex flex-col gap-2">
              <Typography id="onboarding-step_mounting-step-0" />
              <Typography id="onboarding-step_mounting-step-1" />
            </div>
          )}
        </div>

        {isMobile && media}

        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          {current?.lifecycle === ResetLifecycle.RUNNING ? (
            <Button
              variant="secondary"
              onClick={cancel}
              id="step_mounting-cancel"
            />
          ) : current?.lifecycle === ResetLifecycle.DONE ? (
            <Button
              variant="primary"
              onClick={nextStep}
              id="step_mounting-next"
            />
          ) : (
            <ResetButton type={ResetType.MOUNTING} group="default" />
          )}
        </div>
      </div>
      {!isMobile && <div className="pt-1">{media}</div>}
    </>
  );
}
