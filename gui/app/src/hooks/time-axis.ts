import { useMemo } from 'react';

export interface TimeWindowOption {
  sec: number;
  label: string;
  step: number;
}

export const WINDOW_OPTIONS: TimeWindowOption[] = [
  { sec: 10, label: '10s', step: 5 },
  { sec: 30, label: '30s', step: 10 },
  { sec: 60, label: '1m', step: 15 },
  { sec: 120, label: '2m', step: 30 },
  { sec: 300, label: '5m', step: 60 },
];

export function relativeTimeTick(v: number, endSec: number) {
  const diff = Math.round(v - endSec);
  if (diff === 0) return '0s';
  if (Math.abs(diff) >= 60 && Math.abs(diff) % 60 === 0) {
    return `${diff / 60}m`;
  }
  return `${diff}s`;
}

export function screenXToTimeSec(
  clientX: number,
  plotLeftScreen: number,
  plotWidth: number,
  startSec: number,
  endSec: number
): number {
  if (plotWidth <= 0) return startSec;
  const pct = Math.max(0, Math.min(1, (clientX - plotLeftScreen) / plotWidth));
  return startSec + pct * (endSec - startSec);
}

export function timeSecToScreenX(
  t: number,
  plotLeftScreen: number,
  plotWidth: number,
  startSec: number,
  endSec: number
): number {
  if (endSec <= startSec) return plotLeftScreen;
  const pct = Math.max(0, Math.min(1, (t - startSec) / (endSec - startSec)));
  return plotLeftScreen + pct * plotWidth;
}

export function flipTooltipLeft(
  clientX: number,
  screenWidth: number,
  tooltipWidth: number,
  offset: number
): number {
  const wouldOverflow = clientX + offset + tooltipWidth > screenWidth;
  return wouldOverflow ? clientX - offset - tooltipWidth : clientX + offset;
}

/** Ticks snapped to `stepSec` so labels slide with the data */
export function useTimeAxisTicks(
  endSec: number,
  windowSec: number,
  stepSec: number
): number[] {
  const fixedEndTick = Math.floor(endSec / stepSec) * stepSec;
  return useMemo(() => {
    const ticks: number[] = [];
    for (let t = fixedEndTick - windowSec; t <= fixedEndTick + stepSec; t += stepSec) {
      ticks.push(t);
    }
    return ticks;
  }, [fixedEndTick, windowSec, stepSec]);
}

function niceStep(rough: number): number {
  const exp = Math.floor(Math.log10(rough));
  const base = Math.pow(10, exp);
  const frac = rough / base;
  if (frac <= 1) return base;
  if (frac <= 2) return 2 * base;
  if (frac <= 5) return 5 * base;
  return 10 * base;
}

export function niceTicks(
  min: number,
  max: number,
  count = 5
): { min: number; max: number; ticks: number[] } {
  if (!(max > min)) return { min, max, ticks: [min] };
  const step = niceStep((max - min) / Math.max(1, count - 1));
  const niceMin = Math.floor(min / step) * step;
  const niceMax = Math.ceil(max / step) * step;
  const ticks: number[] = [];
  for (let v = niceMin; v <= niceMax + step / 2; v += step) {
    // Strips float drift
    ticks.push(Number(v.toFixed(10)));
  }
  return { min: niceMin, max: niceMax, ticks };
}
