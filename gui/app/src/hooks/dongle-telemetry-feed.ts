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

function downsampleChartRows(
  rows: ChartRow[],
  windowSec: number,
  maxPoints = MAX_DISPLAY_POINTS
): ChartRow[] {
  if (rows.length <= maxPoints) return rows;

  const slotSec = Math.max(0.1, windowSec / maxPoints);
  const result: ChartRow[] = [];
  let lastSlot = -1;

  for (let i = 0; i < rows.length; i++) {
    const row = rows[i];
    const slot = Math.floor(row.t / slotSec);
    if (slot !== lastSlot) {
      result.push(row);
      lastSlot = slot;
    }
  }

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
