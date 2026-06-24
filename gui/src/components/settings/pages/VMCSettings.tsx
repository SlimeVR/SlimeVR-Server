import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  ChangeVMCOSCSettingsRequestT,
  ChangeVRMSettingsRequestT,
  RpcMessage,
  VMCOSCSettingsRequestT,
  VMCOSCSettingsResponseT,
  VRMSettingsRequestT,
  VRMSettingsResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { FileInput } from '@/components/commons/FileInput';
import { VMCIcon } from '@/components/commons/icon/VMCIcon';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import { magic } from '@/utils/formatting';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { error } from '@/utils/logging';
import {
  OSCPortsAddress,
  useOscPortsAddressValidator,
} from '@/hooks/osc-setting-validator';
import { yupResolver } from '@hookform/resolvers/yup';
import { boolean, object } from 'yup';

interface VMCSettingsForm {
  enabled: boolean,
  portsAddress: OSCPortsAddress,
  anchorHip: boolean;
  mirrorTracking: boolean;
}

const defaultVMCSettings: VMCSettingsForm = {
  enabled: false,
  portsAddress: {
    portIn: 39540,
    portOut: 39539,
    address: '127.0.0.1',
  },
  anchorHip: true,
  mirrorTracking: true,
};

export function VRMFileUpload() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { l10n } = useLocalization();
  const [modelName, setModelName] = useState<string | null>(null);

  const { control, watch } = useForm<{
    vrmJson?: FileList;
  }>({
    defaultValues: { vrmJson: undefined },
    reValidateMode: 'onChange',
    mode: 'onChange',
  });

  const vrmJson = watch('vrmJson');

  const updateVRMJson = async () => {
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
    updateVRMJson();
  }, [vrmJson]);

  useEffect(() => {
    sendRPCPacket(RpcMessage.VMCOSCSettingsRequest, new VMCOSCSettingsRequestT());
    sendRPCPacket(RpcMessage.VRMSettingsRequest, new VRMSettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.VRMSettingsResponse, (settings: VRMSettingsResponseT) => {
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
  });

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

export function VMCSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { oscValidator } = useOscPortsAddressValidator();

  const { reset, control, watch, handleSubmit } = useForm<VMCSettingsForm>({
    defaultValues: defaultVMCSettings,
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

    req.enabled = values.enabled
    req.portIn = values.portsAddress.portIn
    req.portOut = values.portsAddress.portOut
    req.address = values.portsAddress.address
    req.anchorHip = values.anchorHip;
    req.mirrorTracking = values.mirrorTracking;

    sendRPCPacket(RpcMessage.ChangeVMCOSCSettingsRequest, req);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useRPCPacket(RpcMessage.VMCOSCSettingsResponse, (settings: VMCOSCSettingsResponseT) => {
    const formData: VMCSettingsForm = defaultVMCSettings;
    if (settings) {
      formData.enabled = settings.enabled;
      if (settings.portIn)
        formData.portsAddress.portIn = settings.portIn;
      if (settings.portOut)
        formData.portsAddress.portOut = settings.portOut;
      if (settings.address)
        formData.portsAddress.address = settings.address.toString();

      formData.anchorHip = settings.anchorHip;
      formData.mirrorTracking = settings.mirrorTracking;

      reset(formData);
    }
  });

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<VMCIcon />} id="vmc">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-osc-vmc')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              <>
                {l10n
                  .getString('settings-osc-vmc-description')
                  .split('\n')
                  .map((line, i) => (
                    <Typography key={i}>{line}</Typography>
                  ))}
              </>
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-vmc-enable')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-vmc-enable-description')}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="enabled"
                label={l10n.getString('settings-osc-vmc-enable-label')}
              />
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-vmc-network')}
            </Typography>
            <div className="flex flex-col pb-2">
              <>
                {l10n
                  .getString('settings-osc-vmc-network-description')
                  .split('\n')
                  .map((line, i) => (
                    <Typography key={i}>{line}</Typography>
                  ))}
              </>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
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
            <Typography variant="section-title">
              {l10n.getString('settings-osc-vmc-network-address')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-vmc-network-address-description')}
              </Typography>
            </div>
            <div className="grid gap-3 pb-5">
              <Input
                type="text"
                control={control}
                name="portsAddress.address"
                placeholder={l10n.getString(
                  'settings-osc-vmc-network-address-placeholder'
                )}
                label=""
              />
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-vmc-vrm')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-vmc-vrm-description')}
              </Typography>
            </div>
            <div className="grid gap-3 pb-5">
              <VRMFileUpload />
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-vmc-anchor_hip')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-vmc-anchor_hip-description')}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="anchorHip"
                label={l10n.getString('settings-osc-vmc-anchor_hip-label')}
              />
            </div>
            <Typography variant="section-title">
              {l10n.getString('settings-osc-vmc-mirror_tracking')}
            </Typography>
            <div className="flex flex-col pb-2">
              <Typography>
                {l10n.getString('settings-osc-vmc-mirror_tracking-description')}
              </Typography>
            </div>
            <div className="grid grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="mirrorTracking"
                label={l10n.getString('settings-osc-vmc-mirror_tracking-label')}
              />
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
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
