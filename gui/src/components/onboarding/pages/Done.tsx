import { useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { SlimeVRIcon } from '@/components/commons/icon/SimevrIcon';
import { Typography } from '@/components/commons/Typography';

export function DonePage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup } = useOnboarding();

  applyProgress(1);

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
      <div className="flex flex-col gap-5 items-center z-10">
        <SlimeVRIcon></SlimeVRIcon>
        <Typography variant="main-title">
          {l10n.getString('onboarding-done-title')}
        </Typography>
        <div className="flex flex-col items-center">
          <Typography color="secondary">
            {l10n.getString('onboarding-done-description')}
          </Typography>
        </div>
        <Button variant="primary" to="/" onClick={skipSetup}>
          {l10n.getString('onboarding-done-close')}
        </Button>
      </div>
    </div>
  );
}
