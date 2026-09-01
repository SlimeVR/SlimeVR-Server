import type { FluentVariable } from '@fluent/bundle';
import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { ReactNode, useMemo } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { Typography } from '@/components/commons/Typography';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { ShowAllPartsToggle } from '@/components/onboarding/BodyAssignment';
import { useOnboarding } from '@/hooks/onboarding';
import { getTrackerName } from '@/hooks/tracker';
import {
  FlatDeviceTracker,
  groupTrackersByConnection,
} from '@/store/app-store';
import { BodyAssignmentPanel } from './BodyAssignmentPanel';
import {
  AssignmentEmptyState,
  AssignmentNavFooter,
  SimpleTrackerRow,
  UnassignedTrackerList,
} from './TrackerAssignmentList';
import { useAssignment } from '@/hooks/tracker-assignment';

function MobileAssignPanel({
  open,
  alonePage,
  onToggle,
  onBackdropPress,
  title,
  description,
  headerAction,
  progress,
  footer,
  children,
}: {
  open: boolean;
  alonePage: boolean;
  onToggle: () => void;
  onBackdropPress: () => void;
  title: ReactNode;
  description: ReactNode;
  headerAction?: ReactNode;
  progress: number;
  footer?: ReactNode;
  children: ReactNode;
}) {
  const headerRow = (
    <div className="flex items-center gap-1 px-2 shrink-0">
      <button
        type="button"
        onClick={onToggle}
        className="flex-1 min-w-0 flex flex-col text-left px-2 py-2"
      >
        {title}
        {description}
      </button>
      {headerAction}
      <button
        type="button"
        onClick={onToggle}
        className="shrink-0 px-2 py-2 fill-background-10"
      >
        {open ? <ArrowDownIcon size={16} /> : <ArrowUpIcon size={16} />}
      </button>
    </div>
  );
  const footerRow = footer && (
    <div className="px-2 pb-2.5 pt-2 shrink-0">{footer}</div>
  );

  return (
    <>
      <div
        className={classNames(
          'fixed inset-0 z-10 bg-background-90/50 transition-opacity duration-200',
          open
            ? 'opacity-100 pointer-events-auto'
            : 'opacity-0 pointer-events-none'
        )}
        onClick={onBackdropPress}
      />
      <div aria-hidden className="invisible mx-2 rounded-xl border">
        {headerRow}
        {footerRow}
      </div>

      <div
        className={classNames(
          'fixed z-10 rounded-xl bg-background-60 border border-background-50/60 flex flex-col overflow-clip',
          alonePage
            ? 'w-[calc(100%-var(--navbar-w)-var(--page-margin)*3)] left-[calc(var(--navbar-w)+var(--page-margin))] bottom-[calc(var(--navbar-h)+var(--page-margin))] -mb-1'
            : 'bottom-1 w-full'
        )}
      >
        {headerRow}

        <div
          className={classNames(
            'overflow-y-auto px-2 transition-all duration-200',
            open ? 'max-h-[50vh] pt-1 pb-2' : 'max-h-0'
          )}
        >
          {children}
        </div>

        {footerRow}
        <div className="absolute inset-x-0 bottom-0">
          <ProgressBar progress={progress} height={3} bottom animated />
        </div>
      </div>
    </>
  );
}

