import classNames from 'classnames';
import { MouseEventHandler, ReactChild, useState } from 'react';
import {
  TrackerDataT,
  TrackerIdT,
  TrackerStatus as TrackerStatusEnum,
} from 'solarxr-protocol';
import { FlatDeviceTracker } from '../../hooks/app';
import { useTracker } from '../../hooks/tracker';
import { FootIcon } from '../commons/icon/FootIcon';
import { Typography } from '../commons/Typography';
import { TrackerBattery } from './TrackerBattery';
import { TrackerStatus } from './TrackerStatus';
import { TrackerWifi } from './TrackerWifi';
import { IPv4 } from 'ip-num/IPNumber';

export function TrackerNameCol({ tracker }: { tracker: TrackerDataT }) {
  const { useName } = useTracker(tracker);

  const name = useName();

  return (
    <div className="flex flex-row gap-2">
      <div className="flex flex-col justify-center items-center fill-background-10">
        <FootIcon></FootIcon>
      </div>
      <div className="flex flex-col flex-grow whitespace-nowrap">
        <Typography bold>{name}</Typography>
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
    </div>
  );
}

export function TrackerRotCol({ tracker }: { tracker: TrackerDataT }) {
  const { useRotation } = useTracker(tracker);

  const rot = useRotation();

  return (
    <Typography color="secondary">
      <span className="whitespace-nowrap">
        {`${rot.pitch.toFixed(0)} / ${rot.yaw.toFixed(0)} / ${rot.roll.toFixed(
          0
        )}`}
      </span>
    </Typography>
  );
}

export function RowContainer({
  children,
  rounded = 'none',
  hover,
  tracker,
  onClick,
  onMouseOver,
  onMouseOut,
}: {
  children: ReactChild;
  rounded?: 'left' | 'right' | 'none';
  hover: boolean;
  tracker: TrackerDataT;
  onClick?: MouseEventHandler<HTMLDivElement>;
  onMouseOver?: MouseEventHandler<HTMLDivElement>;
  onMouseOut?: MouseEventHandler<HTMLDivElement>;
}) {
  const { useVelocity } = useTracker(tracker);

  const velocity = useVelocity();

  return (
    <div
      className={classNames(
        'py-1',
        rounded === 'left' && 'pl-3',
        rounded === 'right' && 'pr-3',
        'overflow-hidden'
      )}
    >
      <div
        onClick={onClick}
        onMouseEnter={onMouseOver}
        onMouseLeave={onMouseOut}
        style={{
          boxShadow: `0px 0px ${velocity * 8}px ${velocity * 8}px #183951`,
        }}
        className={classNames(
          'min-h-[50px]  flex flex-col justify-center px-3',
          rounded === 'left' && 'rounded-l-lg',
          rounded === 'right' && 'rounded-r-lg',
          hover ? 'bg-background-50' : 'bg-background-60'
        )}
      >
        {children}
      </div>
    </div>
  );
}

export function TrackersTable({
  flatTrackers,
  clickedTracker,
}: {
  clickedTracker: (tracker: TrackerDataT) => void;
  flatTrackers: FlatDeviceTracker[];
}) {
  const [hoverTracker, setHoverTracker] = useState<TrackerIdT | null>(null);

  const trackerEqual = (id: TrackerIdT | null) =>
    id?.trackerNum == hoverTracker?.trackerNum &&
    (!id?.deviceId || id.deviceId.id == hoverTracker?.deviceId?.id);

  return (
    <div className="flex w-full overflow-x-auto py-2">
      <div className="flex flex-col gap-1">
        <div className="flex px-3">Tracker</div>
        {flatTrackers.map(({ tracker }, index) => (
          <RowContainer
            key={index}
            rounded="left"
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            <TrackerNameCol tracker={tracker}></TrackerNameCol>
          </RowContainer>
        ))}
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex px-3">Type</div>
        {flatTrackers.map(({ device, tracker }, index) => (
          <RowContainer
            key={index}
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            <Typography color="secondary">
              {device?.hardwareInfo?.manufacturer || '--'}
            </Typography>
          </RowContainer>
        ))}
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex px-3">Battery</div>
        {flatTrackers.map(({ device, tracker }, index) => (
          <RowContainer
            key={index}
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            {(device &&
              device.hardwareStatus &&
              device.hardwareStatus.batteryPctEstimate && (
                <TrackerBattery
                  value={device.hardwareStatus.batteryPctEstimate / 100}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                />
              )) || <></>}
          </RowContainer>
        ))}
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex px-3">Ping</div>
        {flatTrackers.map(({ device, tracker }, index) => (
          <RowContainer
            key={index}
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            {(device &&
              device.hardwareStatus &&
              device.hardwareStatus.rssi &&
              device.hardwareStatus.ping && (
                <TrackerWifi
                  rssi={device.hardwareStatus.rssi}
                  ping={device.hardwareStatus.ping}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                ></TrackerWifi>
              )) || <></>}
          </RowContainer>
        ))}
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex px-3 whitespace-nowrap">Rotation X/Y/Z</div>
        {flatTrackers.map(({ tracker }, index) => (
          <RowContainer
            key={index}
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            <TrackerRotCol tracker={tracker} />
          </RowContainer>
        ))}
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex px-3 whitespace-nowrap">Position X/Y/Z</div>
        {flatTrackers.map(({ tracker }, index) => (
          <RowContainer
            key={index}
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            {(tracker.position && (
              <Typography color="secondary">
                <span className="whitespace-nowrap">
                  {`${tracker.position?.x.toFixed(
                    0
                  )} / ${tracker.position?.y.toFixed(
                    0
                  )} / ${tracker.position?.z.toFixed(0)}`}
                </span>
              </Typography>
            )) || <></>}
          </RowContainer>
        ))}
      </div>
      <div className="flex flex-col gap-1 flex-grow">
        <div className="flex px-3">URL</div>

        {flatTrackers.map(({ device, tracker }, index) => (
          <RowContainer
            key={index}
            rounded="right"
            tracker={tracker}
            onClick={() => clickedTracker(tracker)}
            hover={trackerEqual(tracker.trackerId)}
            onMouseOver={() => setHoverTracker(tracker.trackerId)}
            onMouseOut={() => setHoverTracker(null)}
          >
            <Typography color="secondary">
              udp://
              {IPv4.fromNumber(
                device?.hardwareInfo?.ipAddress?.addr || 0
              ).toString()}
            </Typography>
          </RowContainer>
        ))}
      </div>
    </div>
  );
}
