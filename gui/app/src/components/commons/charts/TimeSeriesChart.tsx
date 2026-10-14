import { memo, ReactNode, useEffect, useRef, useState } from 'react';
import {
  drawBand,
  drawDashedPath,
  drawLine,
  drawSeries,
} from '@/hooks/chart-drawing';
import { ChartPopover, TOOLTIP_OFFSET } from './ChartPopover';
import {
  flipTooltipLeft,
  niceTicks,
  relativeTimeTick,
  screenXToTimeSec,
  timeSecToScreenX,
} from '@/hooks/time-axis';
import { ChartSource } from '@/hooks/time-series';

const DEFAULT_LEFT_AXIS_WIDTH = 48;
const RIGHT_AXIS_WIDTH = 40;
const NO_RIGHT_AXIS_WIDTH = 12;
const XAXIS_HEIGHT = 20;
const TOP_PADDING = 12;
const NO_BANDS: ChartBand[] = [];

export type ChartAxis =
  | {
      min: number;
      max: number;
      ticks: number[];
      format: (v: number) => string;
    }
  | {
      auto: true;
      symmetric?: boolean;
      minSpan: number;
      format: (v: number) => string;
    };

export type AxisSide = 'left' | 'right';

export interface ChartSeries {
  key: string;
  color: string;
  axis?: AxisSide;
  width?: number;
  dashed?: boolean;
}

export interface ChartBand {
  minKey: string;
  maxKey: string;
  color: string;
  axis?: AxisSide;
}

interface ResolvedAxis {
  min: number;
  max: number;
  ticks: number[];
}

function resolveAxis(
  axis: ChartAxis,
  side: AxisSide,
  source: ChartSource,
  series: ChartSeries[],
  startSec: number,
  endSec: number
): ResolvedAxis {
  if (!('auto' in axis)) return axis;

  let lo = Infinity;
  let hi = -Infinity;
  const keys = series
    .filter((s) => (s.axis ?? 'left') === side)
    .map((s) => s.key);
  for (let i = 0; i < source.length; i++) {
    const t = source.time(i);
    if (t < startSec || t > endSec) continue;
    for (const key of keys) {
      const v = source.value(key, i);
      if (v == null) continue;
      if (v < lo) lo = v;
      if (v > hi) hi = v;
    }
  }
  if (lo > hi) {
    lo = 0;
    hi = 0;
  }

  if (axis.symmetric) {
    const m = Math.max(Math.abs(lo), Math.abs(hi), axis.minSpan / 2);
    return niceTicks(-m, m);
  }

  const missing = axis.minSpan - (hi - lo);
  if (missing > 0) {
    lo -= missing / 2;
    hi += missing / 2;
  }
  return niceTicks(lo, hi);
}

