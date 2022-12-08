import { useTranslation } from 'react-i18next';
import { ResetType } from 'solarxr-protocol';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';
import { ResetButton } from '../../../../home/ResetButton';

export function MountingResetStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { t } = useTranslation();

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {t('onboarding.automatic-mounting.mounting-reset.title')}
          </Typography>
          <div className="flex flex-col gap-2">
            <Typography color="secondary">
              {t('onboarding.automatic-mounting.mounting-reset.step.0')}
            </Typography>
            <Typography color="secondary">
              {t('onboarding.automatic-mounting.mounting-reset.step.1')}
            </Typography>
          </div>
        </div>

        <div className="flex gap-3">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
            onClick={prevStep}
          >
            {t('onboarding.automatic-mounting.prev-step')}
          </Button>
          <ResetButton
            variant="small"
            type={ResetType.Mounting}
            onReseted={nextStep}
          ></ResetButton>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img src="/images/mounting-reset-pose.png" width={105} />
      </div>
    </>
  );
}
