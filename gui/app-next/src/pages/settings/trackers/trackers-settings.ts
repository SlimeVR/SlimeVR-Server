import {
  ChangeHIDSettingsRequestT,
  ChangeMagToggleRequestT,
  ChangeTimeoutSettingsRequestT,
  HIDSettingsRequestT,
  HIDSettingsResponseT,
  MagToggleRequestT,
  MagToggleResponseT,
  TimeoutSettingsRequestT,
  TimeoutSettingsResponseT,
} from 'solarxr-protocol';
import { settingsDefaults } from '@/domain/settings/settings-defaults';
import { defineKeyedSettings, defineSettings } from '@/solarxr/settings';

// Keyed by tracker id, 0 is the setting for every tracker
export const magSettings = defineKeyedSettings({
  request: (trackerId: number) => new MagToggleRequestT(trackerId),
  key: (msg) => msg.trackerId,
  response: MagToggleResponseT,
  change: ChangeMagToggleRequestT,
});

export const globalMagSettings = magSettings(0);

export const timeoutSettings = defineSettings({
  request: () => new TimeoutSettingsRequestT(),
  response: TimeoutSettingsResponseT,
  change: ChangeTimeoutSettingsRequestT,
});

export const hidSettings = defineSettings({
  request: () => new HIDSettingsRequestT(),
  response: HIDSettingsResponseT,
  change: ChangeHIDSettingsRequestT,
});

export const globalMagDefaults = () => settingsDefaults.value()?.mag;
export const timeoutDefaults = () => settingsDefaults.value()?.timeout;
export const hidDefaults = () => settingsDefaults.value()?.hid;
