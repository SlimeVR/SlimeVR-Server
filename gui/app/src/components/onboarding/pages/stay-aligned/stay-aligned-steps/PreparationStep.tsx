import { Button } from '@/components/commons/Button';
import { FullResetExamples } from '@/components/commons/FullResetExamples';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { ResetButton } from '@/components/home/ResetButton';
import { Localized } from '@fluent/react';
import { ResetAvailability, ResetType } from 'solarxr-protocol';
import { useAtomValue } from 'jotai';
import { serverGuardsAtom } from '@/store/app-store';

export function PreparationStep({
  nextStep,
  prevStep,
  isActive,
}: VerticalStepComponentProps) {
  const serverGuards = useAtomValue(serverGuardsAtom);
  return (
    <div className="flex flex-col flex-grow justify-between py-2 gap-2">
      {serverGuards?.mountingReset === ResetAvailability.NEEDS_FULL_RESET && (
        <>
          <div className="flex flex-col gap-1">
            <Typography id="onboarding-automatic_mounting-preparation-v2-step-0" />
            <Typography id="onboarding-automatic_mounting-preparation-v2-step-1" />
            <Typography id="onboarding-automatic_mounting-preparation-v2-step-2" />
          </div>
          <Localized id="onboarding-stay_aligned-preparation-tip">
            <TipBox>TIP</TipBox>
          </Localized>
          <FullResetExamples
            className="py-4"
            tileClassName="bg-background-60 max-h-72"
          />
          <div className="flex gap-3 justify-between">
            <Button
              variant={'secondary'}
              onClick={prevStep}
              id="onboarding-stay_aligned-previous_step"
            />
            <ResetButton
              type={ResetType.FULL}
              onReseted={() => {
                if (isActive) {
                  nextStep();
                }
              }}
            />
          </div>
        </>
      )}
      {serverGuards &&
        serverGuards.mountingReset !== ResetAvailability.NEEDS_FULL_RESET && (
          <div className="flex flex-col gap-4">
            <Typography id="onboarding-automatic_mounting-preparation-v2-done" />
            <div className="flex gap-3 justify-between">
              <Button
                variant={'secondary'}
                onClick={prevStep}
                id="onboarding-stay_aligned-previous_step"
              />
              <Button
                variant={'primary'}
                onClick={nextStep}
                id="onboarding-stay_aligned-next_step"
              />
            </div>
          </div>
        )}
    </div>
  );
}
