import classNames from 'classnames';
import { ReactNode } from 'react';
import { BodyPart } from 'solarxr-protocol';
import {
  BodyAssignment,
  BodyPartCardRenderer,
} from '@/components/onboarding/BodyAssignment';
import { BodySlotStyler } from '@/components/commons/BodyInteractions';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { useConfig } from '@/hooks/config';
import { getTrackerName, useTracker, velocityGlowStyle } from '@/hooks/tracker';
import { bodyPartDropProps, trackerDrag } from '@/hooks/tracker-drag';
import { FlatDeviceTracker } from '@/store/app-store';
import { DraggableTracker } from './TrackerAssignmentList';
import { BodyPartError } from '@/hooks/tracker-assignment';

export function BodyAssignmentPanel({
  dotSize,
  headerAction,
  legendAction,
  mobile,
  highlightedRoles,
  rolesWithErrors,
  onRoleSelected,
  renderCard,
  slotStyle,
}: {
  dotSize: number;
  headerAction?: ReactNode;
  legendAction?: ReactNode;
  mobile?: boolean;
  highlightedRoles: BodyPart[];
  rolesWithErrors: Partial<Record<BodyPart, BodyPartError>>;
  onRoleSelected: (role: BodyPart) => void;
  renderCard: BodyPartCardRenderer;
  slotStyle: BodySlotStyler;
}) {
  const { config, setConfig } = useConfig();
  const legend = (
    <SideLegend
      mirror={config?.mirrorView ?? false}
      toggleMirror={() => setConfig({ mirrorView: !config?.mirrorView })}
    />
  );

  return (
    <div className="flex-1 min-h-0 flex flex-col gap-4">
      {!mobile && (
        <div className="flex items-center justify-between gap-2 shrink-0 px-2">
          <TrackerAssignmentTabs />
          {legend}
          {headerAction}
        </div>
      )}
      {mobile && (
        <div className="flex flex-col gap-2 shrink-0">
          <div className="flex items-center justify-between gap-2 px-2">
            <TrackerAssignmentTabs />
            {headerAction}
          </div>
          <div className="flex items-center justify-between gap-2 px-2">
            {legend}
            {legendAction}
          </div>
        </div>
      )}
      <div className="flex-1 min-h-0 overflow-y-auto px-2 flex flex-col fill-background-50">
        <div className="w-full max-w-[770px] m-auto flex-1 min-h-fit flex flex-col tall:py-10">
          <BodyAssignment
            dotSize={dotSize}
            fillHeight
            onlyAssigned={false}
            highlightedRoles={highlightedRoles}
            rolesWithErrors={rolesWithErrors}
            mirror={config?.mirrorView ?? false}
            onRoleSelected={onRoleSelected}
            renderCard={renderCard}
            slotStyle={slotStyle}
          />
        </div>
      </div>
    </div>
  );
}

function TrackerAssignmentTabs() {
  return (
    <div className="flex items-center gap-1 bg-background-70 rounded-lg p-1 w-fit">
      <Tab id="tracker_assignment-tab-body" active />
      <Tab id="tracker_assignment-tab-fingers" disabled />
      <Tab id="tracker_assignment-tab-toes" disabled />
    </div>
  );
}

function Tab({
  id,
  active,
  disabled,
}: {
  id: string;
  active?: boolean;
  disabled?: boolean;
}) {
  return (
    <div
      className={classNames(
        'px-4 py-2 rounded-md',
        active && 'bg-background-50',
        disabled && 'opacity-40 cursor-not-allowed',
        !disabled && !active && 'cursor-pointer hover:bg-background-60'
      )}
    >
      <Typography bold={active} id={id} />
    </div>
  );
}

function SideLegend({
  mirror,
  toggleMirror,
}: {
  mirror: boolean;
  toggleMirror: () => void;
}) {
  return (
    <div
      className="flex items-center gap-1 bg-background-80 rounded-full p-1 w-fit cursor-pointer"
      onClick={toggleMirror}
    >
      <div
        className={classNames(
          'flex items-center gap-2 px-4 py-1',
          mirror ? 'order-2' : 'order-1'
        )}
      >
        <span className="w-2.5 h-2.5 rounded-full bg-background-10 outline outline-4 outline-assign-left" />
        <Typography bold id="tracker_assignment-side-left" />
      </div>
      <div
        className={classNames(
          'flex items-center gap-2 px-4 py-1 rounded-full',
          mirror ? 'order-1' : 'order-2'
        )}
      >
        <span className="w-2.5 h-2.5 rounded-full  bg-background-10 outline outline-4 outline-assign-right" />
        <Typography bold id="tracker_assignment-side-right" />
      </div>
    </div>
  );
}

type SharedCardProps = {
  role: BodyPart;
  direction: 'left' | 'right';
  td: FlatDeviceTracker[] | undefined;
  roleError: string | undefined;
  armed: boolean;
};

type DragCardProps = SharedCardProps & {
  mode: 'drag';
  onSlotClick: (role: BodyPart) => void;
  onUnassign: (role: BodyPart) => void;
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
};

