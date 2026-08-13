import classNames from 'classnames';
import { ReactNode, useState } from 'react';
import { TrackerConnectionGroup as TrackerConnectionGroupData } from '@/store/app-store';
import { Typography } from '@/components/commons/Typography';
import { USBIcon } from '@/components/commons/icon/UsbIcon';
import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { DongleStatus } from 'solarxr-protocol';

function isGroupDisconnected(group: TrackerConnectionGroupData) {
  return group.kind === 'dongle' && group.status === DongleStatus.DISCONNECTED;
}

function ConnectionGroupIcon({
  kind,
  disconnected,
}: {
  kind: TrackerConnectionGroupData['kind'];
  disconnected: boolean;
}) {
  return (
    <div
      className={classNames(
        'h-8 w-8 shrink-0 rounded-full fill-background-10 flex items-center justify-center',
        disconnected ? 'bg-background-50' : 'bg-accent-background-30'
      )}
    >
      {kind === 'dongle' ? (
        <USBIcon size={18} />
      ) : (
        <WifiIcon variant="navbar" value={100} size={16} />
      )}
    </div>
  );
}

export function TrackerConnectionGroupUnassignedDivider({
  count,
  stickyLabel = false,
}: {
  count: number;
  stickyLabel?: boolean;
}) {
  return (
    <div className="relative flex w-full justify-center items-center h-5 px-3">
      <div className="absolute inset-x-3 bg-background-50 h-[2px] rounded-lg" />
      <div
        className={classNames(
          'relative whitespace-nowrap bg-background-70 px-2',
          stickyLabel &&
            'sticky left-[calc(50cqi+8px)] right-[calc(50cqi+8px)] -translate-x-1/2'
        )}
      >
        <Typography
          color="secondary"
          id="toolbar-unassigned_trackers"
          vars={{ count }}
        />
      </div>
    </div>
  );
}

export function TrackerConnectionGroupSection({
  group,
  children,
}: {
  group: TrackerConnectionGroupData;
  children: ReactNode;
}) {
  const [collapsed, setCollapsed] = useState(false);

  const disconnected = isGroupDisconnected(group);
  const lineColor = disconnected
    ? 'border-background-50'
    : 'border-accent-background-30';

  return (
    <div className="flex flex-col">
      <div className="flex items-center gap-4 h-8">
        <ConnectionGroupIcon kind={group.kind} disconnected={disconnected} />
        {group.kind === 'dongle' ? (
          <div
            className="min-w-0 flex-shrink"
            title={group.dongleName || undefined}
          >
            <Typography bold truncate>
              {group.dongleName}
            </Typography>
          </div>
        ) : (
          <Typography
            bold
            whitespace="whitespace-nowrap"
            id="home-connection_group-wifi"
          />
        )}
        <div className="flex-grow border-t-2 border-dashed border-background-60" />
        <div className="sticky -right-2 z-20 flex items-center bg-background-70 pl-4 -ml-4">
          <div className="flex items-center bg-background-80 rounded-full px-1">
            {group.kind === 'dongle' && (
              <div className="flex items-center justify-center fill-background-40 hover:fill-background-30 cursor-pointer rounded-full w-9 h-9">
                <WrenchIcon width={15} />
              </div>
            )}
            <div
              className="flex items-center justify-center fill-background-40 hover:fill-background-30 cursor-pointer rounded-full w-9 h-9"
              onClick={() => setCollapsed((c) => !c)}
            >
              {collapsed ? (
                <ArrowDownIcon size={30} />
              ) : (
                <ArrowUpIcon size={30} />
              )}
            </div>
          </div>
        </div>
      </div>
      {!collapsed && (
        <div className="flex flex-col">
          <div
            className={classNames(
              'ml-[15px] pl-2 border-l-2 pt-3 border-dashed',
              lineColor
            )}
          >
            {children}
          </div>
          <div
            className={classNames(
              'ml-[15px] h-4 w-4 border-l-2 border-b-2 border-dashed rounded-bl-xl',
              lineColor
            )}
          />
        </div>
      )}
    </div>
  );
}
