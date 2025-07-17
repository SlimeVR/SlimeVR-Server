import {
  flightlistIdtoLabel,
  FlightListStep,
  SessionFlightListContext,
  useSessionFlightlist,
} from '@/hooks/session-flightlist';
import classNames from 'classnames';
import {
  FlightListPublicNetworksT,
  FlightListStepId,
  ResetType,
} from 'solarxr-protocol';
import { ReactNode } from 'react';
import { openUrl } from '@tauri-apps/plugin-opener';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { ResetButton } from '@/components/home/ResetButton';
import { A } from '@/components/commons/A';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { Localized } from '@fluent/react';

function Step({
  step: { status, id, optional, firstInvalid },
  children,
}: {
  step: FlightListStep;
  index: number;
  children: ReactNode;
}) {
  return (
    <div className={classNames('flex flex-col relative px-3 fist:h-[600px]')}>
      <div className="absolute left-[23px] top-0 border-l-[2px] border-gray-700 border-dashed h-full"></div>
      <div className="flex w-full gap-2">
        <div
          className={classNames(
            'p-1 h-6 w-6 rounded-full fill-background-10 flex items-center justify-center',
            status === 'complete' && 'bg-accent-background-20',
            status === 'blocked' && 'bg-background-50',
            status === 'skipped' && 'bg-background-50',
            status === 'invalid' && !optional && 'bg-background-50',
            status === 'invalid' && optional && 'bg-background-50'
          )}
        >
          {status === 'complete' && <CheckIcon size={10}></CheckIcon>}
          {status !== 'complete' && (
            <div
              className={classNames(
                'h-3 w-3 rounded-full',
                optional && 'bg-background-40',
                (status === 'skipped' || status === 'blocked' || !optional) &&
                  'bg-accent-background-10 animate-pulse brightness-75'
              )}
            />
          )}
        </div>
        <div className="flex flex-col justify-center">
          <Typography
            id={flightlistIdtoLabel[id]}
            variant="section-title"
          ></Typography>
        </div>
      </div>

      {(firstInvalid || (status === 'invalid' && optional)) && children && (
        <div className="pt-2 pl-8">{children}</div>
      )}
    </div>
  );
}

const stepContentLookup: Record<
  number,
  (step: FlightListStep, context: SessionFlightListContext) => JSX.Element
