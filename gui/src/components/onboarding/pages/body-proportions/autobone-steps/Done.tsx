import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { Button } from '@/components/commons/Button';

export function DoneStep({ variant }: { variant: 'onboarding' | 'alone' }) {
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          {l10n.getString('onboarding-automatic_proportions-done-title')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-automatic_proportions-done-description')}
        </Typography>
      </div>

      <div className="flex">
        {variant === 'onboarding' && (
          <Button variant="primary" to="/onboarding/done">
            {l10n.getString('onboarding-continue')}
          </Button>
        )}
      </div>
    </div>
  );
}
