import classNames from 'classnames';
import { ReactNode } from 'react';
import { Typography } from './Typography';

export type StatusVariant =
  | 'neutral'
  | 'success'
  | 'special'
  | 'warning'
  | 'critical';

const VARIANT_CLASSES: Record<StatusVariant, string> = {
  neutral: 'bg-background-50',
  success: 'bg-status-success',
  special: 'bg-status-special',
  warning: 'bg-status-warning',
  critical: 'bg-status-critical',
};

export function StatusBadge({
  variant,
  id,
}: {
  variant: StatusVariant;
  id: string;
}) {
  return (
    <span className="rounded-md px-2 py-1 bg-background-70 flex gap-2 items-center">
      <div
        className={classNames('h-2 w-2 rounded-full', VARIANT_CLASSES[variant])}
      />
      <Typography id={id} bold whitespace="whitespace-nowrap" />
    </span>
  );
}

export function StatusRow({
  label,
  badge,
  children,
}: {
  label: ReactNode;
  badge: ReactNode;
  children?: ReactNode;
}) {
  return (
    <div className="flex flex-col gap-1 py-2">
      <div className="flex items-center justify-between gap-2 min-w-0">
        <div className="min-w-0">{label}</div>
        {badge}
      </div>
      {children}
    </div>
  );
}
