import { createContext, useContext, useEffect, useState } from 'react';
import {
  DataFeedMessage,
  DataFeedUpdateT,
  ResetResponseT,
  RpcMessage,
  StartDataFeedT,
} from 'solarxr-protocol';
import { handleResetSounds } from '@/sounds/sounds';
import { useConfig } from './config';
import { useBonesDataFeedConfig, useDataFeedConfig } from './datafeed-config';
import { useWebsocketAPI } from './websocket-api';
import { useAtomValue, useSetAtom } from 'jotai';
import { bonesAtom, datafeedAtom, devicesAtom } from '@/store/app-store';
import { updateSentryContext } from '@/utils/sentry';
import { fetchCurrentFirmwareRelease, FirmwareRelease } from './firmware-update';

export interface AppContext {
  currentFirmwareRelease: FirmwareRelease | null;
}

export function useProvideAppContext(): AppContext {
  const { useRPCPacket, sendDataFeedPacket, useDataFeedPacket, isConnected } =
    useWebsocketAPI();
  const { config } = useConfig();
  const { dataFeedConfig } = useDataFeedConfig();
  const bonesDataFeedConfig = useBonesDataFeedConfig();
  const setDatafeed = useSetAtom(datafeedAtom);
  const setBones = useSetAtom(bonesAtom);
  const devices = useAtomValue(devicesAtom);

  const [currentFirmwareRelease, setCurrentFirmwareRelease] =
    useState<FirmwareRelease | null>(null);

  useEffect(() => {
    if (isConnected) {
      const startDataFeed = new StartDataFeedT();
      startDataFeed.dataFeeds = [dataFeedConfig, bonesDataFeedConfig];
      sendDataFeedPacket(DataFeedMessage.StartDataFeed, startDataFeed);
    }
  }, [isConnected]);

  useDataFeedPacket(DataFeedMessage.DataFeedUpdate, (packet: DataFeedUpdateT) => {
    if (packet.index === 0) {
      setDatafeed(packet);
    } else if (packet.index === 1) {
      setBones(packet.bones);
    }
  });

  useEffect(() => {
    updateSentryContext(devices);
  }, [devices]);

  useRPCPacket(RpcMessage.ResetResponse, (resetResponse: ResetResponseT) => {
    if (!config?.feedbackSound) return;
    handleResetSounds(config?.feedbackSoundVolume ?? 1, resetResponse);
  });

  useEffect(() => {
    const interval = setInterval(() => {
      fetchCurrentFirmwareRelease().then((res) => setCurrentFirmwareRelease(res));
    }, 1000);
    return () => {
      clearInterval(interval);
    };
  }, []);

  useEffect(() => {
  });

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
