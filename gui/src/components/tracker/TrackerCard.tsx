import { useConfig } from '@/hooks/config';
import { MouseEventHandler } from 'react';
import {
  DeviceDataT,
  TrackerDataT,
  TrackerStatus as TrackerStatusEnum,
  TrackingChecklistStepT,
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
import { Tooltip } from '@/components/commons/Tooltip';
import { Localized } from '@fluent/react';
import { checkForUpdate } from '@/hooks/firmware-update';
import { WarningIcon } from '@/components/commons/icon/WarningIcon';
import { trackingchecklistIdtoLabel } from '@/hooks/tracking-checklist';

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
      ></div>
      <div
        className={classNames(
          'absolute rounded-full h-8 w-8 justify-center flex items-center',
          showUpdate === 'low-battery'
            ? 'cursor-not-allowed bg-background-80 outline-2 outline-status-critical outline'
            : 'hover:bg-background-40 hover:cursor-pointer bg-background-50'
        )}
      >
        <DownloadIcon width={15}></DownloadIcon>
      </div>
    </div>
  );

  return showUpdate !== 'can-update' ? (
    <Tooltip
      preferedDirection="top"
      content={
        <Localized id={'tracker-settings-update-low-battery'}>
          <Typography></Typography>
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

function TrackerBig({
  device,
  tracker,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
}) {
  const { config } = useConfig();

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
      <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
        {device && device.hardwareStatus && (
          <>
            {device.hardwareStatus.batteryPctEstimate != null && (
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
                  rssi={device.hardwareStatus.rssi}
                  rssiShowNumeric={config?.debug}
                  ping={device.hardwareStatus.ping}
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
  warning,
}: {
  tracker: TrackerDataT;
  device?: DeviceDataT;
  warning?: TrackingChecklistStepT | boolean;
}) {
  const { useName } = useTracker(tracker);

  const trackerName = useName();

  return (
    <div className="flex rounded-md py-3 px-4 w-full gap-4 h-[70px]">
      <div className="flex flex-col justify-center items-center fill-background-10 relative">
        {warning && (
          <div className="absolute -right-2 -bottom-3 text-status-warning ">
            <WarningIcon width={20}></WarningIcon>
          </div>
        )}
        <div
          className={classNames(
            'border-[3px] border-opacity-80 rounded-md overflow-clip',
            {
              'border-status-warning': warning,
              'border-transparent': !warning,
            }
          )}
        >
          <BodyPartIcon
            bodyPart={tracker.info?.bodyPart}
            width={40}
          ></BodyPartIcon>
        </div>
      </div>

      <div className="flex flex-col flex-grow justify-center gap-1">
        <Typography bold truncate variant="section-title">
          {trackerName}
        </Typography>
        <TrackerStatus status={tracker.status}></TrackerStatus>
      </div>
      {device && device.hardwareStatus && (
        <>
          <div className="flex flex-col justify-center items-center">
            {device.hardwareStatus.batteryPctEstimate != null && (
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
                rssi={device.hardwareStatus.rssi}
                ping={device.hardwareStatus.ping}
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
  warning?: TrackingChecklistStepT | boolean;
  showUpdates?: boolean;
}) {
  const { currentFirmwareRelease } = useAppContext();
  const { useVelocity } = useTracker(tracker);
  const velocity = useVelocity();

  const showUpdate =
    showUpdates &&
    tracker.status !== TrackerStatusEnum.DISCONNECTED &&
    currentFirmwareRelease &&
    device &&
    checkForUpdate(currentFirmwareRelease, device);
  return (
    <div className="relative">
      <div
        onClick={onClick}
        className={classNames(
          'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear',
          interactable && 'hover:bg-background-50 cursor-pointer',
          outlined && 'outline outline-2 outline-accent-background-40',
          // warning &&
          //   'outline outline-2 -outline-offset-2 outline-status-warning',
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
        {smol && (
          <Tooltip
            preferedDirection="top"
            disabled={!warning}
            spacing={5}
            content={
              typeof warning === 'object' && (
                <div className="flex gap-1 items-center text-status-warning">
                  <WarningIcon width={20}></WarningIcon>
                  <Typography id={trackingchecklistIdtoLabel[warning.id]} />
                </div>
              )
            }
          >
            <TrackerSmol
              tracker={tracker}
              device={device}
              warning={warning}
            ></TrackerSmol>
          </Tooltip>
        )}
        {!smol && <TrackerBig tracker={tracker} device={device}></TrackerBig>}
      </div>
      {showUpdate &&
        showUpdate !== 'unavailable' &&
        showUpdate !== 'updated' && (
          <UpdateIcon showUpdate={showUpdate}></UpdateIcon>
        )}
    </div>
  );
}
