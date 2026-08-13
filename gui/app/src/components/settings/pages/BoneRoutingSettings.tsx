import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { ReactNode, useEffect, useMemo, useState } from 'react';
import {
  BodyPart,
  BoneRouteT,
  BoneRoutingSettingsRequestT,
  BoneRoutingSettingsResponseT,
  ChangeBoneRoutingSettingsRequestT,
  RoutingOutput,
  RoutingOutputState,
  RoutingOutputStatusT,
  RpcMessage,
} from 'solarxr-protocol';
import {
  ARM_BODY_PARTS,
  HAND_BODY_PARTS,
  LEFT_FINGER_BODY_PARTS,
  LEG_BODY_PARTS,
  RIGHT_FINGER_BODY_PARTS,
  SPINE_BODY_PARTS,
} from '@/hooks/body-parts';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocaleConfig } from '@/i18n/config';
import {
  CHECKBOX_CLASSES,
  CheckboxInternal,
} from '@/components/commons/Checkbox';
import {
  StatusBadge,
  StatusRow,
  type StatusVariant,
} from '@/components/commons/StatusBadge';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { ChestIcon } from '@/components/commons/icon/ChestIcon';
import { FingersIcon } from '@/components/commons/icon/FingersIcon';
import { FootIcon } from '@/components/commons/icon/FootIcon';
import { RouterIcon } from '@/components/commons/icon/RouterIcon';
import { UpperArmIcon } from '@/components/commons/icon/UpperArmIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { WarningBox } from '@/components/commons/TipBox';
import { HandsWarningModal } from './components/HandsWarningModal';

const OUTPUT_LABEL_ID: Record<number, string> = {
  [RoutingOutput.DRIVER]: 'settings-routing-output-driver',
  [RoutingOutput.VRC_OSC]: 'settings-routing-output-vrc_osc',
  [RoutingOutput.VMC]: 'settings-routing-output-vmc',
};

type BoneRow = { id: string; labelId: string; bones: BodyPart[] };

function boneRow(bone: BodyPart): BoneRow {
  return {
    id: BodyPart[bone],
    labelId: `body_part-${BodyPart[bone]}`,
    bones: [bone],
  };
}

function bundleRow(id: string, bones: BodyPart[]): BoneRow {
  return { id, labelId: `settings-routing-row-${id}`, bones };
}

const BONE_GROUPS: {
  id: string;
  icon: ReactNode;
  rows: BoneRow[];
  defaultOpen: boolean;
}[] = [
  {
    id: 'spine',
    icon: <ChestIcon width={20} />,
    defaultOpen: true,
    rows: SPINE_BODY_PARTS.map(boneRow),
  },
  {
    id: 'legs',
    icon: <FootIcon width={28} />,
    defaultOpen: true,
    rows: LEG_BODY_PARTS.map(boneRow),
  },
  {
    id: 'arms',
    icon: <UpperArmIcon width={25} />,
    defaultOpen: true,
    rows: ARM_BODY_PARTS.map(boneRow),
  },
  {
    id: 'fingers',
    icon: <FingersIcon width={18} />,
    defaultOpen: false,
    rows: [
      bundleRow('left_fingers', LEFT_FINGER_BODY_PARTS),
      bundleRow('right_fingers', RIGHT_FINGER_BODY_PARTS),
    ],
  },
];

const ROW_BY_BONE = new Map(
  BONE_GROUPS.flatMap((group) => group.rows).flatMap((row) =>
    row.bones.map((bone) => [bone, row] as const)
  )
);

const ROW_CLASSES = 'flex items-center pl-4 pr-3 mobile:pl-3 mobile:pr-2';

const BONE_CELL_CLASSES = 'flex-1 min-w-44 mobile:min-w-0 mobile:flex-[1.6]';

const OUTPUT_CELL_CLASSES =
  'w-32 shrink-0 mobile:w-auto mobile:flex-1 mobile:shrink flex justify-center';

