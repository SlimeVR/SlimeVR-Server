import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { CSSProperties, ReactNode, useMemo } from 'react';
import {
  BodyPart,
  DeviceDataT,
  DongleDataT,
  TrackerDataT,
} from 'solarxr-protocol';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Button } from '@/components/commons/Button';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { Typography } from '@/components/commons/Typography';
import { TrackerStatus } from '@/components/tracker/TrackerStatus';
import {
  TrackerConnectionGroupCollapseToolbox,
  TrackerConnectionGroupSection,
} from '@/components/tracker/TrackerConnectionGroup';
import { useOnboarding } from '@/hooks/onboarding';
import { getTrackerName, useTracker, velocityGlowStyle } from '@/hooks/tracker';
import {
  bodyPartDropProps,
  trackerDrag,
  useIsTrackerBeingDragged,
} from '@/hooks/tracker-drag';
import {
  FlatDeviceTracker,
  groupTrackersByConnection,
  groupTrackersByDevice,
  TrackerConnectionGroup,
} from '@/store/app-store';
import { ShowAllPartsToggle } from '@/components/onboarding/BodyAssignment';

export function TrackerAssignmentList({
  trackers,
  dongles,
  assignedCount,
  assignedPartsCount,
  expectedTrackersCount,
  onDropTracker,
}: {
  trackers: FlatDeviceTracker[];
  dongles: DongleDataT[];
  assignedCount: number;
  assignedPartsCount: number;
  expectedTrackersCount: number;
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
}) {
  const { state } = useOnboarding();
  const groups = useMemo(
    () => groupTrackersByConnection(trackers, dongles),
    [trackers, dongles]
  );
  const variant = state.alonePage ? 'primary' : 'tertiary';

  return (
    <div className="w-[380px] lg:w-[400px] xl:w-[440px] p-4 flex flex-col gap-4 shrink-0 min-h-0 border-r border-background-60">
      <div className="flex flex-col gap-1 shrink-0">
        <Typography
          variant="mobile-title"
          id="onboarding-assign_trackers-title"
        />
        <Typography
          variant="standard"
          whitespace="whitespace-pre-wrap"
          id="onboarding-assign_trackers-description"
        />
      </div>

      <div className="flex items-center gap-2 shrink-0">
        <Typography
          color="secondary"
          id="onboarding-assign_trackers-assigned"
          vars={{
            assigned: assignedPartsCount,
            trackers: expectedTrackersCount,
          }}
        />
        <div className="flex-grow">
          <ProgressBar
            progress={
              expectedTrackersCount
                ? assignedPartsCount / expectedTrackersCount
                : 0
            }
            height={4}
          />
        </div>
      </div>

      <ShowAllPartsToggle />

      <div
        {...bodyPartDropProps(BodyPart.NONE)}
        className="flex flex-col gap-4 min-h-0 flex-1 overflow-y-auto -mx-2 px-2"
      >
        {trackers.length === 0 ? (
          <AssignmentEmptyState
            kind="no-trackers"
            iconSize={56}
            className="flex-1 my-auto"
          />
        ) : assignedCount === trackers.length ? (
          <AssignmentEmptyState
            kind="all-assigned"
            iconSize={56}
            className="flex-1 my-auto"
          />
        ) : (
          <UnassignedTrackerList
            groups={groups}
            variant={variant}
            renderTracker={(td) => (
              <DraggableTrackerCard
                tracker={td.tracker}
                device={td.device}
                variant={variant}
                onDrop={(bodyPart) =>
                  onDropTracker(td.tracker.trackerId, bodyPart)
                }
              />
            )}
          />
        )}
      </div>

      {!state.alonePage && (
        <div className="shrink-0 pt-4 border-t border-background-60">
          <AssignmentNavFooter
            assignedCount={assignedCount}
            trackerCount={trackers.length}
          />
        </div>
      )}
    </div>
  );
}

export function AssignmentNavFooter({
  assignedCount,
  trackerCount,
}: {
  assignedCount: number;
  trackerCount: number;
}) {
  const { l10n } = useLocalization();
  const { state, slimeSet } = useOnboarding();

  const prevStepPath = state.alonePage
    ? '/onboarding/connect-trackers'
    : slimeSet && ['butterfly', 'dongle-slime'].includes(slimeSet)
      ? '/onboarding/dongle'
      : '/onboarding/connect-trackers';

  return (
    <div className="flex flex-row justify-between items-center">
      <Button variant="secondary" to={prevStepPath}>
        {l10n.getString('onboarding-previous_step')}
      </Button>
      <Button
        variant="primary"
        to="/onboarding/mounting/choose"
        disabled={assignedCount === 0 && trackerCount > 0}
      >
        {l10n.getString('onboarding-continue')}
      </Button>
    </div>
  );
}

export function AssignmentEmptyState({
  kind,
  iconSize = 56,
  className,
}: {
  kind: 'no-trackers' | 'all-assigned';
  iconSize?: number;
  className?: string;
}) {
  const slimeState =
    kind === 'no-trackers' ? SlimeState.JUMPY : SlimeState.HAPPY;
  const titleColor =
    kind === 'all-assigned' ? 'text-status-success' : undefined;
  const titleId =
    kind === 'no-trackers'
      ? 'onboarding-assign_trackers-no_trackers-title'
      : 'onboarding-assign_trackers-all_assigned-title';
  const descId =
    kind === 'no-trackers'
      ? 'onboarding-assign_trackers-no_trackers-description'
      : 'onboarding-assign_trackers-all_assigned-description';

  return (
    <div
      className={classNames(
        'flex flex-col items-center justify-center gap-4 text-center p-6',
        className
      )}
    >
      <LoaderIcon slimeState={slimeState} size={iconSize} />
      <div className="flex flex-col gap-1 max-w-xs">
        <Typography
          bold
          variant="section-title"
          color={titleColor}
          id={titleId}
        />
        <Typography variant="standard" id={descId} />
      </div>
    </div>
  );
}

