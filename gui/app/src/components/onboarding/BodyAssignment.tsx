import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { Fragment, useCallback, useMemo } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import {
  BodyInteractions,
  BodySlotStyler,
} from '@/components/commons/BodyInteractions';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { CompareIcon } from '@/components/commons/icon/CompareIcon';
import { TogglePill, TogglePillOption } from '@/components/commons/TogglePill';
import { TrackerPartCard } from '@/components/tracker/TrackerPartCard';
import { PartCardRenderer } from './parts/PartCard';
import {
  ASSIGNMENT_MODES,
  BodyPartError,
  COMMONS,
  useAssignMode,
} from '@/hooks/tracker-picker';
import { PersonFrontIcon, SIDES } from '@/components/commons/PersonFrontIcon';
import { useAtomValue } from 'jotai';
import { assignedTrackersAtom, trackerByBodyPartAtom } from '@/store/app-store';

type BodySide = (typeof SIDES)[number];

const LEFT_GROUPS = (side: BodySide): BodyPart[][] => [
  [BodyPart.HEAD, BodyPart.NECK],
  [side.shoulder, side.upperArm],
  [side.lowerArm, side.hand],
  [BodyPart.HIP],
  [side.upperLeg, side.lowerLeg, side.foot],
];

const RIGHT_GROUPS = (side: BodySide): BodyPart[][] => [
  [BodyPart.UPPER_CHEST, BodyPart.CHEST],
  [side.shoulder, side.upperArm],
  [side.lowerArm, side.hand],
  [BodyPart.WAIST],
  [side.upperLeg, side.lowerLeg, side.foot],
];

export function ShowAllPartsToggle({ compact }: { compact?: boolean }) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();

  return (
    <div className={classNames('w-fit shrink-0', compact && '[&_label]:h-fit')}>
      <CheckboxInternal
        variant="toggle"
        name="showAllBodyParts"
        checked={config?.assignShowAllBodyParts ?? false}
        onChange={() =>
          setConfig({
            assignShowAllBodyParts: !config?.assignShowAllBodyParts,
          })
        }
        label={l10n.getString(
          compact
            ? 'onboarding-assign_trackers-show_all-short'
            : 'onboarding-assign_trackers-show_all'
        )}
      />
    </div>
  );
}

export function MirrorLegend({ compact }: { compact?: boolean }) {
  const { config, setConfig } = useConfig();
  const mirror = config?.mirrorView ?? false;

  return (
    <TogglePill
      compact={compact}
      onClick={() => setConfig({ mirrorView: !mirror })}
    >
      <TogglePillOption
        compact={compact}
        dotClass="outline-assign-left"
        labelId={
          mirror
            ? 'onboarding-assign_trackers-side-left'
            : 'onboarding-assign_trackers-side-right'
        }
      />
      <CompareIcon width={22} />
      <TogglePillOption
        compact={compact}
        dotClass="outline-assign-right"
        labelId={
          mirror
            ? 'onboarding-assign_trackers-side-right'
            : 'onboarding-assign_trackers-side-left'
        }
      />
    </TogglePill>
  );
}

export function BodyAssignment({
  mirror,
  onRoleSelected,
  rolesWithErrors = {},
  highlightedRoles = [],
  dotSize,
  fillHeight,
  renderCard,
  slotStyle,
}: {
  mirror: boolean;
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  highlightedRoles?: BodyPart[];
  onRoleSelected: (role: BodyPart) => void;
  dotSize?: number;
  fillHeight?: boolean;
  renderCard?: PartCardRenderer;
  slotStyle?: BodySlotStyler;
}) {
  const assignMode = useAssignMode();
  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);

  const assignedRoles = useMemo(
    () =>
      assignedTrackers.map(
        ({ tracker }) => tracker.info?.bodyPart || BodyPart.NONE
      ),
    [assignedTrackers]
  );

  const left = +!mirror;
  const right = +mirror;

  const sideNames = useMemo(
    () => ({
      left: new Set(Object.values(SIDES[left]).map((part) => BodyPart[part])),
      right: new Set(Object.values(SIDES[right]).map((part) => BodyPart[part])),
    }),
    [left, right]
  );

  const hasBodyPart = useCallback(
    (part: BodyPart) =>
      COMMONS.includes(part) || ASSIGNMENT_MODES[assignMode].includes(part),
    [assignMode]
  );

  const card: PartCardRenderer =
    renderCard ??
    (({ role, direction, td, roleError }) => (
      <TrackerPartCard
        roleError={roleError}
        td={td}
        role={role}
        onClick={() => onRoleSelected(role)}
        direction={direction}
      />
    ));

  const column = (groups: BodyPart[][], direction: 'left' | 'right') => (
    <div
      className={classNames(
        'flex flex-col justify-between h-full',
        direction === 'right' && 'text-right'
      )}
    >
      {groups.map((group, index) => (
        <div key={index} className="flex flex-col gap-2">
          {group.filter(hasBodyPart).map((role) => (
            <Fragment key={role}>
              {card({
                role,
                direction,
                td: trackerByPart[role],
                roleError: rolesWithErrors[role]?.label,
              })}
            </Fragment>
          ))}
        </div>
      ))}
    </div>
  );

  return (
    <BodyInteractions
      dotsSize={dotSize}
      slotStyle={slotStyle}
      sideNames={sideNames}
      figure={
        <PersonFrontIcon
          mirror={mirror}
          className={fillHeight ? 'absolute inset-0 h-full w-full' : 'w-full'}
        />
      }
      assignedRoles={assignedRoles}
      highlightedRoles={highlightedRoles}
      onSelectRole={onRoleSelected}
      leftControls={column(LEFT_GROUPS(SIDES[left]), 'right')}
      rightControls={column(RIGHT_GROUPS(SIDES[right]), 'left')}
    />
  );
}
