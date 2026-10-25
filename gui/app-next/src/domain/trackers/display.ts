import { t } from '@/lib/l10n';
import { bodyPartLabels } from './body-parts';
import type { Quat } from './movement';
import type { TrackerInfo } from './store';

export function trackerName(info: TrackerInfo | null | undefined): string {
  if (info?.customName) return info.customName;
  if (info?.bodyPart) return t(bodyPartLabels[info.bodyPart]);
  if (info?.displayName) return info.displayName;
  return t('tracker-unnamed');
}

const RAD_TO_DEG = 180 / Math.PI;

export function toEulerDegrees(q: Quat | null | undefined): {
  x: number;
  y: number;
  z: number;
} {
  if (!q) return { x: 0, y: 0, z: 0 };
  const { x, y, z, w } = q;
  const m11 = 1 - 2 * (y * y + z * z);
  const m13 = 2 * (x * z + y * w);
  const m21 = 2 * (x * y + z * w);
  const m22 = 1 - 2 * (x * x + z * z);
  const m23 = 2 * (y * z - x * w);
  const m31 = 2 * (x * z - y * w);
  const m33 = 1 - 2 * (x * x + y * y);
  const ez = Math.asin(Math.min(1, Math.max(-1, m21)));
  const gimbalLock = Math.abs(m21) >= 0.9999999;
  const ex = gimbalLock ? 0 : Math.atan2(-m23, m22);
  const ey = gimbalLock ? Math.atan2(m13, m33) : Math.atan2(-m31, m11);
  return { x: ex * RAD_TO_DEG, y: ey * RAD_TO_DEG, z: ez * RAD_TO_DEG };
}