export function UnassignedTrackerList({
  groups,
  renderTracker,
  linkHeader,
  variant,
}: {
  groups: TrackerConnectionGroup[];
  renderTracker: (td: FlatDeviceTracker) => ReactNode;
  linkHeader?: boolean;
  variant: 'primary' | 'secondary' | 'tertiary';
}) {
  return (
    <>
      {groups
        .filter((group) => group.unassigned.length > 0)
        .map((group) => (
          <TrackerConnectionGroupSection
            key={group.key}
            group={group}
            linkHeader={linkHeader}
            variant={variant}
            toolbox={<TrackerConnectionGroupCollapseToolbox group={group} />}
          >
            <div className="flex flex-col gap-2 px-2">
              {groupTrackersByDevice(group.unassigned).map(
                (deviceTrackers, index) => (
                  <DeviceTrackerGroup
                    key={index}
                    trackers={deviceTrackers}
                    renderRow={renderTracker}
                  />
                )
              )}
            </div>
          </TrackerConnectionGroupSection>
        ))}
    </>
  );
}

function DeviceTrackerGroup({
  trackers,
  renderRow,
}: {
  trackers: FlatDeviceTracker[];
  renderRow: (td: FlatDeviceTracker) => ReactNode;
}) {
  const [primary, ...extensions] = trackers;

  return (
    <div className="flex flex-col">
      {renderRow(primary)}
      {extensions.length > 0 && (
        <div className="flex flex-col">
          {extensions.map((extension, index) => (
            <div key={index} className="flex items-stretch">
              <div className="w-6 shrink-0 relative">
                <div className="absolute left-4 top-0 h-8 w-4 border-l-2 border-b-2 border-dashed border-accent-background-30 rounded-bl-xl" />
                {index < extensions.length - 1 && (
                  <div className="absolute left-4 top-6 h-8 border-l-2 border-dashed border-accent-background-30" />
                )}
              </div>
              <div className="flex-grow min-w-0 pt-2 pl-2">
                {renderRow(extension)}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export function SimpleTrackerRow({
  tracker,
  device,
  variant,
  onPress,
  selected = false,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  variant: 'primary' | 'secondary' | 'tertiary';
  onPress?: () => void;
  selected?: boolean;
}) {
  const { useVelocity } = useTracker(tracker);
  const velocity = useVelocity();

  const row = (
    <div
      className={classNames(
        'flex items-center gap-2 rounded-lg h-12 p-2 pr-2 transition-[box-shadow] duration-200 ease-linear',
        {
          'bg-background-60': variant === 'primary' || variant === 'tertiary',
          'bg-background-50': variant === 'secondary',
        }
      )}
      style={velocityGlowStyle(velocity)}
    >
      <div className="fill-background-10">
        <BodyPartIcon
          bodyPart={tracker.info?.bodyPart}
          device={device}
          trackerId={tracker.trackerId}
          width={32}
        />
      </div>
      <div className="flex-grow min-w-0">
        <Typography bold truncate whitespace="whitespace-nowrap">
          {getTrackerName(tracker.info)}
        </Typography>
        {tracker.info?.customName &&
          tracker.info?.displayName !== tracker.info?.customName && (
            <Typography color="secondary">
              {tracker.info?.displayName}
            </Typography>
          )}
      </div>
      <div className="shrink-0">
        <TrackerStatus status={tracker.status} />
      </div>
    </div>
  );

  if (!onPress) return row;

  return (
    <button
      type="button"
      onClick={onPress}
      className={classNames(
        'w-full text-left rounded-lg transition-shadow',
        selected && 'ring-2 ring-accent-background-30'
      )}
    >
      {row}
    </button>
  );
}

export function DraggableTracker({
  trackerId,
  label,
  onDrop,
  className,
  style,
  children,
}: {
  trackerId: number;
  label: string;
  onDrop: (bodyPart: BodyPart | null) => void;
  className?: string;
  style?: CSSProperties;
  children: ReactNode;
}) {
  const isBeingDragged = useIsTrackerBeingDragged(trackerId);
  const { dragProps } = trackerDrag.useDraggable({ trackerId, label }, onDrop);

  return (
    <div
      {...dragProps}
      className={classNames(
        'touch-none cursor-grab active:cursor-grabbing select-none',
        'transition-[transform,opacity] hover:scale-[1.02] hover:animate-wiggle',
        isBeingDragged && 'opacity-40',
        className
      )}
      style={style}
    >
      {children}
    </div>
  );
}

function DraggableTrackerCard({
  tracker,
  device,
  variant,
  onDrop,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  variant: 'primary' | 'secondary' | 'tertiary';
  onDrop: (bodyPart: BodyPart) => void;
}) {
  return (
    <DraggableTracker
      trackerId={tracker.trackerId}
      label={getTrackerName(tracker.info) || 'unknown'}
      onDrop={(bodyPart) => {
        if (bodyPart !== null && bodyPart !== BodyPart.NONE) onDrop(bodyPart);
      }}
    >
      <SimpleTrackerRow tracker={tracker} device={device} variant={variant} />
    </DraggableTracker>
  );
}
