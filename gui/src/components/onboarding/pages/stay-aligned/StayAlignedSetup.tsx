import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import { Step, StepperSlider } from '@/components/onboarding/StepperSlider';
import { DoneStep } from './stay-aligned-steps/Done';
import { useLocalization } from '@fluent/react';
import { autoMountingSteps } from '@/components/onboarding/pages/mounting/AutomaticMounting';
import {
  FlatRelaxedPoseStep,
  SittingRelaxedPoseStep,
  StandingRelaxedPoseStep,
} from './stay-aligned-steps/RelaxedPoseSteps';

const steps: Step[] = [
  ...autoMountingSteps,
  { type: 'numbered', component: StandingRelaxedPoseStep },
  { type: 'numbered', component: SittingRelaxedPoseStep },
  { type: 'numbered', component: FlatRelaxedPoseStep },
  { type: 'fullsize', component: DoneStep },
];
export function StayAlignedSetup() {
  const { l10n } = useLocalization();
  const { state } = useOnboarding();

  return (
    <>
      <div className="flex flex-col gap-2 h-full items-center w-full xs:justify-center relative overflow-y-auto overflow-x-hidden px-4 pb-4">
        <div className="flex flex-col w-full h-full xs:justify-center xs:max-w-3xl gap-5">
          <div className="flex flex-col xs:max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-stay_aligned-title')}
            </Typography>
            <Typography color="secondary">
              {l10n.getString('onboarding-stay_aligned-description')}
            </Typography>
          </div>
          <div className="flex pb-4">
            <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={steps}
            />
          </div>
        </div>
      </div>
    </>
  );
}
