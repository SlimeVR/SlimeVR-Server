import { useLocalization } from '@fluent/react';
import { useOnboarding } from '../../../../hooks/onboarding';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { useState } from 'react';
import { SkipSetupWarningModal } from '../../SkipSetupWarningModal';
import { SkipSetupButton } from '../../SkipSetupButton';
import { useTrackers } from '../../../../hooks/tracker';
import { useBnoExists } from '../../../../hooks/imu-logic';
import { StickerSlime } from './StickerSlime';
import { TrackerArrow } from './TrackerArrow';
import { ExtensionArrow } from './ExtensionArrow';

export function AssignmentTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);
  const { useConnectedTrackers } = useTrackers();
  const connectedTrackers = useConnectedTrackers();
  const bnoExists = useBnoExists(connectedTrackers);

  applyProgress(0.46);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center relative">
        <SkipSetupButton
          visible={true}
          modalVisible={skipWarning}
          onClick={() => setSkipWarning(true)}
        ></SkipSetupButton>
        <div className="flex flex-col w-full h-full justify-center px-20 gap-3">
          <div className="mt-10 self-center">
            <Typography variant="main-title">
              {l10n.getString('onboarding-assignment_tutorial')}
            </Typography>
          </div>
          <div className="flex flex-col self-center justify-center gap-5">
            <div className="flex gap-12 flex-row self-center justify-center">
              <div className="flex flex-col gap-5 w-1/4">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'onboarding-assignment_tutorial-first_step'
                    )}
                  </Typography>
                </div>
                <div className="stroke-background-10 fill-background-10">
                  <StickerSlime width="65%"></StickerSlime>
                </div>
              </div>
              <div className="flex flex-col gap-10 w-1/4">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'onboarding-assignment_tutorial-second_step'
                    )}
                  </Typography>
                </div>
                <div className="fill-background-10 stroke-background-10">
                  <TrackerArrow width="75%"></TrackerArrow>
                </div>
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'onboarding-assignment_tutorial-second_step-continuation'
                    )}
                  </Typography>
                </div>
                <div className="fill-background-10 stroke-background-10">
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
        <SkipSetupWarningModal
          accept={skipSetup}
          onClose={() => setSkipWarning(false)}
          isOpen={skipWarning}
        ></SkipSetupWarningModal>
      </div>
    </>
  );
}
