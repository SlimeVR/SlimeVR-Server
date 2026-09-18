import classNames from 'classnames';
import { IPv4 } from 'ip-num';
import { ReactNode, useMemo, useState } from 'react';
import {
  defaultTrackersTableColumns,
  trackersTableColumnOrder,
  TrackersTableColumnsConfig,
  TrackersTableOptionalColumn,
  useConfig,
} from '@/hooks/config';
import {
  getLocalizedTrackerName,
  useTracker,
  velocityGlowStyle,
} from '@/hooks/tracker';
import { ReactLocalization, useLocalization } from '@fluent/react';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { Typography } from '@/components/commons/Typography';
import { formatVector3 } from '@/utils/formatting';
import { TrackerBattery } from './TrackerBattery';
import { TrackerStatus } from './TrackerStatus';
import { TrackerWifi } from './TrackerWifi';
import { FlatDeviceTracker, TrackerConnectionGroup } from '@/store/app-store';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import {
  TrackerConnectionGroupDefaultToolbox,
  TrackerConnectionGroupSection,
  TrackerConnectionGroupUnassignedDivider,
} from './TrackerConnectionGroup';
import { StayAlignedInfo } from '@/components/stay-aligned/StayAlignedInfo';
import { Tooltip } from '@/components/commons/Tooltip';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { FirmwareIcon } from '@/components/commons/FirmwareIcon';
import {
  DeviceDataT,
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
  TrackingChecklistStepT,
} from 'solarxr-protocol';
import {
  highlightedTrackers,
  trackingchecklistIdtoLabel,
  useTrackingChecklist,
} from '@/hooks/tracking-checklist';

function lastVisibleColumn(
  columns: TrackersTableColumnsConfig
): TrackersTableOptionalColumn | null {
  for (let i = trackersTableColumnOrder.length - 1; i >= 0; i--) {
    const column = trackersTableColumnOrder[i];
    if (columns[column]) return column;
  }
  return null;
}

function columnProps(
  column: TrackersTableOptionalColumn,
  columns: TrackersTableColumnsConfig,
  lastColumn: TrackersTableOptionalColumn | null
): { show: boolean; last: boolean } {
  return { show: columns[column], last: lastColumn === column };
}

type SortColumn = 'name' | 'type' | 'battery' | 'ping' | 'tps' | 'temperature';
type SortDirection = 'asc' | 'desc';
type SortState = { column: SortColumn; direction: SortDirection } | null;

const trackerSortValue = (
  { tracker, device }: FlatDeviceTracker,
  column: SortColumn,
  l10n: ReactLocalization
): string | number => {
  switch (column) {
    case 'name':
      return getLocalizedTrackerName(l10n, tracker?.info ?? null).toString();
    case 'type':
      return device?.hardwareInfo?.manufacturer?.toString() || '';
    case 'battery':
      return device?.hardwareStatus?.batteryPctEstimate ?? -1;
    case 'ping':
      return device?.hardwareStatus?.rssi ?? -Infinity;
    case 'tps':
      return tracker.tps ?? -1;
    case 'temperature':
      return tracker.temp ?? -Infinity;
  }
};

function compareTrackers(
  a: FlatDeviceTracker,
  b: FlatDeviceTracker,
  sortState: SortState,
  l10n: ReactLocalization
) {
  if (!sortState) return 0;
  const av = trackerSortValue(a, sortState.column, l10n);
  const bv = trackerSortValue(b, sortState.column, l10n);
  const result =
    typeof av === 'string' && typeof bv === 'string'
      ? av.localeCompare(bv)
      : (av as number) - (bv as number);
  return sortState.direction === 'asc' ? result : -result;
}

