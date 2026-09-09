import classNames from 'classnames';
import {
  CSSProperties,
  ReactNode,
  useRef,
  useState,
  ReactElement,
  useLayoutEffect,
  MutableRefObject,
  createElement,
  useEffect,
  useId,
} from 'react';
import { createPortal } from 'react-dom';
import { Typography } from './Typography';
import { CloseIcon } from './icon/CloseIcon';

type Direction = 'top' | 'left' | 'right' | 'bottom';
interface TooltipProps {
  content: ReactNode;
  children: ReactElement;
  preferedDirection: Direction;
  blockedDirections?: Direction[];
  mode?: 'corner' | 'center';
  variant?: 'auto' | 'drawer' | 'floating';
  disabled?: boolean;
  tag?: string;
  spacing?: number;
  bindTo?: string;
}

type PopoverElement = HTMLDivElement & {
  showPopover?: () => void;
  hidePopover?: () => void;
};

const getPositionArea = (
  preferedDirection: TooltipProps['preferedDirection'],
  mode: TooltipProps['mode']
) => {
  if (mode === 'corner') {
    switch (preferedDirection) {
      case 'top':
        return 'top span-right';
      case 'bottom':
        return 'bottom span-right';
      case 'left':
        return 'span-bottom left';
      case 'right':
        return 'span-bottom right';
    }
  }

  switch (preferedDirection) {
    case 'top':
      return 'top center';
    case 'bottom':
      return 'bottom center';
    case 'left':
      return 'center left';
    case 'right':
      return 'center right';
  }
};

const getPositionTryFallbacks = (
  preferedDirection: TooltipProps['preferedDirection'],
  blockedDirections: Direction[]
) => {
  const oppositeDirection: Record<Direction, Direction> = {
    top: 'bottom',
    bottom: 'top',
    left: 'right',
    right: 'left',
  };

  if (blockedDirections.includes(oppositeDirection[preferedDirection])) {
    return undefined;
  }

  return preferedDirection === 'top' || preferedDirection === 'bottom'
    ? 'flip-block'
    : 'flip-inline';
};

const getSpacingStyle = (
  preferedDirection: TooltipProps['preferedDirection'],
  spacing: number
): CSSProperties => {
  switch (preferedDirection) {
    case 'top':
      return { marginBottom: spacing };
    case 'bottom':
      return { marginTop: spacing };
    case 'left':
      return { marginRight: spacing };
    case 'right':
      return { marginLeft: spacing };
  }
};

export function FloatingTooltip({
  childRef,
  preferedDirection,
  blockedDirections = [],
  mode,
  children,
  spacing,
}: {
  childRef: MutableRefObject<HTMLElement | null>;
  children: ReactNode;
} & Pick<
  TooltipProps,
  'mode' | 'preferedDirection' | 'blockedDirections' | 'spacing'
>) {
  const anchorName = `--tooltip-anchor-${useId().replace(/:/g, '')}`;
  const tooltipRef = useRef<PopoverElement | null>(null);
  const [isOpen, setIsOpen] = useState(false);

  useLayoutEffect(() => {
    if (childRef.current && childRef.current.children[0]) {
      const elem = childRef.current.children[0] as HTMLElement;
      elem.style.setProperty('anchor-name', anchorName);

      const open = () => setIsOpen(true);
      const close = () => setIsOpen(false);

      elem.addEventListener('mouseenter', open);
      elem.addEventListener('mouseleave', close);
      elem.addEventListener('focus', open);
      elem.addEventListener('blur', close);

      return () => {
        elem.style.removeProperty('anchor-name');
        elem.removeEventListener('mouseenter', open);
        elem.removeEventListener('mouseleave', close);
        elem.removeEventListener('focus', open);
        elem.removeEventListener('blur', close);
      };
    }
  }, [anchorName, childRef]);

  useEffect(() => {
    const close = () => setIsOpen(false);
    window.addEventListener('resize', close);
    window.addEventListener('scroll', close, true);
    return () => {
      window.removeEventListener('resize', close);
      window.removeEventListener('scroll', close, true);
    };
  }, []);

  useEffect(() => {
    const el = tooltipRef.current;
    if (!el?.showPopover) return;

    try {
      const isOpenNow = el.matches(':popover-open');

      if (isOpen && !isOpenNow) {
        el.showPopover();
      } else if (!isOpen && isOpenNow) {
        el.hidePopover?.();
      }
    } catch {
      setIsOpen(false);
    }
  }, [isOpen]);

  return (
    <div
      className="fixed inset-auto m-0 border-0 bg-transparent p-0 outline-none backdrop:bg-transparent pointer-events-none"
      ref={tooltipRef}
      {...({ popover: 'manual' } as any)}
      style={
        {
          positionAnchor: anchorName,
          positionArea: getPositionArea(preferedDirection, mode),
          positionTryFallbacks: getPositionTryFallbacks(
            preferedDirection,
            blockedDirections
          ),
          width: 'max-content',
          maxWidth: 'calc(100vw - 20px)',
          ...getSpacingStyle(preferedDirection, spacing ?? 20),
        } as CSSProperties
      }
    >
      <div className="bg-background-90 rounded-md p-2 text-background-10 overflow-auto">
        {children}
      </div>
    </div>
  );
}

const TOOLTIP_DELAY = 500;

interface DrawerStyle {
  bottom: number;
}

