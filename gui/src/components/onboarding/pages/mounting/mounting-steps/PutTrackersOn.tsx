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
  const trackers = useAtomValue(flatTrackersAtom);
  const { l10n } = useLocalization();

  return (
    <div className="flex mobile:flex-col items-center w-full">
      <div className="flex flex-col flex-grow gap-2">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString(
              'onboarding-automatic_mounting-put_trackers_on-title'
            )}
          </Typography>
          <div>
            <Typography>
              {l10n.getString(
                'onboarding-automatic_mounting-put_trackers_on-description'
              )}
            </Typography>
          </div>
          <div className="flex">
            <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>
          </div>
        </div>

        {isMobile && (
          <div className="flex flex-col pt-1 items-center fill-background-50 justify-center">
            <div className="max-h-[400px]">
              <BodyDisplay
                trackers={trackers}
                dotsSize={15}
                hideUnassigned={true}
              />
            </div>
          </div>
        )}

        <div className="flex flex-col gap-3">
          <div className="flex gap-3 mobile:justify-between">
            <Button
              variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
              to="/onboarding/mounting/choose"
              state={{ alonePage: variant === 'alone' }}
            >
              {l10n.getString('onboarding-automatic_mounting-prev_step')}
            </Button>
            <Button variant="primary" onClick={nextStep}>
              {l10n.getString(
                'onboarding-automatic_mounting-put_trackers_on-next'
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
    </div>
  );
}
