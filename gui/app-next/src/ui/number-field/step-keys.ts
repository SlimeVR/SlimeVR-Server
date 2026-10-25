export interface StepRange {
  value: number;
  min: number;
  max: number;
  step: number;
  bigStep?: number;
}

function clamp(value: number, min: number, max: number) {
  return Math.min(max, Math.max(min, value));
}

export function stepValue(range: StepRange, by: number): number {
  const next = Math.round((range.value + by) * 1e9) / 1e9;
  return clamp(next, range.min, range.max);
}

export function stepKeys(key: string, range: StepRange): number | undefined {
  const big = range.bigStep ?? range.step;
  switch (key) {
    case 'ArrowUp':
    case 'ArrowRight':
      return stepValue(range, range.step);
    case 'ArrowDown':
    case 'ArrowLeft':
      return stepValue(range, -range.step);
    case 'PageUp':
      return stepValue(range, big);
    case 'PageDown':
      return stepValue(range, -big);
    case 'Home':
      return range.min;
    case 'End':
      return range.max;
    default:
      return undefined;
  }
}
