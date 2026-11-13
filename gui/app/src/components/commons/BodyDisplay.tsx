import classNames from 'classnames';
import { useEffect, useMemo, useRef, useState } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { PersonFrontIcon } from './PersonFrontIcon';
import { FlatDeviceTracker, groupTrackerByBodyPart } from '@/store/app-store';

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
  hidden: boolean;
} & SlotDot;

function Dot({
  top,
  height,
  width,
  buttonOffset,
  id,
  left,
  dotSize,
  hidden,
}: DotParams) {
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
        }}
      />
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

  const trackerByPart = useMemo(
    () => groupTrackerByBodyPart(trackers),
    [trackers]
  );

  return (
    <div className="flex w-full h-full">
      <div
        ref={personRef}
        className={classNames('relative w-full h-full flex justify-center')}
      >
        <PersonFrontIcon />
        {slotsButtonsPos.map((dotData) => {
          const tracker =
            trackerByPart[BodyPart[dotData.id as keyof typeof BodyPart]];

          return (
            <Dot
              {...dotData}
              dotSize={dotsSize}
              key={dotData.id}
              hidden={hideUnassigned && tracker === undefined}
            />
          );
        })}
      </div>
    </div>
  );
}
