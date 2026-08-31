import classNames from 'classnames';
import { MouseEvent, ReactNode } from 'react';
import { BodyPart } from 'solarxr-protocol';
import {
  BodyAssignment,
  BodyPartCardRenderer,
  MirrorLegend,
} from '@/components/onboarding/BodyAssignment';
import { BodySlotStyler } from '@/components/commons/BodyInteractions';
import { useConfig } from '@/hooks/config';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import {
  getTrackerName,
  useVelocity,
  velocityGlowStyle,
} from '@/hooks/tracker';
import { bodyPartDropProps, trackerDrag } from '@/hooks/tracker-drag';
import { FlatDeviceTracker } from '@/store/app-store';
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
  const { config } = useConfig();
  const legend = <MirrorLegend compact={mobile} />;

  return (
    <div className="flex-1 min-h-0 flex flex-col gap-2 mobile:gap-1">
      {!mobile && (
        <div className="flex items-center justify-between gap-2 shrink-0 px-2">
          <TrackerAssignmentTabs />
          {legend}
          {headerAction}
        </div>
      )}
      {mobile && (
        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 px-2 shrink-0">
          <div className="flex grow items-center justify-between gap-2">
            <TrackerAssignmentTabs compact />
            {headerAction}
          </div>
          <div className="flex grow items-center justify-between gap-2">
            {legend}
            {legendAction}
          </div>
        </div>
      )}
      <div className="flex-1 min-h-0 overflow-y-auto px-2 flex flex-col fill-background-50">
        <div className="w-full max-w-[770px] m-auto flex-1 min-h-fit flex flex-col tall:py-6">
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

function TrackerAssignmentTabs({ compact }: { compact?: boolean }) {
  return (
    <div
      className={classNames(
        'flex items-center bg-background-70 rounded-lg w-fit',
        compact ? 'gap-0.5 p-0.5' : 'gap-1 p-1'
      )}
    >
      <Tab id="tracker_assignment-tab-body" compact={compact} active />
      <Tab id="tracker_assignment-tab-fingers" compact={compact} disabled />
      <Tab id="tracker_assignment-tab-toes" compact={compact} disabled />
    </div>
  );
}

function Tab({
  id,
  compact,
  active,
  disabled,
}: {
  id: string;
  compact?: boolean;
  active?: boolean;
  disabled?: boolean;
}) {
  return (
    <div
      className={classNames(
        'rounded-md',
        compact ? 'px-3 py-1' : 'px-4 py-2',
        active && 'bg-background-50',
        disabled && 'opacity-40 cursor-not-allowed',
        !disabled && !active && 'cursor-pointer hover:bg-background-60'
      )}
    >
      <Typography bold={active} id={id} />
    </div>
  );
}

type SharedCardProps = {
  role: BodyPart;
  direction: 'left' | 'right';
  td: FlatDeviceTracker | undefined;
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
  const { dragProps, isDragging } = trackerDrag.useDraggable(
    td
      ? {
          trackerId: td.tracker.trackerId,
          label: getTrackerName(td.tracker.info),
        }
      : null,
    (bodyPart) => {
      if (td) onDropTracker(td.tracker.trackerId, bodyPart ?? BodyPart.NONE);
    }
  );

  const onClick = (event: MouseEvent<HTMLDivElement>) => {
    dragProps.onClick(event);
    if (event.defaultPrevented) return;

    if (td) onUnassign(role);
    else onSlotClick(role);
  };

  return (
    <div
      {...bodyPartDropProps(role)}
      {...dragProps}
      id={BodyPart[role]}
      onClick={onClick}
      className={classNames(
        'flex flex-col gap-1 control w-40 px-2 py-1 rounded-md relative touch-none select-none',
        'transition-colors duration-150 ease-linear',
        td ? 'cursor-grab active:cursor-grabbing' : 'cursor-pointer',
        isDragging && 'opacity-40',
        direction === 'left' ? 'items-start' : 'items-end',
        isHovering
          ? 'bg-background-50'
          : armed
            ? 'bg-accent-background-30/40'
            : isDragActive
              ? 'bg-background-50/50'
              : 'hover:bg-background-50'
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
        {td ? (
          <AssignedTrackerLabel tracker={td} />
        ) : (
          <div className="flex items-center h-8">
            <Typography color="text-background-30" id="body_part-NONE" />
          </div>
        )}
      </div>
    </div>
  );
}

function AssignedTrackerLabel({ tracker }: { tracker: FlatDeviceTracker }) {
  const velocity = useVelocity(tracker.tracker);
  const name = getTrackerName(tracker.tracker.info);

  return (
    <div
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
    </div>
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
  const velocity = useVelocity(td?.tracker);

  return (
    <button
      type="button"
      id={BodyPart[role]}
      onClick={() => onSelect(role)}
      style={velocityGlowStyle(velocity)}
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
      {awaitingTracker && !armed && !td && (
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
      {td ? (
        <Typography variant="standard" truncate>
          {getTrackerName(td.tracker.info)}
        </Typography>
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
