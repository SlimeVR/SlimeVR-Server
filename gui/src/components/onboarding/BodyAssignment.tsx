import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { BodyPart } from 'solarxr-protocol';
import { FlatDeviceTracker } from '../../hooks/app';
import { useTrackers } from '../../hooks/tracker';
import { BodyInteractions } from '../commons/BodyInteractions';
import { TrackerPartCard } from '../tracker/TrackerPartCard';

export function BodyAssignment({
  advanced,
  onRoleSelected,
  onlyAssigned = false,
}: {
  advanced: boolean;
  onlyAssigned: boolean;
  onRoleSelected: (role: BodyPart) => void;
}) {
  const { t } = useTranslation();
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

  return (
    <>
      <BodyInteractions
        assignedRoles={assignedRoles}
        leftControls={
          <div className="flex flex-col justify-between h-full">
            <div className="flex flex-col gap-2">
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  td={trackerPartGrouped[BodyPart.HEAD]}
                  role={BodyPart.HEAD}
                  onClick={() => onRoleSelected(BodyPart.HEAD)}
                  direction="right"
                />
              )}
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
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
                  td={trackerPartGrouped[BodyPart.RIGHT_SHOULDER]}
                  role={BodyPart.RIGHT_SHOULDER}
                  onClick={() => onRoleSelected(BodyPart.RIGHT_SHOULDER)}
                  direction="right"
                />
              )}
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.RIGHT_UPPER_ARM]}
                role={BodyPart.RIGHT_UPPER_ARM}
                onClick={() => onRoleSelected(BodyPart.RIGHT_UPPER_ARM)}
                direction="right"
              />
            </div>
            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.RIGHT_LOWER_ARM]}
                role={BodyPart.RIGHT_LOWER_ARM}
                onClick={() => onRoleSelected(BodyPart.RIGHT_LOWER_ARM)}
                direction="right"
              />

              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  td={trackerPartGrouped[BodyPart.RIGHT_HAND]}
                  role={BodyPart.RIGHT_HAND}
                  onClick={() => onRoleSelected(BodyPart.RIGHT_HAND)}
                  direction="right"
                />
              )}
            </div>
            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.RIGHT_UPPER_LEG]}
                role={BodyPart.RIGHT_UPPER_LEG}
                onClick={() => onRoleSelected(BodyPart.RIGHT_UPPER_LEG)}
                direction="right"
              />

              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.RIGHT_LOWER_LEG]}
                role={BodyPart.RIGHT_LOWER_LEG}
                onClick={() => onRoleSelected(BodyPart.RIGHT_LOWER_LEG)}
                direction="right"
              />
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.RIGHT_FOOT]}
                role={BodyPart.RIGHT_FOOT}
                onClick={() => onRoleSelected(BodyPart.RIGHT_FOOT)}
                direction="right"
              />
            </div>
          </div>
        }
        rightControls={
          <div className="flex flex-col justify-between h-full">
            <TrackerPartCard
              onlyAssigned={onlyAssigned}
              td={trackerPartGrouped[BodyPart.CHEST]}
              role={BodyPart.CHEST}
              onClick={() => onRoleSelected(BodyPart.CHEST)}
              direction="left"
            />

            <div className="flex flex-col gap-2">
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  td={trackerPartGrouped[BodyPart.LEFT_SHOULDER]}
                  role={BodyPart.LEFT_SHOULDER}
                  onClick={() => onRoleSelected(BodyPart.LEFT_SHOULDER)}
                  direction="left"
                />
              )}

              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.LEFT_UPPER_ARM]}
                role={BodyPart.LEFT_UPPER_ARM}
                onClick={() => onRoleSelected(BodyPart.LEFT_UPPER_ARM)}
                direction="left"
              />
            </div>

            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.LEFT_LOWER_ARM]}
                role={BodyPart.LEFT_LOWER_ARM}
                onClick={() => onRoleSelected(BodyPart.LEFT_LOWER_ARM)}
                direction="left"
              />
              {advanced && (
                <TrackerPartCard
                  onlyAssigned={onlyAssigned}
                  td={trackerPartGrouped[BodyPart.LEFT_HAND]}
                  onClick={() => onRoleSelected(BodyPart.LEFT_HAND)}
                  role={BodyPart.LEFT_HAND}
                  direction="left"
                />
              )}
            </div>

            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.WAIST]}
                onClick={() => onRoleSelected(BodyPart.WAIST)}
                role={BodyPart.WAIST}
                direction="left"
              />
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.HIP]}
                onClick={() => onRoleSelected(BodyPart.HIP)}
                role={BodyPart.HIP}
                direction="left"
              />
            </div>
            <div className="flex flex-col gap-2">
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.LEFT_UPPER_LEG]}
                role={BodyPart.LEFT_UPPER_LEG}
                onClick={() => onRoleSelected(BodyPart.LEFT_UPPER_LEG)}
                direction="left"
              />

              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.LEFT_LOWER_LEG]}
                role={BodyPart.LEFT_LOWER_LEG}
                onClick={() => onRoleSelected(BodyPart.LEFT_LOWER_LEG)}
                direction="left"
              />
              <TrackerPartCard
                onlyAssigned={onlyAssigned}
                td={trackerPartGrouped[BodyPart.LEFT_FOOT]}
                role={BodyPart.LEFT_FOOT}
                onClick={() => onRoleSelected(BodyPart.LEFT_FOOT)}
                direction="left"
              />
            </div>
          </div>
        }
      ></BodyInteractions>
    </>
  );
}
