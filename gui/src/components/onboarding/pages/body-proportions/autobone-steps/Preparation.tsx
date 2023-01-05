import { Button } from '../../../../commons/Button';
import { FromtOfChairIcon } from '../../../../commons/icon/FrontOfChair';
import { Typography } from '../../../../commons/Typography';
import { useLocalization } from '@fluent/react';

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
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_proportions-preparation-title'
            )}
          </Typography>
          <div>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-automatic_proportions-preparation-description'
              )}
            </Typography>
          </div>
        </div>

        <div className="flex gap-3">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_proportions-prev_step')}
          </Button>
          <Button variant="primary" onClick={nextStep}>
            {l10n.getString(
              'onboarding-automatic_proportions-preparation-next'
            )}
          </Button>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <FromtOfChairIcon width={180} />
      </div>
    </>
  );
}
