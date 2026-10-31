import { LogLevel } from 'solarxr-protocol';
import { LogViewLine } from '@/components/commons/log-view/log-line';

export interface LogLineParser {
  parse: (text: string) => Partial<LogViewLine> | null;
  lineStart: RegExp;
}

const ESP_LEVELS: Record<string, LogLevel> = {
  TRACE: LogLevel.TRACE,
  DEBUG: LogLevel.DEBUG,
  INFO: LogLevel.INFO,
  NOTICE: LogLevel.INFO,
  WARN: LogLevel.WARN,
  ERROR: LogLevel.ERROR,
  FATAL: LogLevel.FATAL,
};

// Leading dots are progress output the firmware printed before the line, without a newline
const ESP_PREFIX =
  /^\.*\[(TRACE|DEBUG|INFO|NOTICE|WARN|ERROR|FATAL)\s*\]\s*(?:\[([^\]]+)\]\s*)?/;

export const espLogParser: LogLineParser = {
  parse: (text) => {
    const match = ESP_PREFIX.exec(text);
    if (!match) return null;
    return {
      level: ESP_LEVELS[match[1]],
      label: match[2] ?? null,
      message: text.slice(match[0].length),
    };
  },
  lineStart: /\[(?:TRACE|DEBUG|INFO|NOTICE|WARN|ERROR|FATAL)\s*\] /,
};

const ZEPHYR_LEVELS: Record<string, LogLevel> = {
  dbg: LogLevel.DEBUG,
  inf: LogLevel.INFO,
  wrn: LogLevel.WARN,
  err: LogLevel.ERROR,
};

const ZEPHYR_PREFIX = /^(?:\[([\d:.,]+)\]\s*)?<(dbg|inf|wrn|err)>\s*(?:([\w.-]+):\s)?/;

export const zephyrLogParser: LogLineParser = {
  parse: (text) => {
    const match = ZEPHYR_PREFIX.exec(text);
    if (!match) return null;
    return {
      level: ZEPHYR_LEVELS[match[2]],
      label: match[3] ?? null,
      timeTitle: match[1] ?? null,
      message: text.slice(match[0].length),
    };
  },
  lineStart: /\[\d+:\d{2}:\d{2}\.\d{3},\d{3}\] <(?:dbg|inf|wrn|err)>/,
};
