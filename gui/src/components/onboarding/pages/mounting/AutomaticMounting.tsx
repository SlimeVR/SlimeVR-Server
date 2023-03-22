import { useOnboarding } from '../../../../hooks/onboarding';
import { Typography } from '../../../commons/Typography';
import { Step, StepperSlider } from '../../StepperSlider';
import { DoneStep } from './mounting-steps/Done';
import { MountingResetStep } from './mounting-steps/MountingReset';
import { PreparationStep } from './mounting-steps/Preparation';
import { PutTrackersOnStep } from './mounting-steps/PutTrackersOn';
import { useLocalization } from '@fluent/react';
import { SkipSetupWarningModal } from '../../SkipSetupWarningModal';
import { useState } from 'react';
import { SkipSetupButton } from '../../SkipSetupButton';

const steps: Step[] = [
  { type: 'numbered', component: PutTrackersOnStep },
  { type: 'numbered', component: PreparationStep },
  { type: 'numbered', component: MountingResetStep },
  { type: 'fullsize', component: DoneStep },
];
export function AutomaticMountingPage() {
  const { l10n } = useLocalization();
  const { applyProgress, skipSetup, state } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);

  applyProgress(0.7);

  return (
    <>
      <div className="flex flex-col gap-2 h-full items-center w-full justify-center relative">
        <SkipSetupButton
          visible={!state.alonePage}
          modalVisible={skipWarning}
          onClick={() => setSkipWarning(true)}
        ></SkipSetupButton>
        <div className="flex flex-col w-full h-full justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-automatic_mounting-title')}
            </Typography>
            <Typography color="secondary">
              {l10n.getString('onboarding-automatic_mounting-description')}
            </Typography>
          </div>
          <div className="flex">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={steps}
            ></StepperSlider>
          </div>
        </div>
      </div>
      <SkipSetupWarningModal
        accept={skipSetup}
        onClose={() => setSkipWarning(false)}
        isOpen={skipWarning}
      ></SkipSetupWarningModal>
    </>
  );
}
