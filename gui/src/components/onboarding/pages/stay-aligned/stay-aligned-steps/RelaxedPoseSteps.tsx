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

function makeRelaxedPoseStep(
  descriptionKeys: string[],
  imageUrl: string,
  relaxedPose: StayAlignedRelaxedPose,
  lastStep: boolean
) {
  return ({ nextStep, prevStep }: VerticalStepComponentProps) => {
    const { l10n } = useLocalization();
    const { sendRPCPacket } = useWebsocketAPI();

    return (
      <div className="flex flex-col">
        <div className="flex flex-col gap-2">
          {descriptionKeys.map((descriptionKey) => (
            <Typography color="secondary">
              {l10n.getString(descriptionKey)}
            </Typography>
          ))}
        </div>
        <div className="flex pt-1 items-center fill-background-50 justify-center px-12">
          <img src={imageUrl} width={200} alt="Reset position" />
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
              {l10n.getString(
                'onboarding-stay_aligned-relaxed_poses-skip_step'
              )}
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
  };
}

export const StandingRelaxedPoseStep = makeRelaxedPoseStep(
  [
    'onboarding-stay_aligned-relaxed_poses-standing-step-0',
    'onboarding-stay_aligned-relaxed_poses-standing-step-2',
  ],
  '/images/relaxed_pose_standing.webp',
  StayAlignedRelaxedPose.STANDING,
  false
);

export const SittingRelaxedPoseStep = makeRelaxedPoseStep(
  [
    'onboarding-stay_aligned-relaxed_poses-sitting-step-0',
    'onboarding-stay_aligned-relaxed_poses-sitting-step-2',
  ],
  '/images/relaxed_pose_sitting.webp',
  StayAlignedRelaxedPose.SITTING,
  false
);

export const FlatRelaxedPoseStep = makeRelaxedPoseStep(
  [
    'onboarding-stay_aligned-relaxed_poses-flat-step-0',
    'onboarding-stay_aligned-relaxed_poses-flat-step-2',
  ],
  '/images/relaxed_pose_flat.webp',
  StayAlignedRelaxedPose.FLAT,
  true
);
