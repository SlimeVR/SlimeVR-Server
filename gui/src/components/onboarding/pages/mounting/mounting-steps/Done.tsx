import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';

export function DoneStep({
  nextStep,
  prevStep,
  resetSteps,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  resetSteps: () => void;
  variant: 'onboarding' | 'alone';
}) {
  return (
    <div className="flex flex-col items-center w-full justify-center gap-5">
      <div className="flex gap-1 flex-col justify-center items-center">
        <Typography variant="section-title">
          Mounting rotations calibrated.
        </Typography>
        <Typography color="secondary">
          Your mounting calibration is complete!
        </Typography>
      </div>
      {/* <Button variant="primary">Continue to next step</Button> */}

      <div className="flex gap-3">
        <Button
          variant={variant === 'onboarding' ? 'secondary' : 'tiertiary'}
          onClick={resetSteps}
        >
          Return to start
        </Button>
      </div>
    </div>
  );
}
