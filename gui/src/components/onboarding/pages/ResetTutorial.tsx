import { useLocalization } from '@fluent/react';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';
import { useState } from 'react';
import { SkipSetupWarningModal } from '../SkipSetupWarningModal';
import { SkipSetupButton } from '../SkipSetupButton';

export function ResetTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);

  applyProgress(0.8);

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
                {l10n.getString('onboarding-reset_tutorial')}
                <span className="mx-2 p-1 bg-accent-background-30 text-standard rounded-md">
                  {l10n.getString('onboarding-wip')}
                </span>
              </Typography>
              <Typography color="secondary">
                {l10n.getString('onboarding-reset_tutorial-description')}
              </Typography>
              <div className="flex">
                <Button variant="secondary" to="/onboarding/mounting/auto">
                  {l10n.getString('onboarding-previous_step')}
                </Button>
                <Button
                  variant="primary"
                  to="/onboarding/body-proportions/auto"
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
