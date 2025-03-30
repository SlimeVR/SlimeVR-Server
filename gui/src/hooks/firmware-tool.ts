import { createContext, useContext, useState } from 'react';
import {
  fetchGetFirmwaresDefaultConfigBoard,
  useGetHealth,
  useGetIsCompatibleVersion,
} from '@/firmware-tool-api/firmwareToolComponents';
import {
  BuildResponseDTO,
  CreateBoardConfigDTO,
  CreateBuildFirmwareDTO,
  DefaultBuildConfigDTO,
  FirmwareFileDTO,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { BoardPinsForm } from '@/components/firmware-tool/BoardPinsStep';
import { DeepPartial } from 'react-hook-form';
import {
  BoardType,
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

export type PartialBuildFirmware = DeepPartial<CreateBuildFirmwareDTO>;
export type FirmwareBuildStatus = BuildResponseDTO;
export type SelectedDevice = {
  type: FirmwareUpdateMethod;
  deviceId: string | number;
  deviceNames: string[];
};

export const boardTypeToFirmwareToolBoardType: Record<
  Exclude<
    BoardType,
    // This boards will not be handled by the firmware tool.
    // These are either impossible to compile automatically or deprecated
    | BoardType.CUSTOM
    | BoardType.SLIMEVR_DEV
    | BoardType.SLIMEVR_LEGACY
    | BoardType.OWOTRACK
    | BoardType.WRANGLER
    | BoardType.MOCOPI
    | BoardType.HARITORA
    | BoardType.DEV_RESERVED
  >,
  CreateBoardConfigDTO['type'] | null
> = {
  [BoardType.UNKNOWN]: null,
  [BoardType.NODEMCU]: 'BOARD_NODEMCU',
  [BoardType.WROOM32]: 'BOARD_WROOM32',
  [BoardType.WEMOSD1MINI]: 'BOARD_WEMOSD1MINI',
  [BoardType.TTGO_TBASE]: 'BOARD_TTGO_TBASE',
  [BoardType.ESP01]: 'BOARD_ESP01',
  [BoardType.SLIMEVR]: 'BOARD_SLIMEVR',
  [BoardType.LOLIN_C3_MINI]: 'BOARD_LOLIN_C3_MINI',
  [BoardType.BEETLE32C3]: 'BOARD_BEETLE32C3',
  [BoardType.ESP32C3DEVKITM1]: 'BOARD_ES32C3DEVKITM1',
  [BoardType.WEMOSWROOM02]: null,
  [BoardType.XIAO_ESP32C3]: null,
  [BoardType.ESP32C6DEVKITC1]: null,
  [BoardType.GLOVE_IMU_SLIMEVR_DEV]: null,
};

export const firmwareToolToBoardType: Record<CreateBoardConfigDTO['type'], BoardType> =
  Object.fromEntries(
    Object.entries(boardTypeToFirmwareToolBoardType).map((a) => a.reverse())
  );

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

export interface FirmwareToolContext {
  selectBoard: (boardType: CreateBoardConfigDTO['type']) => Promise<void>;
  selectVersion: (version: CreateBuildFirmwareDTO['version']) => void;
  updatePins: (form: BoardPinsForm) => void;
  updateImus: (imus: CreateBuildFirmwareDTO['imusConfig']) => void;
  setBuildStatus: (buildStatus: FirmwareBuildStatus) => void;
  selectDevices: (device: SelectedDevice[] | null) => void;
  retry: () => void;
  buildStatus: FirmwareBuildStatus;
  defaultConfig: DefaultBuildConfigDTO | null;
  newConfig: PartialBuildFirmware | null;
  selectedDevices: SelectedDevice[] | null;
  isStepLoading: boolean;
  isGlobalLoading: boolean;
  isCompatible: boolean;
  isError: boolean;
}

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

export function useFirmwareToolContext(): FirmwareToolContext {
  const [defaultConfig, setDefaultConfig] = useState<DefaultBuildConfigDTO | null>(
    null
  );
  const [selectedDevices, selectDevices] = useState<SelectedDevice[] | null>(null);
  const [newConfig, setNewConfig] = useState<PartialBuildFirmware>({});
  const [isLoading, setLoading] = useState(false);
  const { isError, isLoading: isInitialLoading, refetch } = useGetHealth({});
  const compatibilityCheckEnabled = !!__VERSION_TAG__;
  const { isLoading: isCompatibilityLoading, data: compatibilityData } =
    useGetIsCompatibleVersion(
      { pathParams: { version: __VERSION_TAG__ } },
      { enabled: compatibilityCheckEnabled }
    );
  const [buildStatus, setBuildStatus] = useState<FirmwareBuildStatus>({
    status: 'CREATING_BUILD_FOLDER',
    id: '',
  });

  return {
    selectBoard: async (boardType: CreateBoardConfigDTO['type']) => {
      setLoading(true);
      const boardDefaults = await fetchGetFirmwaresDefaultConfigBoard({
        pathParams: { board: boardType },
      });
      setDefaultConfig(boardDefaults);
      if (boardDefaults.shouldOnlyUseDefaults) {
        setNewConfig((currConfig) => ({
          ...currConfig,
          ...boardDefaults,
          imusConfig: boardDefaults.imuDefaults,
        }));
      } else {
        setNewConfig((currConfig) => ({
          ...currConfig,
          boardConfig: { ...currConfig.boardConfig, type: boardType },
          imusConfig: [],
        }));
      }
      setLoading(false);
    },
    updatePins: (form: BoardPinsForm) => {
      setNewConfig((currConfig) => {
        return {
          ...currConfig,
          imusConfig: [...(currConfig?.imusConfig || [])],
          boardConfig: {
            ...currConfig.boardConfig,
            ...form,
          },
        };
      });
    },
    updateImus: (imus: CreateBuildFirmwareDTO['imusConfig']) => {
      setNewConfig((currConfig) => {
        return {
          ...currConfig,
          imusConfig: imus.map(({ rotation, ...fields }) => ({
            ...fields,
            rotation: Number(rotation),
          })), // Make sure that the rotation is handled as number
        };
      });
    },
    retry: async () => {
      setLoading(true);
      await refetch();
      setLoading(false);
    },
    selectVersion: (version: CreateBuildFirmwareDTO['version']) => {
      setNewConfig((currConfig) => ({ ...currConfig, version }));
    },
    setBuildStatus,
    selectDevices,
    selectedDevices,
    buildStatus,
    defaultConfig,
    newConfig,
    isStepLoading: isLoading,
    isGlobalLoading: isInitialLoading || isCompatibilityLoading,
    isCompatible: !compatibilityCheckEnabled || (compatibilityData?.success ?? false),
    isError: isError || (!compatibilityData?.success && compatibilityCheckEnabled),
  };
}

export const getFlashingRequests = (
  devices: SelectedDevice[],
  firmwareFiles: FirmwareFileDTO[],
  onboardingState: OnboardingContext['state'],
  defaultConfig: DefaultBuildConfigDTO | null
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
        part.url = firmware.url;

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

        if (!onboardingState.wifi?.ssid || !onboardingState.wifi?.password)
          throw new Error('invalid state, wifi should be set');

        const method = new SerialFirmwareUpdateT();
        method.deviceId = id;
        method.ssid = onboardingState.wifi.ssid;
        method.password = onboardingState.wifi.password;
        method.needManualReboot = defaultConfig?.needManualReboot ?? false;

        method.firmwarePart = firmwareFiles.map(({ offset, url }) => {
          const part = new FirmwarePartT();
          part.offset = offset;
          part.url = url;
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