type RouteMap = Map<BodyPart, Set<RoutingOutput>>;

function toRouteMap(routes: BoneRouteT[]): RouteMap {
  return new Map(
    routes.map((route) => [route.bone, new Set(route.outputs ?? [])])
  );
}

function toBoneRoutes(routes: RouteMap): BoneRouteT[] {
  return Array.from(routes.entries())
    .filter(([, outputs]) => outputs.size > 0)
    .map(([bone, outputs]) => {
      const route = new BoneRouteT();
      route.bone = bone;
      route.outputs = Array.from(outputs);
      return route;
    });
}

type BoneTables = {
  routes: RouteMap;
  accepts: Map<RoutingOutput, Set<BodyPart>>;
  requires: Map<RoutingOutput, Set<BodyPart>>;
  overridable: Map<RoutingOutput, Set<BodyPart>>;
  duplicated: Map<BodyPart, Set<RoutingOutput>>;
};

function cellState(row: BoneRow, output: RoutingOutput, tables: BoneTables) {
  const accepted = row.bones.filter((bone) =>
    tables.accepts.get(output)?.has(bone)
  );
  return {
    accepted: accepted.length > 0,
    required:
      accepted.length > 0 &&
      accepted.every((bone) => tables.requires.get(output)?.has(bone)),
    overridable:
      accepted.length > 0 &&
      accepted.every((bone) => tables.overridable.get(output)?.has(bone)),
    routed: row.bones.some((bone) => tables.routes.get(bone)?.has(output)),
    duplicate: row.bones.some((bone) =>
      tables.duplicated.get(bone)?.has(output)
    ),
  };
}

type OutputSummary =
  | 'sending'
  | 'idle'
  | 'empty'
  | 'stopped'
  | 'off'
  | 'unavailable';

const BADGE_VARIANTS: Record<OutputSummary, StatusVariant> = {
  sending: 'success',
  idle: 'special',
  empty: 'warning',
  stopped: 'neutral',
  off: 'neutral',
  unavailable: 'neutral',
};

function OutputStatusRow({
  status,
  routed,
}: {
  status: RoutingOutputStatusT;
  routed: number;
}) {
  const accepts = status.accepts?.length ?? 0;
  const summary = useMemo(() => {
    switch (status.state ?? RoutingOutputState.UNSUPPORTED) {
      case RoutingOutputState.UNSUPPORTED:
        return 'unavailable';
      case RoutingOutputState.INACTIVE:
        return 'off';
      case RoutingOutputState.ENABLED:
        return status.output === RoutingOutput.DRIVER ? 'stopped' : 'idle';
      default:
        return routed === 0 ? 'empty' : 'sending';
    }
  }, [status, routed]);

  return (
    <StatusRow
      label={
        <Typography
          variant="section-title"
          id={OUTPUT_LABEL_ID[status.output]}
        />
      }
      badge={
        <StatusBadge
          variant={BADGE_VARIANTS[summary]}
          id={`settings-routing-output-badge-${summary}`}
        />
      }
    >
      <div className="flex items-baseline justify-between gap-3 mobile:flex-col mobile:gap-0">
        <Typography
          color="secondary"
          id={`settings-routing-output-${summary}-description`}
        />
        {(summary === 'sending' ||
          summary === 'idle' ||
          summary === 'empty') && (
          <Typography
            color="secondary"
            whitespace="whitespace-nowrap"
            id="settings-routing-output-bone-count"
            vars={{ routed, accepts }}
          />
        )}
      </div>
    </StatusRow>
  );
}

