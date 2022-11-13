import classNames from 'classnames';
import { useMemo } from 'react';
import { Typography } from '../commons/Typography';
import { TrackerStatus as TrackerStatusEnum } from 'solarxr-protocol';

const statusLabelMap: { [key: number]: string } = {
  [TrackerStatusEnum.NONE]: 'No Status',
  [TrackerStatusEnum.BUSY]: 'Busy',
  [TrackerStatusEnum.ERROR]: 'Error',
  [TrackerStatusEnum.DISCONNECTED]: 'Disconnected',
  [TrackerStatusEnum.OCCLUDED]: 'Occluded',
  [TrackerStatusEnum.OK]: 'Connected',
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
  const statusClass = useMemo(() => statusClassMap[status], [status]);
  const statusLabel = useMemo(() => statusLabelMap[status], [status]);

  return (
    <div className="flex text-default gap-2">
      <div className="flex flex-col justify-center">
        <div className={classNames('w-2 h-2 rounded-full', statusClass)}></div>
      </div>
      <Typography color="secondary">{statusLabel}</Typography>
    </div>
  );
}
