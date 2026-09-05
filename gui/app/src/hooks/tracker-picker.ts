import { useLocalization } from '@fluent/react';
import { createContext, useContext, useMemo, useState } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { useAtomValue } from 'jotai';
import { useConfig } from './config';
import {
  assignedRolesAtom,
  assignedTrackersAtom,
  connectedIMUTrackersAtom,
  flatTrackersAtom,
  trackerByBodyPartAtom,
} from '@/store/app-store';
import { ExtremityDescriptor, ExtremitySide } from '@/utils/extremities';
import { HAND_EXTREMITY } from '@/components/onboarding/extremities/hand';
import { FOOT_EXTREMITY } from '@/components/onboarding/extremities/foot';

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

export const ALL_ASSIGNABLE_PARTS = [
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
];

export const TAP_DETECTION_BODY_PARTS = [
  BodyPart.CHEST,
  BodyPart.HIP,
  BodyPart.LEFT_UPPER_ARM,
  BodyPart.RIGHT_UPPER_ARM,
  ...LEGS_PARTS,
  BodyPart.LEFT_FOOT,
  BodyPart.RIGHT_FOOT,
];

const addParts = (parts: Set<BodyPart>, roles: BodyPart[]) => {
  roles.forEach((role) => parts.add(role));
};

export const getSuggestedBodyParts = (
  connectedIMUTrackersCount: number
): BodyPart[] => {
  const parts = new Set<BodyPart>();

  addParts(parts, [BodyPart.CHEST, ...LEGS_PARTS]);
  if (connectedIMUTrackersCount >= 6) parts.add(BodyPart.HIP);
  if (connectedIMUTrackersCount === 7) parts.add(BodyPart.WAIST);
  if (connectedIMUTrackersCount >= 8) {
    addParts(parts, [BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT]);
  }
  if (connectedIMUTrackersCount >= 9) parts.add(BodyPart.WAIST);
  if (connectedIMUTrackersCount >= 10) {
    addParts(parts, [BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM]);
  }
  if (connectedIMUTrackersCount >= 12) {
    addParts(parts, [BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER]);
  }
  if (connectedIMUTrackersCount >= 14) parts.add(BodyPart.UPPER_CHEST);
  if (connectedIMUTrackersCount >= 15) parts.add(BodyPart.NECK);

  return [...parts];
};

/** Which body parts to offer: what the user asked for, or a guess from their trackers */
export function useSuggestedBodyParts(): BodyPart[] {
  const { config } = useConfig();
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);

  return config?.assignShowAllBodyParts
    ? ALL_ASSIGNABLE_PARTS
    : getSuggestedBodyParts(connectedIMUTrackers.length);
}

export type PickerTab = 'body' | 'fingers' | 'toes';

export type PickerTabSpec = {
  labelId: string;
  enabled: boolean;
  dotSize: { drag: number; tap: number };
  view: { kind: 'body' } | { kind: 'extremity'; descriptor: ExtremityDescriptor };
};

export const PICKER_TABS: Record<PickerTab, PickerTabSpec> = {
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
    enabled: true,
    dotSize: { drag: 20, tap: 18 },
    view: { kind: 'extremity', descriptor: FOOT_EXTREMITY },
  },
};

export const PICKER_TAB_ORDER: PickerTab[] = ['body', 'fingers', 'toes'];

export function usePickerShell() {
  const { l10n } = useLocalization();

  const [tab, setTab] = useState<PickerTab>('body');
  const [side, setSide] = useState<ExtremitySide>('right');

  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const flatTrackers = useAtomValue(flatTrackersAtom);
  const assignedRoles = useAtomValue(assignedRolesAtom);

  const suggestedBodyParts = useSuggestedBodyParts();
  const expectedTrackersCount = flatTrackers.length;

  const assignedPartsCount = useMemo(
    () => suggestedBodyParts.filter((part) => assignedRoles.includes(part)).length,
    [suggestedBodyParts, assignedRoles]
  );

  const rolesWithErrors = useMemo(() => {
    const trackerRoles = assignedRoles;

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

    const sortedRoles = trackerRoles.toSorted((a, b) => a - b);

    return sortedRoles.reduce<Partial<Record<BodyPart, BodyPartError>>>(
      (errors, role) => {
        const error = message(role);
        if (error) errors[role] = error;
        return errors;
      },
      {}
    );
  }, [assignedRoles]);

  const firstError = Object.values(rolesWithErrors).find((r) => !!r);

  return {
    tab,
    setTab,
    side,
    setSide,
    assignedTrackers,
    trackerByPart,
    flatTrackers,
    suggestedBodyParts,
    expectedTrackersCount,
    assignedPartsCount,
    rolesWithErrors,
    firstError,
  };
}

export type PickerShell = ReturnType<typeof usePickerShell>;

export type Picker = PickerShell & {
  activePart: BodyPart;
  selectPart: (role: BodyPart) => void;
};

export const PickerContext = createContext<Picker>(undefined as never);

export function usePicker() {
  const context = useContext(PickerContext);
  if (!context) throw new Error('usePicker must be within a PickerContext Provider');
  return context;
}
