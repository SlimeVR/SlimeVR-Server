import classNames from 'classnames';
import { Clickable } from './Clickable';
import { ReactNode } from 'react';
import { Typography } from './Typography';

export function TogglePill({
  compact,
  onClick,
  pressed,
  label,
  value,
  children,
}: {
  compact?: boolean;
  onClick?: () => void;
  pressed?: boolean;
  label?: string;
  value?: string;
  children: ReactNode;
}) {
  const className = classNames(
    'flex items-center gap-1 bg-background-80 rounded-full w-fit',
    onClick && 'cursor-pointer',
    compact ? 'p-0.5' : 'p-1'
  );

  if (!onClick) return <div className={className}>{children}</div>;

  const name = label && value ? `${label}: ${value}` : label;

  return (
    <Clickable
      onClick={onClick}
      pressed={pressed}
      label={name}
      className={className}
    >
      {children}
    </Clickable>
  );
}

export function TogglePillOption({
  compact,
  dotClass,
  labelId,
  active,
  onClick,
}: {
  compact?: boolean;
  dotClass: string;
  labelId: string;
  active?: boolean;
  onClick?: () => void;
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

  return (
    <Clickable onClick={onClick} pressed={active} className={className}>
      {content}
    </Clickable>
  );
}
