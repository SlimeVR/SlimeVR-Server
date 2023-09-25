import { useLocalization } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useTrackers } from '@/hooks/tracker';
import { useBnoExists } from '@/hooks/imu-logic';
import { StickerSlime } from './StickerSlime';
import { TrackerArrow } from './TrackerArrow';
import { ExtensionArrow } from './ExtensionArrow';

export function AssignmentTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();
  const { useConnectedIMUTrackers } = useTrackers();
  const connectedIMUTrackers = useConnectedIMUTrackers();
  const bnoExists = useBnoExists(connectedIMUTrackers);

  applyProgress(0.46);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center overflow-y-auto w-full xs:justify-center relative py-2">
        <div className="flex flex-col w-full xs:justify-center xs:px-20 gap-3 pb-2 px-4">
          <div className="mb-10 self-center">
            <Typography variant="main-title">
              {l10n.getString('onboarding-assignment_tutorial')}
            </Typography>
          </div>
          <div className="flex flex-col self-center justify-center gap-5 px-2">
            <div className="flex gap-12 xs:flex-row mobile:flex-col self-center justify-center">
              <div className="flex flex-col gap-5 xs:w-1/4">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'onboarding-assignment_tutorial-first_step'
                    )}
                  </Typography>
                </div>
                <div className="stroke-background-10 fill-background-10 flex justify-center">
                  <StickerSlime width="65%"></StickerSlime>
                </div>
              </div>
              <div className="flex flex-col gap-10 xs:w-1/4">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'onboarding-assignment_tutorial-second_step-v2'
                    )}
                  </Typography>
                </div>
                <div className="fill-background-10 stroke-background-10 flex justify-center">
                  <TrackerArrow width="75%"></TrackerArrow>
                </div>
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'onboarding-assignment_tutorial-second_step-continuation-v2'
                    )}
                  </Typography>
                </div>
                <div className="fill-background-10 stroke-background-10 flex justify-center">
                  <ExtensionArrow width="75%"></ExtensionArrow>
                </div>
              </div>
            </div>
            <div className="flex">
              <Button
                variant="secondary"
                to={
                  bnoExists
                    ? '/onboarding/calibration-tutorial'
                    : '/onboarding/wifi-creds'
                }
              >
                {l10n.getString('onboarding-previous_step')}
              </Button>
              <Button
                variant="primary"
                to="/onboarding/trackers-assign"
                className="ml-auto"
              >
                {l10n.getString('onboarding-assignment_tutorial-done')}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
