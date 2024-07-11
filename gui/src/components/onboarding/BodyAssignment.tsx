import { useCallback, useMemo } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/hooks/app';
import { AssignMode } from '@/hooks/config';
import { useTrackers } from '@/hooks/tracker';
import { BodyInteractions } from '@/components/commons/BodyInteractions';
import { TrackerPartCard } from '@/components/tracker/TrackerPartCard';
import { BodyPartError } from './pages/trackers-assign/TrackerAssignment';
import { SIDES } from '@/components/commons/PersonFrontIcon';

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

export function BodyAssignment({
  assignMode,
  mirror,
  onRoleSelected,
  rolesWithErrors = {},
  highlightedRoles = [],
  onlyAssigned = false,
  width,
  dotSize,
}: {
  assignMode: AssignMode;
  mirror: boolean;
  onlyAssigned?: boolean;
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  highlightedRoles?: BodyPart[];
  onRoleSelected: (role: BodyPart) => void;
  width?: number;
  dotSize?: number;
}) {
  const { useAssignedTrackers } = useTrackers();

  const assignedTrackers = useAssignedTrackers();

  const trackerPartGrouped = useMemo(
    () =>
      assignedTrackers.reduce<{ [key: number]: FlatDeviceTracker[] }>(
        (curr, td) => {
          if (!td && onlyAssigned) return curr;

          const key = td.tracker.info?.bodyPart || BodyPart.NONE;
          return {
            ...curr,
            [key]: [...(curr[key] || []), td],
          };
        },
        {}
      ),
    [assignedTrackers]
  );

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
      assignMode === AssignMode.All ||
      ASSIGNMENT_MODES[assignMode].indexOf(part) > -1,
    [assignMode]
  );

  return (
    <>
      <BodyInteractions
        width={width}
        mirror={mirror}
        dotsSize={dotSize}
        assignedRoles={assignedRoles}
        highlightedRoles={highlightedRoles}
        onSelectRole={onRoleSelected}
        leftControls={
          <div className="flex flex-col justify-between h-full text-right">
            <div className="flex flex-col gap-2">
              {hasBodyPart(BodyPart.HEAD) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.HEAD]?.label}
                  td={trackerPartGrouped[BodyPart.HEAD]}
                  role={BodyPart.HEAD}
                  onClick={() => onRoleSelected(BodyPart.HEAD)}
                  direction="right"
                />
              )}

              {hasBodyPart(BodyPart.NECK) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.NECK]?.label}
                  td={trackerPartGrouped[BodyPart.NECK]}
                  role={BodyPart.NECK}
                  onClick={() => onRoleSelected(BodyPart.NECK)}
                  direction="right"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              {hasBodyPart(SIDES[left].shoulder) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].shoulder]?.label}
                  td={trackerPartGrouped[SIDES[left].shoulder]}
                  role={SIDES[left].shoulder}
                  onClick={() => onRoleSelected(SIDES[left].shoulder)}
                  direction="right"
                />
              )}

              {hasBodyPart(SIDES[left].upperArm) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].upperArm]?.label}
                  td={trackerPartGrouped[SIDES[left].upperArm]}
                  role={SIDES[left].upperArm}
                  onClick={() => onRoleSelected(SIDES[left].upperArm)}
                  direction="right"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              {hasBodyPart(SIDES[left].lowerArm) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].lowerArm]?.label}
                  td={trackerPartGrouped[SIDES[left].lowerArm]}
                  role={SIDES[left].lowerArm}
                  onClick={() => onRoleSelected(SIDES[left].lowerArm)}
                  direction="right"
                />
              )}

              {hasBodyPart(SIDES[left].hand) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].hand]?.label}
                  td={trackerPartGrouped[SIDES[left].hand]}
                  role={SIDES[left].hand}
                  onClick={() => onRoleSelected(SIDES[left].hand)}
                  direction="right"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              {hasBodyPart(SIDES[left].upperLeg) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].upperLeg]?.label}
                  td={trackerPartGrouped[SIDES[left].upperLeg]}
                  role={SIDES[left].upperLeg}
                  onClick={() => onRoleSelected(SIDES[left].upperLeg)}
                  direction="right"
                />
              )}

              {hasBodyPart(SIDES[left].lowerLeg) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].lowerLeg]?.label}
                  td={trackerPartGrouped[SIDES[left].lowerLeg]}
                  role={SIDES[left].lowerLeg}
                  onClick={() => onRoleSelected(SIDES[left].lowerLeg)}
                  direction="right"
                />
              )}

              {hasBodyPart(SIDES[left].foot) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].foot]?.label}
                  td={trackerPartGrouped[SIDES[left].foot]}
                  role={SIDES[left].foot}
                  onClick={() => onRoleSelected(SIDES[left].foot)}
                  direction="right"
                />
              )}
            </div>
          </div>
        }
        rightControls={
          <div className="flex flex-col justify-between h-full">
            {hasBodyPart(BodyPart.UPPER_CHEST) && (
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.UPPER_CHEST]?.label}
                td={trackerPartGrouped[BodyPart.UPPER_CHEST]}
                role={BodyPart.UPPER_CHEST}
                onClick={() => onRoleSelected(BodyPart.UPPER_CHEST)}
                direction="left"
              />
            )}

            {hasBodyPart(BodyPart.CHEST) && (
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.CHEST]?.label}
                td={trackerPartGrouped[BodyPart.CHEST]}
                role={BodyPart.CHEST}
                onClick={() => onRoleSelected(BodyPart.CHEST)}
                direction="left"
              />
            )}

            <div className="flex flex-col gap-2">
              {hasBodyPart(SIDES[right].shoulder) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].shoulder]?.label}
                  td={trackerPartGrouped[SIDES[right].shoulder]}
                  role={SIDES[right].shoulder}
                  onClick={() => onRoleSelected(SIDES[right].shoulder)}
                  direction="left"
                />
              )}

              {hasBodyPart(SIDES[right].upperArm) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].upperArm]?.label}
                  td={trackerPartGrouped[SIDES[right].upperArm]}
                  role={SIDES[right].upperArm}
                  onClick={() => onRoleSelected(SIDES[right].upperArm)}
                  direction="left"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              {hasBodyPart(BodyPart.WAIST) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.WAIST]?.label}
                  td={trackerPartGrouped[BodyPart.WAIST]}
                  onClick={() => onRoleSelected(BodyPart.WAIST)}
                  role={BodyPart.WAIST}
                  direction="left"
                />
              )}

              {hasBodyPart(BodyPart.HIP) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.HIP]?.label}
                  td={trackerPartGrouped[BodyPart.HIP]}
                  onClick={() => onRoleSelected(BodyPart.HIP)}
                  role={BodyPart.HIP}
                  direction="left"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              {hasBodyPart(SIDES[right].lowerArm) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].lowerArm]?.label}
                  td={trackerPartGrouped[SIDES[right].lowerArm]}
                  role={SIDES[right].lowerArm}
                  onClick={() => onRoleSelected(SIDES[right].lowerArm)}
                  direction="left"
                />
              )}

              {hasBodyPart(SIDES[right].hand) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].hand]?.label}
                  td={trackerPartGrouped[SIDES[right].hand]}
                  onClick={() => onRoleSelected(SIDES[right].hand)}
                  role={SIDES[right].hand}
                  direction="left"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              {hasBodyPart(SIDES[right].upperLeg) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].upperLeg]?.label}
                  td={trackerPartGrouped[SIDES[right].upperLeg]}
                  role={SIDES[right].upperLeg}
                  onClick={() => onRoleSelected(SIDES[right].upperLeg)}
                  direction="left"
                />
              )}

              {hasBodyPart(SIDES[right].lowerLeg) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].lowerLeg]?.label}
                  td={trackerPartGrouped[SIDES[right].lowerLeg]}
                  role={SIDES[right].lowerLeg}
                  onClick={() => onRoleSelected(SIDES[right].lowerLeg)}
                  direction="left"
                />
              )}

              {hasBodyPart(SIDES[right].foot) && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[right].foot]?.label}
                  td={trackerPartGrouped[SIDES[right].foot]}
                  role={SIDES[right].foot}
                  onClick={() => onRoleSelected(SIDES[right].foot)}
                  direction="left"
                />
              )}
            </div>
          </div>
        }
      ></BodyInteractions>
    </>
  );
}
