import { useEffect } from 'react';
import { useAutobone } from '../../../../../hooks/autobone';
import { ProgressBar } from '../../../../commons/ProgressBar';
import { Typography } from '../../../../commons/Typography';

export function Recording({ nextStep }: { nextStep: () => void }) {
  const { progress, hasCalibration, hasRecording } = useAutobone();

  useEffect(() => {
    if (progress === 1 && hasCalibration) {
      nextStep();
    }
  }, [progress, hasCalibration]);

  return (
    <div className="flex flex-col items-center w-full justify-between">
      <div className="flex gap-1 flex-col justify-center items-center">
        <div className="flex text-status-critical justify-center items-center gap-1">
          <div className="w-2 h-2 rounded-lg bg-status-critical"></div>
          <Typography color="text-status-critical">REC</Typography>
        </div>
        <Typography variant="section-title">We're recording...</Typography>
        <Typography color="secondary">Make the moves shown below</Typography>
      </div>
      <Typography color="secondary">
        Bend knees a few times.
        Sit on a chair then stand up.
        Twist upper body left then bend right.
        Twist upper body right then bend left.
        Wiggle around until timer ends.
      </Typography>
      <div className="flex">
        <TipBox>
          Ensure your heels do not move during recording!
        </TipBox>
      </div>
      <div className="flex flex-col gap-2 items-center w-full max-w-[150px]">
        <ProgressBar progress={progress} height={2}></ProgressBar>
        <Typography color="secondary">
          {!hasCalibration && hasRecording
            ? 'Processing the result'
            : '15 seconds left'}
        </Typography>
      </div>
    </div>
  );
}
