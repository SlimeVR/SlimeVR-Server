import { useEffect, useRef } from 'react';

export interface ChartRow {
  t: number;
  [key: string]: number | null;
}

export interface ChartSource {
  readonly length: number;
  time(i: number): number;
  value(key: string, i: number): number | null;
}

export function rowsSource(rows: ChartRow[]): ChartSource {
  return {
    length: rows.length,
    time: (i) => rows[i].t,
    value: (key, i) => rows[i][key] ?? null,
  };
}

/** First index whose time is >= t */
export function lowerBoundTime(source: ChartSource, t: number): number {
  let lo = 0;
  let hi = source.length;
  while (lo < hi) {
    const mid = (lo + hi) >> 1;
    if (source.time(mid) < t) lo = mid + 1;
    else hi = mid;
  }
  return lo;
}

export function nearestIndex(source: ChartSource, t: number): number {
  if (source.length === 0) return -1;
  const lo = lowerBoundTime(source, t);
  if (lo === 0) return 0;
  if (lo >= source.length) return source.length - 1;
  const d1 = Math.abs(source.time(lo - 1) - t);
  const d2 = Math.abs(source.time(lo) - t);
  return d1 < d2 ? lo - 1 : lo;
}

/** One index per time slot, the highest `score` wins so spikes survive */
export function downsampleIndices(
  source: ChartSource,
  from: number,
  windowSec: number,
  maxPoints: number,
  score: (i: number) => number | null
): number[] {
  const result: number[] = [];
  const end = source.length;
  if (end - from <= maxPoints) {
    for (let i = from; i < end; i++) result.push(i);
    return result;
  }

  const slotSec = Math.max(0.01, windowSec / maxPoints);

  const flushSlot = (start: number, stop: number) => {
    let best = start;
    let bestScore = score(start);
    for (let i = start + 1; i < stop; i++) {
      const s = score(i);
      if (s != null && (bestScore == null || s > bestScore)) {
        best = i;
        bestScore = s;
      }
    }
    result.push(best);
  };

  let slotStart = from;
  let currentSlot = Math.floor(source.time(from) / slotSec);
  for (let i = from + 1; i < end; i++) {
    const slot = Math.floor(source.time(i) / slotSec);
    if (slot !== currentSlot) {
      flushSlot(slotStart, i);
      slotStart = i;
      currentSlot = slot;
    }
  }
  flushSlot(slotStart, end);

  return result;
}

export function downsampleMinMax(
  source: ChartSource,
  from: number,
  windowSec: number,
  maxSlots: number,
  keys: readonly string[]
): ChartSource {
  const slotSec = windowSec / maxSlots;
  const end = source.length;

  const slotStarts: number[] = [];
  let currentSlot = NaN;
  for (let i = from; i < end; i++) {
    const slot = Math.floor(source.time(i) / slotSec);
    if (slot !== currentSlot) {
      slotStarts.push(i);
      currentSlot = slot;
    }
  }
  slotStarts.push(end);

  const slotCount = slotStarts.length - 1;
  const times = new Float64Array(slotCount * 2);
  const columns = new Map<string, Float32Array>();
  keys.forEach((key) => columns.set(key, new Float32Array(slotCount * 2)));

  for (let s = 0; s < slotCount; s++) {
    const start = slotStarts[s];
    const stop = slotStarts[s + 1];
    times[s * 2] = source.time(start);
    times[s * 2 + 1] = source.time(stop - 1);

    columns.forEach((col, key) => {
      let minIdx = -1;
      let maxIdx = -1;
      let min = Infinity;
      let max = -Infinity;
      for (let i = start; i < stop; i++) {
        const v = source.value(key, i);
        if (v == null) continue;
        if (v < min) {
          min = v;
          minIdx = i;
        }
        if (v > max) {
          max = v;
          maxIdx = i;
        }
      }
      if (minIdx < 0) {
        col[s * 2] = NaN;
        col[s * 2 + 1] = NaN;
      } else if (minIdx <= maxIdx) {
        col[s * 2] = min;
        col[s * 2 + 1] = max;
      } else {
        col[s * 2] = max;
        col[s * 2 + 1] = min;
      }
    });
  }

  return {
    length: slotCount * 2,
    time: (i) => times[i],
    value: (key, i) => {
      const v = columns.get(key)?.[i];
      return v == null || Number.isNaN(v) ? null : v;
    },
  };
}

export interface ColumnRing {
  readonly capacity: number;
  readonly columnOf: Map<string, number>;
  readonly times: Float64Array;
  readonly columns: Float32Array[];
  start: number;
  length: number;
}

export function createColumnRing(
  keys: readonly string[],
  capacity: number
): ColumnRing {
  return {
    capacity,
    columnOf: new Map(keys.map((key, i) => [key, i])),
    times: new Float64Array(capacity),
    columns: keys.map(() => new Float32Array(capacity)),
    start: 0,
    length: 0,
  };
}

export function clearColumnRing(ring: ColumnRing) {
  ring.start = 0;
  ring.length = 0;
}

export function pushColumnRing(ring: ColumnRing, t: number, values: (number | null)[]) {
  let slot: number;
  if (ring.length < ring.capacity) {
    slot = (ring.start + ring.length) % ring.capacity;
    ring.length++;
  } else {
    slot = ring.start;
    ring.start = (ring.start + 1) % ring.capacity;
  }
  ring.times[slot] = t;
  for (let c = 0; c < ring.columns.length; c++) {
    ring.columns[c][slot] = values[c] ?? NaN;
  }
}

export function columnRingSource(ring: ColumnRing): ChartSource {
  return {
    get length() {
      return ring.length;
    },
    time: (i) => ring.times[(ring.start + i) % ring.capacity],
    value: (key, i) => {
      const col = ring.columnOf.get(key);
      if (col == null) return null;
      const v = ring.columns[col][(ring.start + i) % ring.capacity];
      return Number.isNaN(v) ? null : v;
    },
  };
}

/** Offset from local time to the server clock that stamped the samples */
export interface SourceClock {
  offsetSec: number | null;
}

const CLOCK_RESYNC_SEC = 1;
const CLOCK_SMOOTHING = 0.05;

export function createSourceClock(): SourceClock {
  return { offsetSec: null };
}

export function observeSourceTime(
  clock: SourceClock,
  sampleSec: number,
  localSec = Date.now() / 1000
) {
  const measured = sampleSec - localSec;
  const current = clock.offsetSec;
  if (current == null || Math.abs(measured - current) > CLOCK_RESYNC_SEC) {
    clock.offsetSec = measured;
    return;
  }
  clock.offsetSec = current + CLOCK_SMOOTHING * (measured - current);
}

export function sourceNow(clock: SourceClock, localSec = Date.now() / 1000) {
  return localSec + (clock.offsetSec ?? 0);
}

const TARGET_FPS = 20;
const FRAME_INTERVAL_MS = 1000 / TARGET_FPS;

export function useLiveFrame(live: boolean, onFrame: (nowSec: number) => void) {
  const onFrameRef = useRef(onFrame);
  onFrameRef.current = onFrame;

  useEffect(() => {
    if (!live) return;

    let animId: number;
    let lastRenderTime = 0;

    const renderFrame = () => {
      const nowMs = Date.now();
      if (nowMs - lastRenderTime >= FRAME_INTERVAL_MS) {
        lastRenderTime = nowMs;
        onFrameRef.current(nowMs / 1000);
      }
      animId = requestAnimationFrame(renderFrame);
    };

    animId = requestAnimationFrame(renderFrame);
    return () => cancelAnimationFrame(animId);
  }, [live]);
}
