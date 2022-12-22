import { useTranslation } from 'react-i18next';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { SlimeVRIcon } from '../../commons/icon/SimevrIcon';
import { Typography } from '../../commons/Typography';

export function DonePage() {
  const { t } = useTranslation();
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(1);

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
      <div className="flex flex-col gap-5 items-center z-10">
        <SlimeVRIcon></SlimeVRIcon>
        <Typography variant="main-title">
          {t('onboarding-done-title')}
        </Typography>
        <div className="flex flex-col items-center">
          <Typography color="secondary">
            {t('onboarding-done-description')}
          </Typography>
        </div>
        <Button variant="primary" to="/" onClick={skipSetup}>
          {t('onboarding-done-close')}
        </Button>
      </div>
    </div>
  );
}
