import { ResetType } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { FullResetExamples } from '@/components/commons/FullResetExamples';
import { ResetButton } from '@/components/home/ResetButton';
import {
  MountingFlowLayout,
  MountingPhaseProps,
} from '@/components/onboarding/pages/mounting/MountingFlow';

export function PreparationPhase({ flow, back, next }: MountingPhaseProps) {
  return (
    <MountingFlowLayout
      flow={flow}
      body={
        <>
          <Typography id="onboarding-automatic_mounting-preparation-v2-step-0" />
          <Typography id="onboarding-automatic_mounting-preparation-v2-step-1" />
          <Typography id="onboarding-automatic_mounting-preparation-v2-step-2" />
        </>
      }
      media={<FullResetExamples captions className="h-full w-full" />}
      actions={
        <>
          {back}
          <ResetButton type={ResetType.FULL} onReseted={next} />
        </>
      }
    />
  );
}
