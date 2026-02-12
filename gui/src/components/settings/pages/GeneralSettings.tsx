import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useState, useCallback, useRef } from 'react';
import { DefaultValues, useForm, Controller } from 'react-hook-form';
import {
  ChangeSettingsRequestT,
  FilteringSettingsT,
  FilteringType,
  LegTweaksSettingsT,
  ModelRatiosT,
  ModelSettingsT,
  ModelTogglesT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  SteamVRTrackersSettingT,
  TapDetectionSettingsT,
  HIDSettingsT,
  VelocitySettingsT,
  VelocityPreset,
  VelocityScalingPreset,
  ScalingValuesT,
} from 'solarxr-protocol';
import { useConfig } from '@/hooks/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocaleConfig } from '@/i18n/config';
import { CheckBox, CheckboxInternal } from '@/components/commons/Checkbox';
import { Button } from '@/components/commons/Button';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
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

export type SettingsForm = {
  trackers: {
    waist: boolean;
    chest: boolean;
    automaticTrackerToggle: boolean;
    leftFoot: boolean;
    rightFoot: boolean;
    leftKnee: boolean;
    rightKnee: boolean;
    leftElbow: boolean;
    rightElbow: boolean;
    leftHand: boolean;
    rightHand: boolean;
  };
  filtering: {
    type: number;
    amount: number;
  };
  toggles: {
    extendedSpine: boolean;
    extendedPelvis: boolean;
    extendedKnee: boolean;
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
    imputeWaistFromChestHip: number;
    imputeWaistFromChestLegs: number;
    imputeHipFromChestLegs: number;
    imputeHipFromWaistLegs: number;
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
  };
  legTweaks: {
    correctionStrength: number;
  };
  resetsSettings: ResetSettingsForm;
  stayAligned: StayAlignedSettingsForm;
  hidSettings: {
    trackersOverHID: boolean;
  };
  velocity: {
    sendDerivedVelocity: boolean;
    preset: number;
    enabledGroups: number; // Bitmask
    overrideScalingPreset: boolean;
    scalingPreset: number;
    enableUpscaling: boolean;
    scaleX: number;
    scaleY: number;
    scaleZ: number;
  };
};

const defaultValues: SettingsForm = {
  trackers: {
    waist: false,
    chest: false,
    automaticTrackerToggle: true,
    leftFoot: false,
    rightFoot: false,
    leftElbow: false,
    rightElbow: false,
    leftHand: false,
    rightHand: false,
    leftKnee: false,
    rightKnee: false,
  },
  toggles: {
    extendedSpine: true,
    extendedPelvis: true,
    extendedKnee: true,
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
    imputeWaistFromChestHip: 0.3,
    imputeWaistFromChestLegs: 0.2,
    imputeHipFromChestLegs: 0.45,
    imputeHipFromWaistLegs: 0.4,
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
  },
  legTweaks: { correctionStrength: 0.3 },
  resetsSettings: defaultResetSettings,
  stayAligned: defaultStayAlignedSettings,
  hidSettings: { trackersOverHID: false },
  velocity: {
    sendDerivedVelocity: false,
    preset: VelocityPreset.HYBRID,
    enabledGroups: 0,
    overrideScalingPreset: false,
    scalingPreset: VelocityScalingPreset.UNSCALED,
    enableUpscaling: false,
    scaleX: 1.0,
    scaleY: 1.0,
    scaleZ: 1.0,
  },
};

// Helper component to clamp scale values when upscaling is disabled
function UpscalingClampEffect({
  watch,
  setValue,
  getValues,
}: {
  watch: any;
  setValue: any;
  getValues: any;
}) {
  const enableUpscaling = watch('velocity.enableUpscaling');

  useEffect(() => {
    // If upscaling is disabled, clamp all values above 1.0
    if (!enableUpscaling) {
      const currentX = getValues('velocity.scaleX');
      const currentY = getValues('velocity.scaleY');
      const currentZ = getValues('velocity.scaleZ');

      if (currentX > 1.0) setValue('velocity.scaleX', 1.0);
      if (currentY > 1.0) setValue('velocity.scaleY', 1.0);
      if (currentZ > 1.0) setValue('velocity.scaleZ', 1.0);
    }
  }, [enableUpscaling, setValue, getValues]);

  return null;
}

