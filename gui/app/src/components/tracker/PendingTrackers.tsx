import classNames from 'classnames';
import { ReactLocalization, useLocalization } from '@fluent/react';
import {
  BoardType,
  UnknownDeviceStatus,
  UnknownDeviceT,
} from 'solarxr-protocol';
import { usePendingTrackerActions } from '@/hooks/unknown-devices';
import {
  BUTTERFLY_BOARDS,
  SLIME_BOARDS,
} from '@/components/commons/BodyPartIcon';
import { Button } from '@/components/commons/Button';
import { Clickable } from '@/components/commons/Clickable';
import { IconButton } from '@/components/commons/IconButton';
import { Typography } from '@/components/commons/Typography';
import { CloseIcon } from '@/components/commons/icon/CloseIcon';
import { PlusIcon } from '@/components/commons/icon/PlusIcon';
import { SlimeVRIcon } from '@/components/commons/icon/SlimeVRIcon';

export function pendingBoardLabel(
  boardType: BoardType,
  l10n: ReactLocalization
): string {
  if (SLIME_BOARDS.has(boardType) || BUTTERFLY_BOARDS.has(boardType)) {
    return l10n.getString('home-new_trackers-official_board');
  }
  return BoardType[boardType] ?? BoardType[BoardType.UNKNOWN];
}

export function PendingTrackerActions({ device }: { device: UnknownDeviceT }) {
  const { add, ignore } = usePendingTrackerActions();
  const adding = device.status === UnknownDeviceStatus.ADDING;
  const hardwareId = device.hardwareId as string;

  return (
    <div className="grid items-center justify-items-center shrink-0">
      <div
        className={classNames(
          'col-start-1 row-start-1 flex items-center gap-1 transition-opacity',
          { 'opacity-0 pointer-events-none': adding }
        )}
      >
        <IconButton
          labelId="home-new_trackers-ignore"
          tooltipDirection="top"
          className="rounded-full w-[35px] h-[35px] flex items-center justify-center text-background-30 hover:text-background-10 hover:bg-background-60"
          onClick={() => ignore(hardwareId)}
        >
          <CloseIcon className="stroke-current" size={30} />
        </IconButton>
        <Button
          variant="primary"
          rounded
          className="px-4"
          id="home-new_trackers-add"
          onClick={() => add(hardwareId)}
        />
      </div>
      <div
        className={classNames(
          'col-start-1 row-start-1 flex items-center gap-2 pr-2 transition-opacity pointer-events-none',
          { 'opacity-0': !adding }
        )}
      >
        <div className="w-3.5 h-3.5 rounded-full border-2 border-background-10 border-t-transparent animate-spin" />
        <Typography
          id="home-new_trackers-adding"
          whitespace="whitespace-nowrap"
        />
      </div>
    </div>
  );
}

export function PendingTrackerGlyph({ size }: { size: number }) {
  return (
    <div className="flex items-center justify-center shrink-0 opacity-50">
      <SlimeVRIcon width={size} />
    </div>
  );
}

export function PendingTrackerCard({ device }: { device: UnknownDeviceT }) {
  const { l10n } = useLocalization();
  const details = [device.hardwareId, device.firmwareVersion]
    .filter(Boolean)
    .join(' · ');

  return (
    <div
      className={classNames(
        'flex items-center rounded-lg py-2 px-3 w-full gap-4 h-[70px] border-2 transition-colors',
        device.status === UnknownDeviceStatus.ADDING
          ? 'border-solid border-accent-background-30'
          : 'border-dashed border-background-50'
      )}
    >
      <div className="w-[44px] flex justify-center">
        <PendingTrackerGlyph size={38} />
      </div>
      <div className="flex flex-col flex-grow justify-center gap-1 min-w-0">
        <Typography bold truncate variant="section-title">
          {pendingBoardLabel(device.boardType, l10n)}
        </Typography>
        <Typography truncate>{details}</Typography>
      </div>
      <PendingTrackerActions device={device} />
    </div>
  );
}

export function PendingTrackersDivider({
  devices,
  stickyLabel = false,
}: {
  devices: UnknownDeviceT[];
  stickyLabel?: boolean;
}) {
  const { addAll } = usePendingTrackerActions();
  const waitingCount = devices.filter(
    (device) => device.status !== UnknownDeviceStatus.ADDING
  ).length;

  return (
    <div className="relative flex w-full justify-center items-center min-h-[15px] px-3 my-1">
      <div className="absolute inset-x-3 bg-background-50 h-[2px] rounded-lg" />
      <div
        className={classNames(
          'relative whitespace-nowrap px-2',
          stickyLabel &&
            'sticky left-[calc(50cqi+8px)] right-[calc(50cqi+8px)] -translate-x-1/2'
        )}
      >
        <div className="absolute inset-x-0 top-1/2 -translate-y-1/2 h-[2px] bg-background-70 rounded-lg" />
        <div className="relative flex items-center gap-2 bg-background-70 px-1">
          <div className="w-2 h-2 rounded-full bg-accent-background-20 animate-pulse" />
          <Typography
            id="home-new_trackers-count"
            vars={{ count: devices.length }}
          />
          {waitingCount > 1 && (
            <Clickable
              className="flex items-center gap-1 h-[22px] px-2.5 rounded-full bg-accent-background-30 hover:bg-accent-background-20 text-background-10 fill-background-10"
              onClick={() => addAll(devices)}
            >
              <PlusIcon size={12} />
              <Typography bold id="home-new_trackers-add_all" />
            </Clickable>
          )}
        </div>
      </div>
    </div>
  );
}
