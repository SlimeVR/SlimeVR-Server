import classNames from 'classnames';
import { Clickable } from './Clickable';
import { KeyboardEvent, ReactNode } from 'react';
import { Typography } from './Typography';

export function TogglePill({
  compact,
  onClick,
  pressed,
  label,
  radiogroupLabel,
  children,
}: {
  compact?: boolean;
  onClick?: () => void;
  pressed?: boolean;
  label?: string;
  radiogroupLabel?: string;
  children: ReactNode;
}) {
  const className = classNames(
    'flex items-center gap-1 bg-background-80 rounded-full w-fit',
    onClick && 'cursor-pointer',
    compact ? 'p-0.5' : 'p-1'
  );

  if (radiogroupLabel !== undefined) {
    return (
      <div
        role="radiogroup"
        aria-label={radiogroupLabel}
        className={className}
        onKeyDown={handleRadioKeys}
      >
        {children}
      </div>
    );
  }

  if (!onClick) return <div className={className}>{children}</div>;

  return (
    <Clickable
      onClick={onClick}
      pressed={pressed}
      label={label}
      className={className}
    >
      {children}
    </Clickable>
  );
}

function handleRadioKeys(e: KeyboardEvent<HTMLDivElement>) {
  const step: Record<string, number> = {
    ArrowRight: 1,
    ArrowDown: 1,
    ArrowLeft: -1,
    ArrowUp: -1,
  };
  if (!(e.key in step) && e.key !== 'Home' && e.key !== 'End') return;

  const radios = Array.from(
    e.currentTarget.querySelectorAll<HTMLElement>(
      '[role="radio"]:not([disabled])'
    )
  );
  const current = radios.indexOf(document.activeElement as HTMLElement);
  if (current < 0) return;

  e.preventDefault();
  const last = radios.length - 1;
  const next =
    e.key === 'Home'
      ? 0
      : e.key === 'End'
        ? last
        : (current + step[e.key] + radios.length) % radios.length;

  radios[next].focus();
  radios[next].click();
}

export function TogglePillOption({
  compact,
  dotClass,
  labelId,
  active,
  onClick,
  radio,
}: {
  compact?: boolean;
  dotClass: string;
  labelId: string;
  active?: boolean;
  onClick?: () => void;
  radio?: boolean;
}) {
  const className = classNames(
    'flex items-center rounded-full',
    compact ? 'gap-1.5 px-2 py-0.5' : 'gap-2 px-3 py-1',
    onClick && 'cursor-pointer',
    active === true && 'bg-background-60',
    active === false && 'opacity-50'
  );

  const content = (
    <>
      <span
        className={classNames(
          'w-2.5 h-2.5 rounded-full bg-background-10 outline outline-4',
          dotClass
        )}
      />
      <Typography bold id={labelId} />
    </>
  );

  if (!onClick) return <div className={className}>{content}</div>;

  if (radio) {
    return (
      <Clickable
        onClick={onClick}
        role="radio"
        aria-checked={active}
        tabIndex={active ? 0 : -1}
        className={className}
      >
        {content}
      </Clickable>
    );
  }

  return (
    <Clickable onClick={onClick} pressed={active} className={className}>
      {content}
    </Clickable>
  );
}
