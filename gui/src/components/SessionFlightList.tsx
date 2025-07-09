import {
  flightlistIdtoLabel,
  FlightListStep,
  SessionFlightListContext,
  useSessionFlightlist,
} from '@/hooks/session-flightlist';
import { CheckIcon } from './commons/icon/CheckIcon';
import classNames from 'classnames';
import { Localized } from '@fluent/react';
import {
  FlightListStepId,
  FlightListTrackerResetT,
  ResetType,
} from 'solarxr-protocol';
import { ReactNode } from 'react';
import { Typography } from './commons/Typography';
import { Button } from './commons/Button';
import { ResetButton } from './home/ResetButton';

function Step({
  step: { status, id, optional, firstInvalid: firstActive },
  index,
  children,
}: {
  step: FlightListStep;
  index: number;
  children: ReactNode;
}) {
  return (
    <div className="flex flex-col border-l border-gray-700 ml-2.5 pb-2 last:pb-0">
      <div className="flex w-full gap-2 -ml-2.5">
        <div
          className={classNames(
            'p-1 h-5 w-5 rounded-full fill-background-10 flex items-center justify-center',
            status === 'complete' && 'bg-status-success',
            status === 'blocked' && 'bg-orange-300',
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
      {firstActive && <div className="ml-2 py-2">{children}</div>}
    </div>
  );
}

const stepContentLookup: Record<
  number,
  (step: FlightListStep, context: SessionFlightListContext) => JSX.Element
> = {
  [FlightListStepId.FULL_RESET]: (step) => {
    const data = step.extraData as FlightListTrackerResetT;
    return (
      <div className="space-y-2">
        <Typography>Some Trackers need a reset to be performed</Typography>
        <div className="flex justify-center">
          <ResetButton type={ResetType.Full} size="small"></ResetButton>
        </div>
      </div>
    );
  },
  [FlightListStepId.STEAMVR_DISCONNECTED]: (step, { toggle }) => {
    return (
      <>
        <div className="space-y-2">
          <Typography>
            SteamVR is not running. Are you using it for vr?
          </Typography>
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button variant="primary">Launch SteamVR</Button>
            <Button variant="secondary" onClick={() => toggle(step.id)}>
              I do not use SteamVR
            </Button>
          </div>
        </div>
      </>
    );
  },
};

export function SessionFlightList() {
  const context = useSessionFlightlist();
  const { steps } = context;

  return (
    <div className="flex flex-col">
      {steps.map((step, index) => (
        <Step step={step} index={index + 1} key={step.id}>
          {stepContentLookup[step.id]?.(step, context) || undefined}
        </Step>
      ))}
    </div>
  );
}
