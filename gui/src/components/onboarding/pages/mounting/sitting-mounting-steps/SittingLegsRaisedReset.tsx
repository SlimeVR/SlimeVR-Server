import { BodyPart, ResetBodyPose, ResetType } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { ResetButton } from '@/components/home/ResetButton';
import { useLocalization } from '@fluent/react';

export function SittingLegsRaisedResetStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const { l10n } = useLocalization();

  return (
    <div className="flex mobile:flex-col">
      <div className="flex flex-grow flex-col gap-4 max-w-sm">
        <Typography variant="main-title" bold>
          {l10n.getString(
            'onboarding-automatic_mounting-mounting_reset_feet-title'
          )}
        </Typography>
        <div className="flex flex-col gap-2">
          <Typography color="secondary">
            {l10n.getString(
              'onboarding-automatic_mounting-mounting_reset_feet-step-0'
            )}
          </Typography>
          <Typography color="secondary">
            {l10n.getString(
              'onboarding-automatic_mounting-mounting_reset_feet-step-1'
            )}
          </Typography>
        </div>
        <div className="flex gap-3 mobile:justify-between">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tertiary'}
            onClick={prevStep}
          >
            {l10n.getString('onboarding-automatic_mounting-prev_step')}
          </Button>
          <ResetButton
            variant="small"
            type={ResetType.Mounting}
            bodyPose={ResetBodyPose.SITTING_LEANING_BACK}
            referenceTrackerPosition={BodyPart.CHEST}
            trackerPositions={[
              BodyPart.LEFT_LOWER_LEG,
              BodyPart.RIGHT_LOWER_LEG,
              BodyPart.LEFT_FOOT,
              BodyPart.RIGHT_FOOT,
            ]}
            onReseted={nextStep}
          ></ResetButton>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img
          src="/images/reset-sitting-legs-up-pose.webp"
          width={200}
          alt="Reset position"
        />
      </div>
    </div>
  );
}
