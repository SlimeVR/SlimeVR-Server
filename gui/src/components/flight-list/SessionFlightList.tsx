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
import { ReactNode, useEffect, useMemo, useState } from 'react';
import { openUrl } from '@tauri-apps/plugin-opener';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { ResetButton } from '@/components/home/ResetButton';
import { A } from '@/components/commons/A';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';
import { Localized } from '@fluent/react';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';

function Step({
  step: { status, id, optional, firstRequired },
  children,
}: {
  step: FlightListStep;
  index: number;
  children: ReactNode;
}) {
  const [open, setOpen] = useState(firstRequired);

  const canBeOpened =
    (status === 'skipped' || status === 'invalid') && !firstRequired;

  useEffect(() => {
    if (!canBeOpened) setOpen(false);
  }, [open]);

  return (
    <div
      className={classNames(
        'flex flex-col pr-2 ml-6 last:pb-0 pb-3 border-l-[2px] border-background-50',
        status !== 'complete' || (firstRequired && 'border-dashed')
      )}
    >
      <div
        className={classNames(
          'flex w-full gap-2 ',
          canBeOpened && 'group cursor-pointer'
        )}
        onClick={() => {
          if (canBeOpened) setOpen((open) => !open);
        }}
      >
        <div
          className={classNames(
            'p-1 rounded-full fill-background-10 flex items-center justify-center z-10 h-[25px] w-[25px] -ml-[13px]',
            status === 'complete' && 'bg-accent-background-20',
            status === 'blocked' && 'bg-background-50',
            status === 'skipped' && 'bg-background-50 fill-background-30',
            status === 'invalid' && !optional && 'bg-background-50',
            status === 'invalid' && optional && 'bg-background-50'
          )}
        >
          {status === 'skipped' && <CheckIcon size={10}></CheckIcon>}
          {status === 'complete' && <CheckIcon size={10}></CheckIcon>}
          {(status === 'invalid' || status === 'blocked') && (
            <div
              className={classNames(
                'h-[12px] w-[12px] rounded-full',
                optional && 'bg-background-40',
                !optional &&
                  'bg-accent-background-10 animate-pulse brightness-75'
              )}
            />
          )}
        </div>
        <div className="flex items-center justify-between w-full group-hover:text-background-20 text-section-title">
          <Localized id={flightlistIdtoLabel[id]} />
          {canBeOpened && (
            <div className="fill-background-30 group-hover:scale-125 group-hover:fill-background-20 transition-transform">
              <ArrowDownIcon size={20}></ArrowDownIcon>
            </div>
          )}
        </div>
      </div>
      {(firstRequired || open) && children && (
        <div className="pt-2 pl-5">{children}</div>
      )}
    </div>
  );
}

const stepContentLookup: Record<
  number,
  (step: FlightListStep, context: SessionFlightListContext) => JSX.Element
