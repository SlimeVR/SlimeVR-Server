import { useTranslation } from 'react-i18next';
import { useTrackers } from '../../../../../hooks/tracker';
import { BodyDisplay } from '../../../../commons/BodyDisplay';
import { Button } from '../../../../commons/Button';
import { TipBox } from '../../../../commons/TipBox';
import { Typography } from '../../../../commons/Typography';

export function PutTrackersOnStep({ nextStep }: { nextStep: () => void }) {
  const { trackers } = useTrackers();
  const { t } = useTranslation();

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {t('onboarding.automatic-mounting.put-trackers-on.title')}
          </Typography>
          <div>
            <Typography color="secondary">
              {t('onboarding.automatic-mounting.put-trackers-on.description')}
            </Typography>
          </div>
          <div className="flex">
            <TipBox>{t('tips.find-tracker')}</TipBox>
          </div>
        </div>

        <div className="flex">
          <Button variant="primary" onClick={nextStep}>
            {t('onboarding.automatic-mounting.put-trackers-on.next')}
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
