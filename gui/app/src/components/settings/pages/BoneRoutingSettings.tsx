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

const BONE_GROUPS: {
  id: string;
  icon: ReactNode;
  bones: BodyPart[];
  defaultOpen: boolean;
}[] = [
  {
    id: 'spine',
    icon: <ChestIcon width={20} />,
    defaultOpen: true,
    bones: [
      BodyPart.HEAD,
      BodyPart.NECK,
      BodyPart.UPPER_CHEST,
      BodyPart.CHEST,
      BodyPart.WAIST,
      BodyPart.HIP,
    ],
  },
  {
    id: 'legs',
    icon: <FootIcon width={20} />,
    defaultOpen: true,
    bones: [
      BodyPart.LEFT_UPPER_LEG,
      BodyPart.LEFT_LOWER_LEG,
      BodyPart.LEFT_FOOT,
      BodyPart.RIGHT_UPPER_LEG,
      BodyPart.RIGHT_LOWER_LEG,
      BodyPart.RIGHT_FOOT,
    ],
  },
  {
    id: 'arms',
    icon: <UpperArmIcon width={20} />,
    defaultOpen: true,
    bones: [
      BodyPart.LEFT_SHOULDER,
      BodyPart.LEFT_UPPER_ARM,
      BodyPart.LEFT_LOWER_ARM,
      BodyPart.LEFT_HAND,
      BodyPart.RIGHT_SHOULDER,
      BodyPart.RIGHT_UPPER_ARM,
      BodyPart.RIGHT_LOWER_ARM,
      BodyPart.RIGHT_HAND,
    ],
  },
  {
    id: 'left_fingers',
    icon: (
      <span className="-scale-x-100">
        <FingersIcon width={22} />
      </span>
    ),
    defaultOpen: false,
    bones: [
      BodyPart.LEFT_THUMB_METACARPAL,
      BodyPart.LEFT_THUMB_PROXIMAL,
      BodyPart.LEFT_THUMB_DISTAL,
      BodyPart.LEFT_INDEX_PROXIMAL,
      BodyPart.LEFT_INDEX_INTERMEDIATE,
      BodyPart.LEFT_INDEX_DISTAL,
      BodyPart.LEFT_MIDDLE_PROXIMAL,
      BodyPart.LEFT_MIDDLE_INTERMEDIATE,
      BodyPart.LEFT_MIDDLE_DISTAL,
      BodyPart.LEFT_RING_PROXIMAL,
      BodyPart.LEFT_RING_INTERMEDIATE,
      BodyPart.LEFT_RING_DISTAL,
      BodyPart.LEFT_LITTLE_PROXIMAL,
      BodyPart.LEFT_LITTLE_INTERMEDIATE,
      BodyPart.LEFT_LITTLE_DISTAL,
    ],
  },
  {
    id: 'right_fingers',
    icon: <FingersIcon width={22} />,
    defaultOpen: false,
    bones: [
      BodyPart.RIGHT_THUMB_METACARPAL,
      BodyPart.RIGHT_THUMB_PROXIMAL,
      BodyPart.RIGHT_THUMB_DISTAL,
      BodyPart.RIGHT_INDEX_PROXIMAL,
      BodyPart.RIGHT_INDEX_INTERMEDIATE,
      BodyPart.RIGHT_INDEX_DISTAL,
      BodyPart.RIGHT_MIDDLE_PROXIMAL,
      BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
      BodyPart.RIGHT_MIDDLE_DISTAL,
      BodyPart.RIGHT_RING_PROXIMAL,
      BodyPart.RIGHT_RING_INTERMEDIATE,
      BodyPart.RIGHT_RING_DISTAL,
      BodyPart.RIGHT_LITTLE_PROXIMAL,
      BodyPart.RIGHT_LITTLE_INTERMEDIATE,
      BodyPart.RIGHT_LITTLE_DISTAL,
    ],
  },
];

