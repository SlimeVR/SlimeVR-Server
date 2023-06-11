import { useLocalization } from '@fluent/react';
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
import { useLayout } from '../../../hooks/layout';
import { useOnboarding } from '../../../hooks/onboarding';
import { useTrackers } from '../../../hooks/tracker';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { LoaderIcon, SlimeState } from '../../commons/icon/LoaderIcon';
import { ProgressBar } from '../../commons/ProgressBar';
import { TipBox } from '../../commons/TipBox';
import { Typography } from '../../commons/Typography';
import { TrackerCard } from '../../tracker/TrackerCard';
import { SkipSetupWarningModal } from '../SkipSetupWarningModal';
import { SkipSetupButton } from '../SkipSetupButton';
import { useBnoExists } from '../../../hooks/imu-logic';

const BOTTOM_HEIGHT = 80;

const statusLabelMap = {
  [WifiProvisioningStatus.NONE]:
    'onboarding-connect_tracker-connection_status-none',
  [WifiProvisioningStatus.SERIAL_INIT]:
    'onboarding-connect_tracker-connection_status-serial_init',
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
  [WifiProvisioningStatus.PROVISIONING]: 0.4,
  [WifiProvisioningStatus.CONNECTING]: 0.6,
  [WifiProvisioningStatus.LOOKING_FOR_SERVER]: 0.8,
  [WifiProvisioningStatus.DONE]: 1,
  [WifiProvisioningStatus.CONNECTION_ERROR]: 0.6,
  [WifiProvisioningStatus.COULD_NOT_FIND_SERVER]: 0.8,
};

export function ConnectTrackersPage() {
  const { l10n } = useLocalization();
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { trackers, useConnectedTrackers } = useTrackers();
  const { applyProgress, state, skipSetup } = useOnboarding();
  const navigate = useNavigate();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [provisioningStatus, setProvisioningStatus] =
    useState<WifiProvisioningStatus>(WifiProvisioningStatus.NONE);
  const [skipWarning, setSkipWarning] = useState(false);

  applyProgress(0.4);

  const connectedTrackers = useConnectedTrackers();

  const bnoExists = useBnoExists(connectedTrackers);

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

  return (
    <div className="flex flex-col items-center relative">
      <SkipSetupButton
        visible={!state.alonePage}
        modalVisible={skipWarning}
        onClick={() => setSkipWarning(true)}
      ></SkipSetupButton>
      <div className="flex gap-10 w-full max-w-7xl ">
        <div className="flex flex-col w-full max-w-sm">
          <Typography variant="main-title">
            {l10n.getString('onboarding-connect_tracker-title')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-connect_tracker-description-p0')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('onboarding-connect_tracker-description-p1')}
          </Typography>
          <div className="flex flex-col gap-2 py-5">
            {/* <ArrowLink
              to="/onboarding/connect"
              direction="right"
              variant="boxed"
            >
              I have other types of trackers
            </ArrowLink> */}
            <ArrowLink
              to="/settings/serial"
              state={{ SerialPort: 'Auto' }}
              direction="right"
              variant={state.alonePage ? 'boxed-2' : 'boxed'}
            >
              {l10n.getString('onboarding-connect_tracker-issue-serial')}
            </ArrowLink>
          </div>
          <TipBox>{l10n.getString('tips-find_tracker')}</TipBox>

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
                {/* <SpinIcon
                  youSpinMeRightRoundBabyRightRound={!isError}
                ></SpinIcon> */}
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
        <div className="flex flex-col flex-grow">
          <Typography color="secondary" bold>
            {l10n.getString('onboarding-connect_tracker-connected_trackers', {
              amount: connectedTrackers.length,
            })}
          </Typography>

          <div
            className="flex-grow overflow-y-scroll"
            ref={ref}
            style={{ height: layoutHeight - BOTTOM_HEIGHT }}
          >
            <div className="grid lg:grid-cols-2 md:grid-cols-1 gap-2 mx-3 pt-3">
              {Array.from({
                ...connectedTrackers,
                length: Math.max(trackers.length, 20),
              }).map((tracker, index) => (
                <div key={index}>
                  {!tracker && (
                    <div
                      className={classNames(
                        'rounded-xl  h-16',
                        state.alonePage
                          ? 'bg-background-80'
                          : 'bg-background-70'
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
      </div>
      <SkipSetupWarningModal
        accept={skipSetup}
        onClose={() => setSkipWarning(false)}
        isOpen={skipWarning}
      ></SkipSetupWarningModal>
    </div>
  );
}
