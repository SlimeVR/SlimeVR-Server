import * as v from 'valibot';
import { definePref } from '@/lib/storage';

const devSettingsSchema = v.looseObject({
  highContrast: v.optional(v.boolean(), false),
  preciseRotation: v.optional(v.boolean(), false),
  fastDataFeed: v.optional(v.boolean(), false),
  rawSlimeRotation: v.optional(v.boolean(), false),
});

export type DevSettings = v.InferOutput<typeof devSettingsSchema>;
export type DevFlag =
  'highContrast' | 'preciseRotation' | 'fastDataFeed' | 'rawSlimeRotation';

export const debugPref = definePref('debug', v.boolean(), false);

export const devSettingsPref = definePref('devSettings', devSettingsSchema, {
  highContrast: false,
  preciseRotation: false,
  fastDataFeed: false,
  rawSlimeRotation: false,
});

export function devSetting(key: DevFlag): boolean {
  return debugPref() && devSettingsPref()[key] === true;
}
