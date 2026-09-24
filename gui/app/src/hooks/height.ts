import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { useWebsocketAPI } from './websocket-api';
import {
  RpcMessage,
  SkeletonSettingsRequestT,
  UserHeightResponseT,
} from 'solarxr-protocol';
import { MIN_HEIGHT } from './manual-proportions';

export interface HeightContext {
  headHeight: number | null;
  setHeadHeight: React.Dispatch<React.SetStateAction<number | null>>;
  floorHeight: number | null;
  setFloorHeight: React.Dispatch<React.SetStateAction<number | null>>;
  currentHeight: number | null;
}

export function useProvideHeightContext(): HeightContext {
  const [headHeight, setHeadHeight] = useState<number | null>(null);
  const [floorHeight, setFloorHeight] = useState<number | null>(null);
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  useEffect(
    () =>
      sendRPCPacket(RpcMessage.SkeletonSettingsRequest, new SkeletonSettingsRequestT()),
    []
  );
  useRPCPacket(RpcMessage.UserHeightResponse, (res: UserHeightResponseT) => {
    const head = res.headHeight;
    const floor = res.floorHeight;

    if (validateHeight(head, floor)) {
      setHeadHeight(head ?? null);
      setFloorHeight(floor ?? null);
    }
  });

  const currentHeight = useMemo(
    () => computeHeight(headHeight, floorHeight),
    [headHeight, floorHeight]
  );

  return {
    headHeight,
    setHeadHeight: setHeadHeight,
    floorHeight,
    setFloorHeight,
    currentHeight,
  };
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
  headHeight: number | null | undefined,
  floorHeight: number | null | undefined
) {
  const height = computeHeight(headHeight, floorHeight);
  return height != null && height >= MIN_HEIGHT;
}

export function computeHeight(
  headHeight: number | null | undefined,
  floorHeight: number | null | undefined
) {
  return headHeight !== undefined && headHeight !== null
    ? headHeight - (floorHeight ?? 0)
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
