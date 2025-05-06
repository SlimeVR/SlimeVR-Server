import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { useLocalization } from '@fluent/react';

export function DoneStep({
  resetSteps,
  variant,
}: {
  resetSteps: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          {l10n.getString('onboarding-stay_aligned-done-title')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-stay_aligned-done-description')}
        </Typography>
      </div>

      <div className="flex gap-3">
        <Button
          variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
          onClick={resetSteps}
        >
          {l10n.getString('onboarding-stay_aligned-restart')}
        </Button>
        <Button
          variant="primary"
          to="/settings/trackers"
          state={{ scrollTo: 'stayaligned' }}
        >
          {l10n.getString('onboarding-stay_aligned-done')}
        </Button>
      </div>

      <SkeletonVisualizerWidget />
    </div>
  );
}
