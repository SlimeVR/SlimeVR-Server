import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import {
  SelectedDevice,
  firmwareUpdateErrorStatus,
  getFlashingRequests,
  useFirmwareTool,
} from '@/hooks/firmware-tool';
import { useEffect, useMemo, useState } from 'react';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  DeviceIdTableT,
  FirmwareUpdateMethod,
  FirmwareUpdateStatus,
  FirmwareUpdateStatusResponseT,
  FirmwareUpdateStopQueuesRequestT,
  RpcMessage,
} from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { DeviceCardControl } from './DeviceCard';
import { WarningBox } from '@/components/commons/TipBox';
import { Button } from '@/components/commons/Button';
import { useNavigate } from 'react-router-dom';
import { firmwareToolS3BaseUrl } from '@/firmware-tool-api/firmwareToolFetcher';

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
  const { selectedDevices, buildStatus, selectDevices, defaultConfig } =
    useFirmwareTool();
  const { state: onboardingState } = useOnboarding();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [status, setStatus] = useState<{
    [key: string]: {
      status: FirmwareUpdateStatus;
      type: FirmwareUpdateMethod;
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

  const queueFlashing = (selectedDevices: SelectedDevice[]) => {
    clear();
    if (!buildStatus.firmwareFiles)
      throw new Error('invalid state - no firmware files');
    const requests = getFlashingRequests(
      selectedDevices,
      buildStatus.firmwareFiles.map(({ url, ...fields }) => ({
        url: `${firmwareToolS3BaseUrl}/${url}`,
        ...fields,
      })),
      onboardingState,
      defaultConfig
    );

    requests.forEach((req) => {
      sendRPCPacket(RpcMessage.FirmwareUpdateRequest, req);
    });
  };

  useEffect(() => {
    if (!isActive) return;
    if (!selectedDevices)
      throw new Error('invalid state - no selected devices');
    queueFlashing(selectedDevices);
    return () => clear();
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

  const trackerWithErrors = useMemo(
    () =>
      Object.keys(status).filter((id) =>
        firmwareUpdateErrorStatus.includes(status[id].status)
      ),
    [status, firmwareUpdateErrorStatus]
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

  const hasPendingTrackers = useMemo(
    () =>
      Object.keys(status).filter((id) =>
        [
          FirmwareUpdateStatus.NEED_MANUAL_REBOOT,
          FirmwareUpdateStatus.DOWNLOADING,
          FirmwareUpdateStatus.AUTHENTICATING,
          FirmwareUpdateStatus.REBOOTING,
          FirmwareUpdateStatus.SYNCING_WITH_MCU,
          FirmwareUpdateStatus.UPLOADING,
          FirmwareUpdateStatus.PROVISIONING,
        ].includes(status[id].status)
      ).length > 0,
    [status]
  );

  const shouldShowRebootWarning = useMemo(
    () =>
      Object.keys(status).find((id) =>
        [
          FirmwareUpdateStatus.REBOOTING,
          FirmwareUpdateStatus.UPLOADING,
        ].includes(status[id].status)
      ),
    [status]
  );

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-flashing_step-description')}
          </Typography>
        </div>

        <div className="my-4 flex gap-2 flex-col">
          {shouldShowRebootWarning && (
            <Localized id="firmware_tool-flashing_step-warning">
              <WarningBox>Warning</WarningBox>
            </Localized>
          )}

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
            <Localized id="firmware_tool-retry">
              <Button
                variant="secondary"
                disabled={trackerWithErrors.length === 0}
                onClick={retryError}
              ></Button>
            </Localized>
            <Localized id="firmware_tool-flashing_step-flash_more">
              <Button
                variant="secondary"
                disabled={hasPendingTrackers}
                onClick={() => goTo('FlashingMethod')}
              ></Button>
            </Localized>
            <Localized id="firmware_tool-flashing_step-exit">
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
