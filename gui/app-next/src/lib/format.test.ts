import { beforeEach, describe, expect, it, vi } from 'vitest';
import {
  EMPTY,
  fmt,
  fmtLength,
  fmtList,
  fmtRelative,
  formats,
  lengthUnit,
} from '@/lib/format';
import { loadLocale } from '@/lib/l10n';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';

const FT = '\u2032';
const IN = '\u2033';

// Unit patterns use no-break spaces, compare with plain ones
function plain(text: string) {
  return text.replace(/\s/g, ' ');
}

async function switchLocale(tag: string) {
  await loadLocale([tag]);
}

beforeEach(() => {
  setPlatform(fakePlatform());
  vi.stubGlobal('fetch', async () => new Response('', { status: 200 }));
  lengthUnit.set('metric');
});

describe('fmt', () => {
  it('formats per locale', async () => {
    await switchLocale('en');
    expect(plain(fmt(formats.meters, 1.754))).toBe('1.75 m');
    await switchLocale('fr');
    expect(plain(fmt(formats.meters, 1.754))).toBe('1,75 m');
    await switchLocale('de');
    expect(plain(fmt(formats.degrees, 12))).toBe('12,0°');
  });

  it('renders missing values as --', async () => {
    await switchLocale('en');
    expect(fmt(formats.seconds, null)).toBe(EMPTY);
    expect(fmt(formats.seconds, undefined)).toBe(EMPTY);
    expect(fmt(formats.seconds, Number.NaN)).toBe(EMPTY);
  });

  it('appends suffix and sign', async () => {
    await switchLocale('en');
    expect(plain(fmt(formats.volts, 4.1))).toBe('4.10 V');
    expect(fmt(formats.offset, 0.5)).toBe('+0.5');
    expect(fmt(formats.offset, 0)).toBe('0');
  });
});

describe('fmtList', () => {
  it('joins per locale', async () => {
    await switchLocale('en');
    expect(fmtList(['a', 'b', 'c'])).toBe('a, b, and c');
    await switchLocale('fr');
    expect(fmtList(['a', 'b'])).toBe('a et b');
  });
});

describe('fmtRelative', () => {
  it('picks seconds, minutes, hours', async () => {
    await switchLocale('en');
    const now = 10_000_000;
    expect(fmtRelative(now - 5_000, now)).toBe('5 seconds ago');
    expect(fmtRelative(BigInt(now - 120_000), now)).toBe('2 minutes ago');
    expect(fmtRelative(now - 3 * 3_600_000, now)).toBe('3 hours ago');
    expect(fmtRelative(null, now)).toBe(EMPTY);
  });
});

describe('fmtLength', () => {
  it('formats metric', async () => {
    await switchLocale('fr');
    expect(plain(fmtLength(1.75, 'height'))).toBe('1,75 m');
    await switchLocale('en');
    expect(plain(fmtLength(0.45, 'segment'))).toBe('45 cm');
    expect(plain(fmtLength(0.01, 'offset'))).toBe('+1 cm');
  });

  it('formats imperial height as feet and inches', async () => {
    await switchLocale('en');
    lengthUnit.set('imperial');
    expect(plain(fmtLength(1.75, 'height'))).toBe(`5${FT} 9${IN}`);
    // 71.9 inches rounds to 6 feet, never 5 feet 12 inches
    expect(plain(fmtLength(1.826, 'height'))).toBe(`6${FT} 0${IN}`);
    expect(plain(fmtLength(0.45, 'segment'))).toBe(`17.7${IN}`);
    expect(plain(fmtLength(0.0254, 'offset'))).toBe(`+1${IN}`);
    expect(fmtLength(null, 'height')).toBe(EMPTY);
  });
});
