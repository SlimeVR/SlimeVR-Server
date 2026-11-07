import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { FieldPath, FieldPathValue, useForm } from 'react-hook-form';
import {
  ChangeVMCOSCSettingsRequestT,
  ChangeVRMSettingsRequestT,
  RpcMessage,
  VMCOSCInputState,
  VMCOSCOutputState,
  VMCOSCSettingsRequestT,
  VMCOSCSettingsResponseT,
  VMCOSCStatusChangeResponseT,
  VMCOSCStatusRequestT,
  VMCOSCVrmState,
  VRMSettingsRequestT,
  VRMSettingsResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useRelativeTime } from '@/hooks/relative-time';
import { CheckBox } from '@/components/commons/Checkbox';
import { FileInput } from '@/components/commons/FileInput';
import { VMCIcon } from '@/components/commons/icon/VMCIcon';
import { Input } from '@/components/commons/Input';
import {
  StatusBadge,
  StatusRow,
  type StatusVariant,
} from '@/components/commons/StatusBadge';
import { Typography } from '@/components/commons/Typography';
import { magic } from '@/utils/formatting';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
  SettingsGroupLabel,
} from '@/components/settings/SettingRow';
import { error } from '@/utils/logging';
import {
  OSCPortsAddress,
  useOscPortsAddressValidator,
} from '@/hooks/osc-setting-validator';
import { yupResolver } from '@hookform/resolvers/yup';
import { boolean, object } from 'yup';
import { Button } from '@/components/commons/Button';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { settingReset } from '@/components/settings/SettingReset';
import { defineSettingsPage } from '@/components/settings/settings-definitions';

interface VMCSettingsForm {
  enabled: boolean;
  portsAddress: OSCPortsAddress;
  anchorHip: boolean;
  mirrorTracking: boolean;
}

function toForm(
  settings: VMCOSCSettingsResponseT,
  fallback?: VMCSettingsForm
): VMCSettingsForm {
  return {
    enabled: settings.enabled,
    portsAddress: {
      portIn: settings.portIn || (fallback?.portsAddress.portIn ?? 0),
      portOut: settings.portOut || (fallback?.portsAddress.portOut ?? 0),
      address:
        settings.address?.toString() || (fallback?.portsAddress.address ?? ''),
    },
    anchorHip: settings.anchorHip,
    mirrorTracking: settings.mirrorTracking,
  };
}

export function VRMFileUpload() {
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const { l10n } = useLocalization();
  const [modelName, setModelName] = useState<string | null>(null);

  const { control, watch, getValues } = useForm<{
    vrmJson?: FileList;
  }>({
    defaultValues: { vrmJson: undefined },
    reValidateMode: 'onChange',
    mode: 'onChange',
  });

  const updateVRMJson = async () => {
    const vrmJson = getValues('vrmJson');
    const req = new ChangeVRMSettingsRequestT();
    if (vrmJson !== undefined) {
      if (vrmJson.length > 0) {
        const file = await parseVRMFile(vrmJson[0]);
        if (file) {
          req.vrmJson = file.json;
          setModelName(file.name);
        }
      } else {
        req.vrmJson = '';
        setModelName(null);
      }
    }
    sendRPCPacket(RpcMessage.ChangeVRMSettingsRequest, req);
  };

  useEffect(() => {
    const subscription = watch((_value, { type }) => {
      if (type === 'change') {
        updateVRMJson();
      }
    });
    return () => subscription.unsubscribe();
  }, []);

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.VMCOSCSettingsRequest,
      new VMCOSCSettingsRequestT()
    );
    sendRPCPacket(RpcMessage.VRMSettingsRequest, new VRMSettingsRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.VRMSettingsResponse,
    (settings: VRMSettingsResponseT) => {
      const vrmJson = settings.vrmJson?.toString();
      if (vrmJson) {
        let data: any;
        try {
          data = JSON.parse(vrmJson);
          setModelName(getVRMName(data) || '');
        } catch (e) {
          error('Failed to fetch VRM name: ' + e);
        }
      }
    }
  );

  return (
    <FileInput
      control={control}
      name="vrmJson"
      rules={{
        required: false,
      }}
      value="help"
      importedFileName={
        // if modelName is an empty string, it's an untitled model
        modelName === ''
          ? l10n.getString('settings-osc-vmc-vrm-untitled_model')
          : modelName
      }
      label="settings-osc-vmc-vrm-file_select"
      accept="model/gltf-binary, model/gltf+json, model/vrml, .vrm, .glb, .gltf"
    />
  );
}

