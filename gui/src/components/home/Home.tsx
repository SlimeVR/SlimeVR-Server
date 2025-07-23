import { useLocalization } from '@fluent/react';
import { NavLink, useNavigate } from 'react-router-dom';
import { TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { Typography } from '@/components/commons/Typography';
import { TrackerCard } from '@/components/tracker/TrackerCard';
import { TrackersTable } from '@/components/tracker/TrackersTable';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';
import { useSessionFlightlist } from '@/hooks/session-flightlist';

export function Home() {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const trackers = useAtomValue(flatTrackersAtom);
  const { hightlightedTrackers } = useSessionFlightlist();
  const navigate = useNavigate();

  const sendToSettings = (tracker: TrackerDataT) => {
    navigate(
      `/tracker/${tracker.trackerId?.trackerNum}/${tracker.trackerId?.deviceId?.id}`
    );
  };

  return (
    <div className="relative h-full">
      <NavLink
        to="/vr-mode"
        className="xs:hidden absolute z-50 h-12 w-12 rounded-full bg-accent-background-30 bottom-3 right-3 flex justify-center items-center fill-background-10"
      >
        <HeadsetIcon></HeadsetIcon>
      </NavLink>
      <div className="overflow-y-auto flex flex-col gap-3">
        {trackers.length === 0 && (
          <div className="flex px-5 pt-5 justify-center">
            <Typography variant="standard">
              {l10n.getString('home-no_trackers')}
            </Typography>
          </div>
        )}

        {config?.homeLayout == 'default' && trackers.length > 0 && (
          <div className="grid sm:grid-cols-1 md:grid-cols-2 gap-4 px-5 my-5">
            {trackers.map(({ tracker, device }, index) => (
              <TrackerCard
                key={index}
                tracker={tracker}
                device={device}
                onClick={() => sendToSettings(tracker)}
                smol
                showUpdates
                interactable
                warning={
                  !!hightlightedTrackers.find(
                    (t) =>
                      t?.deviceId?.id === tracker.trackerId?.deviceId?.id &&
                      t?.trackerNum === tracker.trackerId?.trackerNum
                  )
                }
              />
            ))}
          </div>
        )}
        {config?.homeLayout === 'table' && trackers.length > 0 && (
          <div className="px-2 overflow-x-auto">
            <TrackersTable
              flatTrackers={trackers}
              clickedTracker={(tracker) => sendToSettings(tracker)}
            ></TrackersTable>
          </div>
        )}
      </div>
    </div>
  );
}
