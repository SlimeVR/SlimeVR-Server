import { createContext, useContext, useEffect, useLayoutEffect, useState } from 'react';
import {
  DataFeedMessage,
  DataFeedUpdateT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  ResetStatusResponseT,
  RpcMessage,
  StartDataFeedT,
} from 'solarxr-protocol';
import { handleResetSounds } from '@/sounds/sounds';
import { useConfig } from './config';
import { useBonesDataFeedConfig, useDataFeedConfig } from './datafeed-config';
import { useWebsocketAPI } from './websocket-api';
import { useSetAtom } from 'jotai';
import {
  bonesAtom,
  datafeedAtom,
  resetsSettingsAtom,
  resetStatusAtom,
} from '@/store/app-store';
import { fetchCurrentFirmwareRelease, FirmwareRelease } from './firmware-update';
import { DEFAULT_LOCALE, LangContext } from '@/i18n/config';

export interface AppContext {
  currentFirmwareRelease: FirmwareRelease | null;
}

export function useProvideAppContext(): AppContext {
  const {
    useRPCPacket,
    sendRPCPacket,
    sendDataFeedPacket,
    useDataFeedPacket,
    isConnected,
  } = useWebsocketAPI();
  const { changeLocales } = useContext(LangContext);
  const { config } = useConfig();
  const { dataFeedConfig } = useDataFeedConfig();
  const bonesDataFeedConfig = useBonesDataFeedConfig();
  const setDatafeed = useSetAtom(datafeedAtom);
  const setBones = useSetAtom(bonesAtom);
  const setResetStatus = useSetAtom(resetStatusAtom);
  const setResetsSettings = useSetAtom(resetsSettingsAtom);

  const [currentFirmwareRelease, setCurrentFirmwareRelease] =
    useState<FirmwareRelease | null>(null);

  useEffect(() => {
    if (isConnected) {
      const startDataFeed = new StartDataFeedT();
      startDataFeed.dataFeeds = [dataFeedConfig, bonesDataFeedConfig];
      sendDataFeedPacket(DataFeedMessage.StartDataFeed, startDataFeed);
    }
  }, [isConnected, config?.debug, config?.devSettings?.fastDataFeed]);

  useDataFeedPacket(DataFeedMessage.DataFeedUpdate, (packet: DataFeedUpdateT) => {
    if (packet.index === 0) {
      setDatafeed(packet);
    } else if (packet.index === 1) {
      setBones(packet.bones);
    }
  });

  useRPCPacket(RpcMessage.ResetStatusResponse, (resetStatus: ResetStatusResponseT) => {
    setResetStatus(resetStatus);
    if (!config?.feedbackSound) return;
    handleResetSounds(config?.feedbackSoundVolume ?? 1, resetStatus);
  });

  useRPCPacket(
    RpcMessage.ResetsSettingsResponse,
    (settings: ResetsSettingsResponseT) => {
      setResetsSettings(settings);
    }
  );

  useEffect(() => {
    if (!isConnected) return;
    sendRPCPacket(RpcMessage.ResetsSettingsRequest, new ResetsSettingsRequestT());
  }, [isConnected]);

  useEffect(() => {
    if (!config) return;

    const interval = setInterval(() => {
      fetchCurrentFirmwareRelease(config.uuid).then(setCurrentFirmwareRelease);
    }, 1000);
    return () => {
      clearInterval(interval);
    };
  }, [config?.uuid]);

  useLayoutEffect(() => {
    changeLocales([config?.lang || DEFAULT_LOCALE]);
  }, []);

  useEffect(() => {
    const handleBlur = () => {
      document.documentElement.classList.add('freeze-animations');
    };

    const handleFocus = () => {
      document.documentElement.classList.remove('freeze-animations');
    };

    window.addEventListener('blur', handleBlur);
    window.addEventListener('focus', handleFocus);

    return () => {
      window.removeEventListener('blur', handleBlur);
      window.removeEventListener('focus', handleFocus);
    };
  }, []);

  return {
    currentFirmwareRelease,
  };
}

export const AppContextC = createContext<AppContext>(undefined as any);

export function useAppContext() {
  const context = useContext<AppContext>(AppContextC);
  if (!context) {
    throw new Error('useAppContext must be within a AppContext Provider');
  }
  return context;
}