function RouteCell({
  row,
  output,
  automatic,
  accepted,
  required,
  overridable,
  unavailable,
  routed,
  duplicate,
  onToggle,
}: {
  row: BoneRow;
  output: RoutingOutput;
  automatic: boolean;
  accepted: boolean;
  required: boolean;
  overridable: boolean;
  unavailable: boolean;
  routed: boolean;
  duplicate: boolean;
  onToggle: (row: BoneRow, output: RoutingOutput) => void;
}) {
  const cell = (content: ReactNode, className?: string) => (
    <div
      className={classNames(OUTPUT_CELL_CLASSES, 'items-center h-6', className)}
    >
      {content}
    </div>
  );

  if (!accepted) {
    return cell(<div className="w-2.5 h-0.5 rounded bg-background-40" />);
  }

  if (automatic && !overridable) {
    return routed
      ? cell(<CheckIcon size={13} />, 'fill-accent-background-20')
      : cell(<div className="w-1.5 h-1.5 rounded-full bg-background-50" />);
  }

  const lockedId = required
    ? 'settings-routing-cell-required'
    : unavailable
      ? 'settings-routing-cell-unavailable'
      : null;
  const tooltipId =
    lockedId ?? (duplicate ? 'settings-routing-cell-duplicate' : null);

  const checkbox = (
    <input
      type="checkbox"
      className={classNames(
        CHECKBOX_CLASSES,
        lockedId && 'brightness-50 hover:cursor-not-allowed',
        duplicate && 'outline outline-2 outline-status-warning'
      )}
      name={`${row.id}-${output}`}
      checked={routed}
      disabled={lockedId !== null}
      onChange={() => onToggle(row, output)}
    />
  );

  return cell(
    tooltipId ? (
      <Tooltip preferedDirection="top" content={<Typography id={tooltipId} />}>
        {checkbox}
      </Tooltip>
    ) : (
      checkbox
    )
  );
}