// Helper component for bitmask checkbox
function BitmaskCheckbox({
  value,
  bitPosition,
  label,
  onChange,
}: {
  value: number;
  bitPosition: number;
  label: string;
  onChange: (newValue: number) => void;
}) {
  return (
    <CheckboxInternal
      variant="toggle"
      outlined
      name={`velocity.enabledGroups.${label.toLowerCase()}`}
      checked={(value & (1 << bitPosition)) !== 0}
      onChange={(e) => onChange((e.target as HTMLInputElement).checked ? value | (1 << bitPosition) : value & ~(1 << bitPosition))}
      label={label}
    />
  );
}

// Helper component to sync Y and Z values to X when entering unified scaling mode
function UnifiedScaleSyncEffect({
  watch,
  setValue,
  scalingPreset,
}: {
  watch: any;
  setValue: any;
  scalingPreset: number;
}) {
  const scaleX = watch('velocity.scaleX');
  const prevPresetRef = useRef(scalingPreset);

  useEffect(() => {
    // Only sync when transitioning INTO unified mode (not already in it)
    if (
      scalingPreset === VelocityScalingPreset.CUSTOM_UNIFIED &&
      prevPresetRef.current !== VelocityScalingPreset.CUSTOM_UNIFIED
    ) {
      setValue('velocity.scaleY', scaleX);
      setValue('velocity.scaleZ', scaleX);
    }
    prevPresetRef.current = scalingPreset;
  }, [scalingPreset, scaleX, setValue]);

  return null;
}

// Reusable slider component for velocity scaling
function VelocityScaleSlider({
  control,
  name,
  label,
  maxScale,
  onChange: onChangeCallback,
}: {
  control: any;
  name: string;
  label: string;
  maxScale: number;
  onChange?: (value: number) => void;
}) {
  return (
    <Controller
      control={control}
      name={name}
      render={({ field: { onChange, value } }) => {
        const [localValue, setLocalValue] = useState(value);
        useEffect(() => setLocalValue(value), [value]);

        const handleChange = (newValue: number) => {
          onChange(newValue);
          if (onChangeCallback) {
            onChangeCallback(newValue);
          }
        };

        return (
          <div className="flex flex-col gap-1">
            <Typography bold>{label}</Typography>
            <div className="flex gap-2 items-center bg-background-60 p-2 rounded-lg">
              <Button
                variant="tertiary"
                rounded
                onClick={() => handleChange(Math.max(0.01, +(value - 0.01).toFixed(2)))}
              >
                -
              </Button>
              <input
                type="range"
                className="flex-grow accent-accent-background-30"
                min={0.01}
                max={maxScale}
                step={0.01}
                value={localValue}
                onChange={(e) => setLocalValue(+e.target.value)}
                onMouseUp={(e) => handleChange(+(e.target as HTMLInputElement).value)}
                onTouchEnd={(e) => handleChange(+(e.target as HTMLInputElement).value)}
                onBlur={(e) => handleChange(+(e.target as HTMLInputElement).value)}
              />
              <Button
                variant="tertiary"
                rounded
                onClick={() => handleChange(Math.min(maxScale, +(value + 0.01).toFixed(2)))}
              >
                +
              </Button>
              <div className="min-w-[60px] text-center">
                {localValue.toFixed(2)}x
              </div>
            </div>
          </div>
        );
      }}
    />
  );
}

