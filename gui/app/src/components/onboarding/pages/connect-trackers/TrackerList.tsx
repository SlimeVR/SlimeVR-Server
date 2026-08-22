import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useMemo, useState } from 'react';
import {
  DeviceDataT,
  TrackerProvisioningStateT,
  TrackerProvisioningStatus,
  TrackerStatus,
  WifiNetworkT,
  WifiScanStatus,
} from 'solarxr-protocol';
import { useTracker } from '@/hooks/tracker';
import { useBreakpoint } from '@/hooks/breakpoint';
import { FlatDeviceTracker } from '@/store/app-store';
import { BaseModal } from '@/components/commons/BaseModal';
import { A } from '@/components/commons/A';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { TipBox } from '@/components/commons/TipBox';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { TrackerBattery } from '@/components/tracker/TrackerBattery';
import { TrackerWifi } from '@/components/tracker/TrackerWifi';

export const DOCS_TROUBLESHOOTING_URL =
  'https://docs.slimevr.dev/common-issues.html';

export const trackerStatusLabelMap: Record<TrackerProvisioningStatus, string> =
  {
    [TrackerProvisioningStatus.SERIAL_INIT]:
      'onboarding-connect_tracker-connection_status-serial_init',
    [TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS]:
      'onboarding-connect_tracker-connection_status-obtaining_mac_address',
    [TrackerProvisioningStatus.PROVISIONING]:
      'onboarding-connect_tracker-connection_status-provisioning',
    [TrackerProvisioningStatus.CONNECTING]:
      'onboarding-connect_tracker-connection_status-connecting',
    [TrackerProvisioningStatus.LOOKING_FOR_SERVER]:
      'onboarding-connect_tracker-connection_status-looking_for_server',
    [TrackerProvisioningStatus.DONE]:
      'onboarding-connect_tracker-connection_status-done',
    [TrackerProvisioningStatus.CONNECTION_ERROR]:
      'onboarding-connect_tracker-connection_status-connection_error',
    [TrackerProvisioningStatus.COULD_NOT_FIND_SERVER]:
      'onboarding-connect_tracker-connection_status-could_not_find_server',
    [TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR]:
      'onboarding-connect_tracker-connection_status-no_serial_log',
  };

const trackerStatusProgressMap: Record<TrackerProvisioningStatus, number> = {
  [TrackerProvisioningStatus.SERIAL_INIT]: 0.2,
  [TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS]: 0.3,
  [TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR]: 0.3,
  [TrackerProvisioningStatus.PROVISIONING]: 0.4,
  [TrackerProvisioningStatus.CONNECTING]: 0.6,
  [TrackerProvisioningStatus.LOOKING_FOR_SERVER]: 0.8,
  [TrackerProvisioningStatus.DONE]: 1,
  [TrackerProvisioningStatus.CONNECTION_ERROR]: 0.6,
  [TrackerProvisioningStatus.COULD_NOT_FIND_SERVER]: 0.8,
};

const TRACKER_ERROR_STATUSES = [
  TrackerProvisioningStatus.CONNECTION_ERROR,
  TrackerProvisioningStatus.COULD_NOT_FIND_SERVER,
  TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR,
];

const trackerStatusDescMap: Partial<Record<TrackerProvisioningStatus, string>> =
  {
    [TrackerProvisioningStatus.CONNECTION_ERROR]:
      'onboarding-connect_tracker-connection_error-desc',
    [TrackerProvisioningStatus.COULD_NOT_FIND_SERVER]:
      'onboarding-connect_tracker-could_not_find_server-desc',
    [TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR]:
      'onboarding-connect_serial-error-modal-no_serial_log-desc',
  };

