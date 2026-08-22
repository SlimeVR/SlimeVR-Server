import { Localized, useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { ReactNode, useEffect, useMemo, useState } from 'react';
import { useAtomValue } from 'jotai';
import {
  TrackerProvisioningStatus,
  TrackingChecklistPublicNetworksT,
  TrackingChecklistStepId,
  WifiScanStatus,
} from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import {
  useWifiCredsForm,
  useWifiProvisioningSession,
  useWifiScan,
} from '@/hooks/wifi-form';
import { useTrackingChecklist } from '@/hooks/tracking-checklist';
import { useBreakpoint } from '@/hooks/breakpoint';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { A } from '@/components/commons/A';
import { Button } from '@/components/commons/Button';
import { Dropdown } from '@/components/commons/Dropdown';
import { Input } from '@/components/commons/Input';
import { TipBox, WarningBox } from '@/components/commons/TipBox';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { ArrowLink } from '@/components/commons/ArrowLink';
import { ResetIcon } from '@/components/commons/icon/ResetIcon';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { MoreSetsConfirm } from '@/components/onboarding/pages/quiz/MoreSetsConfirm';
import {
  DOCS_TROUBLESHOOTING_URL,
  mergeTrackerList,
  NoSerialLogsModal,
  TrackerList,
} from './TrackerList';

export function FloatingWifiForm({
  summary,
  collapseOn,
  autoOpenOn,
  statusDot,
  alonePage = false,
  children,
}: {
  summary: ReactNode;
  collapseOn?: unknown;
  autoOpenOn?: unknown;
  statusDot?: ReactNode;
  alonePage?: boolean;
  children: ReactNode;
}) {
  const [isOpen, setIsOpen] = useState(() => Boolean(autoOpenOn));

  useEffect(() => {
    if (collapseOn) setIsOpen(false);
  }, [collapseOn]);

  useEffect(() => {
    if (autoOpenOn) setIsOpen(true);
  }, [autoOpenOn]);

  return (
    <>
      <div
        className={classNames(
          'fixed inset-0 bg-black/50 z-30 transition-opacity duration-200',
          isOpen
            ? 'opacity-100 pointer-events-auto'
            : 'opacity-0 pointer-events-none'
        )}
        onClick={() => setIsOpen(false)}
      />

      <div
        className={classNames(
          'fixed inset-x-3 z-40',
          alonePage ? 'bottom-[70px]' : 'bottom-3'
        )}
      >
        <div className="rounded-xl bg-background-60 border border-background-50/60 sentry-mask overflow-hidden">
          <button
            type="button"
            className="w-full flex items-center justify-between gap-3 px-4 py-3 bg-background-60 hover:bg-background-50/50 transition-colors"
            onClick={() => setIsOpen(!isOpen)}
          >
            <div className="flex items-center gap-3 min-w-0 flex-1 text-left">
              {statusDot}
              {summary}
            </div>
            <div className="fill-background-10 shrink-0">
              {isOpen ? <ArrowDownIcon size={16} /> : <ArrowUpIcon size={16} />}
            </div>
          </button>
          <div
            className={classNames(
              'overflow-y-auto px-3 transition-all duration-200',
              isOpen ? 'pb-3 pt-1' : 'max-h-0'
            )}
            style={isOpen ? { maxHeight: 'calc(80vh - 100px)' } : undefined}
          >
            {children}
          </div>
        </div>
      </div>
    </>
  );
}

function RescanButton({
  onClick,
  loading,
}: {
  onClick: () => void;
  loading?: boolean;
}) {
  return (
    <Tooltip
      content={
        <Typography variant="standard" id="onboarding-wifi_creds-rescan" />
      }
      preferedDirection="top"
    >
      <button
        type="button"
        disabled={loading}
        className="w-7 h-7 rounded-md flex items-center justify-center fill-background-10 hover:bg-background-60 transition-all disabled:opacity-50"
        onClick={onClick}
      >
        {loading ? (
          <div className="w-3.5 h-3.5 rounded-full border-2 border-background-10 border-t-transparent animate-spin" />
        ) : (
          <ResetIcon size={16} className="fill-none stroke-background-10" />
        )}
      </button>
    </Tooltip>
  );
}

function WifiFormFields({
  scan,
  credsForm,
  session,
  manualEntry,
  setManualEntry,
  allTrackersDone,
}: {
  scan: ReturnType<typeof useWifiScan>;
  credsForm: ReturnType<typeof useWifiCredsForm>;
  session: ReturnType<typeof useWifiProvisioningSession>;
  manualEntry: boolean;
  setManualEntry: (val: boolean) => void;
  allTrackersDone: boolean;
}) {
  const { l10n } = useLocalization();
  const { control, handleSubmit, submitWifiCreds, formState } = credsForm;
  const { networks, scanStatus, retryScan } = scan;
  const { submit, hasSubmitted, credentialsChanged } = session;

  const showDropdown =
    !manualEntry && scanStatus !== WifiScanStatus.UNSUPPORTED;

  const isScanning =
    scanStatus === WifiScanStatus.SERIAL_INIT ||
    scanStatus === WifiScanStatus.SCANNING;

  const scanErrorNotice = useMemo(() => {
    switch (scanStatus) {
      case WifiScanStatus.NO_SERIAL_DEVICE_FOUND:
        return l10n.getString('onboarding-wifi_creds-scan_error_no_device');
      case WifiScanStatus.NO_SERIAL_LOGS_ERROR:
        return l10n.getString('onboarding-wifi_creds-scan_error_no_logs');
      case WifiScanStatus.CONNECTION_ERROR:
        return l10n.getString('onboarding-wifi_creds-scan_failed');
      case WifiScanStatus.UNSUPPORTED:
        return l10n.getString('onboarding-wifi_creds-scan_unsupported');
      default:
        return null;
    }
  }, [scanStatus, l10n]);

  const dropdownPlaceholder = useMemo(
    () =>
      isScanning && networks.length === 0
        ? l10n.getString('onboarding-wifi_creds-scanning')
        : scanErrorNotice && networks.length === 0
          ? scanErrorNotice
          : networks.length === 0
            ? l10n.getString('onboarding-wifi_creds-scan_idle')
            : l10n.getString('onboarding-wifi_creds-ssid-scan'),
    [isScanning, networks.length, scanErrorNotice, l10n]
  );

  const dropdownItems = useMemo(
    () =>
      networks.map((network) => ({
        value: (network.ssid as string) || '',
        label: (network.ssid as string) || '(hidden network)',
      })),
    [networks]
  );

  const isScanComplete =
    manualEntry ||
    scanStatus === WifiScanStatus.RESULTS ||
    networks.length > 0 ||
    scanStatus === WifiScanStatus.UNSUPPORTED;

  const onSubmit = handleSubmit((data) => {
    submitWifiCreds(data);
    submit(data);
  });

  return (
    <form onSubmit={onSubmit} className="w-full">
      <div className="flex flex-col gap-4 w-full sentry-mask">
        <div className="flex flex-col gap-2">
          <div className="flex flex-col gap-1">
            <div className="flex items-center justify-between min-h-[28px]">
              <Typography id="onboarding-wifi_creds-ssid-label" />
              {showDropdown && (
                <RescanButton onClick={retryScan} loading={isScanning} />
              )}
            </div>
            {showDropdown ? (
              <Localized
                id="onboarding-wifi_creds-ssid-scan"
                attrs={{ placeholder: true }}
              >
                <Dropdown
                  control={control}
                  rules={{ required: true }}
                  name="ssid"
                  display="block"
                  variant="secondary"
                  direction="down"
                  placeholder={dropdownPlaceholder}
                  items={dropdownItems}
                  loading={isScanning}
                />
              </Localized>
            ) : (
              <>
                <TipBox>
                  <Typography
                    color="text-accent-background-10"
                    id="onboarding-wifi_creds-network_band_tip"
                  />
                </TipBox>
                <Localized
                  id="onboarding-wifi_creds-ssid"
                  attrs={{ placeholder: true }}
                >
                  <Input
                    control={control}
                    rules={{ required: true }}
                    name="ssid"
                    type="text"
                    placeholder="ssid"
                    variant="secondary"
                  />
                </Localized>
              </>
            )}
            <div className="min-h-[24px] flex items-center pt-1">
              <button
                type="button"
                className="text-xs text-left underline decoration-dotted text-background-20 hover:text-background-10"
                onClick={() => setManualEntry(!manualEntry)}
              >
                <Localized
                  id={
                    manualEntry
                      ? 'onboarding-wifi_creds-use_scanned'
                      : 'onboarding-wifi_creds-enter_manually'
                  }
                >
                  <span>
                    {manualEntry ? 'Use scanned networks' : 'Enter manually'}
                  </span>
                </Localized>
              </button>
            </div>
          </div>
          <Localized
            id="onboarding-wifi_creds-password"
            attrs={{ placeholder: true, label: true }}
          >
            <Input
              control={control}
              rules={{
                validate: {
                  validPassword: (v: string | undefined) =>
                    !v || new TextEncoder().encode(v).length >= 8, // WPA2 min password is 8 bytes
                },
              }}
              name="password"
              type="password"
              label="Password"
              placeholder="password"
              variant="secondary"
            />
          </Localized>
        </div>
        <Button
          type="submit"
          variant="primary"
          disabled={
            !formState.isValid ||
            (!manualEntry && (isScanning || !isScanComplete)) ||
            (hasSubmitted && allTrackersDone && !credentialsChanged)
          }
          className="self-end mt-1"
          id={
            hasSubmitted
              ? 'onboarding-wifi_creds-retry'
              : 'onboarding-wifi_creds-submit'
          }
        />
      </div>
    </form>
  );
}

function TroubleshootingLinks({
  networkProfileBanner,
}: {
  networkProfileBanner: React.ReactNode;
}) {
  return (
    <>
      <ArrowLink
        to={DOCS_TROUBLESHOOTING_URL}
        direction="right"
        variant="boxed-2"
      >
        <Typography id="onboarding-connect_tracker-issue-serial" />
      </ArrowLink>
      {networkProfileBanner}
    </>
  );
}

export function ConnectTrackersPage() {
  const { l10n } = useLocalization();
  const { visibleSteps, toggleSession } = useTrackingChecklist();
  const { applyProgress, state } = useOnboarding();
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);
  const { isMobile } = useBreakpoint('mobile');

  const credsForm = useWifiCredsForm();
  const scan = useWifiScan();
  const session = useWifiProvisioningSession(credsForm.control);

  const { networks, scanStatus } = scan;
  const { trackers: provisioningTrackers, hasSubmitted } = session;

  const [manualEntry, setManualEntry] = useState(false);
  const [showNoSerialLogsModal, setShowNoSerialLogsModal] = useState(false);

  const rows = useMemo(
    () =>
      hasSubmitted
        ? mergeTrackerList(provisioningTrackers, connectedIMUTrackers)
        : mergeTrackerList(provisioningTrackers, []),
    [hasSubmitted, provisioningTrackers, connectedIMUTrackers]
  );

  const totalTrackersCount = rows.length;
  const connectedCount = rows.filter((r) => r.kind === 'connected').length;
  const connectingCount = rows.filter(
    (row) =>
      row.kind === 'provisioning' &&
      row.tracker.status !== TrackerProvisioningStatus.DONE
  ).length;
  const allTrackersDone = totalTrackersCount > 0 && connectingCount === 0;

  applyProgress(0.4);

  const invalidNetworkProfile = useMemo(() => {
    return visibleSteps.find(
      (step) =>
        step.id === TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC &&
        !step.valid
    );
  }, [visibleSteps]);

  const networkProfileBanner = invalidNetworkProfile && (
    <WarningBox whitespace>
      <div className="flex flex-col gap-4">
        <Typography
          color="text-bg-background-90"
          whitespace="whitespace-pre-wrap"
          id="tracking_checklist-NETWORK_PROFILE_PUBLIC-desc"
          elems={{
            PublicFixLink: (
              <A href="https://docs.slimevr.dev/common-issues.html#network-profile-is-currently-set-to-public" />
            ),
          }}
          vars={{
            count: (
              invalidNetworkProfile.extraData as TrackingChecklistPublicNetworksT
            ).adapters.length,
            adapters: (
              invalidNetworkProfile.extraData as TrackingChecklistPublicNetworksT
            ).adapters.join(', '),
          }}
        />
        <button
          type="button"
          className="text-xs text-left underline decoration-dotted text-background-60 hover:text-background-30"
          onClick={() =>
            toggleSession(TrackingChecklistStepId.NETWORK_PROFILE_PUBLIC)
          }
        >
          {l10n.getString('onboarding-connect_tracker-network_profile-ignore')}
        </button>
      </div>
    </WarningBox>
  );

  const wifiFormFieldsProps = {
    scan,
    credsForm,
    session,
    manualEntry,
    setManualEntry,
    allTrackersDone,
  };

  return (
    <>
      <div className="w-full h-full flex flex-col xs:flex-row overflow-hidden min-h-0">
        <div className="w-full xs:w-[340px] sm:w-[380px] lg:w-[400px] xl:w-[440px] p-5 flex flex-col gap-4 shrink-0 overflow-y-auto border-b xs:border-b-0 xs:border-r border-background-60">
          <div className="flex flex-col gap-1">
            <Typography
              variant="main-title"
              id="onboarding-connect_tracker-title"
            />
            <Typography
              variant="standard"
              whitespace="whitespace-pre-wrap"
              id="onboarding-connect_tracker-description"
            />
          </div>

          {!isMobile && (
            <>
              <WifiFormFields {...wifiFormFieldsProps} />
              <TipBox>
                <Typography
                  variant="standard"
                  color="text-accent-background-10"
                  id="onboarding-connect_tracker-shake_tip"
                />
              </TipBox>
              <TroubleshootingLinks
                networkProfileBanner={networkProfileBanner}
              />
            </>
          )}
        </div>

        <div className="flex-1 flex flex-col gap-4 p-5 min-h-0 overflow-hidden justify-between mobile:pb-28">
          <div className="flex flex-col gap-4 min-h-0 flex-1">
            <TrackerList
              rows={rows}
              scanStatus={scanStatus}
              networks={networks}
              onOpenNoSerialLogsModal={() => setShowNoSerialLogsModal(true)}
            />
            {isMobile && (
              <div className="pt-2 shrink-0">
                <TroubleshootingLinks
                  networkProfileBanner={networkProfileBanner}
                />
              </div>
            )}
          </div>
          <div className="flex flex-row justify-between items-center shrink-0 pt-3 border-t border-background-60">
            <Button
              variant="secondary"
              state={{ alonePage: state.alonePage }}
              to={
                state.alonePage
                  ? '/onboarding/add-trackers'
                  : '/onboarding/quiz/slime-set'
              }
              id="onboarding-wifi_creds-back-v2"
            />
            {state.alonePage ? (
              <Button
                variant="primary"
                to="/"
                id="onboarding-connect_tracker-next"
              />
            ) : (
              <MoreSetsConfirm />
            )}
          </div>
        </div>
      </div>

      {isMobile && (
        <FloatingWifiForm
          alonePage={state.alonePage}
          summary={
            <div className="flex flex-col min-w-0">
              <Typography
                bold
                truncate
                id={
                  totalTrackersCount === 0
                    ? scanStatus === WifiScanStatus.RESULTS ||
                      networks.length > 0
                      ? 'onboarding-connect_tracker-scan_results_title'
                      : 'onboarding-connect_tracker-waiting_first_title'
                    : 'onboarding-connect_tracker-connected_trackers'
                }
                vars={{ amount: connectedCount }}
              />
              <Typography
                variant="standard"
                truncate
                id={
                  totalTrackersCount === 0
                    ? scanStatus === WifiScanStatus.RESULTS ||
                      networks.length > 0
                      ? 'onboarding-connect_tracker-scan_results_desc'
                      : 'onboarding-connect_tracker-waiting_first_desc'
                    : connectingCount > 0
                      ? 'onboarding-connect_tracker-setting_up'
                      : 'onboarding-connect_tracker-all_caught_up'
                }
                vars={{ amount: connectingCount }}
              />
            </div>
          }
          statusDot={
            <div
              className={classNames(
                'w-3.5 h-3.5 rounded-full shrink-0',
                totalTrackersCount === 0
                  ? scanStatus === WifiScanStatus.RESULTS || networks.length > 0
                    ? 'bg-status-warning animate-pulse'
                    : 'bg-background-40'
                  : connectingCount > 0
                    ? 'bg-status-warning animate-pulse'
                    : 'bg-status-success'
              )}
            />
          }
          collapseOn={hasSubmitted}
          autoOpenOn={
            totalTrackersCount > 0 ||
            provisioningTrackers.length > 0 ||
            scanStatus === WifiScanStatus.SERIAL_INIT ||
            scanStatus === WifiScanStatus.SCANNING ||
            scanStatus === WifiScanStatus.RESULTS ||
            networks.length > 0
          }
        >
          <WifiFormFields {...wifiFormFieldsProps} />
        </FloatingWifiForm>
      )}

      <NoSerialLogsModal
        isOpen={showNoSerialLogsModal}
        onClose={() => setShowNoSerialLogsModal(false)}
      />
    </>
  );
}
