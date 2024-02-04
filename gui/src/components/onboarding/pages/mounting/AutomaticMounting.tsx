import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { Step, StepperSlider } from '@/components/onboarding/StepperSlider';
import { DoneStep } from './mounting-steps/Done';
import { MountingResetStep } from './mounting-steps/MountingReset';
import { PreparationStep } from './mounting-steps/Preparation';
import { PutTrackersOnStep } from './mounting-steps/PutTrackersOn';
import { useLocalization } from '@fluent/react';

const steps: Step[] = [
  { type: 'numbered', component: PutTrackersOnStep },
  { type: 'numbered', component: PreparationStep },
  { type: 'numbered', component: MountingResetStep },
  { type: 'fullsize', component: DoneStep },
];
export function AutomaticMountingPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();

  applyProgress(0.7);

  return (
    <>
      <div className="flex flex-col gap-2 h-full items-center w-full xs:justify-center relative overflow-y-auto overflow-x-hidden px-4 pb-4">
        <div className="flex flex-col w-full h-full xs:justify-center xs:max-w-3xl gap-5">
          <div className="flex flex-col xs:max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-automatic_mounting-title')}
            </Typography>
            <Typography color="secondary">
              {l10n.getString('onboarding-automatic_mounting-description')}
            </Typography>
          </div>
          <div className="flex pb-4">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={steps}
            ></StepperSlider>
          </div>
        </div>
      </div>
    </>
  );
}
