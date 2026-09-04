import classNames from 'classnames';
import {
  HTMLAttributes,
  MouseEvent,
  ReactNode,
  useCallback,
  useMemo,
} from 'react';
import { BodyPart } from 'solarxr-protocol';
import {
  BodyAssignment,
  MirrorLegend,
} from '@/components/onboarding/BodyAssignment';
import {
  ExtremityAssignment,
  ExtremityGroupRenderer,
} from '@/components/onboarding/ExtremityAssignment';
import {
  AssignedTrackerLabel,
  ExtremityGroupCard,
  PartCardLabel,
  PartCardProps,
  PartCardRenderer,
  PartCardWarning,
} from '@/components/onboarding/parts/PartCard';
import { ExtremityDescriptor, ExtremitySide } from '@/utils/extremities';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useConfig } from '@/hooks/config';
import { TogglePill, TogglePillOption } from '@/components/commons/TogglePill';
import { Typography } from '@/components/commons/Typography';
import {
  getTrackerName,
  useVelocity,
  velocityGlowStyle,
} from '@/hooks/tracker';
import { bodyPartDropProps, trackerDrag } from '@/hooks/tracker-drag';
import { useAssignment } from '@/hooks/tracker-assignment';
import {
  PickerContext,
  PICKER_TAB_ORDER,
  PICKER_TABS,
  usePicker,
} from '@/hooks/tracker-picker';

/**
 * The figure and its cards, for any flow that picks a body part. What a tap
 * does and what a card shows come from the picker in context and the renderers.
 */
export function PickerPanel({
  headerAction,
  legendAction,
  compact,
  dots,
  renderCard,
  renderGroup,
  dotProps,
}: {
  headerAction?: ReactNode;
  legendAction?: ReactNode;
  compact?: boolean;
  dots: 'drag' | 'tap';
  renderCard?: PartCardRenderer;
  renderGroup: ExtremityGroupRenderer;
  dotProps?: (part: BodyPart) => HTMLAttributes<HTMLDivElement>;
}) {
  const { config } = useConfig();
  const {
    tab,
    side,
    setSide,
    firstError,
    rolesWithErrors,
    activePart,
    selectPart,
  } = usePicker();
  const { isMobile: isTight } = useBreakpoint('mobile');

  const activeParts = useMemo(
    () => (activePart != null ? [activePart] : []),
    [activePart]
  );
  const dotClass = useCallback(
    (part: BodyPart) =>
      part === activePart
        ? 'scale-150 ring-3 ring-accent-background-30'
        : undefined,
    [activePart]
  );

  const { view, dotSize } = PICKER_TABS[tab];
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
  const tabs = <PickerTabs compact={compact} />;

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
              dotSize={dotSize[dots]}
              compact={isTight}
              fillHeight
              highlightedRoles={firstError?.affectedRoles || []}
              rolesWithErrors={rolesWithErrors}
              onRoleSelected={selectPart}
              renderGroup={renderGroup}
              dotClass={dotClass}
              dotProps={dotProps}
              activeParts={activeParts}
            />
          ) : (
            <BodyAssignment
              dotSize={dotSize[dots]}
              fillHeight
              highlightedRoles={firstError?.affectedRoles || []}
              rolesWithErrors={rolesWithErrors}
              mirror={config?.mirrorView ?? false}
              onRoleSelected={selectPart}
              renderCard={renderCard}
              dotClass={dotClass}
              dotProps={dotProps}
              activeParts={activeParts}
            />
          )}
        </div>
      </div>
    </div>
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
  const assignment = useAssignment();
  /** Dots double as drop targets while dragging */
  const dotProps = assignment.mode === 'drag' ? bodyPartDropProps : undefined;

  const renderCard: PartCardRenderer = (props) => (
    <BodyPartCard key={props.role} {...props} />
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
      renderRow={(props) => <BodyPartCard key={props.role} {...props} />}
    />
  );

  return (
    <PickerContext.Provider value={assignment}>
      <PickerPanel
        headerAction={headerAction}
        legendAction={legendAction}
        compact={compact}
        dots={assignment.mode}
        renderCard={renderCard}
        renderGroup={renderGroup}
        dotProps={dotProps}
      />
    </PickerContext.Provider>
  );
}

function PickerTabs({ compact }: { compact?: boolean }) {
  const { tab, setTab } = usePicker();

  return (
    <div
      className={classNames(
        'flex items-center bg-background-70 rounded-lg w-fit',
        compact ? 'gap-0.5 p-0.5' : 'gap-1 p-1'
      )}
    >
      {PICKER_TAB_ORDER.map((key) => (
        <Tab
          key={key}
          compact={compact}
          labelId={PICKER_TABS[key].labelId}
          active={tab === key}
          disabled={!PICKER_TABS[key].enabled}
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

export function BodyPartCard(props: PartCardProps) {
  const { mode } = useAssignment();

  return mode === 'drag' ? (
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
  compact,
  number,
  connector = true,
  labelId,
}: PartCardProps) {
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
}: PartCardProps) {
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
