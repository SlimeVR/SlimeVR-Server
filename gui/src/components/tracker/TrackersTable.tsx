import classNames from 'classnames';
import { IPv4 } from 'ip-num/IPNumber';
import { createContext, ReactNode, useContext, useMemo } from 'react';
import {
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
  TrackingChecklistStepT,
} from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { useTracker } from '@/hooks/tracker';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Typography } from '@/components/commons/Typography';
import { formatVector3 } from '@/utils/formatting';
import { TrackerBattery } from './TrackerBattery';
import { TrackerStatus } from './TrackerStatus';
import { TrackerWifi } from './TrackerWifi';
import { FlatDeviceTracker } from '@/store/app-store';
import { StayAlignedInfo } from '@/components/stay-aligned/StayAlignedInfo';
import {
  HightlightedTrackers,
  trackingchecklistIdtoLabel,
  useTrackingChecklist,
} from '@/hooks/tracking-checklist';
import { Tooltip } from '@/components/commons/Tooltip';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';

const isSlime = ({ device }: FlatDeviceTracker) =>
  device?.hardwareInfo?.manufacturer === 'SlimeVR' ||
  device?.hardwareInfo?.manufacturer === 'HID Device';

const getDeviceName = ({ device }: FlatDeviceTracker) =>
  device?.customName?.toString() || '';

const getTrackerName = ({ tracker }: FlatDeviceTracker) =>
  tracker?.info?.customName?.toString() || '';

export function TrackerNameCell({
  tracker,
  warning,
}: {
  tracker: TrackerDataT;
  warning: TrackingChecklistStepT | boolean;
}) {
  const { useName } = useTracker(tracker);

  const name = useName();

  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-center items-center fill-background-10 relative">
        {warning && (
          <div className="absolute -left-2 -top-1 text-status-warning ">
            <WarningIcon width={16}></WarningIcon>
          </div>
        )}
        <div
          className={classNames(
            'border-[2px] border-opacity-80 rounded-md overflow-clip',
            {
              'border-status-warning': warning,
              'border-transparent': !warning,
            }
          )}
        >
          <BodyPartIcon bodyPart={tracker.info?.bodyPart}></BodyPartIcon>
        </div>
      </div>
      <div className="flex flex-col flex-grow">
        <Typography bold whitespace="whitespace-nowrap">
          {name}
        </Typography>
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
    </div>
  );
}

