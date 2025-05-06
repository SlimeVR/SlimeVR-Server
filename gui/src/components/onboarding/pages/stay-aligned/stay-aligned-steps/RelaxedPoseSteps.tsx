import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import {
  CurrentRelaxedPose,
  DetectRelaxedPoseButton,
  ResetRelaxedPoseButton,
} from '@/components/stay-aligned/RelaxedPose';
import { useLocalization } from '@fluent/react';
import { StayAlignedRelaxedPose } from 'solarxr-protocol';
import { enableStayAligned } from '@/components/onboarding/pages/stay-aligned/StayAlignedSetup';
import { useWebsocketAPI } from '@/hooks/websocket-api';

function makeRelaxedPoseStep(
  titleKey: string,
  descriptionKeys: string[],
  imageUrl: string,
  relaxedPose: StayAlignedRelaxedPose,
  lastStep: boolean
) {
  return ({
    nextStep,
    prevStep,
    variant,
  }: {
    nextStep: () => void;
    prevStep: () => void;
    variant: 'onboarding' | 'alone';
  }) => {
    const { l10n } = useLocalization();
    const { sendRPCPacket } = useWebsocketAPI();

    return (
      <div className="flex mobile:flex-col">
        <div className="flex flex-grow flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            {l10n.getString(titleKey)}
          </Typography>
          <div className="flex flex-col gap-2">
            {descriptionKeys.map((descriptionKey) => (
              <Typography color="secondary">
                {l10n.getString(descriptionKey)}
              </Typography>
            ))}
          </div>
          <CurrentRelaxedPose />
          <div className="flex gap-3 mobile:justify-between">
            <Button
              variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
              onClick={prevStep}
            >
              {l10n.getString('onboarding-stay_aligned-previous_step')}
            </Button>
            <DetectRelaxedPoseButton
              onClick={() => {
                if (lastStep) {
                  enableStayAligned(true, sendRPCPacket);
                }
                nextStep();
              }}
              pose={relaxedPose}
            />
            <ResetRelaxedPoseButton
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
          </div>
        </div>
        <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
          <img src={imageUrl} width={200} alt="Reset position" />
        </div>
      </div>
    );
  };
}

export const StandingRelaxedPoseStep = makeRelaxedPoseStep(
  'onboarding-stay_aligned-relaxed_poses-standing-title',
  [
    'onboarding-stay_aligned-relaxed_poses-standing-step-0',
    'onboarding-stay_aligned-relaxed_poses-standing-step-1',
    'onboarding-stay_aligned-relaxed_poses-standing-step-2',
  ],
  '/images/relaxed_pose_standing.webp',
  StayAlignedRelaxedPose.STANDING,
  false
);

export const SittingRelaxedPoseStep = makeRelaxedPoseStep(
  'onboarding-stay_aligned-relaxed_poses-sitting-title',
  [
    'onboarding-stay_aligned-relaxed_poses-sitting-step-0',
    'onboarding-stay_aligned-relaxed_poses-sitting-step-1',
    'onboarding-stay_aligned-relaxed_poses-sitting-step-2',
  ],
  '/images/relaxed_pose_sitting.webp',
  StayAlignedRelaxedPose.SITTING,
  false
);

export const FlatRelaxedPoseStep = makeRelaxedPoseStep(
  'onboarding-stay_aligned-relaxed_poses-flat-title',
  [
    'onboarding-stay_aligned-relaxed_poses-flat-step-0',
    'onboarding-stay_aligned-relaxed_poses-flat-step-1',
    'onboarding-stay_aligned-relaxed_poses-flat-step-2',
  ],
  '/images/relaxed_pose_flat.webp',
  StayAlignedRelaxedPose.FLAT,
  true
);
