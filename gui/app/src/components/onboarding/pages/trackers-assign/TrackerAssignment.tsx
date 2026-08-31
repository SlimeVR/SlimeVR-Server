import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  AssignTrackerRequestT,
  BodyPart,
  QuatT,
  RpcMessage,
  TapDetectionSetupModeRequestT,
  TapDetectionSetupNotificationT,
} from 'solarxr-protocol';
import { useChokerWarning } from '@/hooks/choker-warning';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import {
  ASSIGN_MODE_OPTIONS,
  ASSIGNMENT_RULES,
  BodyAssignment,
  BodyPartCardRenderer,
  getPreferredAssignMode,
  LOWER_BODY,
} from '@/components/onboarding/BodyAssignment';
import {
  BodySlotStyle,
  BodySlotStyler,
} from '@/components/commons/BodyInteractions';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { AssignMode, useConfig } from '@/hooks/config';
import { playTapSetupSound } from '@/sounds/sounds';
import { useAtomValue } from 'jotai';
import { selectAtom } from 'jotai/utils';
import {
  assignedTrackersAtom,
  connectedIMUTrackersAtom,
  donglesAtom,
  FlatDeviceTracker,
  flatTrackersAtom,
} from '@/store/app-store';
import { bodyPartDropProps, trackerDrag } from '@/hooks/tracker-drag';
import { TrackerAssignmentList } from './TrackerAssignmentList';
import { DropTargetPartCard } from './DropTargetPartCard';
import { TrackerAssignmentTabs } from './TrackerAssignmentTabs';
import { AssignmentSettingsModal } from './AssignmentSettingsModal';
import { DragGhostLayer } from './DragGhostLayer';
import { SideLegend } from './SideLegend';
import { TapAssignModal } from './TapAssignModal';

export type BodyPartError = {
  label: string | undefined;
  affectedRoles: BodyPart[];
};

const hoveredBodyPartAtom = selectAtom(
  trackerDrag.stateAtom,
  (s) => s?.target ?? null
);

