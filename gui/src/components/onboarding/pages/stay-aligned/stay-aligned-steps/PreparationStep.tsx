import { Button } from '@/components/commons/Button';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { ResetButton } from '@/components/home/ResetButton';
import { Localized } from '@fluent/react';
import { ResetType } from 'solarxr-protocol';

export function PreparationStep({
  nextStep,
  prevStep,
}: VerticalStepComponentProps) {
  return (
    <div className="flex flex-col flex-grow justify-between py-2 gap-2">
      <div className="flex flex-col gap-1">
        <Localized id="onboarding-automatic_mounting-preparation-v2-step-0">
          <Typography />
        </Localized>
        <Localized id="onboarding-automatic_mounting-preparation-v2-step-1">
          <Typography />
        </Localized>
        <Localized id="onboarding-automatic_mounting-preparation-v2-step-2">
          <Typography />
        </Localized>
      </div>
      <Localized id="onboarding-stay_aligned-preparation-tip">
        <TipBox>TIP</TipBox>
      </Localized>
      <div className="grid grid-cols-3 py-4 gap-2">
        <div className="flex flex-col bg-background-60 rounded-md relative">
          <CheckIcon className="md:w-20 sm:w-10 w-6 h-auto absolute top-2 right-2 fill-status-success" />
          <img src="/images/reset/FullResetPose.webp" alt="Reset position" />
        </div>
        <div className="flex flex-col bg-background-60 rounded-md relative">
          <CheckIcon className="md:w-20 sm:w-10 w-6 h-auto absolute top-2 right-2 fill-status-success" />
          <img
            src="/images/reset/FullResetPoseSide.webp"
            alt="Reset position side"
          />
        </div>
        <div className="flex flex-col bg-background-60 rounded-md relative">
          <CrossIcon className="md:w-20 sm:w-10 w-6 h-auto absolute top-2 right-2 fill-status-critical" />
          <img
            src="/images/reset/FullResetPoseWrong.webp"
            alt="Reset position wrong"
          />
        </div>
      </div>
      <div className="flex gap-3 justify-between">
        <Localized id="onboarding-stay_aligned-previous_step">
          <Button variant={'secondary'} onClick={prevStep} />
        </Localized>

        <ResetButton type={ResetType.Full} onReseted={nextStep} />
      </div>
    </div>
  );
}
