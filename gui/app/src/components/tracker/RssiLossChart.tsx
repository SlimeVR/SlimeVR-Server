import {
  Dispatch,
  memo,
  SetStateAction,
  useEffect,
  useRef,
  useState,
} from 'react';
import { ChartRow, GapEvent } from '@/hooks/telemetry-history';
import {
  flipTooltipLeft,
  HoveredChart,
  relativeTimeTick,
  screenXToTimeSec,
  TelemetryTracker,
  timeSecToScreenX,
} from './DongleTelemetry';
import {
  TelemetryPopoverTooltip,
  TOOLTIP_OFFSET,
  TOOLTIP_WIDTH,
} from './TelemetryTooltip';

const Y_AXIS_WIDTH = 48;
const RIGHT_AXIS_WIDTH = 40;
const XAXIS_HEIGHT = 20;
const TOP_PADDING = 12;

function drawLine(
  ctx: CanvasRenderingContext2D,
  points: { x: number; y: number }[]
) {
  if (points.length < 2) return;
  ctx.beginPath();
  ctx.moveTo(points[0].x, points[0].y);
  for (let i = 1; i < points.length; i++) {
    ctx.lineTo(points[i].x, points[i].y);
  }
  ctx.stroke();
}

function drawCustomDashedPath(
  ctx: CanvasRenderingContext2D,
  points: { x: number; y: number }[],
  t0Px: number,
  dashLen = 5,
  gapLen = 4
) {
  if (points.length < 2) return;

  const period = dashLen + gapLen;
  const startDist = points[0].x - t0Px;
  let accumulated = ((startDist % period) + period) % period;
  let isDash = accumulated < dashLen;
  if (!isDash) {
    accumulated -= dashLen;
  }

  ctx.beginPath();
  ctx.moveTo(points[0].x, points[0].y);

  for (let i = 0; i < points.length - 1; i++) {
    const p1 = points[i];
    const p2 = points[i + 1];
    const dx = p2.x - p1.x;
    const dy = p2.y - p1.y;
    const dist = Math.hypot(dx, dy);

    if (dist === 0) continue;

    const ux = dx / dist;
    const uy = dy / dist;

    let progress = 0;
    while (progress < dist) {
      const targetLen = isDash ? dashLen : gapLen;
      const step = Math.min(dist - progress, targetLen - accumulated);

      progress += step;
      accumulated += step;

      const currX = p1.x + progress * ux;
      const currY = p1.y + progress * uy;

      if (isDash) {
        ctx.lineTo(currX, currY);
      } else {
        ctx.moveTo(currX, currY);
      }

      if (accumulated >= targetLen) {
        accumulated = 0;
        isDash = !isDash;
      }
    }
  }

  ctx.stroke();
}

