import { ReactNode } from 'react';
import { StatusSystemC, useProvideStatusContext } from '@/hooks/status-system';

export function StatusProvider({ children }: { children: ReactNode }) {
  const context = useProvideStatusContext();

  // throw new Error('BOOOOMMMMMM');

  return (
    <StatusSystemC.Provider value={context}>{children}</StatusSystemC.Provider>
  );
}
