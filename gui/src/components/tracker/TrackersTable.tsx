import classNames from 'classnames';
import { IPv4 } from 'ip-num/IPNumber';
import { MouseEventHandler, ReactNode, useState, useMemo } from 'react';
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
import { useLocalization } from '@fluent/react';
import { formatVector3 } from '../utils/formatting';
import { useConfig } from '../../hooks/config';

const isSlime = ({ device }: FlatDeviceTracker) =>
  device?.hardwareInfo?.manufacturer === 'SlimeVR';

const getDeviceName = ({ device }: FlatDeviceTracker) =>
  device?.customName?.toString() || '';

const getTrackerName = ({ tracker }: FlatDeviceTracker) =>
  tracker?.info?.customName?.toString() || '';

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

export function TrackerRotCol({
  tracker,
  precise,
  color,
  referenceAdjusted,
}: {
  tracker: TrackerDataT;
  precise?: boolean;
  color?: string;
  referenceAdjusted?: boolean;
}) {
  const { useRawRotationEulerDegrees, useRefAdjRotationEulerDegrees } =
    useTracker(tracker);

  const rot = referenceAdjusted
    ? useRefAdjRotationEulerDegrees()
    : useRawRotationEulerDegrees();

  return (
    <Typography color={color}>
      <span className="whitespace-nowrap">
        {formatVector3(rot, precise ? 2 : 0)}
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
  children: ReactNode;
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
          hover ? 'bg-background-50 cursor-pointer' : 'bg-background-60'
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
  const { l10n } = useLocalization();
  const [hoverTracker, setHoverTracker] = useState<TrackerIdT | null>(null);
  const { config } = useConfig();

  const trackerEqual = (id: TrackerIdT | null) =>
    id?.trackerNum == hoverTracker?.trackerNum &&
    (!id?.deviceId || id.deviceId.id == hoverTracker?.deviceId?.id);

  const filteringEnabled =
    config?.debug && config?.devSettings?.filterSlimesAndHMD;
  const sortingEnabled = config?.debug && config?.devSettings?.sortByName;
  // TODO: fix memo
  const filteredSortedTrackers = useMemo(() => {
    const list = filteringEnabled
      ? flatTrackers.filter((t) => getDeviceName(t) === 'HMD' || isSlime(t))
      : flatTrackers;

    if (sortingEnabled) {
      list.sort((a, b) => getTrackerName(a).localeCompare(getTrackerName(b)));
    }
    return list;
  }, [flatTrackers, filteringEnabled, sortingEnabled]);

  const makeColumnContainerProps = (tracker: TrackerDataT, index: number) => ({
    key: index,
    tracker,
    onClick: () => clickedTracker(tracker),
    hover: trackerEqual(tracker.trackerId),
    onMouseOver: () => setHoverTracker(tracker.trackerId),
    onMouseOut: () => setHoverTracker(null),
  });

  const fontColor = config?.devSettings?.highContrast ? 'primary' : 'secondary';
  const moreInfo = config?.devSettings?.moreInfo;

  return (
    <div className="flex w-full overflow-x-auto py-2">
      {/* Name */}
      <div className="flex flex-col gap-1">
        <div className="flex px-3">
          {l10n.getString('tracker-table-column-name')}
        </div>
        {filteredSortedTrackers.map(({ tracker }, index) => (
          <RowContainer
            rounded="left"
            {...makeColumnContainerProps(tracker, index)}
          >
            <TrackerNameCol tracker={tracker}></TrackerNameCol>
          </RowContainer>
        ))}
      </div>

      {/* Type */}
      <div className="flex flex-col gap-1">
        <div className="flex px-3">
          {l10n.getString('tracker-table-column-type')}
        </div>
        {filteredSortedTrackers.map(({ device, tracker }, index) => (
          <RowContainer {...makeColumnContainerProps(tracker, index)}>
            <Typography color={fontColor}>
              {device?.hardwareInfo?.manufacturer || '--'}
            </Typography>
          </RowContainer>
        ))}
      </div>

      {/* Battery */}
      <div className="flex flex-col gap-1">
        <div className="flex px-3">
          {l10n.getString('tracker-table-column-battery')}
        </div>
        {filteredSortedTrackers.map(({ device, tracker }, index) => (
          <RowContainer {...makeColumnContainerProps(tracker, index)}>
            {(device &&
              device.hardwareStatus &&
              device.hardwareStatus.batteryPctEstimate && (
                <TrackerBattery
                  value={device.hardwareStatus.batteryPctEstimate / 100}
                  voltage={device.hardwareStatus?.batteryVoltage}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                  textColor={fontColor}
                />
              )) || <></>}
          </RowContainer>
        ))}
      </div>

      {/* Ping */}
      <div className="flex flex-col gap-1">
        <div className="flex px-3">
          {l10n.getString('tracker-table-column-ping')}
        </div>
        {filteredSortedTrackers.map(({ device, tracker }, index) => (
          <RowContainer {...makeColumnContainerProps(tracker, index)}>
            {(device &&
              device.hardwareStatus &&
              device.hardwareStatus.rssi &&
              device.hardwareStatus.ping && (
                <TrackerWifi
                  rssi={device.hardwareStatus.rssi}
                  rssiShowNumeric
                  ping={device.hardwareStatus.ping}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                  textColor={fontColor}
                ></TrackerWifi>
              )) || <></>}
          </RowContainer>
        ))}
      </div>

      {/* TPS */}
      <div className="flex flex-col gap-1">
        <div className="flex px-3">
          {l10n.getString('tracker-table-column-tps')}
        </div>
        {filteredSortedTrackers.map(({ device, tracker }, index) => (
          <RowContainer {...makeColumnContainerProps(tracker, index)}>
            <Typography color={fontColor}>
              {device?.hardwareStatus?.tps || <></>}
            </Typography>
          </RowContainer>
        ))}
      </div>

      {/* Rotation */}
      <div className="flex flex-col gap-1">
        <div
          className={classNames('flex px-3 whitespace-nowrap', {
            'w-44': config?.devSettings?.preciseRotation,
            'w-32': !config?.devSettings?.preciseRotation,
          })}
        >
          {l10n.getString('tracker-table-column-rotation')}
        </div>
        {filteredSortedTrackers.map(({ tracker }, index) => (
          <RowContainer {...makeColumnContainerProps(tracker, index)}>
            <TrackerRotCol
              tracker={tracker}
              precise={config?.devSettings?.preciseRotation}
              referenceAdjusted={!config?.devSettings?.rawSlimeRotation}
              color={fontColor}
            />
          </RowContainer>
        ))}
      </div>

      {/* Temperature */}
      <div
        className={classNames('flex flex-col gap-1', {
          'flex-grow': !moreInfo,
        })}
      >
        <div className="flex px-3 whitespace-nowrap">
          {l10n.getString('tracker-table-column-temperature')}
        </div>
        {filteredSortedTrackers.map(({ tracker }, index) => (
          <RowContainer
            rounded={moreInfo ? 'none' : 'right'}
            {...makeColumnContainerProps(tracker, index)}
          >
            {(tracker.temp && (
              <Typography color={fontColor}>
                <span className="whitespace-nowrap">
                  {`${tracker.temp?.temp.toFixed(2)}`}
                </span>
              </Typography>
            )) || <></>}
          </RowContainer>
        ))}
      </div>

      {/* Linear Acceleration */}
      {moreInfo && (
        <div className="flex flex-col gap-1">
          <div className="flex px-3 whitespace-nowrap w-32">
            {l10n.getString('tracker-table-column-linear-acceleration')}
          </div>
          {filteredSortedTrackers.map(({ tracker }, index) => (
            <RowContainer {...makeColumnContainerProps(tracker, index)}>
              {(tracker.linearAcceleration && (
                <Typography color={fontColor}>
                  <span className="whitespace-nowrap">
                    {formatVector3(tracker.linearAcceleration, 1)}
                  </span>
                </Typography>
              )) || <></>}
            </RowContainer>
          ))}
        </div>
      )}

      {/* Position */}
      {moreInfo && (
        <div className="flex flex-col gap-1">
          <div className="flex px-3 whitespace-nowrap w-32">
            {l10n.getString('tracker-table-column-position')}
          </div>
          {filteredSortedTrackers.map(({ tracker }, index) => (
            <RowContainer {...makeColumnContainerProps(tracker, index)}>
              {(tracker.position && (
                <Typography color={fontColor}>
                  <span className="whitespace-nowrap">
                    {formatVector3(tracker.position, 1)}
                  </span>
                </Typography>
              )) || <></>}
            </RowContainer>
          ))}
        </div>
      )}

      {/* URL */}
      {moreInfo && (
        <div className="flex flex-col gap-1 flex-grow">
          <div className="flex px-3">
            {l10n.getString('tracker-table-column-url')}
          </div>

          {filteredSortedTrackers.map(({ device, tracker }, index) => (
            <RowContainer
              rounded="right"
              {...makeColumnContainerProps(tracker, index)}
            >
              <Typography color={fontColor}>
                udp://
                {IPv4.fromNumber(
                  device?.hardwareInfo?.ipAddress?.addr || 0
                ).toString()}
              </Typography>
            </RowContainer>
          ))}
        </div>
      )}
    </div>
  );
}
