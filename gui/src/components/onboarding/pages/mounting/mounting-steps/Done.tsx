import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';

export function DoneStep({
  resetSteps,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  resetSteps: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          {l10n.getString('onboarding-automatic_mounting-done-title')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-automatic_mounting-done-description')}
        </Typography>
      </div>

      <div className="flex gap-3">
        <Button
          variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
          onClick={resetSteps}
        >
          {l10n.getString('onboarding-automatic_mounting-done-restart')}
        </Button>

        {variant === 'onboarding' && (
          <Button variant="primary" to="/onboarding/reset-tutorial">
            {l10n.getString('onboarding-automatic_mounting-next')}
          </Button>
        )}
      </div>
    </div>
  );
}
