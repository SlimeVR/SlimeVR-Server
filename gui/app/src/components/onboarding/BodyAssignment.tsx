import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import {
  Fragment,
  HTMLAttributes,
  ReactNode,
  useCallback,
  useMemo,
} from 'react';
import { BodyPart } from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { BodyInteractions } from '@/components/commons/BodyInteractions';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { CompareIcon } from '@/components/commons/icon/CompareIcon';
import { TogglePill, TogglePillOption } from '@/components/commons/TogglePill';
import { TrackerPartCard } from '@/components/tracker/TrackerPartCard';
import { PartCardRenderer } from './parts/PartCard';
import {
  BodyPartError,
  COMMONS,
  useSuggestedBodyParts,
} from '@/hooks/tracker-picker';
import { PersonFrontIcon, SIDES } from '@/components/commons/PersonFrontIcon';
import { useAtomValue } from 'jotai';
import { assignedRolesAtom, trackerByBodyPartAtom } from '@/store/app-store';

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

  const pill = (side: 'left' | 'right') => {
    const oposite = side == 'left' ? 'right' : 'left';
    if (mirror) side = oposite;
    return (
      <TogglePillOption
        compact={compact}
        dotClass={`outline-assign-${side}`}
        labelId={`onboarding-assign_trackers-side-${side}`}
      />
    );
  };

  return (
    <TogglePill
      compact={compact}
      onClick={() => setConfig({ mirrorView: !mirror })}
    >
      {pill('left')}
      <CompareIcon width={22} />
      {pill('right')}
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
  dotClass,
  dotContent,
  dotProps,
  activeParts,
}: {
  mirror: boolean;
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  highlightedRoles?: BodyPart[];
  onRoleSelected: (role: BodyPart) => void;
  dotSize?: number;
  fillHeight?: boolean;
  renderCard?: PartCardRenderer;
  dotClass?: (part: BodyPart) => string | undefined;
  dotContent?: (part: BodyPart) => ReactNode;
  dotProps?: (part: BodyPart) => HTMLAttributes<HTMLDivElement>;
  activeParts?: BodyPart[];
}) {
  const suggestedBodyParts = useSuggestedBodyParts();
  const assignedRoles = useAtomValue(assignedRolesAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);

  const left = +!mirror;
  const right = +mirror;

  const sideNames = useMemo(
    () => ({
      left: new Set(Object.values(SIDES[0]).map((part) => BodyPart[part])),
      right: new Set(Object.values(SIDES[1]).map((part) => BodyPart[part])),
    }),
    []
  );

  const figure = useMemo(
    () => (
      <PersonFrontIcon
        mirror={mirror}
        className={fillHeight ? 'absolute inset-0 h-full w-full' : 'w-full'}
      />
    ),
    [mirror, fillHeight]
  );

  const hasBodyPart = useCallback(
    (part: BodyPart) =>
      COMMONS.includes(part) ||
      suggestedBodyParts.includes(part) ||
      assignedRoles.includes(part),
    [suggestedBodyParts, assignedRoles]
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
      dotClass={dotClass}
      dotContent={dotContent}
      dotProps={dotProps}
      activeParts={activeParts}
      sideNames={sideNames}
      figure={figure}
      assignedRoles={assignedRoles}
      highlightedRoles={highlightedRoles}
      onSelectRole={onRoleSelected}
      leftControls={column(LEFT_GROUPS(SIDES[left]), 'right')}
      rightControls={column(RIGHT_GROUPS(SIDES[right]), 'left')}
    />
  );
}
