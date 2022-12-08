import { useTranslation } from 'react-i18next';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';

export function DoneStep({
  resetSteps,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  resetSteps: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          {t('onboarding.automatic-mounting.done.title')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding.automatic-mounting.done.description')}
        </Typography>
      </div>
      {/* <Button variant="primary">Continue to next step</Button> */}

      <div className="flex gap-3">
        <Button
          variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
          onClick={resetSteps}
        >
          {t('onboarding.automatic-mounting.done.restart')}
        </Button>
      </div>
    </div>
  );
}
