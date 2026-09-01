import classNames from 'classnames';
import { MouseEventHandler } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { getTrackerName } from '@/hooks/tracker';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { FlatDeviceTracker } from '@/store/app-store';
import { useBreakpoint } from '@/hooks/breakpoint';

export function TrackerPartCard({
  td,
  role,
  direction,
  roleError,
  onClick,
}: {
  td: FlatDeviceTracker | undefined;
  role: BodyPart;
  roleError: string | undefined;
  direction: 'left' | 'right';
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  const { isXs } = useBreakpoint('xs');
  const { l10n } = useLocalization();

  const name = td && getTrackerName(td.tracker.info);

  return (
    <div
      className={classNames(
        'flex flex-col gap-1 control xs:w-auto hover:bg-background-50 cursor-pointer px-2 py-1 rounded-md relative transition-[box-shadow] duration-200 ease-linear',
        direction === 'left' ? 'items-start' : 'items-end'
      )}
      id={BodyPart[role]}
      onClick={onClick}
    >
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
      <Typography
        variant={isXs ? 'section-title' : 'standard'}
        whitespace={isXs ? 'whitespace-nowrap' : undefined}
      >
        {l10n.getString('body_part-' + BodyPart[role])}
      </Typography>
      <Typography color={name ? undefined : 'secondary'}>
        {name || l10n.getString('tracker-part_card-unassigned')}
      </Typography>
    </div>
  );
}
