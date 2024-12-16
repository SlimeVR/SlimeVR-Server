import classNames from 'classnames';
import {
  ReactNode,
  useRef,
  useState,
  ReactElement,
  useLayoutEffect,
  useMemo,
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
}

type TooltipState =
  | { open: false }
  | {
      open: true;
      childrenRect: DOMRect;
      tooltipRect: DOMRect;
    };

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

export function Tooltip({
  content,
  children,
  preferedDirection,
  mode = 'center',
}: TooltipProps) {
  const resizeRef = useRef(
    new ResizeObserver(([entry]) => {
      setState((curr) => ({
        ...curr,
        tooltipRect: entry.target.getBoundingClientRect(),
      }));
    })
  );
  const childRef = useRef<HTMLDivElement | null>(null);
  const tooltipRef = useRef<HTMLDivElement | null>(null);
  const [state, setState] = useState<TooltipState>({ open: false });

  const onMouseEnter = () => {
    if (!childRef.current || !tooltipRef.current)
      throw new Error('invalid state');

    const childrenRect = childRef.current.children[0].getBoundingClientRect();
    const tooltipRect = tooltipRef.current.getBoundingClientRect();

    setState((curr) => ({ ...curr, open: true, childrenRect, tooltipRect }));
    resizeRef.current.observe(tooltipRef.current);
  };

  const onMouseLeave = () => {
    if (!childRef.current || !tooltipRef.current)
      throw new Error('invalid state');
    resizeRef.current.unobserve(tooltipRef.current);
    setState({ open: false });
  };

  const onResize = () => {
    setState({ open: false });
  };

  useLayoutEffect(() => {
    if (childRef.current && childRef.current.children[0]) {
      const elem = childRef.current.children[0] as HTMLElement;

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

  const tooltipPosition = useMemo(() => {
    if (!state.open) return;

    const spacing = 10;

    const getPosition = (
      direction: TooltipProps['preferedDirection']
    ): TooltipPos => {
      switch (direction) {
        case 'top':
          return {
            top: state.childrenRect.y - state.tooltipRect.height - spacing,
            left:
              state.childrenRect.x +
              (mode === 'center'
                ? state.childrenRect.width / 2 - state.tooltipRect.width / 2
                : 0),
            width: state.tooltipRect.width,
          };
        case 'left':
          return {
            top:
              state.childrenRect.y +
              (mode === 'center'
                ? state.childrenRect.height / 2 - state.tooltipRect.height / 2
                : 0),
            left: state.childrenRect.x - state.tooltipRect.width - spacing,
            width: state.tooltipRect.width,
          };
        case 'right':
          return {
            top:
              state.childrenRect.y +
              (mode === 'center'
                ? state.childrenRect.height / 2 - state.tooltipRect.height / 2
                : 0),
            left: state.childrenRect.x + state.childrenRect.width + spacing,
            width: state.tooltipRect.width,
          };
        case 'bottom':
          return {
            top: state.childrenRect.y + state.childrenRect.height + spacing,
            left:
              state.childrenRect.x +
              (mode === 'center'
                ? state.childrenRect.width / 2 - state.tooltipRect.width / 2
                : 0),
            width: state.tooltipRect.width,
          };
      }
    };

    const windowRect = document.body.getBoundingClientRect();

    const pos = getPosition(preferedDirection);

    if (isNotInside({ ...pos, height: state.tooltipRect.height }, windowRect)) {
      const [firstPos] = ['left', 'right', 'top', 'left']
        .map((dir) => getPosition(dir as TooltipProps['preferedDirection']))
        .toSorted(
          (a, b) =>
            overlapArea(
              { ...b, height: state.tooltipRect.height },
              windowRect
            ) -
            overlapArea({ ...a, height: state.tooltipRect.height }, windowRect)
        );

      if (
        isNotInside(
          { ...firstPos, height: state.tooltipRect.height },
          windowRect
        )
      ) {
        // TODO: clamp to maximum possible size
        return;
      }

      return firstPos;
    }
    return pos;
  }, [state, preferedDirection]);

  return (
    <div className="contents" ref={childRef}>
      {children}
      {createPortal(
        <div
          className={classNames('absolute z-50')}
          ref={tooltipRef}
          style={tooltipPosition}
        >
          <div className="bg-background-90 rounded-md p-2 text-background-10">
            {content}
          </div>
        </div>,
        document.body
      )}
    </div>
  );
}
