import classNames from 'classnames';
import { useEffect } from 'react';
import { useAutobone } from '../../../../../hooks/autobone';
import { Button } from '../../../../commons/Button';
import { Typography } from '../../../../commons/Typography';

export function VerifyResultsStep({
  nextStep,
  prevStep,
  variant,
}: {
  nextStep: () => void;
  prevStep: () => void;
  variant: 'onboarding' | 'alone';
}) {
  const {
    startRecording,
    hasCalibration,
    bodyParts,
    hasRecording,
    applyProcessing,
  } = useAutobone();

  const apply = () => {
    applyProcessing();
    nextStep();
  };

  const redo = () => {
    startRecording();
    prevStep();
  };

  return (
    <>
      <div className="flex flex-col flex-grow justify-between">
        <div className="flex flex-col gap-4 max-w-sm">
          <Typography variant="main-title" bold>
            Verify results
          </Typography>
          <div>
            <Typography color="secondary">
              Check the results below, do they look correct?
            </Typography>
          </div>
        </div>
        <div className="flex w-full items-center flex-col">
          <div className="flex flex-col pt-1 gap-2 justify-center w-full max-w-xs">
            <Typography bold>Recording results</Typography>
            <div
              className={classNames(
                'flex flex-col  w-full p-4 rounded-md gap-2',
                variant === 'onboarding' && 'bg-background-60',
                variant === 'alone' && 'bg-background-50'
              )}
            >
              {bodyParts?.map(({ bone, label, value }) => (
                <div className="flex justify-between" key={bone}>
                  <Typography color="secondary">{label}</Typography>
                  <Typography bold>{(value * 100).toFixed(2)} CM</Typography>
                </div>
              ))}
              {!hasCalibration && hasRecording && (
                <Typography>Processing recording...</Typography>
              )}
            </div>
          </div>
        </div>
        <div className="flex gap-2">
          <Button
            variant={variant === 'onboarding' ? 'secondary' : 'tierciary'}
            onClick={redo}
          >
            Redo recording
          </Button>
          <Button variant="primary" onClick={apply}>
            They're correct
          </Button>
        </div>
      </div>
    </>
  );
}
