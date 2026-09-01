import classNames from 'classnames';
import {
  HTMLAttributes,
  PointerEvent,
  ReactNode,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { BodyPart } from 'solarxr-protocol';
import { useBreakpoint } from '@/hooks/breakpoint';

const DOT_HIT_PADDING = 12;

export interface BodySlotStyle {
  props?: HTMLAttributes<HTMLDivElement>;
  className?: string;
  connected?: boolean;
  content?: ReactNode;
}

export type BodySlotStyler = (part: BodyPart) => BodySlotStyle;

export interface BodySideNames {
  left: Set<string>;
  right: Set<string>;
}

const NO_SLOT_STYLE: BodySlotStyle = {};

const boxOf = (el: HTMLElement, offset: { left: number; top: number }) => {
  const rect = el.getBoundingClientRect();
  return {
    left: rect.left - offset.left,
    top: rect.top - offset.top,
    right: rect.right - offset.left,
    bottom: rect.bottom - offset.top,
  };
};

const geometry = (
  slot: { left: number; top: number; width: number; height: number },
  control: HTMLElement,
  group: HTMLElement,
  offset: { left: number; top: number }
) => {
  const slotX = slot.left + slot.width / 2;
  const slotY = slot.top + slot.height / 2;
  const { left, top, right, bottom } = boxOf(group, offset);
  const row = boxOf(control, offset);
  const rowY = (row.top + row.bottom) / 2;

  const cap = group.dataset.connectorEdge === 'cap';

  return {
    x: cap ? (left + right) / 2 : slotX > right ? right : left,
    y: cap
      ? slotY > bottom
        ? bottom
        : top
      : Math.min(Math.max(rowY, top), bottom),
    dx: cap ? 0 : slotX > right ? 1 : -1,
    dy: cap ? (slotY > bottom ? 1 : -1) : 0,
    gap: cap
      ? Math.max(top - slotY, slotY - bottom, 0)
      : Math.max(left - slotX, slotX - right, 0),
  };
};

const readCssVarColor = (varName: string, fallback: string) => {
  if (typeof window === 'undefined') return fallback;
  const raw = getComputedStyle(document.documentElement)
    .getPropertyValue(varName)
    .trim();
  return raw ? `rgb(${raw})` : fallback;
};

export interface BodyInteractionsProps {
  leftControls?: ReactNode;
  rightControls?: ReactNode;
  topControls?: ReactNode;
  bottomControls?: ReactNode;
  dotsSize?: number;
  assignedRoles: BodyPart[];
  onSelectRole: (role: BodyPart) => void;
  highlightedRoles: BodyPart[];
  slotStyle?: BodySlotStyler;
  figure: ReactNode;
  sideNames: BodySideNames;
}

export function BodyInteractions({
  leftControls,
  rightControls,
  topControls,
  bottomControls,
  highlightedRoles,
  assignedRoles,
  dotsSize = 15,
  onSelectRole,
  slotStyle,
  figure,
  sideNames,
}: BodyInteractionsProps) {
  const { isMobile } = useBreakpoint('mobile');
  const { left: leftPartNames, right: rightPartNames } = sideNames;

  const [hoveredControl, setHoveredControl] = useState<string | null>(null);

  const personRef = useRef<HTMLDivElement | null>(null);
  const leftContainerRef = useRef<HTMLDivElement | null>(null);
  const rightContainerRef = useRef<HTMLDivElement | null>(null);
  const topContainerRef = useRef<HTMLDivElement | null>(null);
  const bottomContainerRef = useRef<HTMLDivElement | null>(null);
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
    const top = (topContainerRef.current && pos(topContainerRef.current)) || [];
    const bottom =
      (bottomContainerRef.current && pos(bottomContainerRef.current)) || [];
    return [...left, ...right, ...top, ...bottom];
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
        hidden: !controlsPosIds.includes(slot.id),
        buttonOffset: {
          left: canvasBox.left - personBox.left,
          top: canvasBox.top - personBox.top,
        },
      };
    });

    const whiteColor = readCssVarColor('--background-20', '#FFFFFF');
    const ASSIGN_RIGHT = readCssVarColor('--assign-right', '#FFFFFF');
    const ASSIGN_LEFT = readCssVarColor('--assign-left', '#FFFFFF');
    const LINE_BREAK_WIDTH = isMobile ? 20 : 40;

    slots.forEach((slot) => {
      const controls = controlsPos.filter(
        ({ id, dataset }) => id === slot.id && dataset.connector !== 'off'
      );
      const isAssigned = assignedRoles.includes((BodyPart as any)[slot.id]);
      const { connected } = slotStyle?.((BodyPart as any)[slot.id]) ?? {};

      ctx.lineWidth = slot.id === hoveredControl || connected ? 4 : 2;
      ctx.strokeStyle =
        isAssigned || connected
          ? leftPartNames.has(slot.id)
            ? ASSIGN_LEFT
            : rightPartNames.has(slot.id)
              ? ASSIGN_RIGHT
              : whiteColor
          : '#204A6B';

      const slotX = slot.left + slot.width / 2;
      const slotY = slot.top + slot.height / 2;

      controls.forEach((control) => {
        const group = control.closest('[data-connector-group]');
        const controlPosition = getOffset(group ?? control, canvasBox);

        if (group) {
          const { x, y, dx, dy, gap } = geometry(
            slot,
            control,
            group,
            canvasBox
          );

          ctx.beginPath();
          ctx.moveTo(x, y);
          if (gap > LINE_BREAK_WIDTH * 2)
            ctx.lineTo(x + dx * LINE_BREAK_WIDTH, y + dy * LINE_BREAK_WIDTH);
          ctx.lineTo(slotX, slotY);
          ctx.stroke();
          return;
        }

        const offsetX =
          controlPosition.left < slot.left ? controlPosition.width : 0;

        const constolLeft = controlPosition.left + offsetX;
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
        ctx.lineTo(slotX, slotY);
        ctx.stroke();
      });
    });
    setSlotsButtonPos(slots);
  };

  const onControlPointerOver = (event: PointerEvent<HTMLDivElement>) => {
    const control = (event.target as HTMLElement).closest<HTMLElement>(
      '.control'
    );
    setHoveredControl(control?.id || null);
  };

  updateSlotsRef.current = updateSlots;
  const assignedKey = useMemo(
    () => [...assignedRoles].sort((a, b) => a - b).join(','),
    [assignedRoles]
  );

  useEffect(() => {
    updateSlots();
  }, [figure, assignedKey, slotStyle, hoveredControl]);

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
    if (topContainerRef.current)
      mutationObserverRef.current.observe(topContainerRef.current, {
        attributes: true,
        childList: true,
        subtree: true,
      });
    if (bottomContainerRef.current)
      mutationObserverRef.current.observe(bottomContainerRef.current, {
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
      <div className="flex flex-col w-full h-full">
        <div
          ref={topContainerRef}
          className="z-10"
          onPointerOver={onControlPointerOver}
          onPointerLeave={() => setHoveredControl(null)}
        >
          {topControls}
        </div>
        <div className="flex flex-grow min-h-0 gap-5">
          <div
            ref={leftContainerRef}
            className="z-10"
            onPointerOver={onControlPointerOver}
            onPointerLeave={() => setHoveredControl(null)}
          >
            {leftControls}
          </div>
          <div
            ref={personRef}
            className="relative flex justify-center flex-grow"
          >
            {figure}
            {slotsButtonsPos.map(
              ({ top, left, height, width, id, hidden, buttonOffset }) => {
                const style =
                  slotStyle?.((BodyPart as any)[id]) ?? NO_SLOT_STYLE;
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
                          'flex items-center justify-center',
                          assignedRoles.includes((BodyPart as any)[id])
                            ? 'bg-status-success'
                            : 'bg-background-10',
                          leftPartNames.has(id) &&
                            'border-4 border-assign-left',
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
                      >
                        {style.content}
                      </div>
                    </div>
                  </div>
                );
              }
            )}
          </div>
          <div
            ref={rightContainerRef}
            className="z-10"
            onPointerOver={onControlPointerOver}
            onPointerLeave={() => setHoveredControl(null)}
          >
            {rightControls}
          </div>
        </div>
        <div
          ref={bottomContainerRef}
          className="z-10"
          onPointerOver={onControlPointerOver}
          onPointerLeave={() => setHoveredControl(null)}
        >
          {bottomControls}
        </div>
      </div>
    </div>
  );
}
