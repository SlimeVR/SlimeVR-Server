import { GapEvent } from '@/hooks/telemetry-history';
import { ChartRow } from '@/hooks/dongle-telemetry-feed';
import {
  gapSeverityTier,
  isGapEventActive,
  TelemetryTracker,
} from './DongleTelemetry';

export const TOOLTIP_WIDTH = 380;
export const TOOLTIP_OFFSET = 10;
export const TOOLTIP_ROW_HEIGHT = 21;
export const TOOLTIP_HEADER_HEIGHT = 32;

export function estimatedTooltipHeight(trackerCount: number): number {
  return TOOLTIP_HEADER_HEIGHT + trackerCount * TOOLTIP_ROW_HEIGHT;
}

function lowerBound(chartData: ChartRow[], t: number): number {
  let lo = 0;
  let hi = chartData.length;
  while (lo < hi) {
    const mid = (lo + hi) >> 1;
    if (chartData[mid].t < t) lo = mid + 1;
    else hi = mid;
  }
  return lo;
}

export function nearestValue(
  chartData: ChartRow[],
  t: number,
  key: string
): number | null {
  if (chartData.length === 0) return null;
  const idx = lowerBound(chartData, t);
  const atOrBeforeStart = chartData[idx]?.t === t ? idx : idx - 1;

  for (let i = atOrBeforeStart; i >= 0; i--) {
    const v = chartData[i][key];
    if (v != null) return v;
  }
  for (let i = atOrBeforeStart + 1; i < chartData.length; i++) {
    const v = chartData[i][key];
    if (v != null) return v;
  }
  return null;
}

const GAP_SEVERITY_CLASS: Record<ReturnType<typeof gapSeverityTier>, string> = {
  critical: 'text-status-critical',
  warning: 'text-status-warning',
  mild: 'text-status-warning/85',
};

export function TelemetryTooltipTable({
  trackers,
  chartData,
  hoveredTime,
  eventsByTracker,
}: {
  trackers: TelemetryTracker[];
  chartData: ChartRow[];
  hoveredTime: number;
  eventsByTracker: Record<number, GapEvent[]>;
}) {
  return (
    <table className="w-full text-xs table-fixed">
      <colgroup>
        <col style={{ width: 16 }} />
        <col />
        <col style={{ width: 120 }} />
        <col style={{ width: 46 }} />
        <col style={{ width: 46 }} />
      </colgroup>
      <thead>
        <tr className="border-b border-background-10/10 text-background-40 text-left">
          <th className="pb-1 font-semibold" colSpan={2}>
            Tracker
          </th>
          <th className="pb-1 font-semibold text-right">RSSI</th>
          <th className="pb-1 font-semibold text-right">Loss</th>
          <th className="pb-1 font-semibold text-right">Gap</th>
        </tr>
      </thead>
      <tbody>
        {trackers.map((t) => {
          const rssiAvg = nearestValue(
            chartData,
            hoveredTime,
            `${t.deviceId}_avg`
          );
          const rssiMin = nearestValue(
            chartData,
            hoveredTime,
            `${t.deviceId}_min`
          );
          const rssiMax = nearestValue(
            chartData,
            hoveredTime,
            `${t.deviceId}_max`
          );
          const lossPct = nearestValue(
            chartData,
            hoveredTime,
            `${t.deviceId}_loss`
          );
          const gapEvent = (eventsByTracker[t.deviceId] ?? []).find((ev) =>
            isGapEventActive(ev, hoveredTime)
          );
          if (rssiAvg == null && lossPct == null && !gapEvent) return null;
          const minVal = rssiMin != null ? rssiMin.toFixed(0) : '--';
          const maxVal = rssiMax != null ? rssiMax.toFixed(0) : '--';
          return (
            <tr key={t.deviceId}>
              <td className="py-1">
                <span
                  className="w-2 h-2 rounded-full inline-block"
                  style={{ background: t.color }}
                />
              </td>
              <td className="text-background-20 py-1 truncate">{t.name}</td>
              <td className="text-right font-bold font-mono text-background-10 tabular-nums py-1 whitespace-nowrap">
                {rssiAvg != null
                  ? `${rssiAvg.toFixed(0)}dBm (${minVal}/${maxVal})`
                  : '--'}
              </td>
              <td className="text-right font-bold font-mono text-status-warning tabular-nums py-1 whitespace-nowrap">
                {lossPct != null ? `${lossPct.toFixed(1)}%` : '--'}
              </td>
              <td
                className={`text-right font-bold font-mono tabular-nums py-1 pl-1 whitespace-nowrap ${gapEvent ? GAP_SEVERITY_CLASS[gapSeverityTier(gapEvent.durationMs)] : 'text-background-30'}`}
              >
                {gapEvent ? `${gapEvent.durationMs}ms` : '--'}
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