export function MobileTrackerAssign() {
  const { l10n } = useLocalization();
  const { state } = useOnboarding();
  const assignment = useAssignment();
  const {
    tab,
    armedPart,
    pendingTrackerId,
    clearPending,
    panelOpen,
    togglePanel,
    flatTrackers,
    assignedTrackers,
    dongles,
    trackerByPart,
    selectTracker,
  } = assignment;

  const groups = useMemo(
    () => groupTrackersByConnection(flatTrackers, dongles),
    [flatTrackers, dongles]
  );
  const unassignedCount = flatTrackers.length - assignedTrackers.length;

  const selectedTracker =
    pendingTrackerId != null
      ? flatTrackers.find((td) => td.tracker.trackerId === pendingTrackerId)
      : undefined;
  const armedTracker =
    armedPart !== BodyPart.NONE ? trackerByPart[armedPart] : undefined;

  const partName = (part: BodyPart) =>
    l10n.getString('body_part-' + BodyPart[part]);
  const trackerName = (td: FlatDeviceTracker) =>
    getTrackerName(td.tracker.info);

  const noTrackers = flatTrackers.length === 0;
  const allAssigned =
    !noTrackers && assignedTrackers.length === flatTrackers.length;

  const header = ((): {
    titleId: string;
    titleVars?: Record<string, FluentVariable>;
    descId: string;
    descVars?: Record<string, FluentVariable>;
  } => {
    if (selectedTracker)
      return {
        titleId: 'onboarding-assign_trackers-mobile-choose_spot',
        titleVars: { tracker: trackerName(selectedTracker) },
        descId: 'onboarding-assign_trackers-mobile-choose_spot-hint',
      };

    if (armedPart !== BodyPart.NONE)
      return {
        titleId: 'onboarding-assign_trackers-mobile-choose_tracker',
        titleVars: { part: partName(armedPart) },
        ...(armedTracker
          ? {
              descId:
                'onboarding-assign_trackers-mobile-choose_tracker-current',
              descVars: { tracker: trackerName(armedTracker) },
            }
          : {
              descId: 'onboarding-assign_trackers-mobile-choose_tracker-hint',
            }),
      };

    if (allAssigned)
      return {
        titleId: 'onboarding-assign_trackers-all_assigned-title',
        descId: 'onboarding-assign_trackers-all_assigned-description',
      };

    if (noTrackers)
      return {
        titleId: 'onboarding-assign_trackers-no_trackers-title',
        descId: 'onboarding-assign_trackers-no_trackers-description',
      };

    return {
      titleId: 'onboarding-assign_trackers-mobile-idle_title',
      titleVars: { remaining: unassignedCount },
      descId: panelOpen
        ? 'onboarding-assign_trackers-mobile-idle_hint-open'
        : 'onboarding-assign_trackers-mobile-idle_hint',
    };
  })();

  return (
    <div className="relative w-full h-full grid grid-rows-[1fr_auto] overflow-hidden">
      <div className="min-h-0 overflow-hidden pt-1">
        <div className="h-full flex flex-col">
          <BodyAssignmentPanel
            compact
            headerAction={tab === 'body' && <ShowAllPartsToggle compact />}
            legendAction={
              state.alonePage && (
                <Button
                  variant="secondary"
                  className="whitespace-nowrap !px-3 !py-1.5"
                  onClick={assignment.unassignAll}
                  id="onboarding-assign_trackers-unassign_all"
                />
              )
            }
          />
        </div>
      </div>

      <MobileAssignPanel
        open={panelOpen}
        alonePage={state.alonePage}
        onToggle={togglePanel}
        onBackdropPress={clearPending}
        title={
          <Typography
            bold
            truncate
            id={header.titleId}
            vars={header.titleVars}
          />
        }
        description={
          <Typography
            truncate
            color="secondary"
            id={header.descId}
            vars={header.descVars}
          />
        }
        headerAction={
          armedPart !== BodyPart.NONE && armedTracker ? (
            <Button
              variant="tertiary"
              onClick={() => assignment.unassignPart(armedPart)}
              id="onboarding-assign_trackers-mobile-unassign"
              vars={{ part: partName(armedPart) }}
            />
          ) : undefined
        }
        progress={
          assignment.expectedTrackersCount
            ? assignment.assignedPartsCount / assignment.expectedTrackersCount
            : 0
        }
        footer={
          !state.alonePage ? (
            <AssignmentNavFooter
              assignedCount={assignedTrackers.length}
              trackerCount={flatTrackers.length}
            />
          ) : undefined
        }
      >
        {noTrackers ? (
          <AssignmentEmptyState kind="no-trackers" iconSize={48} />
        ) : allAssigned ? (
          <AssignmentEmptyState kind="all-assigned" iconSize={48} />
        ) : (
          <div className="flex flex-col gap-4">
            <UnassignedTrackerList
              groups={groups}
              linkHeader={false}
              variant="secondary"
              renderTracker={(td) => (
                <SimpleTrackerRow
                  tracker={td.tracker}
                  device={td.device}
                  variant="secondary"
                  onClick={() => selectTracker(td.tracker.trackerId)}
                  selected={pendingTrackerId === td.tracker.trackerId}
                />
              )}
            />
          </div>
        )}
      </MobileAssignPanel>
    </div>
  );
}
