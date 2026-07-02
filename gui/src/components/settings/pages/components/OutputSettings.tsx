import { useLocalization } from '@fluent/react';
import { useEffect, useRef, useState } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  BodyPart,
  ChangeOutputTrackersSettingsRequestT,
  OutputTrackersSettingsRequestT,
  OutputTrackersSettingsResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPageLayout';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { HandsWarningModal } from './HandsWarningModal';
import {OutputIcon} from "@/components/commons/icon/OutputIcon";

type OutputTrackersForm = {
  automaticTrackerToggle: boolean;
  trackers: {
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
  };
  sendDerivedVelocity: boolean;
};

const defaultValues: OutputTrackersForm = {
  automaticTrackerToggle: true,
  trackers: {
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
  },
  sendDerivedVelocity: false,
};

// The tracker is at the tail of the bone.
// Example, the waist tracker is at the tail of the hip bone so we use the hip BodyPart
function trackersToBodyPartList(
  trackers: OutputTrackersForm['trackers']
): BodyPart[] {
  const enabledBodyParts: [boolean, BodyPart][] = [
    [trackers.waist, BodyPart.HIP],
    [trackers.chest, BodyPart.CHEST],
    [trackers.leftFoot, BodyPart.LEFT_FOOT],
    [trackers.rightFoot, BodyPart.RIGHT_FOOT],
    [trackers.leftKnee, BodyPart.LEFT_UPPER_LEG],
    [trackers.rightKnee, BodyPart.RIGHT_UPPER_LEG],
    [trackers.leftElbow, BodyPart.LEFT_UPPER_ARM],
    [trackers.rightElbow, BodyPart.RIGHT_UPPER_ARM],
    [trackers.leftHand, BodyPart.LEFT_HAND],
    [trackers.rightHand, BodyPart.RIGHT_HAND],
  ];

  return enabledBodyParts
    .filter(([enabled]) => enabled)
    .map(([, part]) => part);
}

function bodyPartListToTrackers(
  bodyParts: BodyPart[]
): Omit<OutputTrackersForm['trackers'], 'automaticTrackerToggle'> {
  const set = new Set(bodyParts);
  return {
    waist: set.has(BodyPart.HIP),
    chest: set.has(BodyPart.CHEST),
    leftFoot: set.has(BodyPart.LEFT_FOOT),
    rightFoot: set.has(BodyPart.RIGHT_FOOT),
    leftKnee: set.has(BodyPart.LEFT_UPPER_LEG),
    rightKnee: set.has(BodyPart.RIGHT_UPPER_LEG),
    leftElbow: set.has(BodyPart.LEFT_UPPER_ARM),
    rightElbow: set.has(BodyPart.RIGHT_UPPER_ARM),
    leftHand: set.has(BodyPart.LEFT_HAND),
    rightHand: set.has(BodyPart.RIGHT_HAND),
  };
}

const outputTrackersSettingsAtom = atom(new OutputTrackersSettingsResponseT());
const outputTrackersSettingsValueAtom = selectAtom(
  outputTrackersSettingsAtom,
  (settings) => settings,
  isEqual
);

