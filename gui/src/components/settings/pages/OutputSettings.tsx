import { Localized, useLocalization } from '@fluent/react';
import {useEffect, useRef, useState} from 'react';
import { useForm } from 'react-hook-form';
import {
  BodyPart,
  ChangeOutputTrackersSettingsRequestT, OutputTrackersSettingsResponseT,
  RpcMessage, SettingsResponseT, TapDetectionSettingsT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { FileInput } from '@/components/commons/FileInput';
import { OutputTrackersIcon } from '@/components/commons/icon/OutputTrackersIcon';
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
import {atom, useAtomValue, useSetAtom} from "jotai";
import {selectAtom} from "jotai/utils";
import {isEqual} from "@react-hookz/deep-equal";

interface OutputTrackersSettingsForm {
  automaticTrackerToggle: boolean;
  waist: boolean;
  chest: boolean;
  leftFoot: boolean;
  rightFoot: boolean;
  leftKnee: boolean;
  rightKnee: boolean;
  leftElbow: boolean;
  rightElbow: boolean;
  leftHand: boolean;
  rightHand: boolean;
  sendDerivedVelocity: boolean;
}

const defaultOutputTrackersSettings: OutputTrackersSettingsForm = {
  automaticTrackerToggle: true,
  waist: false,
  chest: false,
  leftFoot: false,
  rightFoot: false,
  leftElbow: false,
  rightElbow: false,
  leftHand: false,
  rightHand: false,
  leftKnee: false,
  rightKnee: false,
  sendDerivedVelocity: false,
};

const settingsAtom = atom(new OutputTrackersSettingsResponseT());
const settingsValueAtom = selectAtom(
    settingsAtom,
    (settings) => settings,
    isEqual
);

export function OutputTrackersSettings() {
  const setSettings = useSetAtom(settingsAtom);
  const settings = useAtomValue(settingsValueAtom);
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { reset, control, watch, handleSubmit } = useForm<OutputTrackersSettingsForm>({
    defaultValues: defaultOutputTrackersSettings,
    reValidateMode: 'onChange',
    mode: 'onChange',
    resolver: yupResolver(
        object({
          automaticTrackerToggle: boolean().required(),
          waist: boolean().required(),
          chest: boolean().required(),
          leftFoot: boolean().required(),
          rightFoot: boolean().required(),
          leftKnee: boolean().required(),
          rightKnee: boolean().required(),
          leftElbow: boolean().required(),
          rightElbow: boolean().required(),
          leftHand: boolean().required(),
          rightHand: boolean().required(),
          sendDerivedVelocity: boolean().required(),
        })
    ),
  });

  const blockHandsWarning = useRef(false);
  // If not null, warning will be shown, and showHandsWarning will
  // hold which hands should be toggled ([leftHand, rightHand])
  const [showHandsWarning, setShowHandsWarning] = useState<
      [boolean, boolean] | null
  >(null);

  const {
    trackers: { automaticTrackerToggle },
  } = watch();

  const onSubmit = async (values: OutputTrackersSettingsForm) => {
    const req = new ChangeOutputTrackersSettingsRequestT();

    // The tracker is at the tail of the bone.
    // Example, the waist tracker is at the tail of the hip bone so we use the hip BodyPart
    const enabledBodyParts: [boolean, BodyPart][] = [
      [values.waist, BodyPart.HIP],
      [values.chest, BodyPart.CHEST],
      [values.leftFoot, BodyPart.LEFT_FOOT],
      [values.rightFoot, BodyPart.RIGHT_FOOT],
      [values.leftKnee, BodyPart.LEFT_UPPER_LEG],
      [values.rightKnee, BodyPart.RIGHT_UPPER_LEG],
      [values.leftElbow, BodyPart.LEFT_UPPER_ARM],
      [values.rightElbow, BodyPart.RIGHT_UPPER_ARM],
      [values.leftHand, BodyPart.LEFT_HAND],
      [values.rightHand, BodyPart.RIGHT_HAND],
    ];
    req.trackers = enabledBodyParts
        .filter(([enabled]) => enabled)
        .map(([, part]) => part);

    // Hand warning
    if (
        !blockHandsWarning.current &&
        !showHandsWarning &&
        !settings?.trackers.includes(BodyPart.LEFT_HAND) &&
        !settings?.trackers.includes(BodyPart.RIGHT_HAND) &&
        (values.leftHand || values.rightHand)
    ) {
      // We have just toggled on one of the hand trackers, show the user a warning
      setShowHandsWarning([values.leftHand, values.rightHand]);
      values.leftHand = false;
      values.rightHand = false;
    } else if (
        blockHandsWarning.current &&
        !values.leftHand && !values.rightHand
    ) {
      // Both hand trackers have just been disabled, make sure the warning shows up
      // again next time the user toggles one back on
      blockHandsWarning.current = false;
    }

    req.automaticTrackerToggle = values.automaticTrackerToggle;
    req.sendDerivedVelocity = values.sendDerivedVelocity;

    sendRPCPacket(RpcMessage.ChangeOutputTrackersSettingsRequest, req);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useRPCPacket(RpcMessage.OutputTrackersSettingsResponse, (settings: OutputTrackersSettingsResponseT) => {
    const formData: OutputTrackersSettingsForm = defaultOutputTrackersSettings;
    formData.automaticTrackerToggle = settings.automaticTrackerToggle;
    formData.sendDerivedVelocity = settings.sendDerivedVelocity;

    reset(formData);
  });

  return (
      <SettingsPageLayout>
        <form className="flex flex-col gap-2 w-full">
          <SettingsPagePaneLayout icon={<OutputTrackersIcon />} id="vmc">
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
                <OutputTrackersFileUpload />
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