export function DrawerTooltip({
  children,
  childRef,
}: {
  children: ReactNode;
  childRef: MutableRefObject<HTMLElement | null>;
}) {
  const touchTimestamp = useRef<number>(0);
  const touchTimeout = useRef<number>(0);
  const drawerRef = useRef<HTMLDivElement | null>(null);
  const [drawerStyle, setDrawerStyle] = useState<DrawerStyle | undefined>(
    undefined
  );

  const touchStart = () => {
    if (childRef.current && childRef.current.children[0]) {
      touchTimestamp.current = Date.now();
      const elem = childRef.current.children[0] as HTMLElement;
      elem.classList.add(classNames('transition-all'));
      elem.classList.add(classNames('animate-pulse'));
      elem.classList.add(classNames('scale-[110%]'));
      elem.classList.add(classNames('duration-500'));
      if (elem.hasAttribute('disabled')) {
        open();
      } else {
        touchTimeout.current = setTimeout(() => {
          open();
        }, TOOLTIP_DELAY) as unknown as number;
      }
    }
  };

  const clearEffect = () => {
    if (childRef.current && childRef.current.children[0]) {
      const elem = childRef.current.children[0] as HTMLElement;
      elem.classList.remove(classNames('animate-pulse'));
      elem.classList.remove(classNames('scale-[110%]'));
      elem.classList.remove(classNames('duration-500'));
    }
  };

  const touchEnd = (e: MouseEvent | TouchEvent) => {
    if (
      e.currentTarget instanceof HTMLButtonElement &&
      e.currentTarget.hasAttribute('disabled')
    ) {
      e.preventDefault();
      return;
    }
    if (Date.now() - touchTimestamp.current < TOOLTIP_DELAY) {
      clearTimeout(touchTimeout.current);
      close();
    }
  };

  const scroll = () => {
    close();
  };

  const open = () => {
    if (drawerStyle) return;
    clearEffect();

    if (!drawerRef.current) throw new Error('invalid state');

    setDrawerStyle({ bottom: 0 });
  };

  const close = () => {
    setDrawerStyle(undefined);
  };

  useLayoutEffect(() => {
    if (childRef.current && childRef.current.children[0]) {
      const elem = childRef.current.children[0] as HTMLElement;

      elem.addEventListener('scroll', scroll);

      elem.addEventListener('touchstart', touchStart);
      elem.addEventListener('touchend', touchEnd);
      elem.addEventListener('touchcancel', touchEnd);

      return () => {
        elem.removeEventListener('scroll', scroll);

        elem.removeEventListener('touchstart', touchStart);
        elem.removeEventListener('touchend', touchEnd);
        elem.removeEventListener('touchcancel', touchEnd);
        clearTimeout(touchTimeout.current);
      };
    }
  }, []);
  // FIXME: Completely broken not sure why. Will be solved when tooltips on mobile actually work

  return (
    <>
      <div
        className="fixed top-[44px] rounded-t-lg h-screen z-50 w-full bg-background-90 opacity-50"
        onClick={() => close()}
        style={{
          opacity: drawerStyle ? 0.5 : 0,
          pointerEvents: drawerStyle ? 'all' : 'none',
        }}
      />
      <div
        className={classNames(
          'fixed z-50 w-full text-background-10 max-h-full -bottom-full transition-all overflow-clip'
        )}
        style={drawerStyle}
      >
        <div
          className="bg-background-60 rounded-t-lg border-background-40 border-t-2"
          ref={drawerRef}
        >
          <div className="h-12 rounded-t-lg relative flex justify-center items-center">
            <Typography variant="section-title" textAlign="text-center">
              Pro tip
            </Typography>
            <button
              className="absolute right-4 top-3 h-6 w-6 bg-background-70 rounded-full flex justify-center items-center"
              onClick={() => close()}
            >
              <CloseIcon size={20} className="stroke-white" />
            </button>
          </div>
          <div
            className="p-4 overflow-y-auto"
            style={{ maxHeight: 'calc(100vh - 49px - 44px)' }}
          >
            {children}
          </div>
        </div>
      </div>
    </>
  );
}

export function Tooltip({
  content,
  children,
  preferedDirection,
  blockedDirections = [],
  mode = 'center',
  variant = 'auto',
  disabled = false,
  tag = 'div',
  bindTo,
  spacing = 10,
}: TooltipProps) {
  const childRef = useRef<HTMLElement | null>(null);
  const isAndroid = window.__ANDROID__?.isThere();

  if (bindTo) {
    childRef.current = document.querySelector(bindTo);
  }

  let portal = null;
  if (variant === 'auto') {
    portal = isAndroid ? (
      <DrawerTooltip childRef={childRef}>{content}</DrawerTooltip>
    ) : (
      <FloatingTooltip
        preferedDirection={preferedDirection}
        blockedDirections={blockedDirections}
        mode={mode}
        childRef={childRef}
        spacing={spacing}
      >
        {content}
      </FloatingTooltip>
    );
  }

  if (variant === 'drawer')
    portal = <DrawerTooltip childRef={childRef}>{content}</DrawerTooltip>;

  if (variant === 'floating')
    portal = (
      <FloatingTooltip
        blockedDirections={blockedDirections}
        preferedDirection={preferedDirection}
        mode={mode}
        childRef={childRef}
        spacing={spacing}
      >
        {content}
      </FloatingTooltip>
    );

  return (
    <>
      {bindTo
        ? children
        : createElement(
            tag,
            { className: 'contents', ref: childRef },
            children
          )}
      {!disabled && createPortal(portal, document.body)}
    </>
  );
}
