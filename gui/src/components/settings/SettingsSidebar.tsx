import classNames from 'classnames';
import { ReactNode, useMemo, useState } from 'react';
import { NavLink, useLocation, useMatch } from 'react-router-dom';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { Button } from '../commons/Button';
import { SettingsResetModal } from './SettingsResetModal';
import { defaultConfig as defaultGUIConfig, useConfig } from '@/hooks/config';
import { defaultValues as defaultServerConfig } from './pages/GeneralSettings';
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

export function SettingsLink({
  to,
  scrollTo,
  children,
}: {
  to: string;
  scrollTo?: string;
  children: ReactNode;
}) {
  const { state } = useLocation();
  const doesMatch = useMatch({
    path: to,
  });

  const isActive = useMemo(() => {
    const typedState: { scrollTo?: string } = state as any;
    return (
      (doesMatch && !scrollTo && !typedState?.scrollTo) ||
      (doesMatch && typedState?.scrollTo == scrollTo)
    );
  }, [state, doesMatch]);

  return (
    <NavLink
      to={to}
      state={{ scrollTo }}
      className={classNames('pl-5 py-2 hover:bg-background-60 rounded-lg', {
        'bg-background-60': isActive,
      })}
    >
      {children}
    </NavLink>
  );
}

export function SettingsSidebar() {
  const { l10n } = useLocalization();
  const [skipWarning, setSkipWarning] = useState(false);
  const { setConfig } = useConfig();
  const { sendRPCPacket } = useWebsocketAPI();

  return (
    <div className="flex flex-col px-5 py-5 gap-3 overflow-y-auto bg-background-70 rounded-lg h-full">
      <Typography variant="main-title">
        {l10n.getString('settings-sidebar-title')}
      </Typography>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">
          {l10n.getString('settings-sidebar-general')}
        </Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/trackers" scrollTo="steamvr">
            SteamVR
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="mechanics">
            {l10n.getString('settings-sidebar-tracker_mechanics')}
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="fksettings">
            {l10n.getString('settings-sidebar-fk_settings')}
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="gestureControl">
            {l10n.getString('settings-sidebar-gesture_control')}
          </SettingsLink>
        </div>
      </div>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">
          {l10n.getString('settings-sidebar-interface')}
        </Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/interface" scrollTo="notifications">
            {l10n.getString('settings-sidebar-notifications')}
          </SettingsLink>
          <SettingsLink to="/settings/interface" scrollTo="appearance">
            {l10n.getString('settings-sidebar-appearance')}
          </SettingsLink>
        </div>
        <div className="flex flex-col gap-3">
          <Typography variant="section-title">OSC</Typography>
          <div className="flex flex-col gap-2">
            <SettingsLink to="/settings/osc/router" scrollTo="router">
              {l10n.getString('settings-sidebar-osc_router')}
            </SettingsLink>
            <SettingsLink to="/settings/osc/vrchat" scrollTo="vrchat">
              {l10n.getString('settings-sidebar-osc_trackers')}
            </SettingsLink>
            <SettingsLink to="/settings/osc/vmc" scrollTo="vmc">
              VMC
            </SettingsLink>
          </div>
        </div>
        <div className="flex flex-col gap-3">
          <Typography variant="section-title">
            {l10n.getString('settings-sidebar-utils')}
          </Typography>
          <div className="flex flex-col gap-2">
            <SettingsLink to="/settings/serial">
              {l10n.getString('settings-sidebar-serial')}
            </SettingsLink>
          </div>
          <div className="flex flex-col gap-2">
            <Button variant="primary" onClick={() => setSkipWarning(true)}>
              {l10n.getString('settings-sidebar-reset')}
            </Button>
            <SettingsResetModal
              accept={() => {
                const guiSettings = getGUIDefaults();
                const serverSettings = getServerDefaults();

                // Server settings
                sendRPCPacket(RpcMessage.ChangeSettingsRequest, serverSettings);

                // GUI settings
                setConfig(guiSettings);
              }}
              onClose={() => setSkipWarning(false)}
              isOpen={skipWarning}
            ></SettingsResetModal>
          </div>
        </div>
      </div>
    </div>
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
    doneOnboarding: defaultGUIConfig.doneOnboarding,
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