function TimeSeriesChartComponent({
  source,
  startSec,
  endSec,
  xAxisTicks,
  leftAxis,
  rightAxis,
  series,
  bands = NO_BANDS,
  hoveredTime,
  onHoveredTimeChange,
  renderTooltip,
  tooltipWidth = 240,
  tooltipHeight = 120,
  leftAxisWidth = DEFAULT_LEFT_AXIS_WIDTH,
}: {
  source: ChartSource;
  startSec: number;
  endSec: number;
  xAxisTicks: number[];
  leftAxis: ChartAxis;
  rightAxis?: ChartAxis;
  series: ChartSeries[];
  bands?: ChartBand[];
  hoveredTime: number | null;
  onHoveredTimeChange: (t: number | null) => void;
  renderTooltip?: (hoveredTime: number) => ReactNode;
  tooltipWidth?: number;
  tooltipHeight?: number;
  leftAxisWidth?: number;
}) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const rightAxisWidth = rightAxis ? RIGHT_AXIS_WIDTH : NO_RIGHT_AXIS_WIDTH;

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    const dpr = window.devicePixelRatio || 1;
    const width = rect.width;
    const height = rect.height;

    if (canvas.width !== width * dpr || canvas.height !== height * dpr) {
      canvas.width = width * dpr;
      canvas.height = height * dpr;
    }

    ctx.save();
    ctx.scale(dpr, dpr);
    ctx.clearRect(0, 0, width, height);

    const plotLeft = leftAxisWidth;
    const plotRight = width - rightAxisWidth;
    const plotWidth = plotRight - plotLeft;
    const plotTop = TOP_PADDING;
    const plotBottom = height - XAXIS_HEIGHT;
    const plotHeight = plotBottom - plotTop;

    const timeToPx = (t: number) => {
      const ratio = (t - startSec) / (endSec - startSec);
      return plotLeft + ratio * plotWidth;
    };

    const toPy = (axis: ResolvedAxis) => (v: number) => {
      const clamped = Math.max(axis.min, Math.min(axis.max, v));
      const ratio = (clamped - axis.min) / (axis.max - axis.min || 1);
      return plotBottom - ratio * plotHeight;
    };

    const left = resolveAxis(
      leftAxis,
      'left',
      source,
      series,
      startSec,
      endSec
    );
    const right = rightAxis
      ? resolveAxis(rightAxis, 'right', source, series, startSec, endSec)
      : null;
    const leftToPy = toPy(left);
    const rightToPy = right ? toPy(right) : leftToPy;
    const sideToPy = (side: AxisSide = 'left') =>
      side === 'right' ? rightToPy : leftToPy;

    // horizontal grid lines
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
    ctx.lineWidth = 1;
    left.ticks.forEach((val) => {
      const y = leftToPy(val);
      ctx.beginPath();
      ctx.moveTo(plotLeft, y);
      ctx.lineTo(plotRight, y);
      ctx.stroke();
    });

    // Y-axis tick labels
    ctx.font = '11px Poppins, sans-serif';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = '#ffffff';

    ctx.textAlign = 'right';
    left.ticks.forEach((val) => {
      ctx.fillText(leftAxis.format(val), plotLeft - 6, leftToPy(val));
    });

    if (right && rightAxis) {
      ctx.textAlign = 'left';
      right.ticks.forEach((val) => {
        ctx.fillText(rightAxis.format(val), plotRight + 6, rightToPy(val));
      });
    }

    // X-axis tick labels
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';
    xAxisTicks.forEach((tSec) => {
      const x = timeToPx(tSec);
      if (x >= plotLeft && x <= plotRight) {
        ctx.fillText(relativeTimeTick(tSec, endSec), x, plotBottom + 4);
      }
    });

    ctx.save();
    ctx.beginPath();
    ctx.rect(plotLeft, plotTop, plotWidth, plotHeight);
    ctx.clip();

    bands.forEach((band) => {
      const valToPy = sideToPy(band.axis);
      ctx.fillStyle = band.color;
      ctx.globalAlpha = 0.15;
      drawBand(ctx, source, band.minKey, band.maxKey, timeToPx, valToPy);

      ctx.strokeStyle = band.color;
      ctx.lineWidth = 1;
      ctx.globalAlpha = 0.35;
      drawSeries(source, band.minKey, timeToPx, valToPy, (pts) =>
        drawLine(ctx, pts)
      );
      drawSeries(source, band.maxKey, timeToPx, valToPy, (pts) =>
        drawLine(ctx, pts)
      );
    });
    ctx.globalAlpha = 1.0;

    const t0Px = timeToPx(0);
    series.forEach((s) => {
      ctx.strokeStyle = s.color;
      ctx.lineWidth = s.width ?? 1.5;
      drawSeries(source, s.key, timeToPx, sideToPy(s.axis), (pts) =>
        s.dashed ? drawDashedPath(ctx, pts, t0Px, 5, 4) : drawLine(ctx, pts)
      );
    });

    ctx.restore();

    // vertical hover line
    if (hoveredTime != null) {
      const hoverX = timeToPx(hoveredTime);
      if (hoverX >= plotLeft && hoverX <= plotRight) {
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.4)';
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.moveTo(hoverX, plotTop);
        ctx.lineTo(hoverX, plotBottom);
        ctx.stroke();
      }
    }

    ctx.restore();
  }, [
    source,
    startSec,
    endSec,
    xAxisTicks,
    leftAxis,
    rightAxis,
    series,
    bands,
    hoveredTime,
    leftAxisWidth,
    rightAxisWidth,
  ]);

  const [hoveredY, setHoveredY] = useState<number | null>(null);

  const plotScreenSpan = (canvas: HTMLCanvasElement) => {
    const canvasRect = canvas.getBoundingClientRect();
    return {
      canvasRect,
      plotLeftScreen: canvasRect.left + leftAxisWidth,
      plotWidth: canvasRect.width - leftAxisWidth - rightAxisWidth,
    };
  };

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const { plotLeftScreen, plotWidth } = plotScreenSpan(canvas);
    onHoveredTimeChange(
      screenXToTimeSec(e.clientX, plotLeftScreen, plotWidth, startSec, endSec)
    );
    setHoveredY(e.clientY);
  };

  let clientPos: { x: number; y: number } | null = null;
  if (hoveredTime != null && hoveredY != null && canvasRef.current) {
    const { plotLeftScreen, plotWidth } = plotScreenSpan(canvasRef.current);
    const screenX = timeSecToScreenX(
      hoveredTime,
      plotLeftScreen,
      plotWidth,
      startSec,
      endSec
    );
    clientPos = {
      x: flipTooltipLeft(
        screenX,
        window.innerWidth,
        tooltipWidth,
        TOOLTIP_OFFSET
      ),
      y: hoveredY + TOOLTIP_OFFSET,
    };
  }

  return (
    <div
      className="relative w-full h-full"
      onMouseMove={handleMouseMove}
      onMouseLeave={() => {
        onHoveredTimeChange(null);
        setHoveredY(null);
      }}
    >
      <canvas ref={canvasRef} className="w-full h-full block" />

      {renderTooltip && (
        <ChartPopover
          clientPos={clientPos}
          width={tooltipWidth}
          estimatedHeight={tooltipHeight}
        >
          {hoveredTime != null && renderTooltip(hoveredTime)}
        </ChartPopover>
      )}
    </div>
  );
}

export const TimeSeriesChart = memo(TimeSeriesChartComponent);
