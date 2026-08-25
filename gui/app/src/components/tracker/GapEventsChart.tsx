import classNames from 'classnames';
import { Dispatch, SetStateAction, useId, useRef } from 'react';
import {
  ComposedChart,
  Customized,
  ResponsiveContainer,
  XAxis,
  YAxis,
} from 'recharts';
import { Typography } from '@/components/commons/Typography';
import { GapEvent } from '@/hooks/telemetry-history';
import { ChartRow } from '@/hooks/dongle-telemetry-feed';
import {
  CHART_MARGIN,
  flipTooltipLeft,
  gapSeverityTier,
  HoveredChart,
  isGapEventActive,
  pxToTimeSec,
  relativeTimeTick,
  RIGHT_AXIS_WIDTH,
  TelemetryTracker,
  timeSecToPx,
  Y_AXIS_WIDTH,
} from './DongleTelemetry';
import {
  estimatedTooltipHeight,
  TelemetryTooltipTable,
  TOOLTIP_OFFSET,
  TOOLTIP_WIDTH,
} from './TelemetryTooltip';

const ROW_HEIGHT = 24;
const BAR_HEIGHT = 14;
const XAXIS_HEIGHT = 20;

const GAP_FILL_COLOR: Record<ReturnType<typeof gapSeverityTier>, string> = {
  critical: 'rgb(var(--critical))',
  warning: 'rgb(var(--warning))',
  mild: 'rgba(var(--warning),0.85)',
};

function RowLabels({
  trackers,
  visibleIds,
}: {
  trackers: TelemetryTracker[];
  visibleIds: number[];
}) {
  return (
    <div className="shrink-0" style={{ width: Y_AXIS_WIDTH }}>
      {trackers.map((t) => (
        <div
          key={t.deviceId}
          className={classNames(
            'flex items-center gap-2 pl-1 text-[9.5px] font-bold text-background-30 uppercase transition-opacity',
            { 'opacity-[0.28]': !visibleIds.includes(t.deviceId) }
          )}
          style={{ height: ROW_HEIGHT }}
          title={t.name}
        >
          <span
            className="w-1.5 h-1.5 rounded-full inline-block shrink-0"
            style={{ background: t.color }}
          />
          <span className="truncate">{t.name}</span>
        </div>
      ))}
    </div>
  );
}

interface GapBarsLayerProps {
  trackers: TelemetryTracker[];
  visibleIds: number[];
  eventsByTracker: Record<number, GapEvent[]>;
  clipId: string;
  hoveredTime: number | null;
  xAxisMap?: Record<string, { scale: (v: number) => number }>;
  offset?: { top: number; left: number; width: number; height: number };
}

function GapBarsLayer({
  trackers,
  visibleIds,
  eventsByTracker,
  clipId,
  hoveredTime,
  xAxisMap,
  offset,
}: GapBarsLayerProps) {
  if (!offset) return null;
  const scale = Object.values(xAxisMap ?? {})[0]?.scale;
  if (!scale) return null;

  return (
    <g>
      <defs>
        <clipPath id={clipId}>
          <rect
            x={offset.left}
            y={offset.top}
            width={offset.width}
            height={offset.height}
          />
        </clipPath>
      </defs>

      {trackers.map((_, i) => (
        <line
          key={`sep-${i}`}
          x1={offset.left}
          x2={offset.left + offset.width}
          y1={offset.top + (i + 1) * ROW_HEIGHT}
          y2={offset.top + (i + 1) * ROW_HEIGHT}
          stroke="rgb(var(--background-10))"
          strokeOpacity={0.04}
        />
      ))}

      <g clipPath={`url(#${clipId})`}>
        {trackers.map((t, rowIdx) => {
          if (!visibleIds.includes(t.deviceId)) return null;
          const yCenter = offset.top + rowIdx * ROW_HEIGHT + ROW_HEIGHT / 2;
          return (eventsByTracker[t.deviceId] ?? []).map((ev) => {
            const tStartSec = (ev.t - ev.durationMs) / 1000;
            const tEndSec = ev.t / 1000;
            const x1 = scale(tStartSec);
            const x2 = scale(tEndSec);
            const width = Math.max(3, x2 - x1);

            const color = GAP_FILL_COLOR[gapSeverityTier(ev.durationMs)];
            const isHovered =
              hoveredTime != null && isGapEventActive(ev, hoveredTime);

            return (
              <rect
                key={`${t.deviceId}-${ev.t}`}
                x={x1}
                y={yCenter - BAR_HEIGHT / 2}
                width={width}
                height={BAR_HEIGHT}
                rx={2}
                fill={color}
                fillOpacity={isHovered ? 1 : 0.85}
                stroke={isHovered ? '#fff' : 'none'}
                strokeWidth={isHovered ? 2 : 0}
              />
            );
          });
        })}
      </g>

      {hoveredTime != null && (
        <line
          x1={scale(hoveredTime)}
          x2={scale(hoveredTime)}
          y1={offset.top}
          y2={offset.top + offset.height}
          stroke="#fff"
          strokeOpacity={0.6}
        />
      )}
    </g>
  );
}