> = {
  [FlightListStepId.TRACKERS_CALIBRATION]: (step, { toggle }) => {
    return (
      <div className="space-y-2.5">
        <Typography id="flight_list-TRACKERS_CALIBRATION-desc"></Typography>
        <div className="flex justify-end">
          {step.ignorable && (
            <Button
              id="flight_list-ignore"
              variant="secondary"
              onClick={() => toggle(step.id)}
            ></Button>
          )}
        </div>
      </div>
    );
  },
  [FlightListStepId.FULL_RESET]: () => {
    return (
      <div className="space-y-2.5">
        <Typography id="flight_list-FULL_RESET-desc"></Typography>
        <div>
          <Localized id="onboarding-automatic_mounting-preparation-v2-step-0">
            <Typography color="secondary"></Typography>
          </Localized>
          <Localized id="onboarding-automatic_mounting-preparation-v2-step-1">
            <Typography color="secondary"></Typography>
          </Localized>
          <Localized id="onboarding-automatic_mounting-preparation-v2-step-2">
            <Typography color="secondary"></Typography>
          </Localized>
        </div>
        <div className="grid grid-cols-3 py-1.5 gap-2">
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-64">
            <CheckIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success"></CheckIcon>
            <img
              src="/images/reset/FullResetPose.webp"
              className="h-full object-contain scale-110"
              alt="Reset position"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-64">
            <CheckIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success"></CheckIcon>
            <img
              src="/images/reset/FullResetPoseSide.webp"
              className="h-full object-contain scale-110"
              alt="Reset position side"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-64">
            <CrossIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-critical"></CrossIcon>
            <img
              src="/images/reset/FullResetPoseWrong.webp"
              className="h-full object-contain scale-110"
              alt="Reset position wrong"
            />
          </div>
        </div>
        <div className="flex justify-center">
          <ResetButton type={ResetType.Full} size="small"></ResetButton>
        </div>
      </div>
    );
  },
  [FlightListStepId.STEAMVR_DISCONNECTED]: (step, { toggle }) => {
    return (
      <>
        <div className="space-y-2.5">
          <Typography id="flight_list-STEAMVR_DISCONNECTED-desc"></Typography>
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              id="flight_list-STEAMVR_DISCONNECTED-open"
              variant="primary"
              onClick={() => openUrl('steam://run/250820')}
            ></Button>
            {step.ignorable && (
              <Button
                id="flight_list-ignore"
                variant="secondary"
                onClick={() => toggle(step.id)}
              ></Button>
            )}
          </div>
        </div>
      </>
    );
  },
  [FlightListStepId.TRACKER_ERROR]: () => {
    return <Typography id="flight_list-TRACKER_ERROR-desc"></Typography>;
  },
  [FlightListStepId.UNASSIGNED_HMD]: () => {
    return <Typography id="flight_list-UNASSIGNED_HMD-desc"></Typography>;
  },
  [FlightListStepId.NETWORK_PROFILE_PUBLIC]: (step, { toggle }) => {
    const data = step.extraData as FlightListPublicNetworksT | null;
    return (
      <>
        <div className="space-y-2.5">
          <Typography
            id="flight_list-NETWORK_PROFILE_PUBLIC-desc"
            vars={{
              count: data?.adapters?.length ?? 0,
              adapters: data?.adapters?.join(', ') ?? '',
            }}
            elems={{
              PublicFixLink: (
                <A
                  className="text-background-20"
                  href="https://docs.slimevr.dev/common-issues.html#network-profile-is-currently-set-to-public"
                ></A>
              ),
            }}
            whitespace="whitespace-pre-wrap"
          ></Typography>
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              id="flight_list-NETWORK_PROFILE_PUBLIC-open"
              variant="primary"
              onClick={() => openUrl('ms-settings:network')}
            ></Button>
            {step.ignorable && (
              <Button
                id="flight_list-ignore"
                variant="secondary"
                onClick={() => toggle(step.id)}
              ></Button>
            )}
          </div>
        </div>
      </>
    );
  },
  [FlightListStepId.VRCHAT_SETTINGS]: (step, { toggle }) => {
    return (
      <>
        <div className="space-y-2.5">
          <Typography id="flight_list-VRCHAT_SETTINGS-desc"></Typography>
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              variant="primary"
              to="/vrc-warnings"
              id="flight_list-VRCHAT_SETTINGS-open"
            ></Button>
            {step.ignorable && (
              <Button
                id="flight_list-ignore"
                variant="secondary"
                onClick={() => toggle(step.id)}
              ></Button>
            )}
          </div>
        </div>
      </>
    );
  },
  [FlightListStepId.MOUNTING_CALIBRATION]: (step, { toggle }) => {
    return (
      <div className="space-y-2.5">
        <Typography id="onboarding-automatic_mounting-mounting_reset-step-0"></Typography>
        <Typography id="onboarding-automatic_mounting-mounting_reset-step-1"></Typography>
        <div className="flex w-full justify-center">
          <img
            src="/images/mounting-reset-pose.webp"
            className="h-44"
            alt="mounting reset ski pose"
          />
        </div>
        <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
          <ResetButton size="small" type={ResetType.Mounting}></ResetButton>
          {step.ignorable && (
            <Button
              id="flight_list-ignore"
              variant="secondary"
              onClick={() => toggle(step.id)}
            ></Button>
          )}
        </div>
      </div>
    );
  },
};

export function SessionFlightList() {
  const context = useSessionFlightlist();
  const { steps } = context;

  return (
    <div className="relative h-full w-full overflow-y-auto">
      <div className="flex flex-col pt-4 h-full overflow-clip">
        {steps.map((step, index) => (
          <Step step={step} index={index + 1} key={step.id}>
            {stepContentLookup[step.id]?.(step, context) || undefined}
          </Step>
        ))}
        <div
          className={classNames('flex-grow flex-col justify-end flex relative')}
        >
          {/* <div className="absolute left-2 border-l-[2px] border-gray-700 border-dashed"></div> */}
          <div className="flex w-full gap-2">
            <div className="h-6 w-6 rounded-full bg-status-critical fill-background-10 flex items-center justify-center">
              <CheckIcon />
            </div>
            <div className="flex flex-col justify-center">
              <Typography variant="section-title">
                You are not prepared to use SlimeVR!
              </Typography>
            </div>
          </div>
          <div className="bottom-0 left-0 w-full">
            <ProgressBar progress={0.5} height={8} bottom></ProgressBar>
            <div className="absolute -bottom-4 -right-4 -rotate-45">
              <LoaderIcon slimeState={SlimeState.CURIOUS}></LoaderIcon>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
