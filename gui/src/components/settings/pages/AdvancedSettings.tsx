import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { ThemeSelector } from '@/components/commons/ThemeSelector';
import { SquaresIcon } from '@/components/commons/icon/SquaresIcon';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { useLocaleConfig } from '@/i18n/config';
import { LangSelector } from '@/components/commons/LangSelector';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Range } from '@/components/commons/Range';
import { Dropdown } from '@/components/commons/Dropdown';
import { Button } from '@/components/commons/Button';
import { SettingsResetModal } from '../SettingsResetModal';

import { defaultConfig as defaultGUIConfig, useConfig } from '@/hooks/config';
import { defaultValues as defaultServerConfig } from '@/components/settings/pages/GeneralSettings';
import { defaultValues as defaultDevConfig } from '@/components/widgets/DeveloperModeWidget';
import {
  ChangeSettingsRequestT,
  DriftCompensationSettingsT,
  FilteringSettingsT,
  LegTweaksSettingsT,
  ModelRatiosT,
  ModelSettingsT,
  ModelTogglesT,
  ResetsSettingsT,
  RpcMessage,
  SteamVRTrackersSettingT,
  TapDetectionSettingsT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';

interface InterfaceSettingsForm {
  appearance: {
    devmode: boolean;
    theme: string;
    textSize: number;
    fonts: string;
  };
  notifications: {
    watchNewDevices: boolean;
    feedbackSound: boolean;
    feedbackSoundVolume: number;
    connectedTrackersWarning: boolean;
    useTray: boolean;
    discordPresence: boolean;
  };
}

export function AdvancedSettings() {
  const { currentLocales } = useLocaleConfig();
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit } = useForm<InterfaceSettingsForm>({
    defaultValues: {},
  });

  const [skipWarning, setSkipWarning] = useState(false);
  const { sendRPCPacket } = useWebsocketAPI();

  const onSubmit = (values: InterfaceSettingsForm) => {
    setConfig({
      debug: values.appearance.devmode,
      watchNewDevices: values.notifications.watchNewDevices,
      feedbackSound: values.notifications.feedbackSound,
      feedbackSoundVolume: values.notifications.feedbackSoundVolume,
      theme: values.appearance.theme,
      fonts: values.appearance.fonts.split(','),
      textSize: values.appearance.textSize,
      connectedTrackersWarning: values.notifications.connectedTrackersWarning,
      useTray: values.notifications.useTray,
      discordPresence: values.notifications.discordPresence,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  return (
    <SettingsPageLayout>
      <form
        className="flex flex-col gap-2 w-full"
        style={
          {
            '--font-size': '12rem',
            '--font-size-standard': '12rem',
            '--font-size-vr': '16rem',
            '--font-size-title': '25rem',
          } as React.CSSProperties
        }
      >
        <SettingsPagePaneLayout icon={<WrenchIcon></WrenchIcon>} id="advanced">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-utils-advanced')}
            </Typography>

            <div className="grid grid-cols-2 gap-2 mobile:grid-cols-1">
              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-reset-gui')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-reset-gui-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => setSkipWarning(true)}
                  >
                    {l10n.getString('settings-utils-advanced-reset-gui-label')}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      const guiSettings = getGUIDefaults();
                      setConfig(guiSettings);
                    }}
                    onClose={() => setSkipWarning(false)}
                    isOpen={skipWarning}
                  ></SettingsResetModal>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-reset-server')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-reset-server-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => setSkipWarning(true)}
                  >
                    {l10n.getString(
                      'settings-utils-advanced-reset-server-label'
                    )}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      const serverSettings = getServerDefaults();
                      sendRPCPacket(
                        RpcMessage.ChangeSettingsRequest,
                        serverSettings
                      );
                    }}
                    onClose={() => setSkipWarning(false)}
                    isOpen={skipWarning}
                  ></SettingsResetModal>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-reset-all')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-reset-all-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => setSkipWarning(true)}
                  >
                    {l10n.getString('settings-utils-advanced-reset-all-label')}
                  </Button>
                  <SettingsResetModal
                    accept={() => {
                      const guiSettings = getGUIDefaults();
                      const serverSettings = getServerDefaults();

                      // Server settings
                      sendRPCPacket(
                        RpcMessage.ChangeSettingsRequest,
                        serverSettings
                      );

                      // GUI settings
                      setConfig(guiSettings);
                    }}
                    onClose={() => setSkipWarning(false)}
                    isOpen={skipWarning}
                  ></SettingsResetModal>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-advanced-open_config')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-advanced-open_config-description'
                    )}
                  </Typography>
                </div>
                <div className="flex flex-col gap-2">
                  <Button variant="secondary" onClick={() => {}}>
                    {l10n.getString(
                      'settings-utils-advanced-open_config-label'
                    )}
                  </Button>
                  {/* TODO: open config folder */}
                </div>
              </div>
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}

