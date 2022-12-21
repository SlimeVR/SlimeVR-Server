import { useTranslation } from 'react-i18next';
import { NavLink } from 'react-router-dom';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { SlimeVRIcon } from '../../commons/icon/SimevrIcon';
import { LangSelector } from '../../commons/LangSelector';
import { Typography } from '../../commons/Typography';

export function HomePage() {
  const { t } = useTranslation();
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(0.1);

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
      <div className="flex flex-col gap-5 items-center z-10">
        <SlimeVRIcon></SlimeVRIcon>
        <Typography variant="main-title">
          {t('onboarding-home-title')}
        </Typography>
        <div className="flex flex-col items-center">
          <Typography color="secondary">
            {t('onboarding-home-description-p0')}
          </Typography>
          <Typography color="secondary">
            {t('onboarding-home-description-p1')}
          </Typography>
        </div>
        <Button variant="primary" to="/onboarding/wifi-creds">
          {t('onboarding-home-start')}
        </Button>
        <NavLink to="/" onClick={skipSetup}>
          <Typography color="secondary">{t('onboarding-skip')}</Typography>
        </NavLink>
      </div>
      <div className="absolute right-4 bottom-4 z-50">
        <LangSelector />
      </div>
      <div
        className="absolute bg-accent-background-50 w-full rounded-full"
        style={{
          bottom: 'calc(-300vw / 1.04)',
          height: '300vw',
          width: '300vw',
        }}
      ></div>
      <img
        className="absolute"
        src="/images/slime-girl.png"
        style={{
          width: '40%',
          maxWidth: 800,
          bottom: '1%',
          left: '9%',
        }}
      />
      <img
        className="absolute"
        src="/images/slimes.png"
        style={{
          width: '40%',
          maxWidth: 800,
          bottom: '1%',
          right: '9%',
        }}
      />
    </div>
  );
}
