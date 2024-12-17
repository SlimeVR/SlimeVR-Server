import classNames from 'classnames';
import {
  ReactNode,
  useRef,
  useState,
  ReactElement,
  useLayoutEffect,
} from 'react';
import { createPortal } from 'react-dom';

interface TooltipProps {
  content: ReactNode;
  children: ReactElement;
  preferedDirection: 'top' | 'left' | 'right' | 'bottom';
  mode?: 'corner' | 'center';
}

interface TooltipPos {
  left: number;
  top: number;
  width: number;
  height?: number;
}

type Rect = {
  left: number;
  top: number;
  width: number;
  height: number;
};

function overlapArea(rect1: Rect, rect2: Rect) {
  // Find the overlap in the x direction (width)
  const overlapWidth = Math.max(
    0,
    Math.min(rect1.left + rect1.width, rect2.left + rect2.width) -
    Math.max(rect1.left, rect2.left)
  );

  // Find the overlap in the y direction (height)
  const overlapHeight = Math.max(
    0,
    Math.min(rect1.top + rect1.height, rect2.top + rect2.height) -
    Math.max(rect1.top, rect2.top)
  );

  // If there is an overlap, return the area; otherwise, return 0
  return overlapWidth * overlapHeight;
}

function isNotInside(rect1: Rect, rect2: Rect) {
  // Check if rect1 is not inside rect2 or rect2 is not inside rect1
  const rect1InsideRect2 =
    rect1.left >= rect2.left &&
    rect1.left + rect1.width <= rect2.left + rect2.width &&
    rect1.top >= rect2.top &&
    rect1.top + rect1.height <= rect2.top + rect2.height;

  const rect2InsideRect1 =
    rect2.left >= rect1.left &&
    rect2.left + rect2.width <= rect1.left + rect1.width &&
    rect2.top >= rect1.top &&
    rect2.top + rect2.height <= rect1.top + rect1.height;

  // If neither is inside the other, return true
  return !(rect1InsideRect2 || rect2InsideRect1);
}

const clamp = (v: number, min: number, max: number) => Math.min(max, Math.max(min, v))

