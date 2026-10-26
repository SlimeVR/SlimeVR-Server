import {
  ChangeSkeletonSettingsRequestT,
  SkeletonSettingsRequestT,
  SkeletonSettingsResponseT,
} from 'solarxr-protocol';
import { settingsDefaults } from '@/domain/settings/settings-defaults';
import { defineSettings } from '@/solarxr/settings';

export const skeletonSettings = defineSettings({
  request: () => new SkeletonSettingsRequestT(),
  response: SkeletonSettingsResponseT,
  change: ChangeSkeletonSettingsRequestT,
});

export const skeletonDefaults = () => settingsDefaults.value()?.skeleton;
