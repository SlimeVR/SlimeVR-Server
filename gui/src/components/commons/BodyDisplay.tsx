import classNames from 'classnames';
import {
  ReactNode,
  useEffect,
  useLayoutEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { BodyPart, TrackerDataT } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/hooks/app';
import { useTracker } from '@/hooks/tracker';
import { PersonFrontIcon } from './PersonFrontIcon';

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
      ></div>
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
  leftControls,
  rightControls,
  trackers,
  width = 228,
  dotsSize = 20,
  variant = 'tracker-select',
  hideUnassigned = false,
}: {
  leftControls?: ReactNode;
  rightControls?: ReactNode;
  width?: number;
  dotsSize?: number;
  variant?: 'dots' | 'tracker-select';
  trackers: FlatDeviceTracker[];
  hideUnassigned: boolean;
}) {
  const personRef = useRef<HTMLDivElement | null>(null);
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

  useLayoutEffect(() => {
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
  }, [leftControls, rightControls, variant]);

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
    <div className="flex">
      <div
        ref={personRef}
        className={classNames(
          'relative w-full flex justify-center',
          variant === 'tracker-select' && 'mx-10'
        )}
      >
        <PersonFrontIcon width={width}></PersonFrontIcon>
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
