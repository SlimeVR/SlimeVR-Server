import { ReactNode } from 'react';
import { AppContextC, useProvideAppContext } from '@/hooks/app';

export function AppContextProvider({ children }: { children: ReactNode }) {
  const context = useProvideAppContext();

  return (
    <AppContextC.Provider value={context}>{children}</AppContextC.Provider>
  );
}