export function Tooltip({
  content,
  children,
  preferedDirection,
  mode = 'center',
}: TooltipProps) {
  const childRef = useRef<HTMLDivElement | null>(null);
  const tooltipRef = useRef<HTMLDivElement | null>(null);
  // const [state, setState] = useState<TooltipState>({ open: false });
  const [style, setTooltipStyle] = useState<TooltipPos | undefined>();

  const onMouseEnter = () => {
    if (!childRef.current || !tooltipRef.current)
      throw new Error('invalid state');

    const childrenRect = childRef.current.children[0].getBoundingClientRect();
    const tooltipRect = tooltipRef.current.getBoundingClientRect();

    setTooltipStyle(getTooltipPosition(childrenRect, tooltipRect))
  };

  const onMouseLeave = () => {
    if (!childRef.current || !tooltipRef.current)
      throw new Error('invalid state');
    setTooltipStyle(undefined)
  };

  const onResize = () => {
    setTooltipStyle(undefined)

  };

  useLayoutEffect(() => {
    if (childRef.current && childRef.current.children[0]) {
      const elem = childRef.current.children[0] as HTMLElement;
      console.log(elem)
      elem.addEventListener('mouseenter', onMouseEnter);
      elem.addEventListener('mouseleave', onMouseLeave);

      return () => {
        elem.removeEventListener('mouseenter', onMouseEnter);
        elem.removeEventListener('mouseleave', onMouseLeave);
      };
    }
  }, []);

  useLayoutEffect(() => {
    window.addEventListener('resize', onResize);
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, []);

  const getTooltipPosition = (
    childrenRect: DOMRect,
    tooltipRect: DOMRect,
  ) => {
    const spacing = 10;

    const getPosition = (
      direction: TooltipProps['preferedDirection']
    ): TooltipPos => {
      switch (direction) {
        case 'top':
          return {
            top: childrenRect.y - tooltipRect.height - spacing,
            left:
              childrenRect.x +
              (mode === 'center'
                ? childrenRect.width / 2 - tooltipRect.width / 2
                : 0),
            width: tooltipRect.width,
          };
        case 'left':
          return {
            top:
              childrenRect.y +
              (mode === 'center'
                ? childrenRect.height / 2 - tooltipRect.height / 2
                : 0),
            left: childrenRect.x - tooltipRect.width - spacing,
            width: tooltipRect.width,
          };
        case 'right':
          return {
            top:
              childrenRect.y +
              (mode === 'center'
                ? childrenRect.height / 2 - tooltipRect.height / 2
                : 0),
            left: childrenRect.x + childrenRect.width + spacing,
            width: tooltipRect.width,
          };
        case 'bottom':
          return {
            top: childrenRect.y + childrenRect.height + spacing,
            left:
              childrenRect.x +
              (mode === 'center'
                ? childrenRect.width / 2 - tooltipRect.width / 2
                : 0),
            width: tooltipRect.width,
          };
      }
    };

    const windowRect = document.body.getBoundingClientRect();

    const pos = getPosition(preferedDirection);
    if (isNotInside({ ...pos, height: tooltipRect.height }, windowRect)) {
      const [firstPos] = ['left', 'top', 'right', 'bottom']
        .map((dir) => ({ dir, area: getPosition(dir as TooltipProps['preferedDirection']) }))
        .toSorted(
          (a, b) =>
            overlapArea(
              { ...b.area, height: tooltipRect.height },
              windowRect
            ) -
            overlapArea({ ...a.area, height: tooltipRect.height }, windowRect)
        );

      if (
        isNotInside(
          { ...firstPos.area, height: tooltipRect.height },
          windowRect
        )
      ) {
        switch (firstPos.dir) {
          case 'top':
            return {
              top: clamp(firstPos.area.top, spacing, childrenRect.y - spacing),
              left: clamp(firstPos.area.left, spacing, childrenRect.x - spacing),
              width: clamp(firstPos.area.width, 0, windowRect.width - spacing * 2),
              height: clamp(tooltipRect.height, spacing, childrenRect.y - spacing * 2)
            };
          case 'left':
            return {
              top: clamp(firstPos.area.top, spacing, windowRect.height - spacing),
              left: clamp(firstPos.area.left, spacing, childrenRect.x - spacing),
              width: clamp(firstPos.area.width, 0, childrenRect.x - spacing * 2),
              height: clamp(tooltipRect.height, spacing, windowRect.height - spacing * 2)
            };
          case 'right':
            return {
              top: clamp(firstPos.area.top, spacing, windowRect.height - spacing),
              left: clamp(firstPos.area.left, childrenRect.x + childrenRect.width + spacing, windowRect.width - spacing),
              width: clamp(firstPos.area.width, 0, windowRect.width - spacing - firstPos.area.left),
              height: clamp(tooltipRect.height, spacing, windowRect.height - spacing * 2)
            };
          case 'bottom':
            return {
              top: clamp(firstPos.area.top, childrenRect.y + childrenRect.height + spacing, windowRect.height - childrenRect.y + childrenRect.height + spacing),
              left: clamp(firstPos.area.left, spacing, windowRect.width),
              width: clamp(firstPos.area.width, 0, windowRect.width - spacing * 2),
              height: clamp(tooltipRect.height, firstPos.area.top + childrenRect.height + spacing, windowRect.height - (childrenRect.y + childrenRect.height) - spacing * 2)
            };
        }
      }

      return firstPos.area;
    }
    return pos;
  }

  return (
    <div className="contents" ref={childRef}>
      {children}
      {createPortal(
        <div
          className={classNames('absolute z-50 pointer-events-none')}
          ref={tooltipRef}
          style={style}
        >
          <div className="bg-background-90 rounded-md p-2 text-background-10 overflow-auto" style={style}>
            {content}
          </div>
        </div>,
        document.body
      )}
    </div>
  );
}
