import { Localized, useLocalization } from '@fluent/react';
import { NavLink, useNavigate } from 'react-router-dom';
import { StatusData, TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { useTrackers } from '@/hooks/tracker';
import { Typography } from '@/components/commons/Typography';
import { TrackerCard } from '@/components/tracker/TrackerCard';
import { TrackersTable } from '@/components/tracker/TrackersTable';
import {
  parseStatusToLocale,
  trackerStatusRelated,
  useStatusContext,
} from '@/hooks/status-system';
import { useMemo } from 'react';
import { WarningBox } from '@/components/commons/TipBox';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import classNames from 'classnames';

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
    <div className="relative h-full">
      <NavLink
        to="/vr-mode"
        className="xs:hidden absolute z-50 h-12 w-12 rounded-full bg-accent-background-30 bottom-3 right-3 flex justify-center items-center fill-background-10"
      >
        <HeadsetIcon></HeadsetIcon>
      </NavLink>
      <div className="h-full overflow-y-auto">
        <div
          className={classNames(
            'px-3 pt-3 gap-3 w-full grid md:grid-cols-2 mobile:grid-cols-1',
            filteredStatuses.filter(([, status]) => status.prioritized)
              .length === 0 && 'hidden'
          )}
        >
          {filteredStatuses
            .filter(([, status]) => status.prioritized)
            .map(([, status]) => (
              <Localized
                key={status.id}
                id={`status_system-${StatusData[status.dataType]}`}
                vars={parseStatusToLocale(status, trackers, l10n)}
              >
                <WarningBox whitespace={false}>
                  {`Warning, you should fix ${StatusData[status.dataType]}`}
                </WarningBox>
              </Localized>
            ))}
        </div>
        <div className="overflow-y-auto flex flex-col gap-3">
          {trackers.length === 0 && (
            <div className="flex px-5 pt-5 justify-center">
              <Typography variant="standard">
                {l10n.getString('home-no_trackers')}
              </Typography>
            </div>
          )}

          {!config?.debug && trackers.length > 0 && (
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
      </div>
    </div>
  );
}