type Badge = 'listening' | 'ready' | 'idle' | 'disabled' | 'error';

const BADGE_VARIANTS: Record<Badge, StatusVariant> = {
  listening: 'success',
  ready: 'success',
  idle: 'neutral',
  disabled: 'neutral',
  error: 'critical',
};

function VmcBadge({ badge }: { badge: Badge }) {
  return (
    <StatusBadge
      variant={BADGE_VARIANTS[badge]}
      id={`settings-osc-vmc-status-badge-${badge}`}
    />
  );
}

const INPUT_BADGES: Record<VMCOSCInputState, Badge> = {
  [VMCOSCInputState.IDLE]: 'idle',
  [VMCOSCInputState.LISTENING]: 'ready',
  [VMCOSCInputState.ERROR]: 'error',
};

const OUTPUT_BADGES: Record<VMCOSCOutputState, Badge> = {
  [VMCOSCOutputState.IDLE]: 'idle',
  [VMCOSCOutputState.READY]: 'ready',
  [VMCOSCOutputState.ERROR]: 'error',
};

const VRM_BADGES: Record<VMCOSCVrmState, Badge> = {
  [VMCOSCVrmState.NONE]: 'disabled',
  [VMCOSCVrmState.LOADED]: 'ready',
  [VMCOSCVrmState.ERROR]: 'error',
};

function inputBadge(state: VMCOSCInputState, received: boolean): Badge {
  if (state === VMCOSCInputState.LISTENING && received) return 'listening';
  return INPUT_BADGES[state];
}