function getServerDefaults() {
  const settings = new ChangeSettingsRequestT();

  const trackers = new SteamVRTrackersSettingT();
  trackers.waist = defaultServerConfig.trackers.waist;
  trackers.chest = defaultServerConfig.trackers.chest;
  trackers.leftFoot = defaultServerConfig.trackers.leftFoot;
  trackers.rightFoot = defaultServerConfig.trackers.rightFoot;
  trackers.leftKnee = defaultServerConfig.trackers.leftKnee;
  trackers.rightKnee = defaultServerConfig.trackers.rightKnee;
  trackers.leftElbow = defaultServerConfig.trackers.leftElbow;
  trackers.rightElbow = defaultServerConfig.trackers.rightElbow;
  trackers.leftHand = defaultServerConfig.trackers.leftHand;
  trackers.rightHand = defaultServerConfig.trackers.rightHand;
  trackers.automaticTrackerToggle =
    defaultServerConfig.trackers.automaticTrackerToggle;
  settings.steamVrTrackers = trackers;

  const modelSettings = new ModelSettingsT();
  const toggles = new ModelTogglesT();
  toggles.floorClip = defaultServerConfig.toggles.floorClip;
  toggles.skatingCorrection = defaultServerConfig.toggles.skatingCorrection;
  toggles.extendedKnee = defaultServerConfig.toggles.extendedKnee;
  toggles.extendedPelvis = defaultServerConfig.toggles.extendedPelvis;
  toggles.extendedSpine = defaultServerConfig.toggles.extendedSpine;
  toggles.forceArmsFromHmd = defaultServerConfig.toggles.forceArmsFromHmd;
  toggles.viveEmulation = defaultServerConfig.toggles.viveEmulation;
  toggles.toeSnap = defaultServerConfig.toggles.toeSnap;
  toggles.footPlant = defaultServerConfig.toggles.footPlant;
  toggles.selfLocalization = defaultServerConfig.toggles.selfLocalization;
  modelSettings.toggles = toggles;

  const ratios = new ModelRatiosT();
  ratios.imputeWaistFromChestHip =
    defaultServerConfig.ratios.imputeWaistFromChestHip;
  ratios.imputeWaistFromChestLegs =
    defaultServerConfig.ratios.imputeWaistFromChestLegs;
  ratios.imputeHipFromChestLegs =
    defaultServerConfig.ratios.imputeHipFromChestLegs;
  ratios.imputeHipFromWaistLegs =
    defaultServerConfig.ratios.imputeHipFromWaistLegs;
  ratios.interpHipLegs = defaultServerConfig.ratios.interpHipLegs;
  ratios.interpKneeTrackerAnkle =
    defaultServerConfig.ratios.interpKneeTrackerAnkle;
  ratios.interpKneeAnkle = defaultServerConfig.ratios.interpKneeAnkle;
  modelSettings.ratios = ratios;

  const legTweaks = new LegTweaksSettingsT();
  legTweaks.correctionStrength =
    defaultServerConfig.legTweaks.correctionStrength;
  modelSettings.legTweaks = legTweaks;

  settings.modelSettings = modelSettings;

  const tapDetection = new TapDetectionSettingsT();
  tapDetection.fullResetDelay = defaultServerConfig.tapDetection.fullResetDelay;
  tapDetection.fullResetEnabled =
    defaultServerConfig.tapDetection.fullResetEnabled;
  tapDetection.fullResetTaps = defaultServerConfig.tapDetection.fullResetTaps;
  tapDetection.yawResetDelay = defaultServerConfig.tapDetection.yawResetDelay;
  tapDetection.yawResetEnabled =
    defaultServerConfig.tapDetection.yawResetEnabled;
  tapDetection.yawResetTaps = defaultServerConfig.tapDetection.yawResetTaps;
  tapDetection.mountingResetEnabled =
    defaultServerConfig.tapDetection.mountingResetEnabled;
  tapDetection.mountingResetDelay =
    defaultServerConfig.tapDetection.mountingResetDelay;
  tapDetection.mountingResetTaps =
    defaultServerConfig.tapDetection.mountingResetTaps;
  tapDetection.numberTrackersOverThreshold =
    defaultServerConfig.tapDetection.numberTrackersOverThreshold;
  tapDetection.setupMode = false;
  settings.tapDetectionSettings = tapDetection;

  const filtering = new FilteringSettingsT();
  filtering.type = defaultServerConfig.filtering.type;
  filtering.amount = defaultServerConfig.filtering.amount;
  settings.filtering = filtering;

  const driftCompensation = new DriftCompensationSettingsT();
  driftCompensation.enabled = defaultServerConfig.driftCompensation.enabled;
  driftCompensation.amount = defaultServerConfig.driftCompensation.amount;
  driftCompensation.maxResets = defaultServerConfig.driftCompensation.maxResets;
  settings.driftCompensation = driftCompensation;

  const resetsSettings = new ResetsSettingsT();
  resetsSettings.resetMountingFeet =
    defaultServerConfig.resetsSettings.resetMountingFeet;
  resetsSettings.armsMountingResetMode =
    defaultServerConfig.resetsSettings.armsMountingResetMode;
  resetsSettings.yawResetSmoothTime =
    defaultServerConfig.resetsSettings.yawResetSmoothTime;
  resetsSettings.saveMountingReset =
    defaultServerConfig.resetsSettings.saveMountingReset;
  resetsSettings.resetHmdPitch =
    defaultServerConfig.resetsSettings.resetHmdPitch;
  settings.resetsSettings = resetsSettings;

  return settings;
}

function getGUIDefaults() {
  return {
    debug: defaultGUIConfig.debug,
    watchNewDevices: defaultGUIConfig.watchNewDevices,
    devSettings: defaultDevConfig,
    feedbackSound: defaultGUIConfig.feedbackSound,
    feedbackSoundVolume: defaultGUIConfig.feedbackSoundVolume,
    connectedTrackersWarning: defaultGUIConfig.connectedTrackersWarning,
    // uncomment after #1152 is merged
    // showNavbarOnboarding: defaultGUIConfig.showNavbarOnboarding,
    theme: defaultGUIConfig.theme,
    textSize: defaultGUIConfig.textSize,
    fonts: defaultGUIConfig.fonts,
    useTray: defaultGUIConfig.useTray,
    mirrorView: defaultGUIConfig.mirrorView,
    assignMode: defaultGUIConfig.assignMode,
    discordPresence: defaultGUIConfig.discordPresence,
  };
}
