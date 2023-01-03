import { useTrackers } from '../../../../../hooks/tracker';
import { BodyDisplay } from '../../../../commons/BodyDisplay';
import { Button } from '../../../../commons/Button';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';
import { useLocalization } from '@fluent/react';

export function PutTrackersOnStep({ nextStep }: { nextStep: () => void }) {
  const { trackers } = useTrackers();
  const { l10n } = useLocalization();

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_mounting-put_trackers_on-title'
            )}
          </Typography>
          <div>
            <Typography color="secondary">
              {l10n.getString(
                'onboarding-automatic_mounting-put_trackers_on-description'
              )}
            </Typography>
          </div>
          <div className="flex">
            <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>
          </div>
        </div>

        <div className="flex">
          <Button variant="primary" onClick={nextStep}>
            {l10n.getString(
              'onboarding-automatic_mounting-put_trackers_on-next'
            )}
          </Button>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
        <BodyDisplay
          trackers={trackers}
          width={150}
          dotsSize={15}
          variant="dots"
        />
      </div>
    </>
  );
}
