import { useLocalization } from '@fluent/react';
import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
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
import { hoveredBodyPartAtom } from './tracker-drag';
import {
  ExtremityDescriptor,
  ExtremitySide,
  HAND_EXTREMITY,
} from '@/utils/extremities';

export type BodyPartError = {
  label: string | undefined;
  affectedRoles: BodyPart[];
};

const HANDS_PARTS = new Set([BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND]);
export const ARMS_PARTS = new Set([
  BodyPart.LEFT_UPPER_ARM,
  BodyPart.RIGHT_UPPER_ARM,
  BodyPart.LEFT_LOWER_ARM,
  BodyPart.RIGHT_LOWER_ARM,
]);
export const LEGS_PARTS = new Set([
  BodyPart.LEFT_UPPER_LEG,
  BodyPart.RIGHT_UPPER_LEG,
  BodyPart.LEFT_LOWER_LEG,
  BodyPart.RIGHT_LOWER_LEG,
]);
export const LOWER_BODY = new Set([
  BodyPart.LEFT_FOOT,
  BodyPart.RIGHT_FOOT,
  ...LEGS_PARTS,
]);
export const SPINE_PARTS = [
  BodyPart.UPPER_CHEST,
  BodyPart.CHEST,
  BodyPart.WAIST,
  BodyPart.HIP,
];
export const ASSIGNMENT_RULES: Partial<Record<BodyPart, (BodyPart | BodyPart[])[]>> = {
  [BodyPart.LEFT_FOOT]: [BodyPart.LEFT_LOWER_LEG, BodyPart.LEFT_UPPER_LEG, SPINE_PARTS],
  [BodyPart.RIGHT_FOOT]: [
    BodyPart.RIGHT_LOWER_LEG,
    BodyPart.RIGHT_UPPER_LEG,
    SPINE_PARTS,
  ],
  [BodyPart.LEFT_LOWER_LEG]: [BodyPart.LEFT_UPPER_LEG, SPINE_PARTS],
  [BodyPart.RIGHT_LOWER_LEG]: [BodyPart.RIGHT_UPPER_LEG, SPINE_PARTS],
  [BodyPart.LEFT_UPPER_LEG]: [SPINE_PARTS],
  [BodyPart.RIGHT_UPPER_LEG]: [SPINE_PARTS],
  [BodyPart.HIP]: [BodyPart.CHEST],
  [BodyPart.WAIST]: [BodyPart.CHEST],
  // TODO chest OR upperChest.
  //  Also don't warn if no legs.
};

export const COMMONS = [BodyPart.HEAD, ...HANDS_PARTS];

export const ASSIGNMENT_MODES: Record<AssignMode, BodyPart[]> = {
  //  x5
  [AssignMode.LowerBody]: [BodyPart.CHEST, ...LEGS_PARTS],
  //  x6 (5 + 1)
  [AssignMode.Core]: [BodyPart.CHEST, BodyPart.HIP, ...LEGS_PARTS],
  //  x8 (5 + 3)
  [AssignMode.EnhancedCore]: [
    BodyPart.CHEST,
    BodyPart.HIP,
    ...LEGS_PARTS,
    BodyPart.LEFT_FOOT,
    BodyPart.RIGHT_FOOT,
  ],
  // x10 (7 + 3)
  [AssignMode.FullBody]: [
    BodyPart.CHEST,
    BodyPart.HIP,
    BodyPart.LEFT_UPPER_ARM,
    BodyPart.RIGHT_UPPER_ARM,
    ...LEGS_PARTS,
    BodyPart.LEFT_FOOT,
    BodyPart.RIGHT_FOOT,
  ],
  // special case with all body parts
  [AssignMode.All]: [
    BodyPart.HEAD,
    BodyPart.NECK,
    BodyPart.LEFT_SHOULDER,
    BodyPart.RIGHT_SHOULDER,
    BodyPart.LEFT_HAND,
    BodyPart.RIGHT_HAND,
    BodyPart.LEFT_FOOT,
    BodyPart.RIGHT_FOOT,
    ...SPINE_PARTS,
    ...ARMS_PARTS,
    ...LEGS_PARTS,
  ],
};

