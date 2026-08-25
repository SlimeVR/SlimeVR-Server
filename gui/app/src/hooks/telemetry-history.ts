import { useCallback, useEffect, useRef, useState } from 'react';
import {
  RpcMessage,
  StartTelemetryRequestT,
  StopTelemetryRequestT,
  TelemetryGapEventT,
  TelemetryGapResponseT,
  TelemetrySampleT,
  TelemetryUpdateResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

export interface ChartRow {
  t: number;
  [key: string]: number | null;
}

export interface GapEvent {
  t: number;
  durationMs: number;
  packetsLost: number;
}

const MAX_HISTORY_SEC = 5 * 60;

export function useTelemetryHistory(deviceIds: number[]) {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const historyRowsRef = useRef<ChartRow[]>([]);
  const gapsRef = useRef<Map<number, GapEvent[]>>(new Map());
  const [lastUpdateTick, setLastUpdateTick] = useState(0);

  const deviceIdsKey = deviceIds
    .slice()
    .sort((a, b) => a - b)
    .join(',');

  useEffect(() => {
    if (deviceIds.length === 0) {
      historyRowsRef.current = [];
      gapsRef.current.clear();
      return;
    }
    const req = new StartTelemetryRequestT();
    req.deviceIds = deviceIds;
    sendRPCPacket(RpcMessage.StartTelemetryRequest, req);
    return () => {
      sendRPCPacket(RpcMessage.StopTelemetryRequest, new StopTelemetryRequestT());
    };
  }, [deviceIdsKey]);

  useRPCPacket(
    RpcMessage.TelemetryUpdateResponse,
    ({ samples }: TelemetryUpdateResponseT) => {
      if (!samples || samples.length === 0) return;
      const timeSec = Number(samples[0].time) / 1000;
      const row: ChartRow = { t: timeSec };

      samples.forEach((s: TelemetrySampleT) => {
        const devId = s.deviceId;
        row[`${devId}_avg`] = s.rssi ?? null;
        row[`${devId}_min`] = (s as any).rssiMin ?? s.rssi ?? null;
        row[`${devId}_max`] = (s as any).rssiMax ?? s.rssi ?? null;
        row[`${devId}_loss`] =
          (s as any).packetLossPct != null ? (s as any).packetLossPct * 100 : null;
      });

      const rows = historyRowsRef.current;
      rows.push(row);

      const cutoff = timeSec - MAX_HISTORY_SEC;
      let cut = 0;
      while (cut < rows.length && rows[cut].t < cutoff) cut++;
      if (cut > 0) rows.splice(0, cut);

      setLastUpdateTick((v) => v + 1);
    }
  );

  useRPCPacket(RpcMessage.TelemetryGapResponse, ({ events }: TelemetryGapResponseT) => {
    const gaps = gapsRef.current;
    (events ?? []).forEach((ev: TelemetryGapEventT) => {
      let list = gaps.get(ev.deviceId);
      if (!list) {
        list = [];
        gaps.set(ev.deviceId, list);
      }
      const evMs = Number(ev.time);
      list.push({
        t: evMs,
        durationMs: ev.durationMs,
        packetsLost: ev.packetsLost,
      });

      const cutoffMs = evMs - MAX_HISTORY_SEC * 1000;
      let cut = 0;
      while (cut < list.length && list[cut].t < cutoffMs) cut++;
      if (cut > 0) list.splice(0, cut);
    });

    setLastUpdateTick((v) => v + 1);
  });

  const getChartRows = useCallback((): ChartRow[] => historyRowsRef.current, []);

  const getGapEvents = useCallback(
    (deviceId: number, windowSec: number, nowSec: number): GapEvent[] => {
      const list = gapsRef.current.get(deviceId) ?? [];
      const cutoffMs = (nowSec - windowSec - 5) * 1000;
      return list.filter((ev) => ev.t >= cutoffMs);
    },
    []
  );

  return { getChartRows, getGapEvents, lastUpdateTick };
}
