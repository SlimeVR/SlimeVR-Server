import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from './Typography';

export type KbdVariant = 'default' | 'active' | 'invalid' | 'empty';

const KBD_VARIANTS: Record<KbdVariant, string> = {
  default: 'bg-background-90 border-background-60',
  active: 'bg-background-90 border-accent-background-30 animate-pulse',
  invalid: 'bg-status-critical/20 border-status-critical animate-shake',
  empty: 'bg-background-90 border-dashed border-background-60',
};

// A keycap-styled label
export function Kbd({
  children,
  id,
  variant = 'default',
  className,
}: {
  children?: ReactNode;
  id?: string;
  variant?: KbdVariant;
  className?: string;
}) {
  return (
    <kbd
      className={classNames(
        'inline-flex items-center justify-center rounded-md border transition-all',
        KBD_VARIANTS[variant],
        className
      )}
    >
      <Typography variant="standard" bold textAlign="text-center" id={id}>
        {children}
      </Typography>
    </kbd>
  );
}
