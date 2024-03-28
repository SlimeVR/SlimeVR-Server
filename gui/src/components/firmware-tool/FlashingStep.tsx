import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import {
  SelectedDevice,
  firmwareUpdateErrorStatus,
  useFirmwareTool,
} from '@/hooks/firmware-tool';
import { useEffect, useState } from 'react';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  DeviceIdT,
  DeviceIdTableT,
  FirmwarePartT,
  FirmwareUpdateDeviceId,
  FirmwareUpdateRequestT,
  FirmwareUpdateStatus,
  FirmwareUpdateStatusResponseT,
  FirmwareUpdateStopQueuesRequestT,
  FlashingMethod,
  RpcMessage,
  SerialDevicePortT,
} from 'solarxr-protocol';
import { firmwareToolS3BaseUrl } from '@/firmware-tool-api/firmwareToolFetcher';
import { useOnboarding } from '@/hooks/onboarding';
import { DeviceCardControl } from './DeviceCard';
import { WarningBox } from '@/components/commons/TipBox';
import { Button } from '@/components/commons/Button';
import { useNavigate } from 'react-router-dom';

export function FlashingStep({
  goTo,
  isActive,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
}) {
  const nav = useNavigate();
  const { l10n } = useLocalization();
  const { selectedDevices, buildStatus, selectDevices } = useFirmwareTool();
  const { state: onboardingState } = useOnboarding();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [status, setStatus] = useState<{
    [key: string]: {
      status: FirmwareUpdateStatus;
      type: FlashingMethod;
      progress: number;
      deviceNames: string[];
    };
  }>({});

  const clear = () => {
    setStatus({});
    sendRPCPacket(
      RpcMessage.FirmwareUpdateStopQueuesRequest,
      new FirmwareUpdateStopQueuesRequestT()
    );
  };

  const queueFlashing = (devices: SelectedDevice[]) => {
    clear();

    if (!buildStatus.firmwareFiles)
      throw new Error('invalid state - no firmware files');

    const firmware = buildStatus.firmwareFiles.find(
      ({ isFirmware }) => isFirmware
    );
    if (!firmware) throw new Error('invalid state - no firmware to find');

    for (const device of devices) {
      switch (device.type) {
        case FlashingMethod.OTA: {
          const id = new DeviceIdTableT();
          const dId = new DeviceIdT();
          dId.id = +device.deviceId;
          id.id = dId;

          const part = new FirmwarePartT();
          part.offset = 0;
          part.url = firmwareToolS3BaseUrl + '/' + firmware.url;

          const req = new FirmwareUpdateRequestT();
          req.flashingMethod = device.type;
          req.deviceIdType =
            FirmwareUpdateDeviceId.solarxr_protocol_datatypes_DeviceIdTable;
          req.deviceId = id;
          req.firmwarePart = [part];
          sendRPCPacket(RpcMessage.FirmwareUpdateRequest, req);
          break;
        }
        case FlashingMethod.SERIAL: {
          const id = new SerialDevicePortT();
          id.port = device.deviceId.toString();

          if (!onboardingState.wifi?.ssid || !onboardingState.wifi?.password)
            throw new Error('invalid state, wifi should be set');

          const req = new FirmwareUpdateRequestT();
          req.flashingMethod = device.type;
          req.deviceIdType = FirmwareUpdateDeviceId.SerialDevicePort;
          req.deviceId = id;
          req.ssid = onboardingState.wifi.ssid;
          req.password = onboardingState.wifi.password;

          req.firmwarePart = buildStatus.firmwareFiles.map(
            ({ offset, url }) => {
              const part = new FirmwarePartT();
              part.offset = offset;
              part.url = firmwareToolS3BaseUrl + '/' + url;
              return part;
            }
          );
          sendRPCPacket(RpcMessage.FirmwareUpdateRequest, req);
          break;
        }
        default: {
          throw new Error('unsupported flashing method');
        }
      }
    }
  };

  useEffect(() => {
    if (!isActive) return;
    if (!selectedDevices)
      throw new Error('invalid state - no selected devices');
    queueFlashing(selectedDevices);
    return () => {
      clear();
    };
  }, [isActive]);

  useRPCPacket(
    RpcMessage.FirmwareUpdateStatusResponse,
    (data: FirmwareUpdateStatusResponseT) => {
      if (!data.deviceId) throw new Error('no device id');
      const id =
        data.deviceId instanceof DeviceIdTableT
          ? data.deviceId.id?.id
          : data.deviceId.port;
      if (!id) throw new Error('invalid device id');

      const selectedDevice = selectedDevices?.find(
        ({ deviceId }) => deviceId == id.toString()
      );

      // We skip the status as it can be old trackers still sending status
      if (!selectedDevice) return;

      setStatus((last) => ({
        ...last,
        [id.toString()]: {
          progress: data.progress / 100,
          status: data.status,
          type: selectedDevice.type,
          deviceNames: selectedDevice.deviceNames,
        },
      }));
    }
  );

  const trackerWithErrors = Object.keys(status).filter((id) =>
    firmwareUpdateErrorStatus.includes(status[id].status)
  );

  const retryError = () => {
    const devices = trackerWithErrors.map((id) => {
      const device = status[id];
      return {
        type: device.type,
        deviceId: id,
        deviceNames: device.deviceNames,
      };
    });

    selectDevices(devices);
    queueFlashing(devices);
  };

  const hasPendingTrackers =
    Object.keys(status).filter((id) =>
      [
        FirmwareUpdateStatus.DOWNLOADING,
        FirmwareUpdateStatus.AUTHENTICATING,
        FirmwareUpdateStatus.REBOOTING,
        FirmwareUpdateStatus.SYNCING_WITH_MCU,
        FirmwareUpdateStatus.UPLOADING,
        FirmwareUpdateStatus.PROVISIONING,
      ].includes(status[id].status)
    ).length > 0;

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware-tool-flashing-step-description')}
          </Typography>
        </div>

        <div className="my-4 flex gap-2 flex-col">
          <Localized id="firmware-tool-flashing-step-warning">
            <WarningBox>Warning</WarningBox>
          </Localized>
          {Object.keys(status).map((id) => {
            const val = status[id];

            return (
              <DeviceCardControl
                status={val.status}
                progress={val.progress}
                key={id}
                deviceNames={val.deviceNames}
              ></DeviceCardControl>
            );
          })}
          <div className="flex gap-2 self-end">
            <Localized id="firmware-tool-retry">
              <Button
                variant="secondary"
                disabled={trackerWithErrors.length === 0}
                onClick={retryError}
              ></Button>
            </Localized>
            <Localized id="firmware-tool-flashing-step-flash-more">
              <Button
                variant="secondary"
                disabled={hasPendingTrackers}
                onClick={() => goTo('FlashingMethod')}
              ></Button>
            </Localized>
            <Localized id="firmware-tool-flashing-step-exit">
              <Button
                variant="primary"
                onClick={() => {
                  clear();
                  nav('/');
                }}
              ></Button>
            </Localized>
          </div>
        </div>
      </div>
    </>
  );
}
