import { Localized, useLocalization } from '@fluent/react';
import { useNavigate } from 'react-router-dom';
import { StatusData, TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '../../hooks/config';
import { useTrackers } from '../../hooks/tracker';
import { Typography } from '../commons/Typography';
import { TrackerCard } from '../tracker/TrackerCard';
import { TrackersTable } from '../tracker/TrackersTable';
import {
  parseStatusToLocale,
  trackerStatusRelated,
  useStatusContext,
} from '../../hooks/status-system';
import { useMemo } from 'react';
import { WarningBox } from '../commons/TipBox';

const DONT_REPEAT_STATUSES = [StatusData.StatusTrackerReset];

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

  const filteredStatuses = useMemo(() => {
    const dontRepeat = new Map(DONT_REPEAT_STATUSES.map((x) => [x, false]));
    return Object.entries(statuses).filter(([, value]) => {
      if (dontRepeat.get(value.dataType)) return false;
      if (dontRepeat.has(value.dataType)) dontRepeat.set(value.dataType, true);
      return true;
    });
  }, [statuses]);

  return (
    <div className="overflow-y-auto flex flex-col gap-2">
      <div className="flex flex-col flex-wrap gap-3 px-4 pt-4 lg:flex-row">
        {filteredStatuses
          .filter(([, status]) => status.prioritized)
          .map(([, status]) => (
            <div className="md:w-1/2 w-full" key={status.id}>
              <Localized
                id={`status_system-${StatusData[status.dataType]}`}
                vars={parseStatusToLocale(status, trackers)}
              >
                <WarningBox whitespace={false}>
                  {`Warning, you should fix ${StatusData[status.dataType]}`}
                </WarningBox>
              </Localized>
            </div>
          ))}
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
              warning={Object.values(statuses).some((status) =>
                trackerStatusRelated(tracker, status)
              )}
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
