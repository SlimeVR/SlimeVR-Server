import { useEffect, useState } from 'react';
import { ChartRow, GapEvent, useTelemetryHistory } from './telemetry-history';

export interface TrackerDisplayStat {
  rssi: number | null;
}

export interface DongleTelemetryFeed {
  chartData: ChartRow[];
  gapEvents: Record<number, GapEvent[]>;
  displayStats: Record<number, TrackerDisplayStat>;
  nowSec: number;
}

const TARGET_FPS = 20;
const FRAME_INTERVAL_MS = 1000 / TARGET_FPS; // 50ms interval for smooth 20 FPS rolling
const MAX_DISPLAY_POINTS = 250; // Cap rendered chart points to guarantee flat, constant CPU usage over time

/** Highest loss any tracker reports in this row, null when none of them reported one */
function rowLossPeak(row: ChartRow): number | null {
  let peak: number | null = null;
  for (const key in row) {
    if (!key.endsWith('_loss')) continue;
    const val = row[key];
    if (val != null && (peak == null || val > peak)) peak = val;
  }
  return peak;
}

function downsampleChartRows(
  rows: ChartRow[],
  windowSec: number,
  maxPoints = MAX_DISPLAY_POINTS
): ChartRow[] {
  if (rows.length <= maxPoints) return rows;

  const slotSec = Math.max(0.1, windowSec / maxPoints);
  const result: ChartRow[] = [];

  // The lossiest row wins its slot, so decimation cannot swallow a spike
  const flushSlot = (start: number, end: number) => {
    let best = rows[start];
    let bestPeak = rowLossPeak(best);
    for (let i = start + 1; i < end; i++) {
      const peak = rowLossPeak(rows[i]);
      if (peak != null && (bestPeak == null || peak > bestPeak)) {
        best = rows[i];
        bestPeak = peak;
      }
    }
    result.push(best);
  };

  let slotStart = 0;
  let currentSlot = Math.floor(rows[0].t / slotSec);
  for (let i = 1; i < rows.length; i++) {
    const slot = Math.floor(rows[i].t / slotSec);
    if (slot !== currentSlot) {
      flushSlot(slotStart, i);
      slotStart = i;
      currentSlot = slot;
    }
  }
  flushSlot(slotStart, rows.length);

  return result;
}

export function useDongleTelemetryFeed(
  trackerIds: number[],
  visibleIds: number[],
  windowSec: number,
  live: boolean
): DongleTelemetryFeed {
  const { getChartRows, getGapEvents, lastUpdateTick } =
    useTelemetryHistory(trackerIds);

  const [feedState, setFeedState] = useState<DongleTelemetryFeed>(() => ({
    chartData: [],
    gapEvents: {},
    displayStats: {},
    nowSec: Date.now() / 1000,
  }));

  useEffect(() => {
    if (!live) return;

    let animId: number;
    let lastRenderTime = 0;

    const renderFrame = () => {
      const nowMs = Date.now();
      if (nowMs - lastRenderTime >= FRAME_INTERVAL_MS) {
        lastRenderTime = nowMs;
        const now = nowMs / 1000;

        const rows = getChartRows();
        const cutoff = now - windowSec - 5;
        const windowRows = rows.filter((r) => r.t >= cutoff);
        const sampledRows = downsampleChartRows(
          windowRows,
          windowSec,
          MAX_DISPLAY_POINTS
        );

        const nextGapEvents: Record<number, GapEvent[]> = {};
        const nextStats: Record<number, TrackerDisplayStat> = {};
        const visible = new Set(visibleIds);
        const lastRow = rows.length > 0 ? rows[rows.length - 1] : null;

        trackerIds.forEach((deviceId) => {
          const rssiVal = lastRow ? (lastRow[`${deviceId}_avg`] ?? null) : null;
          nextStats[deviceId] = { rssi: rssiVal };

          if (visible.has(deviceId)) {
            nextGapEvents[deviceId] = getGapEvents(deviceId, windowSec, now);
          }
        });

        setFeedState({
          chartData: sampledRows,
          gapEvents: nextGapEvents,
          displayStats: nextStats,
          nowSec: now,
        });
      }

      animId = requestAnimationFrame(renderFrame);
    };

    animId = requestAnimationFrame(renderFrame);
    return () => cancelAnimationFrame(animId);
  }, [
    trackerIds,
    visibleIds,
    windowSec,
    live,
    lastUpdateTick,
    getChartRows,
    getGapEvents,
  ]);

  return feedState;
}
