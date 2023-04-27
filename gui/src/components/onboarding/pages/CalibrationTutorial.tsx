import { useLocalization } from '@fluent/react';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';
import { useEffect, useState } from 'react';
import { SkipSetupWarningModal } from '../SkipSetupWarningModal';
import { SkipSetupButton } from '../SkipSetupButton';
import { useTrackers } from '../../../hooks/tracker';
import { ImuType } from 'solarxr-protocol';
import { useNavigate } from 'react-router-dom';

export function CalibrationTutorialPage() {
  const navigate = useNavigate();
  const { l10n } = useLocalization();
  const { trackers, useConnectedTrackers } = useTrackers();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);

  applyProgress(0.45);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center relative">
        <SkipSetupButton
          visible={true}
          modalVisible={skipWarning}
          onClick={() => setSkipWarning(true)}
        ></SkipSetupButton>
        <div className="flex flex-col w-full h-full justify-center px-20">
          <div className="flex gap-8 self-center">
            <div className="flex flex-col max-w-md gap-3">
              <Typography variant="main-title">
                {l10n.getString('onboarding-calibration_tutorial')}
              </Typography>
              <Typography color="secondary">
                {l10n.getString('onboarding-calibration_tutorial-description')}
              </Typography>
              <div className="flex">
                <Button variant="secondary" to="/onboarding/wifi-creds">
                  {l10n.getString('onboarding-previous_step')}
                </Button>
                <Button
                  variant="primary"
                  to="/onboarding/trackers-assign"
                  className="ml-auto"
                >
                  {l10n.getString('onboarding-continue')}
                </Button>
              </div>
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
