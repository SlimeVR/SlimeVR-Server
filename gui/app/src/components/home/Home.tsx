import { useLocalization } from '@fluent/react';
import { NavLink, useNavigate } from 'react-router-dom';
import { TrackerDataT } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { Typography } from '@/components/commons/Typography';
import { TrackerCard } from '@/components/tracker/TrackerCard';
import { TrackersTable } from '@/components/tracker/TrackersTable';
import {
  TrackerConnectionGroupSection,
  TrackerConnectionGroupUnassignedDivider,
} from '@/components/tracker/TrackerConnectionGroup';
import { HeadsetIcon } from '@/components/commons/icon/HeadsetIcon';
import { useAtomValue } from 'jotai';
import {
  assignedTrackersAtom,
  donglesAtom,
  flatTrackersAtom,
  groupTrackersByConnection,
  TrackerConnectionGroup,
} from '@/store/app-store';
import { useTrackingChecklist } from '@/hooks/tracking-checklist';
import { Checklist } from '@/components/commons/icon/ChecklistIcon';
import { useMemo, useState } from 'react';
import { HomeSettingsModal } from './HomeSettingsModal';
import { GroupTelemetryOverlay } from './GroupTelemetryOverlay';
import { LayoutIcon } from '@/components/commons/icon/LayoutIcon';

export function Home() {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const trackers = useAtomValue(assignedTrackersAtom);
  const allTrackers = useAtomValue(flatTrackersAtom);
  const dongles = useAtomValue(donglesAtom);
  const { highlightedTrackers } = useTrackingChecklist();
  const navigate = useNavigate();

  const groups = useMemo(
    () => groupTrackersByConnection(allTrackers, dongles),
    [allTrackers, dongles]
  );

  const sendToSettings = (tracker: TrackerDataT) => {
    navigate(`/tracker/${tracker.trackerId}/${tracker.deviceId}`);
  };

  const settingsOpenState = useState(false);
  const [, setSettingsOpen] = settingsOpenState;
  const [telemetryGroup, setTelemetryGroup] =
    useState<TrackerConnectionGroup | null>(null);

  return (
    <div className="relative h-full">
      <HomeSettingsModal open={settingsOpenState} />
      <GroupTelemetryOverlay
        group={telemetryGroup}
        onClose={() => setTelemetryGroup(null)}
      />
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
      <div className="overflow-y-auto flex flex-col gap-3 h-full">
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

        {config?.homeLayout == 'default' && groups.length > 0 && (
          <div className="pl-2 pr-2 flex flex-col gap-4">
            {groups.map((group) => (
              <TrackerConnectionGroupSection
                key={group.key}
                group={group}
                onOpenMetrics={(g) => setTelemetryGroup(g)}
              >
                <div className="flex flex-col gap-3">
                  {group.assigned.length > 0 && (
                    <div className="grid sm:grid-cols-1 md:grid-cols-2 gap-4 px-2">
                      {group.assigned.map(({ tracker, device }, index) => (
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
                              (t) => t === tracker.trackerId
                            ) && highlightedTrackers.step
                          }
                        />
                      ))}
                    </div>
                  )}
                  {group.unassigned.length > 0 && (
                    <>
                      {group.assigned.length > 0 && (
                        <TrackerConnectionGroupUnassignedDivider
                          count={group.unassigned.length}
                        />
                      )}
                      <div className="grid sm:grid-cols-1 md:grid-cols-2 gap-4 px-2">
                        {group.unassigned.map(({ tracker, device }, index) => (
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
                                (t) => t === tracker.trackerId
                              ) && highlightedTrackers.step
                            }
                          />
                        ))}
                      </div>
                    </>
                  )}
                </div>
              </TrackerConnectionGroupSection>
            ))}
          </div>
        )}

        {config?.homeLayout === 'table' && groups.length > 0 && (
          <div className="mx-2 flex flex-col flex-grow min-h-0">
            <TrackersTable
              groups={groups}
              clickedTracker={(tracker) => sendToSettings(tracker)}
              onOpenMetrics={(g) => setTelemetryGroup(g)}
            />
          </div>
        )}
      </div>
    </div>
  );
}
