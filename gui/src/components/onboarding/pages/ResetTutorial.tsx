import { useLocalization } from '@fluent/react';
import { useOnboarding } from '../../../hooks/onboarding';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';
import { useState } from 'react';
import { SkipSetupWarningModal } from '../SkipSetupWarningModal';

export function ResetTutorialPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);

  applyProgress(0.8);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
        <div className="flex flex-col w-full h-full justify-center px-20">
          <div className="flex gap-8">
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
            </div>
          </div>
        </div>
        <div className="w-full py-4 flex flex-row">
          <div className="flex flex-grow gap-3">
            <Button variant="secondary" to="/onboarding/mounting/auto">
              {l10n.getString('onboarding-previous_step')}
            </Button>
            <Button variant="secondary" onClick={() => setSkipWarning(true)}>
              {l10n.getString('onboarding-skip')}
            </Button>
          </div>
          <div className="flex gap-3">
            <Button variant="primary" to="/onboarding/body-proportions/auto">
              {l10n.getString('onboarding-continue')}
            </Button>
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
