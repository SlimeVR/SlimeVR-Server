import { createContext, useContext, useState } from 'react';

export interface HeightContext {
  hmdHeight: number | null;
  setHmdHeight: React.Dispatch<React.SetStateAction<number | null>>;
  floorHeight: number | null;
  setFloorHeight: React.Dispatch<React.SetStateAction<number | null>>;
}

export function useProvideHeightContext(): HeightContext {
  const [hmdHeight, setHmdHeight] = useState<number | null>(null);
  const [floorHeight, setFloorHeight] = useState<number | null>(null);

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
