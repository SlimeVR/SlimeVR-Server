import { ReactNode, useState } from 'react';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import {
  PoseMountingInstructions,
  PoseMountingVideo,
} from '@/components/mounting/PoseMountingVideo';
import { MountingMethod, ResetType } from 'solarxr-protocol';
import { ResetButton } from '@/components/home/ResetButton';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';
import { useCancelMountingOnLeave } from '@/components/onboarding/pages/mounting/MountingFlow';
import { MountingMethodModal } from '@/components/mounting/MountingMethodModal';
import { MountingMethodRadio } from '@/components/mounting/MountingMethodRadio';
import { StepMedia } from '@/components/mounting/StepMountingStatus';
import { useResetsSettings } from '@/hooks/resets-settings';

function MountingLayout({
  instructions,
  media,
  actions,
}: {
  instructions: ReactNode;
  media: ReactNode;
  actions: ReactNode;
}) {
  return (
    <div className="flex flex-col flex-grow justify-between py-2 gap-2">
      <div className="flex flex-col flex-grow gap-4">
        <div className="flex mobile:flex-col gap-6">
          <div className="flex flex-col gap-4 xs:w-72 shrink-0">
            {instructions}
          </div>
          <div className="flex flex-1 items-center justify-center fill-background-50">
            {media}
          </div>
        </div>
        <div className="flex gap-3 justify-between">{actions}</div>
      </div>
    </div>
  );
}

function ChangeMethodButton() {
  const [isOpen, setOpen] = useState(false);

  return (
    <>
      <Button
        variant="secondary"
        onClick={() => setOpen(true)}
        id="onboarding-stay_aligned-verify_mounting-change_method"
      />
      <MountingMethodModal isOpen={isOpen} onClose={() => setOpen(false)} />
    </>
  );
}

function ResetMounting({
  nextStep,
  prevStep,
  isActive,
  instructions,
  media,
}: VerticalStepComponentProps & {
  instructions: ReactNode;
  media: ReactNode;
}) {
  useCancelMountingOnLeave();

  return (
    <MountingLayout
      instructions={instructions}
      media={media}
      actions={
        <>
          <Button
            variant="secondary"
            onClick={prevStep}
            id="onboarding-automatic_mounting-prev_step"
          />
          <div className="flex gap-3">
            <ChangeMethodButton />
            <ResetButton
              type={ResetType.MOUNTING}
              group="default"
              onReseted={() => {
                if (isActive) nextStep();
              }}
            />
          </div>
        </>
      }
    />
  );
}

function PickMethodStep({ prevStep }: VerticalStepComponentProps) {
  return (
    <div className="flex flex-col py-2 gap-4">
      <Typography id="mounting_method_modal-description" />
      <MountingMethodRadio col />
      <div className="flex">
        <Button
          variant="secondary"
          onClick={prevStep}
          id="onboarding-automatic_mounting-prev_step"
        />
      </div>
    </div>
  );
}

export function VerifyMountingStep(props: VerticalStepComponentProps) {
  const { resetsSettings } = useResetsSettings();

  switch (resetsSettings?.mountingMethod) {
    case MountingMethod.STEP:
      return (
        <ResetMounting
          {...props}
          instructions={
            <div className="flex flex-col gap-2">
              <Typography id="onboarding-step_mounting-step-0" />
              <Typography id="onboarding-step_mounting-step-1" />
            </div>
          }
          media={
            <div className="h-72 xs:h-96">
              <StepMedia status={null} className="h-full object-contain" />
            </div>
          }
        />
      );
    case MountingMethod.POSE:
      return (
        <ResetMounting
          {...props}
          instructions={<PoseMountingInstructions />}
          media={<PoseMountingVideo className="h-72 xs:h-96" />}
        />
      );
    default:
      return <PickMethodStep {...props} />;
  }
}
