import { memo, useEffect, useRef } from 'react';
import { ChartRow, GapEvent } from '@/hooks/telemetry-history';
import {
  getGapTextColor,
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

export function nearestRow(chartData: ChartRow[], t: number): ChartRow | null {
  if (chartData.length === 0) return null;
  let lo = 0;
  let hi = chartData.length;
  while (lo < hi) {
    const mid = (lo + hi) >> 1;
    if (chartData[mid].t < t) lo = mid + 1;
    else hi = mid;
  }
  if (lo === 0) return chartData[0];
  if (lo >= chartData.length) return chartData[chartData.length - 1];
  const d1 = Math.abs(chartData[lo - 1].t - t);
  const d2 = Math.abs(chartData[lo].t - t);
  return d1 < d2 ? chartData[lo - 1] : chartData[lo];
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
  isOpen,
  clientPos,
  trackers,
  chartData,
  hoveredTime,
  eventsByTracker,
}: {
  isOpen: boolean;
  clientPos: { x: number; y: number } | null;
  trackers: TelemetryTracker[];
  chartData: ChartRow[];
  hoveredTime: number | null;
  eventsByTracker: Record<number, GapEvent[]>;
}) {
  const popoverRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const el = popoverRef.current as
      | (HTMLDivElement & {
          showPopover?: () => void;
          hidePopover?: () => void;
        })
      | null;
    if (!el?.showPopover) return;

    try {
      const shouldBeOpen = Boolean(isOpen && clientPos && hoveredTime != null);
      const isOpenNow = el.matches(':popover-open');

      if (shouldBeOpen && !isOpenNow) {
        el.showPopover();
      } else if (!shouldBeOpen && isOpenNow) {
        el.hidePopover?.();
      }
    } catch {
      // Safe fallback if popover API fails or is unsupported
    }
  }, [isOpen, clientPos, hoveredTime]);

  const active = isOpen && clientPos != null && hoveredTime != null;
  const estimatedHeight = Math.min(
    360,
    estimatedTooltipHeight(trackers.length)
  );

  const clampedX = clientPos
    ? Math.max(
        10,
        Math.min(clientPos.x, window.innerWidth - TOOLTIP_WIDTH - 10)
      )
    : -9999;
  const clampedY = clientPos
    ? Math.max(
        10,
        Math.min(clientPos.y, window.innerHeight - estimatedHeight - 10)
      )
    : -9999;

  return (
    <div
      ref={popoverRef}
      {...({ popover: 'manual' } as any)}
      style={{
        position: 'fixed',
        left: `${clampedX}px`,
        top: `${clampedY}px`,
        margin: 0,
        inset: 'unset',
      }}
      className="border-0 bg-transparent p-0 outline-none backdrop:bg-transparent pointer-events-none"
    >
      {active && (
        <div className="bg-background-90/95 border border-background-10/10 rounded-lg px-3 py-2 shadow-2xl max-h-[360px] overflow-y-auto w-[380px] text-background-10">
          <TelemetryTooltipTable
            trackers={trackers}
            chartData={chartData}
            hoveredTime={hoveredTime}
            eventsByTracker={eventsByTracker}
          />
        </div>
      )}
    </div>
  );
}
