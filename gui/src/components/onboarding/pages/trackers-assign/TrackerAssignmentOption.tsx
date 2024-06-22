import { useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';

export function TrackersAssignOption({
  mode,
  trackersCount,
}: {
  mode: string;
  trackersCount: number;
}) {
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-row md:gap-4 sm:gap-2 mobile:gap-4">
      <div style={{ width: '2.5rem', textAlign: 'right' }}>
        <Typography variant="main-title">
          {l10n.getString('onboarding-assign_trackers-option-amount', {
            trackersCount,
          })}
        </Typography>
      </div>
      <div className="flex flex-col text-left">
        <Typography variant="standard">
          {l10n.getString('onboarding-assign_trackers-option-label', { mode })}
        </Typography>
        <Typography variant="standard" color="secondary">
          {l10n.getString('onboarding-assign_trackers-option-description', {
            mode,
          })}
        </Typography>
      </div>
    </div>
  );
}
