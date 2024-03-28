import { createContext, useContext, useState } from 'react';
import {
  fetchGetFirmwaresDefaultConfigBoard,
  useGetHealth,
} from '@/firmware-tool-api/firmwareToolComponents';
import {
  BuildResponseDTO,
  CreateBoardConfigDTO,
  CreateBuildFirmwareDTO,
  DefaultBuildConfigDTO,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { BoardPinsForm } from '@/components/firmware-tool/BoardPinsStep';
import { DeepPartial } from 'react-hook-form';
import { BoardType, FirmwareUpdateStatus, FlashingMethod } from 'solarxr-protocol';

export type PartialBuildFirmware = DeepPartial<CreateBuildFirmwareDTO>;
export type FirmwareBuildStatus = BuildResponseDTO;
export type SelectedDevice = {
  type: FlashingMethod;
  deviceId: string | number;
  deviceNames: string[];
};

export const boardTypeToFirmwareToolBoardType: Record<
  BoardType,
  CreateBoardConfigDTO['type'] | null
> = {
  [BoardType.UNKNOWN]: null,
  [BoardType.SLIMEVR_LEGACY]: 'BOARD_SLIMEVR_LEGACY',
  [BoardType.SLIMEVR_DEV]: 'BOARD_SLIMEVR_DEV',
  [BoardType.NODEMCU]: 'BOARD_NODEMCU',
  [BoardType.CUSTOM]: 'BOARD_CUSTOM',
  [BoardType.WROOM32]: 'BOARD_WROOM32',
  [BoardType.WEMOSD1MINI]: 'BOARD_WEMOSD1MINI',
  [BoardType.TTGO_TBASE]: 'BOARD_TTGO_TBASE',
  [BoardType.ESP01]: 'BOARD_ESP01',
  [BoardType.SLIMEVR]: 'BOARD_SLIMEVR',
  [BoardType.LOLIN_C3_MINI]: 'BOARD_LOLIN_C3_MINI',
  [BoardType.BEETLE32C32]: 'BOARD_BEETLE32C32',
  [BoardType.ES32C3DEVKITM1]: 'BOARD_ES32C3DEVKITM1',
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
  const { isError, isInitialLoading, refetch } = useGetHealth({});
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
      setNewConfig((currConfig) => ({
        ...currConfig,
        boardConfig: { ...currConfig.boardConfig, type: boardType },
      }));
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
    isGlobalLoading: isInitialLoading,
    isError,
  };
}
