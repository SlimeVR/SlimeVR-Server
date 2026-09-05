import classNames from 'classnames';
import { Fragment, ReactNode } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import {
  getTrackerName,
  useVelocity,
  velocityGlowStyle,
} from '@/hooks/tracker';
import { FlatDeviceTracker } from '@/store/app-store';
import { ExtremityRow } from '@/components/onboarding/ExtremityAssignment';
import { DigitFlow } from '@/components/onboarding/extremities/ExtremityLayout';

export type PartCardProps = {
  role: BodyPart;
  direction: 'left' | 'right';
  td: FlatDeviceTracker | undefined;
  roleError: string | undefined;
  labelId?: string;
  compact?: boolean;
  number?: number;
  connector?: boolean;
};

export type PartCardRenderer = (props: PartCardProps) => ReactNode;

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
  renderRow,
}: {
  labelId?: string;
  direction: 'left' | 'right';
  rows: ExtremityRow[];
  edge?: 'side' | 'cap';
  flow?: DigitFlow;
  className?: string;
  renderRow: PartCardRenderer;
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
            <Fragment key={role}>
              {renderRow({
                compact: true,
                number,
                connector,
                labelId: rowLabelId,
                role,
                direction,
                td,
                roleError,
              })}
            </Fragment>
          )
        )}
      </div>
    </div>
  );
}

export function PartCardLabel({
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

export function PartCardWarning({
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

export function AssignedTrackerLabel({
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
