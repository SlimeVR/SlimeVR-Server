import {
  TrackingChecklistStep,
  TrackingChecklistContext,
  useTrackingChecklist,
  trackingchecklistIdtoLabel,
} from '@/hooks/tracking-checklist';
import classNames from 'classnames';
import {
  EnableSteamVRDriverRequestT,
  ResetType,
  RpcMessage,
  TrackingChecklistPublicNetworksT,
  TrackingChecklistSteamVRDisconnectedT,
  TrackingChecklistStepId,
  BodyPart,
  RoutingOutput,
  BoneRoutingSettingsRequestT,
  BoneRoutingSettingsResponseT,
  BoneRouteT,
  ChangeBoneRoutingSettingsRequestT,
  MountingMethod,
} from 'solarxr-protocol';
import {
  PoseMountingInstructions,
  PoseMountingVideo,
} from '@/components/mounting/PoseMountingVideo';
import { MountingMethodRadio } from '@/components/mounting/MountingMethodRadio';
import { useResetsSettings } from '@/hooks/resets-settings';
import { ReactNode, useEffect, useMemo, useState } from 'react';
import { Timeline, TimelineItem } from '@/components/commons/Timeline';
import { FullResetExamples } from '@/components/commons/FullResetExamples';
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
import { Localized, useLocalization } from '@fluent/react';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { IconButton } from '@/components/commons/IconButton';
import { TrackingChecklistModal } from './TrackingChecklistModal';
import { NavLink, useNavigate } from 'react-router-dom';
import { useBreakpoint } from '@/hooks/breakpoint';
import { openUrl } from '@/hooks/crossplatform';
import { useWebsocketAPI } from '@/hooks/websocket-api';

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

  const state =
    status === 'complete'
      ? 'done'
      : status === 'skipped'
        ? 'skipped'
        : !optional
          ? 'current'
          : 'todo';

  return (
    <TimelineItem
      size="sm"
      state={state}
      title={<Localized id={trackingchecklistIdtoLabel[id]} />}
      disabled={!canBeOpened}
      expanded={open}
      onToggle={() => setOpen((open) => !open)}
    >
      {(firstRequired || open) && children && (
        <div className="pt-2">{children}</div>
      )}
    </TimelineItem>
  );
}

function SteamVRDisconnected({
  step,
  context,
}: {
  step: TrackingChecklistStep;
  context: TrackingChecklistContext;
}) {
  const { sendRPCPacket } = useWebsocketAPI();
  const data = step.extraData as TrackingChecklistSteamVRDisconnectedT | null;

  const enableDriver = () => {
    sendRPCPacket(
      RpcMessage.EnableSteamVRDriverRequest,
      new EnableSteamVRDriverRequestT()
    );
  };

  const driverNotInstalled = data?.driverInstalled === false;
  const driverBlocked = data?.driverBlockedBySafeMode === true;
  const driverDisabled = data?.driverEnabled === false;

  const showEnableDriverButton = driverBlocked || driverDisabled;

  const getDescriptionId = () => {
    if (driverBlocked)
      return 'tracking_checklist-STEAMVR_DISCONNECTED-driver_blocked-desc';
    if (driverDisabled)
      return 'tracking_checklist-STEAMVR_DISCONNECTED-driver_disabled-desc';
    if (driverNotInstalled)
      return 'tracking_checklist-STEAMVR_DISCONNECTED-driver_not_installed-desc';
    return 'tracking_checklist-STEAMVR_DISCONNECTED-desc';
  };

  return (
    <div className="space-y-2.5">
      <Typography id={getDescriptionId()} />
      <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
        {showEnableDriverButton && (
          <Button
            id="tracking_checklist-STEAMVR_DISCONNECTED-enable"
            variant="primary"
            onClick={enableDriver}
          />
        )}
        {!showEnableDriverButton && !driverNotInstalled && (
          <Button
            id="tracking_checklist-STEAMVR_DISCONNECTED-open"
            variant="primary"
            onClick={() => openUrl('steam://run/250820')}
          />
        )}
        {step.ignorable && (
          <Button
            id="tracking_checklist-ignore"
            variant="secondary"
            onClick={() => context.toggleSession(step.id)}
          />
        )}
      </div>
    </div>
  );
}

function SteamVRHandsEnabled() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [routing, setRouting] = useState<BoneRoutingSettingsResponseT | null>(
    null
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.BoneRoutingSettingsRequest,
      new BoneRoutingSettingsRequestT()
    );
  }, []);

  useRPCPacket(
    RpcMessage.BoneRoutingSettingsResponse,
    (settings: BoneRoutingSettingsResponseT) => setRouting(settings)
  );

  const disableHandTrackers = () => {
    if (!routing) return;

    const req = new ChangeBoneRoutingSettingsRequestT();
    req.automatic = routing.automatic;
    req.routes = (routing.routes ?? [])
      .map((route) => {
        const next = new BoneRouteT();
        next.bone = route.bone;
        next.outputs =
          route.bone === BodyPart.LEFT_HAND ||
          route.bone === BodyPart.RIGHT_HAND
            ? (route.outputs ?? []).filter(
                (output) => output !== RoutingOutput.DRIVER
              )
            : (route.outputs ?? []);
        return next;
      })
      .filter((route) => route.outputs.length > 0);

    sendRPCPacket(RpcMessage.ChangeBoneRoutingSettingsRequest, req);
  };

  return (
    <div className="space-y-2.5">
      <Typography id="tracking_checklist-STEAMVR_HANDS_ENABLED-desc" />
      <div className="flex">
        <Button
          id="tracking_checklist-STEAMVR_HANDS_ENABLED-go"
          variant="primary"
          onClick={disableHandTrackers}
        />
      </div>
    </div>
  );
}

