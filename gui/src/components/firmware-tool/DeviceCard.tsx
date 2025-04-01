import { CHECKBOX_CLASSES } from '@/components/commons/Checkbox';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { Typography } from '@/components/commons/Typography';
import { firmwareUpdateErrorStatus } from '@/hooks/firmware-tool';
import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { Control, Controller } from 'react-hook-form';
import {
  FirmwareUpdateStatus,
  TrackerStatus as TrackerStatusEnum,
} from 'solarxr-protocol';
import { TrackerStatus } from '@/components/tracker/TrackerStatus';
import { useMemo } from 'react';

interface DeviceCardProps {
  deviceNames: string[];
  status?: FirmwareUpdateStatus;
  online?: boolean | null;
}

interface DeviceCardControlProps {
  control?: Control<any>;
  name?: string;
  progress?: number;
  disabled?: boolean;
}

export function DeviceCardContent({ deviceNames, status }: DeviceCardProps) {
  const { l10n } = useLocalization();

  return (
    <div className="p-2 flex h-full gap-2 justify-between flex-col">
      <div className="flex flex-row flex-wrap gap-2 items-center h-full">
        {deviceNames.map((name) => (
          <span
            key={name}
            className="p-1 px-3 rounded-l-full rounded-r-full bg-background-40"
          >
            <Typography>{name}</Typography>
          </span>
        ))}
      </div>
      {status !== undefined ? (
        <Typography>
          {l10n.getString(
            'firmware_update-status-' + FirmwareUpdateStatus[status]
          )}
        </Typography>
      ) : (
        <Typography> </Typography> // placeholder so the size of the component does not change if there is no status
      )}
    </div>
  );
}

export function DeviceCardControl({
  control,
  name,
  progress,
  disabled = false,
  online = null,
  ...props
}: DeviceCardControlProps & DeviceCardProps) {
  const cardborder = useMemo(() => {
    if (!props.status) return 'border-transparent';

    if (props.status === FirmwareUpdateStatus.DONE)
      return 'border-status-success';

    if (props.status === FirmwareUpdateStatus.NEED_MANUAL_REBOOT)
      return 'border-status-special';

    if (firmwareUpdateErrorStatus.includes(props.status))
      return 'border-status-critical';

    return 'border-transparent';
  }, [props.status]);

  return (
    <div
      className={classNames(
        'rounded-md bg-background-60 h-[86px] pt-2 flex flex-col justify-between border-2 relative',
        cardborder
      )}
    >
      {control && name ? (
        <Controller
          control={control}
          name={name}
          render={({ field: { onChange, value, ref } }) => (
            <label className="flex flex-row gap-2 px-4 h-full">
              <div className="flex justify-center flex-col">
                <input
                  ref={ref}
                  onChange={onChange}
                  className={CHECKBOX_CLASSES}
                  checked={value || false}
                  type="checkbox"
                  disabled={disabled}
                ></input>
              </div>

              <div className="w-full">
                <DeviceCardContent {...props}></DeviceCardContent>
              </div>
            </label>
          )}
        ></Controller>
      ) : (
        <div className="px-2 h-full">
          <DeviceCardContent {...props}></DeviceCardContent>
        </div>
      )}
      <div
        className={classNames(
          'align-bottom',
          props.status != FirmwareUpdateStatus.UPLOADING ||
            progress === undefined
            ? 'opacity-0'
            : 'opacity-100'
        )}
      >
        <ProgressBar
          progress={progress || 0}
          bottom
          height={6}
          colorClass="bg-accent-background-20"
        ></ProgressBar>
      </div>
      {online !== null && (
        <div className="absolute top-2 right-2">
          <TrackerStatus
            status={
              online ? TrackerStatusEnum.OK : TrackerStatusEnum.DISCONNECTED
            }
          ></TrackerStatus>
        </div>
      )}
    </div>
  );
}
