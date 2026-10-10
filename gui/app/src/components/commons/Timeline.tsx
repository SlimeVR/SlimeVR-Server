import classNames from 'classnames';
import { ReactNode, Ref } from 'react';
import { Clickable } from './Clickable';
import { Typography } from './Typography';
import { CheckIcon } from './icon/CheckIcon';
import { ArrowDownIcon } from './icon/ArrowIcons';

export type TimelineState = 'done' | 'skipped' | 'current' | 'todo';

const BADGE_SIZE = { md: 'h-8 w-8', sm: 'h-[25px] w-[25px]' };

function TimelineBadge({
  state,
  number,
  size,
  dotClassName,
}: {
  state: TimelineState;
  number?: number;
  size: keyof typeof BADGE_SIZE;
  dotClassName?: string;
}) {
  const numbered = number !== undefined;
  return (
    <div
      className={classNames(
        'flex shrink-0 items-center justify-center rounded-full fill-background-10',
        BADGE_SIZE[size],
        state === 'done' && 'bg-accent-background-20',
        state === 'skipped' && 'bg-background-50 fill-background-30',
        state === 'current' &&
          (numbered ? 'bg-accent-background-40' : 'bg-background-50'),
        state === 'todo' &&
          (numbered ? 'border-2 border-background-50' : 'bg-background-50')
      )}
    >
      {state === 'done' || state === 'skipped' ? (
        <CheckIcon size={size === 'md' ? 12 : 10} />
      ) : numbered ? (
        <Typography
          variant="section-title"
          bold
          color={state === 'todo' ? 'secondary' : 'primary'}
        >
          {number}
        </Typography>
      ) : (
        <div
          className={classNames(
            'h-[12px] w-[12px] rounded-full',
            dotClassName ??
              (state === 'current'
                ? 'bg-accent-background-10 animate-pulse animate-low-priority brightness-75'
                : 'bg-background-40')
          )}
        />
      )}
    </div>
  );
}

export function Timeline({ children }: { children: ReactNode }) {
  return <ol className="flex w-full flex-col">{children}</ol>;
}

export function TimelineItem({
  state,
  number,
  title,
  size = 'md',
  last = false,
  dotClassName,
  expanded,
  onToggle,
  disabled,
  className,
  itemRef,
  children,
}: {
  state: TimelineState;
  number?: number;
  title: ReactNode;
  size?: keyof typeof BADGE_SIZE;
  last?: boolean;
  dotClassName?: string;
  expanded?: boolean;
  onToggle?: () => void;
  disabled?: boolean;
  className?: string;
  itemRef?: Ref<HTMLLIElement>;
  children?: ReactNode;
}) {
  const titleClasses = classNames(
    'flex w-full items-center justify-between gap-2 text-left text-section-title',
    // Only numbered steps dim the ones still to come
    state === 'todo' && number !== undefined && 'text-background-30'
  );

  return (
    <li
      ref={itemRef}
      aria-current={state === 'current' ? 'step' : undefined}
      className={classNames('flex gap-3', className)}
    >
      <div className="flex flex-col items-center">
        <TimelineBadge
          state={state}
          number={number}
          size={size}
          dotClassName={dotClassName}
        />
        {!last && (
          <div
            className={classNames(
              'w-0.5 flex-1 rounded-full',
              size === 'md' ? 'my-1' : 'my-0.5',
              state === 'done' ? 'bg-accent-background-20' : 'bg-background-50'
            )}
          />
        )}
      </div>
      <div
        className={classNames(
          'flex min-w-0 flex-1 flex-col',
          size === 'md' ? 'pt-1' : 'pt-px',
          !last && (size === 'md' ? 'pb-6' : 'pb-3')
        )}
      >
        {onToggle ? (
          <Clickable
            disabled={disabled}
            expanded={disabled ? undefined : expanded}
            className={classNames(
              titleClasses,
              !disabled && 'group cursor-pointer hover:text-background-20'
            )}
            onClick={onToggle}
          >
            {title}
            {!disabled && (
              <div className="fill-background-30 group-hover:scale-125 group-hover:fill-background-20 transition-transform">
                <ArrowDownIcon size={20} />
              </div>
            )}
          </Clickable>
        ) : (
          <div className={titleClasses}>{title}</div>
        )}
        {children}
      </div>
    </li>
  );
}
