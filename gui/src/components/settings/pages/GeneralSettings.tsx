import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import {
  ChangeSettingsRequestT,
  DriftCompensationSettingsT,
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
} from 'solarxr-protocol';
import { useConfig } from '../../../hooks/config';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { useLocaleConfig } from '../../../i18n/config';
import { CheckBox } from '../../commons/Checkbox';
import { SquaresIcon } from '../../commons/icon/SquaresIcon';
import { SteamIcon } from '../../commons/icon/SteamIcon';
import { WrenchIcon } from '../../commons/icon/WrenchIcons';
import { LangSelector } from '../../commons/LangSelector';
import { NumberSelector } from '../../commons/NumberSelector';
import { Radio } from '../../commons/Radio';
import { ThemeSelector } from '../../commons/ThemeSelector';
import { Typography } from '../../commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '../SettingsPageLayout';

interface SettingsForm {
  trackers: {
    waist: boolean;
    chest: boolean;
    feet: boolean;
    knees: boolean;
    elbows: boolean;
    hands: boolean;
  };
  filtering: {
    type: number;
    amount: number;
  };
  driftCompensation: {
    enabled: boolean;
    amount: number;
    maxResets: number;
  };
  toggles: {
    extendedSpine: boolean;
    extendedPelvis: boolean;
    extendedKnee: boolean;
    forceArmsFromHmd: boolean;
    floorClip: boolean;
    skatingCorrection: boolean;
    viveEmulation: boolean;
    toeSnap: boolean;
    footPlant: boolean;
  };
  ratios: {
    imputeWaistFromChestHip: number;
    imputeWaistFromChestLegs: number;
    imputeHipFromChestLegs: number;
    imputeHipFromWaistLegs: number;
    interpHipLegs: number;
    interpKneeTrackerAnkle: number;
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
  };
  legTweaks: {
    correctionStrength: number;
  };
  interface: {
    devmode: boolean;
    watchNewDevices: boolean;
    feedbackSound: boolean;
    feedbackSoundVolume: number;
    theme: string;
  };
}

const defaultValues = {
  trackers: {
    waist: false,
    chest: false,
    elbows: false,
    knees: false,
    feet: false,
    hands: false,
  },
  toggles: {
    extendedSpine: true,
    extendedPelvis: true,
    extendedKnee: true,
    forceArmsFromHmd: false,
    floorClip: false,
    skatingCorrection: false,
    viveEmulation: false,
    toeSnap: false,
    flootPlant: true,
  },
  ratios: {
    imputeWaistFromChestHip: 0.3,
    imputeWaistFromChestLegs: 0.2,
    imputeHipFromChestLegs: 0.45,
    imputeHipFromWaistLegs: 0.4,
    interpHipLegs: 0.25,
    interpKneeTrackerAnkle: 0.85,
  },
  filtering: { amount: 0.1, type: FilteringType.NONE },
  driftCompensation: {
    enabled: false,
    amount: 0.1,
    maxResets: 1,
  },
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
  },
  legTweaks: { correctionStrength: 0.3 },
  interface: {
    devmode: false,
    watchNewDevices: true,
    feedbackSound: true,
    feedbackSoundVolume: 0.5,
    theme: 'slime',
  },
};

