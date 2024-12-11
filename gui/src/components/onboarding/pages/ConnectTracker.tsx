import { Localized, useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  RpcMessage,
  StartWifiProvisioningRequestT,
  StopWifiProvisioningRequestT,
  WifiProvisioningStatus,
  WifiProvisioningStatusResponseT,
} from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { useTrackers } from '@/hooks/tracker';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { ArrowLink } from '@/components/commons/ArrowLink';
import { Button } from '@/components/commons/Button';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { TrackerCard } from '@/components/tracker/TrackerCard';
import { useIsRestCalibrationTrackers } from '@/hooks/imu-logic';
import './ConnectTracker.scss';

const statusLabelMap = {
  [WifiProvisioningStatus.NONE]:
    'onboarding-connect_tracker-connection_status-none',
  [WifiProvisioningStatus.SERIAL_INIT]:
    'onboarding-connect_tracker-connection_status-serial_init',
  [WifiProvisioningStatus.OBTAINING_MAC_ADDRESS]:
    'onboarding-connect_tracker-connection_status-obtaining_mac_address',
  [WifiProvisioningStatus.PROVISIONING]:
    'onboarding-connect_tracker-connection_status-provisioning',
  [WifiProvisioningStatus.CONNECTING]:
    'onboarding-connect_tracker-connection_status-connecting',
  [WifiProvisioningStatus.LOOKING_FOR_SERVER]:
    'onboarding-connect_tracker-connection_status-looking_for_server',
  [WifiProvisioningStatus.DONE]:
    'onboarding-connect_tracker-connection_status-done',
  [WifiProvisioningStatus.CONNECTION_ERROR]:
    'onboarding-connect_tracker-connection_status-connection_error',
  [WifiProvisioningStatus.COULD_NOT_FIND_SERVER]:
    'onboarding-connect_tracker-connection_status-could_not_find_server',
};

const statusProgressMap = {
  [WifiProvisioningStatus.NONE]: 0,
  [WifiProvisioningStatus.SERIAL_INIT]: 0.2,
  [WifiProvisioningStatus.OBTAINING_MAC_ADDRESS]: 0.3,
  [WifiProvisioningStatus.PROVISIONING]: 0.4,
  [WifiProvisioningStatus.CONNECTING]: 0.6,
  [WifiProvisioningStatus.LOOKING_FOR_SERVER]: 0.8,
  [WifiProvisioningStatus.DONE]: 1,
  [WifiProvisioningStatus.CONNECTION_ERROR]: 0.6,
  [WifiProvisioningStatus.COULD_NOT_FIND_SERVER]: 0.8,
};

