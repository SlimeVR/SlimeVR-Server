import { t } from 'i18next';
import { Typography } from '../../../../commons/Typography';

export function DoneStep() {
  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          {t('onboarding-automatic_proportions-done-title')}
        </Typography>
        <Typography color="secondary">
          {t('onboarding-automatic_proportions-done-description')}
        </Typography>
      </div>
    </div>
  );
}
