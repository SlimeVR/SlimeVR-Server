import {
  TrackingChecklistContectC,
  provideTrackingChecklist,
} from '@/hooks/tracking-checklist';
import { ReactNode } from 'react';

export function TrackingChecklistProvider({
  children,
}: {
  children: ReactNode;
}) {
  const context = provideTrackingChecklist();

  return (
    <TrackingChecklistContectC.Provider value={context}>
      {children}
    </TrackingChecklistContectC.Provider>
  );
}
