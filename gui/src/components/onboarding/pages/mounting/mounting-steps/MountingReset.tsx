import { ResetType } from 'solarxr-protocol';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';
import { ResetButton } from '../../../../home/ResetButton';
import { useLocalization } from '@fluent/react';

export function MountingResetStep({
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
              'onboarding-automatic_mounting-mounting_reset-title'
            )}
          </Typography>
          <div className="flex flex-col gap-2">
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-automatic_mounting-mounting_reset-step-0'
              )}
            </Typography>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-automatic_mounting-mounting_reset-step-1'
              )}
            </Typography>
          </div>
        </div>

        <div className="flex gap-3">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          <ResetButton
            variant="small"
            type={ResetType.Mounting}
            onReseted={nextStep}
          ></ResetButton>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img
          src="/images/mounting-reset-pose.png"
          width={125}
          alt="mounting reset ski pose"
        />
      </div>
    </>
  );
}
