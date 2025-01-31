import { MouseEventHandler } from 'react';
import {
  DeviceDataT,
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
} from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { TrackerBattery } from './TrackerBattery';
import { TrackerWifi } from './TrackerWifi';
import { TrackerStatus } from './TrackerStatus';
import classNames from 'classnames';
import { useTracker } from '@/hooks/tracker';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { DownloadIcon } from '@/components/commons/icon/DownloadIcon';
import { Link } from 'react-router-dom';
import { useAppContext } from '@/hooks/app';
import { checkForUpdate } from '@/components/firmware-update/FirmwareUpdate';

function TrackerBig({
  device,
  tracker,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
}) {
  const { useName } = useTracker(tracker);

  const trackerName = useName();

  return (
    <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border my-8 px-6 h-32">
      <div className="flex justify-center fill-background-10">
        <BodyPartIcon bodyPart={tracker.info?.bodyPart}></BodyPartIcon>
      </div>
      <div className="flex justify-center">
        <Typography bold truncate>
          {trackerName}
        </Typography>
      </div>
      <div className="flex justify-center">
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
      <div className="flex text-default justify-center gap-5 flex-wrap">
        {device && device.hardwareStatus && (
          <>
            {device.hardwareStatus.batteryPctEstimate && (
              <TrackerBattery
                voltage={device.hardwareStatus.batteryVoltage}
                value={device.hardwareStatus.batteryPctEstimate / 100}
                disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
              />
            )}
            <div className="flex gap-2">
              {(device.hardwareStatus.rssi != null ||
                device.hardwareStatus.ping != null) && (
                <TrackerWifi
                  rssi={device.hardwareStatus.rssi || 0}
                  ping={device.hardwareStatus.ping || 0}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                ></TrackerWifi>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

function TrackerSmol({
  device,
  tracker,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
}) {
  const { useName } = useTracker(tracker);

  const trackerName = useName();

  return (
    <div className="flex rounded-md py-3 px-4 w-full gap-4 h-16">
      <div className="flex flex-col justify-center items-center fill-background-10">
        <BodyPartIcon bodyPart={tracker.info?.bodyPart}></BodyPartIcon>
      </div>
      <div className="flex flex-col flex-grow justify-center">
        <Typography bold truncate>
          {trackerName}
        </Typography>
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
      {device && device.hardwareStatus && (
        <>
          <div className="flex flex-col justify-center items-center">
            {device.hardwareStatus.batteryPctEstimate && (
              <TrackerBattery
                voltage={device.hardwareStatus.batteryVoltage}
                value={device.hardwareStatus.batteryPctEstimate / 100}
                disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
              />
            )}
          </div>
          <div className="flex flex-col justify-center items-center">
            {(device.hardwareStatus.rssi != null ||
              device.hardwareStatus.ping != null) && (
              <TrackerWifi
                rssi={device.hardwareStatus.rssi || 0}
                ping={device.hardwareStatus.ping || 0}
                disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
              ></TrackerWifi>
            )}
          </div>
        </>
      )}
    </div>
  );
}

export function TrackerCard({
  tracker,
  device,
  smol = false,
  interactable = false,
  outlined = false,
  onClick,
  bg = 'bg-background-60',
  shakeHighlight = true,
  warning = false,
  showUpdates = false,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  smol?: boolean;
  interactable?: boolean;
  outlined?: boolean;
  bg?: string;
  shakeHighlight?: boolean;
  onClick?: MouseEventHandler<HTMLDivElement>;
  warning?: boolean;
  showUpdates?: boolean;
}) {
  const { currentFirmwareRelease } = useAppContext();
  const { useVelocity } = useTracker(tracker);
  const velocity = useVelocity();

  return (
    <div className="relative">
      <div
        onClick={onClick}
        className={classNames(
          'rounded-lg overflow-hidden',
          interactable && 'hover:bg-background-50 cursor-pointer',
          outlined && 'outline outline-2 outline-accent-background-40',
          warning && 'border-status-warning border-solid border-2',
          bg
        )}
        style={
          shakeHighlight
            ? {
                boxShadow: `0px 0px ${Math.floor(velocity * 8)}px ${Math.floor(
                  velocity * 8
                )}px rgb(var(--accent-background-30))`,
              }
            : {}
        }
      >
        {smol && <TrackerSmol tracker={tracker} device={device}></TrackerSmol>}
        {!smol && <TrackerBig tracker={tracker} device={device}></TrackerBig>}
      </div>
      {showUpdates &&
        tracker.status !== TrackerStatusEnum.DISCONNECTED &&
        currentFirmwareRelease &&
        device?.hardwareInfo &&
        checkForUpdate(currentFirmwareRelease, device.hardwareInfo) && (
          <Link to="/firmware-update" className="absolute right-5 -top-2.5">
            <div className="relative">
              <div className="absolute rounded-full h-6 w-6 left-1 top-1 bg-accent-background-10 animate-[ping_2s_linear_infinite]"></div>
              <div className="absolute rounded-full h-8 w-8 hover:bg-background-40 hover:cursor-pointer bg-background-50 justify-center flex items-center">
                <DownloadIcon width={15}></DownloadIcon>
              </div>
            </div>
          </Link>
        )}
    </div>
  );
}
