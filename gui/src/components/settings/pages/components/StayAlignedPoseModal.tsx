import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import {
  DetectRelaxedPoseButton,
  ResetRelaxedPoseButton,
} from '@/components/stay-aligned/RelaxedPose';
import { useLocalization } from '@fluent/react';
import { Dispatch, ReactNode, SetStateAction } from 'react';
import { StayAlignedRelaxedPose } from 'solarxr-protocol';

function StaAlignedPoseModal({
  open,
  title,
  descriptionKeys,
  children,
  relaxedPose,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
  title: string;
  descriptionKeys: string[];
  children: ReactNode;
  relaxedPose: StayAlignedRelaxedPose;
  lastStep?: boolean;
}) {
  const { l10n } = useLocalization();

  return (
    <BaseModal
      isOpen={open[0]}
      appendClasses={'w-xl max-w-xl mobile:w-full'}
      closeable
      onRequestClose={() => {
        open[1](false);
      }}
    >
      <div className="flex flex-col">
        <div className="pb-4">
          <Typography variant="main-title">{l10n.getString(title)}</Typography>
        </div>
        <div className="flex flex-col gap-1">
          {descriptionKeys.map((descriptionKey) => (
            <Typography>{l10n.getString(descriptionKey)}</Typography>
          ))}
        </div>
        <div className="flex pt-1 items-center fill-background-50 justify-center px-12">
          {children}
        </div>
        <div className="flex justify-between">
          <Button variant={'tertiary'} onClick={() => open[1](false)}>
            {l10n.getString('settings-stay_aligned-relaxed_poses-close')}
          </Button>
          <div className="flex gap-2">
            <ResetRelaxedPoseButton
              variant="tertiary"
              onClick={() => {
                open[1](false);
              }}
              pose={relaxedPose}
            >
              {l10n.getString('settings-stay_aligned-relaxed_poses-reset_pose')}
            </ResetRelaxedPoseButton>
            <DetectRelaxedPoseButton
              onClick={() => {
                open[1](false);
              }}
              pose={relaxedPose}
            />
          </div>
        </div>
      </div>
    </BaseModal>
  );
}

export const StandingRelaxedPoseModal = ({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) => (
  <StaAlignedPoseModal
    open={open}
    title={'onboarding-stay_aligned-relaxed_poses-standing-title'}
    descriptionKeys={[
      'onboarding-stay_aligned-relaxed_poses-standing-step-0',
      'onboarding-stay_aligned-relaxed_poses-standing-step-1-v2',
    ]}
    relaxedPose={StayAlignedRelaxedPose.STANDING}
  >
    <img
      src={'/images/stay-aligned/StayAlignedStanding.webp'}
      width={260}
      alt="Reset position"
    />
  </StaAlignedPoseModal>
);

export const SittingRelaxedPoseModal = ({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) => (
  <StaAlignedPoseModal
    open={open}
    title={'onboarding-stay_aligned-relaxed_poses-sitting-title'}
    descriptionKeys={[
      'onboarding-stay_aligned-relaxed_poses-sitting-step-0',
      'onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2',
    ]}
    relaxedPose={StayAlignedRelaxedPose.SITTING}
  >
    <img
      src={'/images/stay-aligned/StayAlignedSitting.webp'}
      width={260}
      alt="Reset position"
    />
  </StaAlignedPoseModal>
);

export const FlatRelaxedPoseModal = ({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) => (
  <StaAlignedPoseModal
    open={open}
    title={'onboarding-stay_aligned-relaxed_poses-flat-title'}
    descriptionKeys={[
      'onboarding-stay_aligned-relaxed_poses-flat-step-0',
      'onboarding-stay_aligned-relaxed_poses-flat-step-1-v2',
    ]}
    relaxedPose={StayAlignedRelaxedPose.FLAT}
  >
    <img
      src={'/images/stay-aligned/StayAlignedFloor.webp'}
      width={560}
      alt="Reset position"
    />
  </StaAlignedPoseModal>
);
