import * as v from 'valibot';
import { locale } from '@/lib/l10n';
import { definePref } from '@/lib/storage';

export type NumberSpec = Intl.NumberFormatOptions & { suffix?: string };

export const EMPTY = '--';

export const formats = {
  seconds: {
    style: 'unit',
    unit: 'second',
    unitDisplay: 'narrow',
    maximumFractionDigits: 2,
  },
  percent: { style: 'percent', maximumFractionDigits: 0 },
  degrees: {
    style: 'unit',
    unit: 'degree',
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  },
  meters: {
    style: 'unit',
    unit: 'meter',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  },
  volts: { suffix: 'V', minimumFractionDigits: 2, maximumFractionDigits: 2 },
  dbm: { suffix: 'dBm', maximumFractionDigits: 0 },
  offset: { signDisplay: 'exceptZero', maximumFractionDigits: 2 },
} satisfies Record<string, NumberSpec>;

// Specs are module constants, so the spec object is the cache key
const numberCache = new WeakMap<NumberSpec, Map<string, Intl.NumberFormat>>();

// Reactive to locale. NumberField uses it for formatToParts.
export function numberFormatter(spec: NumberSpec): Intl.NumberFormat {
  const current = locale();
  let byLocale = numberCache.get(spec);
  if (!byLocale) {
    byLocale = new Map();
    numberCache.set(spec, byLocale);
  }
  let formatter = byLocale.get(current);
  if (!formatter) {
    const options = { ...spec };
    delete options.suffix;
    formatter = new Intl.NumberFormat(current, options);
    byLocale.set(current, formatter);
  }
  return formatter;
}

export function fmt(spec: NumberSpec, value: number | null | undefined): string {
  if (value == null || Number.isNaN(value)) return EMPTY;
  const text = numberFormatter(spec).format(value);
  return spec.suffix ? `${text} ${spec.suffix}` : text;
}

const listCache = new Map<string, Intl.ListFormat>();

export function fmtList(
  items: readonly string[],
  type: Intl.ListFormatType = 'conjunction'
): string {
  const key = `${locale()}|${type}`;
  let formatter = listCache.get(key);
  if (!formatter) {
    formatter = new Intl.ListFormat(locale(), { type });
    listCache.set(key, formatter);
  }
  return formatter.format(items);
}

const relativeCache = new Map<string, Intl.RelativeTimeFormat>();

// `now` comes from the caller's clock, this never ticks on its own
export function fmtRelative(
  timestampMs: number | bigint | null | undefined,
  now: number
): string {
  if (timestampMs == null) return EMPTY;
  let formatter = relativeCache.get(locale());
  if (!formatter) {
    formatter = new Intl.RelativeTimeFormat(locale(), { numeric: 'auto' });
    relativeCache.set(locale(), formatter);
  }
  const seconds = Math.max(0, Math.floor((now - Number(timestampMs)) / 1000));
  if (seconds < 60) return formatter.format(-seconds, 'second');
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return formatter.format(-minutes, 'minute');
  return formatter.format(-Math.floor(minutes / 60), 'hour');
}

export type LengthUnit = 'metric' | 'imperial';
export const lengthUnit = definePref<LengthUnit>(
  'units.length',
  v.picklist(['metric', 'imperial']),
  'metric'
);

export type LengthKind = 'height' | 'segment' | 'offset';

export interface LengthSpec {
  spec: NumberSpec;
  scale: number;
  step: number;
}

const METERS_PER_INCH = 0.0254;

const lengthSpecs: Record<LengthUnit, Record<LengthKind, LengthSpec>> = {
  metric: {
    height: { spec: formats.meters, scale: 1, step: 0.01 },
    segment: {
      spec: {
        style: 'unit',
        unit: 'centimeter',
        unitDisplay: 'short',
        maximumFractionDigits: 1,
      },
      scale: 100,
      step: 1,
    },
    offset: {
      spec: {
        style: 'unit',
        unit: 'centimeter',
        unitDisplay: 'short',
        signDisplay: 'exceptZero',
        maximumFractionDigits: 1,
      },
      scale: 100,
      step: 1,
    },
  },
  imperial: {
    // NumberField edits height as inches, the height page splits feet and inches
    height: {
      spec: {
        style: 'unit',
        unit: 'inch',
        unitDisplay: 'narrow',
        maximumFractionDigits: 0,
      },
      scale: 1 / METERS_PER_INCH,
      step: 1,
    },
    segment: {
      spec: {
        style: 'unit',
        unit: 'inch',
        unitDisplay: 'narrow',
        maximumFractionDigits: 1,
      },
      scale: 1 / METERS_PER_INCH,
      step: 0.5,
    },
    offset: {
      spec: {
        style: 'unit',
        unit: 'inch',
        unitDisplay: 'narrow',
        signDisplay: 'exceptZero',
        maximumFractionDigits: 1,
      },
      scale: 1 / METERS_PER_INCH,
      step: 0.5,
    },
  },
};

const feetSpec: NumberSpec = {
  style: 'unit',
  unit: 'foot',
  unitDisplay: 'narrow',
  maximumFractionDigits: 0,
};

export function lengthSpec(kind: LengthKind): LengthSpec {
  return lengthSpecs[lengthUnit()][kind];
}

export function fmtLength(meters: number | null | undefined, kind: LengthKind): string {
  if (meters == null || Number.isNaN(meters)) return EMPTY;
  const { spec, scale } = lengthSpec(kind);
  if (kind === 'height' && lengthUnit() === 'imperial') {
    const totalInches = Math.round(meters * scale);
    const feet = Math.floor(totalInches / 12);
    return `${fmt(feetSpec, feet)} ${fmt(spec, totalInches % 12)}`;
  }
  return fmt(spec, meters * scale);
}
