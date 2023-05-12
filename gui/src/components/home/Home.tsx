import { useLocalization } from '@fluent/react';
import { useNavigate } from 'react-router-dom';
import { TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '../../hooks/config';
import { useTrackers } from '../../hooks/tracker';
import { Typography } from '../commons/Typography';
import { TrackerCard } from '../tracker/TrackerCard';
import { TrackersTable } from '../tracker/TrackersTable';
import { useStatusContext } from '../../hooks/status-system';

export function Home() {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const { trackers } = useTrackers();
  const { statuses } = useStatusContext();
  const navigate = useNavigate();

  const sendToSettings = (tracker: TrackerDataT) => {
    navigate(
      `/tracker/${tracker.trackerId?.trackerNum}/${tracker.trackerId?.deviceId?.id}`
    );
  };

  return (
    <div className="overflow-y-auto flex flex-col gap-2">
      <div className="flex flex-row flex-wrap gap-3">
        {}
      </div>
      {trackers.length === 0 && (
        <div className="flex px-5 pt-5 justify-center">
          <Typography variant="standard">
            {l10n.getString('home-no_trackers')}
          </Typography>
        </div>
      )}

      {!config?.debug && trackers.length > 0 && (
        <div className="grid sm:grid-cols-1 md:grid-cols-2 gap-3 px-4 my-4">
          {trackers.map(({ tracker, device }, index) => (
            <TrackerCard
              key={index}
              tracker={tracker}
              device={device}
              onClick={() => sendToSettings(tracker)}
              smol
              interactable
            />
          ))}
        </div>
      )}
      {config?.debug && trackers.length > 0 && (
        <div className="px-2 pt-5 overflow-y-scroll overflow-x-auto">
          <TrackersTable
            flatTrackers={trackers}
            clickedTracker={(tracker) => sendToSettings(tracker)}
          ></TrackersTable>
        </div>
      )}
    </div>
  );
}
