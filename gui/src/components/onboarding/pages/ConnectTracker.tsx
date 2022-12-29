import classNames from 'classnames';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  CloseSerialRequestT,
  OpenSerialRequestT,
  RpcMessage,
  SerialUpdateResponseT,
  SetWifiRequestT,
} from 'solarxr-protocol';
import { useLayout } from '../../../hooks/layout';
import { useOnboarding } from '../../../hooks/onboarding';
import { useTrackers } from '../../../hooks/tracker';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { ArrowLink } from '../../commons/ArrowLink';
import { Button } from '../../commons/Button';
import { LoaderIcon } from '../../commons/icon/LoaderIcon';
import { TipBox } from '../../commons/TipBox';
import { Typography } from '../../commons/Typography';
import { TrackerCard } from '../../tracker/TrackerCard';

const BOTTOM_HEIGHT = 80;
type ConnectionStatus =
  | 'CONNECTING'
  | 'CONNECTED'
  | 'HANDSHAKE'
  | 'ERROR'
  | 'START-CONNECTING';

const statusLabelMap = {
  ['CONNECTING']: 'onboarding-connect_tracker-connection_status-connecting',
  ['CONNECTED']: 'onboarding-connect_tracker-connection_status-connected',
  ['ERROR']: 'onboarding-connect_tracker-connection_status-error',
  ['START-CONNECTING']:
    'onboarding-connect_tracker-connection_status-start_connecting',
  ['HANDSHAKE']: 'onboarding-connect_tracker-connection_status-handshake',
};

export function ConnectTrackersPage() {
  const { t } = useTranslation();
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { trackers, useConnectedTrackers } = useTrackers();
  const { applyProgress, state, skipSetup } = useOnboarding();
  const navigate = useNavigate();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [isSerialOpen, setSerialOpen] = useState(false);
  const [connectionStatus, setConnectionStatus] =
    useState<ConnectionStatus>('START-CONNECTING');

  applyProgress(0.4);

  const connectedTrackers = useConnectedTrackers();

  const openSerial = () => {
    const req = new OpenSerialRequestT();
    req.auto = true;

    sendRPCPacket(RpcMessage.OpenSerialRequest, req);
  };

  useEffect(() => {
    if (!state.wifi) {
      navigate('/onboarding/wifi-creds');
    }

    openSerial();
    return () => {
      sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    };
  }, []);

  useRPCPacket(
    RpcMessage.SerialUpdateResponse,
    (data: SerialUpdateResponseT) => {
      if (data.closed) {
        setSerialOpen(false);
        setConnectionStatus('START-CONNECTING');
        setTimeout(() => {
          openSerial();
        }, 1000);
      }

      if (!data.closed && !isSerialOpen) {
        setSerialOpen(true);
        setConnectionStatus('START-CONNECTING');
      }

      if (data.log) {
        const log = data.log as string;
        if (connectionStatus === 'START-CONNECTING' && state.wifi) {
          setConnectionStatus('CONNECTING');
          if (!state.wifi) return;
          const wifi = new SetWifiRequestT();
          wifi.ssid = state.wifi.ssid;
          wifi.password = state.wifi.password;
          sendRPCPacket(RpcMessage.SetWifiRequest, wifi);
        }

        if (log.includes('Connected successfully to SSID')) {
          setConnectionStatus('CONNECTED');
        }

        if (log.includes('Handshake successful')) {
          setConnectionStatus('HANDSHAKE');
          setTimeout(() => {
            setConnectionStatus('START-CONNECTING');
          }, 3000);
        }

        if (
          // eslint-disable-next-line quotes
          log.includes("Can't connect from any credentials")
        ) {
          setConnectionStatus('ERROR');
        }
      }
    }
  );

  useEffect(() => {
    const id = setInterval(() => {
      if (!isSerialOpen) openSerial();
      else clearInterval(id);
    }, 1000);

    return () => {
      clearInterval(id);
    };
  }, [isSerialOpen, sendRPCPacket]);

  return (
    <div className="flex flex-col items-center">
      <div className="flex gap-10 w-full max-w-7xl ">
        <div className="flex flex-col w-full max-w-sm">
          {!state.alonePage && (
            <ArrowLink to="/onboarding/wifi-creds">
              {t('onboarding-connect_tracker-back')}
            </ArrowLink>
          )}
          <Typography variant="main-title">
            {t('onboarding-connect_tracker-title')}
          </Typography>
          <Typography color="secondary">
            {t('onboarding-connect_tracker-description-p0')}
          </Typography>
          <Typography color="secondary">
            {t('onboarding-connect_tracker-description-p1')}
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
              {t('onboarding-connect_tracker-issue-serial')}
            </ArrowLink>
          </div>
          <TipBox>{t('tips-find_tracker')}</TipBox>

          <div
            className={classNames(
              'rounded-xl h-16 flex gap-2 p-3 lg:w-full mt-4',
              state.alonePage ? 'bg-background-60' : 'bg-background-70',
              connectionStatus === 'ERROR' && 'border-2 border-status-critical'
            )}
          >
            <div className="flex flex-col justify-center fill-background-10">
              <LoaderIcon
                youSpinMeRightRoundBabyRightRound={connectionStatus !== 'ERROR'}
              ></LoaderIcon>
            </div>
            <div className="flex flex-col">
              <Typography bold>
                {t('onboarding-connect_tracker-usb')}
              </Typography>
              <Typography color="secondary">
                {t(statusLabelMap[connectionStatus])}
              </Typography>
            </div>
          </div>
        </div>
        <div className="flex flex-col flex-grow">
          <Typography color="secondary" bold>
            {t('onboarding-connect_tracker-connected_trackers', {
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
      <div
        style={{ height: BOTTOM_HEIGHT }}
        className="flex items-center w-full"
      >
        <div className="w-full flex">
          <div className="flex flex-grow">
            {!state.alonePage && (
              <Button variant="secondary" to="/" onClick={skipSetup}>
                {t('onboarding-skip')}
              </Button>
            )}
          </div>
          <div className="flex gap-3">
            {!state.alonePage && (
              <Button variant="primary" to="/onboarding/trackers-assign">
                {t('onboarding-connect_tracker-next')}
              </Button>
            )}
            {state.alonePage && (
              <Button variant="primary" to="/">
                {t('onboarding-connect_tracker-next')}
              </Button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
