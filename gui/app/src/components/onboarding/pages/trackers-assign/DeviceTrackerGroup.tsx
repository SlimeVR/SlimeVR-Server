import { BodyPart } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/store/app-store';
import { DraggableTrackerCard } from './DraggableTrackerCard';

export function DeviceTrackerGroup({
  trackers,
  onDropTracker,
}: {
  trackers: FlatDeviceTracker[];
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
}) {
  const [primary, ...extensions] = trackers;

  return (
    <div className="flex flex-col">
      <DraggableTrackerCard
        tracker={primary.tracker}
        device={primary.device}
        onDrop={(bodyPart) =>
          onDropTracker(primary.tracker.trackerId, bodyPart)
        }
      />
      {extensions.length > 0 && (
        <div className="flex flex-col">
          {extensions.map((extension, index) => (
            <div key={index} className="flex items-stretch">
              <div className="w-6 shrink-0 relative">
                <div className="absolute left-4 top-0 h-8 w-4 border-l-2 border-b-2 border-dashed border-accent-background-30 rounded-bl-xl" />
                {index < extensions.length - 1 && (
                  <div className="absolute left-4 top-6 h-8 border-l-2 border-dashed border-accent-background-30" />
                )}
              </div>
              <div className="flex-grow min-w-0 pt-3 pl-2">
                <DraggableTrackerCard
                  tracker={extension.tracker}
                  device={extension.device}
                  onDrop={(bodyPart) =>
                    onDropTracker(extension.tracker.trackerId, bodyPart)
                  }
                />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
