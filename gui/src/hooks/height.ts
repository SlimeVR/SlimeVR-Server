import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { useWebsocketAPI } from './websocket-api';
import { RpcMessage, SettingsRequestT, SettingsResponseT } from 'solarxr-protocol';
import { MIN_HEIGHT } from './manual-proportions';

export interface HeightContext {
  hmdHeight: number | null;
  setHmdHeight: React.Dispatch<React.SetStateAction<number | null>>;
  floorHeight: number | null;
  setFloorHeight: React.Dispatch<React.SetStateAction<number | null>>;
  currentHeight: number | null;
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
    const hmd = res.modelSettings?.skeletonHeight?.hmdHeight;
    const floor = res.modelSettings?.skeletonHeight?.floorHeight;

    if (validateHeight(hmd, floor)) {
      setHmdHeight(hmd ?? null);
      setFloorHeight(floor ?? null);
    }
  });

  const currentHeight = useMemo(
    () => computeHeight(hmdHeight, floorHeight),
    [hmdHeight, floorHeight]
  );

  return { hmdHeight, setHmdHeight, floorHeight, setFloorHeight, currentHeight };
}

export const HeightContextC = createContext<HeightContext>(undefined as never);

export function useHeightContext() {
  const context = useContext(HeightContextC);
  if (!context) {
    throw new Error('useHeightContext must be within a HeightContext Provider');
  }
  return context;
}

export function validateHeight(
  hmdHeight: number | null | undefined,
  floorHeight: number | null | undefined
) {
  const height = computeHeight(hmdHeight, floorHeight);
  return height != null && height >= MIN_HEIGHT;
}

export function computeHeight(
  hmdHeight: number | null | undefined,
  floorHeight: number | null | undefined
) {
  return hmdHeight !== undefined && hmdHeight !== null
    ? hmdHeight - (floorHeight ?? 0)
    : null;
}

// The headset height is not the full height! This value compensates for the
// offset from the headset height to the user full height
// From Drillis and Contini (1966)
export const EYE_HEIGHT_TO_HEIGHT_RATIO = 0.936;

// Based on average human height (1.65m)
// From https://ourworldindata.org/human-height (January 2024)
export const DEFAULT_FULL_HEIGHT = 1.65;
export const DEFAULT_EYE_HEIGHT = DEFAULT_FULL_HEIGHT * EYE_HEIGHT_TO_HEIGHT_RATIO;
