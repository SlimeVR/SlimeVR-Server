import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import {
  DetectRelaxedPoseButton,
  ResetRelaxedPoseButton,
} from '@/components/stay-aligned/RelaxedPose';
import { useLocalization } from '@fluent/react';
import { StayAlignedRelaxedPose } from 'solarxr-protocol';
import { enableStayAligned } from '@/components/onboarding/pages/stay-aligned/StayAlignedSetup';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { ReactNode } from 'react';

function PosePage({
  nextStep,
  prevStep,
  descriptionKeys,
  children,
  relaxedPose,
  lastStep = false,
}: VerticalStepComponentProps & {
  descriptionKeys: string[];
  children: ReactNode;
  relaxedPose: StayAlignedRelaxedPose;
  lastStep?: boolean;
}) {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  return (
    <div className="flex flex-col py-2">
      <div className="flex flex-col gap-2">
        {descriptionKeys.map((descriptionKey) => (
          <Typography>{l10n.getString(descriptionKey)}</Typography>
        ))}
      </div>
      <div className="flex pt-1 items-center fill-background-50 justify-center px-12">
        {children}
      </div>
      <div className="flex justify-between">
        <Button variant={'secondary'} onClick={prevStep}>
          {l10n.getString('onboarding-stay_aligned-previous_step')}
        </Button>
        <div className="flex gap-2">
          <ResetRelaxedPoseButton
            variant="secondary"
            onClick={() => {
              if (lastStep) {
                enableStayAligned(true, sendRPCPacket);
              }
              nextStep();
            }}
            pose={relaxedPose}
          >
            {l10n.getString('onboarding-stay_aligned-relaxed_poses-skip_step')}
          </ResetRelaxedPoseButton>
          <DetectRelaxedPoseButton
            onClick={() => {
              if (lastStep) {
                enableStayAligned(true, sendRPCPacket);
              }
              nextStep();
            }}
            pose={relaxedPose}
          />
        </div>
      </div>
    </div>
  );
}

export const StandingRelaxedPoseStep = (
  verticalStepProps: VerticalStepComponentProps
) => (
  <PosePage
    {...verticalStepProps}
    descriptionKeys={[
      'onboarding-stay_aligned-relaxed_poses-standing-step-0',
      'onboarding-stay_aligned-relaxed_poses-standing-step-1-v2',
    ]}
    relaxedPose={StayAlignedRelaxedPose.STANDING}
  >
    <img
      src={'/images/stay-aligned/StayAlignedStanding.webp'}
      width={300}
      alt="Reset position"
    />
  </PosePage>
);

export const SittingRelaxedPoseStep = (
  verticalStepProps: VerticalStepComponentProps
) => (
  <PosePage
    {...verticalStepProps}
    descriptionKeys={[
      'onboarding-stay_aligned-relaxed_poses-sitting-step-0',
      'onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2',
    ]}
    relaxedPose={StayAlignedRelaxedPose.SITTING}
  >
    <img
      src={'/images/stay-aligned/StayAlignedSitting.webp'}
      width={300}
      alt="Reset position"
    />
  </PosePage>
);

export const FlatRelaxedPoseStep = (
  verticalStepProps: VerticalStepComponentProps
) => (
  <PosePage
    {...verticalStepProps}
    descriptionKeys={[
      'onboarding-stay_aligned-relaxed_poses-flat-step-0',
      'onboarding-stay_aligned-relaxed_poses-flat-step-1-v2',
    ]}
    relaxedPose={StayAlignedRelaxedPose.FLAT}
    lastStep
  >
    <img
      src={'/images/stay-aligned/StayAlignedFloor.webp'}
      width={600}
      alt="Reset position"
    />
  </PosePage>
);