export function TrackerRotCell({
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

  const rotationRaw = useRawRotationEulerDegrees();
  const rotationRef = useRefAdjRotationEulerDegrees() || rotationRaw;
  const rot = referenceAdjusted ? rotationRef : rotationRaw;

  return (
    <Typography color={color} whitespace="whitespace-nowrap">
      {formatVector3(rot, precise ? 2 : 0)}
    </Typography>
  );
}

function Header({
  name,
  className,
  first = false,
  last = false,
  show = true,
}: {
  first?: boolean;
  last?: boolean;
  name: string;
  className?: string;
  show?: boolean;
}) {
  return (
    <th
      className={classNames('text-start px-2', {
        hidden: !show,
        'pl-4': first,
        'pr-4': last,
      })}
    >
      <div className={className}>
        <Typography id={name} whitespace="whitespace-nowrap"></Typography>
      </div>
    </th>
  );
}

function Cell({
  children,
  first = false,
  last = false,
  show = true,
}: {
  children: ReactNode;
  first?: boolean;
  last?: boolean;
  show?: boolean;
}) {
  const { tracker } = useContext(TrackerRowProvider);
  const { useVelocity } = useTracker(tracker);

  const velocity = useVelocity();

  return (
    <td className={classNames('py-2 group overflow-hidden', { hidden: !show })}>
      <div
        style={{
          boxShadow: `0px 0px ${Math.floor(velocity * 8)}px ${Math.floor(
            velocity * 8
          )}px rgb(var(--accent-background-30))`,
        }}
        className={classNames(
          { 'rounded-l-md ml-3': first, 'rounded-r-md mr-3': last },
          'bg-background-60 group-hover:bg-background-50 hover:cursor-pointer p-2 h-[50px] flex items-center'
        )}
      >
        {children}
      </div>
    </td>
  );
}

const TrackerRowProvider = createContext<FlatDeviceTracker>(undefined as never);

function Row({
  data,
  hightlightedTrackers,
  clickedTracker,
}: {
  data: FlatDeviceTracker;
  hightlightedTrackers: HightlightedTrackers | undefined;
  clickedTracker: (tracker: TrackerDataT) => void;
}) {
  const { config } = useConfig();
  const fontColor = config?.devSettings?.highContrast ? 'primary' : 'secondary';
  const moreInfo = config?.devSettings?.moreInfo;

  const { tracker, device } = data;

  const warning =
    !!hightlightedTrackers?.trackers.find(
      (t) =>
        t?.deviceId?.id === tracker.trackerId?.deviceId?.id &&
        t?.trackerNum === tracker.trackerId?.trackerNum
    ) && hightlightedTrackers.step;

  return (
    <TrackerRowProvider.Provider value={data}>
      <Tooltip
        disabled={!warning}
        preferedDirection="top"
        content={
          warning && (
            <div className="flex gap-1 items-center text-status-warning">
              <WarningIcon width={20}></WarningIcon>
              <Typography id={trackingchecklistIdtoLabel[warning.id]} />
            </div>
          )
        }
        tag="tr"
        spacing={-5}
      >
        <tr className="group" onClick={() => clickedTracker(tracker)}>
          <Cell first>
            <TrackerNameCell
              tracker={tracker}
              warning={warning}
            ></TrackerNameCell>
          </Cell>
          <Cell>
            <Typography color={fontColor}>
              {device?.hardwareInfo?.manufacturer || '--'}
            </Typography>
          </Cell>
          <Cell>
            {device?.hardwareStatus?.batteryPctEstimate != null && (
              <TrackerBattery
                value={device.hardwareStatus.batteryPctEstimate / 100}
                voltage={device.hardwareStatus.batteryVoltage}
                disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                textColor={fontColor}
              />
            )}
          </Cell>
          <Cell>
            {(device?.hardwareStatus?.rssi != null ||
              device?.hardwareStatus?.ping != null) && (
              <TrackerWifi
                rssi={device?.hardwareStatus?.rssi}
                rssiShowNumeric
                ping={device?.hardwareStatus?.ping}
                disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                textColor={fontColor}
              ></TrackerWifi>
            )}
          </Cell>
          <Cell>
            {tracker.tps && (
              <Typography color={fontColor}>{tracker.tps}</Typography>
            )}
          </Cell>
          <Cell>
            <TrackerRotCell
              tracker={tracker}
              precise={config?.devSettings?.preciseRotation}
              referenceAdjusted={!config?.devSettings?.rawSlimeRotation}
              color={fontColor}
            />
          </Cell>
          <Cell last={!moreInfo}>
            {tracker?.temp && tracker?.temp?.temp != 0 && (
              <Typography color={fontColor} whitespace="whitespace-nowrap">
                {tracker.temp.temp.toFixed(2)}
              </Typography>
            )}
          </Cell>
          <Cell show={moreInfo}>
            {tracker.linearAcceleration && (
              <Typography color={fontColor} whitespace="whitespace-nowrap">
                {formatVector3(tracker.linearAcceleration, 1)}
              </Typography>
            )}
          </Cell>
          <Cell show={moreInfo}>
            {tracker.position && (
              <Typography color={fontColor} whitespace="whitespace-nowrap">
                {formatVector3(tracker.position, 2)}
              </Typography>
            )}
          </Cell>
          <Cell show={moreInfo}>
            <StayAlignedInfo color={fontColor} tracker={tracker} />
          </Cell>
          <Cell last={moreInfo} show={moreInfo}>
            <Typography color={fontColor} whitespace="whitespace-nowrap">
              udp://
              {IPv4.fromNumber(
                device?.hardwareInfo?.ipAddress?.addr || 0
              ).toString()}
            </Typography>
          </Cell>
        </tr>
      </Tooltip>
    </TrackerRowProvider.Provider>
  );
}

export function TrackersTable({
  flatTrackers,
  clickedTracker,
}: {
  clickedTracker: (tracker: TrackerDataT) => void;
  flatTrackers: FlatDeviceTracker[];
}) {
  const { config } = useConfig();
  const { hightlightedTrackers } = useTrackingChecklist();

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

  const moreInfo = config?.devSettings?.moreInfo;

  return (
    <div className="w-full overflow-x-auto py-2">
      <table className="w-full" cellPadding={0} cellSpacing={0}>
        <tr>
          <Header name={'tracker-table-column-name'} first></Header>
          <Header name={'tracker-table-column-type'}></Header>
          <Header name={'tracker-table-column-battery'}></Header>
          <Header name={'tracker-table-column-ping'}></Header>
          <Header name={'tracker-table-column-tps'}></Header>
          <Header
            name={'tracker-table-column-rotation'}
            className={classNames({
              'w-44': config?.devSettings?.preciseRotation,
              'w-32': !config?.devSettings?.preciseRotation,
            })}
          ></Header>
          <Header
            name={'tracker-table-column-temperature'}
            last={!moreInfo}
          ></Header>
          <Header
            name={'tracker-table-column-linear-acceleration'}
            className="w-36"
            show={moreInfo}
          ></Header>
          <Header
            name={'tracker-table-column-position'}
            className="w-36"
            show={moreInfo}
          ></Header>
          <Header
            name={'tracker-table-column-stay_aligned'}
            className="w-36"
            show={moreInfo}
            last={moreInfo}
          ></Header>
          <Header name={'tracker-table-column-url'} show={moreInfo}></Header>
        </tr>
        {filteredSortedTrackers.map((data) => (
          <Row
            clickedTracker={clickedTracker}
            data={data}
            hightlightedTrackers={hightlightedTrackers}
          ></Row>
        ))}
      </table>
    </div>
  );
}