> = {
  [FlightListStepId.TRACKERS_REST_CALIBRATION]: (step, { toggle }) => {
    return (
      <div className="space-y-2.5">
        <Typography id="flight_list-TRACKERS_REST_CALIBRATION-desc"></Typography>
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
          <Typography
            color="secondary"
            id="onboarding-automatic_mounting-preparation-v2-step-0"
          ></Typography>
          <Typography
            color="secondary"
            id="onboarding-automatic_mounting-preparation-v2-step-1"
          ></Typography>
          <Typography
            color="secondary"
            id="onboarding-automatic_mounting-preparation-v2-step-2"
          ></Typography>
        </div>
        <div className="grid grid-cols-3 py-1.5 gap-2">
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-52">
            <CheckIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success"></CheckIcon>
            <img
              src="/images/reset/FullResetPose.webp"
              className="h-full object-contain scale-110"
              alt="Reset position"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-52">
            <CheckIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success"></CheckIcon>
            <img
              src="/images/reset/FullResetPoseSide.webp"
              className="h-full object-contain scale-110"
              alt="Reset position side"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-52">
            <CrossIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-critical"></CrossIcon>
            <img
              src="/images/reset/FullResetPoseWrong.webp"
              className="h-full object-contain scale-110"
              alt="Reset position wrong"
            />
          </div>
        </div>
        <div className="flex">
          <ResetButton type={ResetType.Full}></ResetButton>
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
          <ResetButton type={ResetType.Mounting}></ResetButton>
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
  [FlightListStepId.STAY_ALIGNED_CONFIGURED]: (step, { toggle }) => {
    return (
      <>
        <div className="space-y-2.5">
          <Typography id="flight_list-STAY_ALIGNED_CONFIGURED-desc"></Typography>
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              id="flight_list-STAY_ALIGNED_CONFIGURED-open"
              variant="primary"
              to="/onboarding/stay-aligned"
              state={{ alonePage: true }}
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
};

export function SessionFlightList() {
  const context = useSessionFlightlist();
  const { visibleSteps } = context;

  const progress = useMemo(() => {
    const completeSteps = visibleSteps.filter(
      (step) => step.status === 'complete' || step.status === 'skipped'
    );
    return Math.min(1, completeSteps.length / visibleSteps.length);
  }, [visibleSteps]);

  const completion = useMemo(() => {
    if (
      progress === 1 &&
      visibleSteps.find((step) => step.status === 'skipped')
    )
      return 'partial';
    return progress === 1 ? 'complete' : 'incomplete';
  }, [progress, visibleSteps]);

  const slimeState = useMemo(() => {
    if (completion === 'complete') return SlimeState.HAPPY;
    if (completion === 'incomplete') return SlimeState.CURIOUS;
    return SlimeState.HAPPY;
  }, [completion]);

  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;

  return (
    <>
      <div className="h-full w-full flex flex-col overflow-y-auto overflow-x-clip pt-3">
        <div className="flex pl-3 pr-3 pb-2 justify-between items-center">
          <div className="gap-2 flex fill-background-40">
            <Typography variant="section-title">Tracking checklist</Typography>
          </div>
          <div
            className="flex gap-1 items-center fill-background-40 underline hover:fill-background-30 cursor-pointer rounded-full p-2 hover:bg-background-50"
            onClick={() => setSettingsOpen(true)}
          >
            <WrenchIcon width={15}></WrenchIcon>
          </div>
        </div>
        <div className="contents">
          {visibleSteps.map((step, index) => (
            <Step step={step} index={index + 1} key={step.id}>
              {stepContentLookup[step.id]?.(step, context) || undefined}
            </Step>
          ))}
        </div>
        <div
          className={classNames(
            'flex flex-col flex-grow justify-end border-l-[2px] border-background-50 ml-6 pt-3',
            completion === 'incomplete' && 'border-dashed'
          )}
        >
          <div className="flex w-full gap-2">
            <div className="rounded-full bg-background-50 flex items-center justify-center h-[25px] w-[25px] -ml-[13px]">
              <div
                className={classNames(
                  'h-[12px] w-[12px] rounded-full',
                  completion !== 'incomplete'
                    ? 'bg-status-success'
                    : 'bg-status-critical animate-pulse'
                )}
              ></div>
            </div>
            <div className="flex flex-col justify-center">
              {completion === 'incomplete' && (
                <Typography variant="section-title">
                  You are not prepared to use SlimeVR!
                </Typography>
              )}
              {(completion == 'complete' || completion === 'partial') && (
                <Typography variant="section-title">
                  You are prepared to use SlimeVR!
                </Typography>
              )}
            </div>
          </div>
        </div>
        <div className="w-full flex relative p-3 pr-12">
          <ProgressBar
            progress={progress}
            colorClass={
              completion !== 'incomplete'
                ? 'bg-status-success'
                : 'bg-accent-background-20'
            }
          ></ProgressBar>
          <div className="absolute bottom-0 right-0 w-20 h-20 overflow-clip">
            <div className="-rotate-45 translate-x-3.5 translate-y-3.5">
              <LoaderIcon slimeState={slimeState}></LoaderIcon>
            </div>
          </div>
        </div>
      </div>
      {/* <FlightListSettingsModal
        open={settingsOpenState}
      ></FlightListSettingsModal> */}
    </>
  );
}