type TapCardProps = SharedCardProps & {
  mode: 'tap';
  awaitingTracker: boolean;
  onSelect: (role: BodyPart) => void;
};

export function BodyPartCard(props: DragCardProps | TapCardProps) {
  return props.mode === 'drag' ? (
    <DragBodyPartCard {...props} />
  ) : (
    <TapBodyPartCard {...props} />
  );
}

function DragBodyPartCard({
  td,
  role,
  direction,
  roleError,
  armed,
  onSlotClick,
  onUnassign,
  onDropTracker,
}: DragCardProps) {
  const isHovering = trackerDrag.useIsDragHovering(role);
  const isDragActive = trackerDrag.useIsDragActive();
  const isAssigned = !!td && td.length > 0;

  return (
    <div
      {...bodyPartDropProps(role)}
      id={BodyPart[role]}
      className={classNames(
        'flex flex-col gap-1 control w-40 px-2 py-1 rounded-md relative',
        direction === 'left' ? 'items-start' : 'items-end'
      )}
    >
      {roleError && (
        <Tooltip
          content={
            <Typography variant="standard" color="text-status-warning">
              {roleError}
            </Typography>
          }
          preferedDirection="top"
          spacing={8}
        >
          <div
            className={classNames(
              'absolute text-status-warning scale-75 -top-1 cursor-help',
              direction === 'right' ? '-right-6' : '-left-6'
            )}
          >
            <WarningIcon />
          </div>
        </Tooltip>
      )}
      <Typography variant="standard" bold id={'body_part-' + BodyPart[role]} />
      <div className="min-h-10">
        {isAssigned ? (
          <div
            onClick={() => onUnassign(role)}
            className="flex flex-col gap-1 cursor-pointer"
          >
            {td!.map((tracker, index) => (
              <AssignedTrackerLabel
                key={index}
                tracker={tracker}
                onDropTracker={onDropTracker}
              />
            ))}
          </div>
        ) : (
          <div
            onClick={() => onSlotClick(role)}
            className={classNames(
              'flex items-center h-8 px-2 rounded-md cursor-pointer transition-colors duration-150 ease-linear',
              isHovering
                ? 'bg-background-50'
                : armed
                  ? 'bg-accent-background-30/40'
                  : isDragActive
                    ? 'bg-background-50/50'
                    : 'hover:bg-background-50'
            )}
          >
            <Typography color="text-background-30" id="body_part-NONE" />
          </div>
        )}
      </div>
    </div>
  );
}

function AssignedTrackerLabel({
  tracker,
  onDropTracker,
}: {
  tracker: FlatDeviceTracker;
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
}) {
  const { useVelocity } = useTracker(tracker.tracker);
  const velocity = useVelocity();
  const name = getTrackerName(tracker.tracker.info);

  return (
    <DraggableTracker
      trackerId={tracker.tracker.trackerId}
      label={name?.toString() || ''}
      // Dropping outside any body part unassigns.
      onDrop={(bodyPart) =>
        onDropTracker(tracker.tracker.trackerId, bodyPart ?? BodyPart.NONE)
      }
      className="flex items-center gap-2 rounded-md bg-background-80 px-2"
      style={velocityGlowStyle(velocity)}
    >
      <div className="fill-background-10">
        <BodyPartIcon
          trackerId={tracker.tracker.trackerId}
          device={tracker.device}
          width={25}
        />
      </div>
      <div className="py-2">
        <Typography>{name}</Typography>
      </div>
    </DraggableTracker>
  );
}

function TapBodyPartCard({
  td,
  role,
  direction,
  roleError,
  armed,
  awaitingTracker,
  onSelect,
}: TapCardProps) {
  const isAssigned = !!td && td.length > 0;

  return (
    <button
      type="button"
      id={BodyPart[role]}
      onClick={() => onSelect(role)}
      className={classNames(
        'flex flex-col gap-1 control w-[88px] smol:w-[120px] sm:w-[150px] px-2 py-1 rounded-md relative overflow-hidden transition-colors duration-150 ease-linear',
        direction === 'left' ? 'text-left' : 'text-right',
        armed
          ? 'bg-accent-background-30/40'
          : awaitingTracker
            ? 'bg-background-60/60'
            : undefined
      )}
    >
      {awaitingTracker && !armed && !isAssigned && (
        <div className="absolute inset-0 rounded-md border border-accent-background-20/70 animate-pulse pointer-events-none" />
      )}
      {roleError && (
        <div
          className={classNames(
            'absolute text-status-warning scale-75 -top-1',
            direction === 'right' ? '-right-6' : '-left-6'
          )}
        >
          <WarningIcon />
        </div>
      )}
      <Typography variant="standard" bold id={'body_part-' + BodyPart[role]} />
      {isAssigned ? (
        td!.map((tracker, index) => (
          <Typography key={index} variant="standard" truncate>
            {getTrackerName(tracker.tracker.info)}
          </Typography>
        ))
      ) : (
        <Typography
          variant="standard"
          truncate
          color="text-background-30"
          id="body_part-NONE"
        />
      )}
    </button>
  );
}
