import {
  ChangeResetsSettingsRequestT,
  ChangeTapDetectionSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  TapDetectionSettingsRequestT,
  TapDetectionSettingsResponseT,
} from 'solarxr-protocol';
import { settingsDefaults } from '@/domain/settings/settings-defaults';
import { defineSettings } from '@/solarxr/settings';

export const resetsSettings = defineSettings({
  request: () => new ResetsSettingsRequestT(),
  response: ResetsSettingsResponseT,
  change: ChangeResetsSettingsRequestT,
});

export const tapDetectionSettings = defineSettings({
  request: () => new TapDetectionSettingsRequestT(),
  response: TapDetectionSettingsResponseT,
  change: ChangeTapDetectionSettingsRequestT,
});

export const resetsDefaults = () => settingsDefaults.value()?.resets;
export const tapDetectionDefaults = () => settingsDefaults.value()?.tapDetection;