export function TrackerRow({
  title,
  labelId,
  progress = 1,
  isError = false,
  isConnected = false,
  isOffline = false,
  descId,
  docsAnchor,
  onLearnMore,
  velocity = 0,
  device,
}: {
  title: string;
  labelId: string;
  progress?: number;
  isError?: boolean;
  isConnected?: boolean;
  isOffline?: boolean;
  descId?: string;
  docsAnchor?: string;
  onLearnMore?: () => void;
  velocity?: number;
  device?: DeviceDataT;
}) {
  const [modalOpen, setModalOpen] = useState(false);
  const pct = Math.round(progress * 100);

  const shakeShadow =
    velocity > 0
      ? {
          boxShadow: `0px 0px ${Math.floor(velocity * 8)}px ${Math.floor(
            velocity * 8
          )}px rgb(var(--accent-background-30))`,
        }
      : {};

  return (
    <>
      <div
        className={classNames(
          'relative flex flex-col justify-between rounded-lg bg-background-60 overflow-hidden w-full transition-[box-shadow,opacity] duration-200 ease-linear shrink-0',
          isOffline && 'opacity-50'
        )}
        style={shakeShadow}
      >
        <div className="flex items-center gap-3 w-full px-4 py-3 min-h-[46px]">
          <div
            className={classNames(
              'w-3 h-3 rounded-full shrink-0',
              isError
                ? 'bg-status-critical'
                : isConnected
                  ? 'bg-status-success'
                  : isOffline
                    ? 'bg-background-30'
                    : 'bg-status-warning animate-pulse'
            )}
          />
          <div className="flex flex-col flex-grow justify-center min-w-0">
            <Typography bold truncate variant="section-title">
              {title}
            </Typography>
            <Typography
              variant="standard"
              color={
                isError
                  ? 'text-status-critical'
                  : isConnected
                    ? 'text-status-success'
                    : undefined
              }
              id={labelId}
            />
          </div>

          {isError ? (
            <button
              type="button"
              className="text-xs text-status-critical underline font-medium shrink-0"
              onClick={() => {
                if (onLearnMore) {
                  onLearnMore();
                } else {
                  setModalOpen(true);
                }
              }}
            >
              <Typography id="onboarding-connect_tracker-learn_more" />
            </button>
          ) : isConnected ? (
            <div className="flex items-center gap-3 shrink-0">
              {device?.hardwareStatus && (
                <>
                  {device.hardwareStatus.batteryPctEstimate != null && (
                    <TrackerBattery
                      voltage={device.hardwareStatus.batteryVoltage}
                      value={device.hardwareStatus.batteryPctEstimate / 100}
                      runtime={device.hardwareStatus.batteryRuntimeEstimate}
                    />
                  )}
                  {(device.hardwareStatus.rssi != null ||
                    device.hardwareStatus.ping != null) && (
                    <TrackerWifi
                      rssi={device.hardwareStatus.rssi}
                      ping={device.hardwareStatus.ping}
                    />
                  )}
                </>
              )}
            </div>
          ) : isOffline ? null : (
            <span className="text-xs font-mono font-semibold text-background-30 shrink-0">
              {pct}%
            </span>
          )}
        </div>

        {!isError && !isOffline && (
          <div className="absolute bottom-0 left-0 right-0 h-1 bg-background-90 overflow-hidden">
            <div
              className={classNames(
                'h-full transition-all duration-300',
                isConnected ? 'bg-status-success' : 'bg-accent-background-20'
              )}
              style={{ width: `${pct}%` }}
            />
          </div>
        )}
      </div>

      {isError && (
        <ErrorDetailModal
          isOpen={modalOpen}
          onClose={() => setModalOpen(false)}
          titleId={labelId}
          descId={descId}
          docsAnchor={docsAnchor}
        />
      )}
    </>
  );
}

export function TrackerProvisioningRow({
  tracker,
  onOpenNoSerialLogsModal,
}: {
  tracker: TrackerProvisioningStateT;
  onOpenNoSerialLogsModal?: () => void;
}) {
  const { l10n } = useLocalization();
  const isError = TRACKER_ERROR_STATUSES.includes(tracker.status);
  const isDone = tracker.status === TrackerProvisioningStatus.DONE;
  const isNoSerialLogsError =
    tracker.status === TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR;

  const docsAnchor =
    tracker.status === TrackerProvisioningStatus.CONNECTION_ERROR
      ? '#tracker-cant-connect-to-wifi'
      : tracker.status === TrackerProvisioningStatus.COULD_NOT_FIND_SERVER
        ? '#the-trackers-are-connected-to-wi-fi-but-cant-find-the-server'
        : isNoSerialLogsError
          ? '#no-serial-device-appears--looking-for-trackers--connection-to-serial-lost-reconnecting'
          : undefined;

  const rawMac = tracker.macAddress as string | null | undefined;
  const rawPort = tracker.port as string | null | undefined;
  const title = rawMac
    ? l10n.getString('onboarding-connect_tracker-tracker_mac_name', {
        suffix: rawMac.replace(/[:-]/g, '').toUpperCase().slice(-4),
      })
    : rawPort
      ? l10n.getString('onboarding-connect_tracker-tracker_port_name', {
          port: rawPort.replace('/dev/', ''),
        })
      : l10n.getString('onboarding-connect_tracker-usb');

  return (
    <TrackerRow
      title={title}
      labelId={trackerStatusLabelMap[tracker.status]}
      progress={trackerStatusProgressMap[tracker.status]}
      isError={isError}
      isConnected={isDone}
      descId={trackerStatusDescMap[tracker.status]}
      docsAnchor={docsAnchor}
      onLearnMore={isNoSerialLogsError ? onOpenNoSerialLogsModal : undefined}
    />
  );
}

