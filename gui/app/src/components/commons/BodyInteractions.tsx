import classNames from 'classnames';
import {
  HTMLAttributes,
  ReactNode,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { BodyPart } from 'solarxr-protocol';
import { PersonFrontIcon, SIDES } from './PersonFrontIcon';
import { useBreakpoint } from '@/hooks/breakpoint';

const DOT_HIT_PADDING = 12;

export interface BodySlotStyle {
  props?: HTMLAttributes<HTMLDivElement>;
  className?: string;
  connected?: boolean;
}

export type BodySlotStyler = (part: BodyPart) => BodySlotStyle;

const NO_SLOT_STYLE: BodySlotStyle = {};

const readCssVarColor = (varName: string, fallback: string) => {
  if (typeof window === 'undefined') return fallback;
  const raw = getComputedStyle(document.documentElement)
    .getPropertyValue(varName)
    .trim();
  return raw ? `rgb(${raw})` : fallback;
};

export function BodyInteractions({
  leftControls,
  rightControls,
  highlightedRoles,
  assignedRoles,
  dotsSize = 15,
  variant = 'tracker-select',
  mirror,
  fillHeight = false,
  onSelectRole,
  slotStyle,
}: {
  leftControls?: ReactNode;
  rightControls?: ReactNode;
  width?: number;
  dotsSize?: number;
  variant?: 'dots' | 'tracker-select';
  assignedRoles: BodyPart[];
  onSelectRole: (role: BodyPart) => void;
  highlightedRoles: BodyPart[];
  mirror: boolean;
  fillHeight?: boolean;
  slotStyle?: BodySlotStyler;
}) {
  const { isMobile } = useBreakpoint('mobile');

  const { leftPartNames, rightPartNames } = useMemo(() => {
    const left = +!mirror;
    const right = +mirror;
    return {
      leftPartNames: new Set(
        Object.values(SIDES[left]).map((part) => BodyPart[part])
      ),
      rightPartNames: new Set(
        Object.values(SIDES[right]).map((part) => BodyPart[part])
      ),
    };
  }, [mirror]);

  const personRef = useRef<HTMLDivElement | null>(null);
  const leftContainerRef = useRef<HTMLDivElement | null>(null);
  const rightContainerRef = useRef<HTMLDivElement | null>(null);
  const updateSlotsRef = useRef<() => void>(() => {});
  const mutationObserverRef = useRef<MutationObserver>(
    new MutationObserver(() => updateSlotsRef.current())
  );
  const resizeObserverRef = useRef<ResizeObserver>(
    new ResizeObserver(() => updateSlotsRef.current())
  );
  const canvasRefRef = useRef<HTMLCanvasElement | null>(null);
  const [slotsButtonsPos, setSlotsButtonPos] = useState<
    {
      id: string;
      left: number;
      top: number;
      height: number;
      width: number;
      hidden: boolean;
      buttonOffset: {
        left: number;
        top: number;
      };
    }[]
  >([]);

  const getSlotsPos = () => {
    return (
      (personRef.current && [
        ...(personRef.current.querySelectorAll('.body-part-circle') as any),
      ]) ||
      []
    );
  };

  const getControlsPos = () => {
    const pos = (container: HTMLDivElement) =>
      [...(container.querySelectorAll('.control') as any)].filter(
        ({ id }) => !!id
      );

    const left =
      (leftContainerRef.current && pos(leftContainerRef.current)) || [];
    const right =
      (rightContainerRef.current && pos(rightContainerRef.current)) || [];
    return [...left, ...right];
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
    if (
      !(
        personRef.current &&
        canvasRefRef.current &&
        rightContainerRef.current &&
        leftContainerRef.current
      )
    )
      return;
    const ctx = canvasRefRef.current.getContext('2d');
    if (!ctx) return;
    const slotsPos = getSlotsPos();
    const controlsPos = getControlsPos();

    canvasRefRef.current.width = canvasRefRef.current.clientWidth;
    canvasRefRef.current.height = canvasRefRef.current.clientHeight;

    ctx.strokeStyle = '#608AAB';
    ctx.lineWidth = 2;

    const canvasBox = canvasRefRef.current.getBoundingClientRect();
    const personBox = personRef.current.getBoundingClientRect();

    const controlsPosIds = controlsPos.map(({ id: cid }) => cid);
    const slots = slotsPos.map((slot: HTMLDivElement) => {
      const slotPosition = getOffset(slot, canvasBox);
      return {
        ...slotPosition,
        id: slot.id,
        hidden:
          variant === 'tracker-select' && !controlsPosIds.includes(slot.id),
        buttonOffset: {
          left: canvasBox.left - personBox.left,
          top: canvasBox.top - personBox.top,
        },
      };
    });

    if (variant === 'tracker-select') {
      const whiteColor = readCssVarColor('--background-20', '#FFFFFF');
      const ASSIGN_RIGHT = readCssVarColor('--assign-right', '#FFFFFF');
      const ASSIGN_LEFT = readCssVarColor('--assign-left', '#FFFFFF');

      slots.forEach((slot) => {
        const controls = controlsPos.filter(({ id }) => id === slot.id);
        const isAssigned = assignedRoles.includes((BodyPart as any)[slot.id]);
        const { connected } = slotStyle?.((BodyPart as any)[slot.id]) ?? {};

        ctx.strokeStyle =
          isAssigned || connected
            ? leftPartNames.has(slot.id)
              ? ASSIGN_LEFT
              : rightPartNames.has(slot.id)
                ? ASSIGN_RIGHT
                : whiteColor
            : '#204A6B';

        controls.forEach((control) => {
          const controlPosition = getOffset(control, canvasBox);

          const offsetX =
            controlPosition.left < slot.left ? controlPosition.width : 0;

          const constolLeft = controlPosition.left + offsetX;
          const LINE_BREAK_WIDTH = isMobile ? 20 : 40;
          const leftOffsetX =
            LINE_BREAK_WIDTH * (controlPosition.left < slot.left ? -1 : 1);

          ctx.beginPath();
          ctx.moveTo(
            constolLeft,
            controlPosition.top + controlPosition.height / 2
          );
          ctx.lineTo(
            constolLeft - leftOffsetX,
            controlPosition.top + controlPosition.height / 2
          );
          ctx.lineTo(slot.left + slot.width / 2, slot.top + slot.height / 2);
          ctx.stroke();
        });
      });
    }
    setSlotsButtonPos(slots);
  };

  updateSlotsRef.current = updateSlots;
  const assignedKey = useMemo(
    () => [...assignedRoles].sort((a, b) => a - b).join(','),
    [assignedRoles]
  );

  useEffect(() => {
    updateSlots();
  }, [variant, mirror, assignedKey, slotStyle]);

  useEffect(() => {
    if (
      !rightContainerRef.current ||
      !leftContainerRef.current ||
      !personRef.current
    )
      return;

    resizeObserverRef.current.observe(personRef.current);

    mutationObserverRef.current.observe(rightContainerRef.current, {
      attributes: true,
      childList: true,
      subtree: true,
    });
    mutationObserverRef.current.observe(leftContainerRef.current, {
      attributes: true,
      childList: true,
      subtree: true,
    });

    return () => {
      if (
        !rightContainerRef.current ||
        !leftContainerRef.current ||
        !personRef.current
      )
        return;
      mutationObserverRef.current.takeRecords();
      resizeObserverRef.current.unobserve(personRef.current);
    };
  }, []);

  return (
    <div className="relative w-full h-full">
      <canvas
        ref={canvasRefRef}
        className="absolute w-full h-full top-0 z-10"
        width="100%"
        height="100%"
      />
      <div className="flex w-full h-full gap-5">
        <div ref={leftContainerRef} className="z-10">
          {leftControls}
        </div>
        <div ref={personRef} className="relative flex justify-center flex-grow">
          <PersonFrontIcon
            mirror={mirror}
            className={fillHeight ? 'absolute inset-0 h-full w-full' : 'w-full'}
          />
          {slotsButtonsPos.map(
            ({ top, left, height, width, id, hidden, buttonOffset }) => {
              const style = slotStyle?.((BodyPart as any)[id]) ?? NO_SLOT_STYLE;
              const hitSize = dotsSize + DOT_HIT_PADDING * 2;

              return (
                <div
                  key={id}
                  {...style.props}
                  className={classNames('absolute z-10')}
                  onClick={() => onSelectRole((BodyPart as any)[id])}
                  style={{
                    width: hitSize,
                    height: hitSize,
                    top: top + height / 2 - hitSize / 2 + buttonOffset.top,
                    left: left + width / 2 - hitSize / 2 + buttonOffset.left,
                  }}
                >
                  <div
                    className="absolute"
                    style={{ top: DOT_HIT_PADDING, left: DOT_HIT_PADDING }}
                  >
                    {!hidden &&
                      highlightedRoles.includes((BodyPart as any)[id]) && (
                        <div
                          className={classNames(
                            'absolute rounded-full bg-status-warning',
                            'transition-opacity opacity-100 animate-ping'
                          )}
                          style={{
                            width: dotsSize,
                            height: dotsSize,
                            animationDuration: '1.5s',
                          }}
                        />
                      )}
                    <div
                      className={classNames(
                        'absolute rounded-full outline-background-90 transition duration-150 ease-linear box-border',
                        'hover:bg-accent-background-40',
                        assignedRoles.includes((BodyPart as any)[id])
                          ? 'bg-status-success'
                          : 'bg-background-10',
                        leftPartNames.has(id) && 'border-4 border-assign-left',
                        rightPartNames.has(id) &&
                          'border-4 border-assign-right',
                        style.className,
                        hidden ? 'opacity-0' : 'opacity-100'
                      )}
                      style={{
                        width: dotsSize,
                        height: dotsSize,
                        boxShadow: '0px 0px 4px black',
                      }}
                    />
                  </div>
                </div>
              );
            }
          )}
        </div>
        <div ref={rightContainerRef} className="z-10">
          {rightControls}
        </div>
      </div>
    </div>
  );
}
