import classNames from 'classnames';
import { MouseEvent, ReactNode, useCallback } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { BodySlotStyler } from '@/components/commons/BodyInteractions';
import {
  BodyAssignment,
  BodyPartCardRenderer,
  MirrorLegend,
} from '@/components/onboarding/BodyAssignment';
import {
  ExtremityAssignment,
  ExtremityGroupRenderer,
  ExtremityRow,
} from '@/components/onboarding/ExtremityAssignment';
import { DigitFlow } from '@/components/onboarding/extremities/ExtremityLayout';
import { ExtremityDescriptor, ExtremitySide } from '@/utils/extremities';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useConfig } from '@/hooks/config';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { TogglePill, TogglePillOption } from '@/components/commons/TogglePill';
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
import {
  ASSIGNMENT_TAB_ORDER,
  ASSIGNMENT_TABS,
  useAssignment,
} from '@/hooks/tracker-assignment';

function useSlotStyle(): BodySlotStyler {
  const { mode, activePart } = useAssignment();

  return useCallback(
    (part: BodyPart) => ({
      props: mode === 'drag' ? bodyPartDropProps(part) : undefined,
      connected: activePart === part,
      className:
        activePart === part
          ? 'scale-150 ring-3 ring-accent-background-30'
          : undefined,
    }),
    [mode, activePart]
  );
}

export function BodyAssignmentPanel({
  headerAction,
  legendAction,
  compact,
}: {
  headerAction?: ReactNode;
  legendAction?: ReactNode;
  compact?: boolean;
}) {
  const { config } = useConfig();
  const { mode, tab, side, setSide, firstError, rolesWithErrors, selectPart } =
    useAssignment();
  const { isMobile: isTight } = useBreakpoint('mobile');
  const slotStyle = useSlotStyle();

  const { view, dotSize } = ASSIGNMENT_TABS[tab];
  const extremity = view.kind === 'extremity' ? view.descriptor : null;

  const legend = extremity ? (
    <ExtremitySideToggle
      compact={compact}
      descriptor={extremity}
      side={side}
      onChange={setSide}
    />
  ) : (
    <MirrorLegend compact={compact} />
  );
  const tabs = <AssignmentTabs compact={compact} />;

  const renderCard: BodyPartCardRenderer = ({
    role,
    direction,
    td,
    roleError,
  }) => (
    <BodyPartCard
      key={role}
      role={role}
      direction={direction}
      td={td}
      roleError={roleError}
    />
  );

  const renderGroup: ExtremityGroupRenderer = ({
    id,
    labelId,
    direction,
    rows,
    edge,
    flow,
  }) => (
    <ExtremityGroupCard
      key={id}
      edge={edge}
      flow={flow}
      labelId={labelId}
      direction={direction}
      rows={rows}
    />
  );

  return (
    <div className="flex-1 min-h-0 flex flex-col gap-2 mobile:gap-1">
      {!compact && (
        <div className="flex items-center justify-between gap-2 shrink-0 px-2">
          {tabs}
          {legend}
          {headerAction}
        </div>
      )}
      {compact && (
        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 px-2 shrink-0">
          <div className="flex grow items-center justify-between gap-2">
            {tabs}
            {headerAction}
          </div>
          <div className="flex grow items-center justify-between gap-2">
            {legend}
            {legendAction}
          </div>
        </div>
      )}
      {/* Gutter stays put, or the scrollbar appearing resizes the figure back
          under the scroll threshold and the two flicker against each other */}
      <div className="flex-1 min-h-0 overflow-y-auto [scrollbar-gutter:stable] px-2 flex flex-col fill-background-50">
        <div
          className={classNames(
            'w-full m-auto flex-1 min-h-fit flex flex-col tall:py-6',
            extremity ? 'max-w-[940px]' : 'max-w-[770px]'
          )}
        >
          {extremity ? (
            <ExtremityAssignment
              descriptor={extremity}
              side={side}
              dotSize={dotSize[mode]}
              compact={isTight}
              fillHeight
              highlightedRoles={firstError?.affectedRoles || []}
              rolesWithErrors={rolesWithErrors}
              onRoleSelected={selectPart}
              renderGroup={renderGroup}
              slotStyle={slotStyle}
            />
          ) : (
            <BodyAssignment
              dotSize={dotSize[mode]}
              fillHeight
              onlyAssigned={false}
              highlightedRoles={firstError?.affectedRoles || []}
              rolesWithErrors={rolesWithErrors}
              mirror={config?.mirrorView ?? false}
              onRoleSelected={selectPart}
              renderCard={renderCard}
              slotStyle={slotStyle}
            />
          )}
        </div>
      </div>
    </div>
  );
}

