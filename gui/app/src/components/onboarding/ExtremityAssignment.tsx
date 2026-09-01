import classNames from 'classnames';
import { ReactNode, useCallback, useMemo } from 'react';
import { useAtomValue } from 'jotai';
import { BodyPart } from 'solarxr-protocol';
import {
  BodyInteractions,
  BodySlotStyler,
} from '@/components/commons/BodyInteractions';
import { ExtremityFigure } from '@/components/commons/ExtremityFigure';
import { ExtremityDescriptor, ExtremitySide } from '@/utils/extremities';
import { BodyPartError } from '@/hooks/tracker-assignment';
import {
  assignedTrackersAtom,
  FlatDeviceTracker,
  trackerByBodyPartAtom,
} from '@/store/app-store';

export type ExtremityJoint = {
  role: BodyPart;
  td: FlatDeviceTracker | undefined;
  roleError: string | undefined;
};

export type ExtremityGroupRenderer = (args: {
  digit: string;
  labelId?: string;
  direction: 'left' | 'right';
  joints: ExtremityJoint[];
  edge?: 'side' | 'cap';
}) => ReactNode;

export function ExtremityAssignment({
  descriptor,
  side,
  dotSize,
  fillHeight,
  compact = false,
  highlightedRoles = [],
  rolesWithErrors = {},
  onRoleSelected,
  renderGroup,
  slotStyle,
}: {
  descriptor: ExtremityDescriptor;
  side: ExtremitySide;
  dotSize?: number;
  fillHeight?: boolean;
  compact?: boolean;
  highlightedRoles?: BodyPart[];
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  onRoleSelected: (role: BodyPart) => void;
  renderGroup: ExtremityGroupRenderer;
  slotStyle?: BodySlotStyler;
}) {
  const assignedTrackers = useAtomValue(assignedTrackersAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const { digits, root } = descriptor.sides[side];
  const { layout } = descriptor;

  const assignedRoles = useMemo(
    () =>
      assignedTrackers.map(
        ({ tracker }) => tracker.info?.bodyPart || BodyPart.NONE
      ),
    [assignedTrackers]
  );

  const sideNames = useMemo(() => {
    const names = new Set(
      [...Object.values(digits).flat(), root].map((part) => BodyPart[part])
    );
    return {
      left: side === 'left' ? names : new Set<string>(),
      right: side === 'right' ? names : new Set<string>(),
    };
  }, [side, digits, root]);

  const jointNumbers = useMemo(() => {
    const numbers = new Map<BodyPart, number>();
    descriptor.digits.forEach((digit) =>
      digits[digit].forEach((part, joint) => numbers.set(part, joint + 1))
    );
    return numbers;
  }, [descriptor, digits]);

  const numberedSlotStyle: BodySlotStyler = useCallback(
    (part: BodyPart) => {
      const number = jointNumbers.get(part);
      return {
        ...slotStyle?.(part),
        content: number && (
          <span className="text-[10px] font-bold leading-none text-background-90">
            {number}
          </span>
        ),
      };
    },
    [jointNumbers, slotStyle]
  );

  const joints = useCallback(
    (parts: BodyPart[]): ExtremityJoint[] =>
      parts.map((role) => ({
        role,
        td: trackerByPart[role],
        roleError: rolesWithErrors[role]?.label,
      })),
    [trackerByPart, rolesWithErrors]
  );

  const mirrored = side === 'left';

  const column = ({
    group,
    direction,
    withRoot,
    className,
  }: {
    group: string[];
    direction: 'left' | 'right';
    withRoot: boolean;
    className: string;
  }) => (
    <div className={classNames('flex flex-col gap-2 h-full', className)}>
      {group.map((digit) =>
        renderGroup({
          digit,
          labelId: descriptor.digitLabelId(digit),
          direction,
          joints: joints(digits[digit]),
        })
      )}
      {withRoot &&
        renderGroup({ digit: 'root', direction, joints: joints([root]) })}
    </div>
  );

  const sideColumn = (direction: 'left' | 'right', nearSide: boolean) => {
    if (compact && !nearSide) return null;

    return (
      <div
        className={classNames('h-full', direction === 'right' && 'text-right')}
      >
        {compact
          ? column({
              group: layout.compact.side,
              direction,
              withRoot: true,
              className: 'justify-end',
            })
          : column({
              group: nearSide ? layout.roomy.nearSide : layout.roomy.farSide,
              direction,
              withRoot: !nearSide,
              className: `justify-between ${layout.roomy.jointsBand}`,
            })}
      </div>
    );
  };

  return (
    <BodyInteractions
      dotsSize={dotSize}
      slotStyle={numberedSlotStyle}
      sideNames={sideNames}
      assignedRoles={assignedRoles}
      highlightedRoles={highlightedRoles}
      onSelectRole={onRoleSelected}
      figure={
        <ExtremityFigure
          spec={descriptor.figure}
          parts={descriptor.sides[side]}
          side={side}
          height={compact ? layout.compact.figureHeight : undefined}
          className={fillHeight ? 'absolute inset-0 h-full w-full' : 'w-full'}
        />
      }
      topControls={
        compact && (
          <div className="grid grid-cols-3 gap-1 smol:gap-2 pb-1 [&>*]:w-full">
            {(mirrored
              ? [...layout.compact.top].reverse()
              : layout.compact.top
            ).map((digit) =>
              renderGroup({
                digit,
                labelId: descriptor.digitLabelId(digit),
                direction: 'left',
                joints: joints(digits[digit]),
                edge: 'cap',
              })
            )}
          </div>
        )
      }
      leftControls={sideColumn('right', !mirrored)}
      rightControls={sideColumn('left', mirrored)}
    />
  );
}