export function GapEventsChart({
  chartData,
  trackers,
  visibleIds,
  eventsByTracker,
  startSec,
  endSec,
  xAxisTicks,
  hoveredTime,
  onHoveredTimeChange,
  isActive,
  onActiveChange,
}: {
  chartData: ChartRow[];
  trackers: TelemetryTracker[];
  visibleIds: number[];
  eventsByTracker: Record<number, GapEvent[]>;
  startSec: number;
  endSec: number;
  xAxisTicks: number[];
  hoveredTime: number | null;
  onHoveredTimeChange: (t: number | null) => void;
  isActive: boolean;
  onActiveChange: Dispatch<SetStateAction<HoveredChart>>;
}) {
  const clipId = `gap-plot-clip-${useId()}`;
  const wrapperRef = useRef<HTMLDivElement>(null);

  let tooltipLeftPx: number | null = null;
  let tooltipTopPx: number | null = null;
  if (hoveredTime != null && wrapperRef.current) {
    const rect = wrapperRef.current.getBoundingClientRect();
    const hoveredPx = timeSecToPx(hoveredTime, rect.width, startSec, endSec);
    tooltipLeftPx = flipTooltipLeft(
      hoveredPx,
      rect.width,
      TOOLTIP_WIDTH,
      TOOLTIP_OFFSET
    );

    const estimatedHeight = estimatedTooltipHeight(trackers.length);
    const spaceBelow = window.innerHeight - rect.bottom;
    const spaceAbove = rect.top;
    const fitsBelow = spaceBelow >= estimatedHeight + TOOLTIP_OFFSET;
    const fitsAbove = spaceAbove >= estimatedHeight + TOOLTIP_OFFSET;
    const placeBelow = fitsBelow || (!fitsAbove && spaceBelow >= spaceAbove);
    tooltipTopPx = placeBelow
      ? rect.height + TOOLTIP_OFFSET
      : -(estimatedHeight + TOOLTIP_OFFSET);
  }

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!wrapperRef.current) return;
    const rect = wrapperRef.current.getBoundingClientRect();
    onHoveredTimeChange(pxToTimeSec(e.clientX, rect, startSec, endSec));
    onActiveChange('gap');
  };

  return (
    <div className="bg-background-80 rounded-xl p-4">
      <div className="pl-[64px] pb-2">
        <Typography
          variant="section-title"
          id={'dongle-settings-telemetry-chart_gaps'}
        />
      </div>
      <div className="relative">
        <div
          ref={wrapperRef}
          className="flex"
          style={{ height: trackers.length * ROW_HEIGHT + XAXIS_HEIGHT }}
          onMouseMove={handleMouseMove}
          onMouseLeave={() => {
            onHoveredTimeChange(null);
            onActiveChange((prev) => (prev === 'gap' ? null : prev));
          }}
        >
          <RowLabels trackers={trackers} visibleIds={visibleIds} />
          <div className="flex-1">
            <ResponsiveContainer width="100%" height="100%">
              <ComposedChart data={chartData} margin={CHART_MARGIN}>
                <XAxis
                  dataKey="t"
                  type="number"
                  domain={[startSec, endSec]}
                  allowDataOverflow
                  ticks={xAxisTicks}
                  tickFormatter={(v: number) => relativeTimeTick(v, endSec)}
                  height={XAXIS_HEIGHT}
                  stroke="rgb(var(--background-10))"
                  fontSize={11}
                  fontFamily="Poppins, sans-serif"
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis
                  yAxisId="spacer"
                  orientation="right"
                  width={RIGHT_AXIS_WIDTH}
                  domain={[0, 1]}
                  tick={false}
                  axisLine={false}
                  tickLine={false}
                />
                <Customized
                  component={GapBarsLayer}
                  trackers={trackers}
                  visibleIds={visibleIds}
                  eventsByTracker={eventsByTracker}
                  clipId={clipId}
                  hoveredTime={hoveredTime}
                />
              </ComposedChart>
            </ResponsiveContainer>
          </div>
        </div>

        {isActive &&
          hoveredTime != null &&
          tooltipLeftPx != null &&
          tooltipTopPx != null && (
            <div
              className="absolute top-0 left-0 rounded-lg border border-background-10/10 bg-background-90/95 px-3 py-2 shadow-md pointer-events-none z-30"
              style={{
                width: TOOLTIP_WIDTH,
                transform: `translate(${tooltipLeftPx}px, ${tooltipTopPx}px)`,
              }}
            >
              <TelemetryTooltipTable
                trackers={trackers}
                chartData={chartData}
                hoveredTime={hoveredTime}
                eventsByTracker={eventsByTracker}
              />
            </div>
          )}
      </div>
    </div>
  );
}
