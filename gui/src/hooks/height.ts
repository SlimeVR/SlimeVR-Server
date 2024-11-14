import { createContext, useContext, useEffect, useState } from 'react';
import { useWebsocketAPI } from './websocket-api';
import { RpcMessage, SettingsRequestT, SettingsResponseT } from 'solarxr-protocol';
import { MIN_HEIGHT } from '@/components/onboarding/pages/body-proportions/ProportionsChoose';

export interface HeightContext {
  hmdHeight: number | null;
  setHmdHeight: React.Dispatch<React.SetStateAction<number | null>>;
  floorHeight: number | null;
  setFloorHeight: React.Dispatch<React.SetStateAction<number | null>>;
}

export function useProvideHeightContext(): HeightContext {
  const [hmdHeight, setHmdHeight] = useState<number | null>(null);
  const [floorHeight, setFloorHeight] = useState<number | null>(null);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  useEffect(
    () => sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT()),
    []
  );
  useRPCPacket(RpcMessage.SettingsResponse, (res: SettingsResponseT) => {
    if (
      !res.modelSettings?.skeletonHeight?.hmdHeight ||
      !res.modelSettings.skeletonHeight.floorHeight ||
      res.modelSettings.skeletonHeight.hmdHeight -
        res.modelSettings.skeletonHeight.floorHeight <=
        MIN_HEIGHT
    ) {
      return;
    }

    setHmdHeight(res.modelSettings.skeletonHeight.hmdHeight);
    setFloorHeight(res.modelSettings.skeletonHeight.floorHeight);
  });

  return { hmdHeight, setHmdHeight, floorHeight, setFloorHeight };
}

export const HeightContextC = createContext<HeightContext>(undefined as never);

export function useHeightContext() {
  const context = useContext(HeightContextC);
  if (!context) {
    throw new Error('useHeightContext must be within a HeightContext Provider');
  }
  return context;
}
