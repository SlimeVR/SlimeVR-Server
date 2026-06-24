import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useRef, useState } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  ChangeSettingsRequestT,
  FilteringType,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  TapDetectionSettingsT,
  BodyPart,
  OutputTrackersSettingT,
} from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox } from '@/components/commons/Checkbox';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { HandsWarningModal } from '@/components/settings/HandsWarningModal';
import { MagnetometerToggleSetting } from './MagnetometerToggleSetting';
import {
  defaultStayAlignedSettings,
  StayAlignedSettings,
  StayAlignedSettingsForm,
  serializeStayAlignedSettings,
  deserializeStayAlignedSettings,
} from './components/StayAlignedSettings';
import {
  defaultResetSettings,
  loadResetSettings,
  ResetSettingsForm,
} from '@/hooks/reset-settings';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import { isEqual } from '@react-hookz/deep-equal';
import { selectAtom } from 'jotai/utils';
import { Dropdown } from '@/components/commons/Dropdown';
import { ASSIGNMENT_MODES } from '@/components/onboarding/BodyAssignment';
import { OutputIcon } from '@/components/commons/icon/OutputIcon';
import { TouchDoubleIcon } from '@/components/commons/icon/TouchDoubleIcon';
import { MartialArtsIcon } from '@/components/commons/icon/MartialArtsIcon';

export type SettingsForm = {
  filtering: {
    type: number;
    amount: number;
  };
  toggles: {
    forceArmsFromHmd: boolean;
    floorClip: boolean;
    skatingCorrection: boolean;
    toeSnap: boolean;
    footPlant: boolean;
    selfLocalization: boolean;
    usePosition: boolean;
    enforceConstraints: boolean;
    correctConstraints: boolean;
  };
  ratios: {
    imputeSpineFromUpperLower: number;
    imputeSpineCurvature: number;
    interpHipLegs: number;
    interpKneeTrackerAnkle: number;
    interpKneeAnkle: number;
  };
  tapDetection: {
    mountingResetEnabled: boolean;
    yawResetEnabled: boolean;
    fullResetEnabled: boolean;
    yawResetDelay: number;
    fullResetDelay: number;
    mountingResetDelay: number;
    yawResetTaps: number;
    fullResetTaps: number;
    mountingResetTaps: number;
    numberTrackersOverThreshold: number;
    yawResetTracker: string;
    mountingResetTracker: string;
    fullResetTracker: string;
  };
  legTweaks: {
    correctionStrength: number;
  };
  resetsSettings: ResetSettingsForm;
  stayAligned: StayAlignedSettingsForm;
  hidSettings: {
    trackersOverHID: boolean;
  };
};

const defaultValues: SettingsForm = {
  toggles: {
    forceArmsFromHmd: false,
    floorClip: false,
    skatingCorrection: false,
    toeSnap: false,
    footPlant: true,
    selfLocalization: false,
    usePosition: true,
    enforceConstraints: true,
    correctConstraints: true,
  },
  ratios: {
    imputeSpineFromUpperLower: 0.5,
    imputeSpineCurvature: 0.5,
    interpHipLegs: 0.25,
    interpKneeTrackerAnkle: 0.85,
    interpKneeAnkle: 0.2,
  },
  filtering: { amount: 0.1, type: FilteringType.NONE },
  tapDetection: {
    mountingResetEnabled: false,
    yawResetEnabled: false,
    fullResetEnabled: false,
    yawResetDelay: 0.2,
    fullResetDelay: 1.0,
    mountingResetDelay: 1.0,
    yawResetTaps: 2,
    fullResetTaps: 3,
    mountingResetTaps: 3,
    numberTrackersOverThreshold: 1,
    yawResetTracker: String(BodyPart.CHEST),
    mountingResetTracker: String(BodyPart.RIGHT_UPPER_LEG),
    fullResetTracker: String(BodyPart.LEFT_UPPER_LEG),
  },
  legTweaks: { correctionStrength: 0.3 },
  resetsSettings: defaultResetSettings,
  stayAligned: defaultStayAlignedSettings,
  hidSettings: { trackersOverHID: false },
};

const settingsAtom = atom(new SettingsResponseT());
const settingsValueAtom = selectAtom(
  settingsAtom,
  (settings) => settings,
  isEqual
);

