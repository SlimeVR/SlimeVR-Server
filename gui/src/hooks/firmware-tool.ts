import { createContext, useContext, useState } from 'react';
import {
  DeviceIdT,
  FirmwarePartT,
  FirmwareUpdateMethod,
  FirmwareUpdateRequestT,
  FirmwareUpdateStatus,
  OTAFirmwareUpdateT,
  SerialDevicePortT,
  SerialFirmwareUpdateT,
} from 'solarxr-protocol';
import { OnboardingContext } from './onboarding';
import {
  BoardDefaults,
  FirmwareBoardDefaultsNullable,
  FirmwareSource,
  FirmwareWithFiles,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { GetFirmwareBoardDefaultsQueryParams } from '@/firmware-tool-api/firmwareToolComponents';

export type SelectedDevice = {
  type: FirmwareUpdateMethod;
  deviceId: string | number;
  deviceNames: string[];
};

export const firmwareUpdateErrorStatus = [
  FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED,
  FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND,
  FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED,
  FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED,
  FirmwareUpdateStatus.ERROR_TIMEOUT,
  FirmwareUpdateStatus.ERROR_UNKNOWN,
  FirmwareUpdateStatus.ERROR_UNSUPPORTED_METHOD,
  FirmwareUpdateStatus.ERROR_UPLOAD_FAILED,
];

export const firmwareUpdateStatusLabel: Record<FirmwareUpdateStatus, string> = {
  [FirmwareUpdateStatus.DOWNLOADING]: 'firmware_update-status-DOWNLOADING',
  [FirmwareUpdateStatus.NEED_MANUAL_REBOOT]:
    'firmware_update-status-NEED_MANUAL_REBOOT-v2',
  [FirmwareUpdateStatus.AUTHENTICATING]: 'firmware_update-status-AUTHENTICATING',
  [FirmwareUpdateStatus.UPLOADING]: 'firmware_update-status-UPLOADING',
  [FirmwareUpdateStatus.SYNCING_WITH_MCU]: 'firmware_update-status-SYNCING_WITH_MCU',
  [FirmwareUpdateStatus.REBOOTING]: 'firmware_update-status-REBOOTING',
  [FirmwareUpdateStatus.PROVISIONING]: 'firmware_update-status-PROVISIONING',
  [FirmwareUpdateStatus.DONE]: 'firmware_update-status-DONE',
  [FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND]:
    'firmware_update-status-ERROR_DEVICE_NOT_FOUND',
  [FirmwareUpdateStatus.ERROR_TIMEOUT]: 'firmware_update-status-ERROR_TIMEOUT',
  [FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED]:
    'firmware_update-status-ERROR_DOWNLOAD_FAILED',
  [FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED]:
    'firmware_update-status-ERROR_AUTHENTICATION_FAILED',
  [FirmwareUpdateStatus.ERROR_UPLOAD_FAILED]:
    'firmware_update-status-ERROR_UPLOAD_FAILED',
  [FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED]:
    'firmware_update-status-ERROR_PROVISIONING_FAILED',
  [FirmwareUpdateStatus.ERROR_UNSUPPORTED_METHOD]:
    'firmware_update-status-ERROR_UNSUPPORTED_METHOD',
  [FirmwareUpdateStatus.ERROR_UNKNOWN]: 'firmware_update-status-ERROR_UNKNOWN',
};

export type FirmwareToolContext = ReturnType<typeof provideFirmwareTool>;
export const FirmwareToolContextC = createContext<FirmwareToolContext>(
  undefined as any
);

export function useFirmwareTool() {
  const context = useContext<FirmwareToolContext>(FirmwareToolContextC);
  if (!context) {
    throw new Error('useFirmwareTool must be within a FirmwareToolContext Provider');
  }
  return context;
}

export function provideFirmwareTool() {
  const [selectedSource, setSelectedSource] = useState<{
    source: GetFirmwareBoardDefaultsQueryParams;
    default: FirmwareBoardDefaultsNullable;
  }>();

  return {
    selectedSource,
    setSelectedSource,
  };
}

export const getFlashingRequests = (
  devices: SelectedDevice[],
  firmwareFiles: FirmwareWithFiles['files'],
  onboardingState: OnboardingContext['state'],
  defaultConfig: BoardDefaults | null
) => {
  const firmware = firmwareFiles.find(({ isFirmware }) => isFirmware);
  if (!firmware) throw new Error('invalid state - no firmware to find');

  const requests = [];

  for (const device of devices) {
    switch (device.type) {
      case FirmwareUpdateMethod.OTAFirmwareUpdate: {
        const dId = new DeviceIdT();
        dId.id = +device.deviceId;

        const part = new FirmwarePartT();
        part.offset = 0;
        part.url = firmware.filePath;

        const method = new OTAFirmwareUpdateT();
        method.deviceId = dId;
        method.firmwarePart = part;

        const req = new FirmwareUpdateRequestT();
        req.method = method;
        req.methodType = FirmwareUpdateMethod.OTAFirmwareUpdate;
        requests.push(req);
        break;
      }
      case FirmwareUpdateMethod.SerialFirmwareUpdate: {
        const id = new SerialDevicePortT();
        id.port = device.deviceId.toString();

        if (!onboardingState.wifi?.ssid)
          throw new Error('invalid state, wifi should be set');

        const method = new SerialFirmwareUpdateT();
        method.deviceId = id;
        method.ssid = onboardingState.wifi.ssid;
        method.password = onboardingState.wifi.password;
        method.needManualReboot =
          defaultConfig?.flashingRules.needManualReboot ?? false;

        method.firmwarePart = firmwareFiles.map(({ offset, filePath }) => {
          const part = new FirmwarePartT();
          part.offset = offset;
          part.url = filePath;
          return part;
        });

        const req = new FirmwareUpdateRequestT();
        req.method = method;
        req.methodType = FirmwareUpdateMethod.SerialFirmwareUpdate;
        requests.push(req);
        break;
      }
      default: {
        throw new Error('unsupported flashing method');
      }
    }
  }
  return requests;
};
