import { ReactNode } from 'react';
import { SettingsPageDef } from './settings-definitions';
import {
  trackingSettingsPage,
  TrackingSettings,
} from './pages/TrackingSettings';
import {
  trackersSettingsPage,
  TrackersSettings,
} from './pages/TrackersSettings';
import { resetsSettingsPage, ResetsSettings } from './pages/ResetsSettings';
import {
  stayAlignedSettingsPage,
  StayAlignedSettings,
} from './pages/StayAlignedSettings';
import {
  appearanceSettingsPage,
  AppearanceSettings,
} from './pages/AppearanceSettings';
import {
  behaviorSettingsPage,
  BehaviorSettings,
} from './pages/BehaviorSettings';
import { homeSettingsPage } from './pages/components/HomeLayoutSettings';
import { HomeScreenSettings } from './pages/HomeScreenSettings';
import { checklistSettingsPage } from './pages/components/TrackingChecklistSettings';
import { ChecklistSettings } from './pages/ChecklistSettings';
import { keybindSettingsPage, KeybindSettings } from './pages/KeybindSettings';
import {
  profilesSettingsPage,
  ProfilesSettings,
} from './pages/ProfilesSettings';
import {
  boneRoutingSettingsPage,
  BoneRoutingSettings,
} from './pages/BoneRoutingSettings';
import { driverSettingsPage, DriverSettings } from './pages/DriverSettings';
import { vrchatOscSettingsPage, VRCOSCSettings } from './pages/VRCOSCSettings';
import { vmcSettingsPage, VMCSettings } from './pages/VMCSettings';
import { serialSettingsPage, Serial } from './pages/Serial';
import { logsSettingsPage, LogsSettings } from './pages/logs/LogsSettings';
import {
  advancedSettingsPage,
  AdvancedSettings,
} from './pages/AdvancedSettings';

import {
  firmwareToolSettingsPage,
  FirmwareToolSettings,
} from '@/components/firmware-tool/FirmwareTool';
import {
  vrcWarningsSettingsPage,
  VRCWarningsPage,
} from '@/components/vrc/VRCWarningsPage';

export interface SettingsPageRoute {
  page: SettingsPageDef;
  element: ReactNode;
}

export const settingsPages: SettingsPageRoute[] = [
  { page: trackingSettingsPage, element: <TrackingSettings /> },
  { page: trackersSettingsPage, element: <TrackersSettings /> },
  { page: resetsSettingsPage, element: <ResetsSettings /> },
  { page: stayAlignedSettingsPage, element: <StayAlignedSettings /> },
  { page: profilesSettingsPage, element: <ProfilesSettings /> },
  { page: appearanceSettingsPage, element: <AppearanceSettings /> },
  { page: behaviorSettingsPage, element: <BehaviorSettings /> },
  { page: homeSettingsPage, element: <HomeScreenSettings /> },
  { page: checklistSettingsPage, element: <ChecklistSettings /> },
  { page: keybindSettingsPage, element: <KeybindSettings /> },
  { page: boneRoutingSettingsPage, element: <BoneRoutingSettings /> },
  { page: driverSettingsPage, element: <DriverSettings /> },
  { page: vrchatOscSettingsPage, element: <VRCOSCSettings /> },
  { page: vmcSettingsPage, element: <VMCSettings /> },
  { page: serialSettingsPage, element: <Serial /> },
  { page: logsSettingsPage, element: <LogsSettings /> },
  { page: firmwareToolSettingsPage, element: <FirmwareToolSettings /> },
  { page: vrcWarningsSettingsPage, element: <VRCWarningsPage /> },
  { page: advancedSettingsPage, element: <AdvancedSettings /> },
];
