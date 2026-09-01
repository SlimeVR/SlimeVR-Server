import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useState } from 'react';
import {
  AssignTrackerRequestT,
  BodyPart,
  QuatT,
  RpcMessage,
  TapDetectionSetupModeRequestT,
  TapDetectionSetupNotificationT,
} from 'solarxr-protocol';
import { useChokerWarning } from './choker-warning';
import { useWebsocketAPI } from './websocket-api';
import {
  ASSIGN_MODE_OPTIONS,
  ASSIGNMENT_MODES,
  ASSIGNMENT_RULES,
  getPreferredAssignMode,
  LOWER_BODY,
} from '@/components/onboarding/BodyAssignment';
import { AssignMode, useConfig } from './config';
import { playTapSetupSound } from '@/sounds/sounds';
import { useAtomValue } from 'jotai';
import {
  assignedTrackersAtom,
  connectedIMUTrackersAtom,
  donglesAtom,
  flatTrackersAtom,
  trackerByBodyPartAtom,
} from '@/store/app-store';

export type BodyPartError = {
  label: string | undefined;
  affectedRoles: BodyPart[];
};

export function useTrackerAssignment() {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const [armedPart, setArmedPart] = useState<BodyPart>(BodyPart.NONE);

  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
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

  const currentAssignMode = config?.assignShowAllBodyParts
    ? AssignMode.All
    : getPreferredAssignMode(connectedIMUTrackers.length);

  const expectedTrackersCount = ASSIGN_MODE_OPTIONS[currentAssignMode];

  const assignedPartsCount = useMemo(
    () =>
      ASSIGNMENT_MODES[currentAssignMode].filter((part) => !!trackerByPart[part])
        .length,
    [currentAssignMode, trackerByPart]
  );

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
          [role]: trackerRoles.find((tr) => tr === role) ? message(role) : undefined,
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
    const td = trackerByPart[part];
    if (td) {
      sendAssign(
        td.tracker.trackerId,
        BodyPart.NONE,
        td.tracker.info?.mountingOrientation ?? null
      );
    }
    if (armedPart === part) setArmedPart(BodyPart.NONE);
  };

  const unassignAll = () => {
    assignedTrackers.forEach((td) =>
      sendAssign(td.tracker.trackerId, BodyPart.NONE, null)
    );
    setArmedPart(BodyPart.NONE);
  };

  const onDotSelected = (role: BodyPart) => {
    if (trackerByPart[role]) {
      unassignPart(role);
    } else {
      armForTap(role);
    }
  };

  useRPCPacket(
    RpcMessage.TapDetectionSetupNotification,
    (tapSetup: TapDetectionSetupNotificationT) => {
      if (armedPart === BodyPart.NONE || !tapSetup.trackerId) return;
      handleDropTracker(tapSetup.trackerId, armedPart);
      setArmedPart(BodyPart.NONE);
      playTapSetupSound(config?.feedbackSoundVolume);
    }
  );

  return {
    assignedTrackers,
    flatTrackers,
    dongles,
    connectedIMUTrackers,
    armedPart,
    setArmedPart,
    trackerByPart,
    expectedTrackersCount,
    assignedPartsCount,
    rolesWithErrors,
    firstError,
    armForTap,
    handleDropTracker,
    unassignPart,
    unassignAll,
    onDotSelected,
    shouldShowChokerWarn,
    closeChokerWarning,
  };
}

export type TrackerAssignment = ReturnType<typeof useTrackerAssignment>;
