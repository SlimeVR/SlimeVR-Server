import classNames from 'classnames';
import { useMemo } from 'react';
import { TrackerStatus as TrackerStatusEnum } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';

const statusLabelMap: { [key: number]: string } = {
  [TrackerStatusEnum.NONE]: 'tracker-status-none',
  [TrackerStatusEnum.BUSY]: 'tracker-status-busy',
  [TrackerStatusEnum.ERROR]: 'tracker-status-error',
  [TrackerStatusEnum.DISCONNECTED]: 'tracker-status-disconnected',
  [TrackerStatusEnum.OCCLUDED]: 'tracker-status-occluded',
  [TrackerStatusEnum.OK]: 'tracker-status-ok',
};

const statusClassMap: { [key: number]: string } = {
  [TrackerStatusEnum.NONE]: 'bg-background-30',
  [TrackerStatusEnum.BUSY]: 'bg-status-warning',
  [TrackerStatusEnum.ERROR]: 'bg-status-critical',
  [TrackerStatusEnum.DISCONNECTED]: 'bg-background-30',
  [TrackerStatusEnum.OCCLUDED]: 'bg-status-warning',
  [TrackerStatusEnum.OK]: 'bg-status-success',
};

export function TrackerStatus({ status }: { status: number }) {
  const { l10n } = useLocalization();

  const statusClass = useMemo(() => statusClassMap[status], [status]);
  const statusLabel = useMemo(() => statusLabelMap[status], [status]);

  return (
    <div className="flex text-default gap-2">
      <div className="flex flex-col justify-center">
        <div className={classNames('w-2 h-2 rounded-full', statusClass)}></div>
      </div>
      <Typography color="secondary" whitespace="whitespace-nowrap">
        {l10n.getString(statusLabel)}
      </Typography>
    </div>
  );
}
