import { ReactNode, useCallback, useMemo } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { AssignMode, useConfig } from '@/hooks/config';
import {
  BodyInteractions,
  BodySlotStyler,
} from '@/components/commons/BodyInteractions';
import { TrackerPartCard } from '@/components/tracker/TrackerPartCard';
import { BodyPartError } from '@/hooks/tracker-assignment';
import { SIDES } from '@/components/commons/PersonFrontIcon';
import { useAtomValue } from 'jotai';
import {
  assignedTrackersAtom,
  connectedIMUTrackersAtom,
  FlatDeviceTracker,
  trackerByBodyPartAtom,
} from '@/store/app-store';

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
export const ASSIGNMENT_RULES: Partial<
  Record<BodyPart, (BodyPart | BodyPart[])[]>
> = {
  [BodyPart.LEFT_FOOT]: [
    BodyPart.LEFT_LOWER_LEG,
    BodyPart.LEFT_UPPER_LEG,
    SPINE_PARTS,
  ],
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

export const getPreferredAssignMode = (
  connectedIMUTrackersCount: number
): AssignMode =>
  (Object.entries(ASSIGN_MODE_OPTIONS).find(
    ([, count]) => count >= connectedIMUTrackersCount
  )?.[0] as AssignMode) ?? AssignMode.All;

export type BodyPartCardRenderer = (args: {
  role: BodyPart;
  direction: 'left' | 'right';
  td: FlatDeviceTracker | undefined;
  roleError: string | undefined;
}) => ReactNode;

export function BodyAssignment({
  mirror,
  onRoleSelected,
  rolesWithErrors = {},
  highlightedRoles = [],
  onlyAssigned = false,
  dotSize,
  fillHeight,
  renderCard,
  slotStyle,
}: {
  mirror: boolean;
  onlyAssigned?: boolean;
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  highlightedRoles?: BodyPart[];
  onRoleSelected: (role: BodyPart) => void;
  width?: number;
  dotSize?: number;
  fillHeight?: boolean;
  renderCard?: BodyPartCardRenderer;
  slotStyle?: BodySlotStyler;
}) {
  const { config } = useConfig();
  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);

  const assignMode = config?.assignShowAllBodyParts
    ? AssignMode.All
    : getPreferredAssignMode(connectedIMUTrackers.length);

  const assignedRoles = useMemo(
    () =>
      assignedTrackers.map(
        ({ tracker }) => tracker.info?.bodyPart || BodyPart.NONE,
        {}
      ),
    [assignedTrackers]
  );

  const left = +!mirror;
  const right = +mirror;

  const hasBodyPart = useCallback(
    (part: BodyPart) =>
      COMMONS.includes(part) ||
      (assignMode && ASSIGNMENT_MODES[assignMode].includes(part)),
    [assignMode]
  );

  const card: BodyPartCardRenderer =
    renderCard ??
    (({ role, direction, td, roleError }) => (
      <TrackerPartCard
        onlyAssigned={onlyAssigned}
        roleError={roleError}
        td={td}
        role={role}
        onClick={() => onRoleSelected(role)}
        direction={direction}
      />
    ));

  return (
    <BodyInteractions
      mirror={mirror}
      dotsSize={dotSize}
      fillHeight={fillHeight}
      slotStyle={slotStyle}
      assignedRoles={assignedRoles}
      highlightedRoles={highlightedRoles}
      onSelectRole={onRoleSelected}
      leftControls={
        <div className="flex flex-col justify-between h-full text-right">
          <div className="flex flex-col gap-2">
            {hasBodyPart(BodyPart.HEAD) &&
              card({
                role: BodyPart.HEAD,
                direction: 'right',
                td: trackerByPart[BodyPart.HEAD],
                roleError: rolesWithErrors[BodyPart.HEAD]?.label,
              })}

            {hasBodyPart(BodyPart.NECK) &&
              card({
                role: BodyPart.NECK,
                direction: 'right',
                td: trackerByPart[BodyPart.NECK],
                roleError: rolesWithErrors[BodyPart.NECK]?.label,
              })}
          </div>
          <div className="flex flex-col gap-2">
            {hasBodyPart(SIDES[left].shoulder) &&
              card({
                role: SIDES[left].shoulder,
                direction: 'right',
                td: trackerByPart[SIDES[left].shoulder],
                roleError: rolesWithErrors[SIDES[left].shoulder]?.label,
              })}

            {hasBodyPart(SIDES[left].upperArm) &&
              card({
                role: SIDES[left].upperArm,
                direction: 'right',
                td: trackerByPart[SIDES[left].upperArm],
                roleError: rolesWithErrors[SIDES[left].upperArm]?.label,
              })}
          </div>
          <div className="flex flex-col gap-2">
            {hasBodyPart(SIDES[left].lowerArm) &&
              card({
                role: SIDES[left].lowerArm,
                direction: 'right',
                td: trackerByPart[SIDES[left].lowerArm],
                roleError: rolesWithErrors[SIDES[left].lowerArm]?.label,
              })}

            {hasBodyPart(SIDES[left].hand) &&
              card({
                role: SIDES[left].hand,
                direction: 'right',
                td: trackerByPart[SIDES[left].hand],
                roleError: rolesWithErrors[SIDES[left].hand]?.label,
              })}
          </div>
          <div className="flex flex-col gap-2">
            {hasBodyPart(BodyPart.HIP) &&
              card({
                role: BodyPart.HIP,
                direction: 'right',
                td: trackerByPart[BodyPart.HIP],
                roleError: rolesWithErrors[BodyPart.HIP]?.label,
              })}
          </div>
          <div className="flex flex-col gap-2">
            {hasBodyPart(SIDES[left].upperLeg) &&
              card({
                role: SIDES[left].upperLeg,
                direction: 'right',
                td: trackerByPart[SIDES[left].upperLeg],
                roleError: rolesWithErrors[SIDES[left].upperLeg]?.label,
              })}

            {hasBodyPart(SIDES[left].lowerLeg) &&
              card({
                role: SIDES[left].lowerLeg,
                direction: 'right',
                td: trackerByPart[SIDES[left].lowerLeg],
                roleError: rolesWithErrors[SIDES[left].lowerLeg]?.label,
              })}

            {hasBodyPart(SIDES[left].foot) &&
              card({
                role: SIDES[left].foot,
                direction: 'right',
                td: trackerByPart[SIDES[left].foot],
                roleError: rolesWithErrors[SIDES[left].foot]?.label,
              })}
          </div>
        </div>
      }
      rightControls={
        <div className="flex flex-col justify-between h-full">
          <div className="flex flex-col gap-2">
            {hasBodyPart(BodyPart.UPPER_CHEST) &&
              card({
                role: BodyPart.UPPER_CHEST,
                direction: 'left',
                td: trackerByPart[BodyPart.UPPER_CHEST],
                roleError: rolesWithErrors[BodyPart.UPPER_CHEST]?.label,
              })}

            {hasBodyPart(BodyPart.CHEST) &&
              card({
                role: BodyPart.CHEST,
                direction: 'left',
                td: trackerByPart[BodyPart.CHEST],
                roleError: rolesWithErrors[BodyPart.CHEST]?.label,
              })}
          </div>

          <div className="flex flex-col gap-2">
            {hasBodyPart(SIDES[right].shoulder) &&
              card({
                role: SIDES[right].shoulder,
                direction: 'left',
                td: trackerByPart[SIDES[right].shoulder],
                roleError: rolesWithErrors[SIDES[right].shoulder]?.label,
              })}

            {hasBodyPart(SIDES[right].upperArm) &&
              card({
                role: SIDES[right].upperArm,
                direction: 'left',
                td: trackerByPart[SIDES[right].upperArm],
                roleError: rolesWithErrors[SIDES[right].upperArm]?.label,
              })}
          </div>

          <div className="flex flex-col gap-2">
            {hasBodyPart(SIDES[right].lowerArm) &&
              card({
                role: SIDES[right].lowerArm,
                direction: 'left',
                td: trackerByPart[SIDES[right].lowerArm],
                roleError: rolesWithErrors[SIDES[right].lowerArm]?.label,
              })}

            {hasBodyPart(SIDES[right].hand) &&
              card({
                role: SIDES[right].hand,
                direction: 'left',
                td: trackerByPart[SIDES[right].hand],
                roleError: rolesWithErrors[SIDES[right].hand]?.label,
              })}
          </div>
          <div className="flex flex-col gap-2">
            {hasBodyPart(BodyPart.WAIST) &&
              card({
                role: BodyPart.WAIST,
                direction: 'left',
                td: trackerByPart[BodyPart.WAIST],
                roleError: rolesWithErrors[BodyPart.WAIST]?.label,
              })}
          </div>
          <div className="flex flex-col gap-2">
            {hasBodyPart(SIDES[right].upperLeg) &&
              card({
                role: SIDES[right].upperLeg,
                direction: 'left',
                td: trackerByPart[SIDES[right].upperLeg],
                roleError: rolesWithErrors[SIDES[right].upperLeg]?.label,
              })}

            {hasBodyPart(SIDES[right].lowerLeg) &&
              card({
                role: SIDES[right].lowerLeg,
                direction: 'left',
                td: trackerByPart[SIDES[right].lowerLeg],
                roleError: rolesWithErrors[SIDES[right].lowerLeg]?.label,
              })}

            {hasBodyPart(SIDES[right].foot) &&
              card({
                role: SIDES[right].foot,
                direction: 'left',
                td: trackerByPart[SIDES[right].foot],
                roleError: rolesWithErrors[SIDES[right].foot]?.label,
              })}
          </div>
        </div>
      }
    />
  );
}
