import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useAutobone } from '../../../../../hooks/autobone';
import { ProgressBar } from '../../../../commons/ProgressBar';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';

export function Recording({ nextStep }: { nextStep: () => void }) {
  const { t } = useTranslation();
  const { progress, hasCalibration, hasRecording } = useAutobone();

  useEffect(() => {
    if (progress === 1 && hasCalibration) {
      nextStep();
    }
  }, [progress, hasCalibration]);

  console.log(t('onboarding-automatic_proportions-recording-steps'));

  return (
    <div className="flex flex-col items-center w-full justify-between">
      <div className="flex gap-1 flex-col justify-center items-center">
        <div className="flex text-status-critical justify-center items-center gap-1">
          <div className="w-2 h-2 rounded-lg bg-status-critical"></div>
          <Typography color="text-status-critical">
            {t('onboarding-automatic_proportions-recording-title')}
          </Typography>
        </div>
        <Typography variant="section-title">
          {t('onboarding-automatic_proportions-recording-description-p0')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-recording-description-p1')}
        </Typography>
      </div>
      <div>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-recording-steps-0')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-recording-steps-1')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-recording-steps-2')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-recording-steps-3')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-recording-steps-4')}
        </Typography>
      </div>
      <div className="flex">
        <TipBox>{t('tips-do_not_move_heels')}</TipBox>
      </div>
      <div className="flex flex-col gap-2 items-center w-full max-w-[150px]">
        <ProgressBar progress={progress} height={2}></ProgressBar>
        <Typography color="secondary">
          {!hasCalibration && hasRecording
            ? t('onboarding-automatic_proportions-recording-processing')
            : t('onboarding-automatic_proportions-recording-timer', {
                time: 15,
              })}
        </Typography>
      </div>
    </div>
  );
}
