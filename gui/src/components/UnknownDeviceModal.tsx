import { useEffect, useRef, useState } from 'react';
import { BaseModal } from './commons/BaseModal';
import { Typography } from './commons/Typography';
import { Button } from './commons/Button';
import { useLocalization } from '@fluent/react';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocation } from 'react-router-dom';
import {
  AddUnknownDeviceRequestT,
  RpcMessage,
  UnknownDeviceHandshakeNotificationT,
} from 'solarxr-protocol';

export function UnknownDeviceModal() {
  const { l10n } = useLocalization();
  const [open, setOpen] = useState(0);
  const { pathname } = useLocation();
  const ignoredTrackers = useRef(new Set());
  const [currentTracker, setCurrentTracker] = useState<string | null>(null);
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();

  useRPCPacket(
    RpcMessage.UnknownDeviceHandshakeNotification,
    ({ macAddress }: UnknownDeviceHandshakeNotificationT) => {
      if (
        ['/onboarding/connect-trackers'].includes(pathname) ||
        ignoredTrackers.current.has(macAddress) ||
        (currentTracker !== null && currentTracker !== macAddress)
      )
        return;

      setCurrentTracker(macAddress as string);
      setOpen((old) => old + 1);
    }
  );

  useEffect(() => {
    const timeout = setTimeout(() => {
      setOpen(0);
      setCurrentTracker(null);
    }, 5000);

    return () => clearTimeout(timeout);
  }, [open]);

  const closeModal = () => {
    setCurrentTracker(null);
    setOpen(0);
  };

  return (
    <BaseModal isOpen={open !== 0}>
      <div className="flex flex-col gap-3">
        <div className="flex flex-col items-center gap-3 fill-accent-background-20">
          <div className="flex flex-col items-center gap-2">
            <Typography variant="main-title">
              {l10n.getString('unknown_device-modal-title')}
            </Typography>
            <Typography variant="standard">
              {l10n.getString('unknown_device_on-modal-description')}
            </Typography>
          </div>
        </div>

        <Button
          variant="primary"
          onClick={() => {
            sendRPCPacket(
              RpcMessage.AddUnknownDeviceRequest,
              new AddUnknownDeviceRequestT(currentTracker)
            );
            closeModal();
          }}
        >
          {l10n.getString('unknown_device-modal-confirm')}
        </Button>
        <Button
          variant="tertiary"
          onClick={() => {
            ignoredTrackers.current.add(currentTracker);
            closeModal();
          }}
        >
          {l10n.getString('unknown_device-modal-forget')}
        </Button>
      </div>
    </BaseModal>
  );
}
