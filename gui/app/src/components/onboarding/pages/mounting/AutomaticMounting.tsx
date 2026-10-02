import { useOnboarding } from '@/hooks/onboarding';
import { MountingPhaseProps, useMountingPhases } from './MountingFlow';
import { DonePhase } from './mounting-steps/Done';
import { PoseMountingPhase } from './mounting-steps/MountingReset';
import { PreparationPhase } from './mounting-steps/Preparation';

export function AutomaticMountingPage() {
  const { applyProgress } = useOnboarding();
  const { alonePage, ...props } = useMountingPhases({
    titleId: 'onboarding-automatic_mounting-title',
    descriptionId: 'onboarding-automatic_mounting-description',
    mountLabelId: 'onboarding-automatic_mounting-mounting_reset-title',
  });

  applyProgress(0.6);

  const phaseProps: MountingPhaseProps = props;
  switch (props.flow.current) {
    case 'prepare':
      return <PreparationPhase {...phaseProps} />;
    case 'mount':
      return <PoseMountingPhase {...phaseProps} />;
    case 'done':
      return <DonePhase {...phaseProps} alonePage={alonePage} />;
  }
}
