import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { Localized } from '@fluent/react';

export function VerifyMountingStep({
  nextStep,
  prevStep,
}: VerticalStepComponentProps) {
  return (
    <div className="flex flex-grow flex-col gap-4">
      <div className="flex flex-col gap-2">
        <Localized id="onboarding-stay_aligned-verify_mounting-step-0">
          <Typography color="secondary" />
        </Localized>
        <Localized id="onboarding-stay_aligned-verify_mounting-step-1">
          <Typography color="secondary" />
        </Localized>
        <Localized id="onboarding-stay_aligned-verify_mounting-step-2">
          <Typography color="secondary" />
        </Localized>
        <Localized id="onboarding-stay_aligned-verify_mounting-step-3">
          <Typography color="secondary" />
        </Localized>
      </div>
      <div className="flex gap-3 justify-between">
        <Localized id="onboarding-stay_aligned-previous_step">
          <Button variant="secondary" onClick={prevStep}></Button>
        </Localized>

        <div className="flex gap-2">
          <Localized id="onboarding-stay_aligned-verify_mounting-redo_mounting">
            <Button
              variant={'secondary'}
              to="/onboarding/mounting/choose"
            ></Button>
          </Localized>
          <Localized id="onboarding-stay_aligned-next_step">
            <Button variant="primary" onClick={nextStep}></Button>
          </Localized>
        </div>
      </div>
    </div>
  );
}
