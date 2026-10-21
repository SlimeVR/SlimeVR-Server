import { Localized, useLocalization } from '@fluent/react';
import { yupResolver } from '@hookform/resolvers/yup';
import { useEffect, useState } from 'react';
import { FieldPath, FieldPathValue, useForm } from 'react-hook-form';
import { boolean, object } from 'yup';
import {
  ChangeVRCOSCSettingsRequestT,
  RpcMessage,
  VRCOSCInputState,
  VRCOSCOscQueryState,
  VRCOSCOutputState,
  VRCOSCSettingsRequestT,
  VRCOSCSettingsResponseT,
  VRCOSCStatusChangeResponseT,
  VRCOSCStatusRequestT,
  VRCOSCTargetSource,
  VRCOSCTrackingDataState,
  type VRCOSCDiscoveredTargetT,
} from 'solarxr-protocol';
import { A } from '@/components/commons/A';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Input } from '@/components/commons/Input';
import { DropdownInside } from '@/components/commons/Dropdown';
import { Typography } from '@/components/commons/Typography';
import {
  StatusBadge,
  StatusRow,
  type StatusVariant,
} from '@/components/commons/StatusBadge';
import { VRCIcon } from '@/components/commons/icon/VRCIcon';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
  SettingsGroupLabel,
} from '@/components/settings/SettingRow';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { settingReset } from '@/components/settings/SettingReset';
import { useRelativeTime } from '@/hooks/relative-time';
import {
  OSCPortsAddress,
  useOscPortsAddressValidator,
} from '@/hooks/osc-setting-validator';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

interface VRCOSCSettingsForm {
  enabled: boolean;
  useManualNetwork: boolean;
  portsAddress: OSCPortsAddress;
}

function toForm(settings: VRCOSCSettingsResponseT): VRCOSCSettingsForm {
  return {
    enabled: settings.enabled,
    useManualNetwork: settings.useManualNetwork,
    portsAddress: {
      portIn: settings.portIn,
      portOut: settings.portOut,
      address: settings.address?.toString() ?? '',
    },
  };
}

type Badge =
  | 'listening'
  | 'ready'
  | 'found'
  | 'searching'
  | 'idle'
  | 'disabled'
  | 'error'
  | 'received'
  | 'not-sent'
  | 'unknown';

const BADGE_VARIANTS: Record<Badge, StatusVariant> = {
  listening: 'success',
  ready: 'success',
  found: 'success',
  searching: 'special',
  idle: 'neutral',
  disabled: 'neutral',
  error: 'critical',
  received: 'success',
  'not-sent': 'warning',
  unknown: 'neutral',
};

function VrcBadge({ badge }: { badge: Badge }) {
  return (
    <StatusBadge
      variant={BADGE_VARIANTS[badge]}
      id={`settings-osc-vrchat-status-badge-${badge}`}
    />
  );
}

const INPUT_BADGES: Record<VRCOSCInputState, Badge> = {
  [VRCOSCInputState.IDLE]: 'idle',
  [VRCOSCInputState.LISTENING]: 'ready',
  [VRCOSCInputState.ERROR]: 'error',
};

const OUTPUT_BADGES: Record<VRCOSCOutputState, Badge> = {
  [VRCOSCOutputState.IDLE]: 'idle',
  [VRCOSCOutputState.READY]: 'ready',
  [VRCOSCOutputState.ERROR]: 'error',
};

const OSCQUERY_BADGES: Record<VRCOSCOscQueryState, Badge> = {
  [VRCOSCOscQueryState.DISABLED]: 'disabled',
  [VRCOSCOscQueryState.SEARCHING]: 'searching',
  [VRCOSCOscQueryState.FOUND]: 'found',
  [VRCOSCOscQueryState.ERROR]: 'error',
};

const TRACKING_DATA_BADGES: Record<VRCOSCTrackingDataState, Badge> = {
  [VRCOSCTrackingDataState.UNKNOWN]: 'unknown',
  [VRCOSCTrackingDataState.DISABLED_IN_VRCHAT]: 'not-sent',
  [VRCOSCTrackingDataState.RECEIVED]: 'received',
};

function inputBadge(state: VRCOSCInputState, received: boolean): Badge {
  if (state === VRCOSCInputState.LISTENING && received) return 'listening';
  return INPUT_BADGES[state];
}