export function ConnectTrackersPage() {
  const { l10n } = useLocalization();
  const { useConnectedIMUTrackers } = useTrackers();
  const { applyProgress, state } = useOnboarding();
  const navigate = useNavigate();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [provisioningStatus, setProvisioningStatus] =
    useState<WifiProvisioningStatus>(WifiProvisioningStatus.NONE);

  applyProgress(0.4);

  const connectedIMUTrackers = useConnectedIMUTrackers();

  const bnoExists = useIsRestCalibrationTrackers(connectedIMUTrackers);

  useEffect(() => {
    if (!state.wifi) {
      navigate('/onboarding/wifi-creds');
    }

    const req = new StartWifiProvisioningRequestT();
    req.ssid = state.wifi?.ssid as string;
    req.password = state.wifi?.password as string;

    sendRPCPacket(RpcMessage.StartWifiProvisioningRequest, req);
    return () => {
      sendRPCPacket(
        RpcMessage.StopWifiProvisioningRequest,
        new StopWifiProvisioningRequestT()
      );
    };
  }, []);

  useRPCPacket(
    RpcMessage.WifiProvisioningStatusResponse,
    ({ status }: WifiProvisioningStatusResponseT) => {
      setProvisioningStatus(status);
    }
  );

  const isError =
    provisioningStatus === WifiProvisioningStatus.CONNECTION_ERROR ||
    provisioningStatus === WifiProvisioningStatus.COULD_NOT_FIND_SERVER;

  const progressBarClass = useMemo(() => {
    if (isError) {
      return 'bg-status-critical';
    }

    if (provisioningStatus === WifiProvisioningStatus.DONE) {
      return 'bg-status-success';
    }
  }, [provisioningStatus]);

  const slimeStatus = useMemo(() => {
    if (isError) {
      return SlimeState.SAD;
    }

    switch (provisioningStatus) {
      case WifiProvisioningStatus.DONE:
        return SlimeState.HAPPY;
      case WifiProvisioningStatus.NONE:
        return SlimeState.CURIOUS;
      default:
        return SlimeState.JUMPY;
    }
  }, [provisioningStatus]);

  const currentTip = useMemo(
    () =>
      connectedIMUTrackers.length > 0
        ? 'tips-find_tracker'
        : 'tips-turn_on_tracker',
    [connectedIMUTrackers.length]
  );

  return (
    <div className="connect-tracker-layout h-full">
      <div style={{ gridArea: 's' }} className="p-4">
        <Typography variant="main-title">
          {l10n.getString('onboarding-connect_tracker-title')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-connect_tracker-description-p0-v1')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('onboarding-connect_tracker-description-p1-v1')}
        </Typography>
        <div className="flex flex-col gap-2 py-5">
          <ArrowLink
            to="/settings/serial"
            state={{ SerialPort: 'Auto' }}
            direction="right"
            variant={state.alonePage ? 'boxed-2' : 'boxed'}
          >
            {l10n.getString('onboarding-connect_tracker-issue-serial')}
          </ArrowLink>
        </div>
        <Localized
          id={currentTip}
          elems={{ em: <em className="italic"></em>, b: <b></b> }}
        >
          <TipBox>Conditional tip</TipBox>
        </Localized>

        <div
          className={classNames(
            'rounded-xl h-24 flex gap-2 p-3 lg:w-full mt-4 relative',
            state.alonePage ? 'bg-background-60' : 'bg-background-70',
            isError && 'border-2 border-status-critical'
          )}
        >
          <div
            className={classNames(
              'flex flex-col justify-center fill-background-10 absolute',
              'right-5 bottom-8'
            )}
          >
            <LoaderIcon slimeState={slimeStatus}></LoaderIcon>
          </div>

          <div className="flex flex-col grow self-center">
            <Typography bold>
              {l10n.getString('onboarding-connect_tracker-usb')}
            </Typography>
            <div className="flex fill-background-10 gap-1">
              <Typography color="secondary">
                {l10n.getString(statusLabelMap[provisioningStatus])}
              </Typography>
            </div>
            <ProgressBar
              progress={statusProgressMap[provisioningStatus]}
              height={14}
              animated={true}
              colorClass={progressBarClass}
            ></ProgressBar>
          </div>
        </div>
        <div className="flex flex-row mt-4 gap-3">
          <Button
            variant="secondary"
            state={{ alonePage: state.alonePage }}
            to="/onboarding/wifi-creds"
          >
            {state.alonePage
              ? l10n.getString('onboarding-connect_tracker-back')
              : l10n.getString('onboarding-previous_step')}
          </Button>
          <Button
            variant="primary"
            to={
              state.alonePage
                ? '/'
                : bnoExists
                  ? '/onboarding/calibration-tutorial'
                  : '/onboarding/assign-tutorial'
            }
            className="ml-auto"
          >
            {l10n.getString('onboarding-connect_tracker-next')}
          </Button>
        </div>
      </div>
      <div style={{ gridArea: 't' }} className="flex items-center px-5">
        <Typography color="secondary" bold>
          {l10n.getString('onboarding-connect_tracker-connected_trackers', {
            amount: connectedIMUTrackers.length,
          })}
        </Typography>
      </div>
      <div style={{ gridArea: 'c' }} className="xs:overflow-y-auto">
        <div className="grid lg:grid-cols-2 md:grid-cols-1 gap-2 pr-1 mx-5 py-4">
          {Array.from({
            ...connectedIMUTrackers,
            length: Math.max(connectedIMUTrackers.length, 1),
          }).map((tracker, index) => (
            <div key={index}>
              {!tracker && (
                <div
                  className={classNames(
                    'rounded-xl h-16 animate-pulse',
                    state.alonePage ? 'bg-background-80' : 'bg-background-70'
                  )}
                ></div>
              )}
              {tracker && (
                <TrackerCard
                  tracker={tracker.tracker}
                  device={tracker.device}
                  smol
                ></TrackerCard>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