export function BoneRoutingSettings() {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [settings, setSettings] = useState(new BoneRoutingSettingsResponseT());

  const [automatic, setAutomatic] = useState(true);
  const [routes, setRoutes] = useState<RouteMap>(new Map());
  const [openGroups, setOpenGroups] = useState<Set<string>>(
    () => new Set(BONE_GROUPS.filter((g) => g.defaultOpen).map((g) => g.id))
  );
  const [handsWarning, setHandsWarning] = useState<
    [BoneRow, RoutingOutput] | null
  >(null);
  const [handsWarningAccepted, setHandsWarningAccepted] = useState(false);

  const outputs = useMemo(() => settings.outputs ?? [], [settings]);

  const accepts = useMemo(
    () =>
      new Map(
        outputs.map((status) => [status.output, new Set(status.accepts ?? [])])
      ),
    [outputs]
  );

  const requires = useMemo(
    () =>
      new Map(
        outputs.map((status) => [status.output, new Set(status.requires ?? [])])
      ),
    [outputs]
  );

  const overridable = useMemo(
    () =>
      new Map(
        outputs.map((status) => [
          status.output,
          new Set(status.overridable ?? []),
        ])
      ),
    [outputs]
  );

  const conflicts = useMemo(
    () =>
      new Map(
        outputs.map((status) => [
          status.output,
          new Set(status.conflicts ?? []),
        ])
      ),
    [outputs]
  );

  const routedCounts = useMemo(() => {
    const counts = new Map<RoutingOutput, number>(
      outputs.map((status) => [status.output, 0])
    );
    for (const boneOutputs of routes.values()) {
      for (const output of boneOutputs) {
        counts.set(output, (counts.get(output) ?? 0) + 1);
      }
    }
    return counts;
  }, [outputs, routes]);

  const duplicated = useMemo(() => {
    const result = new Map<BodyPart, Set<RoutingOutput>>();
    for (const [bone, boneOutputs] of routes) {
      for (const output of boneOutputs) {
        const clashes = conflicts.get(output);
        if (!clashes) continue;
        for (const other of boneOutputs) {
          if (other === output || !clashes.has(other)) continue;
          const clashed = result.get(bone) ?? new Set<RoutingOutput>();
          clashed.add(output).add(other);
          result.set(bone, clashed);
        }
      }
    }
    return result;
  }, [routes, conflicts]);

  const tables = useMemo(
    () => ({ routes, accepts, requires, overridable, duplicated }),
    [routes, accepts, requires, overridable, duplicated]
  );

  const duplicatedNames = useMemo(() => {
    const list = new Intl.ListFormat(currentLocales, { type: 'conjunction' });
    const outputs = new Set(
      Array.from(duplicated.values()).flatMap((clashed) => Array.from(clashed))
    );
    const labels = new Map(
      Array.from(duplicated.keys()).map((bone) => {
        const row = ROW_BY_BONE.get(bone) ?? boneRow(bone);
        return [row.id, row.labelId];
      })
    );
    return {
      bones: list.format(
        Array.from(labels.values()).map((labelId) => l10n.getString(labelId))
      ),
      outputs: list.format(
        Array.from(outputs).map((output) =>
          l10n.getString(OUTPUT_LABEL_ID[output])
        )
      ),
    };
  }, [duplicated, l10n, currentLocales]);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.BoneRoutingSettingsRequest,
      new BoneRoutingSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    setAutomatic(settings.automatic ?? true);
    setRoutes(toRouteMap(settings.routes ?? []));

    const handsAlreadyOnDriver = (settings.routes ?? []).some(
      (route) =>
        HAND_BODY_PARTS.includes(route.bone) &&
        (route.outputs ?? []).includes(RoutingOutput.DRIVER)
    );
    if (handsAlreadyOnDriver) setHandsWarningAccepted(true);
  }, [settings]);

  useRPCPacket(
    RpcMessage.BoneRoutingSettingsResponse,
    (res: BoneRoutingSettingsResponseT) => setSettings(res)
  );

  const submit = (nextAutomatic: boolean, nextRoutes: RouteMap) => {
    const req = new ChangeBoneRoutingSettingsRequestT();
    req.automatic = nextAutomatic;
    req.routes = toBoneRoutes(nextRoutes);
    sendRPCPacket(RpcMessage.ChangeBoneRoutingSettingsRequest, req);
  };

  const applyToggle = (
    row: BoneRow,
    output: RoutingOutput,
    enable: boolean
  ) => {
    const next = new Map(routes);
    for (const bone of row.bones) {
      if (!accepts.get(output)?.has(bone)) continue;
      if (requires.get(output)?.has(bone)) continue;
      const boneOutputs = new Set(next.get(bone) ?? []);
      if (enable) boneOutputs.add(output);
      else boneOutputs.delete(output);
      next.set(bone, boneOutputs);
    }
    setRoutes(next);
    submit(automatic, next);
  };

  const toggle = (row: BoneRow, output: RoutingOutput) => {
    const enabling = !cellState(row, output, tables).routed;
    const takesOverControllers =
      enabling &&
      output === RoutingOutput.DRIVER &&
      row.bones.some((bone) => HAND_BODY_PARTS.includes(bone));

    if (takesOverControllers && !handsWarningAccepted) {
      setHandsWarning([row, output]);
      return;
    }
    applyToggle(row, output, enabling);
  };

  const setMode = (nextAutomatic: boolean) => {
    setAutomatic(nextAutomatic);
    submit(nextAutomatic, new Map());
  };

  const toggleGroup = (id: string) => {
    const next = new Set(openGroups);
    if (next.has(id)) next.delete(id);
    else next.add(id);
    setOpenGroups(next);
  };

  return (
    <>
      <HandsWarningModal
        isOpen={handsWarning !== null}
        onClose={() => setHandsWarning(null)}
        accept={() => {
          setHandsWarningAccepted(true);
          if (handsWarning) applyToggle(handsWarning[0], handsWarning[1], true);
          setHandsWarning(null);
        }}
      />
      <SettingsPageLayout>
        <SettingsPagePaneLayout icon={<RouterIcon />} id="routing">
          <>
            <Typography variant="main-title" id="settings-routing" />
            <div className="flex flex-col pt-1 pb-4">
              <Typography
                id="settings-routing-description"
                whitespace="whitespace-pre-line"
              />
            </div>

            <Typography variant="section-title" id="settings-routing-mode" />
            <div className="flex flex-col gap-2 pt-1 pb-5">
              <Typography
                id="settings-routing-mode-description"
                whitespace="whitespace-pre-line"
              />
              <CheckboxInternal
                variant="toggle"
                outlined
                name="automatic"
                checked={automatic}
                onChange={() => setMode(!automatic)}
                label={l10n.getString('settings-routing-automatic-label')}
              />
            </div>

            <Typography variant="section-title" id="settings-routing-outputs" />
            <div className="flex flex-col bg-background-80 px-4 py-2 mt-1 mb-3 rounded-md divide-y divide-background-60">
              {outputs.map((status) => (
                <OutputStatusRow
                  key={status.output}
                  status={status}
                  routed={routedCounts.get(status.output) ?? 0}
                />
              ))}
            </div>

            <Typography variant="section-title" id="settings-routing-bones" />
            <div className="pt-1 pb-2">
              <Typography id="settings-routing-bones-description" />
            </div>
            {duplicated.size > 0 && (
              <div className="pb-2">
                <WarningBox whitespace={false}>
                  {l10n.getString('settings-routing-duplicate-warning', {
                    boneCount: duplicated.size,
                    bones: duplicatedNames.bones,
                    outputs: duplicatedNames.outputs,
                  })}
                </WarningBox>
              </div>
            )}

            <div className="rounded-2xl bg-background-80 shadow-sm mb-1 overflow-hidden min-w-0">
              <div className="overflow-x-auto min-w-0">
                <div className="min-w-fit mobile:min-w-0">
                  <div
                    className={classNames(
                      ROW_CLASSES,
                      'py-3 border-b border-background-50 uppercase mobile:[&_h2]:text-standard-bold'
                    )}
                  >
                    <div className={BONE_CELL_CLASSES}>
                      <Typography
                        variant="section-title"
                        id="settings-routing-bone"
                      />
                    </div>
                    {outputs.map((status) => (
                      <div key={status.output} className={OUTPUT_CELL_CLASSES}>
                        <Typography
                          variant="section-title"
                          textAlign="text-center"
                          id={OUTPUT_LABEL_ID[status.output]}
                        />
                      </div>
                    ))}
                  </div>

                  {BONE_GROUPS.map((group) => {
                    const open = openGroups.has(group.id);
                    return (
                      <div
                        key={group.id}
                        className="border-b border-background-50 last:border-b-0"
                      >
                        <button
                          type="button"
                          onClick={() => toggleGroup(group.id)}
                          className={classNames(
                            'w-full flex items-center gap-4 pl-4 pr-4 h-12 bg-background-60 hover:bg-background-50 fill-background-10 transition-colors',
                            open && 'border-b border-background-50'
                          )}
                        >
                          <div className="w-6">{group.icon}</div>
                          <Typography
                            bold
                            id={`settings-routing-group-${group.id}`}
                          />
                          <div className="ml-auto fill-background-10">
                            {open ? (
                              <ArrowUpIcon size={20} />
                            ) : (
                              <ArrowDownIcon size={20} />
                            )}
                          </div>
                        </button>

                        {open && (
                          <div className="divide-y divide-background-60">
                            {group.rows.map((row) => (
                              <div
                                key={row.id}
                                className={classNames(ROW_CLASSES, 'py-1.5')}
                              >
                                <div className={BONE_CELL_CLASSES}>
                                  <Typography truncate id={row.labelId} />
                                </div>

                                {outputs.map((status) => (
                                  <RouteCell
                                    key={status.output}
                                    row={row}
                                    output={status.output}
                                    automatic={automatic}
                                    unavailable={
                                      status.state ===
                                      RoutingOutputState.UNSUPPORTED
                                    }
                                    {...cellState(row, status.output, tables)}
                                    onToggle={toggle}
                                  />
                                ))}
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          </>
        </SettingsPagePaneLayout>
      </SettingsPageLayout>
    </>
  );
}
