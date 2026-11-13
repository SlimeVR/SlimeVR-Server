import { useState } from 'react';
import { GapEvent, useTelemetryHistory } from './telemetry-history';
import {
  ChartRow,
  downsampleIndices,
  lowerBoundTime,
  rowsSource,
  sourceNow,
  useLiveFrame,
} from './time-series';

export interface TrackerDisplayStat {
  rssi: number | null;
}

export interface DongleTelemetryFeed {
  chartData: ChartRow[];
  gapEvents: Record<number, GapEvent[]>;
  displayStats: Record<number, TrackerDisplayStat>;
  nowSec: number;
}

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

export function useDongleTelemetryFeed(
  trackerIds: number[],
  visibleIds: number[],
  windowSec: number,
  live: boolean
): DongleTelemetryFeed {
  const { getChartRows, getGapEvents, clock } = useTelemetryHistory(trackerIds);

  const [feedState, setFeedState] = useState<DongleTelemetryFeed>(() => ({
    chartData: [],
    gapEvents: {},
    displayStats: {},
    nowSec: Date.now() / 1000,
  }));

  useLiveFrame(live, () => {
    const now = sourceNow(clock);
    const rows = getChartRows();
    const source = rowsSource(rows);
    const from = lowerBoundTime(source, now - windowSec - 5);
    const sampledRows = downsampleIndices(
      source,
      from,
      windowSec,
      MAX_DISPLAY_POINTS,
      (i) => rowLossPeak(rows[i])
    ).map((i) => rows[i]);

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
  });

  return feedState;
}
