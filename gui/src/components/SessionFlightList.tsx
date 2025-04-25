import {
  flightlistIdtoLabel,
  FlightListStep,
  useSessionFlightlist,
} from '@/hooks/session-flightlist';
import { CheckIcon } from './commons/icon/CheckIcon';
import classNames from 'classnames';
import { Localized } from '@fluent/react';

function Step({
  step: { status, id, optional },
  index,
}: {
  step: FlightListStep;
  index: number;
}) {
  return (
    <div className="flex w-full gap-2 py-1">
      <div
        className={classNames(
          'p-1 h-5 w-5 rounded-full fill-background-10 flex items-center justify-center',
          status === 'complete' && 'bg-status-success',
          status === 'blocked' && 'bg-background-50',
          status === 'skipped' && 'bg-background-50',
          status === 'invalid' && !optional && 'bg-status-critical',
          status === 'invalid' && optional && 'bg-status-warning'
        )}
      >
        {status === 'complete' && <CheckIcon size={10}></CheckIcon>}
        {status === 'invalid' && index}
        {status === 'blocked' && index}
        {status === 'skipped' && index}
      </div>
      <div>
        <Localized id={flightlistIdtoLabel[id]}></Localized>
      </div>
    </div>
  );
}

export function SessionFlightList() {
  const { steps } = useSessionFlightlist();

  return (
    <div className="flex flex-col">
      {steps.map((step, index) => (
        <Step step={step} index={index + 1} key={step.id}></Step>
      ))}
    </div>
  );
}