function StandableInstalled() {
  const { l10n } = useLocalization();

  return (
    <div className="space-y-2.5">
      {l10n
        .getString('tracking_checklist-STANDABLE_INSTALLED-desc')
        .split('\n')
        .map((line, i) => (
          <Typography key={i}>{line}</Typography>
        ))}
    </div>
  );
}

function MountingCalibrationStep({
  step,
  toggleSession,
}: {
  step: TrackingChecklistStep;
  toggleSession: TrackingChecklistContext['toggleSession'];
}) {
  const { resetsSettings } = useResetsSettings();
  const isStep = resetsSettings?.mountingMethod === MountingMethod.STEP;

  return (
    <div className="space-y-2.5">
      {isStep ? (
        <>
          <Typography id="onboarding-step_mounting-step-0" />
          <Typography id="onboarding-step_mounting-step-1" />
          <div className="flex w-full justify-center">
            <video
              autoPlay
              muted
              loop
              playsInline
              className="h-44"
              src="/videos/step-mounting.webm"
            />
          </div>
        </>
      ) : (
        <>
          <PoseMountingInstructions />
          <div className="flex w-full justify-center">
            <PoseMountingVideo className="h-56" />
          </div>
        </>
      )}
      <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
        <ResetButton type={ResetType.MOUNTING} group="default" />
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
        <FullResetExamples
          className="py-1.5"
          tileClassName="bg-background-80 max-h-64"
        />
        <div className="flex">
          <ResetButton type={ResetType.FULL} />
        </div>
      </div>
    );
  },
  [TrackingChecklistStepId.STEAMVR_DISCONNECTED]: (step, context) => {
    return <SteamVRDisconnected step={step} context={context} />;
  },
  [TrackingChecklistStepId.TRACKER_ERROR]: () => {
    return <Typography id="tracking_checklist-TRACKER_ERROR-desc" />;
  },
  [TrackingChecklistStepId.UNASSIGNED_RELIABLE_REFERENCE]: () => {
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
              to="/settings/vrc-warnings"
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
  [TrackingChecklistStepId.MOUNTING_METHOD]: () => {
    return (
      <div className="space-y-2.5">
        <Typography id="tracking_checklist-MOUNTING_METHOD-desc" />
        <MountingMethodRadio col />
      </div>
    );
  },
  [TrackingChecklistStepId.MOUNTING_CALIBRATION]: (step, { toggleSession }) => {
    return (
      <MountingCalibrationStep step={step} toggleSession={toggleSession} />
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
          <ResetButton type={ResetType.MOUNTING} group="feet" />
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
  [TrackingChecklistStepId.STEAMVR_HANDS_ENABLED]: () => {
    return <SteamVRHandsEnabled />;
  },
  [TrackingChecklistStepId.VRCHAT_OSC_TRACKING_DISABLED]: (
    step,
    { toggleSession }
  ) => {
    return (
      <div className="space-y-2.5">
        <Typography
          id="tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-desc"
          elems={{
            OscTrackingLink: (
              <A
                className="text-background-20"
                href="https://docs.slimevr.dev/server/osc-information.html"
              />
            ),
          }}
        />
        <div className="flex justify-between sm:items-center gap-1 flex-col sm:flex-row">
          <Button
            variant="primary"
            to="/settings/osc/vrchat"
            id="tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-open"
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
    );
  },
  [TrackingChecklistStepId.STANDABLE_INSTALLED]: () => {
    return <StandableInstalled />;
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
            <IconButton
              labelId="tracking_checklist-settings"
              className="flex gap-1 items-center justify-center fill-background-40 hover:fill-background-30 cursor-pointer rounded-full w-8 h-8 hover:bg-background-50"
              onClick={() => setSettingsOpen(true)}
            >
              <WrenchIcon width={15} />
            </IconButton>
            {closable && (
              <IconButton
                labelId={
                  closed
                    ? 'tracking_checklist-expand'
                    : 'tracking_checklist-collapse'
                }
                expanded={!closed}
                className="flex gap-1 items-center justify-center fill-background-40 hover:fill-background-30 cursor-pointer rounded-full w-8 h-8 hover:bg-background-50"
                onClick={() => toggleClosed()}
              >
                {closed && <ArrowDownIcon size={25} />}
                {!closed && <CrossIcon size={25} />}
              </IconButton>
            )}
          </div>
        </div>
        <div
          className={classNames('transition-all duration-500 delay-100', {
            'opacity-0 h-0': closed,
          })}
        >
          <div className="pl-[11px] pr-2">
            <Timeline>
              {visibleSteps.map((step, index) => (
                <Step step={step} index={index + 1} key={step.id}>
                  {stepContentLookup[step.id]?.(step, context) || undefined}
                </Step>
              ))}
            </Timeline>
          </div>
        </div>
        <div className="flex flex-col flex-grow justify-end pl-[11px] pr-2 transition-all duration-500 delay-100">
          <div className="min-h-3 flex-1 pl-[11.5px]">
            <div
              className={classNames('h-full w-0 border-l-2', {
                'border-background-50': !closed,
                'border-transparent': closed,
                'border-dashed': completion === 'incomplete',
              })}
            />
          </div>
          <Timeline>
            <TimelineItem
              size="sm"
              last
              state="todo"
              dotClassName={classNames({
                'bg-status-success': completion === 'complete',
                'bg-status-critical animate-pulse animate-low-priority':
                  completion === 'incomplete',
                'bg-status-warning animate-pulse animate-low-priority':
                  completion === 'partial',
              })}
              disabled={!closed}
              expanded={false}
              onToggle={() => toggleClosed()}
              title={
                <>
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
                </>
              }
            />
          </Timeline>
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
              <LoaderIcon slimeState={slimeState} lowPriority />
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
