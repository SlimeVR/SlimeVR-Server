import {
  DeviceDataT,
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
} from 'solarxr-protocol';
import { Typography } from './Typography';
import classNames from 'classnames';
import { DownloadIcon } from './icon/DownloadIcon';
import { Link } from 'react-router-dom';
import { useAppContext } from '@/hooks/app';
import { Tooltip } from './Tooltip';
import { Localized } from '@fluent/react';
import { checkForUpdate } from '@/hooks/firmware-update';

function UpdateIcon({
  showUpdate,
}: {
  showUpdate:
    | 'can-update'
    | 'low-battery'
    | 'updated'
    | 'unavailable'
    | 'blocked';
}) {
  const content = (
    <div className="relative">
      <div
        className={classNames(
          'absolute rounded-full h-6 w-6 left-1 top-1 bg-accent-background-10 animate-[ping_2s_linear_3]',
          showUpdate !== 'can-update' && 'hidden'
        )}
      />
      <div
        className={classNames(
          'absolute rounded-full h-8 w-8 justify-center flex items-center',
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
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
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
        showUpdate !== 'updated' &&
        showUpdate !== 'blocked' && <UpdateIcon showUpdate={showUpdate} />}
    </div>
  );
}