function StatusCard({
  status,
  onSwitchToTarget,
}: {
  status: VRCOSCStatusChangeResponseT;
  onSwitchToTarget: (target: VRCOSCDiscoveredTargetT) => void;
}) {
  const { l10n } = useLocalization();
  const relativeTime = useRelativeTime();

  const inputState = status.inputState ?? VRCOSCInputState.IDLE;
  const outputState = status.outputState ?? VRCOSCOutputState.IDLE;
  const oscQueryState = status.oscqueryState ?? VRCOSCOscQueryState.DISABLED;
  const targetSource = status.targetSource ?? VRCOSCTargetSource.NONE;
  const trackingDataState =
    status.trackingDataState ?? VRCOSCTrackingDataState.UNKNOWN;

  const sourceLabel =
    targetSource === VRCOSCTargetSource.MANUAL
      ? l10n.getString('settings-osc-vrchat-status-source-manual')
      : targetSource === VRCOSCTargetSource.DISCOVERED
        ? l10n.getString('settings-osc-vrchat-status-source-auto')
        : '';

  const lastInputElapsed = relativeTime(status.lastReceivedInputMillis);
  const lastFrameElapsed = relativeTime(status.lastFrameSentMillis);
  const lastTrackingElapsed = relativeTime(status.lastReceivedTrackingMillis);

  return (
    <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-lg divide-y divide-background-60">
      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vrchat-status-input"
          />
        }
        badge={
          <VrcBadge
            badge={inputBadge(inputState, !!status.lastReceivedInputMillis)}
          />
        }
      >
        {inputState === VRCOSCInputState.IDLE ? (
          <Typography id="settings-osc-vrchat-status-input-idle" />
        ) : (
          <>
            <Typography
              id="settings-osc-vrchat-status-input-listening"
              vars={{ port: status.inputPort?.toString() ?? 'null' }}
            />
            {inputState === VRCOSCInputState.ERROR && status.inputError ? (
              <Typography>{status.inputError?.toString() ?? ''}</Typography>
            ) : lastInputElapsed ? (
              <Typography
                id="settings-osc-vrchat-status-input-last-data"
                vars={{ elapsed: lastInputElapsed }}
              />
            ) : (
              <Typography id="settings-osc-vrchat-status-input-no-data" />
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vrchat-status-tracking"
          />
        }
        badge={<VrcBadge badge={TRACKING_DATA_BADGES[trackingDataState]} />}
      >
        {trackingDataState === VRCOSCTrackingDataState.RECEIVED ? (
          lastTrackingElapsed ? (
            <Typography
              id="settings-osc-vrchat-status-tracking-received"
              vars={{ elapsed: lastTrackingElapsed }}
            />
          ) : null
        ) : trackingDataState === VRCOSCTrackingDataState.DISABLED_IN_VRCHAT ? (
          <Typography
            id="settings-osc-vrchat-status-tracking-disabled"
            elems={{
              OscTrackingLink: (
                <A href="https://docs.slimevr.dev/server/osc-information.html" />
              ),
            }}
          />
        ) : (
          <Typography id="settings-osc-vrchat-status-tracking-unknown" />
        )}
      </StatusRow>

      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vrchat-status-output"
          />
        }
        badge={<VrcBadge badge={OUTPUT_BADGES[outputState]} />}
      >
        {outputState === VRCOSCOutputState.IDLE ? (
          <>
            <Typography
              id={
                status.targetAddress
                  ? 'settings-osc-vrchat-status-output-waiting'
                  : 'settings-osc-vrchat-status-output-idle'
              }
              vars={{
                address: status.targetAddress?.toString() ?? '',
                port: status.targetPort?.toString() ?? 'null',
                source: sourceLabel,
              }}
            />
            <Typography id="settings-osc-vrchat-status-output-no-frame" />
          </>
        ) : (
          <>
            <Typography
              id={
                outputState === VRCOSCOutputState.READY
                  ? 'settings-osc-vrchat-status-output-sending'
                  : 'settings-osc-vrchat-status-output-target'
              }
              vars={{
                address: status.targetAddress?.toString() ?? '',
                port: status.targetPort?.toString() ?? 'null',
                source: sourceLabel,
              }}
            />
            {outputState === VRCOSCOutputState.ERROR && status.outputError ? (
              <Typography>{status.outputError?.toString() ?? ''}</Typography>
            ) : lastFrameElapsed ? (
              <Typography
                id="settings-osc-vrchat-status-output-last-frame"
                vars={{ elapsed: lastFrameElapsed }}
              />
            ) : (
              <Typography id="settings-osc-vrchat-status-output-no-frame" />
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={<Typography variant="section-title">OSCQuery</Typography>}
        badge={<VrcBadge badge={OSCQUERY_BADGES[oscQueryState]} />}
      >
        {oscQueryState === VRCOSCOscQueryState.DISABLED ? (
          <Typography id="settings-osc-vrchat-status-oscquery-disabled" />
        ) : oscQueryState === VRCOSCOscQueryState.ERROR ? (
          <Typography>{status.oscqueryError?.toString() ?? ''}</Typography>
        ) : (
          <>
            <Typography
              id="settings-osc-vrchat-status-oscquery-advertising"
              vars={{
                port: status.oscqueryAdvertisedPort?.toString() ?? 'null',
              }}
            />
            {oscQueryState === VRCOSCOscQueryState.FOUND &&
            status.discoveredTargets.length > 0 ? (
              <>
                <Typography id="settings-osc-vrchat-status-oscquery-discovered-title" />
                <ul className="flex flex-col gap-1">
                  {status.discoveredTargets.map((target, index) => (
                    <li
                      key={index}
                      className="flex items-center justify-between gap-2"
                    >
                      <Typography>
                        {target.name?.toString() ?? ''} (
                        {target.address?.toString() ?? ''}:{target.portOut})
                      </Typography>
                      {status.discoveredTargets.length > 1 && (
                        <Button
                          variant="tertiary"
                          onClick={() => onSwitchToTarget(target)}
                        >
                          {l10n.getString(
                            'settings-osc-vrchat-status-oscquery-switch'
                          )}
                        </Button>
                      )}
                    </li>
                  ))}
                </ul>
              </>
            ) : (
              <Typography id="settings-osc-vrchat-status-oscquery-searching" />
            )}
          </>
        )}
      </StatusRow>
    </div>
  );
}

export const vrchatOscSettingsPage = defineSettingsPage({
  path: 'osc/vrchat',
  title: 'settings-sidebar-vrchat_osc',
  groups: {
    output: {
      title: 'settings-osc-vrchat-output_group',
      rows: {
        enable: {
          title: 'settings-osc-vrchat-enable',
          description: 'settings-osc-vrchat-enable-description',
        },
        bones: {
          title: 'settings-driver-bones',
          description: 'settings-driver-bones-description',
        },
      },
    },
    network: {
      title: 'settings-osc-vrchat-network_group',
      rows: {
        mode: {
          title: 'settings-osc-vrchat-status-network-mode',
          description: 'settings-osc-vrchat-status-network-mode-description',
        },
        network: {
          title: 'settings-osc-vrchat-network',
          description: 'settings-osc-vrchat-status-network-manual-description',
        },
        address: {
          title: 'settings-osc-vrchat-network-address',
          description: 'settings-osc-vrchat-network-address-description-v1',
        },
      },
    },
  },
});

const { groups } = vrchatOscSettingsPage;

export function VRCOSCSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const [status, setStatus] = useState<VRCOSCStatusChangeResponseT | null>(
    null
  );

  const { oscValidator } = useOscPortsAddressValidator();
  const defaults = useSettingsDefaults().vrcosc;
  const { reset, control, watch, handleSubmit, setValue } =
    useForm<VRCOSCSettingsForm>({
      defaultValues: toForm(defaults),
      reValidateMode: 'onChange',
      mode: 'onChange',
      resolver: yupResolver(
        object({
          enabled: boolean().required(),
          useManualNetwork: boolean().required(),
          portsAddress: oscValidator,
        })
      ),
    });

  const enabled = watch('enabled');
  const useManualNetwork = watch('useManualNetwork');

  const onSubmit = (values: VRCOSCSettingsForm) => {
    const req = new ChangeVRCOSCSettingsRequestT();

    req.enabled = values.enabled;
    req.useManualNetwork = values.useManualNetwork;
    req.portIn = values.portsAddress.portIn;
    req.portOut = values.portsAddress.portOut;
    req.address = values.portsAddress.address;

    sendRPCPacket(RpcMessage.ChangeVRCOSCSettingsRequest, req);
  };

  const resetTo = <P extends FieldPath<VRCOSCSettingsForm>>(
    name: P,
    value: FieldPathValue<VRCOSCSettingsForm, P> | undefined
  ) =>
    settingReset<VRCOSCSettingsForm, P>(value, watch(name), (next) => {
      setValue(name, next, { shouldDirty: true });
      handleSubmit(onSubmit)();
    });

  const defaultAddress = defaults.address?.toString();
  const resetPorts = {
    reset: {
      modified:
        watch('portsAddress.portIn') !== defaults.portIn ||
        watch('portsAddress.portOut') !== defaults.portOut,
      onReset: () => {
        setValue('portsAddress.portIn', defaults.portIn, { shouldDirty: true });
        setValue('portsAddress.portOut', defaults.portOut, {
          shouldDirty: true,
        });
        handleSubmit(onSubmit)();
      },
    },
  };

  useEffect(() => {
    const subscription = watch((_value, { type }) => {
      if (type === 'change') {
        handleSubmit(onSubmit)();
      }
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.VRCOSCSettingsRequest,
      new VRCOSCSettingsRequestT()
    );
    sendRPCPacket(RpcMessage.VRCOSCStatusRequest, new VRCOSCStatusRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.VRCOSCSettingsResponse,
    (response: VRCOSCSettingsResponseT) => {
      reset(toForm(response));
    }
  );

  useRPCPacket(
    RpcMessage.VRCOSCStatusChangeResponse,
    (response: VRCOSCStatusChangeResponseT) => {
      setStatus(response);
    }
  );

  return (
    <form className="w-full">
      <SettingsPagePaneLayout
        icon={<VRCIcon />}
        title={vrchatOscSettingsPage.title}
      >
        <div className="flex flex-col">
          <div className="flex flex-col pb-4 max-w-[75ch]">
            {l10n
              .getString('settings-osc-vrchat-description-v1')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </div>

          <SettingsGroup group={groups.output}>
            <SettingRow
              setting={groups.output.rows.enable}
              controlId="vrchat-enabled"
              {...resetTo('enabled', defaults.enabled)}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="enabled"
                  id="vrchat-enabled"
                />
              }
            />
            <SettingRow
              setting={groups.output.rows.bones}
              control={
                <Button
                  variant="secondary"
                  className="whitespace-nowrap"
                  to="/settings/routing"
                  id="settings-driver-bones-link"
                />
              }
            />
          </SettingsGroup>

          {enabled && status && (
            <div className="flex flex-col gap-2 mt-8 min-w-0">
              <div className="px-3 xs:px-4">
                <SettingsGroupLabel id="settings-osc-vrchat-status-title" />
              </div>
              <StatusCard
                status={status}
                onSwitchToTarget={(target) => {
                  setValue('useManualNetwork', true, { shouldDirty: true });
                  setValue(
                    'portsAddress.address',
                    target.address?.toString() ?? '',
                    { shouldDirty: true }
                  );
                  setValue('portsAddress.portOut', target.portOut, {
                    shouldDirty: true,
                  });
                }}
              />
            </div>
          )}

          <SettingsAdvanced>
            <SettingsGroup group={groups.network}>
              <SettingRow
                setting={groups.network.rows.mode}
                controlWidth="wide"
                {...resetTo('useManualNetwork', defaults.useManualNetwork)}
                childrenDisabled={!useManualNetwork}
                control={
                  <DropdownInside
                    name="networkMode"
                    display="block"
                    direction="down"
                    placeholder=""
                    value={useManualNetwork ? 'manual' : 'auto'}
                    onChange={(value) => {
                      setValue('useManualNetwork', value === 'manual', {
                        shouldDirty: true,
                      });
                      handleSubmit(onSubmit)();
                    }}
                    items={[
                      {
                        label: l10n.getString(
                          'settings-osc-vrchat-status-network-mode-auto'
                        ),
                        value: 'auto',
                      },
                      {
                        label: l10n.getString(
                          'settings-osc-vrchat-status-network-mode-manual'
                        ),
                        value: 'manual',
                      },
                    ]}
                  />
                }
              >
                <>
                  <SettingRow
                    setting={groups.network.rows.network}
                    {...resetPorts}
                    below={
                      <div className="grid sm:grid-cols-2 gap-3">
                        <Localized
                          id="settings-osc-vrchat-network-port_in"
                          attrs={{ placeholder: true, label: true }}
                        >
                          <Input
                            type="number"
                            control={control}
                            name="portsAddress.portIn"
                            placeholder="9001"
                            label=""
                          />
                        </Localized>
                        <Localized
                          id="settings-osc-vrchat-network-port_out"
                          attrs={{ placeholder: true, label: true }}
                        >
                          <Input
                            type="number"
                            control={control}
                            name="portsAddress.portOut"
                            placeholder="9000"
                            label=""
                          />
                        </Localized>
                      </div>
                    }
                  />
                  <SettingRow
                    setting={groups.network.rows.address}
                    {...resetTo('portsAddress.address', defaultAddress)}
                    below={
                      <Input
                        type="text"
                        control={control}
                        name="portsAddress.address"
                        placeholder={l10n.getString(
                          'settings-osc-vrchat-network-address-placeholder'
                        )}
                        label=""
                      />
                    }
                  />
                </>
              </SettingRow>
            </SettingsGroup>
          </SettingsAdvanced>
        </div>
      </SettingsPagePaneLayout>
    </form>
  );
}
