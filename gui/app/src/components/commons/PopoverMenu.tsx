import classNames from 'classnames';
import {
  CSSProperties,
  ReactNode,
  useEffect,
  useId,
  useRef,
  useState,
} from 'react';
import { useBreakpoint } from '@/hooks/breakpoint';
import { Clickable } from './Clickable';
import { Drawer } from './Drawer';
import './Dropdown.scss';

export interface PopoverTriggerProps {
  'aria-expanded': boolean;
  'aria-controls': string;
  popovertarget?: string;
  onClick?: () => void;
}

export interface PopoverMenuState {
  open: boolean;
  close: () => void;
}

/**
 * A button that opens a menu anchored below it. On phones it opens as a
 * bottom drawer instead when `drawerTitle` is set.
 */
export function PopoverMenu({
  trigger,
  children,
  drawerTitle,
  width = 'w-[260px]',
  anchorClassName,
}: {
  trigger: (props: PopoverTriggerProps, open: boolean) => ReactNode;
  children: ReactNode | ((state: PopoverMenuState) => ReactNode);
  drawerTitle?: ReactNode;
  width?: string;
  anchorClassName?: string;
}) {
  const { isMobile } = useBreakpoint('mobile');
  const useDrawer = isMobile && drawerTitle !== undefined;
  const [open, setOpen] = useState(false);
  const popoverRef = useRef<HTMLDivElement>(null);
  const id = `popover-menu-${useId().replace(/:/g, '')}`;
  const anchorName = `--${id}`;

  useEffect(() => {
    const el = popoverRef.current;
    if (!el) return;
    el.setAttribute('popover', 'auto');
    const onToggle = (e: Event) =>
      setOpen((e as ToggleEvent).newState === 'open');
    el.addEventListener('toggle', onToggle);
    return () => el.removeEventListener('toggle', onToggle);
  }, [useDrawer]);

  const close = () => {
    popoverRef.current?.hidePopover();
    setOpen(false);
  };

  const content =
    typeof children === 'function' ? children({ open, close }) : children;

  const triggerProps: PopoverTriggerProps = useDrawer
    ? {
        'aria-expanded': open,
        'aria-controls': id,
        onClick: () => setOpen(true),
      }
    : { 'aria-expanded': open, 'aria-controls': id, popovertarget: id };

  return (
    <>
      <div
        className={classNames('flex', anchorClassName)}
        style={{ anchorName } as CSSProperties}
      >
        {trigger(triggerProps, open)}
      </div>
      {useDrawer ? (
        <Drawer isOpen={open} onClose={close} title={drawerTitle}>
          <div id={id}>{content}</div>
        </Drawer>
      ) : (
        <div
          ref={popoverRef}
          id={id}
          className={classNames(
            'dropdown-popover flex-col fixed inset-auto m-0 mt-1 p-0 overflow-hidden rounded-lg border border-background-50 bg-background-60 shadow-lg',
            width
          )}
          style={
            {
              positionAnchor: anchorName,
              positionArea: 'bottom span-left',
              positionTryFallbacks: 'flip-inline',
            } as CSSProperties
          }
        >
          {content}
        </div>
      )}
    </>
  );
}

export function PopoverMenuItem({
  onClick,
  to,
  current,
  description,
  className,
  children,
}: {
  onClick?: () => void;
  to?: string;
  current?: boolean;
  description?: ReactNode;
  className?: string;
  children: ReactNode;
}) {
  const classes = classNames(
    'flex w-full px-4 py-2 mobile:py-3 hover:bg-background-50',
    description ? 'flex-col items-start' : 'items-center gap-2',
    current && 'bg-background-50/50',
    className
  );

  if (to !== undefined) {
    return (
      <Clickable to={to} onClick={onClick} className={classes}>
        {children}
        {description}
      </Clickable>
    );
  }

  return (
    <Clickable
      current={current || undefined}
      onClick={onClick}
      className={classes}
    >
      {children}
      {description}
    </Clickable>
  );
}