export function TrackerNameCell({
  tracker,
  device,
  warning,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  warning: TrackingChecklistStepT | boolean;
}) {
  const { useName } = useTracker(tracker);

  const name = useName();

  return (
    <div className="flex gap-2">
      <div className="flex flex-col justify-center items-center fill-background-10 relative">
        {warning && (
          <div className="absolute -left-2 -top-1 text-status-warning ">
            <WarningIcon width={16} />
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
          <BodyPartIcon
            bodyPart={tracker.info?.bodyPart}
            device={device}
            trackerId={tracker.trackerId}
          />
        </div>
      </div>
      <div className="flex flex-col flex-grow">
        <Typography bold whitespace="whitespace-nowrap">
          {name}
        </Typography>
        <TrackerStatus status={tracker.status} />
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
  first = false,
  last = false,
  show = true,
  sortKey,
  sortState,
  onSort,
}: {
  first?: boolean;
  last?: boolean;
  name: string;
  className?: string;
  show?: boolean;
  sortKey?: SortColumn;
  sortState?: SortState;
  onSort?: (column: SortColumn) => void;
}) {
  const sortable = !!sortKey && !!onSort;
  const active = sortable && sortState?.column === sortKey;

  return (
    <div
      className={classNames('text-start px-2 flex items-center gap-1', {
        hidden: !show,
        'pl-4': first,
        'pr-4': last,
        'cursor-pointer select-none hover:text-background-10': sortable,
      })}
      onClick={sortable ? () => onSort!(sortKey!) : undefined}
    >
      <Typography id={name} whitespace="whitespace-nowrap" />
      {sortable && (
        <span
          className={classNames({ 'opacity-0': !active }, 'fill-background-10')}
        >
          {active && sortState?.direction === 'desc' ? (
            <ArrowDownIcon size={12} />
          ) : (
            <ArrowUpIcon size={12} />
          )}
        </span>
      )}
    </div>
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
  return (
    <div className={classNames('py-0.5 group', { hidden: !show })}>
      <div
        className={classNames(
          { 'rounded-l-md ml-1': first, 'rounded-r-md mr-1': last },
          'bg-background-60 group-hover:bg-background-50 hover:cursor-pointer p-2 h-[50px] flex items-center overflow-hidden'
        )}
      >
        {children}
      </div>
    </div>
  );
}

function Row({
  data,
  highlightedTrackers,
  clickedTracker,
  gridTemplateColumns,
  columns,
  lastColumn,
}: {
  data: FlatDeviceTracker;
  highlightedTrackers: highlightedTrackers | undefined;
  clickedTracker: (tracker: TrackerDataT) => void;
  gridTemplateColumns: string;
  columns: TrackersTableColumnsConfig;
  lastColumn: TrackersTableOptionalColumn | null;
}) {
  const { config } = useConfig();
  const fontColor = config?.devSettings?.highContrast ? 'primary' : 'secondary';
  const col = (column: TrackersTableOptionalColumn) =>
    columnProps(column, columns, lastColumn);

  const { tracker, device } = data;
  const { useVelocity } = useTracker(tracker);
  const velocity = useVelocity();

  const warning =
    !!highlightedTrackers?.trackers.find((t) => t === tracker.trackerId) &&
    highlightedTrackers.step;

  return (
    <>
      <div className="relative z-10">
        <div className="absolute top-2 left-5">
          <FirmwareIcon tracker={tracker} device={device} />
        </div>
      </div>
      <Tooltip
        disabled={!warning}
        preferedDirection="top"
        content={
          warning && (
            <div className="flex gap-1 items-center text-status-warning">
              <WarningIcon width={20} />
              <Typography id={trackingchecklistIdtoLabel[warning.id]} />
            </div>
          )
        }
        spacing={-5}
      >
        <div className="relative">
          <div
            className="pointer-events-none absolute inset-y-0.5 inset-x-1 rounded-md transition-[box-shadow] duration-200 ease-linear"
            style={velocityGlowStyle(velocity)}
          />
          <div
            className="group grid items-center"
            style={{ gridTemplateColumns }}
            onClick={() => clickedTracker(tracker)}
          >
            <Cell first last={!lastColumn}>
              <TrackerNameCell
                tracker={tracker}
                device={device}
                warning={warning}
              />
            </Cell>
            <Cell {...col('type')}>
              <Typography color={fontColor}>
                {device?.hardwareInfo?.manufacturer || '--'}
              </Typography>
            </Cell>
            <Cell {...col('battery')}>
              {device?.hardwareStatus?.batteryPctEstimate != null && (
                <TrackerBattery
                  value={device.hardwareStatus.batteryPctEstimate / 100}
                  voltage={device.hardwareStatus.batteryVoltage}
                  runtime={device.hardwareStatus.batteryRuntimeEstimate}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                  textColor={fontColor}
                />
              )}
            </Cell>
            <Cell {...col('ping')}>
              {(device?.hardwareStatus?.rssi != null ||
                device?.hardwareStatus?.ping != null) && (
                <TrackerWifi
                  rssi={device?.hardwareStatus?.rssi}
                  rssiShowNumeric
                  ping={device?.hardwareStatus?.ping}
                  disabled={tracker.status === TrackerStatusEnum.DISCONNECTED}
                  textColor={fontColor}
                  showPacketLoss
                  packetLoss={device.hardwareStatus.packetLoss}
                  packetsLost={device.hardwareStatus.packetsLost}
                  packetsReceived={device.hardwareStatus.packetsReceived}
                />
              )}
            </Cell>
            <Cell {...col('tps')}>
              {tracker.tps !== null && (
                <Typography color={fontColor}>
                  {tracker.tps.toString()}
                </Typography>
              )}
            </Cell>
            <Cell {...col('rotation')}>
              <TrackerRotCell
                tracker={tracker}
                precise={config?.devSettings?.preciseRotation}
                referenceAdjusted={!config?.devSettings?.rawSlimeRotation}
                color={fontColor}
              />
            </Cell>
            <Cell {...col('temperature')}>
              {tracker?.temp && tracker?.temp != 0 && (
                <Typography color={fontColor} whitespace="whitespace-nowrap">
                  {tracker.temp.toFixed(2)}
                </Typography>
              )}
            </Cell>
            <Cell {...col('linearAcceleration')}>
              {tracker.linearAcceleration && (
                <Typography color={fontColor} whitespace="whitespace-nowrap">
                  {formatVector3(tracker.linearAcceleration, 1)}
                </Typography>
              )}
            </Cell>
            <Cell {...col('position')}>
              {tracker.position && (
                <Typography color={fontColor} whitespace="whitespace-nowrap">
                  {formatVector3(tracker.position, 2)}
                </Typography>
              )}
            </Cell>
            <Cell {...col('stayAligned')}>
              <StayAlignedInfo color={fontColor} tracker={tracker} />
            </Cell>
            <Cell {...col('url')}>
              <Typography color={fontColor} whitespace="whitespace-nowrap">
                udp://
                {IPv4.fromNumber(
                  device?.hardwareInfo?.ipAddress || 0
                ).toString()}
              </Typography>
            </Cell>
          </div>
        </div>
      </Tooltip>
    </>
  );
}

export function TrackersTable({
  groups,
  clickedTracker,
  onOpenMetrics,
}: {
  clickedTracker: (tracker: TrackerDataT) => void;
  groups: TrackerConnectionGroup[];
  onOpenMetrics: (group: TrackerConnectionGroup) => void;
}) {
  const { config } = useConfig();
  const { highlightedTrackers } = useTrackingChecklist();
  const { l10n } = useLocalization();

  const [sortState, setSortState] = useState<SortState>(null);

  const onSort = (column: SortColumn) => {
    setSortState((curr) => {
      if (curr?.column !== column) return { column, direction: 'asc' };
      if (curr.direction === 'asc') return { column, direction: 'desc' };
      return null;
    });
  };

  const sortedGroups = useMemo(() => {
    if (!sortState) return groups;
    const cmp = (a: FlatDeviceTracker, b: FlatDeviceTracker) =>
      compareTrackers(a, b, sortState, l10n);
    return groups.map((group) => ({
      ...group,
      assigned: group.assigned.toSorted(cmp),
      unassigned: group.unassigned.toSorted(cmp),
    }));
  }, [groups, sortState, l10n]);

  const columns = config?.trackersTableColumns ?? defaultTrackersTableColumns;
  const lastColumn = useMemo(() => lastVisibleColumn(columns), [columns]);
  const col = (column: TrackersTableOptionalColumn) =>
    columnProps(column, columns, lastColumn);
  const sortProps = (column: SortColumn) => ({
    sortKey: column,
    sortState,
    onSort,
  });

  const gridTemplateColumns = useMemo(() => {
    const cols = ['minmax(15rem, 1.5fr)']; // Name

    if (columns.type) cols.push('9rem');
    if (columns.battery) cols.push('9rem');
    if (columns.ping) cols.push('9rem'); // (w-24)
    if (columns.tps) cols.push('5rem');
    if (columns.rotation)
      cols.push(config?.devSettings?.preciseRotation ? '11rem' : '9rem');
    if (columns.temperature) cols.push('9rem');
    if (columns.linearAcceleration) cols.push('9rem');
    if (columns.position) cols.push('9rem');
    if (columns.stayAligned) cols.push('9rem');
    if (columns.url) cols.push('11rem');

    return cols.join(' ');
  }, [columns, config?.devSettings?.preciseRotation]);

  return (
    <div className="w-full py-2 px-2">
      <div className="min-w-fit">
        <div
          className={classNames('ml-2 pl-2 border-l-2', 'border-transparent')}
        >
          <div
            className="grid items-center mb-1"
            style={{ gridTemplateColumns }}
          >
            <Header
              name={'tracker-table-column-name'}
              first
              last={!lastColumn}
              {...sortProps('name')}
            />
            <Header
              name={'tracker-table-column-type'}
              {...col('type')}
              {...sortProps('type')}
            />
            <Header
              name={'tracker-table-column-battery'}
              {...col('battery')}
              {...sortProps('battery')}
            />
            <Header
              name={'tracker-table-column-ping'}
              {...col('ping')}
              {...sortProps('ping')}
            />
            <Header
              name={'tracker-table-column-tps'}
              {...col('tps')}
              {...sortProps('tps')}
            />
            <Header
              name={'tracker-table-column-rotation'}
              {...col('rotation')}
            />
            <Header
              name={'tracker-table-column-temperature'}
              {...col('temperature')}
              {...sortProps('temperature')}
            />
            <Header
              name={'tracker-table-column-linear-acceleration'}
              {...col('linearAcceleration')}
            />
            <Header
              name={'tracker-table-column-position'}
              {...col('position')}
            />
            <Header
              name={'tracker-table-column-stay_aligned'}
              {...col('stayAligned')}
            />
            <Header name={'tracker-table-column-url'} {...col('url')} />
          </div>
        </div>
        <div className="flex flex-col gap-2.5">
          {sortedGroups.map((group) => (
            <TrackerConnectionGroupSection
              key={group.key}
              group={group}
              variant="primary"
              toolbox={
                <TrackerConnectionGroupDefaultToolbox
                  group={group}
                  onOpenMetrics={onOpenMetrics}
                />
              }
            >
              <div className="flex flex-col gap-0.5">
                {group.assigned.map((data, index) => (
                  <Row
                    key={index}
                    clickedTracker={clickedTracker}
                    data={data}
                    highlightedTrackers={highlightedTrackers}
                    gridTemplateColumns={gridTemplateColumns}
                    columns={columns}
                    lastColumn={lastColumn}
                  />
                ))}
                {group.assigned.length > 0 && group.unassigned.length > 0 && (
                  <TrackerConnectionGroupUnassignedDivider
                    count={group.unassigned.length}
                    stickyLabel
                  />
                )}
                {group.unassigned.map((data, index) => (
                  <Row
                    key={index}
                    clickedTracker={clickedTracker}
                    data={data}
                    highlightedTrackers={highlightedTrackers}
                    gridTemplateColumns={gridTemplateColumns}
                    columns={columns}
                    lastColumn={lastColumn}
                  />
                ))}
              </div>
            </TrackerConnectionGroupSection>
          ))}
        </div>
      </div>
    </div>
  );
}
