import classNames from 'classnames';
import { BodyPart } from 'solarxr-protocol';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Typography } from '@/components/commons/Typography';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { bodyPartDropProps, trackerDrag } from '@/hooks/tracker-drag';
import { FlatDeviceTracker } from '@/store/app-store';
import { useTracker, velocityGlowStyle } from '@/hooks/tracker';
import { DraggableTracker } from './DraggableTrackerCard';

function AssignedTrackerLabel({
  tracker,
  onDropTracker,
}: {
  tracker: FlatDeviceTracker;
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
}) {
  const { useVelocity } = useTracker(tracker.tracker);
  const velocity = useVelocity();
  const name =
    tracker.tracker.info?.customName ?? tracker.tracker.info?.displayName;

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

export function DropTargetPartCard({
  td,
  role,
  direction,
  roleError,
  armed,
  onSlotClick,
  onUnassign,
  onDropTracker,
}: {
  td: FlatDeviceTracker[];
  role: BodyPart;
  roleError: string | undefined;
  direction: 'left' | 'right';
  armed: boolean;
  onSlotClick: (role: BodyPart) => void;
  onUnassign: (role: BodyPart) => void;
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
}) {
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
      <Typography
        variant={'standard'}
        bold
        id={'body_part-' + BodyPart[role]}
      />
      <div className="min-h-10">
        {isAssigned ? (
          <div
            onClick={() => onUnassign(role)}
            className="flex flex-col gap-1 cursor-pointer"
          >
            {td.map((tracker, index) => (
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
