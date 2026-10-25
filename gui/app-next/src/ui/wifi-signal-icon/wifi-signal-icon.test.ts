import { describe, expect, it } from 'vitest';
import { levelColor } from '@/ui/battery-icon';
import { signalLevel } from '@/ui/wifi-signal-icon';

describe('signalLevel', () => {
  it('maps -95..-40 dBm to 0..1, clamped', () => {
    expect(signalLevel(-95)).toBe(0);
    expect(signalLevel(-40)).toBe(1);
    expect(signalLevel(-67.5)).toBeCloseTo(0.5);
    expect(signalLevel(-120)).toBe(0);
    expect(signalLevel(-10)).toBe(1);
  });

  it('reads unknown as empty', () => {
    expect(signalLevel(null)).toBe(0);
    expect(signalLevel(undefined)).toBe(0);
  });
});

describe('levelColor', () => {
  it('uses the old thresholds', () => {
    expect(levelColor(0.5)).toBe('text-success');
    expect(levelColor(0.4)).toBe('text-warning');
    expect(levelColor(0.2)).toBe('text-critical');
    expect(levelColor(0)).toBe('text-fg');
  });
});
