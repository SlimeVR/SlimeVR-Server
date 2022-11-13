import { ReactChild } from 'react';
import { AppContextC, useProvideAppContext } from '../../hooks/app';

export function AppContextProvider({ children }: { children: ReactChild }) {
  const context = useProvideAppContext();

  return (
    <AppContextC.Provider value={context}>{children}</AppContextC.Provider>
  );
}
