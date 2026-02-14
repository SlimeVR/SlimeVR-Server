import { IElectronAPI, OSStats } from 'electron/preload/interface';
import { createContext, useContext, useLayoutEffect, useState } from 'react';

type CachedData = {
  os: OSStats;
};
type AvailableElectron = {
  isElectron: true;
  api: IElectronAPI;

  data: () => CachedData;
};
type ElectronContext = AvailableElectron | { isElectron: false };

export const ElectronContextC = createContext<ElectronContext>(undefined as any);

export function provideElectron(): ElectronContext {
  const api = window.electronAPI;
  if (!api) return { isElectron: false };
  const [cachedData, setCachedData] = useState<CachedData>();

  useLayoutEffect(() => {
    Promise.all([api.osStats()]).then(([os]) => {
      setCachedData({
        os,
      });
    });
  }, []);

  return {
    isElectron: true,
    api,
    data: () => {
      if (!cachedData) throw 'not loaded yet';
      return cachedData;
    },
  };
}

export function useElectron() {
  const context = useContext<ElectronContext>(ElectronContextC);
  if (!context) {
    throw new Error('useElectron must be within a ElectronContext Provider');
  }
  return context;
}
