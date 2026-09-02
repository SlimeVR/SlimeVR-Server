import { createContext, useContext, useEffect, useState } from 'react';
import {
  AssignTrackerRequestT,
  BodyPart,
  QuatT,
  ResetTrackerAssignmentsT,
  RpcMessage,
  TapDetectionSetupModeRequestT,
  TapDetectionSetupNotificationT,
} from 'solarxr-protocol';
import { useChokerWarning } from './choker-warning';
import { useWebsocketAPI } from './websocket-api';
import { useConfig } from './config';
import { playTapSetupSound } from '@/sounds/sounds';
import { useAtomValue } from 'jotai';
import { donglesAtom } from '@/store/app-store';
import { hoveredBodyPartAtom } from './tracker-drag';
import { usePickerShell } from './tracker-picker';

export type AssignmentMode = 'drag' | 'tap';

export type Pending =
  | { kind: 'part'; part: BodyPart }
  | { kind: 'tracker'; id: number }
  | null;

export function useAssignTracker() {
  const { sendRPCPacket } = useWebsocketAPI();

  return (
    trackerId: number,
    bodyPart: BodyPart,
    mountingOrientation: QuatT | null = null
  ) => {
    const request = new AssignTrackerRequestT();
    request.trackerId = trackerId;
    request.bodyPosition = bodyPart;
    request.mountingOrientation = mountingOrientation;
    sendRPCPacket(RpcMessage.AssignTrackerRequest, request);
  };
}

export function useTrackerAssignment(mode: AssignmentMode) {
  const { config } = useConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const sendAssign = useAssignTracker();
  const shell = usePickerShell();
  const { trackerByPart, flatTrackers } = shell;

  const [pending, setPending] = useState<Pending>(null);
  const [panelOpen, setPanelOpen] = useState(false);

  const dongles = useAtomValue(donglesAtom);
  const dragTarget = useAtomValue(hoveredBodyPartAtom);

  const armedPart = pending?.kind === 'part' ? pending.part : BodyPart.NONE;
  const pendingTrackerId = pending?.kind === 'tracker' ? pending.id : null;
  const activePart = mode === 'drag' ? (dragTarget ?? BodyPart.NONE) : armedPart;

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

  const clearPending = () => {
    setPending(null);
    setPanelOpen(false);
  };

  const moveTrackerToBodyPart = (trackerId: number, bodyPart: BodyPart) => {
    const occupant = trackerByPart[bodyPart];
    if (
      bodyPart !== BodyPart.NONE &&
      occupant &&
      occupant.tracker.trackerId !== trackerId
    ) {
      sendAssign(
        occupant.tracker.trackerId,
        BodyPart.NONE,
        occupant.tracker.info?.mountingOrientation ?? null
      );
    }

    const moved = flatTrackers.find((td) => td.tracker.trackerId === trackerId);
    sendAssign(trackerId, bodyPart, moved?.tracker.info?.mountingOrientation ?? null);
  };

  const { tryOpenChokerWarning, closeChokerWarning, shouldShowChokerWarn } =
    useChokerWarning<{ bodyPart: BodyPart; trackerId?: number }>({
      getBodyPart: (v) => v.bodyPart,
      next: ({ bodyPart, trackerId }) => {
        if (trackerId != null) {
          moveTrackerToBodyPart(trackerId, bodyPart);
          clearPending();
        } else {
          setPending({ kind: 'part', part: bodyPart });
          setPanelOpen(true);
        }
      },
    });

  const armPart = (part: BodyPart) => {
    if (armedPart === part) {
      clearPending();
      return;
    }
    tryOpenChokerWarning({ bodyPart: part });
  };

  const handleDropTracker = (trackerId: number, bodyPart: BodyPart) => {
    tryOpenChokerWarning({ bodyPart, trackerId });
  };

  const unassignPart = (part: BodyPart) => {
    const td = trackerByPart[part];
    if (td) {
      sendAssign(
        td.tracker.trackerId,
        BodyPart.NONE,
        td.tracker.info?.mountingOrientation ?? null
      );
    }
    if (armedPart === part) clearPending();
  };

  const unassignAll = () => {
    sendRPCPacket(RpcMessage.ResetTrackerAssignments, new ResetTrackerAssignmentsT());
    clearPending();
  };

  const selectPart = (role: BodyPart) => {
    if (pendingTrackerId != null) {
      handleDropTracker(pendingTrackerId, role);
      return;
    }
    if (mode === 'drag' && trackerByPart[role]) {
      unassignPart(role);
      return;
    }
    armPart(role);
  };

  const selectTracker = (trackerId: number) => {
    if (armedPart !== BodyPart.NONE) {
      handleDropTracker(trackerId, armedPart);
      return;
    }
    if (pendingTrackerId === trackerId) {
      setPending(null);
      return;
    }
    setPending({ kind: 'tracker', id: trackerId });
    setPanelOpen(false);
  };

  useRPCPacket(
    RpcMessage.TapDetectionSetupNotification,
    (tapSetup: TapDetectionSetupNotificationT) => {
      if (armedPart === BodyPart.NONE || !tapSetup.trackerId) return;
      handleDropTracker(tapSetup.trackerId, armedPart);
      playTapSetupSound(config?.feedbackSoundVolume);
    }
  );

  return {
    ...shell,
    mode,
    dongles,
    pending,
    armedPart,
    pendingTrackerId,
    activePart,
    clearPending,
    panelOpen,
    togglePanel: () => setPanelOpen((open) => !open),
    selectPart,
    selectTracker,
    handleDropTracker,
    unassignPart,
    unassignAll,
    shouldShowChokerWarn,
    closeChokerWarning,
  };
}

export type TrackerAssignment = ReturnType<typeof useTrackerAssignment>;

export const AssignmentContext = createContext<TrackerAssignment>(undefined as never);

export function useAssignment() {
  const context = useContext(AssignmentContext);
  if (!context)
    throw new Error('useAssignment must be within an AssignmentContext Provider');
  return context;
}
