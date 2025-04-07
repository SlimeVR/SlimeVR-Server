import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';

export function PreparationStep({
  nextStep,
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
          {l10n.getString('onboarding-automatic_mounting-preparation-title')}
        </Typography>
        <div className="flex flex-col gap-2">
          <Typography color="secondary">
            {l10n.getString('onboarding-automatic_mounting-preparation-step-0')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-automatic_mounting-preparation-step-1')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-automatic_mounting-preparation-step-2')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-automatic_mounting-preparation-step-3')}
          </Typography>
        </div>
        <div className="flex gap-3 mobile:justify-between">
          <Button variant="primary" onClick={nextStep}>
            {l10n.getString('onboarding-automatic_mounting-next')}
          </Button>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img
          src="/images/reset-sitting-pose.webp"
          width={200}
          alt="Reset position"
        />
      </div>
    </div>
  );
}
