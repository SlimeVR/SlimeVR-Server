import { ReactNode, useMemo } from 'react';
import { BodyPart, DongleDataT } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { GearIcon } from '@/components/commons/icon/GearIcon';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import {
  TrackerConnectionGroupCollapseToolbox,
  TrackerConnectionGroupSection,
} from '@/components/tracker/TrackerConnectionGroup';
import {
  FlatDeviceTracker,
  groupTrackersByConnection,
} from '@/store/app-store';
import { bodyPartDropProps } from '@/hooks/tracker-drag';
import { DeviceTrackerGroup } from './DeviceTrackerGroup';

const groupByDevice = (
  trackers: FlatDeviceTracker[]
): FlatDeviceTracker[][] => {
  const order: number[] = [];
  const byDevice = new Map<number, FlatDeviceTracker[]>();

  trackers.forEach((td) => {
    const key = td.device?.id ?? td.tracker.trackerId;
    if (!byDevice.has(key)) {
      order.push(key);
      byDevice.set(key, []);
    }
    byDevice.get(key)!.push(td);
  });

  return order.map((key) => byDevice.get(key)!);
};

export function TrackerAssignmentList({
  trackers,
  dongles,
  assignedCount,
  expectedTrackersCount,
  onOpenSettings,
  onDropTracker,
  footer,
}: {
  trackers: FlatDeviceTracker[];
  dongles: DongleDataT[];
  assignedCount: number;
  expectedTrackersCount: number;
  onOpenSettings: () => void;
  onDropTracker: (trackerId: number, bodyPart: BodyPart) => void;
  footer?: ReactNode;
}) {
  const groups = useMemo(
    () => groupTrackersByConnection(trackers, dongles),
    [trackers, dongles]
  );

  return (
    <div className="w-full xs:w-[340px] sm:w-[380px] lg:w-[400px] xl:w-[440px] p-5 flex flex-col gap-4 shrink-0 min-h-0 border-b xs:border-b-0 xs:border-r border-background-60">
      <div className="flex flex-col gap-1 shrink-0">
        <Typography
          variant="mobile-title"
          id="onboarding-assign_trackers-title"
        />
        <Typography
          variant="standard"
          whitespace="whitespace-pre-wrap"
          id="onboarding-assign_trackers-description"
        />
      </div>

      <div className="flex items-center gap-2 shrink-0">
        <Typography
          color="secondary"
          id="onboarding-assign_trackers-assigned"
          vars={{ assigned: assignedCount, trackers: expectedTrackersCount }}
        />
        <div className="flex-grow">
          <ProgressBar
            progress={
              expectedTrackersCount ? assignedCount / expectedTrackersCount : 0
            }
            height={4}
          />
        </div>
        <button
          type="button"
          className="fill-background-30 hover:fill-background-20 cursor-pointer shrink-0"
          onClick={onOpenSettings}
        >
          <GearIcon size={18} />
        </button>
      </div>

      <div
        {...bodyPartDropProps(BodyPart.NONE)}
        className="flex flex-col gap-4 min-h-0 flex-1 overflow-y-auto -mx-2 px-2"
      >
        {trackers.length === 0 ? (
          <div className="flex flex-col items-center justify-center flex-1 gap-3 text-center p-6 my-auto">
            <LoaderIcon slimeState={SlimeState.JUMPY} size={56} />
            <div className="flex flex-col gap-1 max-w-xs">
              <Typography
                bold
                variant="section-title"
                id="onboarding-assign_trackers-no_trackers-title"
              />
              <Typography
                variant="standard"
                id="onboarding-assign_trackers-no_trackers-description"
              />
            </div>
          </div>
        ) : assignedCount === trackers.length ? (
          <div className="flex flex-col items-center justify-center flex-1 gap-3 text-center p-6 my-auto">
            <LoaderIcon slimeState={SlimeState.HAPPY} size={56} />
            <div className="flex flex-col gap-1 max-w-xs">
              <Typography
                bold
                variant="section-title"
                color="text-status-success"
                id="onboarding-assign_trackers-all_assigned-title"
              />
              <Typography
                variant="standard"
                id="onboarding-assign_trackers-all_assigned-description"
              />
            </div>
          </div>
        ) : (
          groups
            .filter((group) => group.unassigned.length > 0)
            .map((group) => (
              <TrackerConnectionGroupSection
                key={group.key}
                group={group}
                toolbox={
                  <TrackerConnectionGroupCollapseToolbox group={group} />
                }
              >
                <div className="flex flex-col gap-3 px-2">
                  {groupByDevice(group.unassigned).map(
                    (deviceTrackers, index) => (
                      <DeviceTrackerGroup
                        key={index}
                        trackers={deviceTrackers}
                        onDropTracker={onDropTracker}
                      />
                    )
                  )}
                </div>
              </TrackerConnectionGroupSection>
            ))
        )}
      </div>

      {footer && (
        <div className="shrink-0 pt-4 border-t border-background-60">
          {footer}
        </div>
      )}
    </div>
  );
}
