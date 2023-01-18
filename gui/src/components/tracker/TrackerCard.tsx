import { MouseEventHandler } from 'react';
import {
  DeviceDataT,
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
} from 'solarxr-protocol';
import { Typography } from '../commons/Typography';
import { TrackerBattery } from './TrackerBattery';
import { TrackerWifi } from './TrackerWifi';
import { TrackerStatus } from './TrackerStatus';
import { FootIcon } from '../commons/icon/FootIcon';
import classNames from 'classnames';
import { useTracker } from '../../hooks/tracker';

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
        <FootIcon></FootIcon>
      </div>
      <div className="flex justify-center">
        <Typography bold>{trackerName}</Typography>
      </div>
      <div className="flex justify-center">
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
      <div className="flex text-default justify-center gap-5 flex-wrap">
        {device && device.hardwareStatus && (
          <>
            {device.hardwareStatus.batteryPctEstimate && (
              <TrackerBattery
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
    <div className="flex rounded-md py-3 px-5 w-full gap-4 h-16">
      <div className="flex flex-col justify-center items-center fill-background-10">
        <FootIcon></FootIcon>
      </div>
      <div className="flex flex-col flex-grow">
        <Typography bold>{trackerName}</Typography>
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
      {device && device.hardwareStatus && (
        <>
          <div className="flex flex-col justify-center items-center">
            {device.hardwareStatus.batteryPctEstimate && (
              <TrackerBattery
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
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  smol?: boolean;
  interactable?: boolean;
  outlined?: boolean;
  bg?: string;
  shakeHighlight?: boolean;
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  const { useVelocity } = useTracker(tracker);

  const velocity = useVelocity();

  return (
    <div
      onClick={onClick}
      className={classNames(
        'rounded-lg',
        interactable && 'hover:bg-background-50 cursor-pointer',
        outlined && 'outline outline-2 outline-accent-background-40',
        bg
      )}
      style={
        shakeHighlight
          ? {
              boxShadow: `0px 0px ${velocity * 8}px ${velocity * 8}px #183951`,
            }
          : {}
      }
    >
      {smol && <TrackerSmol tracker={tracker} device={device}></TrackerSmol>}
      {!smol && <TrackerBig tracker={tracker} device={device}></TrackerBig>}
    </div>
  );
}
