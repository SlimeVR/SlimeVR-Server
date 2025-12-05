import classNames from 'classnames';
import { useEffect, useMemo, useRef, useState } from 'react';
import { BodyPart, TrackerDataT } from 'solarxr-protocol';
import { useTracker } from '@/hooks/tracker';
import { PersonFrontIcon } from './PersonFrontIcon';
import { FlatDeviceTracker } from '@/store/app-store';

interface SlotDot {
  id: string;
  left: number;
  top: number;
  height: number;
  width: number;
  buttonOffset: {
    left: number;
    top: number;
  };
}

type DotParams = {
  dotSize: number;
  trackers: FlatDeviceTracker[];
  hidden: boolean;
} & SlotDot;

function Tracker({
  tracker,
  updateVelocity,
}: {
  tracker: TrackerDataT;
  updateVelocity: (velocity: number) => void;
}) {
  const { useVelocity } = useTracker(tracker);

  const velocity = useVelocity();

  useEffect(() => {
    updateVelocity(velocity);
  }, [velocity]);

  return <></>;
}

function Dot({
  top,
  height,
  width,
  buttonOffset,
  id,
  left,
  dotSize,
  trackers,
  hidden,
}: DotParams) {
  const [velocities, setVelocities] = useState<number[]>([]);

  const updateVelocity = (vel: number) => {
    if (velocities.length > 3) {
      velocities.shift();
    }
    velocities.push(vel);
    setVelocities(velocities);
  };

  const globalVelocity = useMemo(
    () => velocities.reduce((curr, v) => curr + v, 0) / (trackers?.length || 1),
    [velocities, trackers]
  );

  return (
    <div
      key={id}
      className="absolute z-10"
      style={{
        top: top + height / 2 - dotSize / 2 + buttonOffset.top,
        left: left + width / 2 - dotSize / 2 + buttonOffset.left,
      }}
    >
      <div
        className={classNames(
          'rounded-full outline outline-2 outline-background-20',
          'bg-background-10 transition-transform',
          hidden && 'opacity-0'
        )}
        style={{
          width: dotSize,
          height: dotSize,
          outlineWidth: globalVelocity * 2 + 2,
        }}
      />
      {trackers?.map(({ tracker }, index) => (
        <Tracker
          tracker={tracker}
          key={index}
          updateVelocity={(vel) => updateVelocity(vel)}
        />
      ))}
    </div>
  );
}

export function BodyDisplay({
  trackers,
  dotsSize = 20,
  hideUnassigned = false,
}: {
  dotsSize?: number;
  trackers: FlatDeviceTracker[];
  hideUnassigned: boolean;
}) {
  const personRef = useRef<HTMLDivElement | null>(null);
  const resizeObserverRef = useRef<ResizeObserver>(
    new ResizeObserver(() => updateSlots())
  );
  const [slotsButtonsPos, setSlotsButtonPos] = useState<SlotDot[]>([]);

  const getSlotsPos = () => {
    return (
      (personRef.current && [
        ...(personRef.current.querySelectorAll('.body-part-circle') as any),
      ]) ||
      []
    );
  };

  const getOffset = (el: HTMLDivElement, offset = { left: 0, top: 0 }) => {
    const rect = el.getBoundingClientRect();
    return {
      left: rect.left - (offset.left || 0),
      top: rect.top - (offset.top || 0),
      width: rect.width || el.offsetWidth,
      height: rect.height || el.offsetHeight,
    };
  };

  const updateSlots = () => {
    if (!personRef.current) return;

    const slotsPos = getSlotsPos();
    const personBox = personRef.current.getBoundingClientRect();
    const slots = slotsPos.map((slot: HTMLDivElement) => {
      const slotPosition = getOffset(slot, personBox);
      return {
        ...slotPosition,
        id: slot.id,
        buttonOffset: {
          left: personBox.left - personBox.left,
          top: personBox.top - personBox.top,
        },
      };
    });
    setSlotsButtonPos(slots);
  };

  useEffect(() => {
    if (!personRef.current) return;

    resizeObserverRef.current.observe(personRef.current);

    updateSlots();

    return () => {
      if (!personRef.current) return;
      resizeObserverRef.current.unobserve(personRef.current);
    };
  }, []);

  const trackerPartGrouped = useMemo(
    () =>
      trackers.reduce<{ [key: number]: FlatDeviceTracker[] }>((curr, td) => {
        if (!td) return curr;

        const key = td.tracker.info?.bodyPart || BodyPart.NONE;
        return {
          ...curr,
          [key]: [...(curr[key] || []), td],
        };
      }, {}),
    [trackers]
  );

  return (
    <div className="flex w-full h-full">
      <div
        ref={personRef}
        className={classNames('relative w-full h-full flex justify-center')}
      >
        <PersonFrontIcon />
        {slotsButtonsPos.map((dotData) => (
          <Dot
            {...dotData}
            dotSize={dotsSize}
            key={dotData.id}
            hidden={
              hideUnassigned &&
              trackerPartGrouped[(BodyPart as any)[dotData.id]] === undefined
            }
            trackers={trackerPartGrouped[(BodyPart as any)[dotData.id]]}
          />
        ))}
      </div>
    </div>
  );
}
