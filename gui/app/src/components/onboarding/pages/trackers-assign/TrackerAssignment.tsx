import classNames from 'classnames';
import { createPortal } from 'react-dom';
import { BodyPart } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { useBreakpoint } from '@/hooks/breakpoint';
import { useOnboarding } from '@/hooks/onboarding';
import { trackerDrag } from '@/hooks/tracker-drag';
import {
  AssignmentContext,
  useAssignment,
  useTrackerAssignment,
} from '@/hooks/tracker-assignment';
import { BodyAssignmentPanel } from './BodyAssignmentPanel';
import { MobileTrackerAssign } from './MobileTrackerAssign';
import { TrackerAssignmentList } from './TrackerAssignmentList';

export function TrackersAssignPage() {
  const { isMobileAssign } = useBreakpoint('mobileAssign');
  const { applyProgress } = useOnboarding();
  const assignment = useTrackerAssignment(isMobileAssign ? 'tap' : 'drag');
  applyProgress(0.5);

  return (
    <AssignmentContext.Provider value={assignment}>
      <NeckWarningModal
        isOpen={assignment.shouldShowChokerWarn}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full justify-center bg-background-90 bg-opacity-90 z-20'
        )}
        onClose={() => assignment.closeChokerWarning(true)}
        accept={() => assignment.closeChokerWarning(false)}
      />
      {isMobileAssign ? <MobileTrackerAssign /> : <DesktopTrackerAssign />}
    </AssignmentContext.Provider>
  );
}

function DesktopTrackerAssign() {
  const { unassignAll } = useAssignment();

  return (
    <>
      <DragGhostLayer />
      <TapAssignModal />

      <div className="w-full h-full flex flex-row overflow-hidden min-h-0">
        <TrackerAssignmentList />

        <div className="flex-1 flex flex-col gap-4 pt-4 min-h-0 overflow-hidden">
          <BodyAssignmentPanel
            headerAction={
              <Button
                variant="secondary"
                onClick={unassignAll}
                id="onboarding-assign_trackers-reset_assignments"
              />
            }
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

function TapAssignModal() {
  const { armedPart, clearPending } = useAssignment();
  const isOpen = armedPart !== BodyPart.NONE;

  return (
    <BaseModal
      isOpen={isOpen}
      appendClasses="max-w-md w-full"
      closeable
      onRequestClose={clearPending}
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
            id={'body_part-' + BodyPart[armedPart]}
          />
        )}
        <Typography id="onboarding-assign_trackers-tap_modal-description" />
        <Button
          variant="secondary"
          onClick={clearPending}
          id="onboarding-assign_trackers-tap_modal-cancel"
        />
      </div>
    </BaseModal>
  );
}
