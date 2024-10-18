import { useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { SlimeVRIcon } from '@/components/commons/icon/SimevrIcon';
import { LangSelector } from '@/components/commons/LangSelector';
import { Typography } from '@/components/commons/Typography';

export function HomePage() {
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();

  applyProgress(0.1);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center px-4">
        <div className="flex flex-col gap-5 items-center z-10 scale-150 mb-20">
          <SlimeVRIcon></SlimeVRIcon>
          <Typography variant="mobile-title">
            {l10n.getString('onboarding-home')}
          </Typography>
          <Button variant="primary" to="/onboarding/wifi-creds">
            {l10n.getString('onboarding-home-start')}
          </Button>
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
          src="/images/slime-girl.webp"
          style={{
            width: '35%',
            maxWidth: 800,
            bottom: '1%',
            left: '9%',
          }}
        />
        <img
          className="absolute"
          src="/images/slimes.webp"
          style={{
            width: '35%',
            maxWidth: 800,
            bottom: '1%',
            right: '9%',
          }}
        />
      </div>
    </>
  );
}
