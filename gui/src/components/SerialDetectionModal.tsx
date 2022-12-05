import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  RpcMessage,
  SerialDevicesRequestT,
  SerialDevicesResponseT,
  SerialDeviceT
} from 'solarxr-protocol';
import { useConfig } from '../hooks/config';
import { usePrevious } from '../hooks/previous';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { useWifiForm, WifiFormData } from '../hooks/wifi-form';
import { BaseModal } from './commons/BaseModal';
import { Button } from './commons/Button';
import { BulbIcon } from './commons/icon/BulbIcon';
import { USBIcon } from './commons/icon/UsbIcon';
import { Typography } from './commons/Typography';

const mapItems = <T,>(items: T[], getKey: (item: T) => string) => {
  const map: any = {};
  for (const item of items) {
    const key = getKey(item);
    map[key] = item;
  }
  return map;
};

const detectChanges = <T,>(
  prevItems: T[],
  nextItems: T[],
  getKey: (item: T) => string,
  compareItems: (a: T, b: T) => boolean
) => {
  const mappedItems = mapItems(prevItems, getKey);
  const addedItems = [];
  const updatedItems = [];
  const removedItems = [];
  const unchangedItems = [];
  for (const nextItem of nextItems) {
    const itemKey = getKey(nextItem);
    if (itemKey in mappedItems) {
      const prevItem = mappedItems[itemKey];
      if (delete mappedItems[itemKey] && compareItems(prevItem, nextItem)) {
        unchangedItems.push(nextItem);
      } else {
        updatedItems.push(nextItem);
      }
    } else {
      addedItems.push(nextItem);
    }
  }
  for (const itemKey in mappedItems) {
    if (itemKey in mappedItems) {
      removedItems.push(mappedItems[itemKey]);
    }
  }
  return { addedItems, updatedItems, removedItems, unchangedItems };
};

export function SerialDetectionModal() {
  const { t } = useTranslation();
  const { config } = useConfig();
  const nav = useNavigate();
  const { pathname } = useLocation();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [currentDevices, setCurrentDevices] = useState<SerialDeviceT[] | null>(
    null
  );
  const prevDevices = usePrevious<SerialDeviceT[] | null>(currentDevices);
  const [isOpen, setOpen] = useState<SerialDeviceT | null>(null);
  const [showWifiForm, setShowWifiForm] = useState(false);

  const { WifiForm, handleSubmit, submitWifiCreds, formState, hasWifiCreds } =
    useWifiForm();

  useEffect(() => {
    if (prevDevices == null) return;

    const changes = detectChanges(
      prevDevices || [],
      currentDevices || [],
      (item) => item.port?.toString() || 'error',
      (a, b) => a.port == b.port && a.name == b.name
    );
    if (changes.addedItems.length === 1) {
      setOpen(changes.addedItems[0]);
    }
  }, [prevDevices, currentDevices]);

  useEffect(() => {
    let timerId: NodeJS.Timer;

    if (
      config?.watchNewDevices &&
      !['/settings/serial', '/onboarding/connect-trackers'].includes(pathname)
    ) {
      timerId = setInterval(() => {
        sendRPCPacket(
          RpcMessage.SerialDevicesRequest,
          new SerialDevicesRequestT()
        );
      }, 3000);
    }
    return () => {
      clearInterval(timerId);
    };
  }, [config, sendRPCPacket, pathname]);

  const openSerial = () => {
    nav('/settings/serial', { state: { serialPort: isOpen?.port } });
    setOpen(null);
  };

  const openWifi = () => {
    if (!hasWifiCreds) {
      setShowWifiForm(true);
    } else {
      setOpen(null);
      nav('/onboarding/connect-trackers', { state: { alonePage: true } });
    }
  };

  const modalWifiSubmit = (form: WifiFormData) => {
    submitWifiCreds(form);
    setOpen(null);
    nav('/onboarding/connect-trackers', { state: { alonePage: true } });
  };

  useRPCPacket(
    RpcMessage.SerialDevicesResponse,
    (val: SerialDevicesResponseT) => {
      setCurrentDevices(val.devices);
    }
  );

  return (
    <BaseModal isOpen={!!isOpen} onRequestClose={() => setOpen(null)}>
      <div className="flex flex-col gap-3">
        {!showWifiForm && (
          <>
            <div className="flex flex-col items-center gap-3 fill-accent-background-20">
              <USBIcon></USBIcon>
              <div className="flex flex-col items-center gap-2">
                <Typography variant="main-title">
                  {t('serial-detection.new-device.p0')}
                </Typography>
                <Typography variant="section-title">
                  {isOpen?.name || 'unknown'}
                </Typography>
                <Typography variant="standard">
                  {t('serial-detection.new-device.p2')}
                </Typography>
              </div>
            </div>

            <Button variant="primary" onClick={openWifi}>
              {t('serial-detection.open-wifi')}
            </Button>
            <Button variant="tiertiary" onClick={openSerial}>
              {t('serial-detection.open-serial')}
            </Button>
            <Button variant="secondary" onClick={() => setOpen(null)}>
              {t('serial-detection.close')}
            </Button>
          </>
        )}
        {showWifiForm && (
          <form
            onSubmit={handleSubmit(modalWifiSubmit)}
            className="flex flex-col gap-3"
          >
            <div className="flex flex-col items-center gap-3">
              <BulbIcon></BulbIcon>
              <Typography variant="main-title">
                {t('serial-detection.new-device.p0')}
              </Typography>
              <Typography variant="standard">
                {t('serial-detection.new-device.p1')}
              </Typography>
            </div>
            <div className="flex flex-col gap-3 rounded-xl max-w-sm">
              <WifiForm></WifiForm>
            </div>

            <Button
              type="submit"
              variant="primary"
              disabled={!formState.isValid}
            >
              {t('serial-detection.submit')}
            </Button>
            <Button variant="secondary" onClick={() => setOpen(null)}>
              {t('serial-detection.close')}
            </Button>
          </form>
        )}
      </div>
    </BaseModal>
  );
}
