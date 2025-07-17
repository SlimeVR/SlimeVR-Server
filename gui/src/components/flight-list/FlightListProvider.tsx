import {
  FlightListContextC,
  provideSessionFlightlist,
} from '@/hooks/session-flightlist';
import { ReactNode } from 'react';

export function FlightListProvider({ children }: { children: ReactNode }) {
  const context = provideSessionFlightlist();

  return (
    <FlightListContextC.Provider value={context}>
      {children}
    </FlightListContextC.Provider>
  );
}
