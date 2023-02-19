import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { useLocation } from 'react-router-dom';
import {
  ChangeSettingsRequestT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  OSCSettingsT,
  VMCOSCSettingsT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { CheckBox } from '../../commons/Checkbox';
import { FileInput, FileValue } from '../../commons/FileInput';
import { VMCIcon } from '../../commons/icon/VMCIcon';
import { Input } from '../../commons/Input';
import { Typography } from '../../commons/Typography';
import { magic } from '../../utils/formatting';
import { SettingsPageLayout } from '../SettingsPageLayout';

interface VMCSettingsForm {
  vmc: {
    oscSettings: {
      enabled: boolean;
      portIn: number;
      portOut: number;
      address: string;
    };
    vrmJson?: FileValue;
    anchorHip: boolean;
  };
}

const defaultValues = {
  vmc: {
    oscSettings: {
      enabled: false,
      portIn: 39540,
      portOut: 39539,
      address: '127.0.0.1',
    },
    anchorHip: true,
  },
};

export function VMCSettings() {
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { state } = useLocation();
  const pageRef = useRef<HTMLFormElement | null>(null);

  const { reset, control, watch, handleSubmit, register } =
    useForm<VMCSettingsForm>({
      defaultValues: defaultValues,
    });

  const onSubmit = async (values: VMCSettingsForm) => {
    const settings = new ChangeSettingsRequestT();

    if (values.vmc) {
      const vmcOsc = new VMCOSCSettingsT();

      vmcOsc.oscSettings = Object.assign(
        new OSCSettingsT(),
        values.vmc.oscSettings
      );
      if (values.vmc.vrmJson?.files.length) {
        vmcOsc.vrmJson = await parseVRMFile(values.vmc.vrmJson.files[0]);
        reset({
          vmc: {
            vrmJson: undefined,
          },
        });
      }
      vmcOsc.anchorHip = values.vmc.anchorHip;

      settings.vmcOsc = vmcOsc;
    }
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    const formData: VMCSettingsForm = defaultValues;
    if (settings.vmcOsc) {
      if (settings.vmcOsc.oscSettings) {
        formData.vmc.oscSettings.enabled = settings.vmcOsc.oscSettings.enabled;
        if (settings.vmcOsc.oscSettings.portIn)
          formData.vmc.oscSettings.portIn = settings.vmcOsc.oscSettings.portIn;
        if (settings.vmcOsc.oscSettings.portOut)
          formData.vmc.oscSettings.portOut =
            settings.vmcOsc.oscSettings.portOut;
        if (settings.vmcOsc.oscSettings.address)
          formData.vmc.oscSettings.address =
            settings.vmcOsc.oscSettings.address.toString();
      }
      formData.vmc.anchorHip = settings.vmcOsc.anchorHip;
    }

    reset(formData);
  });

  // Handle scrolling to selected page
  useEffect(() => {
    const typedState: { scrollTo: string } = state as any;
    if (!pageRef.current || !typedState || !typedState.scrollTo) {
      return;
    }
    const elem = pageRef.current.querySelector(`#${typedState.scrollTo}`);
    if (elem) {
      elem.scrollIntoView({ behavior: 'smooth' });
    }
  }, [state]);

  return (
    <form className="flex flex-col gap-2 w-full" ref={pageRef}>
      <SettingsPageLayout icon={<VMCIcon></VMCIcon>} id="vmc">
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
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </>
          </div>
          <Typography bold>
            {l10n.getString('settings-osc-vmc-enable')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {l10n.getString('settings-osc-vmc-enable-description')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vmc.oscSettings.enabled"
              label={l10n.getString('settings-osc-vmc-enable-label')}
            />
          </div>
          <Typography bold>
            {l10n.getString('settings-osc-vmc-network')}
          </Typography>
          <div className="flex flex-col pb-2">
            <>
              {l10n
                .getString('settings-osc-vmc-network-description')
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
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
                name="vmc.oscSettings.portIn"
                rules={{ required: true }}
                placeholder="9002"
                label=""
              ></Input>
            </Localized>
            <Localized
              id="settings-osc-vmc-network-port_out"
              attrs={{ placeholder: true, label: true }}
            >
              <Input
                type="number"
                control={control}
                name="vmc.oscSettings.portOut"
                rules={{ required: true }}
                placeholder="9000"
                label=""
              ></Input>
            </Localized>
          </div>
          <Typography bold>
            {l10n.getString('settings-osc-vmc-network-address')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {l10n.getString('settings-osc-vmc-network-address-description')}
            </Typography>
          </div>
          <div className="grid gap-3 pb-5">
            <Input
              type="text"
              control={control}
              name="vmc.oscSettings.address"
              rules={{
                required: true,
                pattern:
                  /^(?!0)(?!.*\.$)((1?\d?\d|25[0-5]|2[0-4]\d)(\.|$)){4}$/i,
              }}
              placeholder={l10n.getString(
                'settings-osc-vmc-network-address-placeholder'
              )}
              label=""
            ></Input>
          </div>
          <Typography bold>{l10n.getString('settings-osc-vmc-vrm')}</Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {l10n.getString('settings-osc-vmc-vrm-description')}
            </Typography>
          </div>
          <div className="grid gap-3 pb-5">
            <FileInput
              control={control}
              name="vmc.vrmJson"
              rules={{
                required: false,
              }}
              value="help"
              placeholder={l10n.getString('settings-osc-vmc-vrm-placeholder')}
              label=""
              accept="model/gltf-binary, model/gltf+json, model/vrml, .vrm"
            ></FileInput>
            {/* For some reason, linux (GNOME) is detecting the VRM file is a VRML */}
          </div>
          <Typography bold>
            {l10n.getString('settings-osc-vmc-anchor_hip')}
          </Typography>
          <div className="flex flex-col pb-2">
            <Typography color="secondary">
              {l10n.getString('settings-osc-vmc-anchor_hip-description')}
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="vmc.anchorHip"
              label={l10n.getString('settings-osc-vmc-anchor_hip-label')}
            />
          </div>
        </>
      </SettingsPageLayout>
    </form>
  );
}

const gltfHeaderStart = 0;
const gltfHeaderEnd = 20;
async function parseVRMFile(vrm: File): Promise<string | null> {
  const headerView = new DataView(
    await vrm.slice(gltfHeaderStart, gltfHeaderEnd).arrayBuffer()
  );
  let cursor = 0;
  const magicBytes = headerView.getUint32(cursor, true);
  if (magicBytes !== magic`glTF`) {
    console.error(
      `.vrm file starts with ${magicBytes.toString(
        16
      )} instead of ${magic`glTF`.toString(16)}`
    );
    return null;
  }
  cursor += 4;

  const versionNumber = headerView.getUint32(cursor, true);
  if (versionNumber !== 2) {
    console.error('unsupported glTF version');
    return null;
  }
  cursor += 4;

  // const fileLength = headerView.getUint32(8, true);
  cursor += 4;

  const jsonLength = headerView.getUint32(cursor, true);
  cursor += 4;
  const jsonMagicBytes = headerView.getUint32(cursor, true);
  if (jsonMagicBytes !== magic`JSON`) {
    console.error(
      `first chunk contains ${jsonMagicBytes.toString(
        16
      )} instead of ${magic`JSON`.toString(16)}`
    );
    return null;
  }

  return vrm
    .slice(gltfHeaderEnd, gltfHeaderEnd + jsonLength, 'application/json')
    .text();
}
