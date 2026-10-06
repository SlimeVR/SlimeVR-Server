import { useLocalization } from '@fluent/react';
import { createContext, useContext, useMemo, useState } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { useAtomValue } from 'jotai';
import { useConfig } from './config';
import {
  BodyPartPrerequisites,
  unmetRequirements,
  useBodyPartPrerequisites,
} from './body-part-prerequisites';
import {
  assignedRolesAtom,
  assignedTrackersAtom,
  flatTrackersAtom,
  trackerByBodyPartAtom,
} from '@/store/app-store';
import { useLocaleConfig } from '@/i18n/config';
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
export const SPINE_PARTS = [
  BodyPart.UPPER_CHEST,
  BodyPart.LOWER_CHEST,
  BodyPart.UPPER_WAIST,
  BodyPart.LOWER_WAIST,
  BodyPart.HIP,
];
export const COMMONS = [BodyPart.HEAD, ...HANDS_PARTS];

export const ALL_ASSIGNABLE_PARTS = [
  BodyPart.HEAD,
  BodyPart.NECK,
  BodyPart.LEFT_SHOULDER,
  BodyPart.RIGHT_SHOULDER,
  
  BodyPart.LEFT_BUST,
  BodyPart.RIGHT_BUST,

  BodyPart.LEFT_HAND,
  BodyPart.RIGHT_HAND,
  BodyPart.LEFT_FOOT,
  BodyPart.RIGHT_FOOT,
  BodyPart.LEFT_POSTERIOR,
  BodyPart.RIGHT_POSTERIOR,
  BodyPart.TAIL,
  ...SPINE_PARTS,
  ...ARMS_PARTS,
  ...LEGS_PARTS,
];

export const TAP_DETECTION_BODY_PARTS = [
  BodyPart.UPPER_CHEST,
  BodyPart.LOWER_CHEST,
  BodyPart.HIP,
  BodyPart.LEFT_UPPER_ARM,
  BodyPart.RIGHT_UPPER_ARM,
  ...LEGS_PARTS,
  BodyPart.LEFT_FOOT,
  BodyPart.RIGHT_FOOT,
];

const ASSIGNABLE_PARTS = new Set(ALL_ASSIGNABLE_PARTS);

type SetupRegionName =
  | 'legs'
  | 'chest'
  | 'hip'
  | 'feet'
  | 'upperArms'
  | 'lowerArms'
  | 'shoulders'
  | 'lowerWaist'
  | 'lowerChest'
  | 'upperWaist'
  | 'neck';

/**
 * Setup order. A region is offered once everything in its `needs` is assigned, and is offered
 * whole, so one side of a pair brings the other.
 */
const SETUP_REGIONS: {
  id: SetupRegionName;
  parts: BodyPart[];
  needs: SetupRegionName[];
}[] = [
  { id: 'legs', parts: [...LEGS_PARTS], needs: [] },
  { id: 'chest', parts: [BodyPart.UPPER_CHEST], needs: [] },
  { id: 'hip', parts: [BodyPart.HIP], needs: ['chest'] },
  // one spine point at a time, each halving the largest remaining gap
  { id: 'lowerWaist', parts: [BodyPart.LOWER_WAIST], needs: ['hip'] },
  { id: 'lowerChest', parts: [BodyPart.LOWER_CHEST], needs: ['lowerWaist'] },
  { id: 'upperWaist', parts: [BodyPart.UPPER_WAIST], needs: ['lowerChest'] },
  {
    id: 'feet',
    parts: [BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT],
    needs: ['legs'],
  },
  {
    id: 'upperArms',
    parts: [BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM],
    needs: ['legs', 'chest'],
  },
  {
    id: 'lowerArms',
    parts: [BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM],
    needs: ['upperArms'],
  },
  {
    id: 'shoulders',
    parts: [BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER],
    needs: ['upperArms'],
  },
  { id: 'neck', parts: [BodyPart.NECK], needs: ['upperArms'] },
];

const CANONICAL_PARTS = SETUP_REGIONS.flatMap((region) => region.parts);

/** Which alternative in a requirement group to point the user at */
export function suggestedPart(group: BodyPart[]): BodyPart | undefined {
  return CANONICAL_PARTS.find((candidate) => group.includes(candidate));
}

/** How many unfinished regions to offer at once */
const OFFERED_OPEN_REGIONS = 3;

/**
 * Which points to offer. Any of these show a point:
 *
 * - the always-available parts
 * - anything already assigned
 * - one suggestion per missing requirement
 * - regions whose `needs` are all assigned, up to the cap
 *
 * The cap runs after the `needs` check, never before. Before it, an unfinished region gets
 * skipped over instead of blocking the regions that build on it.
 */
