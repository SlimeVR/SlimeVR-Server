import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { useLocalization } from '@fluent/react';

export function DoneStep({ goTo }: VerticalStepComponentProps) {
  const { l10n } = useLocalization();

  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center pt-10">
        <Typography variant="main-title">
          {l10n.getString('onboarding-stay_aligned-done-description')}
        </Typography>
        <Typography color="secondary">
          Everything is now Setup, you may restart the process if you want to
          re-calibrate the poses
        </Typography>
      </div>
      <div className="flex gap-3 justify-between">
        <Button variant={'secondary'} onClick={() => goTo('start')}>
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
    </div>
  );
}