function StatusCard({ status }: { status: VMCOSCStatusChangeResponseT }) {
  const relativeTime = useRelativeTime();

  const inputState = status.inputState ?? VMCOSCInputState.IDLE;
  const outputState = status.outputState ?? VMCOSCOutputState.IDLE;
  const vrmState = status.vrmState ?? VMCOSCVrmState.NONE;

  const lastInputElapsed = relativeTime(status.lastReceivedInputMillis);
  const lastFrameElapsed = relativeTime(status.lastFrameSentMillis);

  return (
    <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-lg divide-y divide-background-60">
      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vmc-status-input"
          />
        }
        badge={
          <VmcBadge
            badge={inputBadge(inputState, !!status.lastReceivedInputMillis)}
          />
        }
      >
        {inputState === VMCOSCInputState.IDLE ? (
          <Typography id="settings-osc-vmc-status-input-idle" />
        ) : (
          <>
            <Typography
              id="settings-osc-vmc-status-input-listening"
              vars={{ port: status.inputPort?.toString() ?? 'null' }}
            />
            {inputState === VMCOSCInputState.ERROR && status.inputError ? (
              <Typography>{status.inputError?.toString() ?? ''}</Typography>
            ) : lastInputElapsed ? (
              <Typography
                id="settings-osc-vmc-status-input-last-data"
                vars={{ elapsed: lastInputElapsed }}
              />
            ) : (
              <Typography id="settings-osc-vmc-status-input-no-data" />
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vmc-status-output"
          />
        }
        badge={<VmcBadge badge={OUTPUT_BADGES[outputState]} />}
      >
        {outputState === VMCOSCOutputState.IDLE ? (
          status.targetAddress ? (
            <Typography
              id="settings-osc-vmc-status-output-waiting"
              vars={{
                address: status.targetAddress.toString(),
                port: status.targetPort?.toString() ?? 'null',
              }}
            />
          ) : (
            <Typography id="settings-osc-vmc-status-output-idle" />
          )
        ) : (
          <>
            <Typography
              id={
                outputState === VMCOSCOutputState.READY
                  ? 'settings-osc-vmc-status-output-sending'
                  : 'settings-osc-vmc-status-output-target'
              }
              vars={{
                address: status.targetAddress?.toString() ?? '',
                port: status.targetPort?.toString() ?? 'null',
              }}
            />
            {outputState === VMCOSCOutputState.ERROR && status.outputError ? (
              <Typography>{status.outputError?.toString() ?? ''}</Typography>
            ) : lastFrameElapsed ? (
              <Typography
                id="settings-osc-vmc-status-output-last-frame"
                vars={{ elapsed: lastFrameElapsed }}
              />
            ) : (
              <Typography id="settings-osc-vmc-status-output-no-frame" />
            )}
          </>
        )}
      </StatusRow>

      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-osc-vmc-status-vrm"
          />
        }
        badge={<VmcBadge badge={VRM_BADGES[vrmState]} />}
      >
        {vrmState === VMCOSCVrmState.ERROR && status.vrmError ? (
          <Typography>{status.vrmError?.toString() ?? ''}</Typography>
        ) : (
          <Typography
            id={
              vrmState === VMCOSCVrmState.LOADED
                ? 'settings-osc-vmc-status-vrm-loaded'
                : 'settings-osc-vmc-status-vrm-none'
            }
          />
        )}
      </StatusRow>
    </div>
  );
}

export const vmcSettingsPage = defineSettingsPage({
  path: 'osc/vmc',
  title: 'settings-sidebar-osc_vmc',
  groups: {
    output: {
      title: 'settings-osc-vmc-output_group',
      rows: {
        enable: {
          title: 'settings-osc-vmc-enable',
          description: 'settings-osc-vmc-enable-description',
        },
        bones: {
          title: 'settings-driver-bones',
          description: 'settings-driver-bones-description',
        },
      },
    },
    avatar: {
      title: 'settings-osc-vmc-avatar_group',
      rows: {
        vrm: {
          title: 'settings-osc-vmc-vrm',
          description: 'settings-osc-vmc-vrm-description',
        },
        anchorHip: {
          title: 'settings-osc-vmc-anchor_hip',
          description: 'settings-osc-vmc-anchor_hip-description',
        },
        mirrorTracking: {
          title: 'settings-osc-vmc-mirror_tracking',
          description: 'settings-osc-vmc-mirror_tracking-description',
        },
      },
    },
    network: {
      title: 'settings-osc-vmc-network_group',
      rows: {
        network: {
          title: 'settings-osc-vmc-network',
        },
        address: {
          title: 'settings-osc-vmc-network-address',
          description: 'settings-osc-vmc-network-address-description',
        },
      },
    },
  },
});

const { groups } = vmcSettingsPage;

export function VMCSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const { oscValidator } = useOscPortsAddressValidator();
  const defaults = useSettingsDefaults().vmcosc;
  const factoryForm = toForm(defaults);
  const [status, setStatus] = useState<VMCOSCStatusChangeResponseT | null>(
    null
  );

  const { reset, control, watch, handleSubmit, setValue } =
    useForm<VMCSettingsForm>({
      defaultValues: factoryForm,
      reValidateMode: 'onChange',
      mode: 'onChange',
      resolver: yupResolver(
        object({
          enabled: boolean().required(),
          portsAddress: oscValidator,
          anchorHip: boolean().required(),
          mirrorTracking: boolean().required(),
        })
      ),
    });

  const onSubmit = async (values: VMCSettingsForm) => {
    const req = new ChangeVMCOSCSettingsRequestT();

    req.enabled = values.enabled;
    req.portIn = values.portsAddress.portIn;
    req.portOut = values.portsAddress.portOut;
    req.address = values.portsAddress.address;
    req.anchorHip = values.anchorHip;
    req.mirrorTracking = values.mirrorTracking;

    sendRPCPacket(RpcMessage.ChangeVMCOSCSettingsRequest, req);
  };

  const enabled = watch('enabled');
  const resetTo = <P extends FieldPath<VMCSettingsForm>>(
    name: P,
    value: FieldPathValue<VMCSettingsForm, P> | undefined
  ) =>
    settingReset<VMCSettingsForm, P>(value, watch(name), (next) => {
      setValue(name, next, { shouldDirty: true });
      handleSubmit(onSubmit)();
    });

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
    sendRPCPacket(RpcMessage.VMCOSCStatusRequest, new VMCOSCStatusRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.VMCOSCStatusChangeResponse,
    (response: VMCOSCStatusChangeResponseT) => {
      setStatus(response);
    }
  );

  useRPCPacket(
    RpcMessage.VMCOSCSettingsResponse,
    (settings: VMCOSCSettingsResponseT) => {
      reset(toForm(settings, factoryForm));
    }
  );

  return (
    <form className="w-full">
      <SettingsPagePaneLayout icon={<VMCIcon />} title={vmcSettingsPage.title}>
        <div className="flex flex-col">
          <div className="flex flex-col pb-4 max-w-[75ch]">
            {l10n
              .getString('settings-osc-vmc-description')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </div>

          <SettingsGroup group={groups.output}>
            <SettingRow
              setting={groups.output.rows.enable}
              controlId="vmc-enabled"
              {...resetTo('enabled', defaults.enabled)}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="enabled"
                  id="vmc-enabled"
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
                <SettingsGroupLabel id="settings-osc-vmc-status-title" />
              </div>
              <StatusCard status={status} />
            </div>
          )}

          <SettingsGroup group={groups.avatar}>
            <SettingRow
              setting={groups.avatar.rows.vrm}
              below={<VRMFileUpload />}
            />
            <SettingRow
              setting={groups.avatar.rows.anchorHip}
              controlId="vmc-anchor-hip"
              {...resetTo('anchorHip', defaults.anchorHip)}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="anchorHip"
                  id="vmc-anchor-hip"
                />
              }
            />
            <SettingRow
              setting={groups.avatar.rows.mirrorTracking}
              controlId="vmc-mirror"
              {...resetTo('mirrorTracking', defaults.mirrorTracking)}
              control={
                <CheckBox
                  variant="toggle"
                  control={control}
                  name="mirrorTracking"
                  id="vmc-mirror"
                />
              }
            />
          </SettingsGroup>

          <SettingsAdvanced>
            <SettingsGroup group={groups.network}>
              <SettingRow
                setting={groups.network.rows.network}
                {...resetPorts}
                details={
                  <>
                    {l10n
                      .getString('settings-osc-vmc-network-description')
                      .split('\n')
                      .map((line, i) => (
                        <Typography key={i}>{line}</Typography>
                      ))}
                  </>
                }
                below={
                  <div className="grid sm:grid-cols-2 gap-3">
                    <Localized
                      id="settings-osc-vmc-network-port_in"
                      attrs={{ placeholder: true, label: true }}
                    >
                      <Input
                        type="number"
                        control={control}
                        name="portsAddress.portIn"
                        placeholder="9002"
                        label=""
                      />
                    </Localized>
                    <Localized
                      id="settings-osc-vmc-network-port_out"
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
                {...resetTo(
                  'portsAddress.address',
                  defaults.address?.toString()
                )}
                below={
                  <Input
                    type="text"
                    control={control}
                    name="portsAddress.address"
                    placeholder={l10n.getString(
                      'settings-osc-vmc-network-address-placeholder'
                    )}
                    label=""
                  />
                }
              />
            </SettingsGroup>
          </SettingsAdvanced>
        </div>
      </SettingsPagePaneLayout>
    </form>
  );
}

