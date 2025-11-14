import { ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { ResetButton } from '@/components/home/ResetButton';
import { Localized, useLocalization } from '@fluent/react';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';

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
        <div className="grid grid-cols-3 py-4 gap-2">
          <div className="flex flex-col bg-background-70 rounded-md relative max-h-64">
            <CheckIcon className="md:w-14 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success" />
            <img
              src="/images/reset/FullResetPose.webp"
              className="h-full object-contain"
              alt="Reset position"
            />
          </div>
          <div className="flex flex-col bg-background-70 rounded-md relative max-h-64">
            <CheckIcon className="md:w-14 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success" />
            <img
              src="/images/reset/FullResetPoseSide.webp"
              className="h-full object-contain"
              alt="Reset position side"
            />
          </div>
          <div className="flex flex-col bg-background-70 rounded-md relative max-h-64">
            <CrossIcon className="md:w-14 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-critical" />
            <img
              src="/images/reset/FullResetPoseWrong.webp"
              className="h-full object-contain"
              alt="Reset position wrong"
            />
          </div>
        </div>
        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          <ResetButton type={ResetType.Full} onReseted={nextStep} />
        </div>
      </div>
    </div>
  );
}