export function GeneralSettings() {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  // const { state } = useLocation();
  const { currentLocales } = useLocaleConfig();
  // const pageRef = useRef<HTMLFormElement | null>(null);

  const percentageFormat = Intl.NumberFormat(currentLocales, {
    style: 'percent',
    maximumFractionDigits: 0,
  });

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { reset, control, watch, handleSubmit } = useForm<SettingsForm>({
    defaultValues: defaultValues,
  });

  const onSubmit = (values: SettingsForm) => {
    const settings = new ChangeSettingsRequestT();

    if (values.trackers) {
      const trackers = new SteamVRTrackersSettingT();
      trackers.waist = values.trackers.waist;
      trackers.chest = values.trackers.chest;
      trackers.feet = values.trackers.feet;
      trackers.knees = values.trackers.knees;
      trackers.elbows = values.trackers.elbows;
      trackers.hands = values.trackers.hands;
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
      toggles.viveEmulation = values.toggles.viveEmulation;
      toggles.toeSnap = values.toggles.toeSnap;
      toggles.footPlant = values.toggles.footPlant;
      modelSettings.toggles = toggles;
    }

    if (values.ratios) {
      const ratios = new ModelRatiosT();
      ratios.imputeWaistFromChestHip = values.ratios.imputeWaistFromChestHip;
      ratios.imputeWaistFromChestLegs = values.ratios.imputeWaistFromChestLegs;
      ratios.imputeHipFromChestLegs = values.ratios.imputeHipFromChestLegs;
      ratios.imputeHipFromWaistLegs = values.ratios.imputeHipFromWaistLegs;
      ratios.interpHipLegs = values.ratios.interpHipLegs;
      ratios.interpKneeTrackerAnkle = values.ratios.interpKneeTrackerAnkle;
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
    tapDetection.setupMode = false;
    settings.tapDetectionSettings = tapDetection;

    const filtering = new FilteringSettingsT();
    filtering.type = values.filtering.type;
    filtering.amount = values.filtering.amount;
    settings.filtering = filtering;

    const driftCompensation = new DriftCompensationSettingsT();
    driftCompensation.enabled = values.driftCompensation.enabled;
    driftCompensation.amount = values.driftCompensation.amount;
    driftCompensation.maxResets = values.driftCompensation.maxResets;
    settings.driftCompensation = driftCompensation;

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings);

    setConfig({
      debug: values.interface.devmode,
      watchNewDevices: values.interface.watchNewDevices,
      feedbackSound: values.interface.feedbackSound,
      feedbackSoundVolume: values.interface.feedbackSoundVolume,
      theme: values.interface.theme,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    const formData: DefaultValues<SettingsForm> = {
      interface: {
        devmode: config?.debug,
        watchNewDevices: config?.watchNewDevices,
        feedbackSound: config?.feedbackSound,
        feedbackSoundVolume: config?.feedbackSoundVolume,
        theme: config?.theme,
      },
    };

    if (settings.filtering) {
      formData.filtering = settings.filtering;
    }

    if (settings.driftCompensation) {
      formData.driftCompensation = settings.driftCompensation;
    }

    if (settings.steamVrTrackers) {
      formData.trackers = settings.steamVrTrackers;
    }

    if (settings.modelSettings?.toggles) {
      formData.toggles = Object.keys(settings.modelSettings?.toggles).reduce(
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
      formData.ratios = Object.keys(settings.modelSettings?.ratios).reduce(
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
      };
    }

    if (settings.modelSettings?.legTweaks) {
      formData.legTweaks = {
        correctionStrength:
          settings.modelSettings?.legTweaks.correctionStrength ||
          defaultValues.legTweaks.correctionStrength,
      };
    }

    reset(formData);
  });

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
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<SteamIcon></SteamIcon>} id="steamvr">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-steamvr')}
            </Typography>
            <Typography bold>
              {l10n.getString('settings-general-steamvr-subtitle')}
            </Typography>
            <div className="flex flex-col py-2">
              {l10n
                .getString('settings-general-steamvr-description')
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </div>
            <div className="grid grid-cols-2 gap-3 pt-3">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="trackers.chest"
                label={l10n.getString(
                  'settings-general-steamvr-trackers-chest'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="trackers.waist"
                label={l10n.getString(
                  'settings-general-steamvr-trackers-waist'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="trackers.knees"
                label={l10n.getString(
                  'settings-general-steamvr-trackers-knees'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="trackers.feet"
                label={l10n.getString('settings-general-steamvr-trackers-feet')}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="trackers.elbows"
                label={l10n.getString(
                  'settings-general-steamvr-trackers-elbows'
                )}
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="trackers.hands"
                label={l10n.getString(
                  'settings-general-steamvr-trackers-hands'
                )}
              />
            </div>
          </>
        </SettingsPagePaneLayout>
        <SettingsPagePaneLayout icon={<WrenchIcon></WrenchIcon>} id="mechanics">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-tracker_mechanics')}
            </Typography>
            <Typography bold>
              {l10n.getString('settings-general-tracker_mechanics-filtering')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              {l10n
                .getString(
                  'settings-general-tracker_mechanics-filtering-description'
                )
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
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
                desciption={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-none-description'
                )}
                value={FilteringType.NONE}
              ></Radio>
              <Radio
                control={control}
                name="filtering.type"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-smoothing'
                )}
                desciption={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-smoothing-description'
                )}
                value={FilteringType.SMOOTHING}
              ></Radio>
              <Radio
                control={control}
                name="filtering.type"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-prediction'
                )}
                desciption={l10n.getString(
                  'settings-general-tracker_mechanics-filtering-type-prediction-description'
                )}
                value={FilteringType.PREDICTION}
              ></Radio>
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
            <div className="flex flex-col pt-4 pb-4"></div>
            <Typography bold>
              {l10n.getString(
                'settings-general-tracker_mechanics-drift_compensation'
              )}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              {l10n
                .getString(
                  'settings-general-tracker_mechanics-drift_compensation-description'
                )
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </div>
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="driftCompensation.enabled"
              label={l10n.getString(
                'settings-general-tracker_mechanics-drift_compensation-enabled-label'
              )}
            />
            <div className="flex gap-5 pt-5 md:flex-row flex-col">
              <NumberSelector
                control={control}
                name="driftCompensation.amount"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-drift_compensation-amount-label'
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
                name="driftCompensation.maxResets"
                label={l10n.getString(
                  'settings-general-tracker_mechanics-drift_compensation-max_resets-label'
                )}
                min={1}
                max={25}
                step={1}
              />
            </div>
          </>
        </SettingsPagePaneLayout>
        <SettingsPagePaneLayout
          icon={<WrenchIcon></WrenchIcon>}
          id="fksettings"
        >
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-fk_settings')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              <Typography bold>
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-skating_correction'
                )}
              </Typography>
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-skating_correction-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-3 pb-4">
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
              <Typography bold>
                {l10n.getString('settings-general-fk_settings-leg_fk')}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-3 pb-3">
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-fk_settings-leg_tweak-floor_clip-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-1 gap-3 pb-3">
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
              <Typography color="secondary">
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
              <Typography color="secondary">
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
              <Typography bold>
                {l10n.getString('settings-general-fk_settings-arm_fk')}
              </Typography>
              <Typography color="secondary">
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
            {config?.debug && (
              <>
                <div className="flex flex-col pt-2 pb-3">
                  <Typography bold>
                    {l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-toggles'
                    )}
                  </Typography>
                  <Typography color="secondary">
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
                      'settings-general-fk_settings-skeleton_settings-extended_spine'
                    )}
                  />
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="toggles.extendedPelvis"
                    label={l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-extended_pelvis'
                    )}
                  />
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="toggles.extendedKnee"
                    label={l10n.getString(
                      'settings-general-fk_settings-skeleton_settings-extended_knees'
                    )}
                  />
                </div>
                <div className="flex flex-col pt-2 pb-3">
                  <div className="flex flex-col pt-2 pb-3">
                    <Typography bold>
                      {l10n.getString(
                        'settings-general-fk_settings-skeleton_settings-ratios'
                      )}
                    </Typography>
                    <Typography color="secondary">
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
                  </div>
                </div>

                <div className="flex flex-col pt-2 pb-3">
                  <Typography bold>
                    {l10n.getString(
                      'settings-general-fk_settings-vive_emulation-title'
                    )}
                  </Typography>
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-general-fk_settings-vive_emulation-description'
                    )}
                  </Typography>
                </div>
                <div className="grid sm:grid-cols-1 gap-3 pb-5">
                  <CheckBox
                    variant="toggle"
                    outlined
                    control={control}
                    name="toggles.viveEmulation"
                    label={l10n.getString(
                      'settings-general-fk_settings-vive_emulation-label'
                    )}
                  />
                </div>
              </>
            )}
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout
          icon={<WrenchIcon></WrenchIcon>}
          id="gestureControl"
        >
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-gesture_control')}
            </Typography>
            <Typography bold>
              {l10n.getString('settings-general-gesture_control-subtitle')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              <Typography color="secondary">
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
                valueLabelFormat={(value) => `${Math.round(value * 10) / 10} s`}
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
                valueLabelFormat={(value) => `${Math.round(value * 10) / 10} s`}
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
                valueLabelFormat={(value) => `${Math.round(value * 10) / 10} s`}
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
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout
          icon={<SquaresIcon></SquaresIcon>}
          id="interface"
        >
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-interface')}
            </Typography>

            <Typography bold>
              {l10n.getString('settings-general-interface-dev_mode')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-interface-dev_mode-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="interface.devmode"
                label={l10n.getString(
                  'settings-general-interface-dev_mode-label'
                )}
              />
            </div>

            <Typography bold>
              {l10n.getString('settings-general-interface-serial_detection')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-interface-serial_detection-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="interface.watchNewDevices"
                label={l10n.getString(
                  'settings-general-interface-serial_detection-label'
                )}
              />
            </div>

            <Typography bold>
              {l10n.getString('settings-general-interface-feedback_sound')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
                {l10n.getString(
                  'settings-general-interface-feedback_sound-description'
                )}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <CheckBox
                variant="toggle"
                control={control}
                outlined
                name="interface.feedbackSound"
                label={l10n.getString(
                  'settings-general-interface-feedback_sound-label'
                )}
              />
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <NumberSelector
                control={control}
                name="interface.feedbackSoundVolume"
                label={l10n.getString(
                  'settings-general-interface-feedback_sound-volume'
                )}
                valueLabelFormat={(value) => percentageFormat.format(value)}
                min={0.1}
                max={1.0}
                step={0.1}
              />
            </div>
            <div className="pb-4">
              <Typography bold>
                {l10n.getString('settings-general-interface-theme')}
              </Typography>
              <div className="flex flex-wrap gap-3 pt-2">
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'slime'}
                  colors="!bg-slime"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'slime-green'}
                  colors="!bg-slime-green"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'slime-yellow'}
                  colors="!bg-slime-yellow"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'slime-orange'}
                  colors="!bg-slime-orange"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'slime-red'}
                  colors="!bg-slime-red"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'dark'}
                  colors="!bg-dark"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'light'}
                  colors="!bg-light"
                ></ThemeSelector>
                <ThemeSelector
                  control={control}
                  name="interface.theme"
                  value={'trans'}
                  colors="!bg-trans-flag"
                ></ThemeSelector>
              </div>
            </div>
            <Typography bold>
              {l10n.getString('settings-general-interface-lang')}
            </Typography>
            <div className="flex flex-col pt-1 pb-2">
              <Typography color="secondary">
                {l10n.getString('settings-general-interface-lang-description')}
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 pb-4">
              <LangSelector alignment="left" />
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
