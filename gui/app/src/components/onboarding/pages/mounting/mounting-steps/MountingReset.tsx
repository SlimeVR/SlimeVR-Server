import { ResetType } from 'solarxr-protocol';
import {
  PoseMountingInstructions,
  PoseMountingVideo,
} from '@/components/mounting/PoseMountingVideo';
import { ResetButton } from '@/components/home/ResetButton';
import {
  MountingFlowLayout,
  MountingPhaseProps,
  useCancelMountingOnLeave,
} from '@/components/onboarding/pages/mounting/MountingFlow';

export function PoseMountingPhase({ flow, back, next }: MountingPhaseProps) {
  useCancelMountingOnLeave();

  return (
    <MountingFlowLayout
      flow={flow}
      body={<PoseMountingInstructions />}
      media={<PoseMountingVideo className="h-full" />}
      actions={
        <>
          {back}
          <ResetButton
            type={ResetType.MOUNTING}
            group="default"
            onReseted={next}
          />
        </>
      }
    />
  );
}
