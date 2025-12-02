import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { ResetType } from 'solarxr-protocol';
import { ResetButton } from '@/components/home/ResetButton';
import { useLocalization } from '@fluent/react';
import { useBreakpoint } from '@/hooks/breakpoint';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';

export function VerifyMountingStep({
  nextStep,
  prevStep,
}: VerticalStepComponentProps) {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();
  return (
    <div className="flex flex-col flex-grow justify-between py-2 gap-2">
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <div className="flex flex-col gap-2">
            <Typography>
              {l10n.getString(
                'onboarding-automatic_mounting-mounting_reset-step-0'
              )}
            </Typography>
            <Typography>
              {l10n.getString(
                'onboarding-automatic_mounting-mounting_reset-step-1'
              )}
            </Typography>
          </div>
        </div>

        {isMobile && (
          <div className="flex flex-col items-center fill-background-50 justify-center">
            <img
              src="/images/mounting-reset-pose.webp"
              width={450}
              alt="mounting reset ski pose"
            />
          </div>
        )}

        {!isMobile && (
          <div className="flex flex-col pt-1 items-center fill-background-50 justify-center">
            <img
              src="/images/mounting-reset-pose.webp"
              width={600}
              alt="mounting reset ski pose"
            />
          </div>
        )}
        <div className="flex gap-3 justify-between">
          <Button variant={'secondary'} onClick={prevStep}>
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          <ResetButton
            type={ResetType.Mounting}
            group="default"
            onReseted={nextStep}
          />
        </div>
      </div>
    </div>
  );
}
