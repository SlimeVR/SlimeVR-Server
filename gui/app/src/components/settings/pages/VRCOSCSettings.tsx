import { Localized, useLocalization } from '@fluent/react';
import { yupResolver } from '@hookform/resolvers/yup';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
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
  type VRCOSCDiscoveredTargetT,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import {
  StatusBadge,
  StatusRow,
  type StatusVariant,
} from '@/components/commons/StatusBadge';
import { VRCIcon } from '@/components/commons/icon/VRCIcon';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useRelativeTime } from '@/hooks/relative-time';
import {
  OSCPortsAddress,
  useOscPortsAddressValidator,
} from '@/hooks/osc-setting-validator';

interface VRCOSCSettingsForm {
  enabled: boolean;
  useManualNetwork: boolean;
  portsAddress: OSCPortsAddress;
}

const defaultVRCOSCSettings: VRCOSCSettingsForm = {
  enabled: false,
  useManualNetwork: false,
  portsAddress: {
    portIn: 9001,
    portOut: 9000,
    address: '127.0.0.1',
  },
};

type Badge =
  | 'listening'
  | 'ready'
  | 'found'
  | 'searching'
  | 'idle'
  | 'disabled'
  | 'error';

const BADGE_VARIANTS: Record<Badge, StatusVariant> = {
  listening: 'success',
  ready: 'success',
  found: 'success',
  searching: 'special',
  idle: 'neutral',
  disabled: 'neutral',
  error: 'critical',
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

  const sourceLabel =
    targetSource === VRCOSCTargetSource.MANUAL
      ? l10n.getString('settings-osc-vrchat-status-source-manual')
      : targetSource === VRCOSCTargetSource.DISCOVERED
        ? l10n.getString('settings-osc-vrchat-status-source-auto')
        : '';

  const lastInputElapsed = relativeTime(status.lastReceivedInputMillis);
  const lastFrameElapsed = relativeTime(status.lastFrameSentMillis);

  return (
    <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-md divide-y divide-background-60">
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
          <Typography
            color="secondary"
            id="settings-osc-vrchat-status-input-idle"
          />
        ) : (
          <>
            <Typography
              color="secondary"
              id="settings-osc-vrchat-status-input-listening"
              vars={{ port: status.inputPort ?? 0 }}
            />
            {inputState === VRCOSCInputState.ERROR && status.inputError ? (
              <Typography color="secondary">
                {status.inputError?.toString() ?? ''}
              </Typography>
            ) : lastInputElapsed ? (
              <Typography
                color="secondary"
                id="settings-osc-vrchat-status-input-last-data"
                vars={{ elapsed: lastInputElapsed }}
              />
            ) : (
              <Typography
                color="secondary"
                id="settings-osc-vrchat-status-input-no-data"
              />
            )}
          </>
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
          <Typography
            color="secondary"
            id={
              status.targetAddress
                ? 'settings-osc-vrchat-status-output-waiting'
                : 'settings-osc-vrchat-status-output-idle'
            }
            vars={{
              address: status.targetAddress?.toString() ?? '',
              port: status.targetPort ?? 0,
              source: sourceLabel,
            }}
          />
        ) : (
          <>
            <Typography
              color="secondary"
              id={
                outputState === VRCOSCOutputState.READY
                  ? 'settings-osc-vrchat-status-output-sending'
                  : 'settings-osc-vrchat-status-output-target'
              }
              vars={{
                address: status.targetAddress?.toString() ?? '',
                port: status.targetPort ?? 0,
                source: sourceLabel,
              }}
            />
            {outputState === VRCOSCOutputState.ERROR && status.outputError ? (
              <Typography color="secondary">
                {status.outputError?.toString() ?? ''}
              </Typography>
            ) : lastFrameElapsed ? (
              <Typography
                color="secondary"
                id="settings-osc-vrchat-status-output-last-frame"
                vars={{ elapsed: lastFrameElapsed }}
              />
            ) : (
              <Typography
                color="secondary"
                id="settings-osc-vrchat-status-output-no-frame"
              />
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vrchat-status-oscquery"
          />
        }
        badge={<VrcBadge badge={OSCQUERY_BADGES[oscQueryState]} />}
      >
        {oscQueryState === VRCOSCOscQueryState.DISABLED ? (
          <Typography
            color="secondary"
            id="settings-osc-vrchat-status-oscquery-disabled"
          />
        ) : oscQueryState === VRCOSCOscQueryState.ERROR ? (
          <Typography color="secondary">
            {status.oscqueryError?.toString() ?? ''}
          </Typography>
        ) : (
          <>
            <Typography
              color="secondary"
              id="settings-osc-vrchat-status-oscquery-advertising"
              vars={{ port: status.oscqueryAdvertisedPort ?? 0 }}
            />
            {oscQueryState === VRCOSCOscQueryState.FOUND &&
            status.discoveredTargets.length > 0 ? (
              <>
                <Typography
                  color="secondary"
                  id="settings-osc-vrchat-status-oscquery-discovered-title"
                />
                <ul className="flex flex-col gap-1">
                  {status.discoveredTargets.map((target, index) => (
                    <li
                      key={index}
                      className="flex items-center justify-between gap-2"
                    >
                      <Typography color="secondary">
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
              <Typography
                color="secondary"
                id="settings-osc-vrchat-status-oscquery-searching"
              />
            )}
          </>
        )}
      </StatusRow>
    </div>
  );
}

export function VRCOSCSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [status, setStatus] = useState<VRCOSCStatusChangeResponseT | null>(
    null
  );

  const { oscValidator } = useOscPortsAddressValidator();
  const { reset, control, watch, handleSubmit, setValue } =
    useForm<VRCOSCSettingsForm>({
      defaultValues: defaultVRCOSCSettings,
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

  useEffect(() => {
    const subscription = watch((_value, { type }) => {
      if (type === 'change') {
        handleSubmit(onSubmit)();
      }
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.VRCOSCSettingsRequest,
      new VRCOSCSettingsRequestT()
    );
    sendRPCPacket(RpcMessage.VRCOSCStatusRequest, new VRCOSCStatusRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.VRCOSCSettingsResponse,
    (response: VRCOSCSettingsResponseT) => {
      const formData: VRCOSCSettingsForm = {
        ...defaultVRCOSCSettings,
        portsAddress: { ...defaultVRCOSCSettings.portsAddress },
      };

      formData.enabled = response.enabled;
      formData.useManualNetwork = response.useManualNetwork;
      formData.portsAddress.portIn = response.portIn;
      formData.portsAddress.portOut = response.portOut;
      formData.portsAddress.address = response.address?.toString() ?? '';

      reset(formData);
    }
  );

  useRPCPacket(
    RpcMessage.VRCOSCStatusChangeResponse,
    (response: VRCOSCStatusChangeResponseT) => {
      setStatus(response);
    }
  );

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<VRCIcon />} id="vrchat">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-osc-vrchat')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              <>
                {l10n
                  .getString('settings-osc-vrchat-description-v1')
                  .split('\n')
                  .map((line, i) => (
                    <Typography key={i}>{line}</Typography>
                  ))}
              </>
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-osc-vrchat-enable')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-vrchat-enable-description')}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="enabled"
                label={l10n.getString('settings-osc-vrchat-enable-label')}
              />
            </div>

            {enabled && status && (
              <>
                <Typography variant="section-title">
                  {l10n.getString('settings-osc-vrchat-status-title')}
                </Typography>
                <StatusCard
                  status={status}
                  onSwitchToTarget={(target) => {
                    setValue('useManualNetwork', true, {
                      shouldDirty: true,
                    });
                    setValue(
                      'portsAddress.address',
                      target.address?.toString() ?? '',
                      {
                        shouldDirty: true,
                      }
                    );
                    setValue('portsAddress.portOut', target.portOut, {
                      shouldDirty: true,
                    });
                  }}
                />
              </>
            )}

            <Typography variant="section-title" id="settings-driver-bones" />
            <div className="flex flex-col gap-2 pt-1 pb-4">
              <Typography id="settings-driver-bones-description" />
              <div className="w-fit">
                <Button
                  variant="secondary"
                  to="/settings/routing"
                  id="settings-driver-bones-link"
                />
              </div>
            </div>

            <Typography variant="section-title">
              {l10n.getString('settings-osc-vrchat-status-network-mode')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString(
                  'settings-osc-vrchat-status-network-mode-description'
                )}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="useManualNetwork"
                label={l10n.getString(
                  'settings-osc-vrchat-status-network-mode-toggle'
                )}
              />
            </div>

            {useManualNetwork && (
              <>
                <Typography variant="section-title">
                  {l10n.getString('settings-osc-vrchat-network')}
                </Typography>
                <div className="flex flex-col pb-2">
                  <Typography>
                    {l10n.getString(
                      'settings-osc-vrchat-status-network-manual-description'
                    )}
                  </Typography>
                </div>
                <div className="grid grid-cols-2 gap-3 pb-5">
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
                <Typography variant="section-title">
                  {l10n.getString('settings-osc-vrchat-network-address')}
                </Typography>
                <div className="flex flex-col pb-2">
                  <Typography>
                    {l10n.getString(
                      'settings-osc-vrchat-network-address-description-v1'
                    )}
                  </Typography>
                </div>
                <div className="grid gap-3 pb-5">
                  <Input
                    type="text"
                    control={control}
                    name="portsAddress.address"
                    placeholder={l10n.getString(
                      'settings-osc-vrchat-network-address-placeholder'
                    )}
                    label=""
                  />
                </div>
              </>
            )}
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
