import { useTranslation } from 'react-i18next';
import { useAutobone } from '../../../../../hooks/autobone';
import { Button } from '../../../../commons/Button';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';

export function StartRecording({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { t } = useTranslation();
  const { startRecording } = useAutobone();

  const start = () => {
    nextStep();
    startRecording();
  };

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {t('onboarding.automatic-proportions.start-recording.title')}
          </Typography>
          <div>
            <Typography color="secondary">
              {t(
                'onboarding.automatic-proportions.start-recording.description'
              )}
            </Typography>
          </div>
          <div className="flex">
            <TipBox>{t('tips.find-tracker')}</TipBox>
          </div>
        </div>

        <div className="flex gap-3">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
            onClick={prevStep}
          >
            {t('onboarding.automatic-proportions.prev-step')}
          </Button>
          <Button variant="primary" onClick={start}>
            {t('onboarding.automatic-proportions.start-recording.next')}
          </Button>
        </div>
      </div>
    </>
  );
}
