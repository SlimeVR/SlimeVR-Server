import classNames from 'classnames';
import { CSSProperties, ReactNode } from 'react';
import { BodyPart, DeviceDataT, TrackerDataT } from 'solarxr-protocol';
import { trackerDrag, useIsTrackerBeingDragged } from '@/hooks/tracker-drag';
import { SimpleTrackerRow } from './SimpleTrackerRow';

export function DraggableTracker({
  trackerId,
  label,
  onDrop,
  className,
  style,
  children,
}: {
  trackerId: number;
  label: string;
  onDrop: (bodyPart: BodyPart | null) => void;
  className?: string;
  style?: CSSProperties;
  children: ReactNode;
}) {
  const isBeingDragged = useIsTrackerBeingDragged(trackerId);
  const { dragProps } = trackerDrag.useDraggable({ trackerId, label }, onDrop);

  return (
    <div
      {...dragProps}
      className={classNames(
        'touch-none cursor-grab active:cursor-grabbing select-none',
        'transition-[transform,opacity] hover:scale-[1.02] hover:animate-wiggle',
        isBeingDragged && 'opacity-40',
        className
      )}
      style={style}
    >
      {children}
    </div>
  );
}

export function DraggableTrackerCard({
  tracker,
  device,
  onDrop,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  onDrop: (bodyPart: BodyPart) => void;
}) {
  const name = tracker.info?.customName ?? tracker.info?.displayName;

  return (
    <DraggableTracker
      trackerId={tracker.trackerId}
      label={name?.toString() ?? 'unknown'}
      onDrop={(bodyPart) => {
        if (bodyPart !== null && bodyPart !== BodyPart.NONE) onDrop(bodyPart);
      }}
    >
      <SimpleTrackerRow tracker={tracker} device={device} />
    </DraggableTracker>
  );
}
