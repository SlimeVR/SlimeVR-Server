import { Dispatch, SetStateAction, useRef, useState } from 'react';
import {
  CartesianGrid,
  ComposedChart,
  Line,
  ReferenceLine,
  ResponsiveContainer,
  XAxis,
  YAxis,
} from 'recharts';
import { GapEvent } from '@/hooks/telemetry-history';
import { ChartRow } from '@/hooks/dongle-telemetry-feed';
import {
  CHART_MARGIN,
  flipTooltipLeft,
  HoveredChart,
  pxToTimeSec,
  RIGHT_AXIS_WIDTH,
  relativeTimeTick,
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

export function RssiLossChart({
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
  const visible = trackers.filter((t) => visibleIds.includes(t.deviceId));
  const wrapperRef = useRef<HTMLDivElement>(null);
  const [hoveredY, setHoveredY] = useState<number | null>(null);

  let tooltipLeftPx: number | null = null;
  let tooltipTopPx = 8;
  if (hoveredTime != null && wrapperRef.current) {
    const rect = wrapperRef.current.getBoundingClientRect();
    const hoveredPx = timeSecToPx(hoveredTime, rect.width, startSec, endSec);
    tooltipLeftPx = flipTooltipLeft(
      hoveredPx,
      rect.width,
      TOOLTIP_WIDTH,
      TOOLTIP_OFFSET
    );

    if (hoveredY != null) {
      tooltipTopPx = Math.max(
        0,
        Math.min(
          hoveredY + TOOLTIP_OFFSET,
          rect.height - estimatedTooltipHeight(trackers.length)
        )
      );
    }
  }
  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!wrapperRef.current) return;
    const rect = wrapperRef.current.getBoundingClientRect();
    onHoveredTimeChange(pxToTimeSec(e.clientX, rect, startSec, endSec));
    setHoveredY(e.clientY - rect.top);
    onActiveChange('rssi');
  };

  return (
    <div
      ref={wrapperRef}
      className="relative w-full h-full"
      onMouseMove={handleMouseMove}
      onMouseLeave={() => {
        onHoveredTimeChange(null);
        setHoveredY(null);
        onActiveChange((prev) => (prev === 'rssi' ? null : prev));
      }}
    >
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart data={chartData} margin={CHART_MARGIN}>
          <CartesianGrid stroke="rgba(255,255,255,0.08)" vertical={false} />
          <XAxis
            dataKey="t"
            type="number"
            domain={[startSec, endSec]}
            allowDataOverflow
            ticks={xAxisTicks}
            tickFormatter={(v: number) => relativeTimeTick(v, endSec)}
            stroke="rgb(var(--background-10))"
            fontSize={11}
            fontFamily="Poppins, sans-serif"
            tickLine={false}
            axisLine={false}
          />
          <YAxis
            yAxisId="rssi"
            orientation="left"
            domain={[-80, -20]}
            ticks={[-80, -60, -40, -20]}
            width={Y_AXIS_WIDTH}
            stroke="rgb(var(--background-10))"
            fontSize={11}
            fontFamily="Poppins, sans-serif"
            tickLine={false}
            axisLine={false}
            tickFormatter={(v: number) => `${v}dBm`}
          />
          <YAxis
            yAxisId="loss"
            orientation="right"
            domain={[0, 100]}
            ticks={[0, 25, 50, 75, 100]}
            width={RIGHT_AXIS_WIDTH}
            stroke="rgb(var(--background-10))"
            fontSize={11}
            fontFamily="Poppins, sans-serif"
            tickLine={false}
            axisLine={false}
            tickFormatter={(v: number) => `${v}%`}
          />
          {hoveredTime != null && (
            <ReferenceLine
              yAxisId="rssi"
              x={hoveredTime}
              stroke="rgba(255,255,255,0.4)"
              strokeWidth={1.5}
              isFront
            />
          )}
          {visible.map((t) => (
            <Line
              key={`${t.deviceId}-min-line`}
              yAxisId="rssi"
              dataKey={`${t.deviceId}_min`}
              stroke={t.color}
              strokeWidth={1}
              strokeDasharray="2 2"
              opacity={0.45}
              dot={false}
              activeDot={false}
              connectNulls
              isAnimationActive={false}
            />
          ))}
          {visible.map((t) => (
            <Line
              key={`${t.deviceId}-max-line`}
              yAxisId="rssi"
              dataKey={`${t.deviceId}_max`}
              stroke={t.color}
              strokeWidth={1}
              strokeDasharray="2 2"
              opacity={0.45}
              dot={false}
              activeDot={false}
              connectNulls
              isAnimationActive={false}
            />
          ))}
          {visible.map((t) => (
            <Line
              key={`${t.deviceId}-avg`}
              yAxisId="rssi"
              dataKey={`${t.deviceId}_avg`}
              stroke={t.color}
              strokeWidth={2}
              dot={false}
              activeDot={{ r: 4, strokeWidth: 0 }}
              connectNulls
              isAnimationActive={false}
            />
          ))}
          {visible.map((t) => (
            <Line
              key={`${t.deviceId}-loss`}
              yAxisId="loss"
              dataKey={`${t.deviceId}_loss`}
              stroke={t.color}
              strokeWidth={1.5}
              strokeDasharray="3 3"
              dot={false}
              activeDot={{ r: 4, strokeWidth: 0 }}
              connectNulls
              isAnimationActive={false}
            />
          ))}
        </ComposedChart>
      </ResponsiveContainer>

      {isActive && hoveredTime != null && tooltipLeftPx != null && (
        <div
          className="absolute top-0 left-0 pointer-events-none z-30"
          style={{
            transform: `translate(${tooltipLeftPx}px, ${tooltipTopPx}px)`,
          }}
        >
          <div
            className="bg-background-90/95 border border-background-10/10 rounded-lg px-3 py-2 max-h-[360px] overflow-y-auto"
            style={{ width: TOOLTIP_WIDTH }}
          >
            <TelemetryTooltipTable
              trackers={trackers}
              chartData={chartData}
              hoveredTime={hoveredTime}
              eventsByTracker={eventsByTracker}
            />
          </div>
        </div>
      )}
    </div>
  );
}
