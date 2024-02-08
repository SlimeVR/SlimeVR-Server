import { useMemo } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/hooks/app';
import { useTrackers } from '@/hooks/tracker';
import { BodyInteractions } from '@/components/commons/BodyInteractions';
import { TrackerPartCard } from '@/components/tracker/TrackerPartCard';
import { BodyPartError } from './pages/trackers-assign/TrackerAssignment';
import { SIDES } from '@/components/commons/PersonFrontIcon';

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

export function BodyAssignment({
  advanced,
  mirror,
  onRoleSelected,
  rolesWithErrors = {},
  highlightedRoles = [],
  onlyAssigned = false,
  width,
}: {
  advanced: boolean;
  mirror: boolean;
  onlyAssigned?: boolean;
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  highlightedRoles?: BodyPart[];
  onRoleSelected: (role: BodyPart) => void;
  width?: number;
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
  // const right = +mirror;

  return (
    <>
      <BodyInteractions
        width={width}
        mirror={mirror}
        assignedRoles={assignedRoles}
        highlightedRoles={highlightedRoles}
        onSelectRole={onRoleSelected}
        leftControls={
          <div className="flex flex-col justify-between h-full text-right">
            <div className="flex flex-col gap-2">
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.HEAD]?.label}
                  td={trackerPartGrouped[BodyPart.HEAD]}
                  role={BodyPart.HEAD}
                  onClick={() => onRoleSelected(BodyPart.HEAD)}
                  direction="right"
                />
              )}
              {advanced && (
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
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[SIDES[left].shoulder]?.label}
                  td={trackerPartGrouped[SIDES[left].shoulder]}
                  role={SIDES[left].shoulder}
                  onClick={() => onRoleSelected(SIDES[left].shoulder)}
                  direction="right"
                />
              )}
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[SIDES[left].upperArm]?.label}
                td={trackerPartGrouped[SIDES[left].upperArm]}
                role={SIDES[left].upperArm}
                onClick={() => onRoleSelected(SIDES[left].upperArm)}
                direction="right"
              />
            </div>
            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[SIDES[left].lowerArm]?.label}
                td={trackerPartGrouped[SIDES[left].lowerArm]}
                role={SIDES[left].lowerArm}
                onClick={() => onRoleSelected(SIDES[left].lowerArm)}
                direction="right"
              />

              {advanced && (
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
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[SIDES[left].upperLeg]?.label}
                td={trackerPartGrouped[SIDES[left].upperLeg]}
                role={SIDES[left].upperLeg}
                onClick={() => onRoleSelected(SIDES[left].upperLeg)}
                direction="right"
              />

              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[SIDES[left].lowerLeg]?.label}
                td={trackerPartGrouped[SIDES[left].lowerLeg]}
                role={SIDES[left].lowerLeg}
                onClick={() => onRoleSelected(SIDES[left].lowerLeg)}
                direction="right"
              />
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[SIDES[left].foot]?.label}
                td={trackerPartGrouped[SIDES[left].foot]}
                role={SIDES[left].foot}
                onClick={() => onRoleSelected(SIDES[left].foot)}
                direction="right"
              />
            </div>
          </div>
        }
        rightControls={
          <div className="flex flex-col justify-between h-full">
            {advanced && (
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.UPPER_CHEST]?.label}
                td={trackerPartGrouped[BodyPart.UPPER_CHEST]}
                role={BodyPart.UPPER_CHEST}
                onClick={() => onRoleSelected(BodyPart.UPPER_CHEST)}
                direction="left"
              />
            )}

            <TrackerPartCard
              onlyAssigned={onlyAssigned}
              roleError={rolesWithErrors[BodyPart.CHEST]?.label}
              td={trackerPartGrouped[BodyPart.CHEST]}
              role={BodyPart.CHEST}
              onClick={() => onRoleSelected(BodyPart.CHEST)}
              direction="left"
            />

            <div className="flex flex-col gap-2">
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.RIGHT_SHOULDER]?.label}
                  td={trackerPartGrouped[BodyPart.RIGHT_SHOULDER]}
                  role={BodyPart.RIGHT_SHOULDER}
                  onClick={() => onRoleSelected(BodyPart.RIGHT_SHOULDER)}
                  direction="left"
                />
              )}

              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.RIGHT_UPPER_ARM]?.label}
                td={trackerPartGrouped[BodyPart.RIGHT_UPPER_ARM]}
                role={BodyPart.RIGHT_UPPER_ARM}
                onClick={() => onRoleSelected(BodyPart.RIGHT_UPPER_ARM)}
                direction="left"
              />
            </div>

            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.WAIST]?.label}
                td={trackerPartGrouped[BodyPart.WAIST]}
                onClick={() => onRoleSelected(BodyPart.WAIST)}
                role={BodyPart.WAIST}
                direction="left"
              />
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.HIP]?.label}
                td={trackerPartGrouped[BodyPart.HIP]}
                onClick={() => onRoleSelected(BodyPart.HIP)}
                role={BodyPart.HIP}
                direction="left"
              />
            </div>

            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.RIGHT_LOWER_ARM]?.label}
                td={trackerPartGrouped[BodyPart.RIGHT_LOWER_ARM]}
                role={BodyPart.RIGHT_LOWER_ARM}
                onClick={() => onRoleSelected(BodyPart.RIGHT_LOWER_ARM)}
                direction="left"
              />
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  roleError={rolesWithErrors[BodyPart.RIGHT_HAND]?.label}
                  td={trackerPartGrouped[BodyPart.RIGHT_HAND]}
                  onClick={() => onRoleSelected(BodyPart.RIGHT_HAND)}
                  role={BodyPart.RIGHT_HAND}
                  direction="left"
                />
              )}
            </div>

            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.RIGHT_UPPER_LEG]?.label}
                td={trackerPartGrouped[BodyPart.RIGHT_UPPER_LEG]}
                role={BodyPart.RIGHT_UPPER_LEG}
                onClick={() => onRoleSelected(BodyPart.RIGHT_UPPER_LEG)}
                direction="left"
              />

              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.RIGHT_LOWER_LEG]?.label}
                td={trackerPartGrouped[BodyPart.RIGHT_LOWER_LEG]}
                role={BodyPart.RIGHT_LOWER_LEG}
                onClick={() => onRoleSelected(BodyPart.RIGHT_LOWER_LEG)}
                direction="left"
              />
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                roleError={rolesWithErrors[BodyPart.RIGHT_FOOT]?.label}
                td={trackerPartGrouped[BodyPart.RIGHT_FOOT]}
                role={BodyPart.RIGHT_FOOT}
                onClick={() => onRoleSelected(BodyPart.RIGHT_FOOT)}
                direction="left"
              />
            </div>
          </div>
        }
      ></BodyInteractions>
    </>
  );
}
