import { ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { FullResetExamples } from '@/components/commons/FullResetExamples';
import { ResetButton } from '@/components/home/ResetButton';
import { Localized, useLocalization } from '@fluent/react';

export function PreparationStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();

  return (
    <div className="flex mobile:flex-col items-center w-full">
      <div className="flex flex-col flex-grow justify-between">
        <div className="flex flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString('onboarding-automatic_mounting-preparation-title')}
          </Typography>
          <div>
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
        </div>
        <FullResetExamples
          className="py-4"
          tileClassName="bg-background-70 max-h-72"
        />
        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          <ResetButton type={ResetType.FULL} onReseted={nextStep} />
        </div>
      </div>
    </div>
  );
}
