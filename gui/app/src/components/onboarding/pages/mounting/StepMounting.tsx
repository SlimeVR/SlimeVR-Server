import { useOnboarding } from '@/hooks/onboarding';
import { MountingPhaseProps, useMountingPhases } from './MountingFlow';
import { DonePhase } from './mounting-steps/Done';
import { PreparationPhase } from './mounting-steps/Preparation';
import { StepMountingPhase } from './mounting-steps/StepMountingStep';

export function StepMountingPage() {
  const { applyProgress } = useOnboarding();
  const { alonePage, ...props } = useMountingPhases({
    titleId: 'onboarding-step_mounting-title',
    descriptionId: 'onboarding-step_mounting-description',
    mountLabelId: 'onboarding-step_mounting-step-title',
  });

  applyProgress(0.6);

  const phaseProps: MountingPhaseProps = props;
  switch (props.flow.current) {
    case 'prepare':
      return <PreparationPhase {...phaseProps} />;
    case 'mount':
      return <StepMountingPhase {...phaseProps} />;
    case 'done':
      return <DonePhase {...phaseProps} alonePage={alonePage} />;
  }
}
