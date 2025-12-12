import {
  TrackingChecklistStep,
  TrackingChecklistContext,
  useTrackingChecklist,
  trackingchecklistIdtoLabel,
} from '@/hooks/tracking-checklist';
import classNames from 'classnames';
import {
  ResetType,
  TrackingChecklistPublicNetworksT,
  TrackingChecklistStepId,
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
import {
  ArrowDownIcon,
  ArrowRightIcon,
} from '@/components/commons/icon/ArrowIcons';
import { Localized } from '@fluent/react';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { TrackingChecklistModal } from './TrackingChecklistModal';
import { NavLink, useNavigate } from 'react-router-dom';
import { useBreakpoint } from '@/hooks/breakpoint';

function Step({
  step: { status, id, optional, firstRequired },
  children,
}: {
  step: TrackingChecklistStep;
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
          {status === 'skipped' && <CheckIcon size={10} />}
          {status === 'complete' && <CheckIcon size={10} />}
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
          <Localized id={trackingchecklistIdtoLabel[id]} />
          {canBeOpened && (
            <div className="fill-background-30 group-hover:scale-125 group-hover:fill-background-20 transition-transform">
              <ArrowDownIcon size={20} />
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
  (
    step: TrackingChecklistStep,
    context: TrackingChecklistContext
  ) => JSX.Element
> = {
  [TrackingChecklistStepId.TRACKERS_REST_CALIBRATION]: (
    step,
    { toggleSession }
  ) => {
    return (
      <div className="space-y-2.5">
        <Typography id="tracking_checklist-TRACKERS_REST_CALIBRATION-desc" />
        <div className="flex justify-end">
          {step.ignorable && (
            <Button
              id="tracking_checklist-ignore"
              variant="secondary"
              onClick={() => toggleSession(step.id)}
            />
          )}
        </div>
      </div>
    );
  },
  [TrackingChecklistStepId.FULL_RESET]: () => {
    return (
      <div className="space-y-2.5">
        <Typography id="tracking_checklist-FULL_RESET-desc" />
        <div>
          <Typography id="onboarding-automatic_mounting-preparation-v2-step-0" />
          <Typography id="onboarding-automatic_mounting-preparation-v2-step-1" />
          <Typography id="onboarding-automatic_mounting-preparation-v2-step-2" />
        </div>
        <div className="grid grid-cols-3 py-1.5 gap-2">
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-52">
            <CheckIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success" />
            <img
              src="/images/reset/FullResetPose.webp"
              className="h-full object-contain scale-110"
              alt="Reset position"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-52">
            <CheckIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success" />
            <img
              src="/images/reset/FullResetPoseSide.webp"
              className="h-full object-contain scale-110"
              alt="Reset position side"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md relative max-h-52">
            <CrossIcon className="md:w-9 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-critical" />
            <img
              src="/images/reset/FullResetPoseWrong.webp"
              className="h-full object-contain scale-110"
              alt="Reset position wrong"
            />
          </div>
        </div>
        <div className="flex">
          <ResetButton type={ResetType.Full} />
        </div>
      </div>
    );
  },
  [TrackingChecklistStepId.STEAMVR_DISCONNECTED]: (step, { toggleSession }) => {
    return (
      <>
        <div className="space-y-2.5">
          <Typography id="tracking_checklist-STEAMVR_DISCONNECTED-desc" />
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              id="tracking_checklist-STEAMVR_DISCONNECTED-open"
              variant="primary"
              onClick={() => openUrl('steam://run/250820')}
            />
            {step.ignorable && (
              <Button
                id="tracking_checklist-ignore"
                variant="secondary"
                onClick={() => toggleSession(step.id)}
              />
            )}
          </div>
        </div>
      </>
    );
  },
  [TrackingChecklistStepId.TRACKER_ERROR]: () => {
    return <Typography id="tracking_checklist-TRACKER_ERROR-desc" />;
  },
  [TrackingChecklistStepId.UNASSIGNED_HMD]: () => {
    return <Typography id="tracking_checklist-UNASSIGNED_HMD-desc" />;
  },
  [TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC]: (
    step,
    { toggleSession }
  ) => {
    const data = step.extraData as TrackingChecklistPublicNetworksT | null;
    return (
      <>
        <div className="space-y-2.5">
          <Typography
            id="tracking_checklist-NETWORK_PROFILE_PUBLIC-desc"
            vars={{
              count: data?.adapters?.length ?? 0,
              adapters: data?.adapters?.join(', ') ?? '',
            }}
            elems={{
              PublicFixLink: (
                <A
                  className="text-background-20"
                  href="https://docs.slimevr.dev/common-issues.html#network-profile-is-currently-set-to-public"
                />
              ),
            }}
            whitespace="whitespace-pre-wrap"
          />
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              id="tracking_checklist-NETWORK_PROFILE_PUBLIC-open"
              variant="primary"
              onClick={() => openUrl('ms-settings:network')}
            />
            {step.ignorable && (
              <Button
                id="tracking_checklist-ignore"
                variant="secondary"
                onClick={() => toggleSession(step.id)}
              />
            )}
          </div>
        </div>
      </>
    );
  },
  [TrackingChecklistStepId.VRCHAT_SETTINGS]: (step, { toggleSession }) => {
    return (
      <>
        <div className="space-y-2.5">
          <Typography id="tracking_checklist-VRCHAT_SETTINGS-desc" />
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row flex-wrap">
            <Button
              variant="primary"
              to="/vrc-warnings"
              id="tracking_checklist-VRCHAT_SETTINGS-open"
            />
            {step.ignorable && (
              <Button
                id="tracking_checklist-ignore"
                variant="secondary"
                onClick={() => toggleSession(step.id)}
              />
            )}
          </div>
        </div>
      </>
    );
  },
  [TrackingChecklistStepId.MOUNTING_CALIBRATION]: (step, { toggleSession }) => {
    return (
      <div className="space-y-2.5">
        <Typography id="onboarding-automatic_mounting-mounting_reset-step-0" />
        <Typography id="onboarding-automatic_mounting-mounting_reset-step-1" />
        <div className="flex w-full justify-center">
          <img
            src="/images/mounting-reset-pose.webp"
            className="h-44"
            alt="mounting reset ski pose"
          />
        </div>
        <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
          <ResetButton type={ResetType.Mounting} group="default" />
          {step.ignorable && (
            <Button
              id="tracking_checklist-ignore"
              variant="secondary"
              onClick={() => toggleSession(step.id)}
            />
          )}
        </div>
      </div>
    );
  },
  [TrackingChecklistStepId.FEET_MOUNTING_CALIBRATION]: (
    step,
    { toggleSession }
  ) => {
    return (
      <div className="space-y-2.5">
        <Typography id="onboarding-automatic_mounting-mounting_reset-feet-step-0" />
        <Typography id="onboarding-automatic_mounting-mounting_reset-feet-step-1" />
        <div className="flex w-full gap-2">
          <div className="flex flex-col bg-background-80 rounded-md w-full">
            <img
              src="/images/mounting/MountingFeets.webp"
              className="h-44 object-contain"
              alt="mounting reset ski pose"
            />
          </div>
          <div className="flex flex-col bg-background-80 rounded-md w-full">
            <img
              src="/images/mounting/MountingFeetsSide.webp"
              className="h-44 object-contain"
              alt="mounting reset ski pose"
            />
          </div>
        </div>
        <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
          <ResetButton type={ResetType.Mounting} group="feet" />
          {step.ignorable && (
            <Button
              id="tracking_checklist-ignore"
              variant="secondary"
              onClick={() => toggleSession(step.id)}
            />
          )}
        </div>
      </div>
    );
  },
  [TrackingChecklistStepId.STAY_ALIGNED_CONFIGURED]: (
    step,
    { toggleSession }
  ) => {
    return (
      <>
        <div className="space-y-2.5">
          <Typography id="tracking_checklist-STAY_ALIGNED_CONFIGURED-desc" />
          <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
            <Button
              id="tracking_checklist-STAY_ALIGNED_CONFIGURED-open"
              variant="primary"
              to="/onboarding/stay-aligned"
              state={{ alonePage: true }}
            />
            {step.ignorable && (
              <Button
                id="tracking_checklist-ignore"
                variant="secondary"
                onClick={() => toggleSession(step.id)}
              />
            )}
          </div>
        </div>
      </>
    );
  },
};

export function TrackingChecklistMobile() {
  const context = useTrackingChecklist();
  const { completion, firstRequired, warnings } = context;

  return (
    <div style={{ gridArea: 'l' }}>
      <NavLink
        to="/checklist"
        className={classNames(
          'bg-accent-background-30 h-full flex items-center justify-between px-2 fill-background-10 no-underline',
          {
            'bg-status-critical': completion === 'incomplete',
            'bg-status-warning text-background-90 fill-background-90':
              completion === 'partial',
          }
        )}
      >
        <div className={'flex flex-col justify-center'}>
          {completion === 'incomplete' ? 'Required:' : 'Warning:'}{' '}
          <Localized
            id={
              trackingchecklistIdtoLabel[
                firstRequired?.id ?? warnings[0]?.id ?? 0
              ]
            }
          />
        </div>
        <ArrowRightIcon />
      </NavLink>
    </div>
  );
}

export function TrackingChecklist({
  closable = true,
  closed,
  closing,
  toggleClosed,
}: {
  closable?: boolean;
  closed: boolean;
  closing: boolean;
  toggleClosed: () => void;
}) {
  const context = useTrackingChecklist();
  const { visibleSteps, progress, completion, warnings } = context;

  const slimeState = useMemo(() => {
    if (completion === 'complete') return SlimeState.HAPPY;
    if (completion === 'incomplete') return SlimeState.CURIOUS;
    if (completion === 'partial') return SlimeState.SAD;
    return SlimeState.HAPPY;
  }, [completion]);

  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;

  return (
    <>
      <div
        className={classNames(
          {
            'overflow-y-auto': !closing && !closed,
          },
          'h-full w-full flex flex-col overflow-x-clip pt-1'
        )}
      >
        <div
          className={classNames(
            'flex pl-3 pr-2 pb-2 pt-1 justify-between items-center'
          )}
        >
          <div className="gap-2 flex fill-background-40">
            <Typography variant="section-title" id="tracking_checklist" />
          </div>
          <div className="flex gap-1">
            <div
              className="flex gap-1 items-center justify-center fill-background-40 hover:fill-background-30 cursor-pointer rounded-full w-8 h-8 hover:bg-background-50"
              onClick={() => setSettingsOpen(true)}
            >
              <WrenchIcon width={15} />
            </div>
            {closable && (
              <div
                className="flex gap-1 items-center justify-center fill-background-40 hover:fill-background-30 cursor-pointer rounded-full w-8 h-8 hover:bg-background-50"
                onClick={() => toggleClosed()}
              >
                {closed && <ArrowDownIcon size={25} />}
                {!closed && <CrossIcon size={25} />}
              </div>
            )}
          </div>
        </div>
        <div
          className={classNames('transition-all duration-500 delay-100', {
            'opacity-0 h-0': closed,
          })}
        >
          {visibleSteps.map((step, index) => (
            <Step step={step} index={index + 1} key={step.id}>
              {stepContentLookup[step.id]?.(step, context) || undefined}
            </Step>
          ))}
        </div>
        <div
          className={classNames(
            'flex flex-col flex-grow  border-l-[2px] justify-end ml-6 transition-all duration-500 delay-100',
            {
              'pt-3 border-background-50': !closed,
              'border-transparent': closed,
              'border-dashed': completion === 'incomplete',
            }
          )}
        >
          <div
            className={classNames('flex w-full gap-2 z-10', {
              'cursor-pointer': closed,
              'pointer-events-none': !closed,
            })}
            onClick={() => toggleClosed()}
          >
            <div className="rounded-full bg-background-50 flex items-center justify-center h-[25px] w-[25px] -ml-[13px] relative">
              <div
                className={classNames('h-[12px] w-[12px] rounded-full', {
                  'bg-status-success': completion === 'complete',
                  'bg-status-critical animate-pulse':
                    completion === 'incomplete',
                  'bg-status-warning animate-pulse': completion === 'partial',
                })}
              />
            </div>
            <div className={'flex flex-col justify-center'}>
              {completion === 'incomplete' && (
                <Typography
                  variant="section-title"
                  id="tracking_checklist-status-incomplete"
                />
              )}
              {completion === 'partial' && (
                <Typography
                  variant="section-title"
                  id="tracking_checklist-status-partial"
                  vars={{ count: warnings.length }}
                />
              )}
              {completion == 'complete' && (
                <Typography
                  variant="section-title"
                  id="tracking_checklist-status-complete"
                />
              )}
            </div>
          </div>
        </div>
        <div
          className={classNames('w-full flex relative p-3 pr-12', {
            'pt-0': closed,
          })}
        >
          {!closed && (
            <ProgressBar
              progress={progress}
              colorClass={
                completion === 'incomplete'
                  ? 'bg-accent-background-20'
                  : completion === 'partial'
                    ? 'bg-status-warning'
                    : 'bg-status-success'
              }
            />
          )}

          <div className="absolute bottom-0 right-0 w-20 h-20 overflow-clip pointer-events-none">
            <div className="-rotate-45 translate-x-3.5 translate-y-3.5">
              <LoaderIcon slimeState={slimeState} />
            </div>
          </div>
        </div>
      </div>
      <TrackingChecklistModal open={settingsOpenState} />
    </>
  );
}

export function ChecklistPage() {
  const nav = useNavigate();
  const { isMobile } = useBreakpoint('mobile');

  useEffect(() => {
    if (!isMobile) nav('/');
  }, [isMobile]);

  return (
    <div className="rounded-t-lg h-full">
      <TrackingChecklist
        closable={false}
        closed={false}
        closing={false}
        toggleClosed={() => {}}
      />
    </div>
  );
}
