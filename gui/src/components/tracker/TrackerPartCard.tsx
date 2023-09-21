import classNames from 'classnames';
import { MouseEventHandler, useEffect, useMemo, useState } from 'react';
import { BodyPart, TrackerDataT } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/hooks/app';
import { useTracker } from '@/hooks/tracker';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';

function Tracker({
  tracker,
  updateVelocity,
}: {
  tracker: TrackerDataT;
  updateVelocity: (velocity: number) => void;
}) {
  const { l10n } = useLocalization();
  const { useVelocity } = useTracker(tracker);

  const velocity = useVelocity();

  useEffect(() => {
    updateVelocity(velocity);
  }, [velocity]);

  return (
    <Typography>
      {`${tracker.info?.customName || tracker.info?.displayName}` ||
        l10n.getString('tracker-part_card-unassigned')}
    </Typography>
  );
}

export function TrackerPartCard({
  td,
  role,
  direction,
  roleError,
  onlyAssigned,
  onClick,
}: {
  td: FlatDeviceTracker[];
  role: BodyPart;
  roleError: string | undefined;
  onlyAssigned: boolean;
  direction: 'left' | 'right';
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  const { l10n } = useLocalization();
  const [velocities, setVelocities] = useState<number[]>([]);

  const updateVelocity = (vel: number) => {
    if (velocities.length > 3) {
      velocities.shift();
    }
    velocities.push(vel);
    setVelocities(velocities);
  };

  useEffect(() => {
    if (!td) setVelocities([0, 0, 0]);
  }, [td]);

  const globalVelocity = useMemo(
    () =>
      Math.floor(
        velocities.reduce((curr, v) => curr + v, 0) / (td?.length || 1)
      ),
    [velocities, td]
  );

  const showCard = useMemo(
    () => (onlyAssigned && td && td.length > 0) || !onlyAssigned,
    [onlyAssigned, td]
  );

  return (
    (showCard && (
      <div
        className={classNames(
          'flex flex-col gap-1 control xs:w-32 hover:bg-background-50 px-2 py-1 rounded-md relative',
          direction === 'left' ? 'items-start' : 'items-end'
        )}
        id={BodyPart[role]}
        onClick={onClick}
        style={{
          boxShadow: `0px 0px ${globalVelocity * 3}px ${
            globalVelocity * 3
          }px  #BB8AE5`,
        }}
      >
        {roleError && (
          <div
            className={classNames(
              'absolute text-status-warning scale-75 -top-1',
              direction === 'right' ? '-right-6' : '-left-6'
            )}
          >
            <WarningIcon></WarningIcon>
          </div>
        )}
        <Typography color="secondary">
          {l10n.getString('body_part-' + BodyPart[role])}
        </Typography>
        {td?.map(({ tracker }, index) => (
          <Tracker
            tracker={tracker}
            key={index}
            updateVelocity={(vel) => updateVelocity(vel)}
          />
        ))}
        {!td && (
          <Typography>
            {l10n.getString('tracker-part_card-unassigned')}
          </Typography>
        )}
      </div>
    )) || <></>
  );
}
