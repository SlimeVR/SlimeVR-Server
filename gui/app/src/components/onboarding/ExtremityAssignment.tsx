import { HTMLAttributes, ReactNode, useCallback, useMemo } from 'react';
import { useAtomValue } from 'jotai';
import { BodyPart } from 'solarxr-protocol';
import { ExtremityFigure } from '@/components/commons/ExtremityFigure';
import {
  DigitFlow,
  ExtremityLayoutProvider,
  ExtremityLayoutValue,
  ExtremitySlot,
} from './extremities/ExtremityLayout';
import { ExtremityDescriptor, ExtremitySide } from '@/utils/extremities';
import { BodyPartError } from '@/hooks/tracker-picker';
import {
  assignedRolesAtom,
  FlatDeviceTracker,
  trackerByBodyPartAtom,
} from '@/store/app-store';

export type ExtremityRow = {
  role: BodyPart;
  td: FlatDeviceTracker | undefined;
  roleError: string | undefined;
  labelId?: string;
  number?: number;
  /** Whether this row draws a line to its dot */
  connector: boolean;
};

export type ExtremityGroupRenderer = (args: {
  id: string;
  labelId?: string;
  direction: 'left' | 'right';
  rows: ExtremityRow[];
  edge?: 'side' | 'cap';
  flow?: DigitFlow;
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
  dotClass,
  dotProps,
  activeParts,
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
  dotClass?: (part: BodyPart) => string | undefined;
  dotProps?: (part: BodyPart) => HTMLAttributes<HTMLDivElement>;
  activeParts?: BodyPart[];
}) {
  const assignedRoles = useAtomValue(assignedRolesAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const { digits, root } = descriptor.sides[side];

  const sideNames = useMemo(() => {
    const names = new Set(
      [...Object.values(digits).flat(), root].map((part) => BodyPart[part])
    );
    return {
      left: side === 'left' ? names : new Set<string>(),
      right: side === 'right' ? names : new Set<string>(),
    };
  }, [side, digits, root]);

  /** Parts of a digit share one line, so their dots go by number */
  const partNumbers = useMemo(() => {
    const numbers = new Map<BodyPart, number>();
    descriptor.digits.forEach((digit) => {
      if (digits[digit].length < 2) return;
      digits[digit].forEach((part, i) => numbers.set(part, i + 1));
    });
    return numbers;
  }, [descriptor, digits]);

  const dotContent = useCallback(
    (part: BodyPart) => {
      const number = partNumbers.get(part);
      return (
        number && (
          <span className="text-[10px] font-bold leading-none text-background-90">
            {number}
          </span>
        )
      );
    },
    [partNumbers]
  );

  const cell = useCallback(
    (role: BodyPart) => ({
      role,
      td: trackerByPart[role],
      roleError: rolesWithErrors[role]?.label,
    }),
    [trackerByPart, rolesWithErrors]
  );

  const mirrored = side === 'left';

  const digit = useCallback(
    (name: string, { direction, edge }: ExtremitySlot, flow: DigitFlow) => {
      if (name === 'root')
        return renderGroup({
          id: name,
          direction,
          edge,
          rows: [{ ...cell(root), connector: true }],
        });

      const parts = digits[name];
      const main = descriptor.mainPart(parts);
      const rows = parts.map((part, i) => ({
        ...cell(part),
        labelId: descriptor.partLabelId(part),
        number: parts.length > 1 ? i + 1 : undefined,
        connector: part === main,
      }));

      return renderGroup({
        id: name,
        // rows of a single part carry their own name already
        labelId: parts.length > 1 ? descriptor.digitLabelId(name) : undefined,
        direction,
        edge,
        flow,
        // spread across the figure, so they follow the mirrored dots
        rows: flow === 'columns' && mirrored ? [...rows].reverse() : rows,
      });
    },
    [descriptor, digits, root, cell, renderGroup, mirrored]
  );

  const figure = useCallback(
    (height?: number) => (
      <ExtremityFigure
        spec={descriptor.figure}
        parts={descriptor.sides[side]}
        side={side}
        height={height}
        className={fillHeight ? 'absolute inset-0 h-full w-full' : 'w-full'}
      />
    ),
    [descriptor, side, fillHeight]
  );

  const layout = useMemo<ExtremityLayoutValue>(
    () => ({
      compact,
      mirrored,
      digit,
      figure,
      interactions: {
        dotsSize: dotSize,
        dotClass,
        dotContent,
        dotProps,
        activeParts,
        sideNames,
        assignedRoles,
        highlightedRoles,
        onSelectRole: onRoleSelected,
      },
    }),
    [
      compact,
      mirrored,
      digit,
      figure,
      dotSize,
      dotClass,
      dotContent,
      dotProps,
      activeParts,
      sideNames,
      assignedRoles,
      highlightedRoles,
      onRoleSelected,
    ]
  );

  const { Layout } = descriptor;

  return (
    <ExtremityLayoutProvider value={layout}>
      <Layout />
    </ExtremityLayoutProvider>
  );
}
