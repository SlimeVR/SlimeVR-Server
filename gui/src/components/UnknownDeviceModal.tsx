import { useState } from 'react';
import { BaseModal } from './commons/BaseModal';
import { Typography } from './commons/Typography';
import { Button } from './commons/Button';
import { Localized, useLocalization } from '@fluent/react';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocation } from 'react-router-dom';
import {
  AddUnknownDeviceRequestT,
  RpcMessage,
  UnknownDeviceHandshakeNotificationT,
} from 'solarxr-protocol';
import { useDebouncedEffect } from '@/hooks/timeout';
import { useAppContext } from '@/hooks/app';

export function UnknownDeviceModal() {
  const { l10n } = useLocalization();
  const [open, setOpen] = useState(0);
  const { pathname } = useLocation();
  const { state, dispatch } = useAppContext();
  const [currentTracker, setCurrentTracker] = useState<string | null>(null);
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();

  useRPCPacket(
    RpcMessage.UnknownDeviceHandshakeNotification,
    ({ macAddress }: UnknownDeviceHandshakeNotificationT) => {
      if (
        ['/onboarding/connect-trackers', '/settings/firmware-tool'].includes(
          pathname
        ) ||
        state.ignoredTrackers.has(macAddress as string) ||
        (currentTracker !== null && currentTracker !== macAddress)
      )
        return;

      setCurrentTracker(macAddress as string);
      setOpen((old) => old + 1);
    }
  );

  useDebouncedEffect(
    () => {
      setOpen(0);
      setCurrentTracker(null);
    },
    [open],
    3000
  );

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
            <Localized
              id="unknown_device-modal-description"
              elems={{ b: <b></b> }}
              vars={{ deviceId: currentTracker ?? 'ERROR' }}
            >
              <Typography
                variant="standard"
                textAlign="text-center"
                whitespace="whitespace-pre-line"
              >
                There is a new device in here!
              </Typography>
            </Localized>
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
            dispatch({
              type: 'ignoreTracker',
              value: currentTracker as string,
            });
            closeModal();
          }}
        >
          {l10n.getString('unknown_device-modal-forget')}
        </Button>
      </div>
    </BaseModal>
  );
}
