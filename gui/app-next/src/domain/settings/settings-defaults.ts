import { SettingsDefaultsRequestT, SettingsDefaultsResponseT } from 'solarxr-protocol';
import { defineResource } from '@/solarxr/resource';

// Server side defaults of every settings group, for reset buttons
export const settingsDefaults = defineResource({
  request: () => new SettingsDefaultsRequestT(),
  response: SettingsDefaultsResponseT,
});
