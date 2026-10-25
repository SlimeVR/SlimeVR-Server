import * as v from 'valibot';
import { definePref } from '@/lib/storage';

const trackerDisplaySchema = v.looseObject({
  showBatteryVoltage: v.optional(v.boolean(), false),
  showNumericSignal: v.optional(v.boolean(), false),
});

export type TrackerDisplay = v.InferOutput<typeof trackerDisplaySchema>;

// Old config key `trackerDisplay`, edited on the Home layout settings
export const trackerDisplayPref = definePref('trackerDisplay', trackerDisplaySchema, {
  showBatteryVoltage: false,
  showNumericSignal: false,
});
