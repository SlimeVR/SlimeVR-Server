import classNames from 'classnames';
import { BodyPart, QuatT } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { ArrowUpIcon } from '@/components/commons/icon/ArrowIcons';
import {
  PartCardLabel,
  PartCardProps,
  PartCardWarning,
} from '@/components/onboarding/parts/PartCard';
import {
  getTrackerName,
  useVelocity,
  velocityGlowStyle,
} from '@/hooks/tracker';
import { usePicker } from '@/hooks/tracker-picker';
import { getYawInDegrees } from '@/maths/quaternion';

function MountingArrow({ orientation }: { orientation?: QuatT | null }) {
  return (
    <div
      className="shrink-0 fill-background-10"
      style={{
        transform: `rotate(${getYawInDegrees(orientation ?? undefined) - 180}deg)`,
      }}
    >
      <ArrowUpIcon size={16} />
    </div>
  );
}

export function MountingPartCard({
  td,
  role,
  direction,
  roleError,
  labelId,
  number,
  compact,
  connector = true,
}: PartCardProps) {
  const { activePart, selectPart } = usePicker();
  const velocity = useVelocity(td?.tracker);

  if (!td) return null;

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
        activePart === role
          ? 'bg-accent-background-30/40'
          : 'hover:bg-background-50'
      )}
    >
      <PartCardWarning error={roleError} direction={direction} />
      <PartCardLabel
        role={role}
        direction={direction}
        number={number}
        labelId={labelId}
      />
      <div
        className={classNames(
          'flex items-center gap-1.5 max-w-full min-w-0',
          direction === 'right' && 'flex-row-reverse'
        )}
      >
        <MountingArrow orientation={td.tracker.info?.mountingOrientation} />
        <Typography variant="standard" truncate>
          {getTrackerName(td.tracker.info)}
        </Typography>
      </div>
    </button>
  );
}