function drawTrackerSeries(
  chartData: ChartRow[],
  key: string,
  timeToPx: (t: number) => number,
  valToPy: (v: number) => number,
  drawPath: (points: { x: number; y: number }[]) => void
) {
  let pts: { x: number; y: number }[] = [];
  for (let i = 0; i < chartData.length; i++) {
    const row = chartData[i];
    const val = row[key];
    if (val != null) {
      pts.push({ x: timeToPx(row.t), y: valToPy(Number(val)) });
    } else if (pts.length > 0) {
      drawPath(pts);
      pts = [];
    }
  }
  if (pts.length > 0) {
    drawPath(pts);
  }
}

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
  isActive,
  onActiveChange,
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
  isActive: boolean;
  onActiveChange: Dispatch<SetStateAction<HoveredChart>>;
  showMinMax?: boolean;
}) {
  const visible = trackers.filter((t) => visibleIds.includes(t.deviceId));
  const wrapperRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);

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

    const plotLeft = Y_AXIS_WIDTH;
    const plotRight = width - RIGHT_AXIS_WIDTH;
    const plotWidth = plotRight - plotLeft;
    const plotTop = TOP_PADDING;
    const plotBottom = height - XAXIS_HEIGHT;
    const plotHeight = plotBottom - plotTop;

    const timeToPx = (t: number) => {
      const ratio = (t - startSec) / (endSec - startSec);
      return plotLeft + ratio * plotWidth;
    };

    const rssiToPy = (rssi: number) => {
      const clamped = Math.max(-80, Math.min(-20, rssi));
      const ratio = (clamped - -80) / (-20 - -80);
      return plotBottom - ratio * plotHeight;
    };

    const lossToPy = (loss: number) => {
      const clamped = Math.max(0, Math.min(100, loss));
      const ratio = clamped / 100;
      return plotBottom - ratio * plotHeight;
    };

    // horizontal grid lines
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.08)';
    ctx.lineWidth = 1;
    const rssiTicks = [-80, -60, -40, -20];
    rssiTicks.forEach((val) => {
      const y = rssiToPy(val);
      ctx.beginPath();
      ctx.moveTo(plotLeft, y);
      ctx.lineTo(plotRight, y);
      ctx.stroke();
    });

    // Y-axis tick labels
    ctx.font = '11px Poppins, sans-serif';
    ctx.textBaseline = 'middle';

    // Left Y-axis (RSSI)
    ctx.fillStyle = '#ffffff';
    ctx.textAlign = 'right';
    rssiTicks.forEach((val) => {
      const y = rssiToPy(val);
      ctx.fillText(`${val}dBm`, plotLeft - 6, y);
    });

    // Right Y-axis (Packet Loss)
    ctx.textAlign = 'left';
    const lossTicks = [0, 25, 50, 75, 100];
    lossTicks.forEach((val) => {
      const y = lossToPy(val);
      ctx.fillText(`${val}%`, plotRight + 6, y);
    });

    // X-axis tick labels
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';
    ctx.fillStyle = '#ffffff';
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

    const t0Px = timeToPx(0);

    // RSSI Min/Max Range Band
    if (showMinMax) {
      visible.forEach((t) => {
        const minKey = `${t.deviceId}_min`;
        const maxKey = `${t.deviceId}_max`;

        ctx.fillStyle = t.color;
        ctx.globalAlpha = 0.15;

        let inPoly = false;
        const polyMinPoints: { x: number; y: number }[] = [];

        for (let i = 0; i < chartData.length; i++) {
          const row = chartData[i];
          const minVal = row[minKey];
          const maxVal = row[maxKey];
          if (minVal != null && maxVal != null) {
            const x = timeToPx(row.t);
            const yMax = rssiToPy(Number(maxVal));
            const yMin = rssiToPy(Number(minVal));

            if (!inPoly) {
              ctx.beginPath();
              ctx.moveTo(x, yMax);
              inPoly = true;
            } else {
              ctx.lineTo(x, yMax);
            }
            polyMinPoints.push({ x, y: yMin });
          } else if (inPoly) {
            for (let j = polyMinPoints.length - 1; j >= 0; j--) {
              ctx.lineTo(polyMinPoints[j].x, polyMinPoints[j].y);
            }
            ctx.closePath();
            ctx.fill();
            polyMinPoints.length = 0;
            inPoly = false;
          }
        }
        if (inPoly) {
          for (let j = polyMinPoints.length - 1; j >= 0; j--) {
            ctx.lineTo(polyMinPoints[j].x, polyMinPoints[j].y);
          }
          ctx.closePath();
          ctx.fill();
        }

        ctx.strokeStyle = t.color;
        ctx.lineWidth = 1;
        ctx.globalAlpha = 0.35;
        drawTrackerSeries(chartData, minKey, timeToPx, rssiToPy, (pts) =>
          drawLine(ctx, pts)
        );
        drawTrackerSeries(chartData, maxKey, timeToPx, rssiToPy, (pts) =>
          drawLine(ctx, pts)
        );
      });

      ctx.globalAlpha = 1.0;
    }

    // RSSI Avg lines
    visible.forEach((t) => {
      ctx.strokeStyle = t.color;
      ctx.lineWidth = 2.5;
      ctx.globalAlpha = 1.0;
      drawTrackerSeries(
        chartData,
        `${t.deviceId}_avg`,
        timeToPx,
        rssiToPy,
        (pts) => drawLine(ctx, pts)
      );
    });

    // Packet Loss lines
    visible.forEach((t) => {
      ctx.strokeStyle = t.color;
      ctx.lineWidth = 1.5;
      ctx.globalAlpha = 1.0;
      drawTrackerSeries(
        chartData,
        `${t.deviceId}_loss`,
        timeToPx,
        lossToPy,
        (pts) => drawCustomDashedPath(ctx, pts, t0Px, 5, 4)
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
    chartData,
    visible,
    startSec,
    endSec,
    xAxisTicks,
    hoveredTime,
    showMinMax,
  ]);

  const [hoveredY, setHoveredY] = useState<number | null>(null);

  const handleMouseMove = (e: React.MouseEvent<HTMLDivElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const canvasRect = canvas.getBoundingClientRect();
    const plotLeftScreen = canvasRect.left + Y_AXIS_WIDTH;
    const plotWidth = canvasRect.width - Y_AXIS_WIDTH - RIGHT_AXIS_WIDTH;
    onHoveredTimeChange(
      screenXToTimeSec(e.clientX, plotLeftScreen, plotWidth, startSec, endSec)
    );
    setHoveredY(e.clientY);
    onActiveChange('rssi');
  };

  let clientPos: { x: number; y: number } | null = null;
  if (hoveredTime != null && canvasRef.current) {
    const canvasRect = canvasRef.current.getBoundingClientRect();
    const plotLeftScreen = canvasRect.left + Y_AXIS_WIDTH;
    const plotWidth = canvasRect.width - Y_AXIS_WIDTH - RIGHT_AXIS_WIDTH;
    const screenX = timeSecToScreenX(
      hoveredTime,
      plotLeftScreen,
      plotWidth,
      startSec,
      endSec
    );
    const tooltipX = flipTooltipLeft(
      screenX,
      window.innerWidth,
      TOOLTIP_WIDTH,
      TOOLTIP_OFFSET
    );
    clientPos = {
      x: tooltipX,
      y: (hoveredY ?? canvasRect.top) + TOOLTIP_OFFSET,
    };
  }

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
      <canvas ref={canvasRef} className="w-full h-full block" />

      <TelemetryPopoverTooltip
        isOpen={isActive && hoveredTime != null && clientPos != null}
        clientPos={clientPos}
        trackers={trackers}
        chartData={chartData}
        hoveredTime={hoveredTime}
        eventsByTracker={eventsByTracker}
      />
    </div>
  );
}

export const RssiLossChart = memo(RssiLossChartComponent);
