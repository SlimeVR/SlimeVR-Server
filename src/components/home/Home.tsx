import { useNavigate } from 'react-router-dom';
import { TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '../../hooks/config';
import { useTrackers } from '../../hooks/tracker';
import { Typography } from '../commons/Typography';
import { TrackerCard } from '../tracker/TrackerCard';
import { TrackersTable } from '../tracker/TrackersTable';

export function Home() {
  const { config } = useConfig();
  const { useAssignedTrackers } = useTrackers();
  const navigate = useNavigate();

  const asignedTrackers = useAssignedTrackers();

  const sendToSettings = (tracker: TrackerDataT) => {
    navigate(
      `/tracker/${tracker.trackerId?.trackerNum}/${tracker.trackerId?.deviceId?.id}`
    );
  };

  return (
    <div className="overflow-y-auto flex flex-col gap-2">
      {asignedTrackers.length === 0 && (
        <div className="flex px-5 pt-5 justify-center">
          <Typography variant="standard">
            No trackers detected or assigned
          </Typography>
        </div>
      )}

      {!config?.debug && (
        <div className="grid sm:grid-cols-1 md:grid-cols-2 gap-3  px-4 my-4">
          {asignedTrackers.map(({ tracker, device }, index) => (
            <TrackerCard
              key={index}
              tracker={tracker}
              device={device}
              onClick={() => sendToSettings(tracker)}
              smol
              interactable
            />
          ))}{' '}
        </div>
      )}
      {config?.debug && (
        <div className="flex px-5 pt-5 justify-center  overflow-x-auto">
          <TrackersTable
            flatTrackers={asignedTrackers}
            clickedTracker={(tracker) => sendToSettings(tracker)}
          ></TrackersTable>
        </div>
      )}
    </div>
  );
}
