import classNames from 'classnames';
import {
  Fragment,
  HTMLAttributes,
  ReactNode,
  useCallback,
  useMemo,
} from 'react';
import { useAtomValue } from 'jotai';
import { BodyPart } from 'solarxr-protocol';
import {
  BodyInteractions,
  BodyInteractionsProps,
  BodySideNames,
} from '@/components/commons/BodyInteractions';
import { ExtremityFigure } from '@/components/commons/ExtremityFigure';
import { PersonFrontIcon, SIDES } from '@/components/commons/PersonFrontIcon';
import {
  BodyPartError,
  COMMONS,
  useSuggestedBodyParts,
} from '@/hooks/tracker-picker';
import {
  assignedRolesAtom,
  FlatDeviceTracker,
  trackerByBodyPartAtom,
} from '@/store/app-store';
import { ExtremityDescriptor, ExtremitySide } from '@/utils/extremities';
import {
  DigitFlow,
  ExtremityLayoutProvider,
  ExtremityLayoutValue,
  ExtremitySlot,
} from './extremities/ExtremityLayout';
import {
  AssignmentPartCard,
  ExtremityGroupCard,
  PartCardRenderer,
} from './parts/PartCard';

type BodySide = (typeof SIDES)[number];

const LEFT_GROUPS = (side: BodySide): BodyPart[][] => [
  [BodyPart.HEAD, BodyPart.NECK],
  [side.shoulder], [side.bust], [side.upperArm],
  [side.lowerArm, side.hand],
  [BodyPart.HIP],
  [side.upperLeg, side.lowerLeg, side.foot],
];

const RIGHT_GROUPS = (side: BodySide): BodyPart[][] => [
  [BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST],
  [side.shoulder],[side.bust], [side.upperArm],
  [side.lowerArm, side.hand],
  [BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST],
  [side.upperLeg, side.lowerLeg, side.foot],
];

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

type CommonAssignmentProps = {
  dotSize?: number;
  fillHeight?: boolean;
  highlightedRoles?: BodyPart[];
  rolesWithErrors?: Partial<Record<BodyPart, BodyPartError>>;
  onRoleSelected: (role: BodyPart) => void;
  dotClass?: (part: BodyPart) => string | undefined;
  dotContent?: (part: BodyPart) => ReactNode;
  dotProps?: (part: BodyPart) => HTMLAttributes<HTMLDivElement>;
  activeParts?: BodyPart[];
};

export type BodyAssignmentViewProps = CommonAssignmentProps & {
  view: { kind: 'body' };
  mirror: boolean;
  renderCard?: PartCardRenderer;
};

export type ExtremityAssignmentViewProps = CommonAssignmentProps & {
  view: { kind: 'extremity'; descriptor: ExtremityDescriptor };
  side: ExtremitySide;
  compact?: boolean;
  fitContent?: boolean;
  renderGroup?: ExtremityGroupRenderer;
};

export type BodyPartAssignmentProps =
  | BodyAssignmentViewProps
  | ExtremityAssignmentViewProps;

type AssignmentRenderState = {
  assignedRoles: BodyPart[];
  trackerByPart: Record<number, FlatDeviceTracker | undefined>;
  suggestedBodyParts: BodyPart[];
  interactions: Omit<
    BodyInteractionsProps,
    | 'figure'
    | 'leftControls'
    | 'rightControls'
    | 'topControls'
    | 'bottomControls'
    | 'sideNames'
  >;
};

const defaultCard =
  (
    onRoleSelected: (role: BodyPart) => void,
    activeParts: BodyPart[] = []
  ): PartCardRenderer =>
  (props) => (
    <AssignmentPartCard
      {...props}
      pressed={activeParts.includes(props.role)}
      onClick={() => onRoleSelected(props.role)}
    />
  );

const defaultGroup =
  (
    onRoleSelected: (role: BodyPart) => void,
    activeParts: BodyPart[] = []
  ): ExtremityGroupRenderer =>
  ({ id, labelId, direction, rows, edge, flow }) => (
    <ExtremityGroupCard
      key={id}
      edge={edge}
      flow={flow}
      labelId={labelId}
      direction={direction}
      rows={rows}
      renderRow={(props) =>
        defaultCard(onRoleSelected, activeParts)({ ...props, compact: true })
      }
    />
  );

export function BodyPartAssignment(props: BodyPartAssignmentProps) {
  const assignedRoles = useAtomValue(assignedRolesAtom);
  const trackerByPart = useAtomValue(trackerByBodyPartAtom);
  const suggestedBodyParts = useSuggestedBodyParts();
  const {
    dotSize,
    highlightedRoles = [],
    onRoleSelected,
    dotClass,
    dotContent,
    dotProps,
    activeParts,
  } = props;

  const state: AssignmentRenderState = {
    assignedRoles,
    trackerByPart,
    suggestedBodyParts,
    interactions: {
      dotsSize: dotSize,
      dotClass,
      dotContent,
      dotProps,
      activeParts,
      assignedRoles,
      highlightedRoles,
      onSelectRole: onRoleSelected,
    },
  };

  return props.view.kind === 'body' ? (
    <BodyAssignmentView {...(props as BodyAssignmentViewProps)} state={state} />
  ) : (
    <ExtremityAssignmentView
      {...(props as ExtremityAssignmentViewProps)}
      state={state}
    />
  );
}

function BodyAssignmentView({
  state,
  mirror,
  fillHeight,
  rolesWithErrors = {},
  onRoleSelected,
  renderCard,
}: BodyAssignmentViewProps & { state: AssignmentRenderState }) {
  const { assignedRoles, trackerByPart, suggestedBodyParts, interactions } =
    state;
  const left = +!mirror;
  const right = +mirror;
  const card =
    renderCard ?? defaultCard(onRoleSelected, interactions.activeParts);

  const sideNames = useMemo<BodySideNames>(
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
      {...interactions}
      sideNames={sideNames}
      figure={figure}
      leftControls={column(LEFT_GROUPS(SIDES[left]), 'right')}
      rightControls={column(RIGHT_GROUPS(SIDES[right]), 'left')}
    />
  );
}

function ExtremityAssignmentView({
  state,
  view,
  side,
  fillHeight,
  compact = false,
  fitContent = false,
  rolesWithErrors = {},
  onRoleSelected,
  renderGroup,
  dotContent,
}: ExtremityAssignmentViewProps & { state: AssignmentRenderState }) {
  const { trackerByPart, interactions } = state;
  const { descriptor } = view;
  const { digits, root } = descriptor.sides[side];
  const mirrored = side === 'left';

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

  const extremityDotContent = useCallback(
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

  const group =
    renderGroup ?? defaultGroup(onRoleSelected, interactions.activeParts);

  const digit = useCallback(
    (name: string, { direction, edge }: ExtremitySlot, flow: DigitFlow) => {
      if (name === 'root')
        return group({
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

      return group({
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
    [descriptor, digits, root, cell, group, mirrored]
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
      fitContent,
      mirrored,
      digit,
      figure,
      interactions: {
        ...interactions,
        dotContent: dotContent ?? extremityDotContent,
        sideNames,
      },
    }),
    [
      compact,
      fitContent,
      mirrored,
      digit,
      figure,
      interactions,
      dotContent,
      extremityDotContent,
      sideNames,
    ]
  );

  const { Layout } = descriptor;

  return (
    <ExtremityLayoutProvider value={layout}>
      <Layout />
    </ExtremityLayoutProvider>
  );
}