export const ASSIGN_MODE_OPTIONS: Record<AssignMode, number> = [
  AssignMode.LowerBody,
  AssignMode.Core,
  AssignMode.EnhancedCore,
  AssignMode.FullBody,
  AssignMode.All,
].reduce(
  (opts, mode) => ({ ...opts, [mode]: ASSIGNMENT_MODES[mode].length }),
  {} as Record<AssignMode, number>
);

export const getPreferredAssignMode = (connectedIMUTrackersCount: number): AssignMode =>
  (Object.entries(ASSIGN_MODE_OPTIONS).find(
    ([, count]) => count >= connectedIMUTrackersCount
  )?.[0] as AssignMode) ?? AssignMode.All;

/** Which set of body parts to offer: what the user asked for, or a guess from their trackers */
export function useAssignMode(): AssignMode {
  const { config } = useConfig();
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);

  return config?.assignShowAllBodyParts
    ? AssignMode.All
    : getPreferredAssignMode(connectedIMUTrackers.length);
}

export type AssignmentTab = 'body' | 'fingers' | 'toes';

export type AssignmentTabSpec = {
  labelId: string;
  enabled: boolean;
  dotSize: { drag: number; tap: number };
  view: { kind: 'body' } | { kind: 'extremity'; descriptor: ExtremityDescriptor };
};

export const ASSIGNMENT_TABS: Record<AssignmentTab, AssignmentTabSpec> = {
  body: {
    labelId: 'onboarding-assign_trackers-tab-body',
    enabled: true,
    dotSize: { drag: 15, tap: 12 },
    view: { kind: 'body' },
  },
  fingers: {
    labelId: 'onboarding-assign_trackers-tab-fingers',
    enabled: true,
    dotSize: { drag: 22, tap: 20 },
    view: { kind: 'extremity', descriptor: HAND_EXTREMITY },
  },
  toes: {
    labelId: 'onboarding-assign_trackers-tab-toes',
    enabled: false,
    dotSize: { drag: 22, tap: 20 },
    view: { kind: 'body' },
  },
};

export const ASSIGNMENT_TAB_ORDER: AssignmentTab[] = ['body', 'fingers', 'toes'];

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
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const sendAssign = useAssignTracker();

  const [pending, setPending] = useState<Pending>(null);
  const [tab, setTab] = useState<AssignmentTab>('body');
  const [side, setSide] = useState<ExtremitySide>('right');
  const [panelOpen, setPanelOpen] = useState(false);

  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const flatTrackers = useAtomValue(flatTrackersAtom);
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

  const currentAssignMode = useAssignMode();

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

    const message = (assignedRole: BodyPart): BodyPartError | undefined => {
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

    const assignedRoles = trackerRoles.toSorted((a, b) => a - b);

    return assignedRoles.reduce<Partial<Record<BodyPart, BodyPartError>>>(
      (errors, role) => {
        const error = message(role);
        if (error) errors[role] = error;
        return errors;
      },
      {}
    );
  }, [flatTrackers]);

  const firstError = Object.values(rolesWithErrors).find((r) => !!r);

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
    assignedTrackers.forEach((td) =>
      sendAssign(td.tracker.trackerId, BodyPart.NONE, null)
    );
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
    mode,
    assignedTrackers,
    flatTrackers,
    dongles,
    trackerByPart,
    pending,
    armedPart,
    pendingTrackerId,
    activePart,
    clearPending,
    tab,
    setTab,
    side,
    setSide,
    panelOpen,
    togglePanel: () => setPanelOpen((open) => !open),
    expectedTrackersCount,
    assignedPartsCount,
    rolesWithErrors,
    firstError,
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
