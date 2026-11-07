import { memo } from 'react';
import { GapEvent } from '@/hooks/telemetry-history';
import { ChartPopover } from '@/components/commons/charts/ChartPopover';
import { ChartRow, nearestIndex, rowsSource } from '@/hooks/time-series';
import {
  getGapTextColor,
  isGapEventActive,
  TelemetryTracker,
} from './DongleTelemetry';

export const TOOLTIP_WIDTH = 380;
export const TOOLTIP_ROW_HEIGHT = 21;
export const TOOLTIP_HEADER_HEIGHT = 32;

export function estimatedTooltipHeight(trackerCount: number): number {
  return TOOLTIP_HEADER_HEIGHT + trackerCount * TOOLTIP_ROW_HEIGHT;
}

export function nearestRow(chartData: ChartRow[], t: number): ChartRow | null {
  const i = nearestIndex(rowsSource(chartData), t);
  return i < 0 ? null : chartData[i];
}

function TelemetryTooltipTableComponent({
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
  const row = nearestRow(chartData, hoveredTime);

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
          const rssiAvg = row ? row[`${t.deviceId}_avg`] : null;
          const rssiMin = row ? row[`${t.deviceId}_min`] : null;
          const rssiMax = row ? row[`${t.deviceId}_max`] : null;
          const lossPct = row ? row[`${t.deviceId}_loss`] : null;
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
                className="text-right font-bold font-mono tabular-nums py-1 pl-1 whitespace-nowrap text-background-30"
                style={{
                  color: gapEvent
                    ? getGapTextColor(gapEvent.durationMs)
                    : undefined,
                }}
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

export const TelemetryTooltipTable = memo(TelemetryTooltipTableComponent);

export function TelemetryPopoverTooltip({
  clientPos,
  trackers,
  chartData,
  hoveredTime,
  eventsByTracker,
}: {
  clientPos: { x: number; y: number } | null;
  trackers: TelemetryTracker[];
  chartData: ChartRow[];
  hoveredTime: number | null;
  eventsByTracker: Record<number, GapEvent[]>;
}) {
  return (
    <ChartPopover
      clientPos={hoveredTime != null ? clientPos : null}
      width={TOOLTIP_WIDTH}
      estimatedHeight={estimatedTooltipHeight(trackers.length)}
    >
      {hoveredTime != null && (
        <TelemetryTooltipTable
          trackers={trackers}
          chartData={chartData}
          hoveredTime={hoveredTime}
          eventsByTracker={eventsByTracker}
        />
      )}
    </ChartPopover>
  );
}
