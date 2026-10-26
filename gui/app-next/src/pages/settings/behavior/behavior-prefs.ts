import * as v from 'valibot';
import { definePref } from '@/lib/storage';

export const feedbackSoundPref = definePref('feedbackSound', v.boolean(), true);
export const feedbackSoundVolumePref = definePref(
  'feedbackSoundVolume',
  v.number(),
  0.5
);
export const connectedTrackersWarningPref = definePref(
  'connectedTrackersWarning',
  v.boolean(),
  true
);
// null until the user picked tray or exit on the first close
export const useTrayPref = definePref('useTray', v.nullable(v.boolean()), null);
export const discordPresencePref = definePref('discordPresence', v.boolean(), false);
export const bvhDirectoryPref = definePref(
  'bvhDirectory',
  v.nullable(v.string()),
  null
);
