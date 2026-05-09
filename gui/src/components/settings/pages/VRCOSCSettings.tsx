import { Localized, useLocalization } from '@fluent/react';
import { yupResolver } from '@hookform/resolvers/yup';
import classNames from 'classnames';
import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { boolean, number, object, string } from 'yup';
import {
  ChangeVRCOSCSettingsRequestT,
  OSCTrackersSettingT,
  RpcMessage,
  VRCOSCInputState,
  VRCOSCNetworkSettingsT,
  VRCOSCOscQueryState,
  VRCOSCOutputState,
  VRCOSCSettingsRequestT,
  VRCOSCSettingsResponseT,
  VRCOSCSettingsT,
  VRCOSCStatusChangeResponseT,
  VRCOSCStatusRequestT,
  VRCOSCTargetSource,
  type VRCOSCDiscoveredTargetT,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import { VRCIcon } from '@/components/commons/icon/VRCIcon';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { useWebsocketAPI } from '@/hooks/websocket-api';

interface VRCOSCSettingsForm {
  vrchat: {
    enabled: boolean;
    useManualNetwork: boolean;
    manualNetwork: {
      portIn: number;
      portOut: number;
      address: string;
    };
    trackers: {
      head: boolean;
      chest: boolean;
      elbows: boolean;
      feet: boolean;
      knees: boolean;
      hands: boolean;
      waist: boolean;
    };
  };
}

const createDefaultValues = (): VRCOSCSettingsForm => ({
  vrchat: {
    enabled: false,
    useManualNetwork: false,
    manualNetwork: {
      portIn: 9001,
      portOut: 9000,
      address: '127.0.0.1',
    },
    trackers: {
      head: false,
      chest: false,
      elbows: false,
      feet: false,
      knees: false,
      hands: false,
      waist: false,
    },
  },
});

function asString(value: string | Uint8Array | null | undefined): string {
  return typeof value === 'string' ? value : '';
}

function formatElapsedTime(timestamp: bigint | null | undefined, now: number) {
  if (!timestamp) return null;

  const elapsedSeconds = Math.max(
    0,
    Math.floor((now - Number(timestamp)) / 1000)
  );
  if (elapsedSeconds < 5) return 'just now';
  if (elapsedSeconds < 60) return `${elapsedSeconds}s ago`;

  const elapsedMinutes = Math.floor(elapsedSeconds / 60);
  if (elapsedMinutes < 60) return `${elapsedMinutes}m ago`;

  const elapsedHours = Math.floor(elapsedMinutes / 60);
  return `${elapsedHours}h ago`;
}

type BadgeKind =
  | 'idle'
  | 'listening'
  | 'ready'
  | 'found'
  | 'searching'
  | 'disabled'
  | 'error';

const BADGE_LABEL_KEY: Record<BadgeKind, string> = {
  idle: 'settings-osc-vrchat-status-badge-idle',
  listening: 'settings-osc-vrchat-status-badge-listening',
  ready: 'settings-osc-vrchat-status-badge-ready',
  found: 'settings-osc-vrchat-status-badge-found',
  searching: 'settings-osc-vrchat-status-badge-searching',
  disabled: 'settings-osc-vrchat-status-badge-disabled',
  error: 'settings-osc-vrchat-status-badge-error',
};

const BADGE_CLASSES: Record<BadgeKind, string> = {
  idle: 'bg-background-50',
  listening: 'bg-status-success',
  ready: 'bg-status-success',
  found: 'bg-status-success',
  searching: 'bg-status-special',
  disabled: 'bg-background-50',
  error: 'bg-status-critical',
};

function StatusBadge({ kind }: { kind: BadgeKind }) {
  return (
    <span
      className={classNames(
        'rounded-md px-2 py-1 bg-background-70 flex gap-2 items-center',
      )}
    >
      <div
        className={classNames('h-2 w-2 rounded-full', BADGE_CLASSES[kind])}
      />
      <Typography id={BADGE_LABEL_KEY[kind]} bold/>
    </span>
  );
}

function StatusRow({
  label,
  badge,
  children,
}: {
  label: string;
  badge: BadgeKind;
  children?: React.ReactNode;
}) {
  return (
    <div className="flex flex-col gap-1 py-2">
      <div className="flex items-center justify-between gap-2">
        <Typography variant="section-title">{label}</Typography>
        <StatusBadge kind={badge} />
      </div>
      {children}
    </div>
  );
}

function inputBadge(
  state: VRCOSCInputState,
  lastReceivedMillis: bigint | null | undefined
): BadgeKind {
  switch (state) {
    case VRCOSCInputState.LISTENING:
      return lastReceivedMillis ? 'listening' : 'ready';
    case VRCOSCInputState.ERROR:
      return 'error';
    default:
      return 'idle';
  }
}

function outputBadge(state: VRCOSCOutputState): BadgeKind {
  switch (state) {
    case VRCOSCOutputState.READY:
      return 'ready';
    case VRCOSCOutputState.ERROR:
      return 'error';
    default:
      return 'idle';
  }
}

function oscQueryBadge(state: VRCOSCOscQueryState): BadgeKind {
  switch (state) {
    case VRCOSCOscQueryState.FOUND:
      return 'found';
    case VRCOSCOscQueryState.SEARCHING:
      return 'searching';
    case VRCOSCOscQueryState.ERROR:
      return 'error';
    default:
      return 'disabled';
  }
}

function StatusCard({
  status,
  now,
  onSwitchToTarget,
}: {
  status: VRCOSCStatusChangeResponseT;
  now: number;
  onSwitchToTarget: (target: VRCOSCDiscoveredTargetT) => void;
}) {
  const { l10n } = useLocalization();

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

  const lastInputElapsed = formatElapsedTime(
    status.lastReceivedInputMillis,
    now
  );
  const lastFrameElapsed = formatElapsedTime(status.lastFrameSentMillis, now);

  return (
    <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-md divide-y divide-background-60">
      <StatusRow
        label={l10n.getString('settings-osc-vrchat-status-input')}
        badge={inputBadge(inputState, status.lastReceivedInputMillis)}
      >
        {inputState === VRCOSCInputState.IDLE ? (
          <Typography color="secondary">
            {l10n.getString('settings-osc-vrchat-status-input-idle')}
          </Typography>
        ) : (
          <>
            <Typography color="secondary">
              {l10n.getString('settings-osc-vrchat-status-input-listening', {
                port: status.inputPort ?? 0,
              })}
            </Typography>
            {inputState === VRCOSCInputState.ERROR && status.inputError ? (
              <Typography color="secondary">
                {asString(status.inputError)}
              </Typography>
            ) : lastInputElapsed ? (
              <Typography color="secondary">
                {l10n.getString('settings-osc-vrchat-status-input-last-data', {
                  elapsed: lastInputElapsed,
                })}
              </Typography>
            ) : (
              <Typography color="secondary">
                {l10n.getString('settings-osc-vrchat-status-input-no-data')}
              </Typography>
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={l10n.getString('settings-osc-vrchat-status-output')}
        badge={outputBadge(outputState)}
      >
        {outputState === VRCOSCOutputState.IDLE ? (
          <Typography color="secondary">
            {l10n.getString('settings-osc-vrchat-status-output-idle')}
          </Typography>
        ) : (
          <>
            <Typography color="secondary">
              {l10n.getString(
                outputState === VRCOSCOutputState.READY
                  ? 'settings-osc-vrchat-status-output-sending'
                  : 'settings-osc-vrchat-status-output-target',
                {
                  address: asString(status.targetAddress),
                  port: status.targetPort ?? 0,
                  source: sourceLabel,
                }
              )}
            </Typography>
            {outputState === VRCOSCOutputState.ERROR && status.outputError ? (
              <Typography color="secondary">
                {asString(status.outputError)}
              </Typography>
            ) : lastFrameElapsed ? (
              <Typography color="secondary">
                {l10n.getString(
                  'settings-osc-vrchat-status-output-last-frame',
                  { elapsed: lastFrameElapsed }
                )}
              </Typography>
            ) : (
              <Typography color="secondary">
                {l10n.getString('settings-osc-vrchat-status-output-no-frame')}
              </Typography>
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={l10n.getString('settings-osc-vrchat-status-oscquery')}
        badge={oscQueryBadge(oscQueryState)}
      >
        {oscQueryState === VRCOSCOscQueryState.DISABLED ? (
          <Typography color="secondary">
            {l10n.getString('settings-osc-vrchat-status-oscquery-disabled')}
          </Typography>
        ) : oscQueryState === VRCOSCOscQueryState.ERROR ? (
          <Typography color="secondary">
            {asString(status.oscqueryError)}
          </Typography>
        ) : (
          <>
            <Typography color="secondary">
              {l10n.getString(
                'settings-osc-vrchat-status-oscquery-advertising',
                { port: status.oscqueryAdvertisedPort ?? 0 }
              )}
            </Typography>
            {oscQueryState === VRCOSCOscQueryState.FOUND &&
            status.discoveredTargets.length > 0 ? (
              <>
                <Typography color="secondary">
                  {l10n.getString(
                    'settings-osc-vrchat-status-oscquery-discovered-title'
                  )}
                </Typography>
                <ul className="flex flex-col gap-1">
                  {status.discoveredTargets.map((target, index) => (
                    <li
                      key={index}
                      className="flex items-center justify-between gap-2"
                    >
                      <Typography color="secondary">
                        {asString(target.name)} ({asString(target.address)}:
                        {target.portOut})
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
              <Typography color="secondary">
                {l10n.getString(
                  'settings-osc-vrchat-status-oscquery-searching'
                )}
              </Typography>
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
  const [now, setNow] = useState(() => Date.now());

  const bannedPorts = [6969, 21110];
  const portValidator = useMemo(
    () =>
      number()
        .typeError(' ')
        .required()
        .test(
          'ports-dont-match',
          l10n.getString('settings-osc-common-network-ports_match_error'),
          (_port, context) => context.parent.portIn != context.parent.portOut
        )
        .notOneOf(bannedPorts, (context) =>
          l10n.getString('settings-osc-common-network-port_banned_error', {
            port: context.originalValue,
          })
        ),
    [l10n]
  );

  const { reset, control, watch, handleSubmit, setValue } = useForm<VRCOSCSettingsForm>({
    defaultValues: createDefaultValues(),
    reValidateMode: 'onChange',
    mode: 'onChange',
    resolver: yupResolver(
      object({
        vrchat: object({
          enabled: boolean().required(),
          useManualNetwork: boolean().required(),
          manualNetwork: object({
            portIn: portValidator,
            portOut: portValidator,
            address: string()
              .required(' ')
              .matches(
                /^(?!0)(?!.*\.$)((1?\d?\d|25[0-5]|2[0-4]\d)(\.|$)){4}$/i,
                { message: ' ' }
              ),
          }),
          trackers: object({
            head: boolean().required(),
            chest: boolean().required(),
            elbows: boolean().required(),
            feet: boolean().required(),
            knees: boolean().required(),
            hands: boolean().required(),
            waist: boolean().required(),
          }),
        }),
      })
    ),
  });

  const enabled = watch('vrchat.enabled');
  const useManualNetwork = watch('vrchat.useManualNetwork');

  useEffect(() => {
    const interval = window.setInterval(() => setNow(Date.now()), 1_000);
    return () => window.clearInterval(interval);
  }, []);

  const onSubmit = (values: VRCOSCSettingsForm) => {
    const req = new ChangeVRCOSCSettingsRequestT();
    const vrcOsc = new VRCOSCSettingsT();

    vrcOsc.enabled = values.vrchat.enabled;
    vrcOsc.trackers = Object.assign(
      new OSCTrackersSettingT(),
      values.vrchat.trackers
    );
    vrcOsc.manualNetwork = values.vrchat.useManualNetwork
      ? Object.assign(new VRCOSCNetworkSettingsT(), values.vrchat.manualNetwork)
      : null;

    req.settings = vrcOsc;
    sendRPCPacket(RpcMessage.ChangeVRCOSCSettingsRequest, req);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
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
      const settings = response.settings;
      const formData = createDefaultValues();
      if (settings) {
        formData.vrchat.enabled = settings.enabled;
        if (settings.trackers) formData.vrchat.trackers = settings.trackers;
        if (settings.manualNetwork) {
          formData.vrchat.useManualNetwork = true;
          formData.vrchat.manualNetwork.portIn = settings.manualNetwork.portIn;
          formData.vrchat.manualNetwork.portOut =
            settings.manualNetwork.portOut;
          if (settings.manualNetwork.address) {
            formData.vrchat.manualNetwork.address = asString(
              settings.manualNetwork.address
            );
          }
        }
      }

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
                name="vrchat.enabled"
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
                  now={now}
                  onSwitchToTarget={(target) => {
                    setValue('vrchat.useManualNetwork', true, {
                      shouldDirty: true,
                    });
                    setValue(
                      'vrchat.manualNetwork.address',
                      asString(target.address),
                      { shouldDirty: true }
                    );
                    setValue(
                      'vrchat.manualNetwork.portOut',
                      target.portOut,
                      { shouldDirty: true }
                    );
                  }}
                />
              </>
            )}

            <Typography variant="section-title">
              {l10n.getString('settings-osc-vrchat-network-trackers')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString(
                  'settings-osc-vrchat-network-trackers-description'
                )}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="vrchat.trackers.chest"
                label={l10n.getString(
                  'settings-osc-vrchat-network-trackers-chest'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="vrchat.trackers.waist"
                label={l10n.getString(
                  'settings-osc-vrchat-network-trackers-hip'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="vrchat.trackers.knees"
                label={l10n.getString(
                  'settings-osc-vrchat-network-trackers-knees'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="vrchat.trackers.feet"
                label={l10n.getString(
                  'settings-osc-vrchat-network-trackers-feet'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="vrchat.trackers.elbows"
                label={l10n.getString(
                  'settings-osc-vrchat-network-trackers-elbows'
                )}
              />
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
                name="vrchat.useManualNetwork"
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
                      name="vrchat.manualNetwork.portIn"
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
                      name="vrchat.manualNetwork.portOut"
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
                    name="vrchat.manualNetwork.address"
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
