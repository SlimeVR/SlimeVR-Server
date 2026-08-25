import { useEffect, useRef, useState } from 'react';
import {
  computeGapEvents,
  computeLossPctSeries,
  computeRssiStats,
  GapEvent,
  useTelemetryHistory,
} from './telemetry-history';

export interface ChartRow {
  t: number;
  [key: string]: number | null;
}

export interface TrackerDisplayStat {
  rssi: number | null;
}

export interface DongleTelemetryFeed {
  chartData: ChartRow[];
  gapEvents: Record<number, GapEvent[]>;
  displayStats: Record<number, TrackerDisplayStat>;
  nowSec: number;
}

/**
 * Polls `useTelemetryHistory` at ~10Hz and recomputes chart rows, gap events,
 * and summary stats for the given trackers. `trackerIds` is read through a
 * ref so the animation loop keeps running across renders without needing the
 * array to be referentially stable.
 */
export function useDongleTelemetryFeed(
  trackerIds: number[],
  windowSec: number,
  live: boolean
): DongleTelemetryFeed {
  const { getSamples } = useTelemetryHistory();

  const trackerIdsKey = trackerIds
    .slice()
    .sort((a, b) => a - b)
    .join(',');
  const trackerIdsRef = useRef(trackerIds);
  trackerIdsRef.current = trackerIds;

  const [chartData, setChartData] = useState<ChartRow[]>([]);
  const [gapEvents, setGapEvents] = useState<Record<number, GapEvent[]>>({});
  const [displayStats, setDisplayStats] = useState<Record<number, TrackerDisplayStat>>(
    {}
  );
  const [nowSec, setNowSec] = useState(() => Date.now() / 1000);

  useEffect(() => {
    if (!live) return;

    let animId: number;
    let lastCompute = 0;

    const tick = () => {
      const now = Date.now();
      const nowSecVal = now / 1000;
      setNowSec(nowSecVal);

      if (now - lastCompute >= 100) {
        lastCompute = now;
        const ids = trackerIdsRef.current;

        if (ids.length > 0) {
          const nextGapEvents: Record<number, GapEvent[]> = {};
          const nextStats: Record<number, TrackerDisplayStat> = {};
          const timeRowsMap = new Map<number, ChartRow>();

          const getRow = (t: number) => {
            let row = timeRowsMap.get(t);
            if (!row) {
              row = { t };
              timeRowsMap.set(t, row);
            }
            return row;
          };

          const liveRow = getRow(nowSecVal);

          ids.forEach((deviceId) => {
            const samples = getSamples(deviceId);

            computeRssiStats(samples, windowSec).forEach((b) => {
              const row = getRow(b.t);
              row[`${deviceId}_avg`] = b.avg;
              row[`${deviceId}_min`] = b.min;
              row[`${deviceId}_max`] = b.max;
            });

            computeLossPctSeries(samples, windowSec).forEach((b) => {
              getRow(b.t)[`${deviceId}_loss`] = b.lossPct;
            });

            nextGapEvents[deviceId] = computeGapEvents(samples, windowSec);

            const last = samples[samples.length - 1];
            nextStats[deviceId] = { rssi: last?.rssi ?? null };
            if (last) {
              liveRow[`${deviceId}_avg`] = last.rssi;
              liveRow[`${deviceId}_min`] = last.rssiMin ?? last.rssi;
              liveRow[`${deviceId}_max`] = last.rssiMax ?? last.rssi;
              liveRow[`${deviceId}_loss`] =
                last.packetLoss != null ? last.packetLoss * 100 : null;
            }
          });

          const nextChartData = Array.from(timeRowsMap.values()).sort(
            (a, b) => a.t - b.t
          );

          setChartData(nextChartData);
          setGapEvents(nextGapEvents);
          setDisplayStats(nextStats);
        }
      }

      animId = requestAnimationFrame(tick);
    };

    animId = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(animId);
  }, [windowSec, live, trackerIdsKey, getSamples]);

  return { chartData, gapEvents, displayStats, nowSec };
}