export function getOfferedBodyParts(
  assignedRoles: BodyPart[],
  prerequisites: BodyPartPrerequisites
): BodyPart[] {
  const assigned = new Set(assignedRoles);
  const offered = new Set<BodyPart>(COMMONS);

  // an assigned part always keeps its point
  assigned.forEach((part) => {
    if (ASSIGNABLE_PARTS.has(part)) offered.add(part);
  });

  // what an assignment still waits on, naming the same part the warning does
  assigned.forEach((part) => {
    unmetRequirements(prerequisites[part] ?? [], assigned).forEach((group) => {
      const suggestion = suggestedPart(group);
      if (suggestion != null) offered.add(suggestion);
    });
  });

  const finished = (id: SetupRegionName) =>
    SETUP_REGIONS.find((region) => region.id === id)?.parts.every((part) =>
      assigned.has(part)
    ) ?? false;

  let open = 0;
  SETUP_REGIONS.forEach((region) => {
    if (!region.needs.every(finished)) return;

    if (!finished(region.id)) {
      if (open >= OFFERED_OPEN_REGIONS) return;
      open += 1;
    }
    region.parts.forEach((part) => offered.add(part));
  });

  return [...offered];
}

export function useSuggestedBodyParts(): BodyPart[] {
  const { config } = useConfig();
  const assignedRoles = useAtomValue(assignedRolesAtom);
  const prerequisites = useBodyPartPrerequisites();

  const showAll = config?.assignShowAllBodyParts ?? false;

  return useMemo(
    () =>
      showAll
        ? ALL_ASSIGNABLE_PARTS
        : getOfferedBodyParts(assignedRoles, prerequisites),
    [showAll, assignedRoles, prerequisites]
  );
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

export function getPickerSelection(bodyPart?: BodyPart): {
  tab: PickerTab;
  side: ExtremitySide;
} {
  if (bodyPart == null) return { tab: 'body', side: 'right' };

  for (const tab of PICKER_TAB_ORDER) {
    const view = PICKER_TABS[tab].view;
    if (view.kind !== 'extremity') continue;

    for (const side of ['left', 'right'] as ExtremitySide[]) {
      const { digits } = view.descriptor.sides[side];
      if (Object.values(digits).some((parts) => parts.includes(bodyPart))) {
        return { tab, side };
      }
    }
  }

  return { tab: 'body', side: 'right' };
}

export function providePicker() {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();

  const [tab, setTab] = useState<PickerTab>('body');
  const [side, setSide] = useState<ExtremitySide>('right');

  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const flatTrackers = useAtomValue(flatTrackersAtom);
  const assignedRoles = useAtomValue(assignedRolesAtom);

  const suggestedBodyParts = useSuggestedBodyParts();
  const prerequisites = useBodyPartPrerequisites();
  const expectedTrackersCount = flatTrackers.length;

  const assignedPartsCount = assignedRoles.length;

  const rolesWithErrors = useMemo(() => {
    const assigned = new Set(assignedRoles);

    const all = new Intl.ListFormat(currentLocales, { type: 'conjunction' });

    const message = (assignedRole: BodyPart): BodyPartError | undefined => {
      const unmet = unmetRequirements(prerequisites[assignedRole] ?? [], assigned);
      if (unmet.length === 0) return;

      const missing = unmet
        .map(suggestedPart)
        .filter((part): part is BodyPart => part != null);

      return {
        affectedRoles: missing,
        label: l10n.getString('onboarding-assign_trackers-warning', {
          part: l10n.getString(`body_part-${BodyPart[assignedRole]}`),
          missing: all.format(
            missing.map((part) => l10n.getString(`body_part-${BodyPart[part]}`))
          ),
        }),
      };
    };

    return assignedRoles
      .toSorted((a, b) => a - b)
      .reduce<Partial<Record<BodyPart, BodyPartError>>>((errors, role) => {
        const error = message(role);
        if (error) errors[role] = error;
        return errors;
      }, {});
  }, [assignedRoles, prerequisites, l10n, currentLocales]);

  const firstError = Object.values(rolesWithErrors).find((r) => !!r);

  const requiredRoles = useMemo(
    () => [
      ...new Set(
        Object.values(rolesWithErrors).flatMap((error) => error?.affectedRoles ?? [])
      ),
    ],
    [rolesWithErrors]
  );

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
    requiredRoles,
  };
}

export type PickerShell = ReturnType<typeof providePicker>;

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
