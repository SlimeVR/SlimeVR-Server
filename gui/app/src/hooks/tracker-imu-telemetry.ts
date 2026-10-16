import { useState } from 'react';
import {
  RpcMessage,
  StartTrackerImuTelemetryRequestT,
  StopTrackerImuTelemetryRequestT,
  TrackerImuSampleT,
  TrackerImuTelemetryResponseT,
} from 'solarxr-protocol';
import { QuaternionToEulerDegrees } from '@/maths/quaternion';
import {
  ChartSource,
  clearColumnRing,
  columnRingSource,
  createColumnRing,
  createSourceClock,
  observeSourceTime,
  pushColumnRing,
  SourceClock,
} from './time-series';
import { useWebsocketAPI } from './websocket-api';

export const IMU_KEYS = [
  'raw_acc_x',
  'raw_acc_y',
  'raw_acc_z',
  'lin_acc_x',
  'lin_acc_y',
  'lin_acc_z',
  'raw_rot_x',
  'raw_rot_y',
  'raw_rot_z',
  'rot_x',
  'rot_y',
  'rot_z',
] as const;

export const IMU_HISTORY_SEC = 60;
const IMU_MAX_RATE_HZ = 250;

function sampleValues(s: TrackerImuSampleT): (number | null)[] {
  const rawRot = s.rawRotation ? QuaternionToEulerDegrees(s.rawRotation) : null;
  const rot = s.rotation ? QuaternionToEulerDegrees(s.rotation) : null;
  return [
    s.rawAcceleration?.x ?? null,
    s.rawAcceleration?.y ?? null,
    s.rawAcceleration?.z ?? null,
    s.linearAcceleration?.x ?? null,
    s.linearAcceleration?.y ?? null,
    s.linearAcceleration?.z ?? null,
    rawRot?.x ?? null,
    rawRot?.y ?? null,
    rawRot?.z ?? null,
    rot?.x ?? null,
    rot?.y ?? null,
    rot?.z ?? null,
  ];
}

export interface TrackerImuHistory {
  source: ChartSource;
  clock: SourceClock;
}

export function useTrackerImuHistory(trackerId: number | undefined): TrackerImuHistory {
  const { sendRPCPacket, useRPCPacket, useConnectedEffect } = useWebsocketAPI();

  const [{ ring, history }] = useState(() => {
    const ring = createColumnRing(IMU_KEYS, IMU_HISTORY_SEC * IMU_MAX_RATE_HZ);
    const history: TrackerImuHistory = {
      source: columnRingSource(ring),
      clock: createSourceClock(),
    };
    return { ring, history };
  });

  useConnectedEffect(() => {
    clearColumnRing(ring);
    if (trackerId == null) return;

    const req = new StartTrackerImuTelemetryRequestT();
    req.trackerId = trackerId;
    sendRPCPacket(RpcMessage.StartTrackerImuTelemetryRequest, req);
    return () => {
      sendRPCPacket(
        RpcMessage.StopTrackerImuTelemetryRequest,
        new StopTrackerImuTelemetryRequestT()
      );
    };
  }, [trackerId]);

  useRPCPacket(
    RpcMessage.TrackerImuTelemetryResponse,
    ({ trackerId: id, samples }: TrackerImuTelemetryResponseT) => {
      if (id !== trackerId || samples.length === 0) return;
      samples.forEach((s) => {
        pushColumnRing(ring, Number(s.time) / 1000, sampleValues(s));
      });
      const newest = samples[samples.length - 1];
      observeSourceTime(history.clock, Number(newest.time) / 1000);
    }
  );

  return history;
}
