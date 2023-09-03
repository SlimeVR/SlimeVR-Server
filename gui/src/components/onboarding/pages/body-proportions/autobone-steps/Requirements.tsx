import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';

export function RequirementsStep({
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
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_proportions-requirements-title'
            )}
          </Typography>
          <ul className="list-disc mobile:px-4">
            <>
              {l10n
                .getString(
                  'onboarding-automatic_proportions-requirements-description'
                )
                .split('\n')
                .map((line, i) => (
                  <li key={i}>
                    <Typography color="secondary">{line}</Typography>
                  </li>
                ))}
            </>
          </ul>
        </div>

        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_proportions-prev_step')}
          </Button>
          <Button variant="primary" onClick={nextStep}>
            {l10n.getString(
              'onboarding-automatic_proportions-requirements-next'
            )}
          </Button>
        </div>
      </div>
    </>
  );
}
