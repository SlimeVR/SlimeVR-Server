import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { useAtomValue } from 'jotai';
import { selectAtom } from 'jotai/utils';
import { useCallback, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { createPortal } from 'react-dom';
import { BodyPart } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import {
  BodySlotStyle,
  BodySlotStyler,
} from '@/components/commons/BodyInteractions';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { BodyPartCardRenderer } from '@/components/onboarding/BodyAssignment';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useConfig } from '@/hooks/config';
import { useOnboarding } from '@/hooks/onboarding';
import { bodyPartDropProps, trackerDrag } from '@/hooks/tracker-drag';
import { BodyAssignmentPanel, BodyPartCard } from './BodyAssignmentPanel';
import { MobileTrackerAssign } from './MobileTrackerAssign';
import { TrackerAssignmentList } from './TrackerAssignmentList';
import {
  TrackerAssignment,
  useTrackerAssignment,
} from '@/hooks/tracker-assignment';

export function TrackersAssignPage() {
  const { isMobileAssign } = useBreakpoint('mobileAssign');
  const { applyProgress } = useOnboarding();
  const assignment = useTrackerAssignment();
  const [settingsOpen, setSettingsOpen] = useState(false);
  applyProgress(0.5);

  return (
    <>
      <NeckWarningModal
        isOpen={assignment.shouldShowChokerWarn}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full justify-center bg-background-90 bg-opacity-90 z-20'
        )}
        onClose={() => assignment.closeChokerWarning(true)}
        accept={() => assignment.closeChokerWarning(false)}
      />
      {settingsOpen && (
        <AssignmentSettingsModal
          isOpen={settingsOpen}
          onClose={() => setSettingsOpen(false)}
        />
      )}
      {isMobileAssign ? (
        <MobileTrackerAssign
          assignment={assignment}
          onOpenSettings={() => setSettingsOpen(true)}
        />
      ) : (
        <DesktopTrackerAssign
          assignment={assignment}
          onOpenSettings={() => setSettingsOpen(true)}
        />
      )}
    </>
  );
}

const hoveredBodyPartAtom = selectAtom(
  trackerDrag.stateAtom,
  (s) => s?.target ?? null
);

function DesktopTrackerAssign({
  assignment,
  onOpenSettings,
}: {
  assignment: TrackerAssignment;
  onOpenSettings: () => void;
}) {
  const hoveredBodyPart = useAtomValue(hoveredBodyPartAtom);

  const slotStyle: BodySlotStyler = useCallback(
    (part: BodyPart): BodySlotStyle => ({
      props: bodyPartDropProps(part),
      connected: hoveredBodyPart === part,
      className:
        hoveredBodyPart === part
          ? 'scale-150 ring-3 ring-accent-background-30'
          : undefined,
    }),
    [hoveredBodyPart]
  );

  const renderCard: BodyPartCardRenderer = useCallback(
    ({ role, direction, td, roleError }) => (
      <BodyPartCard
        key={role}
        mode="drag"
        role={role}
        direction={direction}
        td={td}
        roleError={roleError}
        armed={assignment.armedPart === role}
        onSlotClick={assignment.armForTap}
        onUnassign={assignment.unassignPart}
        onDropTracker={assignment.handleDropTracker}
      />
    ),
    [assignment.armedPart]
  );

  return (
    <>
      <DragGhostLayer />
      <TapAssignModal
        role={assignment.armedPart}
        onClose={() => assignment.setArmedPart(BodyPart.NONE)}
      />

      <div className="w-full h-full flex flex-row overflow-hidden min-h-0">
        <TrackerAssignmentList
          trackers={assignment.flatTrackers}
          dongles={assignment.dongles}
          assignedCount={assignment.assignedTrackers.length}
          assignedPartsCount={assignment.assignedPartsCount}
          expectedTrackersCount={assignment.expectedTrackersCount}
          onOpenSettings={onOpenSettings}
          onDropTracker={assignment.handleDropTracker}
        />

        <div className="flex-1 flex flex-col gap-4 pt-4 min-h-0 overflow-hidden">
          <BodyAssignmentPanel
            dotSize={15}
            headerAction={
              <Button
                variant="secondary"
                onClick={assignment.unassignAll}
                id="onboarding-assign_trackers-unassign_all"
              />
            }
            highlightedRoles={assignment.firstError?.affectedRoles || []}
            rolesWithErrors={assignment.rolesWithErrors}
            onRoleSelected={assignment.onDotSelected}
            renderCard={renderCard}
            slotStyle={slotStyle}
          />
        </div>
      </div>
    </>
  );
}

function DragGhostLayer() {
  const drag = trackerDrag.useDragGhost();
  if (!drag) return null;

  return createPortal(
    <div
      className="fixed z-50 pointer-events-none -translate-x-1/2 -translate-y-1/2 rounded-lg bg-background-50 outline outline-2 outline-accent-background-40 shadow-lg px-4 py-2 max-w-[220px]"
      style={{ left: drag.x, top: drag.y }}
    >
      <Typography bold truncate>
        {drag.payload.label}
      </Typography>
    </div>,
    document.body
  );
}

function TapAssignModal({
  role,
  onClose,
}: {
  role: BodyPart;
  onClose: () => void;
}) {
  const isOpen = role !== BodyPart.NONE;

  return (
    <BaseModal
      isOpen={isOpen}
      appendClasses="max-w-md w-full"
      closeable
      onRequestClose={onClose}
    >
      <div className="flex flex-col gap-4 items-center text-center">
        <Typography
          variant="main-title"
          id="onboarding-assign_trackers-tap_modal-title"
        />
        {isOpen && (
          <Typography
            bold
            variant="section-title"
            color="text-accent-background-10"
            id={'body_part-' + BodyPart[role]}
          />
        )}
        <Typography id="onboarding-assign_trackers-tap_modal-description" />
        <Button
          variant="secondary"
          onClick={onClose}
          id="onboarding-assign_trackers-tap_modal-cancel"
        />
      </div>
    </BaseModal>
  );
}

function AssignmentSettingsModal({
  isOpen,
  onClose,
}: {
  isOpen: boolean;
  onClose: () => void;
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();

  const { control, watch } = useForm<{
    showAllBodyParts: boolean;
    mirrorView: boolean;
  }>({
    defaultValues: {
      showAllBodyParts: config?.assignShowAllBodyParts ?? false,
      mirrorView: config?.mirrorView ?? false,
    },
  });
  const { showAllBodyParts, mirrorView } = watch();

  useEffect(() => {
    setConfig({
      assignShowAllBodyParts: showAllBodyParts,
      mirrorView: mirrorView,
    });
  }, [showAllBodyParts, mirrorView]);

  return (
    <BaseModal
      isOpen={isOpen}
      appendClasses="max-w-md w-full"
      closeable
      onRequestClose={onClose}
    >
      <div className="flex flex-col gap-2">
        <Typography
          variant="main-title"
          id="onboarding-assign_trackers-settings"
        />
        <CheckBox
          control={control}
          label={l10n.getString('onboarding-assign_trackers-mirror_view')}
          name="mirrorView"
          variant="toggle"
        />
        <CheckBox
          control={control}
          label={l10n.getString('onboarding-assign_trackers-show_all')}
          name="showAllBodyParts"
          variant="toggle"
        />
        <div className="flex justify-end">
          <Button
            variant="tertiary"
            onClick={onClose}
            id="onboarding-assign_trackers-settings-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}
