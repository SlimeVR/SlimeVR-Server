import { TrackerStatus } from 'solarxr-protocol';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';

export const trackerStatusLabels = {
  [TrackerStatus.NONE]: 'tracker-status-none',
  [TrackerStatus.BUSY]: 'tracker-status-busy',
  [TrackerStatus.ERROR]: 'tracker-status-error',
  [TrackerStatus.DISCONNECTED]: 'tracker-status-disconnected',
  [TrackerStatus.OCCLUDED]: 'tracker-status-occluded',
  [TrackerStatus.OK]: 'tracker-status-ok',
  [TrackerStatus.TIMED_OUT]: 'tracker-status-timed_out',
  [TrackerStatus.SLEEPING]: 'tracker-status-sleeping',
} satisfies Record<TrackerStatus, MessageId>;

const DOT = {
  [TrackerStatus.NONE]: 'bg-fg-disabled',
  [TrackerStatus.BUSY]: 'bg-warning',
  [TrackerStatus.ERROR]: 'bg-critical',
  [TrackerStatus.DISCONNECTED]: 'bg-fg-disabled',
  [TrackerStatus.OCCLUDED]: 'bg-warning',
  [TrackerStatus.OK]: 'bg-success',
  [TrackerStatus.TIMED_OUT]: 'bg-warning',
  [TrackerStatus.SLEEPING]: 'bg-info',
} satisfies Record<TrackerStatus, string>;

export function TrackerStatusLabel(props: { status: TrackerStatus }) {
  return (
    <span class="flex items-center gap-sm whitespace-nowrap">
      <span class={cx('size-2 shrink-0 rounded-full', DOT[props.status])} />
      {t(trackerStatusLabels[props.status])}
    </span>
  );
}
