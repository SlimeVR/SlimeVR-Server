import { LogLevel } from 'solarxr-protocol';
import { SERIES_COLORS } from '@/components/commons/charts/series-colors';
import { AnsiSpan } from '@/utils/ansi';

export interface LogViewLine {
  id: number;
  timeMs: number;
  timeTitle: string | null;
  level: LogLevel | null;
  label: string | null;
  color: string | null;
  message: string;
  spans: AnsiSpan[] | null;
  details: string | null;
}

export const LEVEL_CLASSES: Record<LogLevel, string> = {
  [LogLevel.TRACE]: 'border-transparent',
  [LogLevel.DEBUG]: 'border-transparent',
  [LogLevel.INFO]: 'border-background-50',
  [LogLevel.WARN]: 'border-status-warning bg-status-warning/5',
  [LogLevel.ERROR]: 'border-status-critical bg-status-critical/10',
  [LogLevel.FATAL]: 'border-status-critical bg-status-critical/10',
};

export const LEVEL_FILTERS = [
  { level: LogLevel.DEBUG, id: 'settings-logs-level-debug' },
  { level: LogLevel.INFO, id: 'settings-logs-level-info' },
  { level: LogLevel.WARN, id: 'settings-logs-level-warn' },
  { level: LogLevel.ERROR, id: 'settings-logs-level-error' },
];

export function passesLevel(level: LogLevel | null, minLevel: LogLevel) {
  return level === null || level >= minLevel;
}

export function passesQuery(text: string, query: string) {
  return !query || text.toLowerCase().includes(query.toLowerCase());
}

export function nameColor(name: string) {
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = (hash * 31 + name.charCodeAt(i)) | 0;
  }
  return SERIES_COLORS[Math.abs(hash) % SERIES_COLORS.length];
}
