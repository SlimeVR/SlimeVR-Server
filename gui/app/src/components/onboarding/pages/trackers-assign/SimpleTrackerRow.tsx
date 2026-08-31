import { DeviceDataT, TrackerDataT } from 'solarxr-protocol';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Typography } from '@/components/commons/Typography';
import { TrackerStatus } from '@/components/tracker/TrackerStatus';
import { useTracker, velocityGlowStyle } from '@/hooks/tracker';

export function SimpleTrackerRow({
  tracker,
  device,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
}) {
  const { useVelocity } = useTracker(tracker);
  const velocity = useVelocity();

  return (
    <div
      className="flex items-center gap-3 rounded-lg h-12 bg-background-60 p-2 pr-3 transition-[box-shadow] duration-200 ease-linear"
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
          {tracker.info?.customName ?? tracker.info?.displayName}
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
}
