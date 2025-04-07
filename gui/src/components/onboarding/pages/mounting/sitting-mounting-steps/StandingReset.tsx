import { ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { ResetButton } from '@/components/home/ResetButton';
import { useLocalization } from '@fluent/react';

export function StandingResetStep({
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
    <div className="flex mobile:flex-col">
      <div className="flex flex-grow flex-col gap-4 max-w-sm">
        <Typography variant="main-title" bold>
          {l10n.getString('onboarding-automatic_mounting-standing_reset-title')}
        </Typography>
        <div className="flex flex-col gap-2">
          <Typography color="secondary">
            {l10n.getString(
              'onboarding-automatic_mounting-standing_reset-step-0'
            )}
          </Typography>
          <Typography color="secondary">
            {l10n.getString(
              'onboarding-automatic_mounting-standing_reset-step-1'
            )}
          </Typography>
          <Typography color="secondary">
            {l10n.getString(
              'onboarding-automatic_mounting-standing_reset-step-2'
            )}
          </Typography>
        </div>
        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          <ResetButton
            variant="small"
            type={ResetType.Full}
            onReseted={nextStep}
          ></ResetButton>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img
          src="/images/reset-standing-pose.webp"
          width={200}
          alt="Reset position"
        />
      </div>
    </div>
  );
}