function AssignmentTabs({ compact }: { compact?: boolean }) {
  const { tab, setTab } = useAssignment();

  return (
    <div
      className={classNames(
        'flex items-center bg-background-70 rounded-lg w-fit',
        compact ? 'gap-0.5 p-0.5' : 'gap-1 p-1'
      )}
    >
      {ASSIGNMENT_TAB_ORDER.map((key) => (
        <Tab
          key={key}
          compact={compact}
          labelId={ASSIGNMENT_TABS[key].labelId}
          active={tab === key}
          disabled={!ASSIGNMENT_TABS[key].enabled}
          onClick={() => setTab(key)}
        />
      ))}
    </div>
  );
}

function Tab({
  labelId,
  compact,
  active,
  disabled,
  onClick,
}: {
  labelId: string;
  compact?: boolean;
  active?: boolean;
  disabled?: boolean;
  onClick?: () => void;
}) {
  return (
    <div
      onClick={disabled || active ? undefined : onClick}
      className={classNames(
        'rounded-md',
        compact ? 'px-3 py-1' : 'px-4 py-2',
        active && 'bg-background-50',
        disabled && 'opacity-40 cursor-not-allowed',
        !disabled && !active && 'cursor-pointer hover:bg-background-60'
      )}
    >
      <Typography bold={active} id={labelId} />
    </div>
  );
}

function ExtremitySideToggle({
  compact,
  descriptor,
  side,
  onChange,
}: {
  compact?: boolean;
  descriptor: ExtremityDescriptor;
  side: ExtremitySide;
  onChange: (side: ExtremitySide) => void;
}) {
  const option = (value: ExtremitySide, dotClass: string) => (
    <TogglePillOption
      compact={compact}
      dotClass={dotClass}
      active={side === value}
      onClick={() => onChange(value)}
      labelId={'body_part-' + BodyPart[descriptor.sides[value].root]}
    />
  );

  return (
    <TogglePill compact={compact}>
      {option('left', 'outline-assign-left')}
      {option('right', 'outline-assign-right')}
    </TogglePill>
  );
}

type BodyPartCardProps = {
  role: BodyPart;
  direction: 'left' | 'right';
  td: FlatDeviceTracker | undefined;
  roleError: string | undefined;
  labelId?: string;
  compact?: boolean;
  number?: number;
  connector?: boolean;
};

export function BodyPartCard(props: BodyPartCardProps) {
  const { mode } = useAssignment();

  return mode === 'drag' ? (
    <DragBodyPartCard {...props} />
  ) : (
    <TapBodyPartCard {...props} />
  );
}

const CARD_WIDTH = 'w-32 smol:w-40 xsAssign:w-44';
const WIDE_CARD_WIDTH =
  'w-full max-w-96 smol:max-w-[30rem] xsAssign:max-w-[33rem]';
const SINGLE_CARD = `${CARD_WIDTH} p-1`;
const DIGIT_CARD =
  'gap-0.5 p-1 rounded-lg bg-background-70/40 border border-background-60';

export function ExtremityGroupCard({
  labelId,
  direction,
  rows,
  edge = 'side',
  flow = 'rows',
  className,
}: {
  labelId?: string;
  direction: 'left' | 'right';
  rows: ExtremityRow[];
  edge?: 'side' | 'cap';
  flow?: DigitFlow;
  className?: string;
}) {
  const single = rows.length === 1;
  const across = flow === 'columns';

  return (
    <div
      data-connector-group
      data-connector-edge={edge}
      className={classNames(
        'flex flex-col',
        className ??
          (single
            ? SINGLE_CARD
            : `${DIGIT_CARD} ${across ? WIDE_CARD_WIDTH : CARD_WIDTH}`)
      )}
    >
      {labelId && (
        <div
          className={classNames(
            'px-1.5 pb-0.5 overflow-hidden',
            direction === 'right' ? 'text-right' : 'text-left'
          )}
        >
          <Typography
            bold
            truncate
            variant="section-title"
            whitespace="whitespace-nowrap"
            id={labelId}
          />
        </div>
      )}
      <div
        className={classNames(
          across ? 'grid grid-cols-3 gap-1' : 'flex flex-col gap-0.5'
        )}
      >
        {rows.map(
          ({ role, td, roleError, labelId: rowLabelId, number, connector }) => (
            <BodyPartCard
              key={role}
              compact
              number={number}
              connector={connector}
              labelId={rowLabelId}
              role={role}
              direction={direction}
              td={td}
              roleError={roleError}
            />
          )
        )}
      </div>
    </div>
  );
}

