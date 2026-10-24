import { describe, expect, it } from 'vitest';
import { repeatInterval, resolveStick } from './gamepad';
import { motionTuning } from './tuning';

describe('resolveStick', () => {
  it('commits above the enter deadzone and holds down to the exit one', () => {
    expect(resolveStick(0.3, 0, null)).toBeNull();
    expect(resolveStick(0.4, 0, null)).toBe('right');
    expect(resolveStick(0.3, 0, 'right')).toBe('right');
    expect(resolveStick(0.2, 0, 'right')).toBeNull();
  });

  it('keeps the held axis on a diagonal until the other clearly leads', () => {
    expect(resolveStick(0.6, 0.7, 'right')).toBe('right');
    expect(resolveStick(0.4, 0.8, 'right')).toBe('down');
    expect(resolveStick(-0.7, -0.6, 'up')).toBe('up');
  });
});

describe('repeatInterval', () => {
  it('ramps down to the minimum', () => {
    expect(repeatInterval(0)).toBe(motionTuning.repeatStartMs);
    expect(repeatInterval(1)).toBeLessThan(motionTuning.repeatStartMs);
    expect(repeatInterval(100)).toBe(motionTuning.repeatMinMs);
  });
});
