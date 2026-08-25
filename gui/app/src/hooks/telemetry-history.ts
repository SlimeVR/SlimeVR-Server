import { useCallback, useEffect, useRef } from 'react';
import { useAtomValue } from 'jotai';
import { datafeedAtom } from '@/store/app-store';

export interface TelemetrySample {
  t: number;
  rssi: number | null;
  rssiMin: number | null;
  rssiMax: number | null;
  packetLoss: number | null;
  packetsLost: number | null;
}

export interface RssiBucket {
  t: number;
  min: number | null;
  max: number | null;
  avg: number | null;
}

export interface LossPctBucket {
  t: number;
  lossPct: number | null;
}

export interface GapEvent {
  t: number;
  durationMs: number;
  packetsLost: number;
}

const MAX_HISTORY_MS = 5 * 60 * 1000;

function trimOld(samples: TelemetrySample[], now: number) {
  let cut = 0;
  while (cut < samples.length && now - samples[cut].t > MAX_HISTORY_MS) cut++;
  if (cut > 0) samples.splice(0, cut);
}

function windowSlice(samples: TelemetrySample[], windowSec: number) {
  // Keep samples up to 10 seconds past the left edge so line segments
  // spanning across the left boundary (-windowSec) are never erased early.
  const cutoff = Date.now() - (windowSec + 10) * 1000;
  const idx = samples.findIndex((s) => s.t >= cutoff);
  if (idx === -1) return [];
  const startIdx = Math.max(0, idx - 1);
  return samples.slice(startIdx);
}

function bucketByTime(
  samples: TelemetrySample[],
  windowSec: number,
  stepMs: number
): [number, TelemetrySample[]][] {
  const slice = windowSlice(samples, windowSec + 10);
  if (slice.length === 0) return [];

  const buckets = new Map<number, TelemetrySample[]>();
  for (const s of slice) {
    const bucketTime = Math.floor(s.t / stepMs) * stepMs;
    let list = buckets.get(bucketTime);
    if (!list) {
      list = [];
      buckets.set(bucketTime, list);
    }
    list.push(s);
  }

  return Array.from(buckets.entries()).sort((a, b) => a[0] - b[0]);
}

export function computeRssiStats(
  samples: TelemetrySample[],
  windowSec: number,
  stepMs = 100
): RssiBucket[] {
  return bucketByTime(samples, windowSec, stepMs).map(([tMs, chunk]) => {
    const mins = chunk
      .map((s) => s.rssiMin ?? s.rssi)
      .filter((v): v is number => v != null);
    const maxs = chunk
      .map((s) => s.rssiMax ?? s.rssi)
      .filter((v): v is number => v != null);
    const avgs = chunk.map((s) => s.rssi).filter((v): v is number => v != null);

    return {
      t: tMs / 1000,
      avg: avgs.length > 0 ? avgs.reduce((a, b) => a + b, 0) / avgs.length : null,
      min: mins.length > 0 ? Math.min(...mins) : null,
      max: maxs.length > 0 ? Math.max(...maxs) : null,
    };
  });
}

export function computeLossPctSeries(
  samples: TelemetrySample[],
  windowSec: number,
  stepMs = 100
): LossPctBucket[] {
  return bucketByTime(samples, windowSec, stepMs).map(([tMs, chunk]) => {
    const values = chunk.map((s) => s.packetLoss).filter((v): v is number => v != null);
    const avg =
      values.length > 0 ? values.reduce((a, b) => a + b, 0) / values.length : null;
    return { t: tMs / 1000, lossPct: avg != null ? avg * 100 : null };
  });
}

export function computeLossPct(
  samples: TelemetrySample[],
  windowSec: number
): number | null {
  const values = windowSlice(samples, windowSec)
    .map((s) => s.packetLoss)
    .filter((v): v is number => v != null);
  if (values.length === 0) return null;
  return (values.reduce((a, b) => a + b, 0) / values.length) * 100;
}

export function computeGapEvents(
  samples: TelemetrySample[],
  windowSec: number
): GapEvent[] {
  const slice = windowSlice(samples, windowSec);
  const events: GapEvent[] = [];
  for (let i = 1; i < slice.length; i++) {
    const prev = slice[i - 1];
    const cur = slice[i];
    const deltaMs = cur.t - prev.t;

    if (
      deltaMs > 20 &&
      cur.packetsLost != null &&
      prev.packetsLost != null &&
      cur.packetsLost > prev.packetsLost
    ) {
      events.push({
        t: cur.t,
        durationMs: deltaMs,
        packetsLost: cur.packetsLost - prev.packetsLost,
      });
    }
  }
  return events;
}

export function useTelemetryHistory() {
  const datafeed = useAtomValue(datafeedAtom);
  const buffersRef = useRef<Map<number, TelemetrySample[]>>(new Map());

  useEffect(() => {
    if (!datafeed?.devices) return;
    const now = Date.now();
    const buffers = buffersRef.current;
    for (const device of datafeed.devices) {
      let buffer = buffers.get(device.id);
      if (!buffer) {
        buffer = [];
        buffers.set(device.id, buffer);
      }
      buffer.push({
        t: now,
        rssi: device.hardwareStatus?.rssi ?? null,
        rssiMin: device.hardwareStatus?.rssiMin ?? null,
        rssiMax: device.hardwareStatus?.rssiMax ?? null,
        packetLoss: device.hardwareStatus?.packetLoss ?? null,
        packetsLost: device.hardwareStatus?.packetsLost ?? null,
      });
      trimOld(buffer, now);
    }
  }, [datafeed]);

  const getSamples = useCallback(
    (deviceId: number): TelemetrySample[] => buffersRef.current.get(deviceId) ?? [],
    []
  );

  return { getSamples };
}
