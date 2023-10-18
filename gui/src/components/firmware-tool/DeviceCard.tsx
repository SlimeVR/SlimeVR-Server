import { Control, Controller } from 'react-hook-form';
import { Typography } from '@/components/commons/Typography';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { CHECKBOX_CLASSES } from '@/components/commons/Checkbox';
import classNames from 'classnames';
import { FirmwareUpdateStatus } from 'solarxr-protocol';
import { useLocalization } from '@fluent/react';
import { firmwareUpdateErrorStatus } from '@/hooks/firmware-tool';

interface DeviceCardProps {
  deviceNames: string[];
  status?: FirmwareUpdateStatus;
}

interface DeviceCardControlProps {
  control?: Control<any>;
  name?: string;
  progress?: number;
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
      {status && (
        <Typography color="secondary">
          {l10n.getString(
            'firmware-update-status-' + FirmwareUpdateStatus[status]
          )}
        </Typography>
      )}
    </div>
  );
}

export function DeviceCardControl({
  control,
  name,
  progress = undefined,
  ...props
}: DeviceCardControlProps & DeviceCardProps) {
  return (
    <div
      className={classNames(
        'rounded-md bg-background-60 pt-2 flex flex-col justify-between border-2 ',
        props.status && firmwareUpdateErrorStatus.includes(props.status)
          ? 'border-status-critical'
          : 'border-transparent'
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
    </div>
  );
}
