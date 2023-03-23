import { TrackerDataT } from 'solarxr-protocol';
import { useTracker } from '../../hooks/tracker';
import { useLocalization } from '@fluent/react';

export function IMUVisualizerWidget({ tracker }: { tracker: TrackerDataT }) {
  const { l10n } = useLocalization();

  const { useRawRotationEulerDegrees, useIdentAdjRotationEulerDegrees } =
    useTracker(tracker);

  return (
    <div className="bg-background-70 flex flex-col p-3 rounded-lg gap-2"></div>
  );
}