export function GeneralSettings() {
  const setSettings = useSetAtom(settingsAtom);
  const settings = useAtomValue(settingsValueAtom);
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const { currentLocales } = useLocaleConfig();

  const bodyParts: { value: string; label: string }[] = Object.values(BodyPart)
    .filter((v): v is BodyPart => typeof v === 'number')
    .filter((v) => ASSIGNMENT_MODES['full-body'].includes(v as BodyPart))
    .map((value) => ({
      value: String(value),
      label: l10n.getString(`body_part-${BodyPart[value]}`),
    }));

  const percentageFormat = new Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 0,
  });
  const secondsFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  });

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { reset, control, watch, handleSubmit, getValues, setValue } =
    useForm<SettingsForm>({
      defaultValues,
      mode: 'onChange',
      reValidateMode: 'onChange',
    });



  const onSubmit = (values: SettingsForm) => {
    const req = new ChangeSettingsRequestT();

    const modelSettings = new ModelSettingsT();

    if (values.toggles) {
      const toggles = new ModelTogglesT();
      toggles.floorClip = values.toggles.floorClip;
      toggles.skatingCorrection = values.toggles.skatingCorrection;
      toggles.forceArmsFromHmd = values.toggles.forceArmsFromHmd;
      toggles.toeSnap = values.toggles.toeSnap;
      toggles.footPlant = values.toggles.footPlant;
      toggles.selfLocalization = values.toggles.selfLocalization;
      toggles.usePosition = values.toggles.usePosition;
      toggles.enforceConstraints = values.toggles.enforceConstraints;
      toggles.correctConstraints = values.toggles.correctConstraints;
      modelSettings.toggles = toggles;
    }

    if (values.ratios) {
      const ratios = new ModelRatiosT();
      ratios.imputeSpineFromUpperLower =
        values.ratios.imputeSpineFromUpperLower || -1;
      ratios.imputeSpineCurvature = values.ratios.imputeSpineCurvature || -1;
      ratios.interpHipLegs = values.ratios.interpHipLegs || -1;
      ratios.interpKneeTrackerAnkle =
        values.ratios.interpKneeTrackerAnkle || -1;
      ratios.interpKneeAnkle = values.ratios.interpKneeAnkle || -1;
      modelSettings.ratios = ratios;
    }

    if (values.legTweaks) {
      const legTweaks = new LegTweaksSettingsT();
      legTweaks.correctionStrength = values.legTweaks.correctionStrength;
      modelSettings.legTweaks = legTweaks;
    }

    req.modelSettings = modelSettings;

    const tapDetection = new TapDetectionSettingsT();
    tapDetection.fullResetDelay = values.tapDetection.fullResetDelay;
    tapDetection.fullResetEnabled = values.tapDetection.fullResetEnabled;
    tapDetection.fullResetTaps = values.tapDetection.fullResetTaps;
    tapDetection.yawResetDelay = values.tapDetection.yawResetDelay;
    tapDetection.yawResetEnabled = values.tapDetection.yawResetEnabled;
    tapDetection.yawResetTaps = values.tapDetection.yawResetTaps;
    tapDetection.yawResetTracker = Number(values.tapDetection.yawResetTracker);
    tapDetection.mountingResetTracker = Number(
      values.tapDetection.mountingResetTracker
    );
    tapDetection.fullResetTracker = Number(
      values.tapDetection.fullResetTracker
    );
    tapDetection.mountingResetEnabled =
      values.tapDetection.mountingResetEnabled;
    tapDetection.mountingResetDelay = values.tapDetection.mountingResetDelay;
    tapDetection.mountingResetTaps = values.tapDetection.mountingResetTaps;
    tapDetection.numberTrackersOverThreshold =
      values.tapDetection.numberTrackersOverThreshold;
    tapDetection.setupMode = false;
    req.tapDetectionSettings = tapDetection;

    const filtering = new FilteringSettingsT();
    filtering.type = values.filtering.type;
    filtering.amount = values.filtering.amount;
    req.filtering = filtering;

    req.stayAligned = serializeStayAlignedSettings(values.stayAligned);

    const hidSettings = new HIDSettingsT();
    hidSettings.trackersOverHid = values.hidSettings.trackersOverHID;
    req.hidSettings = hidSettings;

    const velocitySettings = new VelocitySettingsT();
    velocitySettings.sendDerivedVelocity =
      values.velocitySettings.sendDerivedVelocity;
    req.velocitySettings = velocitySettings;

    if (values.resetsSettings) {
      req.resetsSettings = loadResetSettings(values.resetsSettings);
    }

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, req);
  };

  useEffect(() => {
    const subscription = watch((value, { type }) => {
      if (type === 'change') handleSubmit(onSubmit)();
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useEffect(() => {
    const formData: DefaultValues<SettingsForm> = {};

    if (settings.filtering) {
      formData.filtering = settings.filtering;
    }

    if (settings.outputTrackers) {
      formData.trackers = settings.outputTrackers;
      if (
        !blockHandsWarning.current &&
        (settings.outputTrackers.trackers.includes(BodyPart.LEFT_HAND) ||
          settings.outputTrackers.trackers.includes(BodyPart.RIGHT_HAND))
      ) {
        blockHandsWarning.current = true;
      }
    }

    if (settings.modelSettings?.toggles) {
      formData.toggles = Object.keys(settings.modelSettings.toggles).reduce(
        (curr, key: string) => ({
          ...curr,
          [key]:
            (settings.modelSettings?.toggles &&
              (settings.modelSettings.toggles as any)[key]) ||
            false,
        }),
        {}
      );
    }

    if (settings.modelSettings?.ratios) {
      formData.ratios = Object.keys(settings.modelSettings.ratios).reduce(
        (curr, key: string) => ({
          ...curr,
          [key]:
            (settings.modelSettings?.ratios &&
              (settings.modelSettings.ratios as any)[key]) ||
            0.0,
        }),
        {}
      );
    }

    if (settings.tapDetectionSettings) {
      formData.tapDetection = {
        yawResetEnabled:
          settings.tapDetectionSettings.yawResetEnabled ||
          defaultValues.tapDetection.yawResetEnabled,
        fullResetEnabled:
          settings.tapDetectionSettings.fullResetEnabled ||
          defaultValues.tapDetection.fullResetEnabled,
        mountingResetEnabled:
          settings.tapDetectionSettings.mountingResetEnabled ||
          defaultValues.tapDetection.mountingResetEnabled,
        yawResetDelay:
          settings.tapDetectionSettings.yawResetDelay ||
          defaultValues.tapDetection.yawResetDelay,
        fullResetDelay:
          settings.tapDetectionSettings.fullResetDelay ||
          defaultValues.tapDetection.fullResetDelay,
        mountingResetDelay:
          settings.tapDetectionSettings.mountingResetDelay ||
          defaultValues.tapDetection.mountingResetDelay,
        yawResetTaps:
          settings.tapDetectionSettings.yawResetTaps ||
          defaultValues.tapDetection.yawResetTaps,
        fullResetTaps:
          settings.tapDetectionSettings.fullResetTaps ||
          defaultValues.tapDetection.fullResetTaps,
        mountingResetTaps:
          settings.tapDetectionSettings.mountingResetTaps ||
          defaultValues.tapDetection.mountingResetTaps,
        yawResetTracker: String(
          settings.tapDetectionSettings.yawResetTracker ||
            defaultValues.tapDetection.yawResetTracker
        ),
        fullResetTracker: String(
          settings.tapDetectionSettings.fullResetTracker ||
            defaultValues.tapDetection.fullResetTracker
        ),
        mountingResetTracker: String(
          settings.tapDetectionSettings.mountingResetTracker ||
            defaultValues.tapDetection.mountingResetTracker
        ),
        numberTrackersOverThreshold:
          settings.tapDetectionSettings.numberTrackersOverThreshold ||
          defaultValues.tapDetection.numberTrackersOverThreshold,
      };
    }

    if (settings.modelSettings?.legTweaks) {
      formData.legTweaks = {
        correctionStrength:
          settings.modelSettings.legTweaks.correctionStrength ||
          defaultValues.legTweaks.correctionStrength,
      };
    }

    if (settings.resetsSettings) {
      formData.resetsSettings = settings.resetsSettings;
    }

    if (settings.stayAligned) {
      formData.stayAligned = deserializeStayAlignedSettings(
        settings.stayAligned
      );
    }

    if (settings.hidSettings) {
      formData.hidSettings = {
        trackersOverHID: settings.hidSettings.trackersOverHid,
      };
    }

    if (settings.velocitySettings) {
      formData.velocitySettings = {
        sendDerivedVelocity: settings.velocitySettings.sendDerivedVelocity,
      };
    }

    reset({ ...getValues(), ...formData });
  }, [settings]);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    setSettings(settings);
  });

  return (
    <SettingsPageLayout>
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
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<OutputIcon />} id="output">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-output')}
            </Typography>

            <div className="flex flex-col pt-4" />
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-steamvr-trackers-tracker_toggling'
              )}
            </Typography>
            <div className="flex flex-col py-2">
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
              name="trackers.automaticTrackerToggle"
              label={l10n.getString(
                'settings-general-steamvr-trackers-tracker_toggling-label'
              )}
            />
            <div className="flex flex-col pt-4" />

            <Typography variant="section-title">
              {l10n.getString('settings-general-output_trackers')}
            </Typography>
            <div className="flex flex-col py-2">
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
                label={l10n.getString(
                  'settings-general-steamvr-trackers-chest'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                disabled={automaticTrackerToggle}
                control={control}
                name="trackers.waist"
                label={l10n.getString(
                  'settings-general-steamvr-trackers-waist'
                )}
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
            <div className="flex flex-col pt-4" />

            <div className="flex flex-col pt-2 pb-1">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-fk_settings-velocity_settings'
                )}
              </Typography>
              <div className="pt-2">
                <Typography>
                  {l10n.getString(
                    'settings-general-fk_settings-velocity_settings-description'
                  )}
                </Typography>
              </div>
            </div>
            <div className="grid sm:grid-cols-1 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="velocitySettings.sendDerivedVelocity"
                label={l10n.getString(
                  'settings-general-fk_settings-velocity_settings-send_derived_velocity'
                )}
              />
            </div>
          </>
        </SettingsPagePaneLayout>
        <StayAlignedSettings values={getValues()} control={control} />
        <SettingsPagePaneLayout icon={<WrenchIcon />} id="mechanics">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-tracker_mechanics')}
            </Typography>
            <Typography variant="section-title">
              {l10n.getString('settings-general-tracker_mechanics-filtering')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              {l10n
                .getString(
                  'settings-general-tracker_mechanics-filtering-description'
                )
                .split('\n')
                .map((line, i) => (
                  <Typography key={i}>{line}</Typography>
                ))}
            </div>
            <Typography>
              {l10n.getString(
                'settings-general-tracker_mechanics-filtering-type'
              )}
            </Typography>
            <div className="flex md:flex-row flex-col gap-3 pt-2">
              <Radio
                control={control}
                name="filtering.type"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-none'
                )}
                description={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-none-description'
                )}
                value={FilteringType.NONE.toString()}
              />
              <Radio
                control={control}
                name="filtering.type"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-smoothing'
                )}
                description={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-smoothing-description'
                )}
                value={FilteringType.SMOOTHING.toString()}
              />
              <Radio
                control={control}
                name="filtering.type"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-prediction'
                )}
                description={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-prediction-description'
                )}
                value={FilteringType.PREDICTION.toString()}
              />
            </div>
            <div className="flex gap-5 pt-5 md:flex-row flex-col">
              <NumberSelector
                control={control}
                name="filtering.amount"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-amount'
                )}
                valueLabelFormat={(value) => percentageFormat.format(value)}
                min={0.1}
                max={1.0}
                step={0.1}
              />
            </div>
            <div className="flex gap-5 pt-5 md:flex-row flex-col">
              <NumberSelector
                control={control}
                name="resetsSettings.yawResetSmoothTime"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-yaw-reset-smooth-time'
                )}
                valueLabelFormat={(value) => secondsFormat.format(value)}
                min={0.0}
                max={0.5}
                step={0.05}
              />
            </div>
            <div className="flex flex-col pt-5 pb-3">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-tracker_mechanics-save_mounting_reset'
                )}
              </Typography>
              <Localized
                id="settings-general-tracker_mechanics-save_mounting_reset-description"
                elems={{ b: <b /> }}
              >
                <Typography />
              </Localized>
            </div>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="resetsSettings.saveMountingReset"
              label={l10n.getString(
                'settings-general-tracker_mechanics-save_mounting_reset-enabled-label'
              )}
            />
            <MagnetometerToggleSetting
              settingType="general"
              id="mechanics-magnetometer"
            />
            <div className="flex flex-col pt-5 pb-3">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-tracker_mechanics-trackers_over_usb'
                )}
              </Typography>
              <Localized
                id="settings-general-tracker_mechanics-trackers_over_usb-description"
                elems={{ b: <b /> }}
              >
                <Typography />
              </Localized>
            </div>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="hidSettings.trackersOverHID"
              label={l10n.getString(
                'settings-general-tracker_mechanics-trackers_over_usb-enabled-label'
              )}
            />
          </>
        </SettingsPagePaneLayout>
        <SettingsPagePaneLayout icon={<MartialArtsIcon />} id="fksettings">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-fk_settings')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4 gap-2">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-skating_correction'
                )}
              </Typography>
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-skating_correction-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-2 pb-4">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.skatingCorrection"
                label={l10n.getString(
                  'settings-general-fk_settings-leg_tweak-skating_correction'
                )}
              />
              <NumberSelector
                control={control}
                name="legTweaks.correctionStrength"
                label={l10n.getString(
                  'settings-general-fk_settings-leg_tweak-skating_correction-amount'
                )}
                valueLabelFormat={(value) => percentageFormat.format(value)}
                min={0.1}
                max={1.0}
                step={0.1}
              />
            </div>

            <div className="flex flex-col pt-2 pb-2">
              <Typography variant="section-title">
                {l10n.getString('settings-general-fk_settings-leg_fk')}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-3 pb-3">
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-floor_clip-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-2 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.floorClip"
                label={l10n.getString(
                  'settings-general-fk_settings-leg_tweak-floor_clip'
                )}
              />
            </div>
            <div className="flex flex-col pt-2 pb-3">
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-foot_plant-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-3 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.footPlant"
                label={l10n.getString(
                  'settings-general-fk_settings-leg_tweak-foot_plant'
                )}
              />
            </div>
            <div className="flex flex-col pt-2 pb-3">
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-toe_snap-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-3 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.toeSnap"
                label={l10n.getString(
                  'settings-general-fk_settings-leg_tweak-toe_snap'
                )}
              />
            </div>

            <div className="flex flex-col pt-2 pb-3">
              <Typography variant="section-title">
                {l10n.getString('settings-general-fk_settings-arm_fk')}
              </Typography>
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-arm_fk-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.forceArmsFromHmd"
                label={l10n.getString(
                  'settings-general-fk_settings-arm_fk-force_arms'
                )}
              />
            </div>

            <div className="flex flex-col pt-2">
              <Typography variant="section-title">
                {l10n.getString('settings-general-fk_settings-reset_settings')}
              </Typography>
            </div>
            <div className="flex flex-col pt-2 pb-3">
              <div className="grid grid-cols-2 gap-2">
                <div className="flex flex-col gap-2">
                  <Typography>
                    {l10n.getString(
                      'settings-general-fk_settings-reset_settings-reset_hmd_pitch-description'
                    )}
                  </Typography>
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="resetsSettings.resetHmdPitch"
                    label={l10n.getString(
                      'settings-general-fk_settings-reset_settings-reset_hmd_pitch'
                    )}
                  />
                </div>
                <div className="flex flex-col gap-2 justify-end">
                  <Typography>
                    {l10n.getString(
                      'settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1'
                    )}
                  </Typography>
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="resetsSettings.resetMountingFeet"
                    label={l10n.getString(
                      'settings-general-fk_settings-leg_fk-reset_mounting_feet-v1'
                    )}
                  />
                </div>
              </div>
            </div>

            <div>
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-arm_fk-reset_mode-description'
                )}
              </Typography>
              <div className="grid md:grid-cols-2 flex-col gap-3 pt-2 pb-3">
                <Radio
                  control={control}
                  name="resetsSettings.armsResetMode"
                  label={l10n.getString(
                    'settings-general-fk_settings-arm_fk-back'
                  )}
                  description={l10n.getString(
                    'settings-general-fk_settings-arm_fk-back-description'
                  )}
                  value={'0'}
                />
                <Radio
                  control={control}
                  name="resetsSettings.armsResetMode"
                  label={l10n.getString(
                    'settings-general-fk_settings-arm_fk-forward'
                  )}
                  description={l10n.getString(
                    'settings-general-fk_settings-arm_fk-forward-description'
                  )}
                  value={'1'}
                />
                <Radio
                  control={control}
                  name="resetsSettings.armsResetMode"
                  label={l10n.getString(
                    'settings-general-fk_settings-arm_fk-tpose_up'
                  )}
                  description={l10n.getString(
                    'settings-general-fk_settings-arm_fk-tpose_up-description'
                  )}
                  value={'2'}
                />
                <Radio
                  control={control}
                  name="resetsSettings.armsResetMode"
                  label={l10n.getString(
                    'settings-general-fk_settings-arm_fk-tpose_down'
                  )}
                  description={l10n.getString(
                    'settings-general-fk_settings-arm_fk-tpose_down-description'
                  )}
                  value={'3'}
                />
              </div>
            </div>

            <div className="flex flex-col pt-2 pb-1">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-fk_settings-enforce_joint_constraints'
                )}
              </Typography>
              <div className="pt-2">
                <Typography>
                  {l10n.getString(
                    'settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description'
                  )}
                </Typography>
              </div>
            </div>
            <div className="grid sm:grid-cols-1 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.enforceConstraints"
                label={l10n.getString(
                  'settings-general-fk_settings-enforce_joint_constraints-enforce_constraints'
                )}
              />
            </div>

            <div className="flex flex-col pt-2 pb-3">
              <Typography bold>
                {l10n.getString('settings-general-fk_settings-ik')}
              </Typography>
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-fk_settings-ik-use_position-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 pb-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.usePosition"
                label={l10n.getString(
                  'settings-general-fk_settings-ik-use_position'
                )}
              />
            </div>

            {config?.debug && (
              <>
                <div className="flex flex-col">
                  <div className="flex flex-col pt-2 pb-3 gap-2">
                    <Typography variant="section-title">
                      {l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-ratios'
                      )}
                    </Typography>
                    <Typography>
                      {l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-ratios-description'
                      )}
                    </Typography>
                  </div>
                  <div className="grid sm:grid-cols-2 gap-3 pb-3">
                    <NumberSelector
                      control={control}
                      name="ratios.imputeSpineFromUpperLower"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-impute_spine_from_top_down'
                      )}
                      valueLabelFormat={(value) =>
                        percentageFormat.format(value)
                      }
                      min={0.0}
                      max={1.0}
                      step={0.05}
                    />
                    <NumberSelector
                      control={control}
                      name="ratios.imputeSpineCurvature"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-impute_spine_curvature'
                      )}
                      valueLabelFormat={(value) =>
                        percentageFormat.format(value)
                      }
                      min={0.0}
                      max={1.0}
                      step={0.05}
                    />
                    <NumberSelector
                      control={control}
                      name="ratios.interpHipLegs"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-interp_hip_legs'
                      )}
                      valueLabelFormat={(value) =>
                        percentageFormat.format(value)
                      }
                      min={0.0}
                      max={1.0}
                      step={0.05}
                    />
                    <NumberSelector
                      control={control}
                      name="ratios.interpKneeTrackerAnkle"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle'
                      )}
                      valueLabelFormat={(value) =>
                        percentageFormat.format(value)
                      }
                      min={0.0}
                      max={1.0}
                      step={0.05}
                    />
                    <NumberSelector
                      control={control}
                      name="ratios.interpKneeAnkle"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-interp_knee_ankle'
                      )}
                      valueLabelFormat={(value) =>
                        percentageFormat.format(value)
                      }
                      min={0.0}
                      max={1.0}
                      step={0.05}
                    />
                  </div>
                </div>
              </>
            )}
            <div className="flex flex-col pt-2 pb-3">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-fk_settings-self_localization-title'
                )}
              </Typography>
              <Typography>
                {l10n.getString(
                  'settings-general-fk_settings-self_localization-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap3 pb5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.selfLocalization"
                label={l10n.getString(
                  'settings-general-fk_settings-self_localization-title'
                )}
              />
            </div>
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout icon={<TouchDoubleIcon />} id="gestureControl">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-gesture_control')}
            </Typography>

            <div className="flex flex-col pt-2 pb-4 gap-2">
              <Typography variant="section-title">
                {l10n.getString('settings-general-gesture_control-subtitle')}
              </Typography>
              <Typography>
                {l10n.getString('settings-general-gesture_control-description')}
              </Typography>
            </div>
            <div>
              <div className="grid sm:grid-cols-3 gap-5 pb-2">
                <CheckBox
                  variant="toggle"
                  outlined
                  control={control}
                  name="tapDetection.yawResetEnabled"
                  label={l10n.getString(
                    'settings-general-gesture_control-yawResetEnabled'
                  )}
                />
                <CheckBox
                  variant="toggle"
                  outlined
                  control={control}
                  name="tapDetection.fullResetEnabled"
                  label={l10n.getString(
                    'settings-general-gesture_control-fullResetEnabled'
                  )}
                />
                <CheckBox
                  variant="toggle"
                  outlined
                  control={control}
                  name="tapDetection.mountingResetEnabled"
                  label={l10n.getString(
                    'settings-general-gesture_control-mountingResetEnabled'
                  )}
                />
              </div>
              <div className="grid sm:grid-cols-3 gap-5 pb-2">
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'settings-general-gesture_control-yawResetTracker'
                    )}
                  </Typography>
                  <Dropdown
                    display="block"
                    control={control}
                    placeholder={''}
                    name="tapDetection.yawResetTracker"
                    items={bodyParts}
                  />
                </div>
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'settings-general-gesture_control-mountingResetTracker'
                    )}
                  </Typography>
                  <Dropdown
                    display="block"
                    control={control}
                    placeholder={''}
                    name="tapDetection.mountingResetTracker"
                    items={bodyParts}
                  />
                </div>
                <div>
                  <Typography variant="section-title">
                    {l10n.getString(
                      'settings-general-gesture_control-fullResetTracker'
                    )}
                  </Typography>
                  <Dropdown
                    display="block"
                    control={control}
                    placeholder={''}
                    name="tapDetection.fullResetTracker"
                    items={bodyParts}
                  />
                </div>
              </div>
            </div>
            <div className="grid sm:grid-cols-3 gap-5 pb-2">
              <NumberSelector
                control={control}
                name="tapDetection.yawResetDelay"
                label={l10n.getString(
                  'settings-general-gesture_control-yawResetDelay'
                )}
                valueLabelFormat={(value) => secondsFormat.format(value)}
                min={0.2}
                max={3.0}
                step={0.2}
              />
              <NumberSelector
                control={control}
                name="tapDetection.fullResetDelay"
                label={l10n.getString(
                  'settings-general-gesture_control-fullResetDelay'
                )}
                valueLabelFormat={(value) => secondsFormat.format(value)}
                min={0.2}
                max={3.0}
                step={0.2}
              />
              <NumberSelector
                control={control}
                name="tapDetection.mountingResetDelay"
                label={l10n.getString(
                  'settings-general-gesture_control-mountingResetDelay'
                )}
                valueLabelFormat={(value) => secondsFormat.format(value)}
                min={0.2}
                max={3.0}
                step={0.2}
              />
            </div>
            <div className="grid sm:grid-cols-3 gap-5 pb-2">
              <NumberSelector
                control={control}
                name="tapDetection.yawResetTaps"
                label={l10n.getString(
                  'settings-general-gesture_control-yawResetTaps'
                )}
                valueLabelFormat={(value) =>
                  l10n.getString('settings-general-gesture_control-taps', {
                    amount: Math.round(value),
                  })
                }
                min={2}
                max={10}
                step={1}
              />
              <NumberSelector
                control={control}
                name="tapDetection.fullResetTaps"
                label={l10n.getString(
                  'settings-general-gesture_control-fullResetTaps'
                )}
                valueLabelFormat={(value) =>
                  l10n.getString('settings-general-gesture_control-taps', {
                    amount: Math.round(value),
                  })
                }
                min={2}
                max={10}
                step={1}
              />
              <NumberSelector
                control={control}
                name="tapDetection.mountingResetTaps"
                label={l10n.getString(
                  'settings-general-gesture_control-mountingResetTaps'
                )}
                valueLabelFormat={(value) =>
                  l10n.getString('settings-general-gesture_control-taps', {
                    amount: Math.round(value),
                  })
                }
                min={2}
                max={10}
                step={1}
              />
            </div>
            <div className="grid sm:grid-cols-1 gap-2 pt-2">
              <Typography variant="section-title">
                {l10n.getString(
                  'settings-general-gesture_control-numberTrackersOverThreshold'
                )}
              </Typography>
              <Typography>
                {l10n.getString(
                  'settings-general-gesture_control-numberTrackersOverThreshold-description'
                )}
              </Typography>
              <NumberSelector
                control={control}
                name="tapDetection.numberTrackersOverThreshold"
                label={l10n.getString(
                  'settings-general-gesture_control-numberTrackersOverThreshold'
                )}
                valueLabelFormat={(value) =>
                  l10n.getString('settings-general-gesture_control-trackers', {
                    amount: Math.round(value),
                  })
                }
                min={1}
                max={20}
                step={1}
              />
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