const HAND_BONES = new Set([BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND]);

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
  automatic,
}: {
  status: RoutingOutputStatusT;
  routed: number;
  automatic: boolean;
}) {
  const accepts = status.accepts?.length ?? 0;
  const summary = useMemo(() => {
    switch (status.state ?? RoutingOutputState.UNSUPPORTED) {
      case RoutingOutputState.UNSUPPORTED:
        return 'unavailable';
      case RoutingOutputState.INACTIVE:
        return status.output === RoutingOutput.DRIVER ? 'stopped' : 'off';
      case RoutingOutputState.ENABLED:
        return 'idle';
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
          id={
            summary === 'idle' && automatic
              ? 'settings-routing-output-idle-automatic-description'
              : `settings-routing-output-${summary}-description`
          }
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
  bone,
  output,
  automatic,
  accepted,
  required,
  unavailable,
  routed,
  duplicate,
  onToggle,
}: {
  bone: BodyPart;
  output: RoutingOutput;
  automatic: boolean;
  accepted: boolean;
  required: boolean;
  unavailable: boolean;
  routed: boolean;
  duplicate: boolean;
  onToggle: (bone: BodyPart, output: RoutingOutput) => void;
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

  if (automatic) {
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
      name={`${bone}-${output}`}
      checked={routed}
      disabled={lockedId !== null}
      onChange={() => onToggle(bone, output)}
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
    [BodyPart, RoutingOutput] | null
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

  const duplicatedNames = useMemo(() => {
    const list = new Intl.ListFormat(currentLocales, { type: 'conjunction' });
    const outputs = new Set(
      Array.from(duplicated.values()).flatMap((clashed) => Array.from(clashed))
    );
    return {
      bones: list.format(
        Array.from(duplicated.keys()).map((bone) =>
          l10n.getString(`body_part-${BodyPart[bone]}`)
        )
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
        HAND_BONES.has(route.bone) &&
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

  const applyToggle = (bone: BodyPart, output: RoutingOutput) => {
    const next = new Map(routes);
    const boneOutputs = new Set(next.get(bone) ?? []);
    if (boneOutputs.has(output)) boneOutputs.delete(output);
    else boneOutputs.add(output);
    next.set(bone, boneOutputs);
    setRoutes(next);
    submit(automatic, next);
  };

  const toggle = (bone: BodyPart, output: RoutingOutput) => {
    const enabling = !routes.get(bone)?.has(output);
    const takesOverControllers =
      enabling && output === RoutingOutput.DRIVER && HAND_BONES.has(bone);

    if (takesOverControllers && !handsWarningAccepted) {
      setHandsWarning([bone, output]);
      return;
    }
    applyToggle(bone, output);
  };

  const setMode = (nextAutomatic: boolean) => {
    setAutomatic(nextAutomatic);
    submit(nextAutomatic, routes);
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
          if (handsWarning) applyToggle(handsWarning[0], handsWarning[1]);
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
                  automatic={automatic}
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
                            'w-full flex items-center gap-2 py-2 pl-4 pr-3 bg-background-60 hover:bg-background-50 fill-background-10 transition-colors',
                            open && 'border-b border-background-50'
                          )}
                        >
                          {group.icon}
                          <Typography
                            bold
                            id={`settings-routing-group-${group.id}`}
                          />
                          <div className="ml-auto fill-background-10">
                            {open ? (
                              <ArrowUpIcon size={16} />
                            ) : (
                              <ArrowDownIcon size={16} />
                            )}
                          </div>
                        </button>

                        {open && (
                          <div className="divide-y divide-background-60">
                            {group.bones.map((bone) => (
                              <div
                                key={bone}
                                className={classNames(ROW_CLASSES, 'py-1.5')}
                              >
                                <div className={BONE_CELL_CLASSES}>
                                  <Typography
                                    truncate
                                    id={`body_part-${BodyPart[bone]}`}
                                  />
                                </div>

                                {outputs.map((status) => (
                                  <RouteCell
                                    key={status.output}
                                    bone={bone}
                                    output={status.output}
                                    automatic={automatic}
                                    accepted={
                                      accepts.get(status.output)?.has(bone) ??
                                      false
                                    }
                                    required={
                                      requires.get(status.output)?.has(bone) ??
                                      false
                                    }
                                    unavailable={
                                      status.state ===
                                      RoutingOutputState.UNSUPPORTED
                                    }
                                    routed={
                                      routes.get(bone)?.has(status.output) ??
                                      false
                                    }
                                    duplicate={
                                      duplicated
                                        .get(bone)
                                        ?.has(status.output) ?? false
                                    }
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
