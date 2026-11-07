import { useBreakpoint } from '@/hooks/breakpoint';
import { BodyDisplay } from '@/components/commons/BodyDisplay';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';

export function PutTrackersOnStep({
  nextStep,
  variant,
}: {
  nextStep: () => void;
  variant: 'alone' | 'onboarding';
}) {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();
  const trackers = useAtomValue(flatTrackersAtom);

  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_proportions-put_trackers_on-title'
            )}
          </Typography>
          <div>
            <Typography>
              {l10n.getString(
                'onboarding-automatic_proportions-put_trackers_on-description'
              )}
            </Typography>
          </div>
          <div className="flex">
            <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>
          </div>
        </div>

        {isMobile && (
          <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
            <BodyDisplay
              trackers={trackers}
              dotsSize={15}
              hideUnassigned={true}
            />
          </div>
        )}

        <div className="flex flex-col gap-3">
          <div className="flex gap-3 mobile:justify-between">
            <Button
              variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
              to="/onboarding/body-proportions/manual"
              state={{ alonePage: variant === 'alone' }}
            >
              {l10n.getString('onboarding-automatic_proportions-back')}
            </Button>
            <Button variant="primary" onClick={nextStep}>
              {l10n.getString(
                'onboarding-automatic_proportions-put_trackers_on-next'
              )}
            </Button>
          </div>
        </div>
      </div>
      {!isMobile && (
        <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-16">
          <BodyDisplay
            trackers={trackers}
            dotsSize={15}
            hideUnassigned={true}
          />
        </div>
      )}
    </>
  );
}
