import { memo, useMemo } from 'react';
import { GapEvent } from '@/hooks/telemetry-history';
import { ChartRow, rowsSource } from '@/hooks/time-series';
import {
  ChartAxis,
  ChartBand,
  ChartSeries,
  TimeSeriesChart,
} from '@/components/commons/charts/TimeSeriesChart';
import { TelemetryTracker } from './DongleTelemetry';
import {
  estimatedTooltipHeight,
  TelemetryTooltipTable,
  TOOLTIP_WIDTH,
} from './TelemetryTooltip';

const RSSI_AXIS: ChartAxis = {
  min: -80,
  max: -20,
  ticks: [-80, -60, -40, -20],
  format: (v) => `${v}dBm`,
};

const LOSS_AXIS: ChartAxis = {
  min: 0,
  max: 100,
  ticks: [0, 25, 50, 75, 100],
  format: (v) => `${v}%`,
};

function RssiLossChartComponent({
  chartData,
  trackers,
  visibleIds,
  eventsByTracker,
  startSec,
  endSec,
  xAxisTicks,
  hoveredTime,
  onHoveredTimeChange,
  showMinMax = true,
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
  showMinMax?: boolean;
}) {
  const visible = useMemo(
    () => trackers.filter((t) => visibleIds.includes(t.deviceId)),
    [trackers, visibleIds]
  );
  const source = useMemo(() => rowsSource(chartData), [chartData]);

  const series = useMemo<ChartSeries[]>(
    () => [
      ...visible.map((t) => ({
        key: `${t.deviceId}_avg`,
        color: t.color,
        width: 2.5,
      })),
      ...visible.map((t) => ({
        key: `${t.deviceId}_loss`,
        color: t.color,
        axis: 'right' as const,
        dashed: true,
      })),
    ],
    [visible]
  );

  const bands = useMemo<ChartBand[]>(
    () =>
      showMinMax
        ? visible.map((t) => ({
            minKey: `${t.deviceId}_min`,
            maxKey: `${t.deviceId}_max`,
            color: t.color,
          }))
        : [],
    [visible, showMinMax]
  );

  return (
    <TimeSeriesChart
      source={source}
      startSec={startSec}
      endSec={endSec}
      xAxisTicks={xAxisTicks}
      leftAxis={RSSI_AXIS}
      rightAxis={LOSS_AXIS}
      series={series}
      bands={bands}
      hoveredTime={hoveredTime}
      onHoveredTimeChange={onHoveredTimeChange}
      tooltipWidth={TOOLTIP_WIDTH}
      tooltipHeight={estimatedTooltipHeight(visible.length)}
      renderTooltip={(t) => (
        <TelemetryTooltipTable
          trackers={visible}
          chartData={chartData}
          hoveredTime={t}
          eventsByTracker={eventsByTracker}
        />
      )}
    />
  );
}

export const RssiLossChart = memo(RssiLossChartComponent);
