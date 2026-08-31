import type { FluentVariable } from '@fluent/bundle';
import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import {
  ReactNode,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { BodyPart } from 'solarxr-protocol';
import {
  BodySlotStyle,
  BodySlotStyler,
} from '@/components/commons/BodyInteractions';
import { Button } from '@/components/commons/Button';
import { ProgressBar } from '@/components/commons/ProgressBar';
import { Typography } from '@/components/commons/Typography';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { GearIcon } from '@/components/commons/icon/GearIcon';
import { BodyPartCardRenderer } from '@/components/onboarding/BodyAssignment';
import { useOnboarding } from '@/hooks/onboarding';
import { getTrackerName } from '@/hooks/tracker';
import {
  FlatDeviceTracker,
  groupTrackersByConnection,
} from '@/store/app-store';
import { BodyAssignmentPanel, BodyPartCard } from './BodyAssignmentPanel';
import {
  AssignmentEmptyState,
  AssignmentNavFooter,
  SimpleTrackerRow,
  UnassignedTrackerList,
} from './TrackerAssignmentList';
import { TrackerAssignment } from '@/hooks/tracker-assignment';

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
  footer: ReactNode;
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
  const footerRow = <div className="px-2 pb-2.5 pt-2 shrink-0">{footer}</div>;

  return (
    <>
      <div
        className={classNames(
          'fixed inset-0 z-10 bg-black/50 transition-opacity duration-200',
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

export function MobileTrackerAssign({
  assignment,
  onOpenSettings,
}: {
  assignment: TrackerAssignment;
  onOpenSettings: () => void;
}) {
  const { l10n } = useLocalization();
  const { state } = useOnboarding();

  const { armedPart, setArmedPart, flatTrackers, assignedTrackers } =
    assignment;

  const groups = useMemo(
    () => groupTrackersByConnection(flatTrackers, assignment.dongles),
    [flatTrackers, assignment.dongles]
  );
  const unassignedCount = flatTrackers.length - assignedTrackers.length;

  const [selectedTrackerId, setSelectedTrackerId] = useState<number | null>(
    null
  );
  const [panelOpen, setPanelOpen] = useState(false);

  const prevArmedRef = useRef(armedPart);
  useEffect(() => {
    const prev = prevArmedRef.current;
    if (armedPart !== BodyPart.NONE && prev !== armedPart) setPanelOpen(true);
    if (armedPart === BodyPart.NONE && prev !== BodyPart.NONE) {
      setPanelOpen(false);
    }
    prevArmedRef.current = armedPart;
  }, [armedPart]);

  useEffect(() => {
    if (selectedTrackerId !== null) setPanelOpen(false);
  }, [selectedTrackerId]);

  const selectedTracker =
    selectedTrackerId != null
      ? flatTrackers.find((td) => td.tracker.trackerId === selectedTrackerId)
      : undefined;
  const armedTracker =
    armedPart !== BodyPart.NONE
      ? (assignment.trackersByPart[armedPart] || [])[0]
      : undefined;

  const partName = (part: BodyPart) =>
    l10n.getString('body_part-' + BodyPart[part]);
  const trackerName = (td: FlatDeviceTracker) =>
    getTrackerName(td.tracker.info);

  const cancelSelection = () => {
    setSelectedTrackerId(null);
    setArmedPart(BodyPart.NONE);
  };

  const dismissPanel = () => {
    cancelSelection();
    setPanelOpen(false);
  };

  const onPartSelected = (role: BodyPart) => {
    if (selectedTrackerId != null) {
      assignment.handleDropTracker(selectedTrackerId, role);
      setSelectedTrackerId(null);
      return;
    }
    assignment.armForTap(role);
  };

  const onTrackerSelected = (td: FlatDeviceTracker) => {
    const id = td.tracker.trackerId;
    if (armedPart !== BodyPart.NONE) {
      assignment.handleDropTracker(id, armedPart);
      setArmedPart(BodyPart.NONE);
      setSelectedTrackerId(null);
      return;
    }
    setSelectedTrackerId((prev) => (prev === id ? null : id));
  };

  const renderCard: BodyPartCardRenderer = useCallback(
    ({ role, direction, td, roleError }) => (
      <BodyPartCard
        key={role}
        mode="tap"
        role={role}
        direction={direction}
        td={td}
        roleError={roleError}
        armed={armedPart === role}
        awaitingTracker={selectedTrackerId != null}
        onSelect={onPartSelected}
      />
    ),
    [armedPart, selectedTrackerId]
  );

  const slotStyle: BodySlotStyler = useCallback(
    (part: BodyPart): BodySlotStyle =>
      armedPart === part
        ? {
            connected: true,
            className: 'scale-150 ring-3 ring-accent-background-30',
          }
        : {},
    [armedPart]
  );

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
            dotSize={12}
            mobile
            headerAction={
              <button
                type="button"
                className="fill-background-30 hover:fill-background-20 cursor-pointer shrink-0 p-1"
                onClick={onOpenSettings}
              >
                <GearIcon size={18} />
              </button>
            }
            legendAction={
              state.alonePage && (
                <Button
                  variant="secondary"
                  onClick={assignment.unassignAll}
                  id="onboarding-assign_trackers-unassign_all"
                />
              )
            }
            highlightedRoles={assignment.firstError?.affectedRoles || []}
            rolesWithErrors={assignment.rolesWithErrors}
            onRoleSelected={onPartSelected}
            renderCard={renderCard}
            slotStyle={slotStyle}
          />
        </div>
      </div>

      <MobileAssignPanel
        open={panelOpen}
        alonePage={state.alonePage}
        onToggle={() => setPanelOpen((o) => !o)}
        onBackdropPress={dismissPanel}
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
            ? assignedTrackers.length / assignment.expectedTrackersCount
            : 0
        }
        footer={
          !state.alonePage ? (
            <AssignmentNavFooter
              assignedCount={assignedTrackers.length}
              trackerCount={flatTrackers.length}
            />
          ) : (
            <></>
          )
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
                  onPress={() => onTrackerSelected(td)}
                  selected={selectedTrackerId === td.tracker.trackerId}
                />
              )}
            />
          </div>
        )}
      </MobileAssignPanel>
    </div>
  );
}
