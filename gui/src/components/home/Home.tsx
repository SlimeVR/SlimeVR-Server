import { useLocalization } from '@fluent/react';
import { NavLink, useNavigate } from 'react-router-dom';
import { TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { Typography } from '@/components/commons/Typography';
import { TrackerCard } from '@/components/tracker/TrackerCard';
import { TrackersTable } from '@/components/tracker/TrackersTable';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import { useAtomValue } from 'jotai';
import {
  assignedTrackersAtom,
  unassignedTrackersAtom,
} from '@/store/app-store';
import { useTrackingChecklist } from '@/hooks/tracking-checklist';
import { Checklist } from '@/components/commons/icon/ChecklistIcon';
import { useState } from 'react';
import { HomeSettingsModal } from './HomeSettingsModal';
import { LayoutIcon } from '@/components/commons/icon/LayoutIcon';

export function Home() {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const trackers = useAtomValue(assignedTrackersAtom);
  const unassignedTrackers = useAtomValue(unassignedTrackersAtom);
  const { highlightedTrackers } = useTrackingChecklist();
  const navigate = useNavigate();

  const sendToSettings = (tracker: TrackerDataT) => {
    navigate(
      `/tracker/${tracker.trackerId?.trackerNum}/${tracker.trackerId?.deviceId?.id}`
    );
  };

  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;

  return (
    <div className="relative h-full">
      <HomeSettingsModal open={settingsOpenState} />
      <NavLink
        to="/vr-mode"
        className="xs:hidden absolute z-50 h-12 w-12 rounded-full bg-accent-background-30 bottom-3 right-3 flex justify-center items-center fill-background-10"
      >
        <HeadsetIcon />
      </NavLink>
      <NavLink
        to="/checklist"
        className="xs:hidden absolute z-50 h-12 w-12 rounded-full bg-accent-background-30 bottom-[70px] right-3 flex justify-center items-center fill-background-10"
      >
        <Checklist />
      </NavLink>
      <div className="overflow-y-auto flex flex-col gap-3">
        <div className="flex w-full gap-2 items-center px-4 h-5">
          <Typography
            color="secondary"
            id="toolbar-assigned_trackers"
            vars={{ count: trackers.length }}
          />
          <div className="bg-background-50 h-[2px] rounded-lg flex-grow" />
          <div
            className="fill-background-30 hover:fill-background-20 cursor-pointer"
            onClick={() => setSettingsOpen(true)}
          >
            <LayoutIcon size={18} />
          </div>
        </div>
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
                  !!highlightedTrackers?.trackers.find(
                    (t) =>
                      t?.deviceId?.id === tracker.trackerId?.deviceId?.id &&
                      t?.trackerNum === tracker.trackerId?.trackerNum
                  ) && highlightedTrackers.step
                }
              />
            ))}
          </div>
        )}

        {config?.homeLayout === 'table' && trackers.length > 0 && (
          <div className="mx-2 overflow-x-auto">
            <TrackersTable
              flatTrackers={trackers}
              clickedTracker={(tracker) => sendToSettings(tracker)}
            />
          </div>
        )}

        {unassignedTrackers.length > 0 && (
          <>
            <div className="flex w-full gap-2 items-center px-4 h-5">
              <Typography
                color="secondary"
                id="toolbar-unassigned_trackers"
                vars={{ count: unassignedTrackers.length }}
              />
              <div className="bg-background-50 h-[2px] rounded-lg flex-grow" />
            </div>
            {config?.homeLayout == 'default' && (
              <div className="grid sm:grid-cols-1 md:grid-cols-2 gap-4 px-5 my-3">
                {unassignedTrackers.map(({ tracker, device }, index) => (
                  <TrackerCard
                    key={index}
                    tracker={tracker}
                    device={device}
                    onClick={() => sendToSettings(tracker)}
                    smol
                    showUpdates
                    interactable
                    warning={
                      !!highlightedTrackers?.trackers.find(
                        (t) =>
                          t?.deviceId?.id === tracker.trackerId?.deviceId?.id &&
                          t?.trackerNum === tracker.trackerId?.trackerNum
                      ) && highlightedTrackers.step
                    }
                  />
                ))}
              </div>
            )}
            {config?.homeLayout === 'table' && (
              <div className="mx-2 overflow-x-auto">
                <TrackersTable
                  flatTrackers={unassignedTrackers}
                  clickedTracker={(tracker) => sendToSettings(tracker)}
                />
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
