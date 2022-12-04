import { ResetType } from 'solarxr-protocol';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';
import { ResetButton } from '../../../../home/ResetButton';

export function PreparationStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  return (
    <>
      <div className="flex flex-col flex-grow">
        <div className="flex flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            Preparation
          </Typography>
          <div>
            <Typography color="secondary">
              1. Stand upright with your arms to your sides.
            </Typography>
            <Typography color="secondary">
              2. Press the "Reset" button and wait for 3 seconds before the
              trackers will reset.
            </Typography>
          </div>
        </div>

        <div className="flex flex-grow items-center"></div>
        <div className="flex gap-3">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
            onClick={prevStep}
          >
            Previous step
          </Button>
          <ResetButton
            variant="small"
            type={ResetType.Full}
            onReseted={nextStep}
          ></ResetButton>
        </div>
      </div>
      <div className="flex flex-col pt-1 items-center fill-background-50 justify-center px-12">
        <img src="/images/reset-pose.png" width={60} />
      </div>
    </>
  );
}
