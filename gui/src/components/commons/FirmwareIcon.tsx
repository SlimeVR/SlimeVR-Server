import {
  DeviceDataT,
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
} from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import classNames from 'classnames';
import { DownloadIcon } from '@/components/commons/icon/DownloadIcon';
import { Link } from 'react-router-dom';
import { useAppContext } from '@/hooks/app';
import { Tooltip } from '@/components/commons/Tooltip';
import { Localized } from '@fluent/react';
import { checkForUpdate } from '@/hooks/firmware-update';

function UpdateIcon({
  showUpdate,
  size,
}: {
  showUpdate:
    | 'can-update'
    | 'low-battery'
    | 'updated'
    | 'unavailable'
    | 'blocked';
  size: number;
}) {
  const content = (
    <div className="relative">
      <div
        className={classNames(
          `absolute rounded-full h-${size} w-${size} left-0.5 top-0.5 bg-accent-background-10 animate-[ping_2s_linear_3]`,
          showUpdate !== 'can-update' && 'hidden'
        )}
      />
      <div
        className={classNames(
          `absolute rounded-full h-${size + 1} w-${size + 1} justify-center flex items-center`,
          showUpdate === 'low-battery'
            ? 'cursor-not-allowed bg-background-80 outline-2 outline-status-critical outline'
            : 'hover:bg-background-40 hover:cursor-pointer bg-background-50'
        )}
      >
        <DownloadIcon width={15} />
      </div>
    </div>
  );

  return showUpdate !== 'can-update' ? (
    <Tooltip
      preferedDirection="top"
      content={
        <Localized id={'tracker-settings-update-low-battery'}>
          <Typography />
        </Localized>
      }
    >
      <div className="absolute right-5 -top-2.5">{content}</div>
    </Tooltip>
  ) : (
    <Link to="/firmware-update" className="absolute right-5 -top-2.5">
      {content}
    </Link>
  );
}

export function FirmwareIcon({
  tracker,
  device,
  size,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  size: number;
}) {
  const { currentFirmwareRelease } = useAppContext();

  const showUpdate =
    tracker.status !== TrackerStatusEnum.DISCONNECTED &&
    currentFirmwareRelease &&
    device &&
    checkForUpdate(currentFirmwareRelease, device);

  return (
    <div>
      {showUpdate &&
        showUpdate !== 'unavailable' &&
        showUpdate !== 'updated' && (
          <UpdateIcon showUpdate={'can-update'} size={size} />
        )}
    </div>
  );
}
