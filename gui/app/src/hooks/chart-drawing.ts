import { ChartSource } from './time-series';

export interface Point {
  x: number;
  y: number;
}

export function drawLine(ctx: CanvasRenderingContext2D, points: Point[]) {
  if (points.length < 2) return;
  ctx.beginPath();
  ctx.moveTo(points[0].x, points[0].y);
  for (let i = 1; i < points.length; i++) {
    ctx.lineTo(points[i].x, points[i].y);
  }
  ctx.stroke();
}

/** Dash phase is anchored on `t0Px` so the pattern scrolls with the data */
export function drawDashedPath(
  ctx: CanvasRenderingContext2D,
  points: Point[],
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

export function drawSeries(
  source: ChartSource,
  key: string,
  timeToPx: (t: number) => number,
  valToPy: (v: number) => number,
  drawPath: (points: Point[]) => void
) {
  let pts: Point[] = [];
  for (let i = 0; i < source.length; i++) {
    const val = source.value(key, i);
    if (val != null) {
      pts.push({ x: timeToPx(source.time(i)), y: valToPy(val) });
    } else if (pts.length > 0) {
      drawPath(pts);
      pts = [];
    }
  }
  if (pts.length > 0) {
    drawPath(pts);
  }
}

export function drawBand(
  ctx: CanvasRenderingContext2D,
  source: ChartSource,
  minKey: string,
  maxKey: string,
  timeToPx: (t: number) => number,
  valToPy: (v: number) => number
) {
  let inPoly = false;
  const polyMinPoints: Point[] = [];

  const closePoly = () => {
    for (let j = polyMinPoints.length - 1; j >= 0; j--) {
      ctx.lineTo(polyMinPoints[j].x, polyMinPoints[j].y);
    }
    ctx.closePath();
    ctx.fill();
    polyMinPoints.length = 0;
    inPoly = false;
  };

  for (let i = 0; i < source.length; i++) {
    const minVal = source.value(minKey, i);
    const maxVal = source.value(maxKey, i);
    if (minVal != null && maxVal != null) {
      const x = timeToPx(source.time(i));
      const yMax = valToPy(maxVal);
      const yMin = valToPy(minVal);

      if (!inPoly) {
        ctx.beginPath();
        ctx.moveTo(x, yMax);
        inPoly = true;
      } else {
        ctx.lineTo(x, yMax);
      }
      polyMinPoints.push({ x, y: yMin });
    } else if (inPoly) {
      closePoly();
    }
  }
  if (inPoly) closePoly();
}