const gltfHeaderStart = 0;
const gltfHeaderEnd = 20;

async function parseVRMFile(
  vrm: File
): Promise<{ json: string; name: string } | null> {
  const headerView = new DataView(
    await vrm.slice(gltfHeaderStart, gltfHeaderEnd).arrayBuffer()
  );
  let cursor = 0;
  const magicBytes = headerView.getUint32(cursor, true);
  if (magicBytes !== magic`glTF`) {
    error(
      `.vrm file starts with ${magicBytes.toString(
        16
      )} instead of ${magic`glTF`.toString(16)}`
    );
    return null;
  }
  cursor += 4;

  const versionNumber = headerView.getUint32(cursor, true);
  if (versionNumber !== 2) {
    error('unsupported glTF version');
    return null;
  }
  cursor += 4;

  // const fileLength = headerView.getUint32(8, true);
  cursor += 4;

  const jsonLength = headerView.getUint32(cursor, true);
  cursor += 4;
  const jsonMagicBytes = headerView.getUint32(cursor, true);
  if (jsonMagicBytes !== magic`JSON`) {
    error(
      `first chunk contains ${jsonMagicBytes.toString(
        16
      )} instead of ${magic`JSON`.toString(16)}`
    );
    return null;
  }

  const json = await vrm
    .slice(gltfHeaderEnd, gltfHeaderEnd + jsonLength, 'application/json')
    .text();

  let data: any;
  try {
    data = JSON.parse(json);
  } catch (e) {
    error('Failed to parse VRM glTF header: ' + e);
    return null;
  }

  const name = getVRMName(data);
  if (name === null) return null;

  // Only keep the fields we care about
  /* eslint-disable camelcase */
  const vrmJson = {
    extensions: {
      VRM: data.extensions.VRM,
      VRMC_vrm: data.extensions.VRMC_vrm,
    },
    extensionsUsed: data.extensionsUsed,
    nodes: data.nodes,
  };
  /* eslint-enable camelcase */

  return { json: JSON.stringify(vrmJson), name };
}

function getVRMName(data: any): string | null {
  try {
    if (typeof data?.extensions?.VRMC_vrm?.specVersion === 'string') {
      const name = data.extensions.VRMC_vrm.meta.name;

      if (typeof name !== 'string') {
        error(
          `The name of the VRM model is not a string, instead it is a ${typeof name}`
        );
        return null;
      }

      return name;
    } else {
      return data?.extensions?.VRM?.meta?.title || '';
    }
  } catch (e) {
    error('Failed to fetch VRM name: ' + e);
    return null;
  }
}
