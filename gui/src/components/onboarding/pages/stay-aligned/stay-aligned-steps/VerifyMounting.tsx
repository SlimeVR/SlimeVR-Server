import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';

export function VerifyMountingStep({
  nextStep,
  resetSteps,
  variant,
}: {
  nextStep: () => void;
  resetSteps: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();

  return (
    <div className="flex mobile:flex-col">
      <div className="flex flex-grow flex-col gap-4 max-w-sm">
        <Typography variant="main-title" bold>
          {l10n.getString('onboarding-stay_aligned-verify_mounting-title')}
        </Typography>
        <div className="flex flex-col gap-2">
          <Typography color="secondary">
            {l10n.getString('onboarding-stay_aligned-verify_mounting-step-0')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-stay_aligned-verify_mounting-step-1')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-stay_aligned-verify_mounting-step-2')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-stay_aligned-verify_mounting-step-3')}
          </Typography>
        </div>
        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={resetSteps}
          >
            {l10n.getString('onboarding-stay_aligned-restart')}
          </Button>
          <Button variant="primary" onClick={nextStep}>
            {l10n.getString('onboarding-stay_aligned-next_step')}
          </Button>
        </div>
      </div>
    </div>
  );
}
