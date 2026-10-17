import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from './Typography';

export function FloatingLabel({
  label,
  floating,
  className,
}: {
  label: ReactNode;
  floating: boolean;
  className?: string;
}) {
  return (
    <Typography
      variant="standard"
      bold={floating}
      italic={!floating}
      color={classNames(
        'text-background-10 pointer-events-none absolute left-3 z-20 leading-none transition-all duration-150 ease-out',
        floating ? `top-[0.5rem] origin-left` : 'top-1/2 -translate-y-1/2',
        className
      )}
    >
      {label}
    </Typography>
  );
}

export function FieldCaption({ children }: { children: ReactNode }) {
  return (
    <Typography
      variant="standard"
      bold
      color={classNames('text-background-10 leading-none origin-left')}
    >
      {children}
    </Typography>
  );
}

// Vertical padding a control needs so a floating label clears its value.
export const FLOATING_LABEL_PADDING = 'pt-[1.15rem] pb-[0.15rem]';
