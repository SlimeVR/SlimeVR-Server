import classNames from 'classnames';
import { ReactNode, useState } from 'react';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { PauseIcon } from '@/components/commons/icon/PauseIcon';
import { PlayIcon } from '@/components/commons/icon/PlayIcon';
import { TrashIcon } from '@/components/commons/icon/TrashIcon';

export type LogStatus = 'live' | 'paused' | 'file';

const STATUS_IDS: Record<LogStatus, string> = {
  live: 'settings-logs-status-live',
  paused: 'settings-logs-status-paused',
  file: 'settings-logs-status-file',
};

export function LogFooter({
  status,
  shown,
  total,
  children,
}: {
  status: LogStatus;
  shown: number;
  total: number;
  children: ReactNode;
}) {
  const live = status === 'live';
  return (
    <div className="border-t-2 pt-2 mt-1.5 border-background-60 flex flex-wrap gap-2 items-center">
      <span className="flex items-center gap-1.5">
        <span
          className={classNames(
            'w-2 h-2 rounded-full',
            live ? 'bg-status-success animate-pulse' : 'bg-status-warning'
          )}
        />
        <Typography
          tag="span"
          color={live ? 'text-status-success' : 'text-status-warning'}
          id={STATUS_IDS[status]}
        />
      </span>
      <span className="min-w-[11ch] tabular-nums">
        <Typography
          tag="span"
          color="text-background-20"
          whitespace="whitespace-nowrap"
          id={
            shown === total
              ? 'settings-logs-lines'
              : 'settings-logs-lines-filtered'
          }
          vars={{ shown, total }}
        />
      </span>
      <div className="flex-grow" />
      {children}
    </div>
  );
}

export function ClearLogButton({
  onClick,
  disabled,
  iconOnly,
}: {
  onClick: () => void;
  disabled?: boolean;
  iconOnly?: boolean;
}) {
  return (
    <Button
      variant="secondary"
      size="small"
      onClick={onClick}
      disabled={disabled}
      icon={
        <span className="flex fill-background-10">
          <TrashIcon size={14} />
        </span>
      }
      id="settings-logs-clear"
      iconOnly={iconOnly}
    />
  );
}

export function PauseLogButton({
  paused,
  onToggle,
  iconOnly,
}: {
  paused: boolean;
  onToggle: () => void;
  iconOnly?: boolean;
}) {
  return (
    <Button
      variant="secondary"
      size="small"
      onClick={onToggle}
      icon={
        <span className="flex fill-background-10">
          {paused ? <PlayIcon width={14} /> : <PauseIcon width={14} />}
        </span>
      }
      id={paused ? 'settings-logs-resume' : 'settings-logs-pause'}
      iconOnly={iconOnly}
    />
  );
}

export function usePausableLines<T>(live: T[]) {
  // Rows shown while paused; the live list keeps growing underneath
  const [frozen, setFrozen] = useState<T[] | null>(null);
  return {
    lines: frozen ?? live,
    paused: frozen !== null,
    togglePause: () => setFrozen((current) => (current ? null : live)),
    clearFrozen: () => setFrozen((current) => current && []),
    resume: () => setFrozen(null),
  };
}