export function OutputTrackersSettings() {
  const setSettings = useSetAtom(outputTrackersSettingsAtom);
  const settings = useAtomValue(outputTrackersSettingsValueAtom);
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const blockHandsWarning = useRef(false);

  // If not null, warning will be shown, and showHandsWarning will
  // hold which hands should be toggled ([leftHand, rightHand])
  const [showHandsWarning, setShowHandsWarning] = useState<
    [boolean, boolean] | null
  >(null);

  const { control, watch, handleSubmit, getValues, setValue, reset } =
    useForm<OutputTrackersForm>({
      defaultValues,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });

  const { automaticTrackerToggle } = watch();

  const onSubmit = (values: OutputTrackersForm) => {
    const settingsReq = new ChangeOutputTrackersSettingsRequestT();

    let leftHand = values.trackers.leftHand;
    let rightHand = values.trackers.rightHand;

    const hadHandTracker =
      settings.trackers?.includes(BodyPart.LEFT_HAND) ||
      settings.trackers?.includes(BodyPart.RIGHT_HAND);

    if (
      !blockHandsWarning.current &&
      !showHandsWarning &&
      !hadHandTracker &&
      (leftHand || rightHand)
    ) {
      setShowHandsWarning([leftHand, rightHand]);
      leftHand = false;
      rightHand = false;
    } else if (blockHandsWarning.current && !leftHand && !rightHand) {
      blockHandsWarning.current = false;
    }

    settingsReq.trackers = trackersToBodyPartList({
      ...values.trackers,
      leftHand,
      rightHand,
    });
    settingsReq.automaticTrackerToggle = values.automaticTrackerToggle;
    settingsReq.sendDerivedVelocity = values.sendDerivedVelocity;

    sendRPCPacket(RpcMessage.ChangeOutputTrackersSettingsRequest, settingsReq);
  };

  useEffect(() => {
    const subscription = watch((_, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.OutputTrackersSettingsRequest,
      new OutputTrackersSettingsRequestT()
    );
  }, []);

  useEffect(() => {
    let formData: DefaultValues<OutputTrackersForm> = {};

    if (settings.trackers) {
      formData = {
        ...bodyPartListToTrackers(settings.trackers),
        automaticTrackerToggle: settings.automaticTrackerToggle,
        sendDerivedVelocity: settings.sendDerivedVelocity,
      };
      if (
        !blockHandsWarning.current &&
        (settings.trackers.includes(BodyPart.LEFT_HAND) ||
          settings.trackers.includes(BodyPart.RIGHT_HAND))
      ) {
        blockHandsWarning.current = true;
      }
    }

    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(
    RpcMessage.OutputTrackersSettingsResponse,
    (settings: OutputTrackersSettingsResponseT) => {
      setSettings(settings);
    }
  );

  return (
    <>
      <HandsWarningModal
        isOpen={!!showHandsWarning}
        onClose={() => {
          setValue('trackers.leftHand', false);
          setValue('trackers.rightHand', false);
          setShowHandsWarning(null);
        }}
        accept={() => {
          const [leftHand, rightHand] = showHandsWarning!;
          blockHandsWarning.current = true;
          setValue('trackers.leftHand', leftHand);
          setValue('trackers.rightHand', rightHand);
          setShowHandsWarning(null);
        }}
      />
      <SettingsPagePaneLayout icon={<OutputIcon />} id="output">
        <>
          <Typography variant="main-title">
            {l10n.getString('settings-general-output')}
            <div className="flex flex-col pb-3" />

            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-steamvr-trackers-tracker_toggling'
              )}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              {l10n
                .getString(
                  'settings-general-output-trackers-tracker_toggling-description'
                )
                .split('\n')
                .map((line, i) => (
                  <Typography key={i}>{line}</Typography>
                ))}
            </div>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="automaticTrackerToggle"
              label={l10n.getString(
                'settings-general-steamvr-trackers-tracker_toggling-label'
              )}
            />
          </Typography>

          <div className="flex flex-col pt-6" />
          <Typography variant="section-title">
            {l10n.getString('settings-general-output_trackers')}
          </Typography>
          <div className="flex flex-col pt-1 pb-2">
            {l10n
              .getString('settings-general-output_trackers-description')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.chest"
              label={l10n.getString('settings-general-steamvr-trackers-chest')}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.waist"
              label={l10n.getString('settings-general-steamvr-trackers-waist')}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.leftKnee"
              label={l10n.getString(
                'settings-general-steamvr-trackers-left_knee'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.rightKnee"
              label={l10n.getString(
                'settings-general-steamvr-trackers-right_knee'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.leftFoot"
              label={l10n.getString(
                'settings-general-steamvr-trackers-left_foot'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.rightFoot"
              label={l10n.getString(
                'settings-general-steamvr-trackers-right_foot'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.leftElbow"
              label={l10n.getString(
                'settings-general-steamvr-trackers-left_elbow'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              disabled={automaticTrackerToggle}
              control={control}
              name="trackers.rightElbow"
              label={l10n.getString(
                'settings-general-steamvr-trackers-right_elbow'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.leftHand"
              label={l10n.getString(
                'settings-general-steamvr-trackers-left_hand'
              )}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.rightHand"
              label={l10n.getString(
                'settings-general-steamvr-trackers-right_hand'
              )}
            />
          </div>

          <div className="flex flex-col pt-6" />
          <Typography variant="section-title">
            {l10n.getString('settings-general-fk_settings-velocity_settings')}
          </Typography>
          <div className="pt-1">
            <Typography>
              {l10n.getString(
                'settings-general-fk_settings-velocity_settings-description'
              )}
            </Typography>
          </div>
          <div className="grid sm:grid-cols-1 pt-2">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="sendDerivedVelocity"
              label={l10n.getString(
                'settings-general-fk_settings-velocity_settings-send_derived_velocity'
              )}
            />
          </div>
        </>
      </SettingsPagePaneLayout>
    </>
  );
}