function DragBodyPartCard({
  td,
  role,
  direction,
  roleError,
  compact,
  number,
  connector = true,
  labelId,
}: BodyPartCardProps) {
  const { armedPart, selectPart, handleDropTracker } = useAssignment();
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
      if (td)
        handleDropTracker(td.tracker.trackerId, bodyPart ?? BodyPart.NONE);
    }
  );

  const onClick = (event: MouseEvent<HTMLDivElement>) => {
    dragProps.onClick(event);
    if (event.defaultPrevented) return;
    selectPart(role);
  };

  return (
    <div
      {...bodyPartDropProps(role)}
      {...dragProps}
      id={BodyPart[role]}
      data-connector={connector ? undefined : 'off'}
      onClick={onClick}
      className={classNames(
        'flex flex-col control rounded-md relative touch-none select-none',
        'transition-colors duration-150 ease-linear',
        compact ? 'gap-0 w-full px-1.5 py-0.5' : 'gap-1 w-40 px-2 py-1',
        td ? 'cursor-grab active:cursor-grabbing' : 'cursor-pointer',
        isDragging && 'opacity-40',
        direction === 'left' ? 'items-start' : 'items-end',
        isHovering
          ? 'bg-background-50'
          : armedPart === role
            ? 'bg-accent-background-30/40'
            : isDragActive
              ? 'bg-background-50/50'
              : 'hover:bg-background-50'
      )}
    >
      <PartCardWarning error={roleError} direction={direction} tooltip />
      <PartCardLabel
        role={role}
        direction={direction}
        number={number}
        labelId={labelId}
      />
      <div className={compact ? 'min-h-6 w-full' : 'min-h-10'}>
        {td ? (
          <AssignedTrackerLabel tracker={td} compact={compact} />
        ) : (
          <div
            className={classNames(
              'flex items-center',
              compact ? 'h-6' : 'h-8',
              direction === 'right' && 'justify-end'
            )}
          >
            <Typography color="text-background-30" id="body_part-NONE" />
          </div>
        )}
      </div>
    </div>
  );
}

function TapBodyPartCard({
  td,
  role,
  direction,
  roleError,
  labelId,
  number,
  compact,
  connector = true,
}: BodyPartCardProps) {
  const { armedPart, pendingTrackerId, selectPart } = useAssignment();
  const velocity = useVelocity(td?.tracker);
  const armed = armedPart === role;
  const awaitingTracker = pendingTrackerId != null;

  return (
    <button
      type="button"
      id={BodyPart[role]}
      data-connector={connector ? undefined : 'off'}
      onClick={() => selectPart(role)}
      style={velocityGlowStyle(velocity)}
      className={classNames(
        'flex flex-col control rounded-md relative overflow-hidden transition-colors duration-150 ease-linear',
        compact
          ? 'gap-0 w-full px-1.5 py-0.5'
          : 'gap-1 w-[88px] smol:w-[120px] sm:w-[150px] px-2 py-1',
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
      <PartCardWarning error={roleError} direction={direction} />
      <PartCardLabel
        role={role}
        direction={direction}
        number={number}
        labelId={labelId}
      />
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

function PartCardLabel({
  role,
  direction,
  number,
  labelId,
}: {
  role: BodyPart;
  direction: 'left' | 'right';
  number?: number;
  labelId?: string;
}) {
  return (
    <div
      className={classNames(
        'flex items-center gap-1.5 max-w-full min-w-0 overflow-hidden',
        direction === 'right' && 'flex-row-reverse'
      )}
    >
      {number !== undefined && <JointNumber number={number} />}
      <Typography
        variant="standard"
        bold
        truncate
        whitespace="whitespace-nowrap"
        id={labelId ?? 'body_part-' + BodyPart[role]}
      />
    </div>
  );
}

function PartCardWarning({
  error,
  direction,
  tooltip,
}: {
  error: string | undefined;
  direction: 'left' | 'right';
  tooltip?: boolean;
}) {
  if (!error) return null;

  const icon = (
    <div
      className={classNames(
        'absolute text-status-warning scale-75 -top-1',
        tooltip && 'cursor-help',
        direction === 'right' ? '-right-6' : '-left-6'
      )}
    >
      <WarningIcon />
    </div>
  );

  if (!tooltip) return icon;

  return (
    <Tooltip
      content={
        <Typography variant="standard" color="text-status-warning">
          {error}
        </Typography>
      }
      preferedDirection="top"
      spacing={8}
    >
      {icon}
    </Tooltip>
  );
}

function JointNumber({ number }: { number: number }) {
  return (
    <span className="shrink-0 w-4 h-4 rounded-full bg-background-10 text-background-90 text-[10px] font-bold flex items-center justify-center">
      {number}
    </span>
  );
}

function AssignedTrackerLabel({
  tracker,
  compact,
}: {
  tracker: FlatDeviceTracker;
  compact?: boolean;
}) {
  const velocity = useVelocity(tracker.tracker);
  const name = getTrackerName(tracker.tracker.info);

  return (
    <div
      className={classNames(
        'flex items-center rounded-md bg-background-80',
        compact ? 'gap-1 px-1' : 'gap-2 px-2'
      )}
      style={velocityGlowStyle(velocity)}
    >
      <div className="fill-background-10">
        <BodyPartIcon
          trackerId={tracker.tracker.trackerId}
          device={tracker.device}
          width={compact ? 18 : 25}
        />
      </div>
      <div className={compact ? 'py-0.5 min-w-0' : 'py-2'}>
        <Typography truncate={compact}>{name}</Typography>
      </div>
    </div>
  );
}