export function TrackersAssignPage() {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { applyProgress, state, slimeSet } = useOnboarding();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const [armedPart, setArmedPart] = useState<BodyPart>(BodyPart.NONE);
  const [settingsOpen, setSettingsOpen] = useState(false);
  const hoveredBodyPart = useAtomValue(hoveredBodyPartAtom);

  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const flatTrackers = useAtomValue(flatTrackersAtom);
  const dongles = useAtomValue(donglesAtom);
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.TapDetectionSetupModeRequest,
      new TapDetectionSetupModeRequestT(true)
    );

    return () => {
      sendRPCPacket(
        RpcMessage.TapDetectionSetupModeRequest,
        new TapDetectionSetupModeRequestT(false)
      );
    };
  }, []);

  const trackersByPart = useMemo(() => {
    const map: Partial<Record<BodyPart, FlatDeviceTracker[]>> = {};
    assignedTrackers.forEach((td) => {
      const part = td.tracker.info?.bodyPart ?? BodyPart.NONE;
      map[part] = [...(map[part] || []), td];
    });
    return map;
  }, [assignedTrackers]);

  const currentAssignMode = config?.assignShowAllBodyParts
    ? AssignMode.All
    : getPreferredAssignMode(connectedIMUTrackers.length);

  const expectedTrackersCount = ASSIGN_MODE_OPTIONS[currentAssignMode];

  const rolesWithErrors = useMemo(() => {
    const trackerRoles = flatTrackers.map(
      ({ tracker }) => tracker.info?.bodyPart || BodyPart.NONE
    );

    const message = (assignedRole: BodyPart) => {
      const unassignedRoles: [BodyPart | BodyPart[], boolean][] = (
        ASSIGNMENT_RULES[assignedRole] || []
      ).map((part) => [
        part,
        Array.isArray(part)
          ? trackerRoles.some((tr) => part.includes(tr))
          : trackerRoles.includes(part),
      ]);

      // Special exception for waist/hip: https://github.com/SlimeVR/SlimeVR-Server/issues/612
      if (
        (assignedRole === BodyPart.HIP || assignedRole === BodyPart.WAIST) &&
        !trackerRoles.some((t) => LOWER_BODY.has(t))
      ) {
        return;
      }

      if (unassignedRoles.every(([, state]) => state)) return;

      return {
        affectedRoles: unassignedRoles
          .filter(([, state]) => !state)
          .flatMap(([part]) => part),
        label: l10n.getString(
          `onboarding-assign_trackers-warning-${BodyPart[assignedRole]}`,
          {
            unassigned: unassignedRoles
              .map(([, state]) => state)
              .reduce((acc, cur, i) => acc + (Number(cur) << i), 0),
          }
        ),
      };
    };

    return Object.keys(BodyPart)
      .map<BodyPart>((key) => +key)
      .filter((key) => typeof key === 'number' && !Number.isNaN(key))
      .reduce<Record<BodyPart, BodyPartError>>((curr, role) => {
        return {
          ...curr,
          [role]: trackerRoles.find((tr) => tr === role)
            ? message(role)
            : undefined,
        };
      }, {} as any);
  }, [flatTrackers]);

  const firstError = Object.values(rolesWithErrors).find((r) => !!r);

  const sendAssign = (
    trackerId: number,
    bodyPart: BodyPart,
    mountingOrientation: QuatT | null
  ) => {
    const assignreq = new AssignTrackerRequestT();
    assignreq.bodyPosition = bodyPart;
    assignreq.mountingOrientation = mountingOrientation;
    assignreq.trackerId = trackerId;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, assignreq);
  };

  const moveTrackerToBodyPart = (trackerId: number, bodyPart: BodyPart) => {
    if (bodyPart !== BodyPart.NONE) {
      assignedTrackers
        .filter(
          (td) =>
            td.tracker.info?.bodyPart === bodyPart &&
            td.tracker.trackerId !== trackerId
        )
        .forEach((occupant) =>
          sendAssign(
            occupant.tracker.trackerId,
            BodyPart.NONE,
            occupant.tracker.info?.mountingOrientation ?? null
          )
        );
    }

    const moved = flatTrackers.find((td) => td.tracker.trackerId === trackerId);
    sendAssign(
      trackerId,
      bodyPart,
      moved?.tracker.info?.mountingOrientation ?? null
    );
  };

  const { tryOpenChokerWarning, closeChokerWarning, shouldShowChokerWarn } =
    useChokerWarning<{ bodyPart: BodyPart; trackerId?: number }>({
      getBodyPart: (v) => v.bodyPart,
      next: ({ bodyPart, trackerId }) => {
        if (trackerId != null) {
          moveTrackerToBodyPart(trackerId, bodyPart);
        } else {
          setArmedPart(bodyPart);
        }
      },
    });

  const armForTap = (part: BodyPart) => {
    if (armedPart === part) {
      setArmedPart(BodyPart.NONE);
      return;
    }
    tryOpenChokerWarning({ bodyPart: part });
  };

  const handleDropTracker = (trackerId: number, bodyPart: BodyPart) => {
    tryOpenChokerWarning({ bodyPart, trackerId });
  };

  const unassignPart = (part: BodyPart) => {
    (trackersByPart[part] || []).forEach((td) =>
      sendAssign(
        td.tracker.trackerId,
        BodyPart.NONE,
        td.tracker.info?.mountingOrientation ?? null
      )
    );
    if (armedPart === part) setArmedPart(BodyPart.NONE);
  };

  const unassignAll = () => {
    assignedTrackers.forEach((td) =>
      sendAssign(td.tracker.trackerId, BodyPart.NONE, null)
    );
    setArmedPart(BodyPart.NONE);
  };

  const onDotSelected = (role: BodyPart) => {
    if ((trackersByPart[role] || []).length > 0) {
      unassignPart(role);
    } else {
      armForTap(role);
    }
  };

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
      <DropTargetPartCard
        key={role}
        role={role}
        direction={direction}
        td={td}
        roleError={roleError}
        armed={armedPart === role}
        onSlotClick={armForTap}
        onUnassign={unassignPart}
        onDropTracker={handleDropTracker}
      />
    ),
    [armedPart]
  );

  useRPCPacket(
    RpcMessage.TapDetectionSetupNotification,
    (tapSetup: TapDetectionSetupNotificationT) => {
      if (armedPart === BodyPart.NONE || !tapSetup.trackerId) return;
      handleDropTracker(tapSetup.trackerId, armedPart);
      setArmedPart(BodyPart.NONE);
      playTapSetupSound(config?.feedbackSoundVolume);
    }
  );

  applyProgress(0.5);

  return (
    <>
      <DragGhostLayer />
      <NeckWarningModal
        isOpen={shouldShowChokerWarn}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full justify-center bg-background-90 bg-opacity-90 z-20'
        )}
        onClose={() => closeChokerWarning(true)}
        accept={() => closeChokerWarning(false)}
      />
      {settingsOpen && (
        <AssignmentSettingsModal
          isOpen={settingsOpen}
          onClose={() => setSettingsOpen(false)}
        />
      )}
      <TapAssignModal
        role={armedPart}
        onClose={() => setArmedPart(BodyPart.NONE)}
      />

      <div className="w-full h-full flex flex-col xs:flex-row overflow-hidden min-h-0">
        <TrackerAssignmentList
          trackers={flatTrackers}
          dongles={dongles}
          assignedCount={assignedTrackers.length}
          expectedTrackersCount={expectedTrackersCount}
          onOpenSettings={() => setSettingsOpen(true)}
          onDropTracker={handleDropTracker}
          footer={
            <div className="flex flex-row justify-between items-center">
              {state.alonePage ? (
                <Button variant="secondary" to="/onboarding/connect-trackers">
                  {l10n.getString('onboarding-previous_step')}
                </Button>
              ) : (
                <Button
                  variant="secondary"
                  to={
                    slimeSet && ['butterfly', 'dongle-slime'].includes(slimeSet)
                      ? '/onboarding/dongle'
                      : '/onboarding/connect-trackers'
                  }
                >
                  {l10n.getString('onboarding-previous_step')}
                </Button>
              )}
              <Button
                variant="primary"
                to="/onboarding/mounting/choose"
                disabled={
                  assignedTrackers.length === 0 && flatTrackers.length > 0
                }
              >
                {l10n.getString('onboarding-continue')}
              </Button>
            </div>
          }
        />

        <div className="flex-1 flex flex-col gap-4 px-4 pt-4 min-h-0 overflow-hidden">
          <div className="flex items-center justify-between gap-2 shrink-0">
            <TrackerAssignmentTabs />
            <SideLegend
              mirror={config?.mirrorView ?? false}
              toggleMirror={() =>
                setConfig({
                  mirrorView: !config?.mirrorView,
                })
              }
            />
            {state.alonePage && (
              <Button
                variant="secondary"
                onClick={unassignAll}
                id="onboarding-assign_trackers-unassign_all"
              />
            )}
          </div>

          <div className="flex-1 min-h-0 flex flex-col fill-background-50 items-center justify-center">
            <div className="w-full h-full min-h-0 max-w-[770px] flex flex-col overflow-y-clip tall:py-10">
              <BodyAssignment
                dotSize={15}
                onlyAssigned={false}
                highlightedRoles={firstError?.affectedRoles || []}
                rolesWithErrors={rolesWithErrors}
                assignMode={currentAssignMode}
                mirror={config?.mirrorView ?? false}
                onRoleSelected={onDotSelected}
                renderCard={renderCard}
                slotStyle={slotStyle}
              />
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
