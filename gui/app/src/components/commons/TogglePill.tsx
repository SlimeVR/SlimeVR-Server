import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from './Typography';

export function TogglePill({
  compact,
  onClick,
  children,
}: {
  compact?: boolean;
  onClick?: () => void;
  children: ReactNode;
}) {
  return (
    <div
      onClick={onClick}
      className={classNames(
        'flex items-center gap-1 bg-background-80 rounded-full w-fit',
        onClick && 'cursor-pointer',
        compact ? 'p-0.5' : 'p-1'
      )}
    >
      {children}
    </div>
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
  return (
    <div
      onClick={onClick}
      className={classNames(
        'flex items-center rounded-full',
        compact ? 'gap-1.5 px-2 py-0.5' : 'gap-2 px-3 py-1',
        onClick && 'cursor-pointer',
        active === true && 'bg-background-60',
        active === false && 'opacity-50'
      )}
    >
      <span
        className={classNames(
          'w-2.5 h-2.5 rounded-full bg-background-10 outline outline-4',
          dotClass
        )}
      />
      <Typography bold id={labelId} />
    </div>
  );
}