export function GeneralSettings() {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const { currentLocales } = useLocaleConfig();

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
    });
  const {
    trackers: {
      automaticTrackerToggle,
      leftHand: steamVrLeftHand,
      rightHand: steamVrRightHand,
    },
  } = watch();

  const onSubmit = useCallback((values: SettingsForm) => {
    console.log('[GeneralSettings] onSubmit called with velocity:', values.velocity);
    const settings = new ChangeSettingsRequestT();

    if (values.trackers) {
      const trackers = new SteamVRTrackersSettingT();
      trackers.waist = values.trackers.waist;
      trackers.chest = values.trackers.chest;
      trackers.leftFoot = values.trackers.leftFoot;
      trackers.rightFoot = values.trackers.rightFoot;

      trackers.leftKnee = values.trackers.leftKnee;
      trackers.rightKnee = values.trackers.rightKnee;

      trackers.leftElbow = values.trackers.leftElbow;
      trackers.rightElbow = values.trackers.rightElbow;

      trackers.leftHand = values.trackers.leftHand;
      trackers.rightHand = values.trackers.rightHand;

      trackers.automaticTrackerToggle = values.trackers.automaticTrackerToggle;
      settings.steamVrTrackers = trackers;
    }

    const modelSettings = new ModelSettingsT();

    if (values.toggles) {
      const toggles = new ModelTogglesT();
      toggles.floorClip = values.toggles.floorClip;
      toggles.skatingCorrection = values.toggles.skatingCorrection;
      toggles.extendedKnee = values.toggles.extendedKnee;
      toggles.extendedPelvis = values.toggles.extendedPelvis;
      toggles.extendedSpine = values.toggles.extendedSpine;
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
      ratios.imputeWaistFromChestHip =
        values.ratios.imputeWaistFromChestHip || -1;
      ratios.imputeWaistFromChestLegs =
        values.ratios.imputeWaistFromChestLegs || -1;
      ratios.imputeHipFromChestLegs =
        values.ratios.imputeHipFromChestLegs || -1;
      ratios.imputeHipFromWaistLegs =
        values.ratios.imputeHipFromWaistLegs || -1;
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

    settings.modelSettings = modelSettings;

    const tapDetection = new TapDetectionSettingsT();
    tapDetection.fullResetDelay = values.tapDetection.fullResetDelay;
    tapDetection.fullResetEnabled = values.tapDetection.fullResetEnabled;
    tapDetection.fullResetTaps = values.tapDetection.fullResetTaps;
    tapDetection.yawResetDelay = values.tapDetection.yawResetDelay;
    tapDetection.yawResetEnabled = values.tapDetection.yawResetEnabled;
    tapDetection.yawResetTaps = values.tapDetection.yawResetTaps;
    tapDetection.mountingResetEnabled =
      values.tapDetection.mountingResetEnabled;
    tapDetection.mountingResetDelay = values.tapDetection.mountingResetDelay;
    tapDetection.mountingResetTaps = values.tapDetection.mountingResetTaps;
    tapDetection.numberTrackersOverThreshold =
      values.tapDetection.numberTrackersOverThreshold;
    tapDetection.setupMode = false;
    settings.tapDetectionSettings = tapDetection;

    const filtering = new FilteringSettingsT();
    filtering.type = values.filtering.type;
    filtering.amount = values.filtering.amount;
    settings.filtering = filtering;

    settings.stayAligned = serializeStayAlignedSettings(values.stayAligned);

    const hidSettings = new HIDSettingsT();
    hidSettings.trackersOverHid = values.hidSettings.trackersOverHID;
    settings.hidSettings = hidSettings;

    if (values.resetsSettings) {
      settings.resetsSettings = loadResetSettings(values.resetsSettings);
    }

    if (values.velocity) {
      const velocity = new VelocitySettingsT();
      velocity.sendDerivedVelocity = values.velocity.sendDerivedVelocity;
      velocity.preset = values.velocity.preset;
      velocity.enabledGroups = values.velocity.enabledGroups;
      velocity.overrideScalingPreset = values.velocity.overrideScalingPreset;
      velocity.scalingPreset = values.velocity.scalingPreset;
      velocity.enableUpscaling = values.velocity.enableUpscaling;

      const scale = new ScalingValuesT();
      scale.scaleX = values.velocity.scaleX;
      scale.scaleY = values.velocity.scaleY;
      scale.scaleZ = values.velocity.scaleZ;
      velocity.scale = scale;

      settings.velocitySettings = velocity;
    }

    console.log('[GeneralSettings] Sending ChangeSettingsRequest, velocitySettings:', settings.velocitySettings);
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings);
  }, [sendRPCPacket]);

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  // If null, we still haven't shown the hands warning
  // if false then initially the hands warning was disabled
  const [handsWarning, setHandsWarning] = useState<boolean | null>(null);
  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    const formData: DefaultValues<SettingsForm> = {};

    if (settings.filtering) {
      formData.filtering = settings.filtering;
    }

    if (settings.steamVrTrackers) {
      formData.trackers = settings.steamVrTrackers;
      if (
        settings.steamVrTrackers.leftHand ||
        settings.steamVrTrackers.rightHand
      ) {
        setHandsWarning(false);
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
      formData.velocity = {
        sendDerivedVelocity: settings.velocitySettings.sendDerivedVelocity ?? defaultValues.velocity.sendDerivedVelocity,
        preset: settings.velocitySettings.preset ?? defaultValues.velocity.preset,
        enabledGroups: settings.velocitySettings.enabledGroups ?? defaultValues.velocity.enabledGroups,
        overrideScalingPreset: settings.velocitySettings.overrideScalingPreset ?? defaultValues.velocity.overrideScalingPreset,
        scalingPreset: settings.velocitySettings.scalingPreset ?? defaultValues.velocity.scalingPreset,
        enableUpscaling: settings.velocitySettings.enableUpscaling ?? defaultValues.velocity.enableUpscaling,
        scaleX: settings.velocitySettings.scale?.scaleX ?? defaultValues.velocity.scaleX,
        scaleY: settings.velocitySettings.scale?.scaleY ?? defaultValues.velocity.scaleY,
        scaleZ: settings.velocitySettings.scale?.scaleZ ?? defaultValues.velocity.scaleZ,
      };
    }

    reset({ ...getValues(), ...formData });
  });

  useEffect(() => {
    if ((steamVrLeftHand || steamVrRightHand) && handsWarning === null) {
      setHandsWarning(true);
    } else if (
      !(steamVrLeftHand || steamVrRightHand) &&
      handsWarning === false
    ) {
      setHandsWarning(null);
    }
  }, [steamVrLeftHand, steamVrRightHand, handsWarning]);

  // Handle scrolling to selected page
  // useEffect(() => {
  //   const typedState: { scrollTo: string } = state as any;
  //   if (!pageRef.current || !typedState || !typedState.scrollTo) {
  //     return;
  //   }
  //   const elem = pageRef.current.querySelector(`#${typedState.scrollTo}`);
  //   if (elem) {
  //     elem.scrollIntoView({ behavior: 'smooth' });
  //   }
  // }, [state]);

  return (
    <SettingsPageLayout>
      <HandsWarningModal
        isOpen={!!handsWarning}
        onClose={() => {
          setValue('trackers.leftHand', false);
          setValue('trackers.rightHand', false);
          setHandsWarning(null);
        }}
        accept={() => {
          setValue('trackers.leftHand', true);
          setValue('trackers.rightHand', true);
          setHandsWarning(false);
        }}
      />
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<SteamIcon />} id="steamvr">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-steamvr')}
            </Typography>
            <Typography variant="section-title">
              {l10n.getString('settings-general-steamvr-subtitle')}
            </Typography>
            <div className="flex flex-col py-2">
              {l10n
                .getString('settings-general-steamvr-description')
                .split('\n')
                .map((line, i) => (
                  <Typography key={i}>{line}</Typography>
                ))}
            </div>
            <div className="flex flex-col pt-4" />
            <Typography variant="section-title">
              {l10n.getString(
                'settings-general-steamvr-trackers-tracker_toggling'
              )}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              {l10n
                .getString(
                  'settings-general-steamvr-trackers-tracker_toggling-description'
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
        <SettingsPagePaneLayout icon={<WrenchIcon />} id="fksettings">
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
                  name="resetsSettings.armsMountingResetMode"
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
                  name="resetsSettings.armsMountingResetMode"
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
                  name="resetsSettings.armsMountingResetMode"
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
                  name="resetsSettings.armsMountingResetMode"
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
                <div className="flex flex-col pt-2 pb-3">
                  <Typography variant="section-title">
                    {l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-toggles'
                    )}
                  </Typography>
                  <Typography>
                    {l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-description'
                    )}
                  </Typography>
                </div>
                <div className="grid sm:grid-cols-2 gap-3 pb-3">
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="toggles.extendedSpine"
                    label={l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-extended_spine_model'
                    )}
                  />
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="toggles.extendedPelvis"
                    label={l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-extended_pelvis_model'
                    )}
                  />
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="toggles.extendedKnee"
                    label={l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-extended_knees_model'
                    )}
                  />
                </div>
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
                      name="ratios.imputeWaistFromChestHip"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip'
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
                      name="ratios.imputeWaistFromChestLegs"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs'
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
                      name="ratios.imputeHipFromChestLegs"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs'
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
                      name="ratios.imputeHipFromWaistLegs"
                      label={l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs'
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
            )}

            <div className="flex flex-col pt-5 pb-2">
              <Typography variant="section-title">
                Tracker Velocity Settings
              </Typography>
              <Typography>
                Enables derived velocity tracking for Natural Locomotion and similar systems.
                This may cause jitter in some VR titles when moving your upper body.
              </Typography>
            </div>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="velocity.sendDerivedVelocity"
              label="Enable Velocity Tracking"
            />

            {watch('velocity.sendDerivedVelocity') && (
              <>
                <div className="flex flex-col pt-4 pb-2">
                  <Typography variant="section-title">
                    Velocity Tracking Preset
                  </Typography>
              <Typography color="secondary">
                Choose which trackers send velocity data. HYBRID is recommended for VRChat.
              </Typography>
            </div>
            <div className="flex gap-3 pt-2 flex-col">
              <Radio
                control={control}
                name="velocity.preset"
                label="All Trackers"
                description="All trackers with position data will send velocity"
                value={VelocityPreset.ALL.toString()}
              />
              <Radio
                control={control}
                name="velocity.preset"
                label="Hybrid (Feet + Ankles)"
                description="Only feet and ankle trackers send velocity (recommended for VRChat)"
                value={VelocityPreset.HYBRID.toString()}
              />
              <Radio
                control={control}
                name="velocity.preset"
                label="Custom"
                description="Manually select which tracker groups send velocity"
                value={VelocityPreset.CUSTOM.toString()}
              />
            </div>

            {Number(watch('velocity.preset')) === VelocityPreset.CUSTOM && (
              <div className="flex flex-col pt-3 pb-2 gap-2">
                <Typography variant="section-title">
                  Custom Tracker Groups
                </Typography>
                <Typography color="secondary">
                  Select which tracker role groups should send velocity data
                </Typography>
                <Controller
                  control={control}
                  name="velocity.enabledGroups"
                  render={({ field: { onChange, value } }) => (
                    <div className="grid grid-cols-2 gap-3 pt-2">
                      <BitmaskCheckbox value={value} bitPosition={0} label="Feet" onChange={onChange} />
                      <BitmaskCheckbox value={value} bitPosition={1} label="Ankles" onChange={onChange} />
                      <BitmaskCheckbox value={value} bitPosition={2} label="Knees" onChange={onChange} />
                      <BitmaskCheckbox value={value} bitPosition={3} label="Chest" onChange={onChange} />
                      <BitmaskCheckbox value={value} bitPosition={4} label="Waist" onChange={onChange} />
                      <BitmaskCheckbox value={value} bitPosition={5} label="Elbows" onChange={onChange} />
                    </div>
                  )}
                />
              </div>
            )}

            <div className="flex flex-col pt-4 pb-2">
              <Typography variant="section-title">
                Velocity Scaling
              </Typography>
              <Typography color="secondary">
                Scale velocity values. Use UNSCALED unless experiencing locomotion issues.
              </Typography>
            </div>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="velocity.overrideScalingPreset"
              label="Override Scaling Preset"
            />

            {watch('velocity.overrideScalingPreset') && (
              <>
                <div className="flex gap-3 pt-2 flex-col">
              <Radio
                control={control}
                name="velocity.scalingPreset"
                label="Unscaled"
                description="No scaling applied (1.0x)"
                value={VelocityScalingPreset.UNSCALED.toString()}
              />
              <Radio
                control={control}
                name="velocity.scalingPreset"
                label="Hybrid/NaLo"
                description="0.25x scaling for hybrid locomotion"
                value={VelocityScalingPreset.HYBRID.toString()}
              />
              <Radio
                control={control}
                name="velocity.scalingPreset"
                label="Custom Unified"
                description="Single scaling value for all axes"
                value={VelocityScalingPreset.CUSTOM_UNIFIED.toString()}
              />
              <Radio
                control={control}
                name="velocity.scalingPreset"
                label="Custom Per-Axis"
                description="Individual scaling per axis (X, Y, Z)"
                value={VelocityScalingPreset.CUSTOM_PER_AXIS.toString()}
              />
            </div>

            {(Number(watch('velocity.scalingPreset')) === VelocityScalingPreset.CUSTOM_UNIFIED ||
              Number(watch('velocity.scalingPreset')) === VelocityScalingPreset.CUSTOM_PER_AXIS) && (
              <>
                <div className="flex flex-col pt-4 pb-2">
                  <Typography variant="section-title">
                    Advanced Scaling
                  </Typography>
                  <Typography color="secondary">
                    WARNING: Enabling upscaling may break full-body tracking position prediction.
                  </Typography>
                </div>
                <CheckBox
                  variant="toggle"
                  outlined
                  control={control}
                  name="velocity.enableUpscaling"
                  label="Allow Upscaling (>1.0x)"
                />
                <UpscalingClampEffect
                  watch={watch}
                  setValue={setValue}
                  getValues={getValues}
                />
              </>
            )}

            {Number(watch('velocity.scalingPreset')) === VelocityScalingPreset.CUSTOM_UNIFIED && (
              <div className="flex flex-col gap-3 pt-4 pb-3">
                <VelocityScaleSlider
                  control={control}
                  name="velocity.scaleX"
                  label="Unified Scale (All Axes)"
                  maxScale={watch('velocity.enableUpscaling') ? 5.0 : 1.0}
                  onChange={(newValue) => {
                    setValue('velocity.scaleY', newValue);
                    setValue('velocity.scaleZ', newValue);
                  }}
                />
              </div>
            )}
            <UnifiedScaleSyncEffect
              watch={watch}
              setValue={setValue}
              scalingPreset={Number(watch('velocity.scalingPreset'))}
            />

            {Number(watch('velocity.scalingPreset')) === VelocityScalingPreset.CUSTOM_PER_AXIS && (
            <div className="flex flex-col gap-3 pt-4 pb-3">
              <VelocityScaleSlider
                control={control}
                name="velocity.scaleX"
                label="Scale X"
                maxScale={watch('velocity.enableUpscaling') ? 5.0 : 1.0}
              />
              <VelocityScaleSlider
                control={control}
                name="velocity.scaleY"
                label="Scale Y"
                maxScale={watch('velocity.enableUpscaling') ? 5.0 : 1.0}
              />
              <VelocityScaleSlider
                control={control}
                name="velocity.scaleZ"
                label="Scale Z"
                maxScale={watch('velocity.enableUpscaling') ? 5.0 : 1.0}
              />
            </div>
            )}
              </>
            )}
              </>
            )}
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout icon={<WrenchIcon />} id="gestureControl">
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