export function ConnectedTrackerRow({
  tracker,
}: {
  tracker: FlatDeviceTracker;
}) {
  const { useName, useVelocity } = useTracker(tracker.tracker);
  const name = useName();
  const velocity = useVelocity();
  const isTimedOut = tracker.tracker.status === TrackerStatus.TIMED_OUT;

  return (
    <TrackerRow
      title={(name as string) || ''}
      labelId={
        isTimedOut
          ? 'tracker-status-timed_out'
          : trackerStatusLabelMap[TrackerProvisioningStatus.DONE]
      }
      isOffline={isTimedOut}
      isConnected={!isTimedOut}
      velocity={velocity}
      device={tracker.device}
    />
  );
}

export function ErrorDetailModal({
  isOpen,
  onClose,
  titleId,
  descId,
  docsAnchor,
}: {
  isOpen: boolean;
  onClose: () => void;
  titleId: string;
  descId?: string;
  docsAnchor?: string;
}) {
  return (
    <BaseModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="max-w-md w-full p-6 flex flex-col gap-4 bg-background-70 rounded-xl border border-background-60"
    >
      <div className="flex flex-col items-center text-center gap-3">
        <LoaderIcon slimeState={SlimeState.SAD} size={56} />

        <div className="flex flex-col gap-1">
          <Typography
            bold
            variant="section-title"
            color="text-status-critical"
            id={titleId}
          />
          {descId && <Typography variant="standard" id={descId} />}
        </div>

        <div className="flex flex-row items-center justify-between w-full pt-2 gap-4">
          <A
            href={`${DOCS_TROUBLESHOOTING_URL}${docsAnchor || ''}`}
            className="text-sm font-medium text-accent-background-10 hover:underline shrink-0"
          >
            <Typography id="onboarding-connect_tracker-learn_more" />
          </A>

          <Button
            variant="secondary"
            onClick={onClose}
            className="shrink-0 px-6"
            id="onboarding-connect_tracker-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}

export function NoSerialLogsModal({
  isOpen,
  onClose,
}: {
  isOpen: boolean;
  onClose: () => void;
}) {
  return (
    <BaseModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="max-w-md w-full p-6 flex flex-col gap-4 bg-background-70 rounded-xl border border-background-60"
    >
      <div className="flex flex-col items-center text-center gap-3">
        <LoaderIcon slimeState={SlimeState.SAD} size={56} />

        <div className="flex flex-col gap-1">
          <Typography
            bold
            variant="section-title"
            color="text-status-critical"
            id="onboarding-connect_tracker-connection_status-no_serial_log"
          />
          <Typography
            variant="standard"
            id="onboarding-connect_serial-error-modal-no_serial_log-desc"
          />
        </div>

        <div className="w-full aspect-video rounded-lg overflow-hidden my-1 bg-background-90">
          <video
            src="/videos/turn-on-tracker.webm"
            autoPlay
            loop
            muted
            playsInline
            className="w-full h-full object-cover"
          />
        </div>

        <div className="flex flex-row items-center justify-between w-full pt-2 gap-4">
          <A
            href={`${DOCS_TROUBLESHOOTING_URL}#no-serial-device-appears--looking-for-trackers--connection-to-serial-lost-reconnecting`}
            className="text-sm font-medium text-accent-background-10 hover:underline shrink-0"
          >
            <Typography id="onboarding-connect_tracker-learn_more" />
          </A>

          <Button
            variant="secondary"
            onClick={onClose}
            className="shrink-0 px-6"
            id="onboarding-connect_tracker-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}

export function StatusBar({
  connectedCount,
  connectingCount,
}: {
  connectedCount: number;
  connectingCount: number;
}) {
  return (
    <div className="hidden sm:flex rounded-lg bg-background-60 p-4 items-center gap-3 shrink-0">
      <div
        className={classNames(
          'w-3.5 h-3.5 rounded-full shrink-0',
          connectingCount > 0
            ? 'bg-status-warning animate-pulse'
            : 'bg-status-success'
        )}
      />
      <div className="flex flex-col justify-center min-w-0">
        <Typography
          bold
          variant="section-title"
          color="primary"
          truncate
          id="onboarding-connect_tracker-connected_trackers"
          vars={{ amount: connectedCount }}
        />
        <Typography
          variant="standard"
          truncate
          id={
            connectingCount > 0
              ? 'onboarding-connect_tracker-setting_up'
              : 'onboarding-connect_tracker-all_caught_up'
          }
          vars={{ amount: connectingCount }}
        />
      </div>
    </div>
  );
}

function EmptyTrackerListIllustration({
  scanStatus,
  hasNetworks = false,
  onOpenNoSerialLogsModal,
}: {
  scanStatus: WifiScanStatus;
  hasNetworks?: boolean;
  onOpenNoSerialLogsModal?: () => void;
}) {
  const isError =
    scanStatus === WifiScanStatus.CONNECTION_ERROR ||
    scanStatus === WifiScanStatus.NO_SERIAL_LOGS_ERROR ||
    scanStatus === WifiScanStatus.NO_SERIAL_DEVICE_FOUND ||
    scanStatus === WifiScanStatus.UNSUPPORTED;

  const hasScanResults = scanStatus === WifiScanStatus.RESULTS || hasNetworks;

  const titleId =
    scanStatus === WifiScanStatus.NO_SERIAL_DEVICE_FOUND
      ? 'onboarding-connect_tracker-connection_status-no_serial_device_found'
      : scanStatus === WifiScanStatus.NO_SERIAL_LOGS_ERROR
        ? 'onboarding-connect_tracker-connection_status-no_serial_log'
        : scanStatus === WifiScanStatus.UNSUPPORTED
          ? 'onboarding-wifi_creds-scan_unsupported'
          : scanStatus === WifiScanStatus.CONNECTION_ERROR
            ? 'onboarding-wifi_creds-scan_failed'
            : hasScanResults
              ? 'onboarding-connect_tracker-scan_results_title'
              : 'onboarding-connect_tracker-waiting_first_title';

  const descId =
    scanStatus === WifiScanStatus.NO_SERIAL_DEVICE_FOUND
      ? 'onboarding-wifi_creds-scan_error_no_device'
      : scanStatus === WifiScanStatus.NO_SERIAL_LOGS_ERROR
        ? 'onboarding-wifi_creds-scan_error_no_logs'
        : scanStatus === WifiScanStatus.UNSUPPORTED
          ? 'onboarding-wifi_creds-scan_unsupported'
          : scanStatus === WifiScanStatus.CONNECTION_ERROR
            ? 'onboarding-wifi_creds-scan_failed'
            : hasScanResults
              ? 'onboarding-connect_tracker-scan_results_desc'
              : 'onboarding-connect_tracker-waiting_first_desc';

  return (
    <div className="flex flex-col items-center justify-center flex-1 h-full min-h-[220px] gap-3 text-center p-6 my-auto">
      <LoaderIcon
        slimeState={isError ? SlimeState.SAD : SlimeState.JUMPY}
        size={56}
      />
      <div className="flex flex-col gap-1 max-w-xs">
        <Typography
          bold
          variant="section-title"
          color={isError ? 'text-status-critical' : 'primary'}
          id={titleId}
        />
        <Typography variant="standard" id={descId} />
      </div>
      {isError && (
        <div className="pt-1">
          <button
            type="button"
            className="text-xs underline font-medium text-background-20 hover:text-background-10"
            onClick={() => {
              if (
                scanStatus === WifiScanStatus.NO_SERIAL_LOGS_ERROR &&
                onOpenNoSerialLogsModal
              ) {
                onOpenNoSerialLogsModal();
              } else {
                window.open(DOCS_TROUBLESHOOTING_URL, '_blank');
              }
            }}
          >
            <Typography id="onboarding-connect_tracker-learn_more" />
          </button>
        </div>
      )}
    </div>
  );
}

export type TrackerRow =
  | { kind: 'provisioning'; key: string; tracker: TrackerProvisioningStateT }
  | { kind: 'connected'; key: string; tracker: FlatDeviceTracker };

export function normalizeMac(mac: string | Uint8Array | null | undefined) {
  return typeof mac === 'string' && mac.length > 0
    ? mac.toUpperCase().replace(/-/g, ':')
    : undefined;
}

export function mergeTrackerList(
  provisioningTrackers: TrackerProvisioningStateT[],
  connectedIMUTrackers: FlatDeviceTracker[]
): TrackerRow[] {
  const connectedByMac = new Map<string, FlatDeviceTracker>();
  const claimedConnected = new Set<FlatDeviceTracker>();

  for (const ct of connectedIMUTrackers) {
    const mac = normalizeMac(ct.device?.hardwareInfo?.hardwareIdentifier);
    if (mac) connectedByMac.set(mac, ct);
  }

  for (const t of provisioningTrackers) {
    const mac = normalizeMac(t.macAddress);
    if (mac) {
      const match = connectedByMac.get(mac);
      if (match) claimedConnected.add(match);
    }
  }

  const rows: TrackerRow[] = [];
  for (const t of provisioningTrackers) {
    const mac = normalizeMac(t.macAddress);
    const key = (t.port as string) || mac || `prov-${t.port}`;
    const liveMatch = mac ? connectedByMac.get(mac) : undefined;

    if (t.status === TrackerProvisioningStatus.DONE && liveMatch) {
      rows.push({ kind: 'connected', key, tracker: liveMatch });
    } else {
      rows.push({ kind: 'provisioning', key, tracker: t });
    }
  }

  for (const ct of connectedIMUTrackers) {
    if (claimedConnected.has(ct)) continue;
    const mac = normalizeMac(ct.device?.hardwareInfo?.hardwareIdentifier);
    const key = mac || `connected-${ct.device?.id}`;
    rows.push({ kind: 'connected', key, tracker: ct });
  }

  return rows;
}

export function TrackerList({
  rows,
  scanStatus,
  networks,
  onOpenNoSerialLogsModal,
}: {
  rows: TrackerRow[];
  scanStatus: WifiScanStatus;
  networks: WifiNetworkT[];
  onOpenNoSerialLogsModal: () => void;
}) {
  const { isMobile } = useBreakpoint('mobile');
  const connectedCount = useMemo(
    () =>
      rows.filter(
        (row) =>
          row.kind === 'connected' ||
          row.tracker.status === TrackerProvisioningStatus.DONE
      ).length,
    [rows]
  );
  const connectingCount = rows.length - connectedCount;

  return (
    <div className="flex flex-col gap-3 min-h-0 flex-1">
      {rows.length > 0 && (
        <StatusBar
          connectedCount={connectedCount}
          connectingCount={connectingCount}
        />
      )}

      {isMobile && rows.length > 0 && (
        <TipBox>
          <Typography
            variant="standard"
            color="text-accent-background-10"
            id="onboarding-connect_tracker-shake_tip"
          />
        </TipBox>
      )}

      <div className="flex flex-col gap-2 min-h-0 flex-1 overflow-y-auto p-3 -m-3">
        {rows.length > 0 ? (
          rows.map((row) =>
            row.kind === 'connected' ? (
              <ConnectedTrackerRow key={row.key} tracker={row.tracker} />
            ) : (
              <TrackerProvisioningRow
                key={row.key}
                tracker={row.tracker}
                onOpenNoSerialLogsModal={onOpenNoSerialLogsModal}
              />
            )
          )
        ) : (
          <EmptyTrackerListIllustration
            scanStatus={scanStatus}
            hasNetworks={networks.length > 0}
            onOpenNoSerialLogsModal={onOpenNoSerialLogsModal}
          />
        )}
      </div>
    </div>
  );
}
